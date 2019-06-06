package ustcrv

import chisel3.Driver

object Main extends App {
  var Args: Array[String] = args
  if (!Args.contains("--target-dir") && !Args.contains("-td")) {
    Args ++= Array("-td", "target")
  }
  var i: Int = Args indexOf "-@"
  if (i != -1) {
    val freq = Args(i + 1).toInt
    Driver.execute(Args patch(i, Nil, 2), () => new ustcrv.nexys4ddr.MainWithClock(freq))
  } else {
    Driver.execute(Args, () => new ustcrv.nexys4ddr.MainWithClock(25000000)) // Default to 25 MHz
  }

  // IMemROMData and DMemROMData are separate files now
  Driver.execute(Args, () => new ustcrv.nexys4ddr.romdata.IMemROM)
  Driver.execute(Args, () => new ustcrv.nexys4ddr.romdata.DMemROM)
}
