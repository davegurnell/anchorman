package anchorman.docx

import java.io._
import java.util.zip._

import anchorman.core._
import anchorman.media._
import cats.Applicative
import cats.implicits._

class DocxWriter[F[_]: Applicative](val mediaDownloader: MediaDownloader[F])
  extends DocumentWriter[F] {
  val metadataWriter = new DocxMetadataWriter
  val numberingWriter = new DocxNumberingWriter
  val styleWriter = new DocxStyleWriter
  val documentWriter = new DocxDocumentWriter(styleWriter)

  def write(doc: Document, stream: OutputStream): F[Unit] =
    mediaDownloader.downloadImages(doc.block).map(writeMedia(doc, stream))

  // TODO: This is all blocking IO...
  private def writeMedia(doc: Document, stream: OutputStream)(
    media: List[ImageFile]
  ): Unit = {
    val zip = new ZipOutputStream(stream)
    try {
      import ZipImplicits._

      zip.writeXmlFile(
        "[Content_Types].xml",
        metadataWriter.writeContentTypes(doc)
      )
      zip.writeXmlFile("_rels/.rels", metadataWriter.writeRootRels(doc))
      zip.writeXmlFile(
        "word/_rels/document.xml.rels",
        metadataWriter.writeDocumentRels(doc, media)
      )
      zip.writeXmlFile(
        "word/document.xml",
        documentWriter.writeDocumentXml(doc, media)
      )
      zip.writeXmlFile(
        "word/numbering.xml",
        numberingWriter.writeNumberingXml(doc)
      )
      zip.writeXmlFile(
        "word/styles.xml",
        styleWriter.writeStylesXml(doc, media)
      )

      media.foreach(
        file => zip.writeImageFile("word/media/" + file.filename, file)
      )
    } finally {
      zip.close()
    }
  }
}
