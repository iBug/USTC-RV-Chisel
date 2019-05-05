---
title: "Spike 模拟器"
tagline: "RISC-V Toolchain"
---

如何在我们自己的机器上模拟执行 RISC-V 程序？官方的工具链中对应的是 `Spike`（<https://github.com/riscv/riscv-isa-sim>）。简单使用 Spike 的程序执行已经在 [C 工具链](./C.md) 中介绍过了，接下来记录一些其它有关 Spike 的内容。

\*在 learn-rv32i-asap 中，验证功能正确性的工具是 `riscv-fesvr`（RISC-V Frontend Server），但是对应链接 <https://github.com/riscv/riscv-fesvr> 表明这个工具已经融合进 Spike 中了。

## 使用 Spike 验证设计正确性

Spike 可以调试程序，并且有几个项目均使用 Spike/Fesvr，结合 Verilator 验证 CPU 设计的正确性。

// TODO