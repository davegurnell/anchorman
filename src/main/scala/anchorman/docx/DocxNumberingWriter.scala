package anchorman.docx

import anchorman.core._
import anchorman.media._
import cats.data.State
import cats._
import cats.std.all._
import cats.syntax.all._

import scala.xml.NodeSeq

class DocxNumberingWriter {
  val abstractNumWriter = new DocxAbstractNumWriter
  val numWriter         = new DocxNumWriter

  def writeNumberingXml(doc: Document): NodeSeq =
    <w:numbering mc:Ignorable="w14 wp14"
             xmlns:m="http://schemas.openxmlformats.org/officeDocument/2006/math"
             xmlns:mc="http://schemas.openxmlformats.org/markup-compatibility/2006"
             xmlns:mo="http://schemas.microsoft.com/office/mac/office/2008/main"
             xmlns:mv="urn:schemas-microsoft-com:mac:vml"
             xmlns:o="urn:schemas-microsoft-com:office:office"
             xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships"
             xmlns:v="urn:schemas-microsoft-com:vml"
             xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main"
             xmlns:w10="urn:schemas-microsoft-com:office:word"
             xmlns:w14="http://schemas.microsoft.com/office/word/2010/wordml"
             xmlns:wne="http://schemas.microsoft.com/office/word/2006/wordml"
             xmlns:wp="http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing"
             xmlns:wp14="http://schemas.microsoft.com/office/word/2010/wordprocessingDrawing"
             xmlns:wpc="http://schemas.microsoft.com/office/word/2010/wordprocessingCanvas"
             xmlns:wpg="http://schemas.microsoft.com/office/word/2010/wordprocessingGroup"
             xmlns:wpi="http://schemas.microsoft.com/office/word/2010/wordprocessingInk"
             xmlns:wps="http://schemas.microsoft.com/office/word/2010/wordprocessingShape">
      {abstractNumWriter.writeUnorderedAbstractNum(doc.block)}
      {abstractNumWriter.writeOrderedAbstractNum(doc.block)}
      {numWriter.writeNums(doc.block)}
    </w:numbering>
}

class DocxAbstractNumWriter {
  def writeUnorderedAbstractNum(block: Block): NodeSeq = {
    // val ulistLevel = maxLevel(block)(_.isInstanceOf[UnorderedList])
    <w:abstractNum w:abstractNumId="0">
      <w:multiLevelType w:val="hybridMultilevel"/>
      {
        (0 to 3).flatMap { level =>
          val bullet = (level % 3) match {
            case 0 => ""
            case 1 => "o"
            case 2 => ""
          }

          val indent  = DocxDocumentWriter.listIndent * (level + 1)
          val hanging = DocxDocumentWriter.listHangingIndent

          val font = (level % 3) match {
            case 0 => "Symbol"
            case 1 => "Courier New"
            case 2 => "Wingdings"
          }

          <w:lvl w:ilvl={level.toString}>
            <w:start w:val="1"/>
            <w:numFmt w:val="bullet"/>
            <w:lvlText w:val={bullet}/>
            <w:lvlJc w:val="left"/>
            <w:pPr>
              <w:ind w:hanging={hanging.dxa.toString} w:left={indent.dxa.toString}/>
            </w:pPr>
            <w:rPr>
              <w:rFonts w:ascii={font} w:hAnsi={font} w:hint="default"/>
            </w:rPr>
          </w:lvl>
        }
      }
    </w:abstractNum>
  }

  def writeOrderedAbstractNum(block: Block): NodeSeq = {
    // val olistLevel = maxLevel(block)(_.isInstanceOf[OrderedList])
    <w:abstractNum w:abstractNumId="1">
      <w:multiLevelType w:val="hybridMultilevel"/>
      {
        (0 to 3).flatMap { level =>
          val format = (level % 3) match {
            case 0 => "decimal"
            case 1 => "lowerLetter"
            case 2 => "lowerRoman"
          }

          val hanging = 360.dxa
          val indent  = (hanging * 2) * (level + 1)

          <w:lvl w:ilvl={level.toString}>
            <w:start w:val="1"/>
            <w:numFmt w:val={format}/>
            <w:lvlText w:val={s"%${level + 1}."}/>
            <w:lvlJc w:val="left"/>
            <w:pPr>
              <w:ind w:hanging={hanging.dxa.toString} w:left={indent.dxa.toString}/>
            </w:pPr>
          </w:lvl>
        }
      }
    </w:abstractNum>
  }
}

class DocxNumWriter {
  case class NumSeed(
    currLevel: Int = 0,
    nextNumId: Int = 1
  )

  type NumState[A] = State[NumSeed, A]

  def writeNums(block: Block): NodeSeq =
    writeNumsForBlock(block).runA(NumSeed()).value

  def writeNumsForBlock(block: Block): NumState[NodeSeq] =
    block match {
      case EmptyBlock  => emptyXml
      case _: Para     => emptyXml

      case UnorderedList(items) =>
        for {
          head <- pushAndWriteNum(0)
          tail <- writeNumsForBlocks(items.map(_.block))
          _    <- popNum
        } yield head ++ tail

      case OrderedList(items) =>
        for {
          head <- pushAndWriteNum(1)
          tail <- writeNumsForBlocks(items.map(_.block))
          _    <- popNum
        } yield head ++ tail

      case Columns(blocks)  => writeNumsForBlocks(blocks)
      case table: Table     => writeNumsForBlocks(table.cells.map(_.block))
      case Image(url)       => emptyXml
      case BlockSeq(blocks) => writeNumsForBlocks(blocks)
    }

  def writeNumsForBlocks(blocks: Seq[Block]): NumState[NodeSeq] =
    blocks.toList.map(writeNumsForBlock).sequenceU.map(_.flatten)

  def pushAndWriteNum(abstractListId: Int): NumState[NodeSeq] =
    State { seed =>
      val NumSeed(currLevel, nextNumId) = seed

      val xml: NodeSeq =
        <w:num w:numId={seed.nextNumId.toString}>
          <w:abstractNumId w:val={abstractListId.toString}/>
          <w:lvlOverride w:ilvl={currLevel.toString}>
            <w:startOverride w:val="1"/>
          </w:lvlOverride>
        </w:num>

      val nextSeed: NumSeed =
        seed.copy(currLevel = currLevel + 1, nextNumId = nextNumId + 1)

      (nextSeed, xml)
    }

  val popNum: NumState[Unit] =
    State.modify(seed => seed.copy(currLevel = seed.currLevel - 1))

  val emptyXml: NumState[NodeSeq] =
    State.pure(NodeSeq.Empty)
}
