	.global _start

_start:
	call main
	movl $0, %ebx
	movl $1, %eax
	int $0x80
	
