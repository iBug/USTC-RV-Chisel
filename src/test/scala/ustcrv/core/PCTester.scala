package ustcrv.core

import chisel3._
import chisel3.util._
import chisel3.iotesters._

import scala.io.Source
import scala.util.control.Breaks._
import org.scalatest.{Matchers, FlatSpec}

class PCSpec extends ChiselFlatSpec {
  behavior of "PCSpec"

  it should "work" in {
    chisel3.iotesters.Driver(() => new PC) {
      c => new PCTester(c)
    } should be(true)
  }
}

class PCTester(val c: PC) extends PeekPokeTester(c) {
  reset _
  expect(c.io.out, 0)

  step(1)
  expect(c.io.out, 0)

  poke(c.io.en, true)
  poke(c.io.sel, 1)
  poke(c.io.in, 2333)
  step(1)
  expect(c.io.out, 2333)
  step(1)
  expect(c.io.out, 2333)

  poke(c.io.sel, 0)
  step(1)
  expect(c.io.out, 2337)
  step(1)
  expect(c.io.out, 2341)

  poke(c.io.en, false)
  step(1)
  expect(c.io.out, 2341)
  step(1)
  expect(c.io.out, 2341)
}
