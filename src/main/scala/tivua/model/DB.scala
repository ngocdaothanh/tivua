package tivua.model

import com.mongodb.casbah.Imports._

import tivua.Config

object DB {
  val db = {
    val conn = MongoConnection(Config.dbHost, Config.dbPort)
    conn(Config.dbName)
  }
}
