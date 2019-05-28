package ustcrv.util

import chisel3._
import chisel3.util._

object OptionalPort {
  def apply(condition: Boolean, port: Any): Any = (if (condition) Some(port) else None) get
}

object PosEdge {
  def apply(signal: Bool, n: Int): Bool = signal && !ShiftRegister(signal, n)
}
