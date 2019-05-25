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

  val inst = io.imemOut
  control.inst := inst

  // Wires
  val wb = Wire(UInt(32.W))

  // CS61c slide p.48, left to right, with mods
  pc.sel := control.PCSel
  pc.in := alu.out
  pc.en := io.enable | step

  io.imemIn := pc.out
  io.pcValue := pc.out

  regFile.addrA := inst(19, 15)
  regFile.addrB := inst(24, 20)
  regFile.addrD := inst(11, 7)
  regFile.dataD := wb
  regFile.regWEn := control.RegWEn

  immGen.in := inst
  immGen.sel := control.ImmSel

  branchComp.a := regFile.dataA
  branchComp.b := regFile.dataB
  branchComp.brType := control.BrType
  control.BrTaken := branchComp.taken

  alu.a := Mux(control.ASel, pc.out, regFile.dataA)
  alu.b := Mux(control.BSel, immGen.out, regFile.dataB)
  alu.sel := control.ALUSel

  io.dmemA := alu.out
  io.dmemDW := regFile.dataB
  io.dmemWE := control.MemRW

  wb := MuxLookup(control.WBSel, 0.U(32.W), Array(
    0.U -> io.dmemDR,
    1.U -> alu.out,
    2.U -> (pc.out + 4.U)
  ))
}
