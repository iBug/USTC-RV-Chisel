package ustcrv.core

import chisel3._
import chisel3.util._

class CoreIO extends Bundle {
}

class Core extends Module {
  val io = IO(new CoreIO)
  val regFile = Module(new RegisterFile(32, 5)).io
  val branchComp = Module(new BranchComp(32)).io

  val pc = DontCare
}
