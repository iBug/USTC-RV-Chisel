package ustcrv.nexys4ddr

import chisel3._
import chisel3.util._

import scala.io.Source

// d = depth, w = width
class ROMIO(val d: Int, val w: Int) extends Bundle {
  val addr = Input(UInt(log2Ceil(d).W))
  val data = Output(UInt(w.W))
}

class ROM(val d: Int, val w: Int, val file: String) extends Module {
  val io = IO(new ROMIO(d, w))
  val data = Source.fromFile(file).getLines.map(BigInt(_, 16).U(w.W)).toList
  val rom = VecInit(data)
  io.data := rom(io.addr)
}

class IMemROM extends ROM(100, 32, "src/data/riscv.imem")
class DMemROM extends ROM(4, 32, "src/data/riscv.dmem")
