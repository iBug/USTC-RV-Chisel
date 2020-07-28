package ustcrv.core

import chisel3._
import chisel3.util._

class RegisterFileIO extends Bundle {
  val addrA = Input(UInt(wAddr.W))
  val addrB = Input(UInt(wAddr.W))
  val dataA = Output(UInt(wData.W))
  val dataB = Output(UInt(wData.W))
  val addrD = Input(UInt(wAddr.W))
  val dataD = Input(UInt(wData.W))
  val regWEn = Input(Bool())
}

class RegisterFileInternal(val wData: Int = 32, val wAddr: Int = 5) extends Module {
  val io = IO(new RegisterFileIO)

  val regCount = 1 << wAddr // Number of registers
  val r = RegInit(VecInit(Seq.fill(regCount)(0.U(wData.W))))

  // x0 should be always zero
  io.dataA := Mux(io.addrA === 0.U, 0.U(wData.W), r(io.addrA))
  io.dataB := Mux(io.addrB === 0.U, 0.U(wData.W), r(io.addrB))

  when (io.regWEn) {
    r(io.addrD) := io.dataD
  }
}

// This wrapper is used for inverting clock signal (posedge -> negedge)
class RegisterFile(val wData: Int = 32, val wAddr: Int = 5) extends Module {
  val io = IO(new RegisterFileIO)
  withClock(~clock) {
    val internal = Module(new RegisterFileInternal)
    internal.io <> io
  }
}