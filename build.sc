import $ivy.`io.chris-kipp::mill-ci-release::0.1.10`

import core.SbtTests
import mill._
import mill.api.Loose
import mill.scalalib._
import mill.scalalib.publish._
import io.kipp.mill.ci.release.CiReleaseModule

trait BaseModule extends SbtModule with CiReleaseModule {
  def scalaVersion = "2.13.14"

  def pomSettings = PomSettings(
    description = "Create DOCX and HTML reports from a Scala AST.",
    organization = "com.davegurnell",
    url = "https://github.com/davegurnell/anchorman",
    licenses = Seq(License.`Apache-2.0`),
    versionControl = VersionControl.github("davegurnell", "anchorman"),
    developers = Seq(
      Developer("davegurnell", "Dave Pereira-Gurnell", "https://github.com/davegurnell")
    )
  )
}

trait TestModule extends SbtTests with TestModule.ScalaTest {
  override def ivyDeps = Agg(
    ivy"org.scalatest::scalatest:3.1.0"
  )
}

trait ItModule extends TestModule {
  override def sources =
    T.sources(
      millSourcePath / "src" / "it" / "scala",
      millSourcePath / "src" / "it" / "java"
    )
}

object core extends BaseModule {
  override def artifactName = "anchorman-core"

  override def ivyDeps =
    super.ivyDeps() ++ Agg(
      ivy"com.davegurnell::unindent:1.3.1",
      ivy"com.outr::hasher:1.2.2",
      ivy"joda-time:joda-time:2.10.5",
      ivy"org.apache.xmlgraphics:fop:2.1",
      ivy"org.scala-lang.modules::scala-xml:2.2.0",
      ivy"org.typelevel::cats-core:2.1.0",
    )

  object test extends TestModule

  object it extends ItModule
}

object play extends BaseModule {
  override def artifactName = "anchorman-play"

  override def moduleDeps = Seq(core)

  override def ivyDeps =
    super.ivyDeps() ++ Agg(
      ivy"javax.xml.bind:jaxb-api:2.3.1",
    )

  override def compileIvyDeps =
    super.compileIvyDeps() ++ Agg(
      ivy"org.playframework::play-ws:3.0.0",
      ivy"org.playframework::play-ahc-ws:3.0.0",
    )

  object test extends TestModule

  object it extends ItModule {
    override def ivyDeps =
      super.ivyDeps() ++ Agg(
        ivy"org.apache.pekko::pekko-actor:1.0.2",
        ivy"org.playframework::play-ws:3.0.1",
        ivy"org.playframework::play-ahc-ws:3.0.1",
      )
  }
}
