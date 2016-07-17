package anchorman.syntax

import anchorman.core._

trait StyleImplicits {
  implicit class ParaStyleImplicits(para: Para) {
    def align(align: TextAlign) =
      para.copy(style = para.style.copy(textAlign = Some(align)))
  }
}