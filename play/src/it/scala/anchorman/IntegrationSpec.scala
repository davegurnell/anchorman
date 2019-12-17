package anchorman

import java.io.File

import akka.actor._
import anchorman.core._
import anchorman.docx._
import anchorman.media._
import cats.implicits._
import org.scalatest._
import play.api.libs.ws.ahc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
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
      implicit val system: ActorSystem =
        ActorSystem("IntegrationSpec")

      val wsClient = StandaloneAhcWSClient()
      val mediaDownloader = new WsClientMediaDownloader(wsClient)
      val docxWriter = new DocxWriter[Future](mediaDownloader)

      try {
        directory.mkdirs()
        Await.result(docxWriter.write(doc, file), 5.seconds)
      } finally {
        wsClient.close()
      }
    }
  }
}
