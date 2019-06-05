module CPU_clock(input in, output out);
    assign out = in;
endmodule

module BlockRAM_IP(
    input clka,
    input [11:0] addra,
    input [7:0] dina,
    input ena,
    input wea,
    input clkb,
    input [11:0] addrb,
    output [7:0] doutb,
    input enb
);
    reg [7:0] ram [0:4095];
    reg [7:0] outbuf;
    assign doutb = outbuf;
    always @(posedge clka)
    begin
        if (ena && wea)
            ram[addra] <= dina;
    end
    always @(posedge clkb)
    begin
        if (enb)
            outbuf <= ram[addrb];
    end
endmodule