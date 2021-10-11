name := "ppro_blog"
 
version := "1.0" 
      
lazy val `blog2021_2` = (project in file(".")).enablePlugins(PlayScala, PlayJava, PlayEbean)

libraryDependencies ++= Seq(
  javaJdbc,
  javaWs,
  filters
)
      
resolvers += "Akka Snapshot Repository" at "https://repo.akka.io/snapshots/"
resolvers += Resolver.bintrayRepo("playframework", "maven")

scalaVersion := "2.12.10"


libraryDependencies ++= Seq( evolutions, jdbc , ehcache , ws , specs2 % Test , guice )

libraryDependencies += "com.adrianhurt" %% "play-bootstrap" % "1.6.1-P28-B3"

libraryDependencies += "org.mindrot" % "jbcrypt" % "0.4"

// enable H2 database
libraryDependencies += "com.h2database" % "h2" % "1.4.192"

TwirlKeys.templateImports += "play.mvc.Http.Request._"
TwirlKeys.templateImports += "play.i18n.Messages._"