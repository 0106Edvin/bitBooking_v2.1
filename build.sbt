name := """test1"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  filters,
  "mysql" % "mysql-connector-java" % "5.1.36",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "com.cloudinary" % "cloudinary" % "1.0.14",
  "org.apache.commons" % "commons-email" % "1.3.3",
  "org.easytesting" % "fest-assert" % "1.4" % Test,
  "org.xhtmlrenderer" % "flying-saucer-pdf" % "9.0.8",
  "org.xhtmlrenderer" % "core-renderer" % "R8pre2",
  "com.lowagie" % "itext" % "4.2.2"
)

lazy val myProject = (project in file("."))
  .enablePlugins(PlayJava, PlayEbean)
// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
