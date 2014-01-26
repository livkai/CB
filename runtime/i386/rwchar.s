	.global writeChar
	.global readChar

writeChar:
	pushl %ebp
	movl %esp, %ebp
	movl $1, %ebx
	movl 8(%ebp), %eax
	movl %eax, buf
	movl $buf, %ecx
	movl $1, %edx
	movl $4, %eax
	int $0x80

	movl %ebp, %esp
	popl %ebp
	ret

readChar:
	pushl %ebp
	movl %esp, %ebp
	movl $0, %ebx
	movl $buf,%ecx
	movl $1, %edx
	movl $3, %eax
	int $0x80

	movl buf,%eax
	movl %ebp, %esp
	popl %ebp
	ret
	
	.lcomm buf,1
