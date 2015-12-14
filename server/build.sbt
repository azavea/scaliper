name := "scaliper-server"
fork := true
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor"   % Version.akka,
  "io.spray"        %% "spray-routing" % Version.spray,
  "io.spray"        %% "spray-can"     % Version.spray,
  "io.spray"        %% "spray-httpx"   % Version.spray,
  "io.spray"        %% "spray-json"    % Version.sprayJson,
  "com.typesafe.slick" %% "slick" % "3.1.0",
  "org.xerial"          % "sqlite-jdbc" % "3.8.11.2",
  "org.slf4j" % "slf4j-nop" % "1.6.4"
)

Revolver.settings
