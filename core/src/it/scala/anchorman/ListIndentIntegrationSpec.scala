package anchorman

import anchorman.core._
import anchorman.syntax._

class ListIndentIntegrationSpec extends IntegrationSpec {
  def name = "listindent"
  def doc = document(
    para(image("http://placehold.it/900x100")),
    exampleTable("A table"),
    ulist(
      item(
        para(image("http://placehold.it/900x100")),
        exampleTable("A table"),
        ulist(
          item(
            para(image("http://placehold.it/900x100")),
            exampleTable("A table"),
            ulist(
              item(
                para(image("http://placehold.it/900x100")),
                exampleTable("A table")
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
