package ustcrv.core

import chisel3._
import chisel3.util._

class ControlIO extends Bundle {
}

class Control extends Module {
  val io = IO(new ControlIO)
}
