name := "scaliper"
version := "0.5.0"
scalaVersion := "2.10.6"
crossScalaVersions := Seq("2.11.7", "2.10.6")
description := "Scaliper is a framework for microbenchmarking in Scala, based on Google's Caliper project"
organization := "com.azavea"
licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.html"))
scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-Yinline-warnings",
  "-language:implicitConversions",
  "-language:reflectiveCalls",
  "-language:higherKinds",
  "-language:postfixOps",
  "-language:existentials",
  "-feature")
publishMavenStyle := true
publishArtifact in Test := false
pomIncludeRepository := { _ => false }

shellPrompt := { s => Project.extract(s).currentProject.id + " > " }

resolvers += Resolver.bintrayRepo("azavea", "maven")

libraryDependencies ++= Seq(
  "com.google.guava" % "guava" % "r09",
  "com.google.code.java-allocation-instrumenter" % "java-allocation-instrumenter" % "2.0",
  "com.esotericsoftware" % "kryo" % "3.0.1",
  "com.twitter" %% "chill" % "0.6.0",
  "io.spray"        %% "spray-json"    % "1.3.1",
  "org.scalatest"       %%  "scalatest"      % "2.2.0"
)

bintrayOrganization := Some("azavea")
bintrayRepository := "maven"
bintrayVcsUrl := Some("https://github.com/azavea/scaliper.git")
bintrayPackageLabels := Seq("scala", "microbenchmark", "caliper")

net.virtualvoid.sbt.graph.Plugin.graphSettings
