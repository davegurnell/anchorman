package anchorman.docx

import java.io._
import java.util.zip._

import anchorman.core._
import anchorman.media._

import scala.concurrent.{ExecutionContext => EC, _}

class DocxWriter(val mediaDownloader: MediaDownloader) {
  val metadataWriter  = new DocxMetadataWriter
  val numberingWriter = new DocxNumberingWriter
  val styleWriter     = new DocxStyleWriter
  val documentWriter  = new DocxDocumentWriter(styleWriter)

  def write(doc: Document, file: File)(implicit ec: EC): Future[Unit] =
    mediaDownloader.downloadMediaFiles(mediaDownloader.imageUrls(doc.block)) map { media: MediaMap =>
      import ZipImplicits._

      val out = new ZipOutputStream(new FileOutputStream(file))
      try {
        out.writeXmlFile("[Content_Types].xml",          metadataWriter.writeContentTypes(doc))
        out.writeXmlFile("_rels/.rels",                  metadataWriter.writeRootRels(doc))
        out.writeXmlFile("word/_rels/document.xml.rels", metadataWriter.writeDocumentRels(doc, media))
        out.writeXmlFile("word/document.xml",            documentWriter.writeDocumentXml(doc, media))
        out.writeXmlFile("word/numbering.xml",           numberingWriter.writeNumberingXml(doc))
        out.writeXmlFile("word/styles.xml",              styleWriter.writeStylesXml(doc, media))
        for (file <- media.values) {
          out.writeMediaFile("word/media/" + file.filename, file)
        }
      } finally {
        out.close()
      }
    }
}
