package scaliper

import scaliper._
import scala.math._

trait ConsoleReport extends Benchmarks { 
  abstract override def report(run: Run): Unit = {
    ConsoleReport.run(run)
    super.report(run)
  }
}

case class LinearTranslation(in1: Double, out1: Double, in2: Double, out2: Double) {
  val denom = in1 - in2
  val m = (out1 - out2) / denom
  val b = (in1 * out2 - in2 * out1) / denom

  def translate(in: Double) = m * in + b
}

object ConsoleReport {
  val units = List(
    ("s", 1000000000),
    ("ms", 1000000),
    ("us", 1000)
  )

  private val maxNameWidth = 30
  private val barGraphWidth = 30
  private val unitsForScore100 = 1
  private val unitsForScore10 = 100000000

  val scoreTranslation = LinearTranslation(log(unitsForScore10), 10, log(unitsForScore100), 100)
  
  def run(r: Run): Unit = {
    val measurements = r.results.map(_._2).flatMap(_.measurementSet.measurements)
    val min = measurements.min
    val max = measurements.max
    val printGraphs = measurements.size > 1

    val (unit, div) = findUnits(min)

    println("")
    val h1 = "Name".padTo(maxNameWidth, " ").mkString
    val h2 = unit
    println(f"  $h1%20s  $h2%10s  linear runtime")

    for( (name, BenchmarkResult(measurementSet, _)) <- r.results) {
      val n = {
        val n = name.padTo(maxNameWidth, " ")
        if(n.length > maxNameWidth) n.take(maxNameWidth).mkString
        else n.mkString
      }
      val m = measurementSet.mean / div
      val mx = max / div
      println(f"  $n  $m%10.2f  ${barGraph(m, mx)}")
    }
  }

  def findUnits(min: Double): (String, Int) = {
    units.find { case (_, d) => min / d > 0.1  && min / d < 1000 } match {
      case Some(t) => t
      case None => units.head
    }
  }

  def barGraph(value: Double, max: Double): String = {
    var graphLength = (value / max * barGraphWidth).toInt
    graphLength = math.max(1, graphLength);
    graphLength = math.min(barGraphWidth, graphLength);
    "=" * graphLength;
  }
}
