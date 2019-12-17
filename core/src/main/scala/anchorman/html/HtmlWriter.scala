package anchorman.html

import java.io._

import anchorman.core._
import cats.implicits._
import unindent._

class HtmlWriter[F[_]: MonadThrowable] extends DocumentWriter[F] {
  def write(doc: Document, stream: OutputStream): F[Unit] = {
    val writer = new OutputStreamWriter(stream)
    try {
      writer.write(writeDocument(doc)).pure[F]
    } catch {
      case exn: Throwable =>
        exn.raiseError[F, Unit]
    } finally {
      writer.close()
    }
  }

  private[html] def writeDocument(doc: Document): String = {
    val Document(block, _, _, _, _, _) = doc

    i"""
    <!DOCTYPE html>
    <html>
    <head>
    <style>
      table {
        width: 100%;
        margin: .5em 0;
      }

      th, td {
        border: 1px solid black;
        padding: .5em 1ex;
      }
    </style>
    </head>
    <body>
    ${writeBlock(block)}
    </body>
    </html>
    """
  }

  private[html] def writeBlock(block: Block): String =
    block match {
      case EmptyBlock =>
        ""

      case BlockSeq(blocks) =>
        blocks.map(writeBlock).mkString("\n")

      case Para(span, ParaType.Title, _) =>
        i"""<h1>${writeSpan(span)}</h1>"""

      case Para(span, ParaType.Heading1, _) =>
        i"""<h2>${writeSpan(span)}</h1>"""

      case Para(span, ParaType.Heading2, _) =>
        i"""<h3>${writeSpan(span)}</h2>"""

      case Para(span, ParaType.Heading3, _) =>
        i"""<h4>${writeSpan(span)}</h3>"""

      case Para(span, ParaType.Default, _) =>
        i"""<p>${writeSpan(span)}</p>"""

      case OrderedList(items) =>
        i"""<ol>${items.map(writeListItem).mkString}</ol>"""

      case UnorderedList(items) =>
        i"""<ul>${items.map(writeListItem).mkString}</ul>"""

      case Columns(columns) =>
        writeBlock(Table(List(TableRow(columns.map(TableCell)))))

      case table @ Table(rows, _, _) =>
        i"""
        <table style="width: 100%; border: 1px solid black">
        ${writeColumnWidths(table)}
        ${rows.map(writeTableRow).mkString("\n")}
        </table>
        """
    }

  private[html] def writeListItem(item: ListItem): String =
    i"<li>${writeBlock(item.block)}</li>"

  private[html] def writeColumnWidths(table: Table): String =
    table.columns
      .map {
        case TableColumn.Auto =>
          i"""<col width="*"></col>"""

        case TableColumn.Fixed(length) =>
          i"""<col style="width: ${length}pt"></col>"""
      }
      .mkString("\n")

  private[html] def writeTableRow(row: TableRow): String =
    i"""<tr>${row.cells.map(writeTableCell).mkString}</tr>"""

  private[html] def writeTableCell(cell: TableCell): String =
    i"""<td>${writeBlock(cell.block)}</td>"""

  private[html] def writeSpan(span: Span): String =
    span match {
      case EmptySpan      => ""
      case Text(text, _)  => text
      case Image(url)     => i"""<img src="$url">"""
      case SpanSeq(spans) => spans.map(writeSpan).mkString
    }
}
