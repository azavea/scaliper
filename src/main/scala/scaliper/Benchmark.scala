package scaliper

trait Benchmark {
  /**
   * Runs the benchmark through {@code reps} iterations.
   *
   * @return any object or null. Benchmark implementors may keep an accumulating
   *      value to prevent the runtime from optimizing away the code under test.
   *      Such an accumulator value can be returned here.
   */
  def run(reps: Int): Unit = {
    var i = 0
    while (i < reps) { run; i += 1 }

  }

  def setUp(): Unit = { }

  def run(): Unit

  def tearDown(): Unit = { }
}
