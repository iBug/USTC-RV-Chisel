package ustcrv.core

import chisel3._
import chisel3.util._

object Register {
  def apply(w: Int, in: UInt, en: Bool, init: Int = 0): UInt = {
    val m = Module(new Register(w, init)).io
    m.in := in
    m.en := en
    m.out
  }
}

class RegisterIO(val w: Int) extends Bundle {
  val in = Input(UInt(w.W))
  val out = Output(UInt(w.W))
  val en = Input(Bool())
}

class Register(val w: Int, val init: Int = 0) extends Module {
  val io = IO(new RegisterIO(w))
  val reg = RegEnable(io.in, init.U(w.W), io.en)
  io.out := reg
}
