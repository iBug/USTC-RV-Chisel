package ustcrv.core

import chisel3._
import chisel3.util._

class RegisterFile(val wData: Int = 32, val wAddr: Int = 5) extends Module {
  val io = IO(new Bundle {
    val addrA = Input(UInt(wAddr.W))
    val addrB = Input(UInt(wAddr.W))
    val dataA = Output(UInt(wData.W))
    val dataB = Output(UInt(wData.W))
    val addrD = Input(UInt(wAddr.W))
    val dataD = Input(UInt(wData.W))
    val regWEn = Input(Bool())
  })

  val regCount = 1 << wAddr // Number of registers
  val r = RegInit(VecInit(Seq.fill(regCount)(0.U(wData.W))))

  io.dataA := r(io.addrA)
  io.dataB := r(io.addrB)

  when (io.regWEn && io.addrD =/= 0.U) {  // x0 should be always zero
    r(io.addrD) := io.dataD
  }
}
