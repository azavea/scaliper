package scaliper

import org.scalatest._
import scala.collection.mutable

class BenchmarkGroup(val name: String) {
  private val _benchmarks = mutable.ListBuffer[(String, Benchmark)]()
  def benchmarks = _benchmarks

  def add(name: String, benchmark: Benchmark): Unit =
    _benchmarks += ((name, benchmark))
}

private[scaliper] class BenchmarkContext(reporter: Reporter)(registerTest: (String, => Unit) => Unit) {
  private var currentGroup: Option[BenchmarkGroup] = None

  def beginGroup(name: String): Unit = {
    currentGroup = Some(new BenchmarkGroup(name))
  }

  def registerBenchmark(name: String, f: => Benchmark): Unit =
    currentGroup match {
      case Some(group) =>
        group.add(name, f)
      case None =>
        sys.error("You can only register benchmarks within a benchmark group.")
    }

  def endGroup(): Unit =
    currentGroup match {
      case Some(group) =>
        registerTest(s"[scaliper] ${group.name}", { reporter.report(CaliperRunner.run(group)) })
        currentGroup = None
      case None =>
        sys.error("endGroup when currentGroup was None")
    }

}

abstract class Benchmarks extends FunSuite with Reporter {
  def report(run: Run): Unit = { }

  private val context = new BenchmarkContext(this)({ (s, f) => test(s)(f) })

  def benchmark(name: String)(f: => Unit): Unit = {
    context.beginGroup(name)
    f
    context.endGroup()
  }

  def run(name: String)(f: => Benchmark): Unit =
    context.registerBenchmark(name, f)

  // def group(name: String)(f: BenchmarkGroup => Unit) = {
  //   val group = new BenchmarkGroup
  //   f(group)

  //   test(s"Benchmarking Group $name") { report(CaliperRunner.run(name, group)) }
  // }

  // def benchmark(name: String)(f: => Benchmark) = {
  //   test(s"Benchmarking $name") { report(CaliperRunner.run(name, f)) }
  // }
}
