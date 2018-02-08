package anchorman.docx

import anchorman.core._
import anchorman.syntax._
import anchorman.media._
import cats._
import cats.data.State
import cats.instances.all._
import cats.syntax.all._

import scala.xml.NodeSeq

class DocxDocumentWriter(val styleWriter: DocxStyleWriter) {
  import DocxDocumentWriter._

  def writeDocumentXml(doc: Document, media: List[MediaFile]): NodeSeq = {
    val Document(block, pageStyle, _, _, _, _) = doc

    val seed = DocumentSeed(
      availableWidth = pageStyle.availableWidth,
      media          = media.map(file => file.url -> file).toMap
    )

    val state = for {
      content   <- writeBlock(block)
      pageStyle <- writePageStyle(pageStyle)
    } yield {
      <w:document xmlns:m="http://schemas.openxmlformats.org/officeDocument/2006/math"
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
                  xmlns:wps="http://schemas.microsoft.com/office/word/2010/wordprocessingShape"
                  xmlns:a="http://schemas.openxmlformats.org/drawingml/2006/main"
                  xmlns:pic="http://schemas.openxmlformats.org/drawingml/2006/picture">
        <w:body>
          {content}
          <w:sectPr>{pageStyle}</w:sectPr>
        </w:body>
      </w:document>
    }

    state.runA(seed).value
  }

  def writeBlock(block: Block): DocumentState[NodeSeq] =
    block match {
      case EmptyBlock =>
        State.pure(NodeSeq.Empty)

      case Para(span, tpe, style) =>
        writePara(span, tpe, style)

      case OrderedList(items) =>
        writeList(items)

      case UnorderedList(items) =>
        writeList(items)

      case Columns(blocks) =>
        writeColumns(blocks)

      case table: Table =>
        for {
          a <- writeTable(table)
          b <- writeBlock(Para.empty)
        } yield a ++ b

      case BlockSeq(blocks) =>
        blocks.foldLeft(emptyXml) { (accum, block) =>
          for {
            a <- accum
            b <- writeBlock(block)
          } yield a ++ b
        }
    }

  def writePara(span: Span, tpe: ParaType, style: ParaStyle): DocumentState[NodeSeq] =
    for {
      numbering <- getNumbering
      content   <- writeSpan(span)
    } yield {
      val styleName: String =
        tpe match {
          case ParaType.Title    => "Title"
          case ParaType.Heading1 => "Heading1"
          case ParaType.Heading2 => "Heading2"
          case ParaType.Heading3 => "Heading3"
          case ParaType.Default  if numbering.nonEmpty => "ListParagraph"
          case ParaType.Default  => "Normal"
        }

      <w:p>
        <w:pPr>
          <w:pStyle w:val={styleName}/>
          { styleWriter.writeParaStyle(style) }
          {
            numbering match {
              case Some(Numbering(ilvl, numId)) =>
                <w:numPr>
                  <w:ilvl w:val={ilvl.toString}/>
                  <w:numId w:val={numId.toString}/>
                </w:numPr>
              case None =>
                NodeSeq.Empty
            }
          }
        </w:pPr>
        {content}
      </w:p>
    }

  def writeList(items: List[ListItem]): DocumentState[NodeSeq] =
    for {
      _   <- pushList
      _   <- indent(listHangingIndent)
      ans <- items.foldLeft(emptyXml) { (accum, item) =>
               for {
                 a <- accum
                 b <- writeBlock(item.block)
               } yield a ++ b
             }
      _   <- indent(-listHangingIndent)
      _   <- popList
    } yield ans

  def writeColumns(columns: List[Block]): DocumentState[NodeSeq] =
    writeTable(Table(List(TableRow(columns.map(TableCell.apply)))))

  def writeTable(table: Table): DocumentState[NodeSeq] = {
    val Table(rows, _, style) = table
    val columns = table.columns

    for {
      width      <- getAvailableWidth
      left       <- getLeftIndent
      cellWidths <- getCellWidths(table, width)
      rowsXml    <- rows.toList.traverse(row => writeTableRow(row, cellWidths, style)).map(_.flatten)
    } yield {
      <w:tbl>
        <w:tblPr>
          <w:tblW w:w={width.dxa.toString} w:type="dxa"/>
          <w:tblInd w:w={(left + table.style.cellMargin.left).dxa.toString} w:type="dxa"/>
          {styleWriter.writeTableStyle(style)}
        </w:tblPr>
        {rowsXml}
      </w:tbl>
    }
  }

  def writeTableRow(row: TableRow, cellWidths: Seq[Dim], tableStyle: TableStyle): DocumentState[NodeSeq] =
    writeTableCells(row.cells, cellWidths, tableStyle).map(cell => <w:tr>{ cell }</w:tr>)


  def writeTableCells(cells: Seq[TableCell], cellWidths: Seq[Dim], tableStyle: TableStyle): DocumentState[NodeSeq] =
    (cells zip cellWidths).foldLeft(emptyXml) { (accum, pair) =>
      val (cell, cellWidth) = pair

      for {
        a <- accum
        w <- getAvailableWidth
        l <- getLeftIndent
        r <- getRightIndent
        _ <- reindent(cellWidth - tableStyle.margin.left - tableStyle.margin.right - tableStyle.cellMargin.left - tableStyle.cellMargin.right, Dim.zero, Dim.zero)
        b <- writeBlockWithTrailingPara(cell.block)
        _ <- reindent(w, l, r)
      } yield {
        a ++
        <w:tc>
          <w:tcPr>
            <w:tcW w:w={cellWidth.dxa.toString} w:type="dxa"/>
          </w:tcPr>
          {b}
        </w:tc>
      }
    }

  def writeSpan(span: Span): DocumentState[NodeSeq] =
    span match {
      case EmptySpan =>
        emptyXml

      case Text(text, style) =>
        writeText(text, style)

      case image: Image =>
        writeImage(image.url)

      case SpanSeq(spans) =>
        spans.foldLeft(emptyXml) { (accum, span) =>
          for {
            a <- accum
            b <- writeSpan(span)
          } yield a ++ b
        }
    }

  val leadingSpaceRegex = "^\\s+\\S".r
  val trailingSpaceRegex = "\\s$".r

  val preserveSpace: NodeSeq =
    <w:t xml:space="preserve"> </w:t>

  def writeText(text: String, style: TextStyle): DocumentState[NodeSeq] =
    State.pure {
      val leadingSpace:  Boolean = leadingSpaceRegex.pattern.matcher(text).find
      val trailingSpace: Boolean = trailingSpaceRegex.pattern.matcher(text).find

      val prologue: NodeSeq = if(leadingSpace && !trailingSpace) preserveSpace else NodeSeq.Empty
      val epilogue: NodeSeq = if(trailingSpace) preserveSpace else NodeSeq.Empty

      <w:r>
        <w:rPr>{styleWriter.writeTextStyle(style)}</w:rPr>
        {prologue}
        <w:t>{text}</w:t>
        {epilogue}
      </w:r>
    }

  def writeImage(url: String): DocumentState[NodeSeq] =
    getMediaFile(url) flatMap {
      case Some(ImageMediaFile(url, relId, filename, contentType, pixelWidth, pixelHeight, content)) =>
        for {
          index <- getMediaIndex
          width <- getAvailableWidth.map(_ min (1.in * pixelWidth / 150))
          height = width * pixelHeight / pixelWidth
        } yield {
          <w:r>
            <w:drawing>
              <wp:inline>
                <wp:extent cx={width.emu.toString} cy={height.emu.toString}/> <!-- TODO: Fix up -->
                <wp:docPr id="1" name={filename}/> <!-- TODO: Fix up -->
                <a:graphic>
                  <a:graphicData uri={"http://schemas.openxmlformats.org/drawingml/2006/picture"}>
                    <pic:pic>
                      <pic:nvPicPr>
                        <pic:cNvPr id={index.toString} name={filename} />
                        <pic:cNvPicPr/>
                      </pic:nvPicPr>
                      <pic:blipFill>
                        <a:blip r:embed={relId}>
                        </a:blip>
                        <a:stretch>
                          <a:fillRect/>
                        </a:stretch>
                      </pic:blipFill>
                      <pic:spPr>
                        <a:xfrm>
                          <a:off x="0" y="0"/>
                          <a:ext cx={width.emu.toString} cy={height.emu.toString}/>
                        </a:xfrm>
                        <a:prstGeom prst="rect">
                          <a:avLst/>
                        </a:prstGeom>
                      </pic:spPr>
                    </pic:pic>
                  </a:graphicData>
                </a:graphic>
              </wp:inline>
            </w:drawing>
          </w:r> : NodeSeq
        }

      case Some(PlainMediaFile(url, relId, filename, contentType, content)) =>
        State.pure(<w:r><w:t></w:t></w:r> : NodeSeq)

      case None =>
        State.pure(<w:r><w:t></w:t></w:r> : NodeSeq)
    }

  def writeBlockWithTrailingPara(block: Block): DocumentState[NodeSeq] =
    if (blockEndsWithPara(block)) {
      writeBlock(block)
    } else {
      writeBlock(block) // ++ writeBlock(Para(EmptySpan)) // Looks like we don't need this.
    }

  def blockEndsWithPara(block: Block): Boolean =
    block match {
      case EmptyBlock       => false
      case BlockSeq(blocks) => blocks.lastOption.exists(blockEndsWithPara)
      case _: Para          => true
      case _: UnorderedList => false
      case _: OrderedList   => false
      case _: Columns       => false
      case _: Table         => false
    }

  def writePageStyle(pageStyle: PageStyle): DocumentState[NodeSeq] =
    State.pure {
      <w:pgSz w:w={pageStyle.size.width.dxa.toString}
              w:h={pageStyle.size.height.dxa.toString}/> ++
      <w:pgMar w:top={pageStyle.margin.top.dxa.toString}
               w:right={pageStyle.margin.right.dxa.toString}
               w:bottom={pageStyle.margin.bottom.dxa.toString}
               w:left={pageStyle.margin.left.dxa.toString}/>
    }
}

object DocxDocumentWriter {
  val listIndent        = 360.dxa
  val listHangingIndent = 360.dxa

  case class DocumentSeed(
    availableWidth: Dim = Dims.defaultPageSize.width,
    leftIndent: Dim = Dim.zero,
    rightIndent: Dim = Dim.zero,
    media: Map[String, MediaFile] = Map.empty,
    nextMediaId: Int = 0,
    currListIds: List[Int] = Nil,
    nextListId: Int = 1
  )

  type DocumentState[A] = State[DocumentSeed, A]

  val emptyXml: DocumentState[NodeSeq] =
    State.pure(NodeSeq.Empty)

  val getAvailableWidth: DocumentState[Dim] =
    State.inspect(_.availableWidth)

  val getLeftIndent: DocumentState[Dim] =
    State.inspect(_.leftIndent)

  val getRightIndent: DocumentState[Dim] =
    State.inspect(_.rightIndent)

  def indent(left: Dim = Dim.zero, right: Dim = Dim.zero): DocumentState[Unit] =
    State.modify { state =>
      state.copy(
        availableWidth = state.availableWidth - left - right,
        leftIndent     = state.leftIndent     + left,
        rightIndent    = state.rightIndent    + right
      )
    }

  def reindent(width: Dim, left: Dim = Dim.zero, right: Dim = Dim.zero): DocumentState[Unit] =
    State.modify { state =>
      state.copy(
        availableWidth = width,
        leftIndent     = left,
        rightIndent    = right
      )
    }

  def getCellWidths(table: Table, availableWidth: Dim): DocumentState[Seq[Dim]] =
    State.pure {
      val numAuto = table.columns.count {
        case _: TableColumn.Fixed => false
        case TableColumn.Auto     => true
      }

      val remainingWidth =
        availableWidth -
        table.style.margin.left -
        table.style.margin.right -
        table.columns.collect {
          case TableColumn.Fixed(width) => width
        }.foldLeft(Dim.zero)(_ + _)

      table.columns map {
        case TableColumn.Auto       => remainingWidth / numAuto
        case TableColumn.Fixed(len) => len
      }
    }

  val pushList: DocumentState[Unit] =
    State.modify { state =>
      state.copy(
        currListIds    = state.nextListId :: state.currListIds,
        nextListId     = state.nextListId + 1
      )
    }

  val popList: DocumentState[Unit] =
    State.modify { state =>
      state.copy(
        currListIds = state.currListIds.tail
      )
    }

  case class Numbering(ilvl: Int, numId: Int)

  val getNumbering: DocumentState[Option[Numbering]] =
    State.inspect { state =>
      if(state.currListIds.isEmpty) {
        None
      } else {
        Some(Numbering(state.currListIds.length - 1, state.currListIds.head))
      }
    }

  val getMediaIndex: DocumentState[Int] =
    State.apply(seed => (seed.copy(nextMediaId = seed.nextMediaId + 1), seed.nextMediaId))

  def getMediaFile(url: String): DocumentState[Option[MediaFile]] =
    State.inspect(_.media.get(url))
}
