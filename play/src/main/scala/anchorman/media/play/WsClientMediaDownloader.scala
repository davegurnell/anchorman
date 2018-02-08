package anchorman.media
package play

import java.awt.image.BufferedImage
import java.awt.{Graphics2D, Image => AwtImage}
import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import java.security.MessageDigest
import javax.imageio.ImageIO
import javax.xml.bind.DatatypeConverter

import _root_.play.api.libs.ws.{WSClient, WSResponse}
import anchorman.core._
import cats.data.{EitherT, NonEmptyList}
import cats.implicits._

import scala.concurrent.{ExecutionContext => EC, _}
import scala.util.Try

class WsClientMediaDownloader(val wsClient: WSClient) extends MediaDownloader {
  def downloadMediaFiles(block: Block)(implicit ec: EC): Future[List[MediaFile]] = {
    val images: List[Image] =
      this.images(block)

    val urls: List[String] =
      images.flatMap(_.sourceUrls.toList).distinct

    val media: Future[List[ImageMediaFile]] =
      downloadImages(urls)

    media.map(superimposeAll(images))
  }

  def downloadImages(urls: List[String])(implicit ec: EC): Future[List[ImageMediaFile]] =
    urls.flatTraverse(url => downloadImage(url).map(_.toList))

  def downloadImage(url: String)(implicit ec: EC): Future[Option[ImageMediaFile]] =
    download(url).map { response =>
      val bytes = response.bodyAsBytes.toArray

      readImage(url, bytes).map { image =>
        ImageMediaFile(
          url         = url,
          relId       = mediaRelId(url),
          filename    = generateFilename(response.contentType),
          contentType = response.contentType,
          width       = image.getWidth,
          height      = image.getHeight,
          content     = bytes
        )
      }
    }

  def superimposeAll(images: List[Image])(media: List[ImageMediaFile])(implicit ec: EC): List[ImageMediaFile] = {
    val mediaMap = media.map(file => file.url -> file).toMap
    val imageMap = images.map(img => img.url -> img).toMap

    imageMap.values.toList.flatMap { image =>
      image.sourceUrls match {
        case NonEmptyList(url, Nil) =>
          mediaMap.get(url)

        case urls: NonEmptyList[_] =>
          superimpose(urls.toList.flatMap(url => mediaMap.get(url).toList))
      }
    }
  }


  def superimpose(files: List[ImageMediaFile])(implicit ec: EC): Option[ImageMediaFile] =
    files match {
      case Nil          => None
      case head :: tail => Some(superimpose(NonEmptyList(head, tail)))
    }

  def superimpose(files: NonEmptyList[ImageMediaFile])(implicit ec: EC): ImageMediaFile = {
    val filesList = files.toList

    val outUrl          = Image.url(files.map(_.url))
    val outMediaRelId   = mediaRelId(outUrl)
    val outContentType  = "image/png"
    val outInformalType = "png"
    val outFilename     = generateFilename(outContentType)
    val outWidth        = filesList.map(_.width).max
    val outHeight       = filesList.map(_.height).max

    val outImage        = new BufferedImage(outWidth, outHeight, BufferedImage.TYPE_INT_ARGB)
    val outGraphics     = outImage.getGraphics.asInstanceOf[Graphics2D]

    filesList.foreach { file =>
      readImage(file.url, file.content).foreach { image =>
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
      filename    = outFilename,
      contentType = outContentType,
      width       = outWidth,
      height      = outHeight,
      content     = outContent
    )
  }

  private[media] def download(url: String)(implicit ec: EC): Future[WSResponse] =
    wsClient.url(url).withFollowRedirects(true).get()

  private[media] def readImage(url: String, bytes: Array[Byte]): Option[BufferedImage] =
    Try(Option(ImageIO.read(new ByteArrayInputStream(bytes)))).toOption.flatten

  private[media] def mediaRelId(url: String): String = {
    val digest = MessageDigest.getInstance("MD5")
    digest.update(url.getBytes)
    "rMedia" + DatatypeConverter.printHexBinary(digest.digest()).toUpperCase
  }

  private[media] def generateFilename(contentType: String): String =
    contentType match {
      case "image/jpeg" => generateFilenameWithExtension("jpg")
      case "image/png"  => generateFilenameWithExtension("png")
      case "text/csv"   => generateFilenameWithExtension("csv")
      case _            => generateFilenameWithExtension("unknown")
    }

  private[media] def generateFilenameWithExtension(extension: String): String =
    s"${java.util.UUID.randomUUID.toString}.$extension"
}
