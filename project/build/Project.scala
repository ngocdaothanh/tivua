import sbt._

class Project(info: ProjectInfo) extends DefaultProject(info) {
  val localMavenRepo = "Local Maven" at
    "file://" + Path.userHome + "/.m2/repository"

  val localIvyRepo = "Local Ivy" at
    "file://" + Path.userHome + "/.ivy2/local"

  val scalateRepo = "Scalate" at
    "http://repo.fusesource.com/nexus/content/repositories/snapshots"

  override def libraryDependencies = Set(
    "cntt"           %% "xitrum"          % "0.1-SNAPSHOT",
    "ch.qos.logback" %  "logback-classic" % "0.9.25",
    "postgresql"     %  "postgresql"      % "8.4-701.jdbc4"
  ) ++ super.libraryDependencies

  override def mainClass = Some("colinh.Boot")
}
