package anchorman.core

trait Dsl extends PromoterImplicits with DimImplicits {
  def document[A](blocks: A*)(implicit promoter: BlockPromoter[A]): Document =
    Document(BlockSeq(blocks.map(promoter.apply)))

  def block[A](blocks: A*)(implicit promoter: BlockPromoter[A]): Block =
    blocks match {
      case Seq()     => EmptyBlock
      case Seq(head) => promoter(head)
      case blocks    => BlockSeq(blocks.map(promoter.apply))
    }

  def title[A](spans: A*)(implicit promoter: SpanPromoter[A]): Para =
    Para(SpanSeq(spans.map(promoter.apply)), tpe = ParaType.Title)

  def heading1[A](spans: A*)(implicit promoter: SpanPromoter[A]): Para =
    Para(SpanSeq(spans.map(promoter.apply)), tpe = ParaType.Heading1)

  def heading2[A](spans: A*)(implicit promoter: SpanPromoter[A]): Para =
    Para(SpanSeq(spans.map(promoter.apply)), tpe = ParaType.Heading2)

  def heading3[A](spans: A*)(implicit promoter: SpanPromoter[A]): Para =
    Para(SpanSeq(spans.map(promoter.apply)), tpe = ParaType.Heading3)

  def para[A](spans: A*)(implicit promoter: SpanPromoter[A]): Para =
    Para(SpanSeq(spans.map(promoter.apply)), tpe = ParaType.Default)

  def ulist[A](items: ListItem*): UnorderedList =
    UnorderedList(items)

  def olist[A](items: ListItem*): OrderedList =
    OrderedList(items)

  def item[A](blocks: A*)(implicit promoter: BlockPromoter[A]): ListItem =
    ListItem(BlockSeq(blocks.map(promoter.apply)))

  def table[A](rows: TableRow*): Table =
    Table(rows)

  def row[A](cells: TableCell*): TableRow =
    TableRow(cells)

  def cell[A](blocks: A*)(implicit promoter: BlockPromoter[A]): TableCell =
    TableCell(BlockSeq(blocks.map(promoter.apply)))

  def image(url: String): Image =
    Image(url)

  def columns[A](blocks: A*)(implicit promoter: BlockPromoter[A]): Columns =
    Columns(blocks.map(promoter.apply))

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

trait BlockPromoter[A] {
  def apply(value: A): Block
}

object BlockPromoter {
  def instance[A](func: A => Block): BlockPromoter[A] =
    new BlockPromoter[A] {
      def apply(value: A): Block =
        func(value)
    }
}

trait SpanPromoter[A] {
  def apply(value: A): Span
}

object SpanPromoter {
  def instance[A](func: A => Span): SpanPromoter[A] =
    new SpanPromoter[A] {
      def apply(value: A): Span =
        func(value)
    }
}

trait PromoterImplicits {
  implicit def identitySpanPromoter[A <: Span]: SpanPromoter[A] =
    SpanPromoter.instance[A](identity)

  implicit def identityBlockPromoter[A <: Block]: BlockPromoter[A] =
    BlockPromoter.instance[A](identity)

  implicit val spanToBlockPromoter: BlockPromoter[Span] =
    BlockPromoter.instance[Span](Para(_))

  implicit val stringToSpanPromoter: SpanPromoter[String] =
    SpanPromoter.instance[String](Text(_, TextStyle.empty))

  implicit def spanPromoterToBlockPromoter[A](implicit spanPromoter: SpanPromoter[A]): BlockPromoter[A] =
    BlockPromoter.instance[A](value => Para(spanPromoter(value)))
}

trait DimImplicits {
  implicit class IntOps(value: Int) {
    def dxa : Dim = Dim(value / 20.0)
    def pt  : Dim = Dim(value)
    def in  : Dim = Dim(value * 72.0)
  }

  implicit class DoubleOps(value: Double) {
    def dxa : Dim = Dim(value / 20.0)
    def pt  : Dim = Dim(value)
    def in  : Dim = Dim(value * 72.0)
  }
}