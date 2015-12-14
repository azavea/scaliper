package scaliper.server.db

import slick.driver.JdbcProfile
import java.util.Date
import java.sql.Timestamp

/** Allows dynamic selection of database type.
  */
trait Profile {
  val profile: JdbcProfile
}

/** Base implementation of the data access layer.
  */
class DAL(override val profile: JdbcProfile)
  extends Profile
  with BenchmarkResultsComponent

trait BenchmarkResultsComponent { self: Profile =>

  import profile.api._

  implicit def date2SqlTimestampMapper: BaseColumnType[Date] = 
    MappedColumnType.base[Date, Timestamp](
      { date => new Timestamp(date.getTime) },
      { ts => new Date(ts.getTime) }
    )

  class BenchmarkResultsTable(tag: Tag) extends Table[BenchmarkResultDAO](tag, "benchmark_results") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def applicationName = column[String]("application_name")
    def suiteName  = column[String]("suite_name")
    def benchmarkName  = column[String]("benchmark_name")
    def timestamp    = column[Date]("timstamp")
    def measurement = column[Double]("measurement")

    def * = (id.?, applicationName, suiteName, benchmarkName, timestamp, measurement) <>
            (BenchmarkResultDAO.tupled, BenchmarkResultDAO.unapply)
  }

  val BenchmarkResults = TableQuery[BenchmarkResultsTable]
}
