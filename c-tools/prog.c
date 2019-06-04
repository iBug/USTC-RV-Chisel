#include "helper.h"
#include "string.h"
#include "stdio.h"

int global_array[20];
char success[] = "Hello, world!\nRISC-V!";

int square(int x) {
	return x * x;
}

int main(void) {
	int x[20];
	for (int i = 0; i < 20; i++)
		x[i] = square(i);
	memcpy(global_array, x, 20 * sizeof(int));
	// for (int i = 0; i < 20; i++)
	//	global_array[i] = x[i];
	for (int i = 0; i < sizeof(success); i++) {
		putchar(success[i]);
	}
	return 0;
}
