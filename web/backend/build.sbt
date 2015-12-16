import play.sbt.PlayImport._
import play.sbt.PlayScala
import play.sbt.routes.RoutesKeys._

name := "simpletime"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  specs2 % Test,
  "com.github.tuxBurner" %% "play-neo4jplugin" % "1.5.0",
  "com.github.unterstein" %% "play-elasticplugin" % "1.1.0-SNAPSHOT",
  "com.google.code.gson" % "gson" % "2.4"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
resolvers += "tuxburner.github.io" at "http://tuxburner.github.io/repo"
resolvers += "unterstein.github.io" at "http://unterstein.github.io/repo"
resolvers += "Neo4j Maven Repo" at "http://m2.neo4j.org/content/repositories/releases"
resolvers += "Spring milestones" at "http://repo.spring.io/milestone"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
