import sbt._
import Keys._

object MyBuild extends Build {
  val mySettings = Defaults.defaultSettings ++ Seq(
    organization := "tv.cntt",
    name         := "tivua",
    version      := "1.0-SNAPSHOT",
    scalaVersion := "2.9.0-1"
  )

  val myResolvers = Seq(
    // For Xitrum
    "Sonatype Snapshot Repository" at "https://oss.sonatype.org/content/repositories/snapshots",

    // For Netty 4, remove this when Netty 4 is released
    "Local Maven Repository"       at "file://" + Path.userHome.absolutePath + "/.m2/repository"
  )

  val myLibraryDependencies = Seq(
    "tv.cntt"            %% "xitrum"          % "1.1-SNAPSHOT",
    "ch.qos.logback"     %  "logback-classic" % "0.9.28",
    "com.mongodb.casbah" %% "casbah"          % "2.1.5-1"
  )

  lazy val project = Project (
    "project",
    file ("."),
    settings = mySettings ++ Seq(
      resolvers           := myResolvers,
      libraryDependencies := myLibraryDependencies,

      mainClass           := Some("tivua.Boot"),
      unmanagedBase in Runtime <<= baseDirectory { base => base / "config" }
    )
  )
}
