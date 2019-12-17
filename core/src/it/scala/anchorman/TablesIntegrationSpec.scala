package anchorman

import anchorman.core._
import anchorman.syntax._

class TablesIntegrationSpec extends IntegrationSpec {
  def name = "tables"
  def doc = document(
    twoByTwo { (ox, oy) =>
      twoByTwo { (ix, iy) =>
        para(s"outer $ox,$oy inner $ix,$iy")
      }
    }
  )

  def twoByTwo(oneByOne: (Int, Int) => Block): Block =
    table(
      row(cell(oneByOne(0, 0)), cell(oneByOne(0, 1))),
      row(cell(oneByOne(1, 0)), cell(oneByOne(1, 1)))
    )
}
