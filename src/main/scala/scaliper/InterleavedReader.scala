package scaliper

object InterProc {
  val outputMarker = "//ZxJ/"
}

trait InterleavedReader {
  lazy val marker = InterProc.outputMarker
  
  def log(s:String):Unit

  def handleOutput(s:String):Unit

  def readLine(s:String) = {
    if(s.startsWith(marker)) handleOutput(s.substring(marker.length))
    else log(s)
  }
}
