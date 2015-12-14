package scaliper

import spray.json._

case class BenchmarkResult(measurementSet: MeasurementSet, eventLog: String)

object BenchmarkResult {
  implicit object BenchmarkResultFormat extends RootJsonFormat[BenchmarkResult] {
    def write(br: BenchmarkResult): JsValue =
      JsObject(
        "measurementSet" -> br.measurementSet.toJson,
        "eventLog" -> JsString(br.eventLog)
      )

    def read(value: JsValue): BenchmarkResult =
      value.asJsObject.getFields("measurementSet", "eventLog") match {
        case Seq(measurementSet, JsString(eventLog)) => 
          BenchmarkResult(measurementSet.convertTo[MeasurementSet], eventLog)
        case _ => throw new DeserializationException(s"Expected benchmark result JSON, got ${value.compactPrint}")
      }
  }
}
