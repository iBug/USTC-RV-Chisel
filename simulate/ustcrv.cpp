#include <verilated.h>
#include "VMainWithClock.h"

int main(int argc, char **argv) {
    Verilated::commandArgs(argc, argv);
    VMainWithClock *top = new VMainWithClock;
    int clock = 0;
    while (!Verilated::gotFinish()) {
        clock = (clock + 1) & 1;
        top->clock = clock;
        top->eval();
    }
    delete top;
    return 0;
}