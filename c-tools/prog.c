#include "helper.h"
#include "string.h"

int global_array[20];

int square(int x) {
	return x * x;
}

int main(void) {
	int x[20];
	for (int i = 0; i < 20; i++)
		x[i] = square(i);
	memcpy(global_array, x, 20 * sizeof(int));
	return 0;
}
