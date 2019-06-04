package ustcrv.nexys4ddr.vga

import chisel3._
import chisel3.util._

import ustcrv.util._

class DataManagerIO extends Bundle {
  val enable = Input(Bool())
  val data = Input(UInt(32.W))

  val wAddr = Output(UInt(16.W))
  val wData = Output(UInt(8.W))
  val wEnable = Output(Bool())
}

class DataManager extends Module {
  val io = IO(new DataManagerIO)

  val posX = RegInit(0.U(8.W))
  val posY = RegInit(0.U(8.W))
  //val offsetY = RegInit(0.U(8.W)) // For scrolling
  io.wAddr := posY * 80.U + posX
  io.wData := Mux(io.data =/= 0x0A.U && (io.data <= 0x20.U || io.data >= 0x7F.U),
    0.U(8.W), io.data)
  io.wEnable := PosEdge(io.enable)

  val nextPosX = Mux(
    posX === 79.U || io.data === 0x0A.U,
    0.U, posX + 1.U)
  val nextPosY = Mux(
    posX === 79.U || io.data === 0x0A.U,
    Mux(posY === 29.U, 0.U, posY + 1.U), posY)

  println("TaoKY very strong!!!")
  when (io.enable) {
    posX := nextPosX
    posY := nextPosY
  }
}
