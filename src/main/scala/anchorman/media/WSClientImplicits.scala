package anchorman.media

import play.api.libs.ws.WSResponse

object WSClientImplicits {
  implicit class WSResponseOps(response: WSResponse) {
    def filename(url: String): String =
      contentDispositionFilename getOrElse urlFilename(url)

    val ContentDispositionFilename = ".*filename=\"([^;]+)\"".r

    def contentDispositionFilename: Option[String] =
      response header "Content-Disposition" collect {
        case ContentDispositionFilename(filename) =>
          filename.replaceAll("[^a-zA-Z0-9.-_]", "")
      }

    def urlFilename(url: String): String =
      java.net.URLDecoder.decode(url.split("/").last, "UTF-8")

    def contentType: String =
      response header "Content-Type" getOrElse "application/octet-stream"
  }
}
