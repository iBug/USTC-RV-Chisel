module IMemROM (
    input [7:0] addr,
    output [31:0] data
);
    reg [31:0] rom [255:0];

    initial
        $readmemh("USTC-RV-Chisel.srcs/sources_1/new/data/riscv.imem");
endmodule

module DMemROM (
    input [7:0] addr,
    output [31:0] data
);
    reg [31:0] rom [255:0];

    initial
        $readmemh("USTC-RV-Chisel.srcs/sources_1/new/data/riscv.dmem");
endmodule
