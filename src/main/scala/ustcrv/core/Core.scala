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

  val step = io.pcStep && RegNext(io.pcStep)

  // Wires
  val wb = Wire(UInt(32.W))

  // Pipeline registers
  val fe_inst = RegInit(Instructions.NOP)
  val fe_pc = Reg(UInt())

  val de_control = Reg(Control.controlSignals)
  val de_regFile_dataA = Reg(UInt())
  val de_regFile_dataB = Reg(UInt())
  val de_pc = Reg(UInt())
  val de_immGen = Reg(UInt())

  val em_alu = Reg(UInt())

  // CS61c slide p.48, left to right, with mods
  // IF
  val pc = withClockAndReset (clock, reset.asBool | io.pcReset) {
    Module(new PC)
  }.io
  pc.sel := control.signals.PCSel
  pc.in := alu.out
  pc.en := io.enable | step

  io.imemIn := pc.out
  io.pcValue := pc.out

  fe_pc := pc.out
  fe_inst := io.imemOut

  // ID
  regFile.addrA := fe_inst(19, 15)
  regFile.addrB := fe_inst(24, 20)
  regFile.addrD := fe_inst(11, 7)
  regFile.dataD := wb
  regFile.regWEn := control.signals.RegWEn

  control.inst := fe_inst

  immGen.in := fe_inst
  immGen.sel := control.signals.ImmSel

  branchComp.a := regFile.dataA
  branchComp.b := regFile.dataB
  branchComp.brType := control.signals.BrType
  control.BrTaken := branchComp.taken

  de_control := control.signals
  de_immGen := immGen.out
  de_pc := fe_pc
  de_regFile_dataA := regFile.dataA
  de_regFile_dataB := regFile.dataB

  // EX
  alu.a := Mux(de_control.ASel, de_pc, de_regFile_dataA)
  alu.b := Mux(de_control.BSel, de_immGen, de_regFile_dataB)
  alu.sel := de_control.ALUSel

  // MEM
  io.dmemA := alu.out
  io.dmemDW := regFile.dataB
  io.dmemWE := control.signals.MemRW
  io.dmemL := control.signals.MemLength
  io.dmemS := control.signals.MemSign

  // WB
  wb := MuxLookup(control.signals.WBSel, 0.U(32.W), Array(
    Control.WB_DM -> io.dmemDR,
    Control.WB_ALU -> alu.out,
    Control.WB_PC4 -> (pc.out + 4.U)
  ))
}
