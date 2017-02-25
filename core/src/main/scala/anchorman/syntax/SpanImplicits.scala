package anchorman.syntax

import anchorman.core._

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

trait SpanImplicits {
  implicit def identitySpanPromoter[A <: Span]: SpanPromoter[A] =
    SpanPromoter.instance[A](identity)

  implicit val stringToSpanPromoter: SpanPromoter[String] =
    SpanPromoter.instance[String](Text(_, TextStyle.empty))
}