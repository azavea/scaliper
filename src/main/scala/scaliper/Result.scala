package scaliper

import java.util.Date

case class Run(results: Seq[(String, BenchmarkResult)], suiteName: String, date: Date)

case class BenchmarkResult(measurementSet: MeasurementSet, eventLog: String)
