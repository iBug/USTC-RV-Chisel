package ustcrv.nexys4ddr.vga

import chisel3._
import chisel3.util.log2Ceil

class BlockRAM(val wData: Int, val depth: Int) extends Module {
  val io = IO(new Bundle {
    val wAddr = Input(UInt(log2Ceil(depth)W))
    val wData = Input(UInt(wData.W))
    val wEnable = Input(Bool())

    val rAddr = Input(UInt(log2Ceil(depth)W))
    val rData = Output(UInt(wData.W))
  })

  val core = Module(new BlockRAM_IP(wData, depth)).io
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

class BlockRAM_IP(val wData: Int, val depth: Int) extends BlackBox {
  val io = IO(new Bundle {
    val clka = Input(Clock())
    val addra = Input(UInt(log2Ceil(depth)W))
    val dina = Input(UInt(wData.W))
    val ena = Input(Bool())
    val wea = Input(Bool())

    val clkb = Input(Clock())
    val addrb = Input(UInt(log2Ceil(depth)W))
    val doutb = Output(UInt(wData.W))
    val enb = Input(Bool())
  })
}
