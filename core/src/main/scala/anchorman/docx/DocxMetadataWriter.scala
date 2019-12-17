package anchorman.docx

import java.security.MessageDigest
import javax.xml.bind.DatatypeConverter

import anchorman.core.Document
import anchorman.media._

import scala.xml.NodeSeq

class DocxMetadataWriter {
  val documentRelId: String =
    "rDocument"

  val numberingRelId: String =
    "rNumbering"

  val stylesRelId: String =
    "rStyles"

  def mediaRelId(url: String): String = {
    val digest = MessageDigest.getInstance("MD5")
    digest.update(url.getBytes)
    "rMedia" + DatatypeConverter.printHexBinary(digest.digest()).toUpperCase
  }

  def writeContentTypes(doc: Document): NodeSeq =
    <Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
      <Default ContentType="application/xml" Extension="xml"/>
      <Default ContentType="image/jpeg" Extension="jpg"/>
      <Default ContentType="image/png" Extension="png"/>
      <Default ContentType="image/x-emf" Extension="emf"/>
      <Default ContentType="application/vnd.openxmlformats-package.relationships+xml" Extension="rels"/>
      <Override ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml" PartName="/word/document.xml"/>
      <Override ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.numbering+xml" PartName="/word/numbering.xml"/>
      <Override ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.styles+xml" PartName="/word/styles.xml"/>
    </Types>

  def writeRootRels(doc: Document): NodeSeq =
    <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
      <Relationship Id={documentRelId} Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="word/document.xml"/>
    </Relationships>

  def writeDocumentRels(doc: Document, media: Seq[ImageFile]): NodeSeq = {
    <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
      <Relationship Id={numberingRelId} Target="numbering.xml" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/numbering"/>
      <Relationship Id={stylesRelId} Target="styles.xml" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles"/>
      {
      media.map { file =>
        <Relationship Id={file.relId} Target={"media/" + file.filename} Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/image"/>
      }
    }
    </Relationships>
  }
}
