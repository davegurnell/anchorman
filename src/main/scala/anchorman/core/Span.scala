package anchorman.core

sealed trait Span extends Product with Serializable

case object EmptySpan extends Span

case class Text(text: String, style: TextStyle = TextStyle.empty) extends Span

case class Image(url: String) extends Span

case class SpanSeq(spans: Seq[Span]) extends Span
