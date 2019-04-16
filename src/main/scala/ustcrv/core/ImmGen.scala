package ustcrv.core

import chisel3._
import chisel3.util._

object Imm {
  val I = 0.U(3.W)
  val S = 1.U(3.W)
  val B = 2.U(3.W)
  val U = 3.U(3.W)
  val J = 4.U(3.W)
}

class ImmGen extends Module {
  val io = IO(new Bundle {
    val in = Input(UInt(32.W))
    val out = Output(UInt(32.W))
    val sel = Input(UInt(3.W))
  })

  val iI = io.in(31, 20).asSInt
  val iS = Cat(io.in(31, 25), io.in(11, 7)).asSInt
  val iB = Cat(io.inst(31), io.inst(7), io.inst(30, 25), io.inst(11, 8), 0.U(1.W)).asSInt
  val iU = Cat(io.inst(31, 12), 0.U(12.W)).asSInt
  val iJ = Cat(io.inst(31), io.inst(19, 12), io.inst(20), io.inst(30, 25), io.inst(24, 21), 0.U(1.W)).asSInt

  io.out := MuxLookup(io.sel, 0.U(32.W), Array(
    Imm.I -> iI,
    Imm.S -> iS,
    Imm.B -> iB,
    Imm.U -> iU,
    Imm.J -> iJ
  )).asUInt
}
