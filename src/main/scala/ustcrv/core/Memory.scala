package ustcrv.core

import chisel3._
import chisel3.util._

class IMem(val size: Int = 4096) extends Module {
  val io = IO(new Bundle {
    val in = Input(UInt(32.W))
    val out = Output(UInt(32.W))
  })

  val mem = Mem(size, UInt(32.W))
}

class DMem(val size: Int = 4096) extends Module {
}
