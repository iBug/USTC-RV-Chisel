package ustcrv.core

import chisel3._
import chisel3.util._

object Debug {
  val NOP = 0.U
}

class DebugIO extends Bundle {
  // Inputs
  val control = Input(UInt(4.W))
  val dataIn = Input(UInt(32.W))
  val dataOut = Output(UInt(32.W))

  // Signals (I and O)
  // Reset PC
  val pcReset = Output(Bool())
  // Write IMem
  // Write DMem
}

class Debugger extends Module {
  val io = IO(new DebugIO)
}
