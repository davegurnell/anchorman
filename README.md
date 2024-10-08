# Anchorman

Report generation library.
Produces downloadable documents (DOCX, HTML, etc) from a Scala AST.
Early alpha status. Work in progress.

Copyright 2016 Dave Gurnell. Licensed [Apache 2][license].

[![Build Status](https://travis-ci.org/davegurnell/anchorman.svg?branch=master)](https://travis-ci.org/davegurnell/anchorman)
[![Coverage status](https://img.shields.io/codecov/c/github/davegurnell/anchorman/master.svg)](https://codecov.io/github/davegurnell/anchorman)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.davegurnell/anchorman-core_2.13/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.davegurnell/anchorman-core_2.13)

## Getting Started

Grab the code from Bintray by adding the following to your `build.sbt`:

```scala
libraryDependencies ++= Seq(
  "com.davegurnell" %% "anchorman-core" % "<<VERSION>>", // You definitely need this
  "com.davegurnell" %% "anchorman-play" % "<<VERSION>>", // To download images in Play
)
```

[license]: http://www.apache.org/licenses/LICENSE-2.0

## Synopsis

Anchorman lets you generate "reports" (downloadable documents) for your web apps.
First build an AST representing the document you want to write:

```scala
import anchorman.core._
import anchorman.syntax._

val doc: Document = document(
  para(
    """
    |This is a paragraph. Isn't it lovely?
    |But wait! There's more!
    """.trim.stripMargin
  ),
  para(
    """
    |The style modifier below centers this paragraph.
    """.trim.stripMargin
  ).align(TextAlign.Center),
  olist(
    item("This is a numbered list."),
    item("Amazing!"),
    item("Just like good old HTML"),
  )
)
```

Then "render" that document using a writer.
Here's an HTML example:

```scala
import java.io.File

import anchorman.html._
import cats.implicits._

import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

val htmlWriter = new HtmlWriter[Future]()

val file = new File("doc.html")

val future: Future[Unit] =
  htmlWriter.write(doc, file)

Await.result(future, 5.seconds)
```

There's also a writer for DOCX files that needs
a "media downloader" to download and cache images.
We provide an implementation that uses the standalone version of
[play-ws](https://github.com/playframework/play-ws),
but you can code your own relatively easily.
We'd happily accept a PR for an [sttp](https://github.com/softwaremill/sttp) client ;)

```scala
import java.io.File

import pekko.actor._
import anchorman.core._
import anchorman.docx._
import anchorman.media._
import cats.implicits._
import org.scalatest._
import play.api.libs.ws.ahc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._

implicit val system: ActorSystem =
  ActorSystem("Anchorman")

val wsClient = StandaloneAhcWSClient()
val mediaDownloader = new WsClientMediaDownloader(wsClient)
val docxWriter = new DocxWriter[Future](mediaDownloader)

val future: Future[Unit] =
  htmlWriter.write(doc, file)

Await.result(future, 5.seconds)
```

## Releasing

The repo uses sbt-native-packager and sbt-ci-release to publish:

- snapshot builds when regular commits are pushed to the main branch;
- release builds when tags of the form `vX.Y.Z` are pushed.

The release uses four environment variables:

- `PGP_PASSPHRASE` is the password for "Anchorman Publishing Key";
- `PGP_SECRET` is the base64 encoding of the secret key,
  exported from GPG as a single line of base64-encoded text
  [as described here](https://github.com/sbt/sbt-ci-release?tab=readme-ov-file#secrets)
- `SONATYPE_USERNAME` is my Sonatype username;
- `SONATYPE_PASSWORD` is my Sonatype password.

These are stored in secrets in Github Actions.
