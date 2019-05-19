package ustcrv.core

import chisel3._
import chisel3.util._

// Package: Where the core, the memories and the debug module are connected

class PackageIO extends Bundle {
  // Hmm
}

class Package extends Module {
  val io = IO(new PackageIO)

  val core = Module(new Core).io
  val imem = Mem(256, UInt(32.W))
  val dmem = Mem(256, UInt(32.W))
  val debugger = Module(new Debugger).io

  // Debugger first
  core.enable := debugger.pcEnable
  core.pcReset := debugger.pcReset
  debugger.pcValue := core.pcValue

  // Mem for debugger
  imem.dMode := debugger.idMode
  imem.drAddr := debugger.idrAddr
  imem.dwAddr := debugger.idwAddr
  debugger.idrData := imem.drData
  imem.dwData := debugger.idwData

  dmem.dAddr := debugger.daddr
  debugger.ddataR := dmem.drData
  dmem.dwData := debugger.ddataW
  dmem.dMode := debugger.dmemRW

  // Core and Memory
  imem.in := core.imemIn
  core.imemOut := imem.out
  dmem.addr := core.addr
  core.dataR := dmem.dataR
  dmem.dataW := core.dataW
  dmem.memRW := core.dmemWE
}
