package anchorman

import java.io.File

import akka.actor._
import akka.stream._
import akka.stream.scaladsl._
import anchorman.core._
import anchorman.docx._
import anchorman.media._
import anchorman.media.play._
import org.scalatest._
import _root_.play.api.libs.ws.ahc.AhcWSClient

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
      implicit val system = ActorSystem("IntegrationSpec")
      implicit val materializer = ActorMaterializer()
      val wsClient = AhcWSClient()
      val mediaDownloader = new WsClientMediaDownloader(wsClient)
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
