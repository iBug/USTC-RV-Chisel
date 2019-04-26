package ustcrv.core

import chisel3._
import chisel3.util._

class IMem(val size: Int = 4096, val offset: Int = 0, val debug: Boolean = false) extends Module {
  val io = IO(new Bundle {
    val in = Input(UInt(32.W))
    val out = Output(UInt(32.W))
    if (debug) {
      val drAddr = Input(UInt(32.W))
      val dwAddr = Input(UInt(32.W))
      val drData = Output(UInt(32.W))
      val dwData = Input(UInt(32.W))
      val dMode = Input(Bool()) // 0 = read, 1 = write
    }
  })

  val mem = SyncReadMem(size, UInt(32.W))
  val addr = (io.in - offset.U) >> 2.U

  io.out := mem.read(addr)

  if (debug) {
    when (io.dMode) {
      mem.write(io.dwAddr >> 2.U, io.dwData)
      io.drData := DontCare
    } .otherwise {
      io.drData := mem.read(io.drAddr)
    }
  }
}

class DMem(val size: Int = 4096, val offset: Int = 0, val debug: Boolean = false) extends Module {
  val io = IO(new Bundle {
    val addr = Input(UInt(32.W))
    val dataR = Output(UInt(32.W))
    val dataW = Input(UInt(32.W))
    val memRW = Input(Bool())
    if (debug) {
      val drAddr = Input(UInt(32.W))
      val dwAddr = Input(UInt(32.W))
      val drData = Output(UInt(32.W))
      val dwData = Input(UInt(32.W))
      val dMode = Input(Bool()) // 0 = read, 1 = write
    }
  })

  val mem = SyncReadMem(size, UInt(32.W))
  val addr = (io.addr - offset.U) >> 2.U // access unit is 4 bytes

  when (io.memRW) {
    mem.write(addr, io.dataW)
    io.dataR := DontCare
  } .otherwise {
    io.dataR := mem.read(addr)
  }

  if (debug) {
    when (io.dMode) {
      mem.write(io.dwAddr >> 2.U, io.dwData)
      io.drData := DontCare
    } .otherwise {
      io.drData := mem.read(io.drAddr)
    }
  }
}
