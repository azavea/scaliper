package scaliper

import sys.process._

object JavaRunner {

  /* TODO: Determine if the captureVmLog option is worth implementing.
   * if so, here are the java options:
   *               "-verbose:gc",
   *               "-Xbatch",
   *               "-XX:+UseSerialGC",
   *               "-XX:+PrintCompilation",
   */


  def run(classPath:String,
          className:String,
          applicationArgs:Seq[String])(fout:String=>Unit):(String,Int) = {
    val cmd = Seq("java",
                  "-cp", classPath,
                  className) ++ applicationArgs
    (cmd.reduce { (a,b) => s"${a} ${b}" }, cmd ! ProcessLogger(fout))
  }
}
