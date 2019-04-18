package ustcrv.core

import chisel3._
import chisel3.util._
import org.stringtemplate.v4.compiler.Bytecode.Instruction

class ControlIO extends Bundle {
  val inst = Input(UInt(32.W))
  // according to COD5 RISCV Edition
  val branch = Output(Bool())
  val mem_read = Output(Bool())
  val mem_to_reg = Output(Bool())
  val alu_op = Output(UInt(4.W))
  val mem_write = Output(Bool())
  val alu_src = Output(Bool())  // 0: reg2, 1: (signed-ext) imm
  val reg_write = Output(Bool())
}

import Instructions._

class Control extends Module {
  val io = IO(new ControlIO)

  val Y = true.B
  val N = false.B

  //              branch mem_read mem_to_reg alu_op mem_write alu_src reg_write
  //                 |      |       |           |       |       |         |
  val default = List(N,     N,      N,      ALU.ADD,    N,      N,        N)

  val map = Array(
    ADD  ->     List(N,     N,      N,      ALU.ADD,    N,      N,        Y),
    ADDI ->     List(N,     N,      N,      ALU.ADD,    N,      Y,        Y),
  )
}
