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
  private val (applicationName, endpoint) = {
    val conf = ConfigFactory.load()
    (conf.getString("scaliper.application.name"),
      conf.getString("scaliper.report.endpoint"))
  }

  abstract override def report(run: Run): Unit = {
    EndpointReport.report(NamedRun(applicationName, run), endpoint)
    super.report(run)
  }
}

object EndpointReport {
  def report(r: NamedRun, endpoint: String): Unit = {
    val svc = url(endpoint).setContentType("application/json", "UTF-8") << r.toJson.compactPrint
    try {
      Await.result(Http(svc OK as.String), 1 second)
    } catch {
      case e: Exception => throw new Exception(s"Failed to send run information to $endpoint").initCause(e)
    }
  }
}

