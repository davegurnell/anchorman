package anchorman.core

sealed abstract class Block extends Product with Serializable

case object EmptyBlock extends Block

case class Para(
  span  : Span      = EmptySpan,
  tpe   : ParaType  = ParaType.Default,
  style : ParaStyle = ParaStyle.empty
) extends Block

object Para {
  val empty = Para(EmptySpan)
}

case class Columns(columns: Seq[Block]) extends Block

case class UnorderedList(items: Seq[ListItem]) extends Block
case class OrderedList(items: Seq[ListItem]) extends Block

case class Table(
  rows          : Seq[TableRow],
  manualColumns : Option[Seq[TableColumn]] = None,
  style         : TableStyle               = TableStyle.default
) extends Block {
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

case class BlockSeq(blocks: Seq[Block]) extends Block

sealed abstract class ParaType extends Product with Serializable
object ParaType {
  case object Title    extends ParaType
  case object Heading1 extends ParaType
  case object Heading2 extends ParaType
  case object Heading3 extends ParaType
  case object Default  extends ParaType
}

case class ListItem(block: Block)

case class TableRow(cells: Seq[TableCell])

case class TableCell(block: Block)

sealed abstract class TableColumn extends Product with Serializable
object TableColumn {
  case object Auto extends TableColumn
  case class Fixed(length: Dim) extends TableColumn
}
