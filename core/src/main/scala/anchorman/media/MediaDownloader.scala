package anchorman.media

import anchorman.core._

import scala.concurrent.{ExecutionContext => EC, _}

trait MediaDownloader {
  def downloadMediaFiles(block: Block)(implicit ec: EC): Future[List[MediaFile]]

  def images(block: Block): List[Image] =
    block match {
      case EmptyBlock           => Nil
      case Para(span, _, _)     => images(span)
      case UnorderedList(items) => items.flatMap(item => images(item.block))
      case OrderedList(items)   => items.flatMap(item => images(item.block))
      case Columns(blocks)      => blocks.flatMap(images)
      case Table(rows, _, _)    => for {
                                     row  <- rows
                                     cell <- row.cells
                                     url  <- images(cell.block)
                                   } yield url
      case BlockSeq(blocks)     => blocks.flatMap(images)
    }

  def images(span: Span): List[Image] =
    span match {
      case EmptySpan            => Nil
      case _ : Text             => Nil
      case i : Image            => List(i)
      case SpanSeq(spans)       => spans.flatMap(images)
    }
}
