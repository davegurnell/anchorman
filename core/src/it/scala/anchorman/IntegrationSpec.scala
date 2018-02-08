package anchorman

import java.io.File

import anchorman.core._
import anchorman.docx._
import anchorman.media._
import anchorman.media.noop._
import org.scalatest._

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

trait IntegrationSpec extends FreeSpec {
  def name: String
  def doc: Document

  def directory: File =
    new File("target/it")

  def outputFile(extension: String): File =
    new File(directory, s"$name.$extension")

  s"$name integration spec" - {
    val file = outputFile("docx")

    s"writes ${file.getPath}" in {
      val mediaDownloader = new NoopMediaDownloader()
      val docxWriter = new DocxWriter(mediaDownloader)
      directory.mkdirs()
      Await.result(docxWriter.write(doc, file), 5.seconds)
    }
  }
}
