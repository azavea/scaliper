package scaliper

import scaliper._

import scala.collection.JavaConversions._

/** Contains measurements of duration in nanoseconds */
case class MeasurementSet(measurements: List[Double]) {

  def size = measurements.size

  def median = Statistics.median(measurements)
  def mean = Statistics.mean(measurements)

  def standardDeviation = Statistics.std(measurements)

  def min = measurements.min
  def max = measurements.max

}

