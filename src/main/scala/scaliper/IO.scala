package scaliper

import scala.reflect.ClassTag

import spray.json._
import spray.json.DefaultJsonProtocol._

object IO {
  def write(path: String, txt: String): Unit = {
    import java.nio.file.{Paths, Files}
    import java.nio.charset.StandardCharsets
    
    Files.write(Paths.get(path), txt.getBytes(StandardCharsets.UTF_8))
  }
  
  def read(path: String): String =
    scala.io.Source.fromFile(path).getLines.mkString
}

object JsonConversion {
  def getOutput(ms: MeasurementSet) = ms.measurements.toJson.compactPrint

  def getMeasurementSet(s: String): MeasurementSet = {
    MeasurementSet(s.parseJson.convertTo[List[Double]])
  }
}
