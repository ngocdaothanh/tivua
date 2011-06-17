package tivua

import xitrum.{Config => XConfig}

object Config {
  private val properties = XConfig.loadPropertiesFromClasspath("tivua.properties")

  val siteName = properties.getProperty("site_name")

  val dbHost = properties.getProperty("db.host")
  val dbPort = properties.getProperty("db.port").toInt
  val dbName = properties.getProperty("db.name")
}