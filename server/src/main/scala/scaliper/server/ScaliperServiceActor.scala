package scaliper.server

import scaliper._
import scaliper.server.db._
import akka.actor._
import spray.routing.{HttpService, RequestContext}
import spray.routing.directives.CachingDirectives
import spray.http.MediaTypes
import spray.httpx.SprayJsonSupport._
import spray.http.{ AllOrigins, MediaTypes }
import spray.http.{ HttpMethods, HttpMethod, HttpResponse, AllOrigins }
import spray.http.HttpHeaders._
import spray.http.HttpMethods._
import spray.routing._
import spray.json._
import scala.concurrent._

import scala.concurrent.ExecutionContext.Implicits.global

class ScaliperServiceActor extends Actor with HttpService {
  def actorRefFactory = context
  def receive = runRoute(root)

  import Main.dal.profile.api._

  def root =
    path("ping") { complete { "pong\n" } } ~
    path("saverun") { saverunRoute }

  def saverunRoute =
    cors {
      post {
        entity(as[NamedRun]) { namedRun =>
          complete {
            val insert =
              DBIO.seq(
                namedRun.run.results.flatMap { case (benchmarkName, result) =>
                  result.measurementSet.measurements.map { measurement =>
                    Main.dal.BenchmarkResults += BenchmarkResultDAO(None, namedRun.name, namedRun.run.suiteName, benchmarkName, namedRun.run.date, measurement)
                  }
                }
              :_*)
            Main.db.run(insert).map { _ => "ok" }
          }
        }
      }
    }

  val corsHeaders = List(`Access-Control-Allow-Origin`(AllOrigins),
    `Access-Control-Allow-Methods`(GET, POST, OPTIONS, DELETE),
    `Access-Control-Allow-Headers`("Origin, X-Requested-With, Content-Type, Accept, Accept-Encoding, Accept-Language, Host, Referer, User-Agent, Access-Control-Request-Method, Access-Control-Request-Headers"))

  def cors: Directive0 = {
    val rh = implicitly[RejectionHandler]
    respondWithHeaders(corsHeaders) & handleRejections(rh)
  }

}
