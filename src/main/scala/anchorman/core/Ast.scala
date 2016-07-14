package anchorman.core

case class Document(
  block         : Block,
  pageStyle     : PageStyle        = PageStyle.default,
  defaultStyle  : ParaAndTextStyle = ParaAndTextStyle.default,
  heading1Style : ParaAndTextStyle = ParaAndTextStyle.heading1,
  heading2Style : ParaAndTextStyle = ParaAndTextStyle.heading2,
  heading3Style : ParaAndTextStyle = ParaAndTextStyle.heading3
)

sealed trait Block
case object EmptyBlock extends Block
case class BlockSeq(blocks: Seq[Block]) extends Block
case class Para(span: Span, tpe: ParaType = ParaType.Default, style: ParaStyle = ParaStyle.empty) extends Block
case class Image(url: String) extends Block

object Para {
  val empty = Para(EmptySpan)
}

case class Columns(columns: Seq[Block]) extends Block

case class UnorderedList(items: Seq[ListItem]) extends Block
case class OrderedList(items: Seq[ListItem]) extends Block
case class ListItem(block: Block)

case class Table(rows: Seq[TableRow], manualColumns: Option[Seq[TableColumn]] = None, style: TableStyle = TableStyle.default) extends Block {
  val numRows: Int =
    rows.length

  val numCols: Int =
    rows.map(_.cells.length).foldLeft(0)(math.max)

  def defaultColumns: Seq[TableColumn] =
    (1 to numCols).map(_ => TableColumn.Auto)

  def columns: Seq[TableColumn] =
    manualColumns.getOrElse(defaultColumns).padTo(numCols, TableColumn.Auto)

  def cells: Seq[TableCell] =
    rows.flatMap(_.cells)
}
case class TableRow(cells: Seq[TableCell])
case class TableCell(block: Block)

sealed trait TableColumn
object TableColumn {
  case object Auto extends TableColumn
  case class Fixed(length: Dim) extends TableColumn
}

sealed trait Span
case object EmptySpan extends Span
case class SpanSeq(spans: Seq[Span]) extends Span
case class Text(text: String, style: TextStyle = TextStyle.empty) extends Span

sealed trait ParaType
object ParaType {
  case object Title extends ParaType
  case object Heading1 extends ParaType
  case object Heading2 extends ParaType
  case object Heading3 extends ParaType
  case object Default extends ParaType
}

case class PageStyle(
  size   : Dims   = Dims.defaultPageSize,
  margin : Insets = Insets.defaultPageMargins
) {
  def availableWidth = size.width - margin.left - margin.right
}

object PageStyle {
  val default = PageStyle()
}

case class ParaStyle(spacing: Option[ParaSpacing] = None)

case class ParaSpacing(before: Dim = 0.pt, after: Dim = 0.pt)

object ParaStyle {
  val empty = ParaStyle()
  val default  = ParaStyle(spacing = Some(ParaSpacing(before =  0.pt, after = 6.pt)))
  val heading1 = ParaStyle(spacing = Some(ParaSpacing(before = 24.pt, after = 6.pt)))
  val heading2 = ParaStyle(spacing = Some(ParaSpacing(before = 12.pt, after = 6.pt)))
  val heading3 = ParaStyle(spacing = Some(ParaSpacing(before =  6.pt, after = 6.pt)))
}

case class TableStyle(
  margin      : Insets  = Insets.empty,
  borders     : Borders = Borders.all,
  cellMargin  : Insets  = Insets(6.pt, 6.pt, 0.pt, 6.pt),
  cellBorders : Borders = Borders.all
)

object TableStyle {
  val default = TableStyle()
}

case class TextStyle(
  size      : Option[Dim]     = None,
  bold      : Option[Boolean] = None,
  italic    : Option[Boolean] = None,
  underline : Option[Boolean] = None
)

object TextStyle {
  val empty = TextStyle()
  val default  = TextStyle(size = Some(10.pt), bold = Some(false), italic = Some(false), underline = Some(false))
  val heading1 = TextStyle(size = Some(20.pt), bold = Some(true), italic = Some(false), underline = Some(false))
  val heading2 = TextStyle(size = Some(16.pt), bold = Some(true), italic = Some(false), underline = Some(false))
  val heading3 = TextStyle(size = Some(12.pt), bold = Some(true), italic = Some(false), underline = Some(false))
}

case class ParaAndTextStyle(paraStyle: ParaStyle, textStyle: TextStyle)

object ParaAndTextStyle {
  val default  = ParaAndTextStyle(ParaStyle.default,  TextStyle.default)
  val heading1 = ParaAndTextStyle(ParaStyle.heading1, TextStyle.heading1)
  val heading2 = ParaAndTextStyle(ParaStyle.heading2, TextStyle.heading2)
  val heading3 = ParaAndTextStyle(ParaStyle.heading3, TextStyle.heading3)
}

case class Borders(
  top     : Boolean = false,
  right   : Boolean = false,
  bottom  : Boolean = false,
  left    : Boolean = false
)

object Borders {
  val none = Borders(top = false, right = false, bottom = false, left = false)
  val all  = Borders(top =  true, right =  true, bottom =  true, left =  true)
}

case class Insets(
  top    : Dim = Dim.zero,
  right  : Dim = Dim.zero,
  bottom : Dim = Dim.zero,
  left   : Dim = Dim.zero
)

object Insets {
  val empty = Insets()

  val defaultPageMargins = Insets(
    top    = 1440.dxa,
    right  = 1797.dxa,
    bottom = 1440.dxa,
    left   = 1797.dxa
  )
}

case class Dims(width: Dim, height: Dim)

object Dims {
  val defaultPageSize = 11906.dxa by 16838.dxa
}

case class Dim(points: Double) {
  def + (that: Dim) = Dim(this.points + that.points)
  def - (that: Dim) = Dim(this.points - that.points)
  def * (that: Double) = Dim(this.points * that)
  def / (that: Double) = Dim(this.points / that)
  def > (that: Dim) = this.points > that.points

  def inches     = (points /    72)
  def dxa        = (points *    20).toInt
  def emu        = (points * 12700).toInt
  def halfPoints = (points *     2).toInt

  def by(that: Dim) = Dims(this, that)
}

object Dim {
  val zero = Dim(0.0)
}
