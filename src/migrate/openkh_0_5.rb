# This program migrates data and uploaded files from OpenKH 0.5 to Tivua.
# OpenKH uses PostgreSQL, Tivua uses MongoDB.
#
# Required gems:
# * dbi and dbd-pg: for accessing PostgreSQL
# * nokogiri:       for fixing malformed HTML in OpenKH DB
# * mongodb:        for acessing MongoDB

require 'rubygems'
require 'yaml'
require 'dbi'
require 'nokogiri'
require 'mongo'
require 'bson/types/object_id'

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

  # Decode "&#272;a s&#7889;" to "Đa số" etc.
  # "htmlentities" gem cannot be used because it also decode "&lt;" to "<" etc.
  #HTMLEntities.new.decode(html2)
  html2.gsub(/(&#(\d+);)/) { [$2.to_i(10)].pack('U') }
end

#-------------------------------------------------------------------------------

$node_id_to_doc_id = {}
$url_conversion_table = {}

def convert_articles_and_comments
  # Articles
  article_coll = $tivua.collection("articles")

  articles = []
  $openkh.select_all("SELECT node_versions.*, nodes.* FROM node_versions INNER JOIN nodes ON nodes.type = 'Article' AND nodes.id = node_versions.node_id AND node_versions.version = nodes.active_version ORDER BY node_versions.id") do |r|
    obj = YAML::load(r[:_body])
    articles << {
      :node_id           => r[:node_id],
      :title             => r[:title],
      :teaser            => fix_malformed_html(obj[0]),
      :body              => fix_malformed_html(obj[1]),
      :user_id           => r[:user_id].to_s,
      :sticky            => r[:sticky] != 0,
      :hits              => r[:views],
      :updated_at        => r[:created_at].to_time
    }
  end

  # created_at = created_at of the first version
  articles.each do |article|
    $openkh.select_all("SELECT * FROM node_versions WHERE node_id = " + article[:node_id].to_s + " ORDER BY version LIMIT 1") do |r|
      article[:created_at] = r[:created_at].to_time
    end
  end
  
  # thread_updated_at = max(created_at of the last version, updated_at of comments)
  articles.each do |article|
    last_version_created_at = nil
    $openkh.select_all("SELECT * FROM node_versions WHERE node_id = " + article[:node_id].to_s + " ORDER BY version DESC LIMIT 1") do |r|
      last_version_created_at = r[:created_at].to_time
    end

    comments_max_updated_at = nil
    $openkh.select_all("SELECT * FROM comments WHERE node_id = " + article[:node_id].to_s + " ORDER BY updated_at DESC LIMIT 1") do |r|
      comments_max_updated_at = r[:updated_at].to_time
    end

    article[:thread_updated_at] = [last_version_created_at, comments_max_updated_at].find_all { |t| !t.nil? }.max
  end

  articles.each do |article|
    doc_id = article_coll.insert(
      "title"             => article[:title],
      "teaser"            => article[:teaser],
      "body"              => article[:body],
      "user_id"           => article[:user_id],
      "sticky"            => article[:sticky],
      "hits"              => article[:hits],
      "created_at"        => article[:created_at],
      "updated_at"        => article[:updated_at],
      "thread_updated_at" => article[:updated_at]
    )

    $node_id_to_doc_id[article[:node_id]] = doc_id.to_s
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
    $openkh.select_all("SELECT * FROM comments WHERE node_id = " + forum[:node_id].to_s + " ORDER BY created_at") do |r|
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
      "title"             => forum[:title],
      "teaser"            => comments[0][:body],
      "body"              => "",
      "user_id"           => comments[0][:user_id],
      "sticky"            => forum[:sticky],
      "hits"              => forum[:hits],
      "created_at"        => comments[0][:created_at],
      "updated_at"        => comments[0][:updated_at],
      "thread_updated_at" => comments.map { |c| c[:updated_at] }.max
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

  # to_be_categorized
  to_be_categorized_doc_ids = Set.new($node_id_to_doc_id.values)
  categories[0] = {
    :name     => "",
    :position => 99
  }

  # TOCs
  $openkh.select_all("SELECT * FROM node_versions INNER JOIN nodes ON nodes.type = 'Toc' AND nodes.id = node_versions.node_id AND node_versions.version = nodes.active_version ORDER BY node_versions.id") do |r|
    category_id = r[:sticky]
    toc         = fix_malformed_html(r[:_body])
    categories[category_id][:toc] = toc
  end

  # article_ids
  categories.each do |category_id, name_position_toc|
    next if category_id == 0  # to_be_categorized

    doc_ids = []
    $openkh.select_all("SELECT * FROM categories_nodes WHERE category_id = " + category_id.to_s) do |r|
      node_id = r[:node_id]
      doc_id  = $node_id_to_doc_id[node_id]
      unless doc_id.nil?
        doc_ids << doc_id
        to_be_categorized_doc_ids.delete(doc_id)
      end
    end

    doc_id = category_coll.insert(
      "name"     => name_position_toc[:name],
      "position" => name_position_toc[:position],
      "toc"      => name_position_toc[:toc]
    )
    insert_to_articles_categories(doc_id.to_s, doc_ids)
  end

  # Insert category to_be_categorized
  name_position_toc = categories[0]
  doc_id = category_coll.insert(
    "name"     => name_position_toc[:name],
    "position" => name_position_toc[:position],
    "toc"      => name_position_toc[:toc]
  )
  insert_to_articles_categories(doc_id.to_s, to_be_categorized_doc_ids)
end

def insert_to_articles_categories(category_id, article_ids)
  articles_categories_coll = $tivua.collection("articles_categories")
  articles_coll            = $tivua.collection("articles")

  article_ids.each do |article_id|
    article = articles_coll.find_one({"_id" => BSON::ObjectId.from_string(article_id)})
    thread_updated_at = article["thread_updated_at"]

    articles_categories_coll.insert(
      "article_id"        => article_id,
      "category_id"       => category_id,
      "thread_updated_at" => thread_updated_at
    )
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
