package ustcrv.core

import chisel3._
import chisel3.util._

object ALU {
  val ADD  = 0.U(3.W)
  val SUB  = 0.U(3.W)
  val SLL  = 1.U(3.W)
  val SLT  = 2.U(3.W)
  val SLTU = 3.U(3.W)
  val XOR  = 4.U(3.W)
  val SRL  = 5.U(3.W)
  val SRA  = 5.U(3.W)
  val OR   = 6.U(3.W)
  val AND  = 7.U(3.W)
}
