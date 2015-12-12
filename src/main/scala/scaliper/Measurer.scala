package scaliper

import scaliper._

import scala.language.implicitConversions
import scala.collection.mutable

object Measurer {

  val toleranceFactor = 0.01
  val maxTrials = 10

  def log(message: String) = println(s"[scaliper]  ${message}")

  def prepareForTest() = {
    System.gc()
    System.gc()
  }

  def run(benchmark: Benchmark, warmupTime: Long, runTime: Long): MeasurementSet = {
    val warmupScaled = warmupTime  * 1000000
    val runScaled = runTime * 1000000
    val estimatedRepTime = warmup(benchmark, warmupScaled)

    val lowerBound = 0.1
    val upperBound = 10000000000.0

    if(estimatedRepTime < lowerBound || upperBound < estimatedRepTime) 
      throw new UserException.RuntimeOutOfRangeException(estimatedRepTime, lowerBound, upperBound)

    log("measuring nanos per rep with scale 1.00")
    val m1 = measure(benchmark, 1.00, estimatedRepTime, runScaled)
    log("measuring nanos per rep with scale 0.50")
    val m2 = measure(benchmark, 0.50, m1, runScaled)
    log("measuring nanos per rep with scale 1.50")
    val m3 = measure(benchmark, 1.50, m1, runScaled)

    val measurements = mutable.ListBuffer(m1, m2, m3)
    var i = 3

    while(Statistics.std(measurements) >= Statistics.mean(measurements) * toleranceFactor && i < maxTrials) {
      log("performing additional measurement with scale 1.00")
      measurements += measure(benchmark, 1.00, m1, runTime)
      i += 1
    }
    
    new MeasurementSet(measurements.toList)
  }

  def warmup(benchmark: Benchmark, warmupTime: Long) = {
    log("starting warmup")

    var elapsed = 0L
    var netReps = 0l
    var reps = 1
    var checkedScalesLinearly = false

    while(elapsed < warmupTime) {
      elapsed +=  measureReps(benchmark, reps)
      netReps += reps
      reps *= 2
      
      // if reps overflowed, that's suspicious! Check that it time scales with reps
      if(reps <= 0) {
        if(!checkedScalesLinearly) {
          checkScalesLinearly(benchmark)
          checkedScalesLinearly = true
        }
        reps = Int.MaxValue
      }
    }
    
    log("ending warmup")

    elapsed / netReps
  }

  def checkScalesLinearly(benchmark: Benchmark) = {
    var half = measureReps(benchmark, Int.MaxValue / 2)
    var one = measureReps(benchmark, Int.MaxValue)
    if(half / one > 0.75)
      throw new UserException.DoesNotScaleLinearlyException
  }

  def measureReps(benchmark: Benchmark, reps: Int): Long = {
    prepareForTest()
    log(s"measurement starting. reps = ${reps}")
    val startNanos = System.nanoTime()
    benchmark.run(reps)
    val endNanos = System.nanoTime()
    log(s"measurement ending. ${endNanos - startNanos}")
    return endNanos - startNanos
  }

  def measure(benchmark: Benchmark, durationScale: Double,
                                            estimatedRepTime: Double,
                                            runTime: Double) = {
    var reps = (durationScale * runTime / estimatedRepTime).toInt
    if(reps == 0) reps = 1
    
    log(s"running trial with ${reps} reps")
    val elapsed = measureReps(benchmark, reps)
    val repTime = elapsed / reps.toDouble
    
    repTime
  }
}
