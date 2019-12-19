package anchorman.core

import anchorman.syntax._
import org.scalatest._
import org.scalatest.freespec._
import org.scalatest.matchers.should._

class AstSpec extends AnyFreeSpec with Matchers {
  "table" - {
    val t = table(row(cell("A1")), row(cell("A2"), cell("B2"), cell("C2")))

    "num rows and cols" in {
      t.numRows should equal(2)
      t.numCols should equal(3)
    }

    "columns" in {
      import TableColumn._
      t.manualColumns should equal(None)
      t.defaultColumns should equal(Seq(Auto, Auto, Auto))
      t.columns should equal(Seq(Auto, Auto, Auto))
    }

//    "cell widths" in {
//      t.cellWidths(900.pt) should equal(Seq(300.pt, 300.pt, 300.pt))
//    }
  }

}
