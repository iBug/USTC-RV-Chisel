# USTC RISC-V

[![Build Status](https://travis-ci.org/iBug/USTC-RV-Chisel.svg?branch=master)](https://travis-ci.org/iBug/USTC-RV-Chisel) [![CircleCI](https://circleci.com/gh/iBug/USTC-RV-Chisel.svg?style=shield)](https://circleci.com/gh/iBug/USTC-RV-Chisel)

Chisel implementation of USTC RISC-V

## TODO List

- Hardware Design
  - Implement RISC-V RV32I ISA (user instructions)
  - Implement RISC-V RV32I ISA (privileged instructions)
  - Verify the design is running properly
  - Program implementation to Nexys4 DDR board
- Toolchain research
  - Find out C-to-bare-metal compilation route
  - Find out how to debug RISC-V programs
- Combined
  - Send compiled program to, and run on FPGA board
  - (Optional / Delayable) Find out how to debug FPGA from computer (may require additional hardware)
- More TBA

## Done list

- TBA

## References

- https://github.com/watz0n/learn-rv32i-asap
- https://cnrv.io/
