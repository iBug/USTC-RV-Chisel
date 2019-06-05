#!/bin/sh
verilator --cc ../target/MainWithClock.v ip.v -exe ustcrv.cpp
make -C obj_dir -f VMainWithClock.mk
obj_dir/VMainWithClock