package anchorman.syntax

import anchorman.core._
import cats.data.NonEmptyList

trait Dsl {
  def document[A](blocks: A*)(implicit promoter: BlockPromoter[A]): Document =
    Document(BlockSeq(blocks.map(promoter.apply).toList))

  def block[A](blocks: A*)(implicit promoter: BlockPromoter[A]): Block =
    blocks match {
      case Seq()     => EmptyBlock
      case Seq(head) => promoter(head)
      case blocks    => BlockSeq(blocks.map(promoter.apply).toList)
    }

  def title[A](spans: A*)(implicit promoter: SpanPromoter[A]): Para =
    Para(SpanSeq(spans.map(promoter.apply).toList), tpe = ParaType.Title)

  def heading1[A](spans: A*)(implicit promoter: SpanPromoter[A]): Para =
    Para(SpanSeq(spans.map(promoter.apply).toList), tpe = ParaType.Heading1)

  def heading2[A](spans: A*)(implicit promoter: SpanPromoter[A]): Para =
    Para(SpanSeq(spans.map(promoter.apply).toList), tpe = ParaType.Heading2)

  def heading3[A](spans: A*)(implicit promoter: SpanPromoter[A]): Para =
    Para(SpanSeq(spans.map(promoter.apply).toList), tpe = ParaType.Heading3)

  def para[A](spans: A*)(implicit promoter: SpanPromoter[A]): Para =
    Para(SpanSeq(spans.map(promoter.apply).toList), tpe = ParaType.Default)

  def ulist[A](items: ListItem*): UnorderedList =
    UnorderedList(items.toList)

  def olist[A](items: ListItem*): OrderedList =
    OrderedList(items.toList)

  def item[A](blocks: A*)(implicit promoter: BlockPromoter[A]): ListItem =
    ListItem(BlockSeq(blocks.map(promoter.apply).toList))

  def table[A](rows: TableRow*): Table =
    Table(rows.toList)

  def row[A](cells: TableCell*): TableRow =
    TableRow(cells.toList)

  def cell[A](blocks: A*)(implicit promoter: BlockPromoter[A]): TableCell =
    TableCell(BlockSeq(blocks.map(promoter.apply).toList))

  def image(url: String, urls: String*): Image =
    Image(NonEmptyList(url, urls.toList))

  def columns[A](blocks: A*)(implicit promoter: BlockPromoter[A]): Columns =
    Columns(blocks.map(promoter.apply).toList)

  def text(text: String): Text =
    Text(text, TextStyle.empty)

  def bold(text: String): Text =
    Text(text, TextStyle(bold = Some(true)))

  def italic(text: String): Text =
    Text(text, TextStyle(italic = Some(true)))

  def underline(text: String): Text =
    Text(text, TextStyle(underline = Some(true)))

  def fontSize(size: Dim)(text: String): Text =
    Text(text, TextStyle(size = Some(size)))
}
