package anchorman.media

import anchorman.core._
import cats.Applicative
import cats.implicits._

abstract class MediaDownloader[F[_]: Applicative] {
  def downloadImages(block: Block): F[List[ImageFile]]

  final def images(block: Block): F[List[Image]] =
    block match {
      case EmptyBlock =>
        List.empty[Image].pure[F]

      case Para(span, _, _) =>
        images(span)

      case UnorderedList(items) =>
        items.flatTraverse(item => images(item.block))

      case OrderedList(items) =>
        items.flatTraverse(item => images(item.block))

      case Columns(blocks) =>
        blocks.flatTraverse(images)

      case Table(rows, _, _) =>
        rows.flatTraverse { row =>
          row.cells.flatTraverse(cell => images(cell.block))
        }

      case BlockSeq(blocks) =>
        blocks.flatTraverse(images)
    }

  final def images(span: Span): F[List[Image]] =
    span match {
      case EmptySpan =>
        List.empty[Image].pure[F]

      case _: Text =>
        List.empty[Image].pure[F]

      case i: Image =>
        List(i).pure[F]

      case SpanSeq(spans) =>
        spans.flatTraverse(images)
    }
}
