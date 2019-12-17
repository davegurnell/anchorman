package anchorman

import java.io.File

import anchorman.core._
import anchorman.docx._
import anchorman.media._
import cats.Id
import org.scalatest._

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
      val mediaDownloader = new NoopMediaDownloader[Id]()
      val docxWriter = new DocxWriter[Id](mediaDownloader)
      directory.mkdirs()
      docxWriter.write(doc, file)
    }
  }
}
