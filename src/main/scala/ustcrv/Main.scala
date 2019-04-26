package ustcrv

import chisel3.Driver
import core.Core

object Main extends App {
  var Args: Seq[String] = args
  if (Args contains "--target-dir" || Args contains "-td") {
    Args ++= Seq("-td", "target")
  }
  Driver.execute(args, () => new Core)
}
