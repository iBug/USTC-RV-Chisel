package ustcrv.core

import chisel3._
import chisel3.util._

// Package: Where the core, the memories and the debug module are connected

class PackageIO extends Bundle {
  // Debugger ports
  val dControl = Input(UInt(4.W))
  val dEnable = Input(Bool())
  val dDataIn = Input(UInt(32.W))
  val dDataOut = Output(UInt(32.W))
}

class Package extends Module {
  val io = IO(new PackageIO)

  val core = Module(new Core).io
  val imem = Module(new IMem(256, 0, true)).io
  val dmem = Module(new DMem(256, 0, true)).io
  val debugger = Module(new Debugger).io

  // Debugger first
  debugger.control := io.dControl
  debugger.enable := io.dEnable
  debugger.dataIn := io.dDataIn
  io.dDataOut := debugger.dataOut

  // Core and debugger
  core.enable := debugger.pcEnable
  core.pcReset := debugger.pcReset
  core.pcStep := debugger.pcStep
  debugger.pcValue := core.pcValue

  // Mem for debugger
  imem.dMode := debugger.idMode
  imem.drAddr := debugger.idrAddr
  imem.dwAddr := debugger.idwAddr
  debugger.idrData := imem.drData
  imem.dwData := debugger.idwData

  dmem.enable := true.B
  dmem.dAddr := debugger.daddr
  debugger.ddataR := dmem.drData
  dmem.dwData := debugger.ddataW
  dmem.dMode := debugger.dmemRW

  // Core and Memory
  imem.in := core.imemIn
  core.imemOut := imem.out
  dmem.addr := core.dmemA
  core.dmemDR := dmem.dataR
  dmem.dataW := core.dmemDW
  dmem.memRW := core.dmemWE
}
