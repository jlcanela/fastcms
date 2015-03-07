import sbt._
import Keys._
import play.Play.autoImport._
import play.PlayScala
import PlayKeys._

object ApplicationBuild extends Build {
  val appName = "fastcms"
  val appVersion = "1.0.0"

  val appDependencies: Seq[sbt.ModuleID] = Seq(
    //"com.wordnik" %% "swagger-play2" % "1.3.12",
   // "com.wordnik" %% "swagger-play2-utils" % "1.3.12",
    "org.zeroturnaround" % "zt-zip" % "1.7",
    "com.typesafe.play" %% "play-ws" % "2.4.0-M2",
    "org.scalaz" % "scalaz-core_2.10" % "7.1.0",
    "com.github.jknack" % "handlebars" % "2.0.0",
    "com.github.jknack" % "handlebars-json" % "2.0.0",
    "com.sksamuel.elastic4s" %% "elastic4s" % "1.4.12",
//    "com.github.spullara.mustache.java" % "compiler" % "0.8.17",
    "com.h2database" % "h2" % "1.4.185",
    //"com.typesafe.play" %% "anorm" % "2.4.0-M2",
    "com.typesafe.play" %% "play-jdbc" % "2.4.0-M2",
    "com.typesafe.play" %% "anorm" % "2.4.0-M2"
    //"org.fusesource.scalate" % "scalate-core_2.10" % "1.7.0"
    //"org.apache.commons" % "commons-compress" % "1.5",
  )

  
  
  val main = Project(appName, file(".")).enablePlugins(play.PlayScala).settings(
    version := appVersion,
    libraryDependencies ++= appDependencies,
    resolvers := Seq(
      "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository",
      Resolver.url("Local Ivy Repository", url("file://"+Path.userHome.absolutePath+"/.ivy2/local"))(Resolver.ivyStylePatterns),
      "sonatype-snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
      "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases",
      "java-net" at "http://download.java.net/maven/2",
      "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases",
      "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"))
}
