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

  // Ports deprived from debugger
  val pcEnable = Input(Bool())
  val pcValue = Output(UInt(32.W))

  // Memory-mapped I/O interface
  val mEnable = Output(Bool())
  val mAddr = Output(UInt(32 W))
  val mDataR = Input(UInt(32 W))
  val mDataW = Output(UInt(32 W))
  val mMode = Output(Bool()) // 0-read, 1-write
}

class Package extends Module {
  val io = IO(new PackageIO)

  val core = Module(new Core).io
  val imem = Module(new IMem(256, 0, true)).io
  val dmem = Module(new DMem(256, 0x1000, true)).io
  val debugger = Module(new Debugger).io

  // Debugger first
  debugger.control := io.dControl
  debugger.enable := io.dEnable
  debugger.dataIn := io.dDataIn
  io.dDataOut := debugger.dataOut

  // Core and debugger
  core.enable := io.pcEnable // Used to be managed by debugger
  core.pcReset := debugger.pcReset
  core.pcStep := debugger.pcStep
  debugger.pcValue := core.pcValue
  io.pcValue := debugger.pcValue

  // Mem for debugger
  imem.dMode := debugger.idMode
  imem.drAddr := debugger.idrAddr
  imem.dwAddr := debugger.idwAddr
  debugger.idrData := imem.drData
  imem.dwData := debugger.idwData

  dmem.dEnable := debugger.denable
  dmem.dAddr := debugger.daddr
  debugger.ddataR := dmem.drData
  dmem.dwData := debugger.ddataW
  dmem.dMode := debugger.dmemRW

  // Core and Memory
  imem.in := core.imemIn
  core.imemOut := imem.out
  dmem.enable := core.dmemA >= 0x1000.U && core.dmemA < 0x2000.U
  dmem.addr := core.dmemA
  core.dmemDR := dmem.dataR
  dmem.dataW := core.dmemDW
  dmem.memRW := core.dmemWE
  dmem.length := core.dmemL
  dmem.sign := core.dmemS

  // Memmap
  io.mEnable := core.dmemA >= 0x2000.U // I/O mapping range
  io.mAddr := core.dmemA
  //io.mDataR ???
  io.mDataW := core.dmemDR
  io.mMode := core.dmemWE
}
