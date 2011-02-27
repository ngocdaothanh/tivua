import sbt._

class Project(info: ProjectInfo) extends DefaultProject(info) {
  // Compile options

  override def compileOptions = super.compileOptions ++
    Seq("-deprecation",
        "-Xmigration",
        "-Xcheckinit",
        "-Xwarninit",
        "-encoding", "utf8")
        .map(x => CompileOption(x))

  override def javaCompileOptions = JavaCompileOption("-Xlint:unchecked") :: super.javaCompileOptions.toList

  // Repos ---------------------------------------------------------------------

  // For Xitrum
  val sonatypeSnapshot = "Sonatype Snapshot" at "https://oss.sonatype.org/content/repositories/snapshots"

  // For Netty 4.0.0.Alpha1-SNAPSHOT, which must be installed to local Maven repository manurally
  val localMaven = "Local Maven Repository" at "file://" + Path.userHome + "/.m2/repository"

  override def libraryDependencies =
    Set(
      "tv.cntt"        %% "xitrum"            % "1.0-SNAPSHOT",
      "ch.qos.logback" %  "logback-classic"   % "0.9.27",  // Xitrum needs SLF4J implementation
      "org.mongodb"    %  "mongo-java-driver" % "2.4"
    ) ++ super.libraryDependencies

 // Paths ---------------------------------------------------------------------

  override def unmanagedClasspath = super.unmanagedClasspath +++ ("config")

  override def mainClass = Some("tivua.Boot")
}
