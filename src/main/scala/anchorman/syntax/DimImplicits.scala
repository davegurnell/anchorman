package anchorman.syntax

import anchorman.core._

trait DimImplicits {
  implicit class IntOps(value: Int) {
    def dxa : Dim = Dim(value / 20.0)
    def pt  : Dim = Dim(value)
    def in  : Dim = Dim(value * 72.0)
  }

  implicit class DoubleOps(value: Double) {
    def dxa : Dim = Dim(value / 20.0)
    def pt  : Dim = Dim(value)
    def in  : Dim = Dim(value * 72.0)
  }
}