package scaliper.server

import scaliper.server.db._
import akka.actor._
import akka.io.IO
import spray.can.Http

import scala.concurrent._
import scala.concurrent.duration._


object Main {
  import slick.driver.SQLiteDriver
    
  val DbUrl = "jdbc:sqlite:test.db"
  val Driver = "org.sqlite.JDBC"

  import SQLiteDriver.api._

  val db = Database.forURL(DbUrl, driver=Driver)
  val dal = new DAL(SQLiteDriver)

  if(!new java.io.File("sqlite.db").exists) {
    Await.result(db.run {
      //dal.schema.create
      dal.BenchmarkResults.schema.create
    }, 5 seconds)
  }

  def main(args: Array[String]): Unit = {
    implicit val system = akka.actor.ActorSystem("scaliper-server-system")

    // create and start our service actor
    val service =
      system.actorOf(Props(classOf[ScaliperServiceActor]), "scaliper")

    // start a new HTTP server on port 8050 with our service actor as the handler
    IO(Http) ! Http.Bind(service, "0.0.0.0", 8050)
  }
}
