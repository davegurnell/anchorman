package anchorman.media

sealed abstract class MediaFile extends Product with Serializable {
  def url: String
  def relId: String
  def filename: String
  def contentType: String
  def content: Array[Byte]
  def isImage: Boolean
}

case class PlainMediaFile(
  url: String,
  relId: String,
  filename: String,
  contentType: String,
  content: Array[Byte]
) extends MediaFile {
  val isImage: Boolean =
    false

  override def toString: String =
    s"PlainMediaFile($url,$relId,$filename,$contentType,<content>)"
}

case class ImageMediaFile(
  url: String,
  relId: String,
  filename: String,
  contentType: String,
  width: Int,
  height: Int,
  content: Array[Byte]
) extends MediaFile {
  val isImage: Boolean =
    true

  override def toString: String =
    s"ImageMediaFile($url,$relId,$filename,$contentType,$width,$height,<content>)"
}
