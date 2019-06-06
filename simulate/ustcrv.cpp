#include <inttypes.h>
#include <verilated.h>
#include <verilated_vcd_c.h>
#include "VMainWithClock.h"

const int SimTime = 10000;

int main(int argc, char **argv) {
    int main_time = 0;
    Verilated::commandArgs(argc, argv);
    Verilated::traceEverOn(true);
    VerilatedVcdC *tfp = new VerilatedVcdC;
    VMainWithClock *top = new VMainWithClock;
    top->trace(tfp, 99);
    tfp->open("sim.vcd");
    while (!Verilated::gotFinish()) {
        ++main_time;
        if (main_time % 10 == 3) {
            top->clock = 1;
        }
        if (main_time % 10 == 8) {
            top->clock = 0;
        }
        if (main_time > 1 && main_time < 10) {
            top->reset = 1;
        } else {
            top->reset = 0;
        }
        top->eval();
        tfp->dump(main_time);
    }
    tfp->close();
    delete top;
    return 0;
}