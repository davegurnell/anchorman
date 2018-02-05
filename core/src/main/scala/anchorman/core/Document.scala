package anchorman.core

case class Document(
  block         : Block,
  pageStyle     : PageStyle        = PageStyle.default,
  defaultStyle  : ParaAndTextStyle = ParaAndTextStyle.default,
  heading1Style : ParaAndTextStyle = ParaAndTextStyle.heading1,
  heading2Style : ParaAndTextStyle = ParaAndTextStyle.heading2,
  heading3Style : ParaAndTextStyle = ParaAndTextStyle.heading3
)
