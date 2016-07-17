package anchorman

import anchorman.core._
import anchorman.syntax._

class TextIntegrationSpec extends IntegrationSpec {
  def name = "text"
  def doc = document(
    para(
      bold(" lorem ipsum "),
      italic(" dolor sit "),
      text(" amet ")
    )
  )
}