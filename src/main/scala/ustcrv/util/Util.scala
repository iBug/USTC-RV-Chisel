package ustcrv.util

import chisel3._

object OptionalPort {
  def apply(condition: Boolean, port: Any): Any = (if (condition) Some(port) else None) get
}

object PosEdge {
  def apply(signal: Bool): Bool = signal && !RegNext(signal)
}
