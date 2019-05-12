package ustcrv.core

import chisel3._
import chisel3.util._

// Package: Where the core, the memories and the debug module are connected

class PackageIO extends Bundle {
}

class Package extends Module {
  val io = IO(new PackageIO)

  val imem = Mem(256, UInt(32.W))
  val dmem = Mem(256, UInt(32.W))
}
