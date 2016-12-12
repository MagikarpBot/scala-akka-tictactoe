name := "tictactoe"

version := "1.0"

scalaVersion := "2.11.8"

offline := true
resolvers += "Local Maven Repository" at "file:///"+Path.userHome+ "/.ivy2/cache"
resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

unmanagedJars in Compile += Attributed.blank(file(System.getenv("JAVA_HOME") + "/jre/lib/ext/jfxrt.jar"))

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)


libraryDependencies ++= Seq(
  "org.scalafx" %% "scalafx" % "8.0.40-R8",
  "org.scalafx" %% "scalafxml-core-sfx8" % "0.2.2",
  "com.github.nscala-time" %% "nscala-time" % "2.12.0",
  "com.jsuereth" %% "scala-arm" % "1.4",
  "com.typesafe.akka" %% "akka-actor" % "2.4.12",
  "com.typesafe.akka" %% "akka-remote" % "2.4.12"
)

mainClass in assembly := Some("hep88.Boom")

EclipseKeys.executionEnvironment := Some(EclipseExecutionEnvironment.JavaSE18)

fork := true

