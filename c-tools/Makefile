CROSS = riscv32-unknown-elf
CCPATH = $(RISCV)/bin/

CC = $(CCPATH)/$(CROSS)-gcc
AS = $(CCPATH)/$(CROSS)-as
LD = $(CCPATH)/$(CROSS)-ld
OC = $(CCPATH)/$(CROSS)-objcopy
OD = $(CCPATH)/$(CROSS)-objdump

CCFLAGS = -Wall -Og -I./include -fomit-frame-pointer -march=rv32i -ffunction-sections -static
ASFLAGS = -march=rv32i
LDFLAGS = -T $(PROJ).ld -Map=$(PROJ).map --gc-sections -print-gc-sections
OCFLAGS = -O binary
ODFLAGS = -d

MEMSIZE = 4096

PROJ = riscv
OBJS = init.o prog.o helper.o std.o
ASMS = prog.s helper.s std.s

all: $(PROJ).imem $(PROJ).dmem

clean:
	-bash -c 'rm -f $(ASMS) *.o $(PROJ).{bin,lst,map,imem,dmem}'

%.o: %.s
	$(AS) $(ASFLAGS) -c $< -o $@

%.s: %.c $(PROJ).ld
	$(CC) $(CCFLAGS) -S $< -o $@

$(PROJ).o: $(ASMS) $(OBJS)
	$(LD) $(OBJS) -o $@ $(LDFLAGS)

$(PROJ).bin: $(PROJ).o
	$(OC) $(OCFLAGS) $< $@
	$(OD) $(ODFLAGS) $< > $(PROJ).lst

$(PROJ).imem: $(PROJ).bin
	hexdump -ve '1/4 "%08x\n"' -n $(MEMSIZE) $< | grep -v 00000000 > $@
	wc -l $@

$(PROJ).dmem: $(PROJ).bin
	hexdump -ve '1/4 "%08x\n"' -s $(MEMSIZE) $< > $@
	wc -l $@
