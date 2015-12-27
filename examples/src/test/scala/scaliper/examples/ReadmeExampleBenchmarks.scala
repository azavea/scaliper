package scaliper.examples

import scaliper._

class Foo(sleepTime: Int) {
  def thisTakesTime(arr: Array[Int]): Unit = {
    val dummy = arr.map(_ + 1).reduce(_ + _)
    Thread.sleep(sleepTime)
  }

  def stop() = { 
    // dummy 
  }
}

class Bar(sleepTime: Int) {
  def thisTakesLessTime(arr: Array[Int]) = {
    Thread.sleep(sleepTime)
  }
}

class ReadmeExampleBenchmarks extends Benchmarks with ConsoleReport {
  val arraySize = 1024 * 1024

  benchmark("Doing things that take time") {
    run("Taking some time") {
      new Benchmark {
        var arr: Array[Int] = _
        var foo: Foo = _
        
        // We want to capture closed over values as vals inside this trait
        val size = arraySize 

        override def setUp() = {
          // You want to initialize all data through the setUp function.
          // You do not want to be timing the initial setUp logic in "run",
          // and you do not want to have to serialize the initialized data
          // through to the JVM that will be running the benchmark.
          arr = Array.fill(size)(scala.util.Random.nextInt)
          foo = new Foo(1010)
        }

        def run() = foo.thisTakesTime(arr)

        override def tearDown() = {
          foo.stop()
        }
      }
    }

    run("Taking less time") {
      new Benchmark {
        var arr: Array[Int] = _
        var bar: Bar = _
        
        val size = arraySize 

        override def setUp() = {
          arr = Array.fill(size)(scala.util.Random.nextInt)
          bar = new Bar(510)
        }

        def run() = bar.thisTakesLessTime(arr)
      }
    }
  }
}
