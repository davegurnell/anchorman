package anchorman.core

import java.net.URLEncoder.{encode => urlEncode}

import cats.data.NonEmptyList

sealed abstract class Span extends Product with Serializable

case object EmptySpan extends Span

case class Text(text: String, style: TextStyle = TextStyle.empty) extends Span

case class Image(sourceUrls: NonEmptyList[String]) extends Span {
  def url: String =
    Image.url(sourceUrls)
}

object Image {
  def apply(url: String, urls: String*): Image =
    Image(NonEmptyList(url, urls.toList))

  def url(sourceUrls: NonEmptyList[String]): String =
    sourceUrls match {
      case NonEmptyList(head, Nil) => head
      case NonEmptyList(head, tail) =>
        s"superimpose:${(head :: tail).map(urlEncode(_, "utf-8")).mkString(":")}"
    }
}

case class SpanSeq(spans: List[Span]) extends Span
