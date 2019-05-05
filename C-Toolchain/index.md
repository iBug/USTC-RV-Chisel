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
        return i + i;
}

int main(void)
{
        static int static_var = 24;

        int a = 1;

        int array[64];

        array[a] = func1(static_var + global_array[0] + a + array[0]);

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
   10084:       fec42783                lw      a5,-20(s0)
   10088:       00179793                slli    a5,a5,0x1
   1008c:       00078513                mv      a0,a5
   10090:       01c12403                lw      s0,28(sp)
   10094:       02010113                addi    sp,sp,32
   10098:       00008067                ret

0001009c <_start>:
   1009c:       ee010113                addi    sp,sp,-288
   100a0:       10112e23                sw      ra,284(sp)
   100a4:       10812c23                sw      s0,280(sp)
   100a8:       12010413                addi    s0,sp,288
   100ac:       00100793                li      a5,1
   100b0:       fef42623                sw      a5,-20(s0)
   100b4:       000117b7                lui     a5,0x11
   100b8:       1187a703                lw      a4,280(a5) # 11118 <_edata>
   100bc:       000117b7                lui     a5,0x11
   100c0:       1147a783                lw      a5,276(a5) # 11114 <static_var.1382>
   100c4:       00f70733                add     a4,a4,a5
   100c8:       fec42783                lw      a5,-20(s0)
   100cc:       00f70733                add     a4,a4,a5
   100d0:       eec42783                lw      a5,-276(s0)
   100d4:       00f707b3                add     a5,a4,a5
   100d8:       00078513                mv      a0,a5
   100dc:       f99ff0ef                jal     ra,10074 <func1>
   100e0:       00050713                mv      a4,a0
   100e4:       fec42783                lw      a5,-20(s0)
   100e8:       00279793                slli    a5,a5,0x2
   100ec:       ff040693                addi    a3,s0,-16
   100f0:       00f687b3                add     a5,a3,a5
   100f4:       eee7ae23                sw      a4,-260(a5)
   100f8:       fec42783                lw      a5,-20(s0)
   100fc:       00078513                mv      a0,a5
   10100:       11c12083                lw      ra,284(sp)
   10104:       11812403                lw      s0,280(sp)
   10108:       12010113                addi    sp,sp,288
   1010c:       00008067                ret
```

可以看到：

- `sp` 被用作栈指针。
- 尽管程序中有全局变量，`gp` 寄存器没有被使用。

