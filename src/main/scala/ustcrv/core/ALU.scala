package ustcrv.core

import chisel3._
import chisel3.util._

object ALU {
  val wControl: Int = 4

  val ADD  = "b0000".U(wControl.W)
  val SUB  = "b1000".U(wControl.W)
  val SLL  = "b0001".U(wControl.W)
  val SLT  = "b0010".U(wControl.W)
  val SLTU = "b0011".U(wControl.W)
  val XOR  = "b0100".U(wControl.W)
  val SRL  = "b0101".U(wControl.W)
  val SRA  = "b1101".U(wControl.W)
  val OR   = "b0110".U(wControl.W)
  val AND  = "b1111".U(wControl.W)
}

class ALU(w: Int) extends Module {
  val io = IO(new Bundle {
    val a = Input(UInt(w.W))
    val b = Input(UInt(w.W))
    val sel = Input(UInt(ALU.wControl.W))
    val out = Output(UInt(w.W))
  })

  val shamt = io.b(4, 0).asUInt

  io.out := MuxLookup(io.sel, -1.S(w.W).asUInt, Array(
    ALU.ADD  -> (io.a + io.b),
    ALU.SUB  -> (io.a - io.b),
    ALU.SLL  -> (io.a << shamt),
    ALU.SLT  -> (io.a.asSInt < io.b.asSInt),
    ALU.SLTU -> (io.a < io.b),
    ALU.XOR  -> (io.a ^ io.b),
    ALU.SRL  -> (io.a >> shamt),
    ALU.SRA  -> (io.a.asSInt >> shamt).asUInt,
    ALU.OR   -> (io.a | io.b),
    ALU.AND  -> (io.a & io.b)
  ))
}
