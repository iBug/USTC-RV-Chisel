package ustcrv.core

import chisel3._
import chisel3.util._
import chisel3.iotesters._

import scala.io.Source
import scala.util.control.Breaks._
import org.scalatest.{Matchers, FlatSpec}

class PackageSpec extends FlatSpec with Matchers {
  behavior of "PackageSpec"

  it should "work" in {
    chisel3.iotesters.Driver(() => new Package) {
      c => new PackageTester(c)
    } should be(true)
  }
}

object PackageTests extends App {
  val a = Array("--display-base", "16")
  chisel3.iotesters.Driver.execute(a, () => new Package) {
    c => new PackageTester(c)
  }
}

class PackageTester(val c: Package) extends PeekPokeTester(c) {
  // Data Location: (pwd)/src/test/data

  // Load data
  val iData = Source.fromFile("src/data/riscv.imem").getLines.map(BigInt(_, 16)).toList
  val dData = Source.fromFile("src/data/riscv.dmem").getLines.map(BigInt(_, 16)).toList

  // For convenience
  def debugStep = {
    poke(c.io.dEnable, true)
    step(1)
    poke(c.io.dEnable, false)
    step(1)
  }

  // Initialize
  poke(c.io.pcEnable, false)

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

  // Write to DMem
  poke(c.io.dControl, Debug.DMEMWA)
  poke(c.io.dDataIn, 4096)
  debugStep
  poke(c.io.dControl, Debug.DMEMWD)
  for (data <- dData) {
    poke(c.io.dDataIn, data)
    debugStep
  }

  // Read from DMem
  poke(c.io.dControl, Debug.DMEMRA)
  poke(c.io.dDataIn, 4096)
  debugStep
  poke(c.io.dControl, Debug.DMEMRD)
  for (data <- dData) {
    debugStep
    expect(c.io.dDataOut, data)
  }

  // Run the CPU
  poke(c.io.dControl, Debug.READPC)
  debugStep
  println("CPU start")
  poke(c.io.pcEnable, true)
  for (n <- 0 until 2000) {
    step(1)
    if (peek(c.io.mEnable) != 0 && peek(c.io.mMode) != 0 && peek(c.io.mAddr) == 0x2000) {
      val addr = peek(c.io.mAddr)
      val data = peek(c.io.mDataW)
      val pc = peek(c.io.pcValue)
      System.out.printf(f"Write at 0x${pc}%X: 0x${addr}%X: (${data toChar}%c) ${data}%d\n")
    }
  }
  poke(c.io.pcEnable, false)
  println("CPU stop")

  // Check DMem stuff
  poke(c.io.dControl, Debug.DMEMRA)
  poke(c.io.dDataIn, 0x1018)
  debugStep
  poke(c.io.dControl, Debug.DMEMRD)
  for (i <- 0 until 20) {
    debugStep
    //System.out.printf(f"${peek(c.io.dDataOut)}%d\n")
    expect(c.io.dDataOut, i * i)
  }

  // Check stack
  poke(c.io.dControl, Debug.DMEMRA)
  poke(c.io.dDataIn, 0x1FFC)
  debugStep
  poke(c.io.dControl, Debug.DMEMRD)
  debugStep
  expect(c.io.dDataOut, 0x0C)
}
