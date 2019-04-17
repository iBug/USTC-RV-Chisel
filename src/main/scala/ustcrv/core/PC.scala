package ustcrv.core

import chisel3._
import chisel3.util._

class PC(val w: Int = 32, val s: Int = 4) extends Module {
  val io = IO(new Bundle {
    val out = Output(UInt(w.W))
    val en  =  Input(Bool())    // Should change at next clock
    val sel =  Input(UInt(1.W))
    val in  =  Input(UInt(w.W)) // Coming from branching
  })

  val r = RegInit(0.U(w.W))

  when (io.en) {
    r := MuxLookup(io.sel, r + 4.U, Seq(
      1.U -> io.in
    ))
  }
}
