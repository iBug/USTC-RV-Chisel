.global _stack_start
.global main

_start:
	la sp, _stack_start
	call main
	dead_loop: j dead_loop
