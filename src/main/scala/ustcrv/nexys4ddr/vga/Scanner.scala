package ustcrv.nexys4ddr.vga

import chisel3._
import chisel3.util._

class Scanner(val hd: Int = 640, val hf: Int = 16, val hs: Int = 96, val hb: Int = 48, val vd: Int = 480, val vf: Int = 10, val vs: Int = 2, val vb: Int = 31, val w: Int = 12) extends Module {
  val io = IO(new Bundle {
    val hs = Output(Bool())
    val vs = Output(Bool())
    val en = Output(Bool())
    val x = Output(UInt(w.W))
    val y = Output(UInt(w.W))
  })

  val HD = RegInit(0.U(w.W))
  val VD = RegInit(0.U(w.W))

  val hm = (hd + hf + hs + hb - 1)U
  val vm = (vd + vf + vs + vb - 1)U

  io.en := (HD < hd.U) && (VD < vd.U)
  io.x := Mux(io.en, HD, 0.U)
  io.y := Mux(io.en, VD, 0.U)
  io.hs := ~((HD >= (hd + hf).U) && (HD < (hd + hf + hs).U))
  io.vs := ~((VD >= (vd + vf).U) && (VD < (vd + vf + vs).U))

  when (HD >= hm) {
    HD := 0.U
    when (VD >= vm) {
      VD := 0.U
    } .otherwise {
      VD := VD + 1.U
    }
  } .otherwise {
    HD := HD + 1.U
  }
}
