package ustcrv.core

import chisel3._
import chisel3.util._

object Debug {
  val NOP = 0.U
  val STEP = 1.U
  val STOP = 2.U
  val START = 3.U
  val IMEMRA = 8.U
  val IMEMWA = 9.U
  val IMEMRD = 10.U
  val IMEMWD = 11.U
  val DMEMRA = 12.U
  val DMEMWA = 13.U
  val DMEMRD = 14.U
  val DMEMWD = 15.U
}

class DebugIO extends Bundle {
  // Inputs
  val enable = Input(Bool())
  val control = Input(UInt(4.W)) // XXX: Expand this signal?
  val dataIn = Input(UInt(32.W))
  val dataOut = Output(UInt(32.W))

  // Signals (I and O)
  // PC
  val pcEnable = Output(Bool())
  val pcReset = Output(Bool())
  val pcValue = Input(UInt(32.W))
  // IMem
  val idMode = Output(Bool())
  val idrAddr = Output(UInt(32.W))
  val idwAddr = Output(UInt(32.W))
  val idrData = Input(UInt(32.W))
  val idwData = Output(UInt(32.W))
  // DMem
  val denable = Output(Bool())
  val daddr = Output(UInt(32.W))
  val ddataR = Input(UInt(32.W))
  val ddataW = Output(UInt(32.W))
  val dmemRW = Output(Bool())
}

class Debugger extends Module {
  val io = IO(new DebugIO)
  val update = io.enable && !RegNext(io.enable)
}
