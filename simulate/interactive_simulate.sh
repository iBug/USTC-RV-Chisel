#!/bin/sh
verilator -LDFLAGS "-lboost_system -lpthread" -CFLAGS "-std=c++11" --cc ../target/MainWithClock.v ip.v -exe ustcrv_ws.cpp
make -C obj_dir -f VMainWithClock.mk
obj_dir/VMainWithClock