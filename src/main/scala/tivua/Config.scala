package tivua

import xitrum.{Config => XConfig}

object Config {
  private val properties = XConfig.propertiesFromClasspath("tivua.properties")

  val siteName = properties.getProperty("site_name")

  val facebookAppId     = properties.getProperty("facebook.app_id")
  val facebookAppSecret = properties.getProperty("facebook.app_secret")

  val mongodbHost = properties.getProperty("mongodb.host")
  val mongodbPort = properties.getProperty("mongodb.port").toInt
  val mongodbName = properties.getProperty("mongodb.name")
}
