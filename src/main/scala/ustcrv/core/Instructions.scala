// Imported from https://github.com/ucb-bar/riscv-mini/blob/master/src/main/scala/Instructions.scala

package ustcrv.core

import chisel3.UInt
import chisel3.util.BitPat

object Instructions {
  // Loads
  val LB     = BitPat("b?????????????????000?????0000011")
  val LH     = BitPat("b?????????????????001?????0000011")
  val LW     = BitPat("b?????????????????010?????0000011")
  val LBU    = BitPat("b?????????????????100?????0000011")
  val LHU    = BitPat("b?????????????????101?????0000011")
  // Stores
  val SB     = BitPat("b?????????????????000?????0100011")
  val SH     = BitPat("b?????????????????001?????0100011")
  val SW     = BitPat("b?????????????????010?????0100011")
  // Shifts
  val SLL    = BitPat("b0000000??????????001?????0110011")
  val SLLI   = BitPat("b0000000??????????001?????0010011")
  val SRL    = BitPat("b0000000??????????101?????0110011")
  val SRLI   = BitPat("b0000000??????????101?????0010011")
  val SRA    = BitPat("b0100000??????????101?????0110011")
  val SRAI   = BitPat("b0100000??????????101?????0010011")
  // Arithmetic
  val ADD    = BitPat("b0000000??????????000?????0110011")
  val ADDI   = BitPat("b?????????????????000?????0010011")
  val SUB    = BitPat("b0100000??????????000?????0110011")
  val LUI    = BitPat("b?????????????????????????0110111")
  val AUIPC  = BitPat("b?????????????????????????0010111")
  // Logical
  val XOR    = BitPat("b0000000??????????100?????0110011")
  val XORI   = BitPat("b?????????????????100?????0010011")
  val OR     = BitPat("b0000000??????????110?????0110011")
  val ORI    = BitPat("b?????????????????110?????0010011")
  val AND    = BitPat("b0000000??????????111?????0110011")
  val ANDI   = BitPat("b?????????????????111?????0010011")
  // Compare
  val SLT    = BitPat("b0000000??????????010?????0110011")
  val SLTI   = BitPat("b?????????????????010?????0010011")
  val SLTU   = BitPat("b0000000??????????011?????0110011")
  val SLTIU  = BitPat("b?????????????????011?????0010011")
  // Branches
  val BEQ    = BitPat("b?????????????????000?????1100011")
  val BNE    = BitPat("b?????????????????001?????1100011")
  val BLT    = BitPat("b?????????????????100?????1100011")
  val BGE    = BitPat("b?????????????????101?????1100011")
  val BLTU   = BitPat("b?????????????????110?????1100011")
  val BGEU   = BitPat("b?????????????????111?????1100011")
  // Jump & Link
  val JAL    = BitPat("b?????????????????????????1101111")
  val JALR   = BitPat("b?????????????????000?????1100111")
  // Synch
  val FENCE  = BitPat("b0000????????00000000000000001111")
  val FENCEI = BitPat("b00000000000000000001000000001111")
  // CSR Access
  val CSRRW  = BitPat("b?????????????????001?????1110011")
  val CSRRS  = BitPat("b?????????????????010?????1110011")
  val CSRRC  = BitPat("b?????????????????011?????1110011")
  val CSRRWI = BitPat("b?????????????????101?????1110011")
  val CSRRSI = BitPat("b?????????????????110?????1110011")
  val CSRRCI = BitPat("b?????????????????111?????1110011")
  // Change Level
  val ECALL  = BitPat("b00000000000000000000000001110011")
  val EBREAK = BitPat("b00000000000100000000000001110011")
  val ERET   = BitPat("b00010000000000000000000001110011")
  val WFI    = BitPat("b00010000001000000000000001110011")

  val NOP = BitPat.bitPatToUInt(BitPat("b00000000000000000000000000010011"))
}