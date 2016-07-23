package anchorman.docx

import java.io._
import java.util.zip._

import anchorman.core._
import anchorman.media._

import scala.concurrent.{ExecutionContext => EC, _}

class DocxWriter(val mediaDownloader: MediaDownloader) extends DocumentWriter {
  val metadataWriter  = new DocxMetadataWriter
  val numberingWriter = new DocxNumberingWriter
  val styleWriter     = new DocxStyleWriter
  val documentWriter  = new DocxDocumentWriter(styleWriter)

  def write(doc: Document, stream: OutputStream)(implicit ec: EC): Future[Unit] = {
    mediaDownloader.downloadMediaFiles(mediaDownloader.imageUrls(doc.block)) map { media: MediaMap =>
      println(media)
      val zip = new ZipOutputStream(stream)
      try {
        import ZipImplicits._

        zip.writeXmlFile("[Content_Types].xml",          metadataWriter.writeContentTypes(doc))
        zip.writeXmlFile("_rels/.rels",                  metadataWriter.writeRootRels(doc))
        zip.writeXmlFile("word/_rels/document.xml.rels", metadataWriter.writeDocumentRels(doc, media))
        zip.writeXmlFile("word/document.xml",            documentWriter.writeDocumentXml(doc, media))
        zip.writeXmlFile("word/numbering.xml",           numberingWriter.writeNumberingXml(doc))
        zip.writeXmlFile("word/styles.xml",              styleWriter.writeStylesXml(doc, media))
        for (file <- media.values if file.isImage) {
          zip.writeMediaFile("word/media/" + file.filename, file)
        }
      } finally {
        zip.close()
      }
    }
  }
}
