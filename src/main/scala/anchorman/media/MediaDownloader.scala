package anchorman.media

import java.io.ByteArrayInputStream
import java.security.MessageDigest
import javax.imageio.ImageIO
import javax.xml.bind.DatatypeConverter

import anchorman.core._
import play.api.libs.ws.WSClient

import scala.concurrent.{ExecutionContext => EC, _}
import scala.util.Try

class MediaDownloader(val wsClient: WSClient) {
  def imageUrls(block: Block): Seq[String] =
    block match {
      case EmptyBlock           => Seq.empty
      case BlockSeq(blocks)     => blocks.flatMap(imageUrls)
      case _: Para              => Seq.empty
      case UnorderedList(items) => items.flatMap(item => imageUrls(item.block))
      case OrderedList(items)   => items.flatMap(item => imageUrls(item.block))
      case Columns(blocks)      => blocks.flatMap(imageUrls)
      case Table(rows, _, _)    =>
        for {
          row <- rows
          cell <- row.cells
          url <- imageUrls(cell.block)
        } yield url
      case Image(url)           => Seq(url)
    }

  def downloadMediaFiles(urls: Seq[String])(implicit ec: EC): Future[MediaMap] =
    Future.sequence(urls.map(downloadMediaFile))
      .map(_.toMap)

  def downloadMediaFile(url: String)(implicit ec: EC): Future[(String, MediaFile)] =
    wsClient.url(url).get().map { response =>
      import WSClientImplicits._

      val media: MediaFile =
        Try(Option(ImageIO.read(new ByteArrayInputStream(response.bodyAsBytes)))).toOption.flatten match {
          case Some(image) =>
            ImageMediaFile(
              relId       = mediaRelId(url),
              filename    = generateFilename(response.contentType), // response.filename(url),
              contentType = response.contentType,
              width       = image.getWidth,
              height      = image.getHeight,
              content     = response.bodyAsBytes
            )
          case None =>
            PlainMediaFile(
              relId       = mediaRelId(url),
              filename    = generateFilename(response.contentType), // response.filename(url),
              contentType = response.contentType,
              content     = response.bodyAsBytes
            )
        }

      url -> media
    }

  private[media] def mediaRelId(url: String): String = {
    val digest = MessageDigest.getInstance("MD5")
    digest.update(url.getBytes)
    "rMedia" + DatatypeConverter.printHexBinary(digest.digest()).toUpperCase
  }

  private[media] def generateFilename(contentType: String): String = contentType match {
    case "image/jpeg" => generateFilenameWithExtension("jpg")
    case "image/png"  => generateFilenameWithExtension("png")
    case "text/csv"   => generateFilenameWithExtension("csv")
    case _            => generateFilenameWithExtension("unknown")
  }

  private[media] def generateFilenameWithExtension(extension: String): String =
    s"${java.util.UUID.randomUUID.toString}.${extension}"
}
