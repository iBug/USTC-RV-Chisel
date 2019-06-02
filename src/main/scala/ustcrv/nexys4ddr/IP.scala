package ustcrv.nexys4ddr

import chisel3._

object CPUClock {
  def apply(in: Clock): Clock = {
    val m = Module(new CPU_clock).io
    m.in := in
    m.out
  }
}

class CPU_clock extends BlackBox {
  val io = IO(new Bundle {
    val in = Input(Clock())
    val out = Output(Clock())
  })
}
