# Anchorman

Reporti generation library.
Produces downloadable documents (DOCX, HTML, etc) from structured Scala objects.
Early alpha status. Work in progress.

Copyright 2016 Dave Gurnell. Licensed [Apache 2][license].

## Getting Started

Grab the code from Bintray by adding the following to your `build.sbt`:

~~~ scala
scalaVersion := "2.11.7"

resolvers += "Awesome Utilities" at "https://dl.bintray.com/davegurnell/maven"

libraryDependencies += "com.davegurnell" %% "anchorman" % "0.1.0"
~~~
