package ustcrv.nexys4ddr

import chisel3._
import chisel3.util._
import chisel3.iotesters._

import org.scalatest.{Matchers, FlatSpec}

class MainSpec extends FlatSpec with Matchers {
  behavior of "MainSpec"

  it should "work" in {
    chisel3.iotesters.Driver(() => new Main) {
      c => new MainTester(c)
    } should be(true)
  }
}

object MainTests extends App {
  val a = Array("--display-base", "16")
  chisel3.iotesters.Driver.execute(a, () => new Main) {
    c => new MainTester(c)
  }
}

class MainTester(val c: Main) extends PeekPokeTester(c) {
  poke(c.io.SW, 4 * 0)
  step(240)
  expect(c.io.data, 0x5B26)
  for (n <- 0 until 10) {
    expect(c.io.LED, 16) // will fail
    step(1)
  }
}
