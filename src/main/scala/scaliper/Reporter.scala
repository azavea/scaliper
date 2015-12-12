package scaliper

trait Reporter {
  def report(run: Run): Unit
}
