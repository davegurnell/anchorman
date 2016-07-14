package anchorman

import anchorman.core._

class ListsIntegrationSpec extends IntegrationSpec {
  def name = "lists"
  def doc = document(
    para("Unordered Lists"),

    ulist2 { i =>
      block(
        para(s"Unordered item $i"),
        ulist2 { j =>
          block(
            para(s"Unordered item $i.$j"),
            ulist2 { k =>
              para(s"Unordered item $i.$j.$k")
            }
          )
        }
      )
    },

    para("Ordered Lists"),

    olist3 { i =>
      block(
        para(s"Ordered item $i"),
        olist3 { j =>
          block(
            para(s"Ordered item $i.$j"),
            olist3 { k =>
              para(s"Ordered item $i.$j.$k")
            }
          )
        }
      )
    },

    para("Mixed Lists"),

    olist3 { i =>
      block(
        para(s"Ordered item $i"),
        ulist2 { j =>
          block(
            para(s"Ordered item $i.$j"),
            olist3 { k =>
              para(s"Ordered item $i.$j.$k")
            }
          )
        }
      )
    }
  )

  def ulist2(content: Int => Block): Block =
    ulist(
      item(content(1)),
      item(content(2))
    )

  def olist3(content: Int => Block): Block =
    olist(
      item(content(1)),
      item(content(2))
    )
}