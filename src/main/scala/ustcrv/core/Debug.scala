package ustcrv.core

import chisel3._
import chisel3.util._

object Debug {
  val NOP = 0.U
  val STEP = 1.U
  val STOP = 2.U
  val START = 3.U
  val RESET = 7.U
  val IMEMRA = 8.U
  val IMEMWA = 9.U
  val IMEMRD = 10.U
  val IMEMWD = 11.U
  val DMEMRA = 12.U
  val DMEMWA = 13.U
  val DMEMRD = 14.U
  val DMEMWD = 15.U
}

class DebugIO extends Bundle {
  // Inputs
  val enable = Input(Bool())
  val control = Input(UInt(4.W)) // XXX: Expand this signal?
  val dataIn = Input(UInt(32.W))
  val dataOut = Output(UInt(32.W))

  // Signals (I and O)
  // PC
  val pcEnable = Output(Bool())
  val pcReset = Output(Bool())
  val pcStep = Output(Bool())
  val pcValue = Input(UInt(32.W))
  // IMem
  val idMode = Output(Bool())
  val idrAddr = Output(UInt(32.W))
  val idwAddr = Output(UInt(32.W))
  val idrData = Input(UInt(32.W))
  val idwData = Output(UInt(32.W))
  // DMem
  val denable = Output(Bool())
  val daddr = Output(UInt(32.W))
  val ddataR = Input(UInt(32.W))
  val ddataW = Output(UInt(32.W))
  val dmemRW = Output(Bool())
}

class Debugger extends Module {
  val io = IO(new DebugIO)
  val enable = io.enable
  val update = enable && !RegNext(enable)
  val op = io.control
  val dataIn = io.dataIn
  val dataOut = RegInit(0.U(32.W))

  val cpuEnable = RegInit(false.B)
  val imemRA = RegInit(0.U(32.W))
  val imemWA = RegInit(0.U(32.W))
  val idMode = RegInit(false.B)
  val dmemA = RegInit(0.U(32.W))
  val ddMode = RegInit(false.B)

  io.pcEnable := cpuEnable
  io.dataOut := dataOut

  io.idMode := idMode
  io.idrAddr := imemRA
  io.idwAddr := imemWA
  io.idwData := dataIn

  io.denable := true.B // looks like this can't be avoided
  io.daddr := dmemA
  io.ddataW := dataIn
  io.dmemRW := ddMode

  io.pcStep := false.B
  io.pcReset := false.B
  when (update) {
    when (op === Debug.NOP) {
      // Do nothing
    } .elsewhen (op === Debug.STEP) {
      cpuEnable := false.B
      io.pcStep := true.B
    } .elsewhen (op === Debug.START) {
      cpuEnable := true.B
    } .elsewhen (op === Debug.STOP) {
      cpuEnable := false.B
    } .elsewhen (op === Debug.STOP) {
      io.pcReset := true.B
    } .elsewhen (op === Debug.IMEMRA) {
      imemRA := dataIn
    } .elsewhen (op === Debug.IMEMWA) {
      imemWA := dataIn
    } .elsewhen (op === Debug.IMEMRD) {
      dataOut := io.idrData
    } .elsewhen (op === Debug.IMEMWD) {
      // Write data is sent via combinational circuit
      idMode := true.B
    } .elsewhen (op === Debug.DMEMRA) {
      dmemA := dataIn
    } .elsewhen (op === Debug.DMEMWA) {
      dmemA := dataIn
    } .elsewhen (op === Debug.DMEMRD) {
      dataOut := io.ddataR
    } .elsewhen (op === Debug.DMEMWD) {
      // Write data is sent via combinational circuit
      ddMode := true.B
    }
  } .elsewhen (!enable) {
    idMode := false.B
    ddMode := false.B
  }
}
