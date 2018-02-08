package anchorman.media

case class ImageFile(
  url: String,
  relId: String,
  filename: String,
  contentType: String,
  width: Int,
  height: Int,
  content: Array[Byte]
) {
  override def toString: String =
    s"ImageImageFile($url,$relId,$filename,$contentType,$width,$height,<content>)"
}
