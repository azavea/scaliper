package scaliper

import scala.math._
import scala.language.implicitConversions

object Statistics {
  def median(values: Seq[Double]): Double = {
    val sorted = values.sorted
    val len = values.length
    if(len % 2 == 0 )
      (sorted(len/2 - 1) + sorted(len/2)) / 2
    else
      sorted(len / 2)
  }
  
  def mean(values: Seq[Double]) = {
    values.sum / values.length
  }

  def std(values: Seq[Double]) = {
    val m = mean(values)
    val sumOfSquares = values.map { x => val d = x - m ; m*m }.sum
    sqrt(sumOfSquares/(values.length - 1))
  }

}
