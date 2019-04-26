package ustcrv.core

import chisel3._
import chisel3.util._

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
  val MemRW = Output(Bool())  // Data Memory, 0: Read, 1: Write
  val WBSel = Output(UInt(2.W))  // For Write-back, 0: Data Memory, 1: ALU Result, 2: PC + 4
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
  val A_PC  = 1.U

  val B_RS2 = 0.U
  val B_IMM = 1.U

  val WB_DM  = 0.U
  val WB_ALU = 1.U
  val WB_PC4 = 2.U

  val PC_4   = 0.U
  val PC_ALU = 1.U

  val MEM_R = 0.U
  val MEM_W = 1.U

  //                    PCSel   ImmSel    RegWEn  BrType   BSel      ASel       ALUSel    MemRW     WBSel
  //                      |       |         |       |       |         |           |         |         |
  val default = List(   PC_4,   IMM_X,      N,     XX,    B_RS2,    A_RS1,     ALU.ADD,   MEM_R,    WB_DM)

  val map = Array(
    ADD    ->   List(   PC_4,   IMM_X,      Y,     XX,    B_RS2,    A_RS1,     ALU.ADD,   MEM_R,   WB_ALU),
    ADDI   ->   List(   PC_4,   IMM_I,      Y,     XX,    B_IMM,    A_RS1,     ALU.ADD,   MEM_R,   WB_ALU),
    AND    ->   List(   PC_4,   IMM_X,      Y,     XX,    B_RS2,    A_RS1,     ALU.AND,   MEM_R,   WB_ALU),
    ANDI   ->   List(   PC_4,   IMM_I,      Y,     XX,    B_IMM,    A_RS1,     ALU.AND,   MEM_R,   WB_ALU),
    AUIPC  ->   List(   PC_4,   IMM_U,      Y,     XX,    B_IMM,     A_PC,     ALU.ADD,   MEM_R,   WB_ALU),
    BEQ    ->   List(   PC_4,   IMM_B,      N,     EQ,    B_IMM,     A_PC,     ALU.ADD,   MEM_R,   WB_ALU),
    BGE    ->   List(   PC_4,   IMM_B,      N,     GE,    B_IMM,     A_PC,     ALU.ADD,   MEM_R,   WB_ALU),
    BGEU   ->   List(   PC_4,   IMM_B,      N,    GEU,    B_IMM,     A_PC,     ALU.ADD,   MEM_R,   WB_ALU),
    BLT    ->   List(   PC_4,   IMM_B,      N,     LT,    B_IMM,     A_PC,     ALU.ADD,   MEM_R,   WB_ALU),
    BLTU   ->   List(   PC_4,   IMM_B,      N,    LTU,    B_IMM,     A_PC,     ALU.ADD,   MEM_R,   WB_ALU),
    BNE    ->   List(   PC_4,   IMM_B,      N,     NE,    B_IMM,     A_PC,     ALU.ADD,   MEM_R,   WB_ALU),
    JAL    ->   List( PC_ALU,   IMM_J,      Y,     XX,    B_IMM,     A_PC,     ALU.ADD,   MEM_R,   WB_PC4),
    JALR   ->   List( PC_ALU,   IMM_I,      Y,     XX,    B_IMM,    A_RS1,     ALU.ADD,   MEM_R,   WB_PC4),
    LB     ->   List(   PC_4,   IMM_I,      Y,     XX,    B_IMM,    A_RS1,     ALU.ADD,   MEM_R,    WB_DM),
    LBU    ->   List(   PC_4,   IMM_I,      Y,     XX,    B_IMM,    A_RS1,     ALU.ADD,   MEM_R,    WB_DM),
    LH     ->   List(   PC_4,   IMM_I,      Y,     XX,    B_IMM,    A_RS1,     ALU.ADD,   MEM_R,    WB_DM),
    LHU    ->   List(   PC_4,   IMM_I,      Y,     XX,    B_IMM,    A_RS1,     ALU.ADD,   MEM_R,    WB_DM),
    LW     ->   List(   PC_4,   IMM_I,      Y,     XX,    B_IMM,    A_RS1,     ALU.ADD,   MEM_R,    WB_DM),
    LUI    ->   List(   PC_4,   IMM_U,      Y,     XX,    B_IMM,     A_PC,  ALU.COPY_B,   MEM_R,   WB_ALU),
    OR     ->   List(   PC_4,   IMM_X,      Y,     XX,    B_RS2,    A_RS1,      ALU.OR,   MEM_R,   WB_ALU),
    ORI    ->   List(   PC_4,   IMM_I,      Y,     XX,    B_IMM,    A_RS1,      ALU.OR,   MEM_R,   WB_ALU),
    SB     ->   List(   PC_4,   IMM_S,      N,     XX,    B_IMM,    A_RS1,     ALU.ADD,   MEM_W,   WB_ALU),
    SH     ->   List(   PC_4,   IMM_S,      N,     XX,    B_IMM,    A_RS1,     ALU.ADD,   MEM_W,   WB_ALU),
    SW     ->   List(   PC_4,   IMM_S,      N,     XX,    B_IMM,    A_RS1,     ALU.ADD,   MEM_W,   WB_ALU),
    SLL    ->   List(   PC_4,   IMM_X,      Y,     XX,    B_RS2,    A_RS1,     ALU.SLL,   MEM_R,   WB_ALU),
    SLLI   ->   List(   PC_4,   IMM_I,      Y,     XX,    B_IMM,    A_RS1,     ALU.SLL,   MEM_R,   WB_ALU),
    SLT    ->   List(   PC_4,   IMM_X,      Y,     XX,    B_RS2,    A_RS1,     ALU.SLT,   MEM_R,   WB_ALU),
    SLTI   ->   List(   PC_4,   IMM_I,      Y,     XX,    B_IMM,    A_RS1,     ALU.SLT,   MEM_R,   WB_ALU),
    SLTIU  ->   List(   PC_4,   IMM_I,      Y,     XX,    B_IMM,    A_RS1,    ALU.SLTU,   MEM_R,   WB_ALU),
    SLTU   ->   List(   PC_4,   IMM_X,      Y,     XX,    B_RS2,    A_RS1,    ALU.SLTU,   MEM_R,   WB_ALU),
    SRA    ->   List(   PC_4,   IMM_X,      Y,     XX,    B_RS2,    A_RS1,     ALU.SRA,   MEM_R,   WB_ALU),
    SRAI   ->   List(   PC_4,   IMM_I,      Y,     XX,    B_IMM,    A_RS1,     ALU.SRA,   MEM_R,   WB_ALU),
    SRL    ->   List(   PC_4,   IMM_X,      Y,     XX,    B_RS2,    A_RS1,     ALU.SRL,   MEM_R,   WB_ALU),
    SRLI   ->   List(   PC_4,   IMM_I,      Y,     XX,    B_IMM,    A_RS1,     ALU.SRL,   MEM_R,   WB_ALU),
    SUB    ->   List(   PC_4,   IMM_X,      Y,     XX,    B_RS2,    A_RS1,     ALU.SUB,   MEM_R,   WB_ALU),
    XOR    ->   List(   PC_4,   IMM_X,      Y,     XX,    B_RS2,    A_RS1,     ALU.XOR,   MEM_R,   WB_ALU),
    XORI   ->   List(   PC_4,   IMM_I,      Y,     XX,    B_IMM,    A_RS1,     ALU.XOR,   MEM_R,   WB_ALU)
  )

  val CtrlSignals = ListLookup(io.inst, default, map)

  io.PCSel := Mux(io.BrTaken, PC_ALU, CtrlSignals(0))
  io.ImmSel := CtrlSignals(1)
  io.RegWEn := CtrlSignals(2)
  io.BrType := CtrlSignals(3)
  io.BSel := CtrlSignals(4)
  io.ASel := CtrlSignals(5)
  io.ALUSel := CtrlSignals(6)
  io.MemRW := CtrlSignals(7)
  io.WBSel := CtrlSignals(8)
}
