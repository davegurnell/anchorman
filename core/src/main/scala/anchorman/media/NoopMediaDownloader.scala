package anchorman.media

import anchorman.core._
import cats.Applicative
import cats.implicits._

class NoopMediaDownloader[F[_]: Applicative] extends MediaDownloader[F] {
  def downloadImages(block: Block): F[List[ImageFile]] =
    List.empty[ImageFile].pure[F]
}
