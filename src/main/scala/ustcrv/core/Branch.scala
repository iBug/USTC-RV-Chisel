package ustcrv.core

import chisel3._
import chisel3.util._

object Branch {
  val XX  = 0.U
  val EQ  = 4.U
  val NE  = 1.U
  val LT  = 2.U
  val GE  = 3.U
  val LTU = 6.U
  val GEU = 7.U
}

class BranchComp(val w: Int) extends Module {
  val io = IO(new Bundle {
    val a = Input(UInt(w.W))
    val b = Input(UInt(w.W))
    val brType = Input(UInt(3.W))
    val taken = Output(Bool())
  })

  val a = io.a
  val b = io.b
  val sa = a.asSInt
  val sb = b.asSInt
  io.taken := MuxLookup(io.brType, false.B, Array(
    Branch.EQ  -> ( a ===  b),
    Branch.NE  -> ( a =/=  b),
    Branch.LT  -> (sa  <  sb),
    Branch.GE  -> (sa  >= sb),
    Branch.LTU -> ( a  <   b),
    Branch.GEU -> ( a  >=  b)
  ))
}
