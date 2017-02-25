package anchorman.core

import anchorman.syntax._

case class Dim(points: Double) {
  def + (that: Dim) = Dim(this.points + that.points)
  def - (that: Dim) = Dim(this.points - that.points)
  def * (that: Double) = Dim(this.points * that)
  def / (that: Double) = Dim(this.points / that)
  def > (that: Dim) = this.points > that.points

  def unary_- = Dim(-this.points)

  def min(that: Dim) = if(this.points < that.points) this else that
  def max(that: Dim) = if(this.points < that.points) that else this

  def inches     = (points /    72)
  def dxa        = (points *    20).toInt
  def emu        = (points * 12700).toInt
  def halfPoints = (points *     2).toInt

  def by(that: Dim) = Dims(this, that)
}

object Dim {
  val zero = Dim(0.0)
}

case class Dims(width: Dim, height: Dim)

object Dims {
  val defaultPageSize = 11906.dxa by 16838.dxa
}

case class Insets(
  top    : Dim = Dim.zero,
  right  : Dim = Dim.zero,
  bottom : Dim = Dim.zero,
  left   : Dim = Dim.zero
)

object Insets {
  val empty = Insets()

  val defaultPageMargins = Insets(
    top    = 1440.dxa,
    right  = 1797.dxa,
    bottom = 1440.dxa,
    left   = 1797.dxa
  )
}
