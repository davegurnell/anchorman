package anchorman.core

import java.net.URLEncoder.{encode => urlEncode}

sealed abstract class Span extends Product with Serializable

case object EmptySpan extends Span

case class Text(text: String, style: TextStyle = TextStyle.empty) extends Span

case class Image(sourceUrls: Seq[String]) extends Span {
  def url = Image.url(sourceUrls)
}

object Image {
  def apply(url: String): Image =
    Image(List(url))

  def url(sourceUrls: Seq[String]): String =
    sourceUrls match {
      case Seq()    => ???
      case Seq(url) => url
      case urls     => s"superimpose:${sourceUrls.map(urlEncode(_, "utf-8")).mkString(":")}"
    }
}

case class SpanSeq(spans: Seq[Span]) extends Span
