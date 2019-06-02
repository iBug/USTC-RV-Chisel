package ustcrv.nexys4ddr.vga

import chisel3._
import chisel3.util._

class Scanner(val hd: Int = 640, val hf: Int = 16, val hs: Int = 96, val hb: Int = 48, val vd: Int = 480, val vf: Int = 10, val vs: Int = 2, val vb: Int = 31, val w: Int = 12, val cd: Int = 16384, val cw: Int = 16) extends Module {
  val io = IO(new Bundle {
    val hs = Output(Bool())
    val vs = Output(Bool())
    val en = Output(Bool())
    val x = Output(UInt(w.W))
    val y = Output(UInt(w.W))
  })

  val CD = RegInit(0.U(cw.W))
  val HD = RegInit(0.U(w.W))
  val VD = RegInit(0.U(w.W))
  val CDNext = WireInit(CD +& cd.U)
  val PE = RegNext(CDNext(cw))
  CD := CDNext(cw - 1, 0)

  val hm = hd + hf + hs + hb
  val vm = vd + vf + vs + vb

  io.en := (HD < hd.U) && (VD < vd.U)
  io.x := Mux(io.en, HD, 0.U)
  io.y := Mux(io.en, VD, 0.U)
  io.hs := ~((HD >= (hd + hf).U) && (HD < (hd + hf + hs).U))
  io.vs := ~((VD >= (vd + vf).U) && (VD < (vd + vf + vs).U))

  when (PE) {
    when (HD >= (hm - 1).U) {
      HD := 0.U
      when (VD >= (vm - 1).U) {
        VD := 0.U
      } .otherwise {
        VD := VD + 1.U
      }
    } .otherwise {
      HD := HD + 1.U
    }
  }
}
