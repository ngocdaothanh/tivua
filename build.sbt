organization := "tv.cntt"

name         := "tivua"

version      := "1.0-SNAPSHOT"

scalaVersion := "2.9.0-1"

// For Xitrum
resolvers += "Sonatype Snapshot Repository" at "https://oss.sonatype.org/content/repositories/snapshots"

// For Netty 4, remove this when Netty 4 is released
resolvers += "Local Maven Repository"       at "file://" + Path.userHome.absolutePath + "/.m2/repository"

libraryDependencies += "tv.cntt"            %%  "xitrum"          % "1.1-SNAPSHOT"

libraryDependencies += "ch.qos.logback"     %   "logback-classic" % "0.9.28"

libraryDependencies += "com.mongodb.casbah" %%  "casbah"          % "2.1.5-1"

mainClass := Some("tivua.Boot")

unmanagedBase in Runtime <<= baseDirectory { base => base / "config" }
