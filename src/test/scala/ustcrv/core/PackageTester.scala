package ustcrv.core

import chisel3._
import chisel3.util._
import chisel3.iotesters._

import java.nio.file.{Files, Paths}

class PackageTester(val c: Package) extends PeekPokeTester(c) {
  // Data Location: (pwd)/src/test/data
}
