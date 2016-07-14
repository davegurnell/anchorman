# Anchorman

Report generation library.
Produces downloadable documents (DOCX, HTML, etc) from a Scala AST.
Early alpha status. Work in progress.

Copyright 2016 Dave Gurnell. Licensed [Apache 2][license].

[![Build Status](https://travis-ci.org/davegurnell/anchorman.svg?branch=develop)](https://travis-ci.org/davegurnell/anchorman)

## Getting Started

Grab the code from Bintray by adding the following to your `build.sbt`:

~~~ scala
scalaVersion := "2.11.8"

resolvers += "Awesome Utilities" at "https://dl.bintray.com/davegurnell/maven"

libraryDependencies += "com.davegurnell" %% "anchorman" % "0.1.0"
~~~

[license]: http://www.apache.org/licenses/LICENSE-2.0

