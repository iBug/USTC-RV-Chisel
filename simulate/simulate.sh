#!/bin/sh
verilator --cc ../target/MainWithClock.v ip.v -exe ustcrv.cpp --trace
make -C obj_dir -f VMainWithClock.mk
obj_dir/VMainWithClock