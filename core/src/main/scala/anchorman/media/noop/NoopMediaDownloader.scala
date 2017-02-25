package anchorman.media
package noop

import anchorman.core._
import scala.concurrent.{ExecutionContext => EC, _}

class NoopMediaDownloader extends MediaDownloader {
  def downloadMediaFiles(block: Block)(implicit ec: EC): Future[Seq[MediaFile]] =
    Future.successful(Seq.empty)
}
