package ustcrv.nexys4ddr.vga

import chisel3._
import chisel3.util.log2Ceil

class BlockRAM(val width: Int, val depth: Int) extends Module {
  val io = IO(new Bundle {
    val wAddr = Input(UInt(log2Ceil(depth)W))
    val wData = Input(UInt(width.W))
    val wEnable = Input(Bool())

    val rAddr = Input(UInt(log2Ceil(depth)W))
    val rData = Output(UInt(width.W))
  })

  val core = Module(new BlockRAM_IP(width, depth)).io
  // TODO: Connect wires
  core.clka := clock
  core.clkb := clock
  core.ena := true.B
  core.enb := true.B
  core.addra <> io.wAddr
  core.dina <> io.wData
  core.wea <> io.wEnable
  core.addrb <> io.rAddr
  core.doutb <> io.rData
}

class BlockRAM_IP(val width: Int, val depth: Int) extends BlackBox {
  val io = IO(new Bundle {
    val clka = Input(Clock())
    val addra = Input(UInt(log2Ceil(depth)W))
    val dina = Input(UInt(width.W))
    val ena = Input(Bool())
    val wea = Input(Bool())

    val clkb = Input(Clock())
    val addrb = Input(UInt(log2Ceil(depth)W))
    val doutb = Output(UInt(width.W))
    val enb = Input(Bool())
  })
}
