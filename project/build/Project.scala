import sbt._

class Project(info: ProjectInfo) extends DefaultProject(info) {
  val localMavenRepo = "Local Maven Repo" at
    "file://" + Path.userHome + "/.m2/repository"

  val localIvyRepo = "Local Ivy Repo" at
    "file://" + Path.userHome + "/.ivy2/local"

  val scalateRepo = "Scalate Repo" at
    "http://repo.fusesource.com/nexus/content/repositories/snapshots"

  override def libraryDependencies =
    Set(
      "cntt" %% "xitrum" % "0.1-SNAPSHOT" % "compile"
    ) ++ super.libraryDependencies

  override def mainClass = Some("colinh.Http")
}
