package ustcrv.nexys4ddr.vga

import chisel3._
import chisel3.util._

import scala.io.Source

object Charmap {
  def apply(in: UInt): Vec[UInt] = {
    val m = Module(new Charmap).io
    m.in := in
    m.out
  }
}

class Charmap extends Module {
  val io = IO(new Bundle {
    val in = Input(UInt(8.W))
    val out = Output(Vec(16, UInt(8.W)))
  })

  val data = Source fromFile("src/data/pixel_text.dat")getLines()map(
      l => VecInit(
        l split(" ") map(s => ("h" + s)U(8.W)) toList
      )) toList
  val rom = VecInit(data)
  io.out := Mux(io.in < 0x20.U || io.in >= 0x7F.U, 0.U(8.W), rom(0x20.U + io.in))
}
