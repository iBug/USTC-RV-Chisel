package ustcrv.core

import chisel3._
import chisel3.util._

class IMem(val size: Int = 4096, val offset: Int = 0) extends Module {
  val io = IO(new Bundle {
    val in = Input(UInt(32.W))
    val out = Output(UInt(32.W))
  })

  val mem = SyncReadMem(size, UInt(32.W))
  val addr = (io.in - offset.U) >> 2

  io.out := mem.read(addr)
}

class DMem(val size: Int = 4096, val offset: Int = 0) extends Module {
  val io = IO(new Bundle {
    val addr = Input(UInt(32.W))
    val dataR = Output(UInt(32.W))
    val dataW = Input(UInt(32.W))
    val memRW = Input(Bool())
  })

  val mem = SyncReadMem(size, UInt(32.W))
  val addr = (io.addr - offset.U) >> 2 // access unit is 4 bytes

  when (io.memRW) {
    mem.write(addr, io.dataW)
    io.dataR := DontCare
  } .otherwise {
    io.dataR := mem.read(addr)
  }
}
