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

case class Columns(columns: List[Block]) extends Block
case class UnorderedList(items: List[ListItem]) extends Block
case class OrderedList(items: List[ListItem]) extends Block

case class Table(
  rows          : List[TableRow],
  manualColumns : Option[List[TableColumn]] = None,
  style         : TableStyle                = TableStyle.default
) extends Block {
  val numRows: Int =
    rows.length

  val numCols: Int =
    rows.map(_.cells.length).foldLeft(0)(math.max)

  def defaultColumns: List[TableColumn] =
    (1 to numCols).map(_ => TableColumn.Auto).toList

  def columns: List[TableColumn] =
    manualColumns.getOrElse(defaultColumns).padTo(numCols, TableColumn.Auto)

  def cells: List[TableCell] =
    rows.flatMap(_.cells)
}

case class BlockSeq(blocks: List[Block]) extends Block

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
