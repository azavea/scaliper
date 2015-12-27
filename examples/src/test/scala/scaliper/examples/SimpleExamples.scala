package scaliper

trait IntsArraySetup { self: Benchmark =>
  def size: Int

  var ints: Array[Int] = null
  override def setUp() = {
    ints = Array.ofDim[Int](size)
  }
}

trait DoublesArraySetup { self: Benchmark =>
  def size: Int

  var doubles: Array[Double] = null
  override def setUp() = {
    doubles = Array.ofDim[Double](size)
  }
}

class SimpleBenchmarks extends Benchmarks with ConsoleReport {
  val size = 100000
  benchmark("Foreach Benchmarks") {
    run("Int Foreach") {
      new Benchmark with IntsArraySetup {
        val size = SimpleBenchmarks.this.size

        def run() = {
          ints.foreach { i =>
            ints(0) = i
          }
//          ints
        }
      }
    }

    run("Double Foreach") {
      new Benchmark with DoublesArraySetup {
        val size = SimpleBenchmarks.this.size

        def run() = {
          var s = 0.0
          doubles.foreach { d =>
            doubles(0) = d
          }
          doubles
        }
      }

    }
  }

  benchmark("While Loops") {
    run("Int While") {
      new Benchmark with IntsArraySetup {
        val size = SimpleBenchmarks.this.size

        def run() = {
          var s = 0
          var i = 0
          val len = ints.length
          while(i < len) {
            s += ints(i)
            ints(i) = math.sqrt(s * ints(i).toDouble).toInt
            i += 1
          }
          ints
        }
      }
    }

    run("Double While") {
      new Benchmark with DoublesArraySetup {
        val size = SimpleBenchmarks.this.size

        def run() = {
          var s = 0.0
          var i = 0
          val len = doubles.length
          while(i < len) {
            s += doubles(i)
            doubles(i) = math.sqrt(s * doubles(i)).toInt.toDouble
            i += 1
          }
          doubles
        }
      }
    }
  }
}
