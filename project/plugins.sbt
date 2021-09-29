logLevel := Level.Warn

resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.8.8")

// Play enhancer - this automatically generates getters/setters for public fields
// and rewrites accessors of these fields to use the getters/setters. Remove this
// plugin if you prefer not to have this feature, or disable on a per project
// basis using disablePlugins(PlayEnhancer) in your build.sbt
addSbtPlugin("com.typesafe.sbt" % "sbt-play-enhancer" % "1.2.2")

// ebean ORM
//addSbtPlugin("com.typesafe.sbt" % "sbt-play-ebean" % "6.0.0")
//addSbtPlugin("com.typesafe.sbt" % "sbt-play-ebean" % "4.1.4")
addSbtPlugin("com.payintech" % "sbt-play-ebean" % "21.02")

//addSbtPlugin("org.mindrot" % "jbcrypt" % "0.3m")