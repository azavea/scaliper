package scaliper

import scaliper._

import spray.json._
import spray.json.DefaultJsonProtocol._

/** Contains measurements of duration in nanoseconds */
case class MeasurementSet(measurements: List[Double]) {

  def size = measurements.size

  def median = Statistics.median(measurements)
  def mean = Statistics.mean(measurements)

  def standardDeviation = Statistics.std(measurements)

  def min = measurements.min
  def max = measurements.max

}

object MeasurementSet {
  implicit object MeasurementSetFormat extends RootJsonFormat[MeasurementSet] {
    def write(ms: MeasurementSet): JsValue =
      JsObject("measurements" -> ms.measurements.toJson)

    def read(value: JsValue): MeasurementSet =
      value.asJsObject.getFields("measurements") match {
        case Seq(measurements) => MeasurementSet(measurements.convertTo[List[Double]])
        case _ => throw new DeserializationException(s"Expected measurement JSON, got ${value.compactPrint}")
      }
  }
}
