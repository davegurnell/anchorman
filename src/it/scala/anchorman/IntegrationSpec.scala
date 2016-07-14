package anchorman

import java.io.File

import anchorman.core._
import anchorman.docx._
import anchorman.media._
import org.scalatest._
import play.api.libs.ws.ning.NingWSClient

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

trait IntegrationSpec extends FreeSpec {
  def name: String
  def doc: Document

  def directory: File =
    new File("target/it")

  def outputFile(extension: String): File =
    new File(directory, s"${name}.${extension}")

  s"${name} integration spec" - {
    val file = outputFile("docx")

    s"writes ${file.getPath}" in {
      val wsClient = NingWSClient()
      val mediaDownloader = new MediaDownloader(wsClient)
      val docxWriter = new DocxWriter(mediaDownloader)
      try {
        directory.mkdirs()
        Await.result(docxWriter.write(doc, file), 5.seconds)
      } finally {
        wsClient.close()
      }
    }
  }
}
