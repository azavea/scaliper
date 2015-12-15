# Scaliper

Scaliper is a microbenchmarking tool based on [Google's Caliper](https://github.com/google/caliper). The codebase conatins a partial port from Java to Scala of some of Caliper's measurment code, as well as the method of running benchmarks in a seperate JVM and the warm up routines, based on an early version of Caliper. It contains methods of declaring benchmarks in test suites, so that they can be run as part of the ScalaTest framework.

### Examples

Say you have some function `thisTakesTime(arr: Array[Int]): Unit` that you'd like to benchmark, in a class Foo:

```scala
class Foo(sleepTime: Int) {
  def thisTakesTime(arr: Array[Int]): Unit = {
    val dummy = arr.map(_ + 1).reduce(_ + _)
    Thread.sleep(sleepTime)
  }

  def stop() = { 
    // dummy 
  }
}
```

You want to compare it's run time against another function, `thisTakesLessTime(arr: Array[Int]): Unit`, in another class Bar.

```scala
class Bar(sleepTime: Int) {
  def thisTakesLessTime(arr: Array[Int]) = {
    Thread.sleep(sleepTime)
  }
}
```

You could write your benchmarks like this:

```scala
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

        def run() = {
          foo.thisTakesTime(arr)

          // Always return something; this will help prevent the JVM from optimizing your function body away
          arr
        }

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

        def run() = {
          bar.thisTakesLessTime(arr)
          arr
        }
      }
    }
  }
}
```

Define this in your project's `src/test/scala` folder, so it will be picked up by the test framework.

You can run this benchmark in the examples with:

`> ./sbt "project examples" "test-only scaliper.examples.ReadmeExampleBenchmarks`

To get console output like:

```console
[info] ReadmeExampleBenchmarks:
Running benchmarks for Doing things that take time...
  1 of 2: Taking some time              1022223792.00 ns; σ=1077518487.04474 ns @ 10 trials
  2 of 2: Taking less time              510909828.30 ns; σ=538546245.46452 ns @ 10 trials

  Name                                     s  linear runtime
  Taking some time                      1.02  =============================
  Taking less time                      0.51  ==============
[info] - [scaliper] Doing things that take time
```
