package ustcrv

import chisel3._

object OptionalPort {
  def apply(condition: Boolean, port: UInt): UInt = (if (condition) Some(port) else None) get
}
