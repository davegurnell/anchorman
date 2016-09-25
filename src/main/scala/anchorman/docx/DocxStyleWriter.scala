package anchorman.docx

import anchorman.core._
import anchorman.syntax._
import anchorman.media._

import scala.xml.NodeSeq

class DocxStyleWriter {
  val defaultParagraphSpacing = 6.pt

  def writeStylesXml(doc: Document, media: Seq[MediaFile]): NodeSeq = {
    val Document(
      block,
      pageStyle,
      ParaAndTextStyle(defaultParaStyle, defaultTextStyle),
      ParaAndTextStyle(heading1ParaStyle, heading1TextStyle),
      ParaAndTextStyle(heading2ParaStyle, heading2TextStyle),
      ParaAndTextStyle(heading3ParaStyle, heading3TextStyle)
    ) = doc

    <w:styles mc:Ignorable="w14"
              xmlns:mc="http://schemas.openxmlformats.org/markup-compatibility/2006"
              xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships"
              xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main"
              xmlns:w14="http://schemas.microsoft.com/office/word/2010/wordml">

      <w:docDefaults>
        <w:pPrDefault>
          <w:pPr>
            { writeParaStyle(defaultParaStyle) }
          </w:pPr>
        </w:pPrDefault>
        <w:rPrDefault>
          <w:rPr>
            { writeTextStyle(defaultTextStyle) }
          </w:rPr>
        </w:rPrDefault>
      </w:docDefaults>

      <w:style w:type="paragraph" w:styleId="Normal">
        <w:name w:val="Normal"/>
        <w:pPr>
          { writeParaStyle(defaultParaStyle) }
        </w:pPr>
        <w:rPr>
          { writeTextStyle(defaultTextStyle) }
        </w:rPr>
      </w:style>

      <w:style w:type="paragraph" w:styleId="ListParagraph">
        <w:name w:val="List Paragraph"/>
        <w:pPr>
          { writeParaStyle(defaultParaStyle) }
        </w:pPr>
        <w:rPr>
          { writeTextStyle(defaultTextStyle) }
        </w:rPr>
      </w:style>

      <w:style w:type="paragraph" w:styleId="Heading1">
        <w:name w:val="Heading 1"/>
        <w:pPr>
          { writeParaStyle(heading1ParaStyle) }
        </w:pPr>
        <w:rPr>
          { writeTextStyle(heading1TextStyle) }
        </w:rPr>
      </w:style>

      <w:style w:type="paragraph" w:styleId="Heading2">
        <w:name w:val="Heading 2"/>
        <w:pPr>
          { writeParaStyle(heading2ParaStyle) }
        </w:pPr>
        <w:rPr>
          { writeTextStyle(heading2TextStyle) }
        </w:rPr>
      </w:style>

      <w:style w:type="paragraph" w:styleId="Heading3">
        <w:name w:val="Heading 3"/>
        <w:pPr>
          { writeParaStyle(heading3ParaStyle) }
        </w:pPr>
        <w:rPr>
          { writeTextStyle(heading3TextStyle) }
        </w:rPr>
      </w:style>

    </w:styles>
  }

  def writeParaStyle(style: ParaStyle): NodeSeq = {
    val ParaStyle(textAlign, verticalAlign, spacing) = style

    val textAlignStyle: NodeSeq =
      textAlign match {
        case Some(TextAlign.Left)   => <w:jc w:val="start"/>
        case Some(TextAlign.Center) => <w:jc w:val="center"/>
        case Some(TextAlign.Right)  => <w:jc w:val="end"/>
        case Some(TextAlign.Full)   => <w:jc w:val="both"/>
        case None                   => NodeSeq.Empty
      }

    val verticalAlignStyle: NodeSeq =
      verticalAlign match {
        case Some(VerticalAlign.Top)      => <w:textAlignment w:val="top"/>
        case Some(VerticalAlign.Center)   => <w:textAlignment w:val="center"/>
        case Some(VerticalAlign.Bottom)   => <w:textAlignment w:val="bottom"/>
        case Some(VerticalAlign.Baseline) => <w:textAlignment w:val="baseline"/>
        case None                         => NodeSeq.Empty
      }

    val spacingStyle: NodeSeq =
      spacing match {
        case Some(ParaSpacing(before, after)) =>
          <w:spacing w:before={before.dxa.toString} w:after={after.dxa.toString} />

        case None =>
          NodeSeq.Empty
      }

    textAlignStyle ++ spacingStyle
  }

  def writeTableStyle(style: TableStyle) = {
    val tableBordersStyle: NodeSeq =
      <w:tblBorders>
        { if(style.borders.top)    <w:top w:val="single" w:sz="1"/>    else NodeSeq.Empty }
        { if(style.borders.right)  <w:end w:val="single" w:sz="1"/>    else NodeSeq.Empty }
        { if(style.borders.bottom) <w:bottom w:val="single" w:sz="1"/> else NodeSeq.Empty }
        { if(style.borders.left)   <w:start w:val="single" w:sz="1"/>  else NodeSeq.Empty }
        { if(style.cellBorders.top  || style.cellBorders.bottom) <w:insideH w:val="single" w:sz="1"/> else NodeSeq.Empty }
        { if(style.cellBorders.left || style.cellBorders.right)  <w:insideV w:val="single" w:sz="1"/> else NodeSeq.Empty }
      </w:tblBorders>

    val cellMarginStyle: NodeSeq =
      <w:tblCellMar>
        { <w:top w:w={style.cellMargin.top.dxa.toString} w:type="dxa"/> }
        { <w:end w:w={style.cellMargin.right.dxa.toString} w:type="dxa"/> }
        { <w:bottom w:w={style.cellMargin.bottom.dxa.toString} w:type="dxa"/> }
        { <w:start w:w={style.cellMargin.left.dxa.toString} w:type="dxa"/> }
      </w:tblCellMar>

    tableBordersStyle ++ cellMarginStyle
  }

  def writeTextStyle(style: TextStyle): NodeSeq = {
    val TextStyle(size, bold, italic, underline) = style

    val sizeStyle: NodeSeq =
      size match {
        case Some(size) => <w:sz w:val={size.halfPoints.toString}/>
        case None       => NodeSeq.Empty
      }

    val boldStyle: NodeSeq =
      style.bold match {
        case Some(true)  => <w:b/>
        case Some(false) => NodeSeq.Empty
        case None        => NodeSeq.Empty
      }

    val italicStyle: NodeSeq =
      style.italic match {
        case Some(true)  => <w:i/>
        case Some(false) => NodeSeq.Empty
        case None        => NodeSeq.Empty
      }

    val underlineStyle: NodeSeq =
      style.underline match {
        case Some(true)  => <w:u/>
        case Some(false) => NodeSeq.Empty
        case None        => NodeSeq.Empty
      }

    sizeStyle ++ boldStyle ++ italicStyle ++ underlineStyle
  }
}
