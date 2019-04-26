package ustcrv.core

import chisel3._
import chisel3.util._

class IMem(val size: Int = 4096) extends Module {
  val io = IO(new Bundle {
    val in = Input(UInt(32.W))
    val out = Output(UInt(32.W))
  })

  val mem = SyncReadMem(size, UInt(32.W))

  io.out := mem.read(io.in)
}

class DMem(val size: Int = 4096) extends Module {
  val io = IO(new Bundle {
    val addr = Input(UInt(32.W))
    val dataR = Output(UInt(32.W))
    val dataW = Input(UInt(32.W))
    val memRW = Input(Bool())
  })

  val mem = SyncReadMem(size, UInt(32.W))

  when (io.memRW) {
    mem.write(io.addr, io.dataW)
    io.dataR := DontCare
  } .otherwise {
    io.dataR := mem.read(io.addr)
  }
}
