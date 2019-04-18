package ustcrv.core

import chisel3._
import chisel3.util._

class RegisterFile(val wData: Int, val wAddr: Int) extends Module {
  val io = IO(new Bundle {
    val ra0 = Input(UInt(wAddr.W))
    val ra1 = Input(UInt(wAddr.W))
    val rd0 = Output(UInt(wData.W))
    val rd1 = Output(UInt(wData.W))
    val wa0 = Input(UInt(wAddr.W))
    val wd0 = Input(UInt(wData.W))
    val we = Input(Bool())
  })

  val regCount = 1 << wAddr // Number of registers
  val r = VecInit(Seq.fill(regCount)(RegInit(0.U(wData.W))))

  io.rd0 := r(io.ra0)
  io.rd1 := r(io.ra1)

  when (io.we && io.wa0 =/= 0.U) {  // x0 should be always zero
    r(io.wa0) := io.wd0
  }
}
