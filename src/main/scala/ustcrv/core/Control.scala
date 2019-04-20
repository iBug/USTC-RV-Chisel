package ustcrv.core

import chisel3._
import chisel3.util._
import org.stringtemplate.v4.compiler.Bytecode.Instruction

import Instructions._

class ControlIO extends Bundle {
  val inst = Input(UInt(32.W))
  // according to https://inst.eecs.berkeley.edu/~cs61c/fa17/lec/11/L11_Datapath1%20(1up).pdf P48
  val PCSel = Output(Bool())
  val ImmSel = Output(Bool())
  val RegWEn = Output(Bool())
  val BrUn = Output(Bool())
  val ALUSel = Output(UInt(4.W))
  val MemRW = Output(Bool())
  val WBsel = Output(Bool())  // 0: reg2, 1: (signed-ext) imm
}

class Control extends Module {
  val io = IO(new ControlIO)

  val Y = true.B
  val N = false.B

  //              branch memRead mem2Reg      aluOp  memWrite aluSrc   regWrite
  //                 |      |       |           |       |       |         |
  val default = List(N,     N,      N,      ALU.ADD,    N,      N,        N)

  val map = Array(
    ADD    ->   List(N,     N,      N,      ALU.ADD,    N,      N,        Y),
    ADDI   ->   List(N,     N,      N,      ALU.ADD,    N,      Y,        Y),
    SUB    ->   List(N,     N,      N,      ALU.SUB,    N,      N,        Y),
    LUI    ->   List(N,     N,      N,      ALU.COPY_B, N,      Y,        Y),
    AUIPC  ->   List(N,     N,      N,      ALU.ADD,    N,      Y,        N)
  )
}
