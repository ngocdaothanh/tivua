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
  Nokogiri::HTML(html, 'UTF-8').xpath("//body").children.to_s
end

#-------------------------------------------------------------------------------

def convert_articles
  article_coll = $tivua.collection("articles")
  coder = HTMLEntities.new

  $openkh.select_all("SELECT * FROM node_versions INNER JOIN nodes ON nodes.type = 'Article' AND nodes.id = node_versions.node_id AND node_versions.version = nodes.active_version ORDER BY node_versions.id") do |a|
    title      = a[:title]
    obj        = YAML::load(a[:_body])
    teaser     = coder.decode(fix_malformed_html(obj[0]))
    body       = coder.decode(fix_malformed_html(obj[1]))
    hits       = 100
    created_at = Time.now
    updated_at = Time.now
    user_id    = "1"

    # title, teaser, body, false, hits, created_at, updated_at, user_id
    article_coll.insert(
      "title"      => title,
      "teaser"     => teaser,
      "body"       => body,
      "sticky"     => false,
      "hits"       => hits,
      "created_at" => created_at,
      "updated_at" => updated_at,
      "user_id"    => user_id
    )
  end
end

#-------------------------------------------------------------------------------

# openkh -> colinh
$url_conversion_table = {}

def main
  $openkh = DBI.connect("#{DB[:adapter]}:#{DB[:openkh]}:#{DB[:host]}:#{DB[:port]}", DB[:username], DB[:password])
  $tivua  = Mongo::Connection.new.db(DB[:tivua])

  convert_articles

  $openkh.disconnect
end
main
