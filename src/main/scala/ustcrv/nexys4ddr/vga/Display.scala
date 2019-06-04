package ustcrv.nexys4ddr.vga

import chisel3._
import chisel3.util._

class Display extends Module {
  val io = IO(new Bundle {
    val out = new VGADisplay

    val rAddr = Output(UInt(16.W))
    val rData = Input(UInt(8.W))
  })

  val scanner = Module(new Scanner)io
  io.out.VGA_HS := scanner.hs
  io.out.VGA_VS := scanner.vs

  val pixel = Wire(Bool())
  val outputPixel = Mux(scanner.en, pixel, false.B)
  val outputSignal = Cat(outputPixel, outputPixel, outputPixel, outputPixel)
  io.out.VGA_R := outputSignal
  io.out.VGA_G := outputSignal
  io.out.VGA_B := outputSignal

  io.rAddr := 80.U * (scanner.y >> 4.U) + (scanner.x >> 3.U)
  val charmap = Charmap(io.rData)
  pixel := charmap(scanner.y(3, 0))(scanner.x(2, 0))
}
