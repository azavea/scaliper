import scala.util.Properties

object Version {
  val scaliper = "0.5.0" + Properties.envOrElse("SCALIPER_VERSION_SUFFIX", "-SNAPSHOT")
  val scala = "2.10.6"
  val sprayJson = "1.3.2"
  val spray = "1.3.3"
  val akka = "2.3.9"
}
