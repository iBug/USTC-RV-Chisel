package ustcrv

import chisel3.Driver

object Main extends App {
  var Args: Array[String] = args
  if (!Args.contains("--target-dir") && !Args.contains("-td")) {
    Args ++= Array("-td", "target")
  }
  Driver.execute(Args, () => new ustcrv.nexys4ddr.Main)
}
