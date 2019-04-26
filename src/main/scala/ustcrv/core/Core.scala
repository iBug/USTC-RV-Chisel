package ustcrv.core

import chisel3._
import chisel3.util._

class CoreIO extends Bundle {
  // hmmmm
}

class Core extends Module {
  val io = IO(new CoreIO)
  val control = Module(new Control).io

  val alu = Module(new ALU).io
  val regFile = Module(new RegisterFile(32, 5)).io
  val branchComp = Module(new BranchComp(32)).io
  val immGen = Module(new ImmGen).io
  val pc = Module(new PC).io

  val imem = Module(new IMem).io
  val dmem = Module(new DMem).io

  val inst = imem.out

  // Wires
  val wb = Wire(UInt(32.U))

  // CS61c slide p.48, left to right
  pc.sel := control.PCSel
  pc.in := alu.out
  pc.en := true.B

  imem.in := pc.out

  regFile.addrA := inst(19, 15)
  regFile.addrB := inst(24, 20)
  regFile.addrD := inst(11, 7)
  regFile.dataD := wb

  immGen.in := inst
  immGen.sel := control.ImmSel

  branchComp.a := regFile.dataA
  branchComp.b := regFile.dataB
  branchComp.brType := control.BrType
  control.BrTaken := branchComp.taken

  alu.a := Mux(control.ASel, pc.out, regFile.dataA)
  alu.b := Mux(control.BSel, immGen.out, regFile.dataB)
  alu.sel := control.ALUSel

  dmem.addr := alu.out
  dmem.dataW := regFile.dataB
  dmem.memRW := control.MemRW

  wb := MuxLookup(control.WBSel, 0.U(32.W), Array(
    0.U -> (pc.out + 4.U),
    1.U -> alu.out,
    2.U -> dmem.dataR
  ))
}
