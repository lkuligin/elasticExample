resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"

// Build Version Plugins
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.5.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.0.6")
addSbtPlugin("io.spray" % "sbt-revolver" % "0.8.0")
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.8.2")
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3.3")