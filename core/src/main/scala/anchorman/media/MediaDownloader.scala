package anchorman.media

import anchorman.core._
import scala.concurrent.{ExecutionContext => EC, _}

trait MediaDownloader {
  def downloadMediaFiles(block: Block)(implicit ec: EC): Future[Seq[MediaFile]]

  def images(block: Block): Seq[Image] =
    block match {
      case EmptyBlock           => Seq.empty
      case Para(span, _, _)     => images(span)
      case UnorderedList(items) => items.flatMap(item => images(item.block))
      case OrderedList(items)   => items.flatMap(item => images(item.block))
      case Columns(blocks)      => blocks.flatMap(images)
      case Table(rows, _, _)    =>
        for {
          row  <- rows
          cell <- row.cells
          url  <- images(cell.block)
        } yield url
      case BlockSeq(blocks)     => blocks.flatMap(images)
    }

  def images(span: Span): Seq[Image] =
    span match {
      case EmptySpan            => Seq.empty
      case _ : Text             => Seq.empty
      case i : Image            => Seq(i)
      case SpanSeq(spans)       => spans.flatMap(images)
    }
}
