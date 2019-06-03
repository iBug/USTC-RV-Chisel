package ustcrv.nexys4ddr.vga

import chisel3._
import chisel3.util._

class VGAOutput extends Bundle {
  val VGA_HS = Output(Bool())
  val VGA_VS = Output(Bool())
  val VGA_R = Output(UInt(4.W))
  val VGA_G = Output(UInt(4.W))
  val VGA_B = Output(UInt(4.W))
}

class VGAData extends Bundle {
}

class VGA extends Module {
  val io = IO(new Bundle {
    val data = new VGAData
    val out = new VGAOutput
  })

  val scanner = Module(new Scanner).io
}
