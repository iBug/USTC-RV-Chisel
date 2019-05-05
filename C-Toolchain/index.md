---
title: "RISC-V 官方 C 工具链相关笔记"
description: "RISC-V C Toolchain"
---

RISC-V 官方的 C 工具链可以在 <https://github.com/freechipsproject/rocket-tools> 找到。在国内需要加速下载（clone submodule 很慢）的话可以使用 https://github.com/cnrv/clone-helpers/ 辅助。

编译需要的时间比较长。注意：如果需要生成 RV32 的程序，使用脚本 `./build-rv32ima.sh`（而非 `./build.sh`）。下面都以 RV32I 指令集为例。

## 程序的编译

Hello world 程序通常被认为是最简单的样例程序。

```c
#include <stdio.h>

int main(void) {
        printf("Hello, world!\n");
        return 0;
}
```

默认情况下，编译器会做怎样的处理呢？进行编译。

```
$RISCV/bin/riscv32-unknown-elf-gcc -march=rv32i helloworld.c
```

反汇编。

```
$RISCV/bin/riscv32-unknown-elf-objdump -d a.out
```

观察生成的结果可以发现：

- 默认情况下，编译器把整个 C 库（newlib）都塞到了编译的程序中（就算根本没有用到）
- C 库带来了一些不属于 RV32I 指令集的指令（比如说 `div`）
- 并且对于我们的项目来说，它的 `printf()` 会把数据输出到哪里去呢？这里需要我们自己处理。

如果需要模拟执行我们的程序，一种办法是使用 `spike`（<https://github.com/riscv/riscv-isa-sim/>）。

```
$ $RISCV/bin/spike -m128 pk a.out
Hello, world!
```

`-m128` 参数指定了占用的内存大小。`spike` 默认占用 2GB 的内存，如果内存大小不足，启动会出错。而 `pk` 是 <https://github.com/riscv/riscv-pk/>，一个轻量级的，可运行静态链接的 RISCV ELF 应用程序的环境（Proxy Kernel）。

如果不想要链接 C 库，需要参数 `-nostdlib`。

现在，我们以下面这个程序作为示例。

```
int global_init_var = 23;
int global_array[32];

int func1(int i)
{
        return i + global_array[i] + global_init_var;
}

int main(void)
{
        static int static_var = 24;

        int a = 1;

        int array[64];

        array[32] = func1(static_var + global_array[0] + a);

        return a;
}
```

编译时由于没有了 C 库，会出现没有入口点的警告。我们将 `main()` 改名为 `_start()`。观察生成的可执行文件。

```
00010074 <func1>:
   10074:       fe010113                addi    sp,sp,-32
   10078:       00812e23                sw      s0,28(sp)
   1007c:       02010413                addi    s0,sp,32
   10080:       fea42623                sw      a0,-20(s0)
   10084:       000117b7                lui     a5,0x11
   10088:       fec42703                lw      a4,-20(s0)
   1008c:       00271713                slli    a4,a4,0x2
   10090:       12478793                addi    a5,a5,292 # 11124 <_edata>
   10094:       00f707b3                add     a5,a4,a5
   10098:       0007a703                lw      a4,0(a5)
   1009c:       fec42783                lw      a5,-20(s0)
   100a0:       00f70733                add     a4,a4,a5
   100a4:       000117b7                lui     a5,0x11
   100a8:       11c7a783                lw      a5,284(a5) # 1111c <global_init_var>
   100ac:       00f707b3                add     a5,a4,a5
   100b0:       00078513                mv      a0,a5
   100b4:       01c12403                lw      s0,28(sp)
   100b8:       02010113                addi    sp,sp,32
   100bc:       00008067                ret

000100c0 <_start>:
   100c0:       ee010113                addi    sp,sp,-288
   100c4:       10112e23                sw      ra,284(sp)
   100c8:       10812c23                sw      s0,280(sp)
   100cc:       12010413                addi    s0,sp,288
   100d0:       00100793                li      a5,1
   100d4:       fef42623                sw      a5,-20(s0)
   100d8:       000117b7                lui     a5,0x11
   100dc:       1247a703                lw      a4,292(a5) # 11124 <_edata>
   100e0:       000117b7                lui     a5,0x11
   100e4:       1207a783                lw      a5,288(a5) # 11120 <static_var.1382>
   100e8:       00f70733                add     a4,a4,a5
   100ec:       fec42783                lw      a5,-20(s0)
   100f0:       00f707b3                add     a5,a4,a5
   100f4:       00078513                mv      a0,a5
   100f8:       f7dff0ef                jal     ra,10074 <func1>
   100fc:       00050793                mv      a5,a0
   10100:       f6f42623                sw      a5,-148(s0)
   10104:       fec42783                lw      a5,-20(s0)
   10108:       00078513                mv      a0,a5
   1010c:       11c12083                lw      ra,284(sp)
   10110:       11812403                lw      s0,280(sp)
   10114:       12010113                addi    sp,sp,288
   10118:       00008067                ret
```

可以看到：

- `sp` 被用作栈指针——我们可能需要初始化栈指针到合适的内存位置。
- 尽管程序中有全局变量，`gp` 寄存器没有被使用。
- 没有使用 `fp` (frame pointer)，由于寄存器很多，使用 frame pointer 确实没有太大的必要。
- 没有 `fence` 与控制寄存器相关部分。

## 「定制」C 工具链：以 darkriscv 为例

在我们最终的架构中，需要将指令和数据放在不同的地方（哈佛架构），需要自己定义一套地址空间。可以使用链接脚本解决这个问题。

参考 darkriscv 的链接脚本：

```
MEMORY
{
    ROM (x!rw) : ORIGIN = 0x00000000, LENGTH = 0x1000
    RAM (rw!x) : ORIGIN = 0x00001000, LENGTH = 0x1000
    IO  (rw!x) : ORIGIN = 0x80000000, LENGTH = 0x10
}

SECTIONS
{
    .text :
    {
        boot.o(.text)
        *(.text)

    } > ROM

    .data :
    {
        *(.data)
        *(.bss)
        *(.rela*)
        *(.rodata*)

    } > RAM

    .io :
    {
        io.o(COMMON)

    } > IO
}
```

> In both cases, a proper designed linker script (darksocv.ld) probably solves the problem!
>
> The current memory map in the linker script is the follow:
>
> - 0x00000000: 4KB ROM
> - 0x00001000: 4KB RAM
>
> Also, the linker maps the IO in the following positions:
>
> - 0x80000000: UART status
> - 0x80000004: UART xmit/recv buffer
> - 0x80000008: LED buffer
>
> The RAM memory contains the .data area, the .bss area (after the .data and initialized with zero), the .rodada and the stack area at the end of RAM.

可以看到，程序的 `.text` 段接在 `boot.o`（用作初始化环境）后面放在了 ROM 区域，`.data`, `.bss` 等段则放在 RAM 区域，IO 则由 `io.o` 处理。此外由于不使用默认的 newlib C 库，darkriscv 自己实现了一些 C 函数，在 `stdio.c` 中。下面是通过调用定义的 IO 实现字符输入/输出的「标准库」代码。

```c
// putchar and getchar uses the "low-level" io

int getchar(void)
{
  while((io.uart.stat&2)==0); // uart empty, wait...
  
  return io.uart.fifo;
}

int putchar(int c)
{
  if(c=='\n')
  {
    while(io.uart.stat&1); // uart busy, wait...
    io.uart.fifo = '\r';  
  }
  
  while(io.uart.stat&1); // uart busy, wait...
  return io.uart.fifo = c;
}
```

最终，通过一个 Makefile 调用 `objcopy` 等程序，可以生成对应 ROM 和 RAM 文件，这些文件可以进一步转换为方便使用的格式，如 COE 等。