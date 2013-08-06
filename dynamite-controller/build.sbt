name := "dynamite-controller"

version := "1.0"

scalaVersion := "2.10.2"

sbtVersion := "0.12.4"

libraryDependencies ++= Seq(
	"com.typesafe.akka" %% "akka-actor" % "2.2.0",
	"net.liftweb" %% "lift-json" % "2.5.1"
)