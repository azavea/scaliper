package scaliper

object InProcessRunner {
  val warmupTime: Long = 3000
  val runTime: Long = 1000

  def main(args:Array[String]) = {
    try {
      val p = args(args.length - 1)
      val benchmark = KryoSerializer.deserializeFromFile[Benchmark](p)
      benchmark.setUp()

      val results: MeasurementSet =
        try {
          Measurer.run(benchmark, warmupTime, runTime)
        } catch {
          case e: Exception =>
            System.err.println(s"                         THROWN IN SCALIPER $e")
            throw e
        } finally {
          benchmark.tearDown()
        }

      val f = java.io.File.createTempFile("pre", "post")
      val resultsPath = f.getPath

      IO.write(resultsPath, JsonConversion.getOutput(results))

      println(InterProc.outputMarker+resultsPath)
      System.exit(0)
    } catch {
      case e:UserException =>
        e.display() // Would want to communicate this back to the host process
      System.exit(1)
    }
  }
}
