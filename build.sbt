name := "anchorman"

organization := "com.davegurnell"

scalaVersion in ThisBuild := "2.11.8"

resolvers += "Awesome Utilities" at "https://dl.bintray.com/davegurnell/maven"

libraryDependencies ++= Seq(
  "com.davegurnell"        %% "bulletin"          % "0.6.0",
  "com.davegurnell"        %% "unindent"          % "1.0.0",
  "com.typesafe.play"      %% "play-ws"           % "2.4.6",
  "joda-time"               % "joda-time"         % "2.8.1",
  "org.apache.poi"          % "poi"               % "3.14",
  "org.apache.poi"          % "poi-ooxml"         % "3.14",
  "org.apache.poi"          % "poi-ooxml-schemas" % "3.14",
  "org.apache.xmlgraphics"  % "fop"               % "2.1",
  "org.scala-lang.modules" %% "scala-xml"         % "1.0.4",
  "org.scalatest"          %% "scalatest"         % "2.2.4" % Test,
  "org.scalatest"          %% "scalatest"         % "2.2.4" % IntegrationTest,
  "org.typelevel"          %% "cats"              % "0.6.0"
)

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-Xfatal-warnings")

configs(IntegrationTest)

Defaults.itSettings

// Bintray:

licenses += ("Apache-2.0", url("http://apache.org/licenses/LICENSE-2.0"))

bintrayPackageLabels in bintray := Seq("scala", "docx", "html", "pdf", "utility")

bintrayRepository in bintray := "maven"
