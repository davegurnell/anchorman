package anchorman.docx

import anchorman.core._
import anchorman.syntax._
import org.scalatest._

import scala.xml.NodeSeq

class DocxDocumentWriterSpec extends FreeSpec with Matchers {
  import XmlImplicits._
  import DocxDocumentWriter._

  val writer = new DocxDocumentWriter(new DocxStyleWriter)
  val seed = DocumentSeed(availableWidth = 1000.dxa)

  "spans" - {
    "write empty span" in {
      val actual = writer.writeSpan(EmptySpan).runA(seed).value
      val expected = NodeSeq.Empty

      actual should ===(expected)
    }

    "write text span" in {
      val actual = writer.writeSpan(Text("Hello world")).runA(seed).value
      val expected = <w:r><w:rPr></w:rPr><w:t>Hello world</w:t></w:r>

      actual should ===(expected)
    }

    "write span seq" in {
      val actual = writer
        .writeSpan(SpanSeq(List(Text("Hello "), Text("world"))))
        .runA(seed)
        .value
      val expected =
        <w:r><w:rPr></w:rPr><w:t>Hello </w:t><w:t xml:space="preserve"> </w:t></w:r> ++
          <w:r><w:rPr></w:rPr><w:t>world</w:t></w:r>

      actual should ===(expected)
    }
  }

  "block" - {
    "write empty block" in {
      val actual = writer.writeBlock(EmptyBlock).runA(seed).value
      val expected = NodeSeq.Empty

      actual should ===(expected)
    }

    "write para" in {
      val actual = writer.writeBlock(para("Hello world")).runA(seed).value
      val expected =
        <w:p>
          <w:pPr>
            <w:pStyle w:val="Normal"/>
          </w:pPr>
          <w:r>
            <w:rPr></w:rPr>
            <w:t>Hello world</w:t>
          </w:r>
        </w:p>

      actual should ===(expected)
    }

    "write ordered list" in {

      val actual: NodeSeq =
        writer.writeBlock(olist(item("Hello"), item("world"))).runA(seed).value

      def expectedPara(content: String): NodeSeq =
        <w:p>
          <w:pPr>
            <w:pStyle w:val="ListParagraph"/>
            <w:numPr>
              <w:ilvl w:val="0"/>
              <w:numId w:val="1"/>
            </w:numPr>
          </w:pPr>
          <w:r>
            <w:rPr></w:rPr>
            <w:t>{content}</w:t>
          </w:r>
        </w:p>

      val expected: NodeSeq =
        expectedPara("Hello") ++
          expectedPara("world")

      actual should ===(expected)
    }

    "write unordered list" in {
      val actual: NodeSeq =
        writer.writeBlock(ulist(item("Hello"), item("world"))).runA(seed).value

      def expectedPara(content: String): NodeSeq =
        <w:p>
          <w:pPr>
            <w:pStyle w:val="ListParagraph"/>
            <w:numPr>
              <w:ilvl w:val="0"/>
              <w:numId w:val="1"/>
            </w:numPr>
          </w:pPr>
          <w:r>
            <w:rPr></w:rPr>
            <w:t>{content}</w:t>
          </w:r>
        </w:p>

      val expected: NodeSeq =
        expectedPara("Hello") ++
          expectedPara("world")

      actual should ===(expected)
    }

    "write table" in {
      val actual: NodeSeq =
        writer
          .writeBlock(
            table(row(cell("A1"), cell("A2")), row(cell("B1"), cell("B2")))
          )
          .runA(seed)
          .value

      def expectedCell(content: String): NodeSeq =
        <w:tc>
          <w:tcPr>
            <w:tcW w:w="500" w:type="dxa"/>
          </w:tcPr>
          <w:p>
            <w:pPr>
              <w:pStyle w:val="Normal"/>
            </w:pPr>
            <w:r>
              <w:rPr>
              </w:rPr>
              <w:t>{content}</w:t>
            </w:r>
          </w:p>
        </w:tc>

      val expected: NodeSeq =
        <w:tbl>
          <w:tblPr>
            <w:tblW w:w="1000" w:type="dxa"/>
            <w:tblInd w:w="120" w:type="dxa"/>
            <w:tblBorders>
              <w:top w:val="single" w:sz="1"/>
              <w:end w:val="single" w:sz="1"/>
              <w:bottom w:val="single" w:sz="1"/>
              <w:start w:val="single" w:sz="1"/>
              <w:insideH w:val="single" w:sz="1"/>
              <w:insideV w:val="single" w:sz="1"/>
            </w:tblBorders>
            <w:tblCellMar>
              <w:top w:w="120" w:type="dxa"/>
              <w:end w:w="120" w:type="dxa"/>
              <w:bottom w:w="0" w:type="dxa"/>
              <w:start w:w="120" w:type="dxa"/>
            </w:tblCellMar>
          </w:tblPr>
          <w:tr>
            {expectedCell("A1")}
            {expectedCell("A2")}
          </w:tr>
          <w:tr>
            {expectedCell("B1")}
            {expectedCell("B2")}
          </w:tr>
        </w:tbl> ++
          <w:p>
          <w:pPr>
          <w:pStyle w:val="Normal"/>
          </w:pPr>
        </w:p>

      actual should ===(expected)
    }
  }
}
