package ustcrv.nexys4ddr

import chisel3._
import chisel3.util._

// d = depth, w = width
class ROMIO(val d: Int, val w: Int) extends Bundle {
  val addr = Input(UInt(log2Ceil(d).W))
  val data = Output(UInt(w.W))
}

class ROM(val d: Int, val w: Int) extends BlackBox {
  val io = IO(new ROMIO(d, w))
}

class IMemROM extends ROM(0x100, 32)
class DMemROM extends ROM(0x100, 32)
