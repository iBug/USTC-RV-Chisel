MEMORY
{
	IMEM (x!rw): ORIGIN = 0x00000000, LENGTH = 0x1000
	DMEM (rw!x): ORIGIN = 0x00001000, LENGTH = 0x1000
}

_stack_start = 0x00002000;

SECTIONS
{
	.text :
	{
		init.o(.text)
		*(.text)
	} > IMEM

	.data :
	{
		*(.data)
		*(.bss)
		*(.rela*)
		*(.rodata*)
	} > DMEM

}
