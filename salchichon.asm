.data
    nl: .asciiz "\n"
    t1: .asciiz ""
    _s1_: .asciiz "Hola mundo"
    t2: .word 0
    _b1_: .word 0
    t3: .asciiz "Hola mundo"
    t4: .word 0
    t5: .word 0
    t6: .word 0
    x: .word 0
    t7: .word 0
    _i_: .word 0
    t8: .word 0
    t9: .asciiz "Compiladores 2024"
    _str_: .asciiz "Compiladores 2024"
    f1: .word 0x0000000000000000
    super: .word 0
    f2: .word 0
    f3: .word 0

.text
.globl main

main:
    jal principal
    li   $v0, 10
    syscall

#SYSCALL
printStr:
    li   $v0, 4
    syscall
    jr $ra
.end printStr

printInt:
    li   $v0, 1
    syscall
    jr $ra
.end printInt

printFloat:
    li   $v0, 2
    syscall
    jr $ra
.end printFloat

readInt:
    li   $v0, 5
    syscall
    jr $ra
.end readInt

readFloat:
    li   $v0, 6
    syscall
    jr $ra
.end readFloat

