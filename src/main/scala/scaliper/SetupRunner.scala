package scaliper

import scaliper._
import java.io.File
import java.io.IOException

import scala.collection.JavaConversions._
import scala.collection.mutable

object MeasurementType {
  val TIME = 0
  val INSTANCE = 1
  val MEMORY = 2
  val DEBUG = 3
}

object SetupRunner {
  def split(args:String) = args.split("\\s+") 
                               .map { x => x.replace(" ","").replace("\t","") }
                               .filter { x => x != "" } 
                               .toSeq

  def defaultClasspath(o: Object) = o.getClass.getClassLoader match {
    case urlcl: java.net.URLClassLoader => extractClasspath(urlcl)
    case cl => sys.props("java.class.path")
  }

  def extractClasspath(urlclassloader: java.net.URLClassLoader): String = {
    val fileResource = "file:(.*)".r
    val files = urlclassloader.getURLs.map(_.toString) collect {
      case fileResource(file) => file
    }
    files.mkString(":")
  }

  def parseOutput(s:String):MeasurementSet = {
    JsonConversion.getMeasurementSet(s)

  }

  def measure(benchmark: Benchmark)(observer: String => Unit): MeasurementSet = {
    val eventLog = new StringBuilder()

    var resultPath: String = null
    var measurementSet: MeasurementSet = null
    var consoleDots = 0

    val reader = new InterleavedReader {
      def log(s:String) = { observer(s) }
      def handleOutput(s:String) = {
        resultPath = s
        measurementSet = parseOutput(IO.read(resultPath))
      }
    }

    def warmupTime:Long = 3000
    def runTime:Long = 1000
    def trial = 1

    val f = java.io.File.createTempFile("pre", "post")
    f.deleteOnExit
    val p = f.getPath
    KryoSerializer.serializeToFile(benchmark, p)
    var caliperArgs = Seq("--warmupMillis", warmupTime.toString,
                          "--runMillis", runTime.toLong.toString,
                          "--measurementType", MeasurementType.TIME.toString(),
                          "--marker", reader.marker,
                          "--BENCHDATA", p)

    val (cmd,rc) = try {
      JavaRunner.run(
        defaultClasspath(benchmark),
        scaliper.InProcessRunner.getClass.getName.replace("$",""),
        caliperArgs
      )(reader.readLine)
    } catch {
      case e:java.io.IOException => throw new RuntimeException("failed to start subprocess", e)
    } finally {
//      f.delete
      if(resultPath != null) {
        new File(resultPath).delete
      }
    }

    if (measurementSet == null) {
      val message = s"Failed to execute ${cmd}" 
      System.err.println("  " + message)
      System.err.println(eventLog.toString())
      throw new ConfigurationException(message)
    }

    measurementSet
  }
}
