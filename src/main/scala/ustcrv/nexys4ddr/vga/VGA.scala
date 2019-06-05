package ustcrv.nexys4ddr.vga

import chisel3._
import chisel3.util._

class VGADisplay extends Bundle {
  val VGA_HS = Output(Bool())
  val VGA_VS = Output(Bool())
  val VGA_R = Output(UInt(4.W))
  val VGA_G = Output(UInt(4.W))
  val VGA_B = Output(UInt(4.W))
}

class VGAData extends Bundle {
  val enable = Input(Bool())
  val dataR = Output(UInt(32.W))
  val dataW = Input(UInt(32.W))
  val memRW = Input(Bool())
}

class VGA extends Module {
  val io = IO(new Bundle {
    val in = new VGAData
    val out = new VGADisplay
  })

  val scanner = withClockAndReset (clock, false.B) { Module(new Scanner) } io
  val dm = Module(new DataManager) io
  val display = withClockAndReset (clock, false.B) { Module(new Display) } io
  val vram = Module(new BlockRAM(8, 2400)) io

  vram.wAddr := dm.wAddr
  vram.wData := dm.wData
  vram.wEnable := dm.wEnable
  dm.enable := io.in.enable && io.in.memRW
  dm.data := io.in.dataW
  io.in.dataR := 0.U(32.W) // Nothing to read as of now

  io.out <> display.out
  vram.rAddr := display.rAddr
  display.rData := vram.rData
}
