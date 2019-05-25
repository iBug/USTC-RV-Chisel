package ustcrv.core

import chisel3._
import chisel3.util._

class IMem(val size: Int = 1024, val offset: Int = 0, val debug: Boolean = false) extends Module {
  val io = IO(new Bundle {
    val in = Input(UInt(32.W))
    val out = Output(UInt(32.W))

    // Debug ports
    val drAddr = Input(UInt(32.W))
    val dwAddr = Input(UInt(32.W))
    val drData = Output(UInt(32.W))
    val dwData = Input(UInt(32.W))
    val dMode = Input(Bool()) // 0 = read, 1 = write
  })

  val mem = Mem(size, UInt(32.W))
  val addr = (io.in - offset.U) >> 2.U
  val drAddr = (io.drAddr - offset.U) >> 2.U
  val dwAddr = (io.dwAddr - offset.U) >> 2.U

  io.out := mem.read(addr)

  if (debug) {
    when (io.dMode) {
      mem.write(dwAddr, io.dwData)
      io.drData := DontCare
    } .otherwise {
      io.drData := mem.read(drAddr)
    }
  } else {
    io.drData := DontCare
  }
}

class DMem(val size: Int = 1024, val offset: Int = 0, val debug: Boolean = false) extends Module {
  val io = IO(new Bundle {
    // IO for CPU Core
    val enable = Input(Bool())
    val addr = Input(UInt(32.W))
    val dataR = Output(UInt(32.W))
    val dataW = Input(UInt(32.W))
    val memRW = Input(Bool())

    // IO for debug module
    val dEnable = Input(Bool())
    val dAddr = Input(UInt(32.W))
    val drData = Output(UInt(32.W))
    val dwData = Input(UInt(32.W))
    val dMode = Input(Bool()) // 0 = read, 1 = write
  })

  val mem = Mem(size, UInt(32.W))
  val addr = (io.addr - offset.U) >> 2.U // access unit is 4 bytes
  val dAddr = (io.dAddr - offset.U) >> 2.U // access unit is 4 bytes
  val data = RegInit(0.U(32.W))
  io.dataR := data

  when (io.enable) {
    when (io.memRW) {
      mem.write(addr, io.dataW)
    } .otherwise {
      data := mem.read(addr)
    }
  }

  if (debug) {
    val ddata = RegInit(0.U(32.W))
    io.drData := ddata
    when (io.dEnable) {
      when (io.dMode) {
        mem.write(dAddr, io.dwData)
      } .otherwise {
        ddata := mem.read(dAddr)
      }
    }
  } else {
    io.drData := DontCare
  }
}
