package anchorman.media
package play

import java.awt.{Image => AwtImage, Graphics2D}
import java.awt.image.{BufferedImage, ImageObserver}
import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import java.security.MessageDigest
import javax.imageio.ImageIO
import javax.xml.bind.DatatypeConverter

import anchorman.core._
import _root_.play.api.libs.ws.WSClient

import scala.concurrent.{ExecutionContext => EC, _}
import scala.util.Try

class WsClientMediaDownloader(val wsClient: WSClient) extends MediaDownloader {
  def downloadMediaFiles(block: Block)(implicit ec: EC): Future[Seq[MediaFile]] =
    Future.sequence(images(block).map { image =>
      downloadMediaFiles(image.sourceUrls).map { files =>
        files.collect { case file: ImageMediaFile => file } match {
          case Seq()     => Seq()
          case Seq(file) => Seq(file)
          case files     => Seq(superimposeImageMediaFiles(files))
        }
      }
    }).map(_.flatten)

  def downloadMediaFiles(urls: Seq[String])(implicit ec: EC): Future[Seq[MediaFile]] =
    Future.sequence(urls.map(downloadMediaFile))

  def downloadMediaFile(url: String)(implicit ec: EC): Future[MediaFile] =
    wsClient.url(url).withFollowRedirects(true).get().map { response =>
      import WSClientImplicits._

      val bytes = response.bodyAsBytes.toArray

      readImage(bytes) match {
        case Some(image) =>
          ImageMediaFile(
            url         = url,
            relId       = mediaRelId(url),
            filename    = generateFilename(response.contentType), // response.filename(url),
            contentType = response.contentType,
            width       = image.getWidth,
            height      = image.getHeight,
            content     = bytes
          )
        case None =>
          PlainMediaFile(
            url         = url,
            relId       = mediaRelId(url),
            filename    = generateFilename(response.contentType), // response.filename(url),
            contentType = response.contentType,
            content     = bytes
          )
      }
    }

  def superimposeImageMediaFiles(files: Seq[ImageMediaFile])(implicit ec: EC): ImageMediaFile = {
    val outUrl          = Image.url(files.map(_.url))
    val outMediaRelId   = mediaRelId(outUrl)
    val outContentType  = "image/png"
    val outInformalType = "png"
    val outFilename     = generateFilename(outContentType)
    val outWidth        = files.map(_.width).max
    val outHeight       = files.map(_.height).max

    val outImage        = new BufferedImage(outWidth, outHeight, BufferedImage.TYPE_INT_ARGB)
    val outGraphics     = outImage.getGraphics.asInstanceOf[Graphics2D]

    files.foreach { file =>
      readImage(file.content).foreach { image =>
        outGraphics.drawImage(
          image,
          (outWidth  - image.getWidth ) / 2,
          (outHeight - image.getHeight) / 2,
          null
        )
      }
    }

    val outContent = {
      val buffer = new ByteArrayOutputStream
      ImageIO.write(outImage, outInformalType, buffer)
      buffer.toByteArray
    }

    ImageMediaFile(
      url         = outUrl,
      relId       = outMediaRelId,
      filename    = outFilename, // response.filename(url),
      contentType = outContentType,
      width       = outWidth,
      height      = outHeight,
      content     = outContent
    )
  }

  private[media] def readImage(bytes: Array[Byte]): Option[BufferedImage] =
    Try(Option(ImageIO.read(new ByteArrayInputStream(bytes)))).toOption.flatten

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
