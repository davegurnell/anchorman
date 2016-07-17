package anchorman

import anchorman.core._
import anchorman.syntax._

class ListIndentIntegrationSpec extends IntegrationSpec {
  def name = "listindent"
  def doc = document(
    exampleTable("full width"),
    ulist(
      item(
        exampleTable("one indent"),
        ulist(
          item(
            exampleTable("two indents"),
            ulist(
              item(
                exampleTable("three indents")
              )
            )
          )
        )
      )
    )
  )

  def exampleTable(text: String): Block =
    table(row(cell(text)))
}
