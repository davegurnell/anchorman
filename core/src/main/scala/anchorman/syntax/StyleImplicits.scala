package anchorman.syntax

import anchorman.core._

trait StyleImplicits {
  implicit class ParaStyleImplicits(para: Para) {
    def align(align: TextAlign): Para =
      para.copy(style = para.style.copy(textAlign = Option(align)))

    def spacing(spacing: ParaSpacing): Para =
      para.copy(style = para.style.copy(spacing = Option(spacing)))

    def spacing(before: Dim, after: Dim): Para =
      para.copy(
        style = para.style.copy(spacing = Option(ParaSpacing(before, after)))
      )

    def shade(color: Color): Para =
      para.copy(style = para.style.copy(shading = Option(color)))
  }

  implicit class TextStyleImplicits(text: Text) {
    def size(size: Dim): Text =
      text.copy(style = text.style.copy(size = Option(size)))
  }
}
