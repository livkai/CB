	.file	"rwint.c"
	.text
.Ltext0:
	.section	.rodata
.LC0:
	.string	"fgetc"
	.text
	.globl	readInt
	.type	readInt, @function
readInt:
.LFB0:
	.file 1 "rwint.c"
	.loc 1 12 0
	.cfi_startproc
	pushl	%ebp
.LCFI0:
	.cfi_def_cfa_offset 8
	.cfi_offset 5, -8
	movl	%esp, %ebp
.LCFI1:
	.cfi_def_cfa_register 5
	subl	$56, %esp
	.loc 1 12 0
	movl	%gs:20, %eax
	movl	%eax, -12(%ebp)
	xorl	%eax, %eax
	.loc 1 15 0
	movl	$0, -36(%ebp)
	.loc 1 16 0
	movl	$0, -32(%ebp)
	.loc 1 18 0
	leal	-23(%ebp), %eax
	movl	%eax, -28(%ebp)
	jmp	.L7
.L10:
	.loc 1 39 0
	nop
.L7:
	.loc 1 20 0
	movl	stdin, %eax
	movl	%eax, (%esp)
	call	fgetc
	movb	%al, -24(%ebp)
	movzbl	-24(%ebp), %eax
	testb	%al, %al
	je	.L10
	.loc 1 21 0
	movzbl	-24(%ebp), %eax
	cmpb	$10, %al
	jne	.L3
	.loc 1 22 0
	movl	stdin, %eax
	movl	%eax, (%esp)
	call	ferror
	testl	%eax, %eax
	je	.L4
	.loc 1 23 0
	movl	$.LC0, (%esp)
	call	perror
	.loc 1 24 0
	movl	$1, (%esp)
	call	exit
.L4:
	.loc 1 26 0
	leal	-23(%ebp), %eax
	movl	%eax, (%esp)
	call	atoi
	.loc 1 40 0
	movl	-12(%ebp), %edx
	xorl	%gs:20, %edx
	je	.L8
	jmp	.L9
.L3:
	.loc 1 29 0
	movzbl	-24(%ebp), %eax
	cmpb	$45, %al
	je	.L5
	.loc 1 29 0 is_stmt 0 discriminator 2
	movzbl	-24(%ebp), %eax
	cmpb	$48, %al
	je	.L5
	.loc 1 29 0 discriminator 1
	movzbl	-24(%ebp), %eax
	cmpb	$49, %al
	je	.L5
	movzbl	-24(%ebp), %eax
	cmpb	$50, %al
	je	.L5
	movzbl	-24(%ebp), %eax
	cmpb	$51, %al
	je	.L5
	movzbl	-24(%ebp), %eax
	cmpb	$52, %al
	je	.L5
	movzbl	-24(%ebp), %eax
	cmpb	$53, %al
	je	.L5
	movzbl	-24(%ebp), %eax
	cmpb	$54, %al
	je	.L5
	movzbl	-24(%ebp), %eax
	cmpb	$55, %al
	je	.L5
	movzbl	-24(%ebp), %eax
	cmpb	$56, %al
	je	.L5
	movzbl	-24(%ebp), %eax
	cmpb	$57, %al
	jne	.L6
.L5:
	cmpl	$0, -36(%ebp)
	jne	.L6
	.loc 1 30 0 is_stmt 1
	movzbl	-24(%ebp), %edx
	movl	-28(%ebp), %eax
	movb	%dl, (%eax)
	.loc 1 31 0
	addl	$1, -28(%ebp)
	.loc 1 32 0
	movl	$1, -32(%ebp)
	.loc 1 39 0
	jmp	.L10
.L6:
	.loc 1 34 0
	cmpl	$1, -32(%ebp)
	jne	.L10
	.loc 1 35 0
	movl	$1, -36(%ebp)
	.loc 1 39 0
	jmp	.L10
.L9:
	.loc 1 40 0
	call	__stack_chk_fail
.L8:
	leave
	.cfi_restore 5
.LCFI2:
	.cfi_def_cfa 4, 4
	ret
	.cfi_endproc
.LFE0:
	.size	readInt, .-readInt
	.section	.rodata
.LC1:
	.string	"%i\n"
.LC2:
	.string	"%i"
	.text
	.globl	writeInt
	.type	writeInt, @function
writeInt:
.LFB1:
	.loc 1 43 0
	.cfi_startproc
	pushl	%ebp
.LCFI3:
	.cfi_def_cfa_offset 8
	.cfi_offset 5, -8
	movl	%esp, %ebp
.LCFI4:
	.cfi_def_cfa_register 5
	subl	$40, %esp
	.loc 1 43 0
	movl	%gs:20, %eax
	movl	%eax, -12(%ebp)
	xorl	%eax, %eax
	.loc 1 44 0
	movl	$.LC1, %edx
	movl	stdout, %eax
	movl	8(%ebp), %ecx
	movl	%ecx, 8(%esp)
	movl	%edx, 4(%esp)
	movl	%eax, (%esp)
	call	fprintf
	.loc 1 46 0
	movl	$.LC2, %eax
	movl	8(%ebp), %edx
	movl	%edx, 8(%esp)
	movl	%eax, 4(%esp)
	leal	-23(%ebp), %eax
	movl	%eax, (%esp)
	call	sprintf
	.loc 1 47 0
	movl	-12(%ebp), %edx
	xorl	%gs:20, %edx
	je	.L12
	call	__stack_chk_fail
.L12:
	leave
	.cfi_restore 5
.LCFI5:
	.cfi_def_cfa 4, 4
	ret
	.cfi_endproc
.LFE1:
	.size	writeInt, .-writeInt
.Letext0:
	.file 2 "/usr/lib/gcc/x86_64-linux-gnu/4.6/include/stddef.h"
	.file 3 "/usr/include/bits/types.h"
	.file 4 "/usr/include/libio.h"
	.file 5 "/usr/include/stdio.h"
	.section	.debug_info,"",@progbits
.Ldebug_info0:
	.long	0x399
	.value	0x2
	.long	.Ldebug_abbrev0
	.byte	0x4
	.uleb128 0x1
	.long	.LASF53
	.byte	0x1
	.long	.LASF54
	.long	.LASF55
	.long	.Ltext0
	.long	.Letext0
	.long	.Ldebug_line0
	.uleb128 0x2
	.long	.LASF8
	.byte	0x2
	.byte	0xd4
	.long	0x30
	.uleb128 0x3
	.byte	0x4
	.byte	0x7
	.long	.LASF0
	.uleb128 0x3
	.byte	0x1
	.byte	0x8
	.long	.LASF1
	.uleb128 0x3
	.byte	0x2
	.byte	0x7
	.long	.LASF2
	.uleb128 0x3
	.byte	0x4
	.byte	0x7
	.long	.LASF3
	.uleb128 0x3
	.byte	0x1
	.byte	0x6
	.long	.LASF4
	.uleb128 0x3
	.byte	0x2
	.byte	0x5
	.long	.LASF5
	.uleb128 0x4
	.byte	0x4
	.byte	0x5
	.string	"int"
	.uleb128 0x3
	.byte	0x8
	.byte	0x5
	.long	.LASF6
	.uleb128 0x3
	.byte	0x8
	.byte	0x7
	.long	.LASF7
	.uleb128 0x2
	.long	.LASF9
	.byte	0x3
	.byte	0x38
	.long	0x61
	.uleb128 0x2
	.long	.LASF10
	.byte	0x3
	.byte	0x8d
	.long	0x85
	.uleb128 0x3
	.byte	0x4
	.byte	0x5
	.long	.LASF11
	.uleb128 0x2
	.long	.LASF12
	.byte	0x3
	.byte	0x8e
	.long	0x6f
	.uleb128 0x5
	.byte	0x4
	.uleb128 0x6
	.byte	0x4
	.long	0x9f
	.uleb128 0x3
	.byte	0x1
	.byte	0x6
	.long	.LASF13
	.uleb128 0x7
	.long	.LASF43
	.byte	0x94
	.byte	0x4
	.value	0x111
	.long	0x267
	.uleb128 0x8
	.long	.LASF14
	.byte	0x4
	.value	0x112
	.long	0x5a
	.byte	0x2
	.byte	0x23
	.uleb128 0
	.uleb128 0x8
	.long	.LASF15
	.byte	0x4
	.value	0x117
	.long	0x99
	.byte	0x2
	.byte	0x23
	.uleb128 0x4
	.uleb128 0x8
	.long	.LASF16
	.byte	0x4
	.value	0x118
	.long	0x99
	.byte	0x2
	.byte	0x23
	.uleb128 0x8
	.uleb128 0x8
	.long	.LASF17
	.byte	0x4
	.value	0x119
	.long	0x99
	.byte	0x2
	.byte	0x23
	.uleb128 0xc
	.uleb128 0x8
	.long	.LASF18
	.byte	0x4
	.value	0x11a
	.long	0x99
	.byte	0x2
	.byte	0x23
	.uleb128 0x10
	.uleb128 0x8
	.long	.LASF19
	.byte	0x4
	.value	0x11b
	.long	0x99
	.byte	0x2
	.byte	0x23
	.uleb128 0x14
	.uleb128 0x8
	.long	.LASF20
	.byte	0x4
	.value	0x11c
	.long	0x99
	.byte	0x2
	.byte	0x23
	.uleb128 0x18
	.uleb128 0x8
	.long	.LASF21
	.byte	0x4
	.value	0x11d
	.long	0x99
	.byte	0x2
	.byte	0x23
	.uleb128 0x1c
	.uleb128 0x8
	.long	.LASF22
	.byte	0x4
	.value	0x11e
	.long	0x99
	.byte	0x2
	.byte	0x23
	.uleb128 0x20
	.uleb128 0x8
	.long	.LASF23
	.byte	0x4
	.value	0x120
	.long	0x99
	.byte	0x2
	.byte	0x23
	.uleb128 0x24
	.uleb128 0x8
	.long	.LASF24
	.byte	0x4
	.value	0x121
	.long	0x99
	.byte	0x2
	.byte	0x23
	.uleb128 0x28
	.uleb128 0x8
	.long	.LASF25
	.byte	0x4
	.value	0x122
	.long	0x99
	.byte	0x2
	.byte	0x23
	.uleb128 0x2c
	.uleb128 0x8
	.long	.LASF26
	.byte	0x4
	.value	0x124
	.long	0x2a5
	.byte	0x2
	.byte	0x23
	.uleb128 0x30
	.uleb128 0x8
	.long	.LASF27
	.byte	0x4
	.value	0x126
	.long	0x2ab
	.byte	0x2
	.byte	0x23
	.uleb128 0x34
	.uleb128 0x8
	.long	.LASF28
	.byte	0x4
	.value	0x128
	.long	0x5a
	.byte	0x2
	.byte	0x23
	.uleb128 0x38
	.uleb128 0x8
	.long	.LASF29
	.byte	0x4
	.value	0x12c
	.long	0x5a
	.byte	0x2
	.byte	0x23
	.uleb128 0x3c
	.uleb128 0x8
	.long	.LASF30
	.byte	0x4
	.value	0x12e
	.long	0x7a
	.byte	0x2
	.byte	0x23
	.uleb128 0x40
	.uleb128 0x8
	.long	.LASF31
	.byte	0x4
	.value	0x132
	.long	0x3e
	.byte	0x2
	.byte	0x23
	.uleb128 0x44
	.uleb128 0x8
	.long	.LASF32
	.byte	0x4
	.value	0x133
	.long	0x4c
	.byte	0x2
	.byte	0x23
	.uleb128 0x46
	.uleb128 0x8
	.long	.LASF33
	.byte	0x4
	.value	0x134
	.long	0x2b1
	.byte	0x2
	.byte	0x23
	.uleb128 0x47
	.uleb128 0x8
	.long	.LASF34
	.byte	0x4
	.value	0x138
	.long	0x2c1
	.byte	0x2
	.byte	0x23
	.uleb128 0x48
	.uleb128 0x8
	.long	.LASF35
	.byte	0x4
	.value	0x141
	.long	0x8c
	.byte	0x2
	.byte	0x23
	.uleb128 0x4c
	.uleb128 0x8
	.long	.LASF36
	.byte	0x4
	.value	0x14a
	.long	0x97
	.byte	0x2
	.byte	0x23
	.uleb128 0x54
	.uleb128 0x8
	.long	.LASF37
	.byte	0x4
	.value	0x14b
	.long	0x97
	.byte	0x2
	.byte	0x23
	.uleb128 0x58
	.uleb128 0x8
	.long	.LASF38
	.byte	0x4
	.value	0x14c
	.long	0x97
	.byte	0x2
	.byte	0x23
	.uleb128 0x5c
	.uleb128 0x8
	.long	.LASF39
	.byte	0x4
	.value	0x14d
	.long	0x97
	.byte	0x2
	.byte	0x23
	.uleb128 0x60
	.uleb128 0x8
	.long	.LASF40
	.byte	0x4
	.value	0x14e
	.long	0x25
	.byte	0x2
	.byte	0x23
	.uleb128 0x64
	.uleb128 0x8
	.long	.LASF41
	.byte	0x4
	.value	0x150
	.long	0x5a
	.byte	0x2
	.byte	0x23
	.uleb128 0x68
	.uleb128 0x8
	.long	.LASF42
	.byte	0x4
	.value	0x152
	.long	0x2c7
	.byte	0x2
	.byte	0x23
	.uleb128 0x6c
	.byte	0
	.uleb128 0x9
	.long	.LASF56
	.byte	0x4
	.byte	0xb6
	.uleb128 0xa
	.long	.LASF44
	.byte	0xc
	.byte	0x4
	.byte	0xbc
	.long	0x2a5
	.uleb128 0xb
	.long	.LASF45
	.byte	0x4
	.byte	0xbd
	.long	0x2a5
	.byte	0x2
	.byte	0x23
	.uleb128 0
	.uleb128 0xb
	.long	.LASF46
	.byte	0x4
	.byte	0xbe
	.long	0x2ab
	.byte	0x2
	.byte	0x23
	.uleb128 0x4
	.uleb128 0xb
	.long	.LASF47
	.byte	0x4
	.byte	0xc2
	.long	0x5a
	.byte	0x2
	.byte	0x23
	.uleb128 0x8
	.byte	0
	.uleb128 0x6
	.byte	0x4
	.long	0x26e
	.uleb128 0x6
	.byte	0x4
	.long	0xa6
	.uleb128 0xc
	.long	0x9f
	.long	0x2c1
	.uleb128 0xd
	.long	0x30
	.byte	0
	.byte	0
	.uleb128 0x6
	.byte	0x4
	.long	0x267
	.uleb128 0xc
	.long	0x9f
	.long	0x2d7
	.uleb128 0xd
	.long	0x30
	.byte	0x27
	.byte	0
	.uleb128 0xe
	.byte	0x1
	.long	.LASF57
	.byte	0x1
	.byte	0xc
	.long	0x5a
	.long	.LFB0
	.long	.LFE0
	.long	.LLST0
	.long	0x33a
	.uleb128 0xf
	.string	"tmp"
	.byte	0x1
	.byte	0xd
	.long	0x2b1
	.byte	0x2
	.byte	0x91
	.sleb128 -32
	.uleb128 0xf
	.string	"buf"
	.byte	0x1
	.byte	0xe
	.long	0x33a
	.byte	0x2
	.byte	0x91
	.sleb128 -31
	.uleb128 0x10
	.long	.LASF48
	.byte	0x1
	.byte	0xf
	.long	0x5a
	.byte	0x2
	.byte	0x91
	.sleb128 -44
	.uleb128 0x10
	.long	.LASF49
	.byte	0x1
	.byte	0x10
	.long	0x5a
	.byte	0x2
	.byte	0x91
	.sleb128 -40
	.uleb128 0xf
	.string	"ptr"
	.byte	0x1
	.byte	0x11
	.long	0x99
	.byte	0x2
	.byte	0x91
	.sleb128 -36
	.byte	0
	.uleb128 0xc
	.long	0x9f
	.long	0x34a
	.uleb128 0xd
	.long	0x30
	.byte	0xa
	.byte	0
	.uleb128 0x11
	.byte	0x1
	.long	.LASF58
	.byte	0x1
	.byte	0x2b
	.byte	0x1
	.long	0x5a
	.long	.LFB1
	.long	.LFE1
	.long	.LLST1
	.long	0x382
	.uleb128 0x12
	.string	"x"
	.byte	0x1
	.byte	0x2b
	.long	0x5a
	.byte	0x2
	.byte	0x91
	.sleb128 0
	.uleb128 0x10
	.long	.LASF50
	.byte	0x1
	.byte	0x2d
	.long	0x33a
	.byte	0x2
	.byte	0x91
	.sleb128 -31
	.byte	0
	.uleb128 0x13
	.long	.LASF51
	.byte	0x5
	.byte	0xa9
	.long	0x2ab
	.byte	0x1
	.byte	0x1
	.uleb128 0x13
	.long	.LASF52
	.byte	0x5
	.byte	0xaa
	.long	0x2ab
	.byte	0x1
	.byte	0x1
	.byte	0
	.section	.debug_abbrev,"",@progbits
.Ldebug_abbrev0:
	.uleb128 0x1
	.uleb128 0x11
	.byte	0x1
	.uleb128 0x25
	.uleb128 0xe
	.uleb128 0x13
	.uleb128 0xb
	.uleb128 0x3
	.uleb128 0xe
	.uleb128 0x1b
	.uleb128 0xe
	.uleb128 0x11
	.uleb128 0x1
	.uleb128 0x12
	.uleb128 0x1
	.uleb128 0x10
	.uleb128 0x6
	.byte	0
	.byte	0
	.uleb128 0x2
	.uleb128 0x16
	.byte	0
	.uleb128 0x3
	.uleb128 0xe
	.uleb128 0x3a
	.uleb128 0xb
	.uleb128 0x3b
	.uleb128 0xb
	.uleb128 0x49
	.uleb128 0x13
	.byte	0
	.byte	0
	.uleb128 0x3
	.uleb128 0x24
	.byte	0
	.uleb128 0xb
	.uleb128 0xb
	.uleb128 0x3e
	.uleb128 0xb
	.uleb128 0x3
	.uleb128 0xe
	.byte	0
	.byte	0
	.uleb128 0x4
	.uleb128 0x24
	.byte	0
	.uleb128 0xb
	.uleb128 0xb
	.uleb128 0x3e
	.uleb128 0xb
	.uleb128 0x3
	.uleb128 0x8
	.byte	0
	.byte	0
	.uleb128 0x5
	.uleb128 0xf
	.byte	0
	.uleb128 0xb
	.uleb128 0xb
	.byte	0
	.byte	0
	.uleb128 0x6
	.uleb128 0xf
	.byte	0
	.uleb128 0xb
	.uleb128 0xb
	.uleb128 0x49
	.uleb128 0x13
	.byte	0
	.byte	0
	.uleb128 0x7
	.uleb128 0x13
	.byte	0x1
	.uleb128 0x3
	.uleb128 0xe
	.uleb128 0xb
	.uleb128 0xb
	.uleb128 0x3a
	.uleb128 0xb
	.uleb128 0x3b
	.uleb128 0x5
	.uleb128 0x1
	.uleb128 0x13
	.byte	0
	.byte	0
	.uleb128 0x8
	.uleb128 0xd
	.byte	0
	.uleb128 0x3
	.uleb128 0xe
	.uleb128 0x3a
	.uleb128 0xb
	.uleb128 0x3b
	.uleb128 0x5
	.uleb128 0x49
	.uleb128 0x13
	.uleb128 0x38
	.uleb128 0xa
	.byte	0
	.byte	0
	.uleb128 0x9
	.uleb128 0x16
	.byte	0
	.uleb128 0x3
	.uleb128 0xe
	.uleb128 0x3a
	.uleb128 0xb
	.uleb128 0x3b
	.uleb128 0xb
	.byte	0
	.byte	0
	.uleb128 0xa
	.uleb128 0x13
	.byte	0x1
	.uleb128 0x3
	.uleb128 0xe
	.uleb128 0xb
	.uleb128 0xb
	.uleb128 0x3a
	.uleb128 0xb
	.uleb128 0x3b
	.uleb128 0xb
	.uleb128 0x1
	.uleb128 0x13
	.byte	0
	.byte	0
	.uleb128 0xb
	.uleb128 0xd
	.byte	0
	.uleb128 0x3
	.uleb128 0xe
	.uleb128 0x3a
	.uleb128 0xb
	.uleb128 0x3b
	.uleb128 0xb
	.uleb128 0x49
	.uleb128 0x13
	.uleb128 0x38
	.uleb128 0xa
	.byte	0
	.byte	0
	.uleb128 0xc
	.uleb128 0x1
	.byte	0x1
	.uleb128 0x49
	.uleb128 0x13
	.uleb128 0x1
	.uleb128 0x13
	.byte	0
	.byte	0
	.uleb128 0xd
	.uleb128 0x21
	.byte	0
	.uleb128 0x49
	.uleb128 0x13
	.uleb128 0x2f
	.uleb128 0xb
	.byte	0
	.byte	0
	.uleb128 0xe
	.uleb128 0x2e
	.byte	0x1
	.uleb128 0x3f
	.uleb128 0xc
	.uleb128 0x3
	.uleb128 0xe
	.uleb128 0x3a
	.uleb128 0xb
	.uleb128 0x3b
	.uleb128 0xb
	.uleb128 0x49
	.uleb128 0x13
	.uleb128 0x11
	.uleb128 0x1
	.uleb128 0x12
	.uleb128 0x1
	.uleb128 0x40
	.uleb128 0x6
	.uleb128 0x1
	.uleb128 0x13
	.byte	0
	.byte	0
	.uleb128 0xf
	.uleb128 0x34
	.byte	0
	.uleb128 0x3
	.uleb128 0x8
	.uleb128 0x3a
	.uleb128 0xb
	.uleb128 0x3b
	.uleb128 0xb
	.uleb128 0x49
	.uleb128 0x13
	.uleb128 0x2
	.uleb128 0xa
	.byte	0
	.byte	0
	.uleb128 0x10
	.uleb128 0x34
	.byte	0
	.uleb128 0x3
	.uleb128 0xe
	.uleb128 0x3a
	.uleb128 0xb
	.uleb128 0x3b
	.uleb128 0xb
	.uleb128 0x49
	.uleb128 0x13
	.uleb128 0x2
	.uleb128 0xa
	.byte	0
	.byte	0
	.uleb128 0x11
	.uleb128 0x2e
	.byte	0x1
	.uleb128 0x3f
	.uleb128 0xc
	.uleb128 0x3
	.uleb128 0xe
	.uleb128 0x3a
	.uleb128 0xb
	.uleb128 0x3b
	.uleb128 0xb
	.uleb128 0x27
	.uleb128 0xc
	.uleb128 0x49
	.uleb128 0x13
	.uleb128 0x11
	.uleb128 0x1
	.uleb128 0x12
	.uleb128 0x1
	.uleb128 0x40
	.uleb128 0x6
	.uleb128 0x1
	.uleb128 0x13
	.byte	0
	.byte	0
	.uleb128 0x12
	.uleb128 0x5
	.byte	0
	.uleb128 0x3
	.uleb128 0x8
	.uleb128 0x3a
	.uleb128 0xb
	.uleb128 0x3b
	.uleb128 0xb
	.uleb128 0x49
	.uleb128 0x13
	.uleb128 0x2
	.uleb128 0xa
	.byte	0
	.byte	0
	.uleb128 0x13
	.uleb128 0x34
	.byte	0
	.uleb128 0x3
	.uleb128 0xe
	.uleb128 0x3a
	.uleb128 0xb
	.uleb128 0x3b
	.uleb128 0xb
	.uleb128 0x49
	.uleb128 0x13
	.uleb128 0x3f
	.uleb128 0xc
	.uleb128 0x3c
	.uleb128 0xc
	.byte	0
	.byte	0
	.byte	0
	.section	.debug_loc,"",@progbits
.Ldebug_loc0:
.LLST0:
	.long	.LFB0-.Ltext0
	.long	.LCFI0-.Ltext0
	.value	0x2
	.byte	0x74
	.sleb128 4
	.long	.LCFI0-.Ltext0
	.long	.LCFI1-.Ltext0
	.value	0x2
	.byte	0x74
	.sleb128 8
	.long	.LCFI1-.Ltext0
	.long	.LCFI2-.Ltext0
	.value	0x2
	.byte	0x75
	.sleb128 8
	.long	.LCFI2-.Ltext0
	.long	.LFE0-.Ltext0
	.value	0x2
	.byte	0x74
	.sleb128 4
	.long	0
	.long	0
.LLST1:
	.long	.LFB1-.Ltext0
	.long	.LCFI3-.Ltext0
	.value	0x2
	.byte	0x74
	.sleb128 4
	.long	.LCFI3-.Ltext0
	.long	.LCFI4-.Ltext0
	.value	0x2
	.byte	0x74
	.sleb128 8
	.long	.LCFI4-.Ltext0
	.long	.LCFI5-.Ltext0
	.value	0x2
	.byte	0x75
	.sleb128 8
	.long	.LCFI5-.Ltext0
	.long	.LFE1-.Ltext0
	.value	0x2
	.byte	0x74
	.sleb128 4
	.long	0
	.long	0
	.section	.debug_aranges,"",@progbits
	.long	0x1c
	.value	0x2
	.long	.Ldebug_info0
	.byte	0x4
	.byte	0
	.value	0
	.value	0
	.long	.Ltext0
	.long	.Letext0-.Ltext0
	.long	0
	.long	0
	.section	.debug_line,"",@progbits
.Ldebug_line0:
	.section	.debug_str,"MS",@progbits,1
.LASF9:
	.string	"__quad_t"
.LASF43:
	.string	"_IO_FILE"
.LASF25:
	.string	"_IO_save_end"
.LASF5:
	.string	"short int"
.LASF8:
	.string	"size_t"
.LASF35:
	.string	"_offset"
.LASF54:
	.string	"rwint.c"
.LASF19:
	.string	"_IO_write_ptr"
.LASF14:
	.string	"_flags"
.LASF21:
	.string	"_IO_buf_base"
.LASF26:
	.string	"_markers"
.LASF16:
	.string	"_IO_read_end"
.LASF48:
	.string	"flag1"
.LASF49:
	.string	"flag2"
.LASF6:
	.string	"long long int"
.LASF34:
	.string	"_lock"
.LASF11:
	.string	"long int"
.LASF31:
	.string	"_cur_column"
.LASF50:
	.string	"digits"
.LASF47:
	.string	"_pos"
.LASF58:
	.string	"writeInt"
.LASF46:
	.string	"_sbuf"
.LASF30:
	.string	"_old_offset"
.LASF57:
	.string	"readInt"
.LASF1:
	.string	"unsigned char"
.LASF4:
	.string	"signed char"
.LASF7:
	.string	"long long unsigned int"
.LASF0:
	.string	"unsigned int"
.LASF44:
	.string	"_IO_marker"
.LASF33:
	.string	"_shortbuf"
.LASF18:
	.string	"_IO_write_base"
.LASF42:
	.string	"_unused2"
.LASF15:
	.string	"_IO_read_ptr"
.LASF22:
	.string	"_IO_buf_end"
.LASF13:
	.string	"char"
.LASF45:
	.string	"_next"
.LASF36:
	.string	"__pad1"
.LASF37:
	.string	"__pad2"
.LASF38:
	.string	"__pad3"
.LASF39:
	.string	"__pad4"
.LASF40:
	.string	"__pad5"
.LASF2:
	.string	"short unsigned int"
.LASF3:
	.string	"long unsigned int"
.LASF20:
	.string	"_IO_write_end"
.LASF12:
	.string	"__off64_t"
.LASF28:
	.string	"_fileno"
.LASF27:
	.string	"_chain"
.LASF10:
	.string	"__off_t"
.LASF24:
	.string	"_IO_backup_base"
.LASF51:
	.string	"stdin"
.LASF29:
	.string	"_flags2"
.LASF41:
	.string	"_mode"
.LASF17:
	.string	"_IO_read_base"
.LASF32:
	.string	"_vtable_offset"
.LASF53:
	.string	"GNU C 4.6.3"
.LASF23:
	.string	"_IO_save_base"
.LASF55:
	.string	"/home/basti/workspace/CB/runtime/i386"
.LASF52:
	.string	"stdout"
.LASF56:
	.string	"_IO_lock_t"
	.ident	"GCC: (Ubuntu/Linaro 4.6.3-1ubuntu5) 4.6.3"
	.section	.note.GNU-stack,"",@progbits
