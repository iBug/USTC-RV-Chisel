package ustcrv.core

import chisel3._
import chisel3.util._
import chisel3.iotesters._

import scala.io.Source
import scala.util.control.Breaks._

class BranchSpec extends ChiselFlatSpec {
  "Branch" should "work" in {
    chisel3.iotesters.Driver.execute(Array(), () => new BranchComp) {
      c => new BranchTester(c)
    } should be (true)
  }
}

class BranchTester(val c: BranchComp) extends PeekPokeTester(c) {
  // Values: (a, b, EQ, NE, LT, GE, LTU, GEU)
  val Y = true
  val N = false
  val brTypes = List(Branch.EQ, Branch.NE, Branch.LT, Branch.GE, Branch.LTU, Branch.GEU)
  val testCases = List[(BigInt, BigInt, Array[Boolean])](
    (3, 3, Array(Y, N, N, Y, N, Y)),
    (3, 5, Array(N, Y, Y, N, Y, N)),
    (5, 3, Array(N, Y, N, Y, N, Y)),
    (5, -1, Array(N, Y, N, Y, Y, N)),
    (-1, 3, Array(N, Y, Y, N, N, Y)),
    (-3, -1, Array(N, Y, Y, N, Y, N)),
  )

  for ((a, b, results) <- testCases) {
    poke(c.io.a, a)
    poke(c.io.b, b)

    for ((brType, result) <- brTypes zip results) {
      poke(c.io.brType, brType.litValue)
      expect(c.io.taken, result)
    }
    poke(c.io.brType, Branch.XX.litValue)
    expect(c.io.taken, false)
  }
}
