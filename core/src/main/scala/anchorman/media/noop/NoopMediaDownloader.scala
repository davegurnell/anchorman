package anchorman.media
package noop

import anchorman.core._
import scala.concurrent.{ExecutionContext => EC, _}

class NoopMediaDownloader extends MediaDownloader {
  def downloadImages(block: Block)(implicit ec: EC): Future[List[ImageFile]] =
    Future.successful(List.empty[ImageFile])
}
