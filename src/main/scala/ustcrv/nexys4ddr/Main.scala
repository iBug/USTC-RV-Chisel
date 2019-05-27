package ustcrv.nexys4ddr

import chisel3._
import chisel3.util._

import ustcrv.core.{Debug, Package}

class MainIO extends Bundle {
  val seg = new SegmentDisplayIO
  val SW = Input(UInt(16.W))
}

class Main extends Module {
  val io = IO(new MainIO)

  // This thing itself is a state machine
  val state = RegInit(15.U(4.W))
  val action = RegInit(0.U(4.W))

  // Fixed storage
  val imem = Module(new IMemROM).io
  val dmem = Module(new DMemROM).io

  val debug = Module(new Package).io
  val seg = Module(new SegmentDisplay).io
  io.seg <> seg.out

  val dispAddr = Wire(UInt(32.W))
  val dispData = RegInit(0.U(32.W))
  val romAddr = RegInit(0.U(32.W))
  val romData = Wire(UInt(32.W))

  val dControl = WireInit(0.U(4.W)) // Equals to Debug.NOP
  val dEnable = RegInit(false.B)
  val dDataIn = WireInit(0.U(32.W))
  val dDataOut = WireInit(debug.dDataOut)

  dispAddr := Cat(io.SW(12, 2), 0.U(2.W))
  romData := DontCare

  imem.addr := romAddr
  dmem.addr := romAddr

  debug.dControl := dControl
  debug.dEnable := dEnable
  debug.dDataIn := dDataIn

  when (state === 0.U) {
    action := 0.U // XXX
  }

  // actions?

  /*
   #######################################
   # Initialization: Write IMem and DMem #
   #######################################
   */

  // State 12: Write IMem address
  when (state === 12.U) {
    dControl := Debug.IMEMWA
    when (!dEnable) {
      dEnable := true.B
      dDataIn := romAddr
    } .otherwise {
      dEnable := false.B
      state := 13.U // Go write IMem data
    }
  }
  // State 13: Write IMem data
  when (state === 13.U) {
    dControl := Debug.IMEMWD
    dDataIn := imem.data
    when (!dEnable) {
      dEnable := true.B
    } .otherwise {
      dEnable := false.B
      when (romAddr === 100.U) {
        romAddr := 0x1000.U
        state := 14.U // Go write DMem address
      } .otherwise {
        romAddr := romAddr + 1.U // ROM has 32-bit word
      }
    }
  }

  // State 14: Write DMem address
  when (state === 14.U) {
    dControl := Debug.DMEMWA
    when (!dEnable) {
      dEnable := true.B
      dDataIn := romAddr
    } .otherwise {
      dEnable := false.B
      state := 15.U // Go write DMem data
    }
  }
  // State 15: Write DMem data
  when (state === 15.U) {
    dControl := Debug.DMEMWD
    dDataIn := dmem.data
    when (!dEnable) {
      dEnable := true.B
    } .otherwise {
      dEnable := false.B
      when (romAddr === 0x1010.U) { // Less DMem data to write
        romAddr := 0.U
        state := 0.U // Normal
      } .otherwise {
        romAddr := romAddr + 1.U
      }
    }
  }
}
