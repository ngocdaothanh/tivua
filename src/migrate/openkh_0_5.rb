# This program migrates data and uploaded files from OpenKH 0.5 to Tivua.
# OpenKH uses PostgreSQL, Tivua uses MongoDB.
#
# Required gems:
# * dbi and dbd-pg: for accessing PostgreSQL
# * nokogiri:       for fixing malformed HTML in OpenKH DB
# * mongodb:        for acessing MongoDB
# * htmlentities:   for decoding "&#272;a s&#7889;" to "Đa số" etc.

require 'rubygems'
require 'yaml'
require 'dbi'
require 'nokogiri'
require 'mongo'
require 'htmlentities'

DB = {
  :adapter  => 'DBI:Pg',
  :host     => 'localhost',
  :port     => 5432,
  :username => 'postgres',
  :password => 'postgres',
  :openkh   => 'openkh',
  :tivua    => 'tivua'
}

def fix_malformed_html(html)
  html2 = Nokogiri::HTML(html, 'UTF-8').xpath("//body").children.to_s

  HTMLEntities.new.decode(html2)
end

#-------------------------------------------------------------------------------

$node_id_to_doc_id = {}
$url_conversion_table = {}

def convert_articles_and_comments
  # Articles
  article_coll = $tivua.collection("articles")

  $openkh.select_all("SELECT node_versions.*, nodes.* FROM node_versions INNER JOIN nodes ON nodes.type = 'Article' AND nodes.id = node_versions.node_id AND node_versions.version = nodes.active_version ORDER BY node_versions.id") do |r|
    node_id    = r[:node_id]
    title      = r[:title]
    obj        = YAML::load(r[:_body])
    teaser     = fix_malformed_html(obj[0])
    body       = fix_malformed_html(obj[1])
    hits       = r[:views]
    sticky     = r[:sticky] != 0
    created_at = Time.now
    updated_at = r[:created_at].to_time
    user_id    = r[:user_id].to_s

    doc_id = article_coll.insert(
      "title"      => title,
      "teaser"     => teaser,
      "body"       => body,
      "sticky"     => sticky,
      "hits"       => hits,
      "created_at" => created_at,
      "updated_at" => updated_at,
      "user_id"    => user_id
    )

    $node_id_to_doc_id[node_id] = doc_id.to_s
  end

  # Comments
  comment_coll = $tivua.collection("comments")

  count = 0

  $openkh.select_all("SELECT * FROM comments") do |r|
    node_id    = r[:node_id]
    body       = fix_malformed_html(r[:message])
    user_id    = r[:user_id].to_s
    created_at = r[:created_at].to_time
    updated_at = r[:updated_at].to_time

    article_id = $node_id_to_doc_id[node_id]

    if article_id.nil?
      count = count + 1
    end

    unless article_id.nil?
      comment_coll.insert(
        "article_id" => article_id,
        "user_id"    => user_id,
        "body"       => body,
        "created_at" => created_at,
        "updated_at" => updated_at
      )
    end
  end

  puts count
end

def convert_forums_and_comments
  article_coll = $tivua.collection("articles")
  comment_coll = $tivua.collection("comments")

  count = 0

  forums = []
  $openkh.select_all("SELECT node_versions.*, nodes.* FROM node_versions INNER JOIN nodes ON nodes.type = 'Forum' AND nodes.id = node_versions.node_id AND node_versions.version = nodes.active_version ORDER BY node_versions.id") do |r|
    forum = {
      :node_id    => r[:node_id],
      :title      => r[:title],
      :hits       => r[:views],
      :sticky     => r[:sticky] != 0
    }
    forums << forum
  end

  forums.each do |forum|
    comments = []
    $openkh.select_all("SELECT * FROM comments WHERE node_id = " + forum[:node_id].to_s) do |r|
      comment = {
        :node_id    => r[:node_id],
        :body       => fix_malformed_html(r[:message]),
        :user_id    => r[:user_id].to_s,
        :created_at => r[:created_at].to_time,
        :updated_at => r[:updated_at].to_time
      }
      comments << comment
    end

    count = count + comments.size

    doc_id = article_coll.insert(
      "title"      => forum[:title],
      "teaser"     => comments[0][:body],
      "body"       => "",
      "sticky"     => forum[:sticky],
      "hits"       => forum[:hits],
      "created_at" => comments[0][:created_at],
      "updated_at" => comments[0][:updated_at],
      "user_id"    => comments[0][:user_id]
    )

    article_id = doc_id.to_s
    $node_id_to_doc_id[forum[:node_id]] = article_id

    comments[1..-1].each do |comment|
      comment_coll.insert(
        "article_id" => article_id,
        "user_id"    => comment[:user_id],
        "body"       => comment[:body],
        "created_at" => comment[:created_at],
        "updated_at" => comment[:updated_at]
      )
    end
  end

  puts count
end

def convert_categories_and_tocs
  # Categories
  category_coll = $tivua.collection("categories")

  categories = {}
  $openkh.select_all("SELECT * FROM categories") do |r|
    categories[r[:id]] = {
      :name     => r[:name],
      :position => r[:position]
    }
  end

  # Uncategorized
  categories[0] = {
    :name     => "",
    :position => 99
  }

  # TOCs
  $openkh.select_all("SELECT * FROM node_versions INNER JOIN nodes ON nodes.type = 'Toc' AND nodes.id = node_versions.node_id AND node_versions.version = nodes.active_version ORDER BY node_versions.id") do |r|
    category_id = r[:sticky]
    toc         = r[:_body]

    categories[category_id][:toc] = toc
  end

  # article_ids
  category_id_to_doc_id = {}

  categories.each do |category_id, name_position_toc|
    article_ids = []
    $openkh.select_all("SELECT * FROM categories_nodes WHERE category_id = " + category_id.to_s) do |r|
      doc_id = $node_id_to_doc_id[r[:node_id]]
      article_ids << doc_id unless doc_id.nil?
    end

    doc_id = category_coll.insert(
      "name"        => name_position_toc[:name],
      "position"    => name_position_toc[:position],
      "toc"         => name_position_toc[:toc],
      "article_ids" => article_ids
    )
    category_id_to_doc_id[category_id] = doc_id.to_s
  end
end

#-------------------------------------------------------------------------------

def main
  $openkh = DBI.connect("#{DB[:adapter]}:#{DB[:openkh]}:#{DB[:host]}:#{DB[:port]}", DB[:username], DB[:password])
  $tivua  = Mongo::Connection.new.db(DB[:tivua])

  convert_articles_and_comments
  convert_forums_and_comments
  convert_categories_and_tocs
end
main
