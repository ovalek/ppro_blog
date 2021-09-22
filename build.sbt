name := "ppro_blog"
 
version := "1.0" 
      
lazy val `blog2021_2` = (project in file(".")).enablePlugins(PlayScala, PlayJava, PlayEbean)

libraryDependencies ++= Seq(
  javaJdbc,
  javaWs,
  filters
)
      
resolvers += "Akka Snapshot Repository" at "https://repo.akka.io/snapshots/"
      
scalaVersion := "2.13.5"

libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test , guice )

libraryDependencies += "com.adrianhurt" %% "play-bootstrap" % "1.6.1-P28-B3"
//libraryDependencies += "uk.gov.hmrc" %% "bootstrap-backend-play-xx" % "x.x.x"
      