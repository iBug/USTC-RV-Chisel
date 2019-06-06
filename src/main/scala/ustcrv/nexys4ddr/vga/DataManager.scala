package ustcrv.nexys4ddr.vga

import chisel3._
import chisel3.util._

class DataManagerIO extends Bundle {
  val enable = Input(Bool())
  val data = Input(UInt(32.W))

  val wAddr = Output(UInt(16.W))
  val wData = Output(UInt(8.W))
  val wEnable = Output(Bool())
}

class DataManager extends Module {
  val io = IO(new DataManagerIO)

  val enable = io.enable //PosEdge(io.enable)
  val data = io.data(7, 0) //RegNext(io.data)
  val posX = RegInit(0.U(8.W))
  val posY = RegInit(0.U(8.W))
  //val offsetY = RegInit(0.U(8.W)) // For scrolling
  io.wAddr := posY * 80.U + posX
  io.wData := Mux(data <= 0x20.U || data >= 0x7F.U,
    0.U(8.W), data)
  io.wEnable := enable

  val newline = posX === 79.U || io.data === 0x0A.U
  val nextPosX = Mux(newline, 0.U, posX + 1.U)
  val nextPosY = Mux(newline,
    Mux(posY === 29.U, 0.U, posY + 1.U), posY)

  when (enable) {
    posX := nextPosX
    posY := nextPosY
  }
}
