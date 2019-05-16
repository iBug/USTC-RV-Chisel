---
title: "核心设计"
tagline: " "
---

单周期 RISC-V CPU 的核心基本按照加州伯克利大学的 CS61C 课程第 11 讲幻灯片设计，如图：

![image](https://user-images.githubusercontent.com/7273074/57189390-d4c85680-6f40-11e9-86fd-6f9a6357cb9a.png)

一个重要的不同点是，分支判断模块并没有按照图中设计编写，而是采用了另一种方式：控制模块向 BrComp 发送信号确定分支类型，BrComp 唯一的输出信号是 taken（分支是否被选中），这样简化了控制模块与分支相关的部分的结构。
