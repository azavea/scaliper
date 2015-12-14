package scaliper

import java.util.Date
import java.text.{SimpleDateFormat, ParsePosition}

import spray.json._
import spray.json.DefaultJsonProtocol._

case class Run(results: Seq[(String, BenchmarkResult)], suiteName: String, date: Date)

object Run {
  implicit object RunFormat extends RootJsonFormat[Run] {
    def df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ")

    def write(run: Run): JsValue =
      JsObject(
        "results" -> run.results.toJson,
        "suite" -> JsString(run.suiteName),
        "timestamp" -> JsString(df.format(run.date))
      )

    def read(value: JsValue): Run =
      value.asJsObject.getFields("results", "suite", "timestamp") match {
        case Seq(results, JsString(suiteName), JsString(date)) => 
          Run(results.convertTo[Seq[(String, BenchmarkResult)]], suiteName, df.parse(date, new ParsePosition(0)))
        case _ => throw new DeserializationException(s"Expected run JSON, got ${value.compactPrint}")
      }
  }
}

case class NamedRun(name: String, run: Run)

object NamedRun {
  implicit object RunFormat extends RootJsonFormat[NamedRun] {
    def write(namedRun: NamedRun): JsValue =
      JsObject(
        "name" -> JsString(namedRun.name),
        "run" -> namedRun.run.toJson
      )

    def read(value: JsValue): NamedRun =
      value.asJsObject.getFields("name", "run") match {
        case Seq(JsString(name), run) => 
          NamedRun(name, run.convertTo[Run])
        case _ => throw new DeserializationException(s"Expected named run JSON, got ${value.compactPrint}")
      }
  }
}
