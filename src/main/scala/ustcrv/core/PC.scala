package ustcrv.core

import chisel3._
import chisel3.util._

// w: width, s: step
object PC {
  def apply(in: UInt, bubbleF: Bool, flushF: Bool, w: Int = 32, s: Int = 4): UInt = {
    val m = Module(new PC(w, s)).io
    m.in := in
    m.bubbleF := bubbleF
    m.flushF := flushF
    m.out
  }
}

class PC(val w: Int = 32, val s: Int = 4) extends Module {
  val io = IO(new Bundle {
    val out     = Output(UInt(w.W))
    val in      =  Input(UInt(w.W)) // Coming from branching
    val bubbleF = Input(Bool())
    val flushF  = Input(Bool())
  })

  val r = RegInit(0.U(w.W))
  io.out := r

  when (!io.bubbleF) {
    r := Mux(io.flushF, 0.U(w.W), io.in)
  }
}
