#include "string.h"
#include "stdio.h"
#include "io.h"

char *memset(char *b, int c, size_t len) {
	char *ret = b;
	while (len--) {
		*b++ = c;
	}
	return ret;
}

char *memcpy(char *restrict dst, const char *restrict src, size_t n) {
	char *ret = dst;
	while (n--) {
		*dst++ = *src++;
	}

	return ret;
}

int putchar(int c) {
	io.vga_outchar = c;
	return c;
}
