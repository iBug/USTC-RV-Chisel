package ustcrv.core

import chisel3._
import chisel3.util._

class PackageIO extends Bundle {
}

class Package extends Module {
  val io = IO(new PackageIO)
}
