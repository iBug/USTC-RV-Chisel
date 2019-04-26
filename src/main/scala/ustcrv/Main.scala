package ustcrv

import chisel3.Driver
import core.Core

object Main extends App {
  var Args: Array[String] = args
  if (!Args.contains("--target-dir") && !Args.contains("-td")) {
    Args ++= Array("-td", "target")
  }
  Driver.execute(Args, () => new Core)
}
