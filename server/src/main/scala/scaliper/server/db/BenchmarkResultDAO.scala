package scaliper.server.db

import scaliper._
import java.util.Date

case class BenchmarkResultDAO(
  id: Option[Int],
  applicationName: String,
  suiteName: String,
  benchmarkName: String,
  timestamp: Date,
  measurement: Double
)
