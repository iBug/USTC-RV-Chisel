package ustcrv.nexys4ddr

import chisel3._
import chisel3.util._

import ustcrv.core.Package

class MainIO extends Bundle {
}

class Main extends Module {
  val io = IO(new MainIO)

  val cpu = Module(new Package).io
}
