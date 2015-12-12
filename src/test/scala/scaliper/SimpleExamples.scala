package scaliper

object SimpleBenchmarks {
  val intWhile = 
    new Benchmark {
      val name = "Int While"
      var arr: Array[Int] = null
      override def setUp() = {
        arr = Array.fill[Int](10000)(100)
      }

      def run() = {
        var s = 0
        var i = 0
        val len = arr.length
        while(i < len) {
          s += arr(i)
          arr(i) = s
          i += 1
        }
        arr
      }
    }

  val doubleWhile = 
    new Benchmark {
      val name = "Double While"
      var arr: Array[Double] = null
      override def setUp() = {
        arr = Array.fill[Double](10000)(100.0)
      }

      def run() = {
        var s = 0.0
        var i = 0
        val len = arr.length
        while(i < len) {
          s += arr(i)
          arr(i) = s
          i += 1
        }
        arr
      }
    }
}

class SimpleBenchmarks extends Benchmarks with ConsoleReport {
  benchmark("Foreach Benchmarks") {
    run("Int Foreach") {
      new Benchmark {
        var ints: Array[Int] = null
        override def setUp() = {
          ints = Array.ofDim[Int](10000)
        }

        def run() = {
          ints.foreach { i =>
            ints(0) = i
          }
          ints
        }
      }
    }

    run("Double Foreach") {
      new Benchmark {
        var doubles: Array[Double] = null
        override def setUp() = {
          doubles = Array.ofDim[Double](10000)
        }

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
      new Benchmark {
        var arr: Array[Int] = null
        override def setUp() = {
          arr = Array.fill[Int](10000)(100)
        }

        def run() = {
          var s = 0
          var i = 0
          val len = arr.length
          while(i < len) {
            s += arr(i)
            arr(i) = s
            i += 1
          }
          arr
        }
      }
    }

    run("Double While") {
      new Benchmark {
        var arr: Array[Double] = null
        override def setUp() = {
          arr = Array.fill[Double](10000)(100.0)
        }

        def run() = {
          var s = 0.0
          var i = 0
          val len = arr.length
          while(i < len) {
            s += arr(i)
            arr(i) = s
            i += 1
          }
          arr
        }
      }
    }
  }
}
