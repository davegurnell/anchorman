package anchorman.core

import anchorman.syntax._

case class PageStyle(
  size   : Dims   = Dims.defaultPageSize,
  margin : Insets = Insets.defaultPageMargins
) {
  def availableWidth = size.width - margin.left - margin.right
}

object PageStyle {
  val default = PageStyle()
}

case class ParaAndTextStyle(paraStyle: ParaStyle, textStyle: TextStyle)

object ParaAndTextStyle {
  val default  = ParaAndTextStyle(ParaStyle.default,  TextStyle.default)
  val heading1 = ParaAndTextStyle(ParaStyle.heading1, TextStyle.heading1)
  val heading2 = ParaAndTextStyle(ParaStyle.heading2, TextStyle.heading2)
  val heading3 = ParaAndTextStyle(ParaStyle.heading3, TextStyle.heading3)
}

case class ParaStyle(
  textAlign: Option[TextAlign] = None,
  verticalAlign: Option[VerticalAlign] = None,
  spacing: Option[ParaSpacing] = None
)

object ParaStyle {
  val empty = ParaStyle()
  val default  = ParaStyle(spacing = Some(ParaSpacing(before =  0.pt, after = 6.pt)))
  val heading1 = ParaStyle(spacing = Some(ParaSpacing(before = 24.pt, after = 6.pt)))
  val heading2 = ParaStyle(spacing = Some(ParaSpacing(before = 12.pt, after = 6.pt)))
  val heading3 = ParaStyle(spacing = Some(ParaSpacing(before =  6.pt, after = 6.pt)))
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
  val heading1 = TextStyle(size = Some(20.pt), bold = Some(true),  italic = Some(false), underline = Some(false))
  val heading2 = TextStyle(size = Some(16.pt), bold = Some(true),  italic = Some(false), underline = Some(false))
  val heading3 = TextStyle(size = Some(12.pt), bold = Some(true),  italic = Some(false), underline = Some(false))
}

case class TableStyle(
  margin      : Insets  = Insets.empty,
  borders     : Borders = Borders.all,
  cellMargin  : Insets  = Insets(6.pt, 6.pt, 0.pt, 6.pt),
  cellBorders : Borders = Borders.all
)

object TableStyle {
  val default    = TableStyle()
  val borderless = default.copy(
    borders     = Borders.none,
    cellBorders = Borders.none
  )
}

sealed abstract class TextAlign extends Product with Serializable
object TextAlign {
  case object Left   extends TextAlign
  case object Center extends TextAlign
  case object Right  extends TextAlign
  case object Full   extends TextAlign
}

sealed abstract class VerticalAlign extends Product with Serializable
object VerticalAlign {
  case object Top      extends VerticalAlign
  case object Center   extends VerticalAlign
  case object Bottom   extends VerticalAlign
  case object Baseline extends VerticalAlign
}

case class ParaSpacing(before: Dim = 0.pt, after: Dim = 0.pt)

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
