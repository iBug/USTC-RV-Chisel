package ustcrv.core

import chisel3._
import chisel3.util._
import chisel3.iotesters._

import scala.io.Source
import org.scalatest.{Matchers, FlatSpec}

class PackageSpec extends FlatSpec with Matchers {
  behavior of "PackageSpec"

  it should "work" in {
    chisel3.iotesters.Driver(() => new Package) {
      c => new PackageTester(c)
    } should be(true)
  }
}

class PackageTester(val c: Package) extends PeekPokeTester(c) {
  // Data Location: (pwd)/src/test/data

  // Load data
  val iData = Source.fromFile("src/test/data/riscv.imem").getLines.map(BigInt(_, 16)).toList
  val dData = Source.fromFile("src/test/data/riscv.dmem").getLines.map(BigInt(_, 16)).toList

  // For convenience
  def debugStep = {
    poke(c.io.dEnable, true)
    step(1)
    poke(c.io.dEnable, false)
    step(1)
  }

  // Write to IMem
  poke(c.io.dControl, Debug.IMEMWA)
  poke(c.io.dDataIn, 0)
  debugStep
  poke(c.io.dControl, Debug.IMEMWD)
  for (data <- iData) {
    poke(c.io.dDataIn, data)
    debugStep
  }

  // Read from IMem
  poke(c.io.dControl, Debug.IMEMRA)
  poke(c.io.dDataIn, 0)
  debugStep
  poke(c.io.dControl, Debug.IMEMRD)
  for (data <- iData) {
    debugStep
    expect(c.io.dDataOut, data)
  }
}
