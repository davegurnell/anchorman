package anchorman.media

sealed trait MediaFile {
  def relId: String
  def filename: String
  def contentType: String
  def content: Array[Byte]
}

case class PlainMediaFile(relId: String, filename: String, contentType: String, content: Array[Byte]) extends MediaFile
case class ImageMediaFile(relId: String, filename: String, contentType: String, width: Int, height: Int, content: Array[Byte]) extends MediaFile
