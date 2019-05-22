package ustcrv.core

import chisel3._
import chisel3.util._
import chisel3.iotesters._

import scala.io.Source

class PackageTester(val c: Package) extends PeekPokeTester(c) {
  // Data Location: (pwd)/src/test/data

  // Load data
  val iData = Soruce.fromFile("src/test/data/riscv.imem").getLines.map(BigInt(s, 16)).toList
  val dData = Soruce.fromFile("src/test/data/riscv.dmem").getLines.map(BigInt(s, 16)).toList

  // Merge
}
