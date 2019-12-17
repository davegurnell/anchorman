package anchorman.core

import java.io.{File, FileOutputStream, OutputStream}

import cats.Functor
import cats.implicits._

abstract class DocumentWriter[F[_]: Functor] {
  def write(doc: Document, file: File): F[File] =
    write(doc, new FileOutputStream(file)).map(_ => file)

  def write(doc: Document, output: OutputStream): F[Unit]
}
