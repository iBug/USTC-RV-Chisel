package ustcrv.core

import chisel3._
import chisel3.util._

object ALU {
  val ADD  = "b0000".U(4.W)
  val SUB  = "b1000".U(4.W)
  val SLL  = "b0001".U(4.W)
  val SLT  = "b0010".U(4.W)
  val SLTU = "b0011".U(4.W)
  val XOR  = "b0100".U(4.W)
  val SRL  = "b0101".U(4.W)
  val SRA  = "b1101".U(4.W)
  val OR   = "b0110".U(4.W)
  val AND  = "b1111".U(4.W)
}
