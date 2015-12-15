package scaliper

import scaliper._
import spray.json._
import dispatch._
import dispatch.Defaults._
import scala.concurrent._
import scala.concurrent.duration._

import com.typesafe.config.ConfigFactory

/** Sends a JSON representation of the run to an endpoint */
trait EndpointReport extends Benchmarks {
  private val (applicationName, endpoint, failIfError) = {
    val conf = ConfigFactory.load()
    (conf.getString("scaliper.application.name"),
      conf.getString("scaliper.report.endpoint.url"),
      conf.getBoolean("scaliper.report.endpoint.failIfError"))
  }

  abstract override def report(run: Run): Unit = {
    EndpointReport.report(NamedRun(applicationName, run), endpoint, failIfError)
    super.report(run)
  }
}

object EndpointReport {
  def report(r: NamedRun, endpoint: String, failIfError: Boolean): Unit = {
    val svc = url(endpoint).setContentType("application/json", "UTF-8") << r.toJson.compactPrint
    try {
      Await.result(Http(svc OK as.String), 1 second)
    } catch {
      case e: Exception =>
        if(failIfError) throw new Exception(s"Failed to send run information to $endpoint").initCause(e)
        else { println(s"Reporting to endpoint $endpoint failed: $e") }
    }
  }
}

