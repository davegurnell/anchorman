package anchorman.media

sealed trait MediaFile {
  def relId: String
  def filename: String
  def contentType: String
  def content: Array[Byte]
  def isImage: Boolean
}

case class PlainMediaFile(
  relId: String,
  filename: String,
  contentType: String,
  content: Array[Byte]
) extends MediaFile {
  val isImage: Boolean =
    false

  override def toString: String =
    s"PlainMediaFile($relId,$filename,$contentType,<content>)"
}

case class ImageMediaFile(
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
    s"ImageMediaFile($relId,$filename,$contentType,$width,$height,<content>)"
}
