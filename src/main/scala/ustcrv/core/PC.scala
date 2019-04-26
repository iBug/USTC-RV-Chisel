package ustcrv.core

import chisel3._
import chisel3.util._

// w: width, s: step
object PC {
  def apply(en: Bool, sel: UInt, in: UInt, w: Int = 32, s: Int = 4): UInt = {
    val m = Module(new PC(w, s)).io
    m.en := en
    m.sel := sel
    m.in := in
    m.out
  }
}

class PC(val w: Int = 32, val s: Int = 4) extends Module {
  val io = IO(new Bundle {
    val out = Output(UInt(w.W))
    val en  =  Input(Bool())    // Should change at next clock
    val sel =  Input(UInt(1.W))
    val in  =  Input(UInt(w.W)) // Coming from branching
  })

  val r = RegInit(0.U(w.W))
  io.out := r

  when (io.en) {
    r := MuxLookup(io.sel, r + 4.U, Seq(
      1.U -> io.in
    ))
  }
}
