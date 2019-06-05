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
    val length = Input(UInt(2.W))
    val sign = Input(UInt(1.W))

    // IO for debug module
    val dEnable = Input(Bool())
    val dAddr = Input(UInt(32.W))
    val drData = Output(UInt(32.W))
    val dwData = Input(UInt(32.W))
    val dMode = Input(Bool()) // 0 = read, 1 = write
  })

  val mem = Mem(size, Vec(4, UInt(8.W)))
  val addr = (io.addr - offset.U) >> 2.U // access unit is 4 bytes
  val subword = (io.addr - offset.U)(1, 0) // low 2 bits
  val mask = Wire(UInt(4.W)) // write mask

  mask := MuxLookup(io.length, 15.U, Array(
    Control.ML_B -> (1.U << subword),
    Control.ML_H -> ((0x33.U << subword)(7, 4)),
  ))

  io.dataR := DontCare

  when (io.enable) {
    when (io.memRW) { // Write
      val dataW = MuxLookup(io.length,
        VecInit(io.dataW(7, 0), io.dataW(15, 8), io.dataW(23, 16), io.dataW(31, 24)),
        Array(
          Control.ML_B -> VecInit(io.dataW(7, 0), io.dataW(7, 0), io.dataW(7, 0), io.dataW(7, 0)),
          Control.ML_H -> VecInit(io.dataW(7, 0), io.dataW(15, 8), io.dataW(7, 0), io.dataW(15, 8))
      ))
      mem.write(addr, dataW, mask.asBools)
    } .otherwise { // Read
      val data = mem.read(addr).asUInt
      val sb = WireInit(0.S(32.W))
      val sh = WireInit(0.S(32.W))
      val ub = WireInit(0.U(32.W))
      val uh = WireInit(0.U(32.W))
      val sw = subword << 3.U // sw = shift width
      sb := (data >> sw)(7, 0).asSInt
      sh := (Cat(data, data) >> sw)(15, 0).asSInt
      ub := (data >> sw)(7, 0)
      uh := (Cat(data, data) >> sw)(15, 0)
      io.dataR := Mux(io.sign.asBool,
        // Signed read
        MuxLookup(io.length, data, Array(
          Control.ML_B -> sb.asUInt,
          Control.ML_H -> sh.asUInt
        )),
        // Unsigned read
        MuxLookup(io.length, data, Array(
          Control.ML_B -> ub.asUInt,
          Control.ML_H -> uh.asUInt
        ))
      )
    }
  }

  if (debug) {
    val dAddr = (io.dAddr - offset.U) >> 2.U // access unit is 4 bytes
    val ddata = RegInit(0.U(32.W))
    io.drData := ddata
    when (io.dEnable) {
      when (io.dMode) {
        val dwData = VecInit(io.dwData(7, 0), io.dwData(15, 8), io.dwData(23, 16), io.dwData(31, 24))
        mem.write(dAddr, dwData)
      } .otherwise {
        val rawData = mem.read(dAddr).asUInt
        ddata := rawData
        io.drData := rawData
      }
    }
  } else {
    io.drData := DontCare
  }
}
