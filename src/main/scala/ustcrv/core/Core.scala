package ustcrv.core

import chisel3._
import chisel3.util._

class CoreIO extends Bundle {
  val enable = Input(Bool()) // CPU pause when zero
  val imemIn = Output(UInt(32.W))
  val imemOut = Input(UInt(32.W))
  val dmemA = Output(UInt(32.W))
  val dmemDR = Input(UInt(32.W))
  val dmemDW = Output(UInt(32.W))
  val dmemWE = Output(UInt(32.W))
  val dmemL = Output(UInt(2.W))
  val dmemS = Output(UInt(1.W))

  // Ports for debugging
  val pcReset = Input(Bool())
  val pcStep = Input(Bool())
  val pcValue = Output(UInt(32.W))
}

class Core extends Module {
  val io = IO(new CoreIO)
  val control = Module(new Control).io

  val alu = Module(new ALU).io
  val regFile = Module(new RegisterFile(32, 5)).io
  val branchComp = Module(new BranchComp(32)).io
  val immGen = Module(new ImmGen).io
  val pc = withClockAndReset (clock, reset.asBool | io.pcReset) {
    Module(new PC)
  }.io

  val step = io.pcStep && RegNext(io.pcStep)

  // Wires
  val wb = Wire(UInt(32.W))

  // Pipeline registers
  val fd_inst = RegInit(Instructions.NOP)
  val fd_pc = Reg(UInt())

  val de_control = Reg(Control.controlSignals)
  val de_regFile_dataA = Reg(UInt())
  val de_regFile_dataB = Reg(UInt())
  val de_regFile_addrD = Reg(UInt())
  val de_regFile_addrA = Reg(UInt())
  val de_regFile_addrB = Reg(UInt())
  val de_pc = Reg(UInt())
  val de_immGen = Reg(UInt())

  val em_control = Reg(Control.controlSignals)
  val em_alu = Reg(UInt())
  val em_pc = Reg(UInt())
  val em_regFile_dataB = Reg(UInt())
  val em_regFile_addrD = Reg(UInt())

  var mw_control = Reg(Control.controlSignals)
  val mw_alu = Reg(UInt())
  val mw_pc = Reg(UInt())
  val mw_dmemDR = Reg(UInt())
  val mw_regFile_addrD = Reg(UInt())

  when (de_control.PCSel === Control.PC_ALU) {
    fd_pc := 0.U
    fd_inst := Instructions.NOP
    de_control := 0.U.asTypeOf(Control.controlSignals)
    de_immGen := 0.U
    de_pc := 0.U
    de_regFile_dataA := 0.U
    de_regFile_dataB := 0.U
    de_regFile_addrD := 0.U
    de_regFile_addrA := 0.U
    de_regFile_addrB := 0.U
  }.elsewhen (de_control.WBSel === Control.WB_DM && (de_regFile_addrD === regFile.addrA || de_regFile_addrD === regFile.addrB)) {
    de_control := 0.U.asTypeOf(Control.controlSignals)
    de_immGen := 0.U
    de_pc := 0.U
    de_regFile_dataA := 0.U
    de_regFile_dataB := 0.U
    de_regFile_addrD := 0.U
    de_regFile_addrA := 0.U
    de_regFile_addrB := 0.U
  }.otherwise {
    fd_pc := pc.out
    fd_inst := io.imemOut
  }

  de_control := control.signals
  de_immGen := immGen.out
  de_pc := fd_pc
  de_regFile_dataA := regFile.dataA
  de_regFile_dataB := regFile.dataB
  de_regFile_addrD := fd_inst(11, 7)
  de_regFile_addrA := regFile.addrA
  de_regFile_addrB := regFile.addrB

  em_control := de_control
  em_alu := alu.out
  em_regFile_dataB := de_regFile_dataB
  em_pc := de_pc
  em_regFile_addrD := de_regFile_addrD

  mw_control := em_control
  mw_alu := em_alu
  mw_pc := em_pc
  mw_dmemDR := io.dmemDR
  mw_regFile_addrD := em_regFile_addrD

  // CS61c slide p.48, left to right, with mods
  // IF
  pc.sel := de_control.PCSel
  pc.in := alu.out
  pc.en := io.enable | step

  io.imemIn := pc.out
  io.pcValue := pc.out

  // ID
  regFile.addrA := fd_inst(19, 15)
  regFile.addrB := fd_inst(24, 20)
  regFile.addrD := mw_regFile_addrD
  regFile.dataD := wb
  regFile.regWEn := mw_control.RegWEn

  control.inst := fd_inst

  immGen.in := fd_inst
  immGen.sel := control.signals.ImmSel

  branchComp.a := regFile.dataA
  branchComp.b := regFile.dataB
  branchComp.brType := control.signals.BrType
  control.BrTaken := branchComp.taken

  // EX
  val dataA = Wire(UInt())
  val dataB = Wire(UInt())

  dataA := Mux(em_regFile_addrD === de_regFile_addrA && de_regFile_addrA =/= 0.U, em_alu,
    Mux(mw_regFile_addrD === de_regFile_addrA && de_regFile_addrA =/= 0.U, wb, de_regFile_dataA))
  dataB := Mux(em_regFile_addrD === de_regFile_addrB && de_regFile_addrB =/= 0.U, em_alu,
    Mux(mw_regFile_addrD === de_regFile_addrB && de_regFile_addrB =/= 0.U, wb, de_regFile_dataB))

  alu.a := Mux(de_control.ASel, de_pc, dataA)
  alu.b := Mux(de_control.BSel, de_immGen, de_regFile_dataB)
  alu.sel := de_control.ALUSel

  // MEM
  io.dmemA := em_alu
  io.dmemDW := em_regFile_dataB
  io.dmemWE := em_control.MemRW
  io.dmemL := em_control.MemLength
  io.dmemS := em_control.MemSign

  // WB
  wb := MuxLookup(mw_control.WBSel, 0.U(32.W), Array(
    Control.WB_DM -> mw_dmemDR,
    Control.WB_ALU -> mw_alu,
    Control.WB_PC4 -> (mw_pc + 4.U)
  ))
}
