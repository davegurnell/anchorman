package anchorman.syntax

import anchorman.core._

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

trait BlockImplicits {
  implicit def identityBlockPromoter[A <: Block]: BlockPromoter[A] =
    BlockPromoter.instance[A](identity)

  implicit val spanToBlockPromoter: BlockPromoter[Span] =
    BlockPromoter.instance[Span](Para(_))

  implicit def spanPromoterToBlockPromoter[A](implicit spanPromoter: SpanPromoter[A]): BlockPromoter[A] =
    BlockPromoter.instance[A](value => Para(spanPromoter(value)))
}
