package ustcrv.core

import chisel3._
import chisel3.util._

// w: width, s: step
class NPCGenerator(val w: Int = 32, val s: Int = 4) extends Module {
  val io = IO(new Bundle {
    val out     = Output(UInt(w.W))
    val pc      = Input(UInt(w.W))
    val jalTarget = Input(UInt(w.W))
    val jalrTarget = Input(UInt(w.W))
    val brTarget = Input(UInt(w.W))
    val jal = Input(Bool())
    val jalr = Input(Bool())
    val br = Input(Bool())
  })

  when (io.jalr) {
    io.out := io.jalrTarget
  } .elsewhen (io.br) {
    io.out := io.brTarget
  } .elsewhen (io.jal) {
    io.out := io.jalTarget
  } .otherwise {
    io.out := io.pc
  }
}
