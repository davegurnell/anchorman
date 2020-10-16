package anchorman.media

import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import java.security.MessageDigest

import anchorman.core._
import cats.data.NonEmptyList
import cats.implicits._
import javax.imageio.ImageIO
import javax.xml.bind.DatatypeConverter
import play.api.libs.ws._
import scala.concurrent._
import scala.util.Try

class WsClientMediaDownloader(val wsClient: StandaloneWSClient)(
  implicit ec: ExecutionContext
) extends MediaDownloader[Future] {
  override def downloadImages(block: Block): Future[List[ImageFile]] =
    for {
      images <- this.images(block)
      urls = images.flatMap(_.sourceUrls.toList).distinct
      files <- downloadImages(urls).map(superimposeAll(images))
    } yield files

  private[media] def downloadImages(
    urls: List[String]
  ): Future[List[ImageFile]] =
    urls.flatTraverse(url => downloadImage(url).map(_.toList))

  private[media] def downloadImage(url: String): Future[Option[ImageFile]] =
    download(url).map { response =>
      val bytes = response.bodyAsBytes.toArray

      readImage(url, bytes).map { image =>
        ImageFile(
          url = url,
          relId = mediaRelId(url),
          filename = generateFilename(response.contentType),
          contentType = response.contentType,
          width = image.getWidth,
          height = image.getHeight,
          content = bytes
        )
      }
    }

  private[media] def superimposeAll(
    images: List[Image]
  )(media: List[ImageFile]): List[ImageFile] = {
    val mediaMap = media.map(file => file.url -> file).toMap
    val imageMap = images.map(img => img.url  -> img).toMap

    imageMap.values.toList.flatMap { image =>
      image.sourceUrls match {
        case NonEmptyList(url, Nil) =>
          mediaMap.get(url)

        case urls: NonEmptyList[_] =>
          superimpose(urls.toList.flatMap(url => mediaMap.get(url).toList))
      }
    }
  }

  private[media] def superimpose(files: List[ImageFile]): Option[ImageFile] =
    files match {
      case Nil          => None
      case head :: tail => Some(superimpose(NonEmptyList(head, tail)))
    }

  private[media] def superimpose(files: NonEmptyList[ImageFile]): ImageFile = {
    val filesList = files.toList

    val outUrl = Image.url(files.map(_.url))
    val outMediaRelId = mediaRelId(outUrl)
    val outContentType = "image/png"
    val outInformalType = "png"
    val outFilename = generateFilename(outContentType)
    val outWidth = filesList.map(_.width).max
    val outHeight = filesList.map(_.height).max

    val outImage =
      new BufferedImage(outWidth, outHeight, BufferedImage.TYPE_INT_ARGB)
    val outGraphics = outImage.getGraphics.asInstanceOf[Graphics2D]

    filesList.foreach { file =>
      readImage(file.url, file.content).foreach { image =>
        outGraphics.drawImage(
          image,
          (outWidth - image.getWidth) / 2,
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

    ImageFile(
      url = outUrl,
      relId = outMediaRelId,
      filename = outFilename,
      contentType = outContentType,
      width = outWidth,
      height = outHeight,
      content = outContent
    )
  }

  private[media] def download(url: String): Future[StandaloneWSResponse] =
    wsClient.url(url).withFollowRedirects(true).get()

  private[media] def readImage(
    url: String,
    bytes: Array[Byte]
  ): Option[BufferedImage] =
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
