package ustcrv

import chisel3._
import chisel3.util._

import ustcrv.core.Package

class Nexys4IO extends Bundle {
}

class Nexys4DDR extends Module {
  val io = IO(new Nexys4IO)
}
