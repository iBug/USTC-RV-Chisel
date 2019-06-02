module CPU_clock (
    input in,
    output reg out
);
    integer i;
    initial begin
        i <= 0;
        out <= 1'b0;
    end
    always @(posedge in) begin
        if (i >= 49) begin
            i <= 0;
            out <= ~out;
        end else begin
            i <= i + 1;
        end
    end
endmodule
