package scaliper

trait Benchmark {
  /**
   * Runs the benchmark through {@code reps} iterations.
   *
   * @return any object or null. Benchmark implementors may keep an accumulating
   *      value to prevent the runtime from optimizing away the code under test.
   *      Such an accumulator value can be returned here.
   */
  def run(reps: Int): Object = {
    var i = 0
    var x: Object = null
    while (i < reps) { x = run; i += 1 }
    x
  }

  def setUp(): Unit = { }

  def run(): Object

  def tearDown(): Unit = { }
}
