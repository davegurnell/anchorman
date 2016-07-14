package anchorman.core

import java.io.{File, FileOutputStream, OutputStream}
import scala.concurrent.{ExecutionContext => EC, _}

trait DocumentWriter {
  def write(doc: Document, file: File)(implicit ec: EC): Future[File] =
    write(doc, new FileOutputStream(file)).map(_ => file)

  def write(doc: Document, output: OutputStream)(implicit ec: EC): Future[Unit]
}
