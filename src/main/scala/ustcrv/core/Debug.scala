package ustcrv.core

import chisel3._
import chisel3.util._

object Debug {
  val NOP = 0.U
}

class DebugIO extends Bundle {
  // Inputs
  val control = Input(UInt(4.W)) // XXX: Expand this signal?
  val enable = Input(Bool()) // Not yet clear about what this is going to be used for
  val dataIn = Input(UInt(32.W))
  val dataOut = Output(UInt(32.W))

  // Signals (I and O)
  // PC
  val pcEnable = Output(Bool())
  val pcReset = Output(Bool())
  val pcValue = Input(UInt(32.W))

  val memSelect = Output(Bool()) // False = CPU, True = this module
  // IMem
  val idMode = Output(Bool())
  val idrAddr = Output(UInt(32.W))
  val idwAddr = Output(UInt(32.W))
  val idrData = Input(UInt(32.W))
  val idwData = Output(UInt(32.W))
  // DMem - Going different: DMem has dedicated IO for debug
  val daddr = Output(UInt(32.W))
  val ddataR = Input(UInt(32.W))
  val ddataW = Output(UInt(32.W))
  val dmemRW = Output(Bool())
}

class Debugger extends Module {
  val io = IO(new DebugIO)
}
