package anchorman.pdf

import anchorman.core._
import scala.xml.{Document => XmlDocument, Text => XmlText, _}

object FopWriter {
  def write(doc: Document): Array[Byte] =
    writeDocument(doc).toString.getBytes

  def writeDocument(doc: Document): NodeSeq = {
    val Document(block, _, _, _, _, _) = doc

    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
      <fo:layout-master-set>
        <fo:simple-page-master master-name="simpleA4" page-height="29.7cm" page-width="21cm" margin-top="2cm" margin-bottom="2cm" margin-left="2cm" margin-right="2cm">
          <fo:region-body/>
        </fo:simple-page-master>
      </fo:layout-master-set>

      <fo:page-sequence master-reference="simpleA4">
        <fo:flow flow-name="xsl-region-body">
          { writeBlock(block) }
        </fo:flow>
      </fo:page-sequence>
    </fo:root>
  }

  def writeBlock(block: Block): NodeSeq =
    block match {
      case EmptyBlock =>
        NodeSeq.Empty

      case BlockSeq(blocks) =>
        blocks.foldLeft(NodeSeq.Empty)(_ ++ writeBlock(_))

      case Para(span, tpe, style) =>
        <fo:block>
          { writeSpan(span) }
        </fo:block>

      case OrderedList(items) =>
        writeList(items, index => <fo:block>{ index + 1 }.</fo:block>)

      case UnorderedList(items) =>
        writeList(items, index => <fo:block>•</fo:block>)

      case Columns(blocks) =>
        // TODO: Tables
        NodeSeq.Empty

      case Table(_, _, _) =>
        // TODO: Tables
        NodeSeq.Empty

      case Image(url) =>
        ???
    }

  def writeList(items: Seq[ListItem], bullet: Int => NodeSeq): NodeSeq =
    <fo:list-block>
      {
        items.zipWithIndex map { pair =>
          val (ListItem(block), index) = pair

          <fo:list-item>
            <fo:list-item-label end-indent="label-end()">
              { bullet(index) }
            </fo:list-item-label>
            <fo:list-item-body>
              { writeBlock(block) }
            </fo:list-item-body>
          </fo:list-item>
        }
      }
    </fo:list-block>

  def writeSpan(span: Span): NodeSeq =
    span match {
      case EmptySpan =>
        NodeSeq.Empty

      case SpanSeq(spans) =>
        spans.foldLeft(NodeSeq.Empty)(_ ++ writeSpan(_))

      case Text(text, style) =>
        XmlText(text)
    }
}
