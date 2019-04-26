package ustcrv.core

import chisel3._
import chisel3.util._

object Imm {
  val I = 0.U(3.W)  // Immediate
  val S = 1.U(3.W)  // Store
  val B = 2.U(3.W)  // Branch
  val U = 3.U(3.W)  // Upper Immediate
  val J = 4.U(3.W)  // Jump

  def apply(in: UInt, sel: UInt): UInt = {
    val m = Module(new ImmGen).io
    m.in := in
    m.sel := sel
    m.out
  }
}

class ImmGen extends Module {
  val io = IO(new Bundle {
    val in = Input(UInt(32.W))
    val out = Output(UInt(32.W))
    val sel = Input(UInt(3.W))
  })

  val iI = io.in(31, 20).asSInt
  val iS = Cat(io.in(31, 25), io.in(11, 7)).asSInt
  val iB = Cat(io.in(31), io.in(7), io.in(30, 25), io.in(11, 8), 0.U(1.W)).asSInt
  val iU = Cat(io.in(31, 12), 0.U(12.W)).asSInt
  val iJ = Cat(io.in(31), io.in(19, 12), io.in(20), io.in(30, 25), io.in(24, 21), 0.U(1.W)).asSInt

  io.out := MuxLookup(io.sel, 0.S(32.W), Array(
    Imm.I -> iI,
    Imm.S -> iS,
    Imm.B -> iB,
    Imm.U -> iU,
    Imm.J -> iJ
  )).asUInt
}
