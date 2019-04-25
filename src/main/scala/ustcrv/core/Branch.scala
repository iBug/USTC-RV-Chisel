package ustcrv.core

import chisel3._
import chisel3.util._

class BranchComp(val w: Int) extends Module {
  val io = IO(new Bundle {
    val a = Input(UInt(w.W))
    val b = Input(UInt(w.W))
    val brUn = Input(Bool())
    val brLt = Output(Bool())
    val brEq = Output(Bool())
  })

  val sa = io.a.asSInt
  val sb = io.b.asSInt
  io.brLt := Mux(io.brUn, io.a < io.b, sa < sb)
  io.brEq := Mux(io.brUn, io.a === io.b, sa === sb)
}
