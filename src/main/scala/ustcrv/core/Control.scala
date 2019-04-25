package ustcrv.core

import chisel3._
import chisel3.util._
import org.stringtemplate.v4.compiler.Bytecode.Instruction

import Instructions._
import Branch._

class ControlIO extends Bundle {
  val inst = Input(UInt(32.W))
  // according to https://inst.eecs.berkeley.edu/~cs61c/fa17/lec/11/L11_Datapath1%20(1up).pdf P48
  val PCSel = Output(Bool())  // 0: PC + 4, 1: From ALU
  val ImmSel = Output(UInt(3.W))  // 0: Immediate, 1: Store, 2: Branch, 3: Upper Immediate, 4: Jump
  val RegWEn = Output(Bool())  // Regfile Write Enable
  val BrType = Output(UInt(3.W))  // Which BrType?
  val BrTaken = Input(Bool())
  val BSel = Output(Bool())  // 0: rs2, 1: imm
  val ASel = Output(Bool())  // 0: rs1, 1: pc
  val ALUSel = Output(UInt(4.W))  // ALUop
  val MemRW = Output(Bool())  // Data Memory Write Enable
  val WBSel = Output(Bool())  // For Write-back, 0: Data Memory, 1: ALU Result
}

class Control extends Module {
  val io = IO(new ControlIO)

  val Y = true.B
  val N = false.B

  val IMM_X = 0.U
  val IMM_I = 0.U
  val IMM_S = 1.U
  val IMM_B = 2.U
  val IMM_U = 3.U
  val IMM_J = 4.U

  val A_RS1 = 0.U
  val A_PC = 1.U

  val B_RS2 = 0.U
  val B_IMM = 1.U

  val WB_DM = 0.U
  val WB_ALU = 1.U

  val PC_4 = 0.U
  val PC_ALU = 1.U

  //                    PCSel   ImmSel    RegWEn  BrType   BSel      ASel       ALUSel  MemRW   WBSel
  //                      |       |         |       |       |         |           |       |       |
  val default = List(   PC_4,   IMM_X,      N,     XX,    B_RS2,    A_RS1,     ALU.ADD,   N,    WB_DM)

  val map = Array(
    ADD    ->   List(   PC_4,   IMM_X,      Y,     XX,    B_RS2,    A_RS1,     ALU.ADD,   N,   WB_ALU),
    ADDI   ->   List(   PC_4,   IMM_I,      Y,     XX,    B_IMM,    A_RS1,     ALU.ADD,   N,   WB_ALU),
    AND    ->   List(   PC_4,   IMM_X,      Y,     XX,    B_RS2,    A_RS1,     ALU.AND,   N,   WB_ALU),
    ANDI   ->   List(   PC_4,   IMM_I,      Y,     XX,    B_IMM,    A_RS1,     ALU.AND,   N,   WB_ALU),
    AUIPC  ->   List(   PC_4,   IMM_U,      Y,     XX,    B_IMM,     A_PC,     ALU.ADD,   N,   WB_ALU),
    BEQ    ->   List(   PC_4,   IMM_B,      N,     EQ,    B_IMM,     A_PC,     ALU.ADD,   N,   WB_ALU),
    BGE    ->   List(   PC_4,   IMM_B,      N,     GE,    B_IMM,     A_PC,     ALU.ADD,   N,   WB_ALU),
    BGEU   ->   List(   PC_4,   IMM_B,      N,    GEU,    B_IMM,     A_PC,     ALU.ADD,   N,   WB_ALU),
    BLT    ->   List(   PC_4,   IMM_B,      N,     LT,    B_IMM,     A_PC,     ALU.ADD,   N,   WB_ALU),
    BLTU   ->   List(   PC_4,   IMM_B,      N,    LTU,    B_IMM,     A_PC,     ALU.ADD,   N,   WB_ALU),
    BNE    ->   List(   PC_4,   IMM_B,      N,     NE,    B_IMM,     A_PC,     ALU.ADD,   N,   WB_ALU),
    JAL    ->   List( PC_ALU,   IMM_J,      Y,      N,    B_IMM,     A_PC,     ALU.ADD,   N,   WB_ALU),

  )
}
