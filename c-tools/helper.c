unsigned int __mulsi3 (unsigned int x, unsigned int y) {
	// mock __mulsi3 provided by compiler
	// binary multiply algorithm
	unsigned int ans = 0;

	for (;;) {
		if (x & 1) {  // if x is odd
			ans += y;
		}
		x >>= 1; // x /= 2
		y <<= 1; // y *= 2
		if (x == 0)
			break;
	}

	return ans;
}
