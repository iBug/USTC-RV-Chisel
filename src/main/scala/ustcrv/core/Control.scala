package ustcrv.core

import chisel3._
import chisel3.util._

import Instructions._
import Branch._

class ControlSignals extends Bundle {
  val PCSel = Bool()  // 0: PC + 4, 1: From ALU
  val ImmSel = UInt(3.W)  // 0: Immediate, 1: Store, 2: Branch, 3: Upper Immediate, 4: Jump
  val RegWEn = Bool()  // Regfile Write Enable
  val BrType = UInt(3.W)  // Which BrType?
  val BSel = Bool()  // 0: rs2, 1: imm
  val ASel = Bool()  // 0: rs1, 1: pc
  val ALUSel = UInt(4.W)  // ALUop
  val MemRW = Bool()  // Data Memory, 0: Read, 1: Write
  val WBSel = UInt(2.W)  // For Write-back, 0: Data Memory, 1: ALU Result, 2: PC + 4
  val MemLength = UInt(2.W)
  val MemSign = UInt(1.W)
}

class ControlIO extends Bundle {
  val inst = Input(UInt(32.W))
  // according to https://inst.eecs.berkeley.edu/~cs61c/fa17/lec/11/L11_Datapath1%20(1up).pdf P48
  val BrTaken = Input(Bool())
  val signals = Output(new ControlSignals)
}

object Control {
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

  val ML_X = 0.U
  val ML_W = 1.U
  val ML_H = 2.U
  val ML_B = 3.U

  val MS_U = 0.U
  val MS_S = 1.U

  val controlSignals = new ControlSignals
}

class Control extends Module {
  val io = IO(new ControlIO)
  import Control._

  val Y = true.B
  val N = false.B

  //                    PCSel   ImmSel    RegWEn  BrType   BSel      ASel       ALUSel    MemRW     WBSel  MemLength MemSign
  //                      |       |         |       |       |         |           |         |         |        |        |
  val default = List(   PC_4,   IMM_X,      N,     XX,    B_RS2,    A_RS1,     ALU.ADD,   MEM_R,    WB_DM,   ML_X,    MS_U)

  val map = Array(
    ADD    ->   List(   PC_4,   IMM_X,      Y,     XX,    B_RS2,    A_RS1,     ALU.ADD,   MEM_R,   WB_ALU,   ML_X,    MS_U),
    ADDI   ->   List(   PC_4,   IMM_I,      Y,     XX,    B_IMM,    A_RS1,     ALU.ADD,   MEM_R,   WB_ALU,   ML_X,    MS_U),
    AND    ->   List(   PC_4,   IMM_X,      Y,     XX,    B_RS2,    A_RS1,     ALU.AND,   MEM_R,   WB_ALU,   ML_X,    MS_U),
    ANDI   ->   List(   PC_4,   IMM_I,      Y,     XX,    B_IMM,    A_RS1,     ALU.AND,   MEM_R,   WB_ALU,   ML_X,    MS_U),
    AUIPC  ->   List(   PC_4,   IMM_U,      Y,     XX,    B_IMM,     A_PC,     ALU.ADD,   MEM_R,   WB_ALU,   ML_X,    MS_U),
    BEQ    ->   List(   PC_4,   IMM_B,      N,     EQ,    B_IMM,     A_PC,     ALU.ADD,   MEM_R,   WB_ALU,   ML_X,    MS_U),
    BGE    ->   List(   PC_4,   IMM_B,      N,     GE,    B_IMM,     A_PC,     ALU.ADD,   MEM_R,   WB_ALU,   ML_X,    MS_U),
    BGEU   ->   List(   PC_4,   IMM_B,      N,    GEU,    B_IMM,     A_PC,     ALU.ADD,   MEM_R,   WB_ALU,   ML_X,    MS_U),
    BLT    ->   List(   PC_4,   IMM_B,      N,     LT,    B_IMM,     A_PC,     ALU.ADD,   MEM_R,   WB_ALU,   ML_X,    MS_U),
    BLTU   ->   List(   PC_4,   IMM_B,      N,    LTU,    B_IMM,     A_PC,     ALU.ADD,   MEM_R,   WB_ALU,   ML_X,    MS_U),
    BNE    ->   List(   PC_4,   IMM_B,      N,     NE,    B_IMM,     A_PC,     ALU.ADD,   MEM_R,   WB_ALU,   ML_X,    MS_U),
    JAL    ->   List( PC_ALU,   IMM_J,      Y,     XX,    B_IMM,     A_PC,     ALU.ADD,   MEM_R,   WB_PC4,   ML_X,    MS_U),
    JALR   ->   List( PC_ALU,   IMM_I,      Y,     XX,    B_IMM,    A_RS1,     ALU.ADD,   MEM_R,   WB_PC4,   ML_X,    MS_U),
    LB     ->   List(   PC_4,   IMM_I,      Y,     XX,    B_IMM,    A_RS1,     ALU.ADD,   MEM_R,    WB_DM,   ML_B,    MS_S),
    LBU    ->   List(   PC_4,   IMM_I,      Y,     XX,    B_IMM,    A_RS1,     ALU.ADD,   MEM_R,    WB_DM,   ML_B,    MS_U),
    LH     ->   List(   PC_4,   IMM_I,      Y,     XX,    B_IMM,    A_RS1,     ALU.ADD,   MEM_R,    WB_DM,   ML_H,    MS_S),
    LHU    ->   List(   PC_4,   IMM_I,      Y,     XX,    B_IMM,    A_RS1,     ALU.ADD,   MEM_R,    WB_DM,   ML_H,    MS_U),
    LW     ->   List(   PC_4,   IMM_I,      Y,     XX,    B_IMM,    A_RS1,     ALU.ADD,   MEM_R,    WB_DM,   ML_W,    MS_S),
    LUI    ->   List(   PC_4,   IMM_U,      Y,     XX,    B_IMM,     A_PC,  ALU.COPY_B,   MEM_R,   WB_ALU,   ML_X,    MS_U),
    OR     ->   List(   PC_4,   IMM_X,      Y,     XX,    B_RS2,    A_RS1,      ALU.OR,   MEM_R,   WB_ALU,   ML_X,    MS_U),
    ORI    ->   List(   PC_4,   IMM_I,      Y,     XX,    B_IMM,    A_RS1,      ALU.OR,   MEM_R,   WB_ALU,   ML_X,    MS_U),
    SB     ->   List(   PC_4,   IMM_S,      N,     XX,    B_IMM,    A_RS1,     ALU.ADD,   MEM_W,   WB_ALU,   ML_B,    MS_U),
    SH     ->   List(   PC_4,   IMM_S,      N,     XX,    B_IMM,    A_RS1,     ALU.ADD,   MEM_W,   WB_ALU,   ML_H,    MS_U),
    SW     ->   List(   PC_4,   IMM_S,      N,     XX,    B_IMM,    A_RS1,     ALU.ADD,   MEM_W,   WB_ALU,   ML_W,    MS_U),
    SLL    ->   List(   PC_4,   IMM_X,      Y,     XX,    B_RS2,    A_RS1,     ALU.SLL,   MEM_R,   WB_ALU,   ML_X,    MS_U),
    SLLI   ->   List(   PC_4,   IMM_I,      Y,     XX,    B_IMM,    A_RS1,     ALU.SLL,   MEM_R,   WB_ALU,   ML_X,    MS_U),
    SLT    ->   List(   PC_4,   IMM_X,      Y,     XX,    B_RS2,    A_RS1,     ALU.SLT,   MEM_R,   WB_ALU,   ML_X,    MS_U),
    SLTI   ->   List(   PC_4,   IMM_I,      Y,     XX,    B_IMM,    A_RS1,     ALU.SLT,   MEM_R,   WB_ALU,   ML_X,    MS_U),
    SLTIU  ->   List(   PC_4,   IMM_I,      Y,     XX,    B_IMM,    A_RS1,    ALU.SLTU,   MEM_R,   WB_ALU,   ML_X,    MS_U),
    SLTU   ->   List(   PC_4,   IMM_X,      Y,     XX,    B_RS2,    A_RS1,    ALU.SLTU,   MEM_R,   WB_ALU,   ML_X,    MS_U),
    SRA    ->   List(   PC_4,   IMM_X,      Y,     XX,    B_RS2,    A_RS1,     ALU.SRA,   MEM_R,   WB_ALU,   ML_X,    MS_U),
    SRAI   ->   List(   PC_4,   IMM_I,      Y,     XX,    B_IMM,    A_RS1,     ALU.SRA,   MEM_R,   WB_ALU,   ML_X,    MS_U),
    SRL    ->   List(   PC_4,   IMM_X,      Y,     XX,    B_RS2,    A_RS1,     ALU.SRL,   MEM_R,   WB_ALU,   ML_X,    MS_U),
    SRLI   ->   List(   PC_4,   IMM_I,      Y,     XX,    B_IMM,    A_RS1,     ALU.SRL,   MEM_R,   WB_ALU,   ML_X,    MS_U),
    SUB    ->   List(   PC_4,   IMM_X,      Y,     XX,    B_RS2,    A_RS1,     ALU.SUB,   MEM_R,   WB_ALU,   ML_X,    MS_U),
    XOR    ->   List(   PC_4,   IMM_X,      Y,     XX,    B_RS2,    A_RS1,     ALU.XOR,   MEM_R,   WB_ALU,   ML_X,    MS_U),
    XORI   ->   List(   PC_4,   IMM_I,      Y,     XX,    B_IMM,    A_RS1,     ALU.XOR,   MEM_R,   WB_ALU,   ML_X,    MS_U)
  )

  val CtrlSignals = ListLookup(io.inst, default, map)

  io.signals.PCSel := Mux(io.BrTaken, PC_ALU, CtrlSignals(0))
  io.signals.ImmSel := CtrlSignals(1)
  io.signals.RegWEn := CtrlSignals(2)
  io.signals.BrType := CtrlSignals(3)
  io.signals.BSel := CtrlSignals(4)
  io.signals.ASel := CtrlSignals(5)
  io.signals.ALUSel := CtrlSignals(6)
  io.signals.MemRW := CtrlSignals(7)
  io.signals.WBSel := CtrlSignals(8)
  io.signals.MemLength := CtrlSignals(9)
  io.signals.MemSign := CtrlSignals(10)
}
