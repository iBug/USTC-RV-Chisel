package ustcrv.nexys4ddr

import chisel3._
import chisel3.util._

object SegmentDisplay {
  val D: Array[(UInt, UInt)] = Array(
    0.U -> "b0000001".U(7.W),
    1.U -> "b1001111".U(7.W),
    2.U -> "b0010010".U(7.W),
    3.U -> "b0000110".U(7.W),
    4.U -> "b1001100".U(7.W),
    5.U -> "b0100100".U(7.W),
    6.U -> "b0100000".U(7.W),
    7.U -> "b0001111".U(7.W),
    8.U -> "b0000000".U(7.W),
    9.U -> "b0000100".U(7.W),
    10.U -> "b0001000".U(7.W),
    11.U -> "b1100000".U(7.W),
    12.U -> "b0110001".U(7.W),
    13.U -> "b1000010".U(7.W),
    14.U -> "b0110000".U(7.W),
    15.U -> "b0111000".U(7.W)
  )

  def apply(input: UInt): UInt = MuxLookup(input, "b1111111".U(7.W), D)
}

class SegmentOutput extends Bundle {
  val SEG = Output(UInt(7.W))
  val DP = Output(Bool())
  val AN = Output(UInt(8.W))
}

class SegmentDisplayIO extends Bundle {
  val segmentOutput = new SegmentOutput
  val numA = Input(UInt(32.W))
  val numB = Input(UInt(32.W))
}

class SegmentDisplay(val clk: BigInt = 10000) extends Module {
  val io = IO(new SegmentDisplayIO)

  val count = RegInit(0.U(16.W))
  val pos = RegInit(0.U(3.W))
  val digits = WireInit(VecInit(
    io.numB(3, 0),
    io.numB(7, 4),
    io.numB(11, 8),
    io.numB(15, 12),
    io.numA(3, 0),
    io.numA(7, 4),
    io.numA(11, 8),
    io.numA(15, 12)
  ))

  io.segmentOutput.SEG := SegmentDisplay(digits(pos))
  io.segmentOutput.DP := true.B // no DP
  io.segmentOutput.AN := ~(1.U(8.W) << pos)

  when (count >= clk.U) {
    count := 0.U
    pos := pos + 1.U
  } .otherwise {
    count := count + 1.U
  }
}
