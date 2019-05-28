---
title: "Spike 模拟器"
tagline: "RISC-V Toolchain"
---

如何在我们自己的机器上模拟执行 RISC-V 程序？官方的工具链中对应的是 `Spike`（<https://github.com/riscv/riscv-isa-sim>）。简单使用 Spike 的程序执行已经在 [C 工具链]({{ page.dir }}/c) 中介绍过了，接下来记录一些其它有关 Spike 的内容。

\*在 learn-rv32i-asap 中，验证功能正确性的工具是 `riscv-fesvr`（RISC-V Frontend Server），但是对应链接 <https://github.com/riscv/riscv-fesvr> 表明这个工具已经融合进 Spike 中了。

Spike 可以调试程序，并且有几个项目均使用 Spike/Fesvr，结合 Verilator 验证 CPU 设计的正确性（Debug Sepc）。但是对于自己定义的地址空间来讲的话（比如说，想从 `0x0` 开始执行程序），Spike 的限制比较大，并且文档不全，比如说没有明确提到 Spike 自带的 bootloader 是从 `0x1000` 开始的，并且起始的入口点是硬编码在程序中的。

我调试了好几次都没有成功用 Spike 调试出来（一直遇到 `terminate called after throwing an instance of 'trap_store_access_fault'` 错误），可能和 <https://github.com/riscv/riscv-isa-sim/issues/192> 这个 issue 有关。

如果需要调试我们自己的程序，特别是加入 I/O 之后，有可能还需要自己写 simulator。
