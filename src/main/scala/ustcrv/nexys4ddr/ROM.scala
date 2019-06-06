package ustcrv.nexys4ddr

import chisel3._
import chisel3.util._

import scala.io.Source

// d = depth, w = width
class ROMIO(val dw: Int, val w: Int) extends Bundle {
  val addr = Input(UInt(dw.W))
  val data = Output(UInt(w.W))
  val length = Output(UInt(32.W))
}

class ROM(val dw: Int, val w: Int, val file: String) extends Module {
  val io = IO(new ROMIO(dw, w))
  val data = Source fromFile(file) getLines() map(BigInt(_, 16).U(w.W)) toList
  val rom = VecInit(data)
  io.data := rom(io.addr)
  io.length := data.length.U
}

class IMemROM extends ROM(8, 32, "src/data/riscv.imem")
class DMemROM extends ROM(8, 32, "src/data/riscv.dmem")
