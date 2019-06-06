package ustcrv.nexys4ddr.romdata

import chisel3._
import chisel3.util._

import ustcrv.nexys4ddr.ROM

class IMemROM extends ROM(8, 32, "src/data/riscv.imem")
class DMemROM extends ROM(8, 32, "src/data/riscv.dmem")
