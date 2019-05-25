package ustcrv

import chisel3._

object OptionalPort {
  def apply(condition: Boolean, port: Any): Any = (if (condition) Some(port) else None) get
}
