package ustcrv.nexys4ddr

import chisel3._
import chisel3.util._

import ustcrv.core.{Debug, Package}
import ustcrv.util._
import ustcrv.nexys4ddr

class MainIO extends Bundle {
  val seg = new SegmentOutput
  val SW = Input(UInt(16.W))
  val LED = Output(UInt(16.W))
  //val data = Output(UInt(32.W)) // For debugging
  val vga = new nexys4ddr.vga.VGADisplay
}

class MainWithClock(val freq: BigInt = 100000000) extends Module {
  val io = IO(new MainIO)
  val main = withClockAndReset (CPUClock(clock), reset) { Module(new Main(freq)) }.io
  io <> main
}

class Main(val freq: BigInt = 100000000) extends Module {
  val io = IO(new MainIO)

  // This thing itself is a state machine
  val state = RegInit(12.U(4.W))
  val action = RegInit(0.U(4.W))

  // Fixed storage
  val imem = Module(new IMemROM).io
  val dmem = Module(new DMemROM).io

  val debug = Module(new Package).io
  val seg = withClockAndReset(clock, false.B) { Module(new SegmentDisplay(freq / 1000)) }.io
  io.seg <> seg.out

  val dispAddr = Wire(UInt(32.W))
  val dispData = RegInit(0.U(32.W))
  val romAddr = RegInit(0.U(32.W))

  val dControl = WireInit(0.U(4.W)) // Equals to Debug.NOP
  val dEnable = RegInit(false.B)
  val dDataIn = WireInit(0.U(32.W))
  val dDataOut = WireInit(debug.dDataOut)
  //io.data := dispData // For debugging

  // TODO: Tidy up DMem and memory-mapped I/O devices, implement a unified memory interface
  val vga = Module(new nexys4ddr.vga.VGA).io
  io.vga <> vga.out
  vga.in.enable := debug.mEnable
  debug.mDataR := 0.U(32.W)
  vga.in.dataW := debug.mDataW
  vga.in.memRW := debug.mMode

  io.LED := (1.U << state)
  seg.numA := Mux(io.SW(15), dispData(31, 16), dispAddr(15, 0))
  seg.numB := dispData(15, 0)
  debug.pcEnable := io.SW(0)

  dispAddr := Cat(io.SW(12, 2), 0.U(2.W))

  imem.addr := romAddr
  dmem.addr := romAddr

  debug.dControl := dControl
  debug.dEnable := dEnable
  debug.dDataIn := dDataIn

  when (state === 0.U) {
    dEnable := false.B
    dControl := Debug.NOP
    action := 0.U // XXX

    when (PosEdge(io.SW(1), 4)) { // action: STEP
      action := Debug.STEP
      state := 1.U
    } .otherwise { // No action, fall back to reading mems
      state := 3.U
      when (io.SW(12)) { // 0x1000 is DMem
        action := Debug.DMEMRA
      } .otherwise { // IMem
        action := Debug.IMEMRA
      }
    }
  } .elsewhen (state < 12.U) {
    dControl := action
  }

  when (state === 1.U) {
    dEnable := true.B
    state := 2.U
  }
  when (state === 2.U) {
    dEnable := false.B
    dispData := dDataOut
    action := 0.U
    state := 0.U
  }
  when (state === 3.U) { // Start of read IMem/DMem
    dEnable := true.B
    dDataIn := dispAddr
    state := 4.U
  }
  when (state === 4.U) {
    dEnable := false.B
    dDataIn := dispAddr
    action := Mux(action === Debug.DMEMRA, Debug.DMEMRD, Debug.IMEMRD)
    state := 1.U
  }

  /*
   #######################################
   # Initialization: Write IMem and DMem #
   #######################################
   */

  // State 12: Write IMem address
  when (state === 12.U) {
    dControl := Debug.IMEMWA
    dDataIn := romAddr
    when (!dEnable) {
      dEnable := true.B
    } .otherwise {
      dEnable := false.B
      state := 13.U // Go write IMem data
      romAddr := romAddr - 1.U // Unknown issue, work around it
    }
  }
  // State 13: Write IMem data
  when (state === 13.U) {
    dControl := Debug.IMEMWD
    dDataIn := imem.data
    when (!dEnable) {
      dEnable := true.B
      when (romAddr === imem.d.U) {
        romAddr := 0x1000.U
        state := 14.U // Go write DMem address
      } .otherwise {
        romAddr := romAddr + 1.U // ROM has 32-bit word
      }
    } .otherwise {
      dEnable := false.B
    }
  }

  // State 14: Write DMem address
  when (state === 14.U) {
    dControl := Debug.DMEMWA
    dDataIn := romAddr
    when (!dEnable) {
      dEnable := true.B
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
      when (romAddr === (0x1000 + dmem.d).U) { // Less DMem data to write
        romAddr := 0.U
        state := 0.U // Normal
      } .otherwise {
        romAddr := romAddr + 1.U
      }
    } .otherwise {
      dEnable := false.B
    }
  }
}
