package ustcrv.nexys4ddr

import chisel3._
import chisel3.util._
import chisel3.experimental.MultiIOModule

import scala.io.Source

// d = depth, w = width
class ROMBlackBoxIO(val dw: Int, val w: Int) extends Bundle {
  val clock = Input(Clock())
  val reset = Input(Bool())
  val addr = Input(UInt(dw.W))
  val data = Output(UInt(w.W))
  val length = Output(UInt(32.W))
}

class ROM(val dw: Int, val w: Int, val file: String) extends MultiIOModule {
  val addr = IO(Input(UInt(dw.W)))
  val data = IO(Output(UInt(w.W)))
  val length = IO(Output(UInt(32.W)))
  val romData = Source fromFile(file) getLines() map(BigInt(_, 16).U(w.W)) toList
  val rom = VecInit(romData)
  data := rom(addr)
  length := romData.length.U
}

class ROMBlackBox(val dw: Int, val w: Int, val file: String = "") extends BlackBox {
  val io = IO(new ROMBlackBoxIO(dw, w))
}

class IMemROM extends ROMBlackBox(8, 32)
class DMemROM extends ROMBlackBox(8, 32)
