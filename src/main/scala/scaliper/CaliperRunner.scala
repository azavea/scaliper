package scaliper


import scala.collection.mutable
import java.util.Date

object CaliperRunner {
  def run(benchmarkGroup: BenchmarkGroup): Run =
    run(benchmarkGroup.name, benchmarkGroup.benchmarks)

  def run(name: String, benchmarks: Traversable[(String, Benchmark)]): Run = {
    val executedDate = new Date()
    val results = mutable.ListBuffer[(String, BenchmarkResult)]()

    println(s"Running benchmarks for $name...")
    try {
      val size = benchmarks.size
      var i = 0
      for ( (benchmarkName, benchmark) <- benchmarks) {
        beforeMeasurement(i, size, benchmarkName)

        val eventLog = new StringBuilder()
        var consoleDots = 0

        val result =
          try {
            val measurements = SetupRunner.measure(benchmark) { s =>
              print(s".") ; consoleDots += 1; eventLog.append(s+"\n")
            }

            BenchmarkResult(measurements, eventLog.toString)
          } catch {
            case e: Exception =>
              println(eventLog)
              throw e
          }

        afterMeasurement(result, consoleDots)
        results += ((benchmarkName, result))
        i += 1
      }

      return new Run(results.toSeq, name, executedDate)
    } catch {
      case e: Exception => 
        throw new Exception("An exception was thrown from the benchmark code.", e)
    }
  }

  def beforeMeasurement(index: Int, total: Int, benchmarkName: String): Unit = {
    val t =  total.toString
    val i = (index + 1).toString.reverse.padTo(t.length, " ").reverse.mkString
    val max = 35
    val s = f"  $i of $t: $benchmarkName".padTo(max, ' ')
    if(s.size <= max)
      print(s + "   ")
    else
      print(s.take(max) + "   ")
  }

  def afterMeasurement(result: BenchmarkResult, consoleDots: Int) = {
    val info = f"  ${result.measurementSet.mean}%10.2f ns; \u03C3=${result.measurementSet.standardDeviation}%.5f ns @ ${result.measurementSet.size} trials"
    val backSpaces = "\b" * consoleDots
    val spaces = {
      val d = backSpaces.size - info.size
      if(d > 0) { " " * d }
      else { "" }
    }
    println(f"${backSpaces}${info}${spaces}")
  }
}
