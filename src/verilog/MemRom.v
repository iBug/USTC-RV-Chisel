module IMemRom (
    input [7:0] addr,
    output [31:0] data
);
    reg [31:0] rom [255:0];

    initial
        $readmemh("data/riscv.imem");
endmodule

module DMemRom (
    input [7:0] addr,
    output [31:0] data
);
    reg [31:0] rom [255:0];

    initial
        $readmemh("data/riscv.dmem");
endmodule
