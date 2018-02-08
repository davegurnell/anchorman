package anchorman.docx

import anchorman.media.ImageFile

import java.io.OutputStreamWriter
import java.util.zip.{ZipEntry, ZipOutputStream}

import scala.xml.{MinimizeMode, NodeSeq, XML}

object ZipImplicits {
  implicit class ZipOutputStreamOps(out: ZipOutputStream) {
    def writeXmlFile(path: String, nodes: NodeSeq): Unit = {
      out.putNextEntry(new ZipEntry(path))

      val writer = new OutputStreamWriter(out)
      try XML.write(writer, nodes.head, "UTF-8", true, null, MinimizeMode.Always)
      finally writer.flush()
    }

    def writeImageFile(path: String, file: ImageFile): Unit = {
      out.putNextEntry(new ZipEntry(path))
      try out.write(file.content)
      finally out.flush()
    }
  }
}