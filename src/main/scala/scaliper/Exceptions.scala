package scaliper

object ConfigurationException {
  def apply(s:String):Throwable = ConfigurationException(s)
  def apply(s:String,c:Throwable):Throwable = ConfigurationException(s).initCause(c)
}

class ConfigurationException(s:String) extends RuntimeException(s) 

abstract class UserException(msg:String) extends RuntimeException(msg) {
  def display():Unit
}

abstract class ErrorInUsageException(msg:String) extends UserException(msg) {
  def display():Unit = {
    val message = getMessage
    if(message != null) System.err.println(s"Error: ${message}")
  }
}

object UserException {
  class DoesNotScaleLinearlyException 
    extends ErrorInUsageException("Doing 2x as much work didn't take 2x as much time! "
                                + "Is the JIT optimizing away the body of your benchmark?")

  class RuntimeOutOfRangeException(repTime:Double,lowerBound:Double,upperBound:Double)
    extends ErrorInUsageException(s"Runtime ${repTime} ns/rep out of range [${lowerBound},${upperBound}]")

}
