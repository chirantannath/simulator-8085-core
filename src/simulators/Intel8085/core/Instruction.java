/* MIT License

Copyright (c) 2021 Chirantan Nath <emergency.jasper@gmail.com>

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.*/

package simulators.Intel8085.core;

import java.util.Set;
import java.util.EnumSet;
import java.util.Arrays;
import java.util.Collections;
import java.util.ArrayDeque;

/** Enumeration listing all possible instructions (1-byte opcodes) for the 8085 
 * in the order of their bit-wise pattern; such that the ordinal value (see {@link java.lang.Enum#ordinal()})
 * has the actual instruction opcode (1st byte) in its lowest significant byte.
 * This means that some of the instructions will be repeated multiple times for
 * multiple combinations (for example the MOV instruction has 63 combinations) and
 * the number of constants for this enumeration is 256; this enum also has <code>Unused_<i>XX</i></code> constants 
 * which indicate slots in the 8-bit code space (hexadecimal code <i>XX</i>) which are not used for any instruction by 
 * the 8085.
 * @author Chirantan Nath (emergency.jasper@gmail.com)
 */
public enum Instruction implements FlagRegisterConstants {
    /**No operation (NOP); hex machine code 0x00.*/
    NoOperation(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.NOACCESS, "NOP", "NOP", 1),
    /**Load register pair immediate BC (LXI B); hex machine code 0x01.*/
    LoadRegisterPairImmediate_B(NOFLAGS, new byte[]{10}, new byte[]{3}, Instruction.IMMEDIATE, "LXI", "LXI B, data 16", 3),
    /**Store accumulator indirect BC (STAX B); hex machine code 0x02.*/
    StoreAccumulatorIndirect_B(NOFLAGS, new byte[]{7}, new byte[]{2}, Instruction.REGISTER_INDIRECT, "STAX", "STAX B", 1),
    /**Increment register pair BC (INX B); hex machine code 0x03.*/
    IncrementRegisterPair_B(NOFLAGS, new byte[]{6}, new byte[]{1}, Instruction.REGISTER, "INX", "INX B", 1),
    /**Increment register B (INR B); hex machine code 0x04.*/
    IncrementRegister_B(ZERO | SIGN | PARITY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "INR", "INR B", 1),
    /**Decrement register B (DCR B); hex machine code 0x05.*/
    DecrementRegister_B(ZERO | SIGN | PARITY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "DCR", "DCR B", 1),
    /**Move immediate B (MVI B); hex machine code 0x06.*/
    MoveImmediate_B(NOFLAGS, new byte[]{7}, new byte[]{2}, Instruction.IMMEDIATE, "MVI", "MVI B, data 8", 2),
    /**Rotate left (RLC); hex machine code 0x07.*/
    RotateLeft(CARRY, new byte[]{4}, new byte[]{1}, Instruction.NOACCESS, "RLC", "RLC", 1),
    /**Unused instruction at slot 0x08.*/
    @Deprecated Unused_08,
    /**Double/direct add register pair BC to HL (DAD B); hex machine code 0x09.*/
    DoubleAddRegisterPair_B(CARRY, new byte[]{10}, new byte[]{3}, Instruction.REGISTER, "DAD", "DAD B", 1),
    /**Load accumulator indirect BC (LDAX B); hex machine code 0x0A.*/
    LoadAccumulatorIndirect_B(NOFLAGS, new byte[]{7}, new byte[]{2}, Instruction.REGISTER_INDIRECT, "LDAX", "LDAX B", 1),
    /**Decrement register pair BC (DCX B); hex machine code 0x0B.*/
    DecrementRegisterPair_B(NOFLAGS, new byte[]{6}, new byte[]{1}, Instruction.REGISTER, "DCX", "DCX B", 1),
    /**Increment register C (INR C); hex machine code 0x0C.*/
    IncrementRegister_C(ZERO | SIGN | PARITY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "INR", "INR C", 1),
    /**Decrement register C (DCR C); hex machine code 0x0D.*/
    DecrementRegister_C(ZERO | SIGN | PARITY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "DCR", "DCR C", 1),
    /**Move immediate C (MVI C); hex machine code 0x0E.*/
    MoveImmediate_C(NOFLAGS, new byte[]{7}, new byte[]{2}, Instruction.IMMEDIATE, "MVI", "MVI C, data 8", 2),
    /**Rotate right (RRC); hex machine code 0x0F.*/
    RotateRight(CARRY, new byte[]{4}, new byte[]{1}, Instruction.NOACCESS, "RRC", "RRC", 1),
    /**Unused instruction at slot 0x10.*/
    @Deprecated Unused_10,
    /**Load register pair immediate DE (LXI D); hex machine code 0x11.*/
    LoadRegisterPairImmediate_D(NOFLAGS, new byte[]{10}, new byte[]{3}, Instruction.IMMEDIATE, "LXI", "LXI D, data 16", 3),
    /**Store accumulator indirect DE (STAX D); hex machine code 0x12.*/
    StoreAccumulatorIndirect_D(NOFLAGS, new byte[]{7}, new byte[]{2}, Instruction.REGISTER_INDIRECT, "STAX", "STAX D", 1),
    /**Increment register pair DE (INX D); hex machine code 0x13.*/
    IncrementRegisterPair_D(NOFLAGS, new byte[]{6}, new byte[]{1}, Instruction.REGISTER, "INX", "INX D", 1),
    /**Increment register D (INR D); hex machine code 0x14.*/
    IncrementRegister_D(ZERO | SIGN | PARITY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "INR", "INR D", 1),
    /**Decrement register D (DCR D); hex machine code 0x15.*/
    DecrementRegister_D(ZERO | SIGN | PARITY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "DCR", "DCR D", 1),
    /**Move immediate D (MVI D); hex machine code 0x16.*/
    MoveImmediate_D(NOFLAGS, new byte[]{7}, new byte[]{2}, Instruction.IMMEDIATE, "MVI", "MVI D, data 8", 2),
    /**Rotate left through carry(RAL); hex machine code 0x17.*/
    RotateLeftThroughCarry(CARRY, new byte[]{4}, new byte[]{1}, Instruction.NOACCESS, "RAL", "RAL", 1),
    /**Unused instruction at slot 0x18.*/
    @Deprecated Unused_18,
    /**Double/direct add register pair DE to HL (DAD D); hex machine code 0x19.*/
    DoubleAddRegisterPair_D(CARRY, new byte[]{10}, new byte[]{3}, Instruction.REGISTER, "DAD", "DAD D", 1),
    /**Load accumulator indirect DE (LDAX D); hex machine code 0x1A.*/
    LoadAccumulatorIndirect_D(NOFLAGS, new byte[]{7}, new byte[]{2}, Instruction.REGISTER_INDIRECT, "LDAX", "LDAX D", 1),
    /**Decrement register pair DE (DCX D); hex machine code 0x1B.*/
    DecrementRegisterPair_D(NOFLAGS, new byte[]{6}, new byte[]{1}, Instruction.REGISTER, "DCX", "DCX D", 1),
    /**Increment register E (INR E); hex machine code 0x1C.*/
    IncrementRegister_E(ZERO | SIGN | PARITY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "INR", "INR E", 1),
    /**Decrement register E (DCR E); hex machine code 0x1D.*/
    DecrementRegister_E(ZERO | SIGN | PARITY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "DCR", "DCR E", 1),
    /**Move immediate E (MVI C); hex machine code 0x1E.*/
    MoveImmediate_E(NOFLAGS, new byte[]{7}, new byte[]{2}, Instruction.IMMEDIATE, "MVI", "MVI E, data 8", 2),
    /**Rotate right through carry (RAL); hex machine code 0x1F.*/
    RotateRightThroughCarry(CARRY, new byte[]{4}, new byte[]{1}, Instruction.NOACCESS, "RAR", "RAR", 1),
    /**Read interrupt masks (RIM), hex machine code 0x20.*/
    ReadInterruptMasks(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.NOACCESS, "RIM", "RIM", 1),
    /**Load register pair immediate HL (LXI H); hex machine code 0x21.*/
    LoadRegisterPairImmediate_H(NOFLAGS, new byte[]{10}, new byte[]{3}, Instruction.IMMEDIATE, "LXI", "LXI H, data 16", 3),
    /**Store HL direct (SHLD), hex machine code 0x22.*/
    StoreHLDirect(NOFLAGS, new byte[]{16}, new byte[]{5}, Instruction.DIRECT, "SHLD", "SHLD address", 3),
    /**Increment register pair HL (INX H); hex machine code 0x23.*/
    IncrementRegisterPair_H(NOFLAGS, new byte[]{6}, new byte[]{1}, Instruction.REGISTER, "INX", "INX H", 1),
    /**Increment register H (INR H); hex machine code 0x24.*/
    IncrementRegister_H(ZERO | SIGN | PARITY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "INR", "INR H", 1),
    /**Decrement register D (DCR H); hex machine code 0x25.*/
    DecrementRegister_H(ZERO | SIGN | PARITY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "DCR", "DCR H", 1),
    /**Move immediate H (MVI H); hex machine code 0x26.*/
    MoveImmediate_H(NOFLAGS, new byte[]{7}, new byte[]{2}, Instruction.IMMEDIATE, "MVI", "MVI H, data 8", 2),
    /**Decimal adjust accumulator (DAA); hex machine code 0x27.*/
    DecimalAdjustAccumulator(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.NOACCESS, "DAA", "DAA", 1),
    /**Unused instruction at slot 0x28.*/
    @Deprecated Unused_28,
    /**Double/direct add register pair HL to HL (DAD H); hex machine code 0x29.*/
    DoubleAddRegisterPair_H(CARRY, new byte[]{10}, new byte[]{3}, Instruction.REGISTER, "DAD", "DAD H", 1),
    /**Load HL direct (SHLD), hex machine code 0x2A.*/
    LoadHLDirect(NOFLAGS, new byte[]{16}, new byte[]{5}, Instruction.DIRECT, "LHLD", "LHLD address", 3),
    /**Decrement register pair HL (DCX H), hex machine code 0x2B.*/
    DecrementRegisterPair_H(NOFLAGS, new byte[]{6}, new byte[]{1}, Instruction.REGISTER, "DCX", "DCX H", 1),
    /**Increment register L (INR L); hex machine code 0x2C.*/
    IncrementRegister_L(ZERO | SIGN | PARITY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "INR", "INR L", 1),
    /**Decrement register L (DCR L); hex machine code 0x2D.*/
    DecrementRegister_L(ZERO | SIGN | PARITY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "DCR", "DCR L", 1),
    /**Move immediate L (MVI L); hex machine code 0x2E.*/
    MoveImmediate_L(NOFLAGS, new byte[]{7}, new byte[]{2}, Instruction.IMMEDIATE, "MVI", "MVI L, data 8", 2),
    /**Complement accumulator (CMA); hex machine code 0x2F.*/
    ComplementAccumulator(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.NOACCESS, "CMA", "CMA", 1),
    /**Set interrupt masks (SIM), hex machine code 0x30.*/
    SetInterruptMasks(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.NOACCESS, "SIM", "SIM", 1),
    /**Load register pair immediate SP (LXI SP); hex machine code 0x31.*/
    LoadRegisterPairImmediate_SP(NOFLAGS, new byte[]{10}, new byte[]{3}, Instruction.IMMEDIATE, "LXI", "LXI SP, data 16", 3),
    /**Store accumulator direct (STA); hex machine code 0x32.*/
    StoreAccumulatorDirect(NOFLAGS, new byte[]{13}, new byte[]{4}, Instruction.DIRECT, "STA", "STA address", 3),
    /**Increment register pair SP (INX SP); hex machine code 0x33.*/
    IncrementRegisterPair_SP(NOFLAGS, new byte[]{6}, new byte[]{1}, Instruction.REGISTER, "INX", "INX SP", 1),
    /**Increment memory (INR M); hex machine code 0x34.*/
    IncrementMemory(ZERO | SIGN | PARITY | AUXILIARY_CARRY, new byte[]{10}, new byte[]{3}, Instruction.REGISTER_INDIRECT, "INR", "INR M", 1),
    /**Decrement memory (DCR M); hex machine code 0x35.*/
    DecrementMemory(ZERO | SIGN | PARITY | AUXILIARY_CARRY, new byte[]{10}, new byte[]{3}, Instruction.REGISTER_INDIRECT, "DCR", "DCR M", 1),
    /**Move immediate to memory (MVI M); hex machine code 0x36.*/
    MoveImmediateToMemory(NOFLAGS, new byte[]{10}, new byte[]{3}, Instruction.IMMEDIATE | Instruction.REGISTER_INDIRECT, "MVI", "MVI M, data 8", 2),
    /**Set carry (STC); hex machine code 0x37.*/
    SetCarry(CARRY, new byte[]{4}, new byte[]{1}, Instruction.NOACCESS, "STC", "STC", 1),
    /**Unused instruction at slot 0x38.*/
    @Deprecated Unused_38,
    /**Double/direct add register pair SP to HL (DAD SP); hex machine code 0x39.*/
    DoubleAddRegisterPair_SP(CARRY, new byte[]{10}, new byte[]{3}, Instruction.REGISTER, "DAD", "DAD SP", 1),
    /**Load accumulator direct (LDA); hex machine code 0x3A.*/
    LoadAccumulatorDirect(NOFLAGS, new byte[]{13}, new byte[]{4}, Instruction.DIRECT, "LDA", "LDA address", 3),
    /**Decrement register pair SP (DCX SP), hex machine code 0x3B.*/
    DecrementRegisterPair_SP(NOFLAGS, new byte[]{6}, new byte[]{1}, Instruction.REGISTER, "DCX", "DCX SP", 1),
    /**Increment register A (INR A); hex machine code 0x3C.*/
    IncrementRegister_A(ZERO | SIGN | PARITY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "INR", "INR A", 1),
    /**Decrement register A (DCR A); hex machine code 0x3D.*/
    DecrementRegister_A(ZERO | SIGN | PARITY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "DCR", "DCR A", 1),
    /**Move immediate A (MVI A); hex machine code 0x3E.*/
    MoveImmediate_A(NOFLAGS, new byte[]{7}, new byte[]{2}, Instruction.IMMEDIATE, "MVI", "MVI A, data 8", 2),
    /**Complement carry (CMC); hex machine code 0x3F.*/
    ComplementCarry(CARRY, new byte[]{4}, new byte[]{1}, Instruction.NOACCESS, "CMC", "CMC", 1),
    /**Move register B from B (MOV B, B); hex machine code 0x40.*/
    Move_B_B(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "MOV", "MOV B, B", 1),
    /**Move register B from C (MOV B, C); hex machine code 0x41.*/
    Move_B_C(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "MOV", "MOV B, C", 1),
    /**Move register B from D (MOV B, D); hex machine code 0x42.*/
    Move_B_D(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "MOV", "MOV B, D", 1),
    /**Move register B from E (MOV B, E); hex machine code 0x43.*/
    Move_B_E(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "MOV", "MOV B, E", 1),
    /**Move register B from H (MOV B, H); hex machine code 0x44.*/
    Move_B_H(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "MOV", "MOV B, H", 1),
    /**Move register B from L (MOV B, L); hex machine code 0x45.*/
    Move_B_L(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "MOV", "MOV B, L", 1),
    /**Move register B from memory (MOV B, M); hex machine code 0x46.*/
    Move_B_M(NOFLAGS, new byte[]{7}, new byte[]{2}, Instruction.REGISTER_INDIRECT, "MOV", "MOV B, M", 1),
    /**Move register B from A (MOV B, A); hex machine code 0x47.*/
    Move_B_A(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "MOV", "MOV B, A", 1),
    /**Move register C from B (MOV C, B); hex machine code 0x48.*/
    Move_C_B(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "MOV", "MOV C, B", 1),
    /**Move register C from C (MOV C, C); hex machine code 0x49.*/
    Move_C_C(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "MOV", "MOV C, C", 1),
    /**Move register C from D (MOV C, D); hex machine code 0x4A.*/
    Move_C_D(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "MOV", "MOV C, D", 1),
    /**Move register C from E (MOV C, E); hex machine code 0x4B.*/
    Move_C_E(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "MOV", "MOV C, E", 1),
    /**Move register C from H (MOV C, H); hex machine code 0x4C.*/
    Move_C_H(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "MOV", "MOV C, H", 1),
    /**Move register C from L (MOV C, L); hex machine code 0x4D.*/
    Move_C_L(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "MOV", "MOV C, L", 1),
    /**Move register C from memory (MOV C, M); hex machine code 0x4E.*/
    Move_C_M(NOFLAGS, new byte[]{7}, new byte[]{2}, Instruction.REGISTER_INDIRECT, "MOV", "MOV C, M", 1),
    /**Move register C from A (MOV C, A); hex machine code 0x4F.*/
    Move_C_A(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "MOV", "MOV C, A", 1),
    /**Move register D from B (MOV D, B); hex machine code 0x50.*/
    Move_D_B(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "MOV", "MOV D, B", 1),
    /**Move register D from C (MOV D, C); hex machine code 0x51.*/
    Move_D_C(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "MOV", "MOV D, C", 1),
    /**Move register D from D (MOV D, D); hex machine code 0x52.*/
    Move_D_D(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "MOV", "MOV D, D", 1),
    /**Move register D from E (MOV D, E); hex machine code 0x53.*/
    Move_D_E(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "MOV", "MOV D, E", 1),
    /**Move register D from H (MOV D, H); hex machine code 0x54.*/
    Move_D_H(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "MOV", "MOV D, H", 1),
    /**Move register D from L (MOV D, L); hex machine code 0x55.*/
    Move_D_L(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "MOV", "MOV D, L", 1),
    /**Move register D from memory (MOV D, M); hex machine code 0x56.*/
    Move_D_M(NOFLAGS, new byte[]{7}, new byte[]{2}, Instruction.REGISTER_INDIRECT, "MOV", "MOV D, M", 1),
    /**Move register D from A (MOV D, A); hex machine code 0x57.*/
    Move_D_A(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "MOV", "MOV D, A", 1),
    /**Move register E from B (MOV E, B); hex machine code 0x58.*/
    Move_E_B(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "MOV", "MOV E, B", 1),
    /**Move register E from C (MOV E, C); hex machine code 0x59.*/
    Move_E_C(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "MOV", "MOV E, C", 1),
    /**Move register E from D (MOV E, D); hex machine code 0x5A.*/
    Move_E_D(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "MOV", "MOV E, D", 1),
    /**Move register E from E (MOV E, E); hex machine code 0x5B.*/
    Move_E_E(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "MOV", "MOV E, E", 1),
    /**Move register E from H (MOV E, H); hex machine code 0x5C.*/
    Move_E_H(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "MOV", "MOV E, H", 1),
    /**Move register E from L (MOV E, L); hex machine code 0x5D.*/
    Move_E_L(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "MOV", "MOV E, L", 1),
    /**Move register E from memory (MOV E, M); hex machine code 0x5E.*/
    Move_E_M(NOFLAGS, new byte[]{7}, new byte[]{2}, Instruction.REGISTER_INDIRECT, "MOV", "MOV E, M", 1),
    /**Move register E from A (MOV E, A); hex machine code 0x5F.*/
    Move_E_A(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "MOV", "MOV E, A", 1),
    /**Move register H from B (MOV H, B); hex machine code 0x60.*/
    Move_H_B(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "MOV", "MOV H, B", 1),
    /**Move register H from C (MOV H, C); hex machine code 0x61.*/
    Move_H_C(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "MOV", "MOV H, C", 1),
    /**Move register H from D (MOV H, D); hex machine code 0x62.*/
    Move_H_D(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "MOV", "MOV H, D", 1),
    /**Move register H from E (MOV H, E); hex machine code 0x63.*/
    Move_H_E(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "MOV", "MOV H, E", 1),
    /**Move register H from H (MOV H, H); hex machine code 0x64.*/
    Move_H_H(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "MOV", "MOV H, H", 1),
    /**Move register H from L (MOV H, L); hex machine code 0x65.*/
    Move_H_L(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "MOV", "MOV H, L", 1),
    /**Move register H from memory (MOV H, M); hex machine code 0x66.*/
    Move_H_M(NOFLAGS, new byte[]{7}, new byte[]{2}, Instruction.REGISTER_INDIRECT, "MOV", "MOV H, M", 1),
    /**Move register H from A (MOV H, A); hex machine code 0x67.*/
    Move_H_A(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "MOV", "MOV H, A", 1),
    /**Move register L from B (MOV L, B); hex machine code 0x68.*/
    Move_L_B(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "MOV", "MOV L, B", 1),
    /**Move register L from C (MOV L, C); hex machine code 0x69.*/
    Move_L_C(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "MOV", "MOV L, C", 1),
    /**Move register L from D (MOV L, D); hex machine code 0x6A.*/
    Move_L_D(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "MOV", "MOV L, D", 1),
    /**Move register L from E (MOV L, E); hex machine code 0x6B.*/
    Move_L_E(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "MOV", "MOV L, E", 1),
    /**Move register L from H (MOV L, H); hex machine code 0x6C.*/
    Move_L_H(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "MOV", "MOV L, H", 1),
    /**Move register L from L (MOV L, L); hex machine code 0x6D.*/
    Move_L_L(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "MOV", "MOV L, L", 1),
    /**Move register L from memory (MOV L, M); hex machine code 0x6E.*/
    Move_L_M(NOFLAGS, new byte[]{7}, new byte[]{2}, Instruction.REGISTER_INDIRECT, "MOV", "MOV L, M", 1),
    /**Move register L from A (MOV L, A); hex machine code 0x6F.*/
    Move_L_A(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "MOV", "MOV L, A", 1),
    /**Move to memory from register B (MOV M, B); hex machine code 0x70.*/
    MoveToMemory_B(NOFLAGS, new byte[]{7}, new byte[]{2}, Instruction.REGISTER_INDIRECT, "MOV", "MOV M, B", 1),
    /**Move to memory from register C (MOV M, C); hex machine code 0x71.*/
    MoveToMemory_C(NOFLAGS, new byte[]{7}, new byte[]{2}, Instruction.REGISTER_INDIRECT, "MOV", "MOV M, C", 1),
    /**Move to memory from register D (MOV M, D); hex machine code 0x72.*/
    MoveToMemory_D(NOFLAGS, new byte[]{7}, new byte[]{2}, Instruction.REGISTER_INDIRECT, "MOV", "MOV M, D", 1),
    /**Move to memory from register E (MOV M, E); hex machine code 0x73.*/
    MoveToMemory_E(NOFLAGS, new byte[]{7}, new byte[]{2}, Instruction.REGISTER_INDIRECT, "MOV", "MOV M, E", 1),
    /**Move to memory from register H (MOV M, H); hex machine code 0x74.*/
    MoveToMemory_H(NOFLAGS, new byte[]{7}, new byte[]{2}, Instruction.REGISTER_INDIRECT, "MOV", "MOV M, H", 1),
    /**Move to memory from register L (MOV M, L); hex machine code 0x75.*/
    MoveToMemory_L(NOFLAGS, new byte[]{7}, new byte[]{2}, Instruction.REGISTER_INDIRECT, "MOV", "MOV M, L", 1),
    /**Halt (HLT); hex machine code 0x76.*/
    Halt(NOFLAGS, new byte[]{5}, new byte[]{2}, Instruction.NOACCESS, "HLT", "HLT", 1),
    /**Move to memory from register A (MOV M, A); hex machine code 0x77.*/
    MoveToMemory_A(NOFLAGS, new byte[]{7}, new byte[]{2}, Instruction.REGISTER_INDIRECT, "MOV", "MOV M, A", 1),
    /**Move register A from B (MOV A, B); hex machine code 0x78.*/
    Move_A_B(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "MOV", "MOV A, B", 1),
    /**Move register A from C (MOV A, C); hex machine code 0x79.*/
    Move_A_C(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "MOV", "MOV A, C", 1),
    /**Move register A from D (MOV A, D); hex machine code 0x7A.*/
    Move_A_D(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "MOV", "MOV A, D", 1),
    /**Move register A from E (MOV A, E); hex machine code 0x7B.*/
    Move_A_E(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "MOV", "MOV A, E", 1),
    /**Move register A from H (MOV A, H); hex machine code 0x7C.*/
    Move_A_H(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "MOV", "MOV A, H", 1),
    /**Move register A from L (MOV A, L); hex machine code 0x7D.*/
    Move_A_L(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "MOV", "MOV A, L", 1),
    /**Move register A from memory (MOV A, M); hex machine code 0x7E.*/
    Move_A_M(NOFLAGS, new byte[]{7}, new byte[]{2}, Instruction.REGISTER_INDIRECT, "MOV", "MOV A, M", 1),
    /**Move register A from A (MOV A, A); hex machine code 0x7F.*/
    Move_A_A(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "MOV", "MOV A, A", 1),
    /**Add register B (ADD B); hex machine code 0x80.*/
    AddRegister_B(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "ADD", "ADD B", 1),
    /**Add register C (ADD C); hex machine code 0x81.*/
    AddRegister_C(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "ADD", "ADD C", 1),
    /**Add register D (ADD D); hex machine code 0x82.*/
    AddRegister_D(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "ADD", "ADD D", 1),
    /**Add register E (ADD E); hex machine code 0x83.*/
    AddRegister_E(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "ADD", "ADD E", 1),
    /**Add register H (ADD H); hex machine code 0x84.*/
    AddRegister_H(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "ADD", "ADD H", 1),
    /**Add register L (ADD L); hex machine code 0x85.*/
    AddRegister_L(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "ADD", "ADD L", 1),
    /**Add memory (ADD M); hex machine code 0x86.*/
    AddMemory(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{7}, new byte[]{2}, Instruction.REGISTER_INDIRECT, "ADD", "ADD M", 1),
    /**Add register A (ADD A); hex machine code 0x87.*/
    AddRegister_A(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "ADD", "ADD A", 1),
    /**Add register with carry B (ADC B); hex machine code 0x88.*/
    AddRegisterWithCarry_B(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "ADC", "ADC B", 1),
    /**Add register with carry C (ADC C); hex machine code 0x89.*/
    AddRegisterWithCarry_C(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "ADC", "ADC C", 1),
    /**Add register with carry D (ADC D); hex machine code 0x8A.*/
    AddRegisterWithCarry_D(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "ADC", "ADC D", 1),
    /**Add register with carry E (ADC E); hex machine code 0x8B.*/
    AddRegisterWithCarry_E(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "ADC", "ADC E", 1),
    /**Add register with carry H (ADC H); hex machine code 0x8C.*/
    AddRegisterWithCarry_H(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "ADC", "ADC H", 1),
    /**Add register with carry L (ADC L); hex machine code 0x8D.*/
    AddRegisterWithCarry_L(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "ADC", "ADC L", 1),
    /**Add memory with carry (ADC M); hex machine code 0x8E.*/
    AddMemoryWithCarry(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{7}, new byte[]{2}, Instruction.REGISTER_INDIRECT, "ADC", "ADC M", 1),
    /**Add register with carry A (ADC A); hex machine code 0x8F.*/
    AddRegisterWithCarry_A(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "ADC", "ADC A", 1),
    /**Subtract register B (SUB B); hex machine code 0x90.*/
    SubtractRegister_B(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "SUB", "SUB B", 1),
    /**Subtract register C (SUB C); hex machine code 0x91.*/
    SubtractRegister_C(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "SUB", "SUB C", 1),
    /**Subtract register D (SUB D); hex machine code 0x92.*/
    SubtractRegister_D(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "SUB", "SUB D", 1),
    /**Subtract register E (SUB E); hex machine code 0x93.*/
    SubtractRegister_E(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "SUB", "SUB E", 1),
    /**Subtract register H (SUB H); hex machine code 0x94.*/
    SubtractRegister_H(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "SUB", "SUB H", 1),
    /**Subtract register L (SUB L); hex machine code 0x95.*/
    SubtractRegister_L(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "SUB", "SUB L", 1),
    /**Subtract memory (SUB M); hex machine code 0x96.*/
    SubtractMemory(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{7}, new byte[]{2}, Instruction.REGISTER_INDIRECT, "SUB", "SUB M", 1),
    /**Subtract register A (SUB A); hex machine code 0x97.*/
    SubtractRegister_A(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "SUB", "SUB A", 1),
    /**Subtract register with borrow B (SBB B); hex machine code 0x98.*/
    SubtractRegisterWithBorrow_B(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "SBB", "SBB B", 1),
    /**Subtract register with borrow C (SBB C); hex machine code 0x99.*/
    SubtractRegisterWithBorrow_C(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "SBB", "SBB C", 1),
    /**Subtract register with borrow D (SBB D); hex machine code 0x9A.*/
    SubtractRegisterWithBorrow_D(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "SBB", "SBB D", 1),
    /**Subtract register with borrow E (SBB E); hex machine code 0x9B.*/
    SubtractRegisterWithBorrow_E(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "SBB", "SBB E", 1),
    /**Subtract register with borrow H (SBB H); hex machine code 0x9C.*/
    SubtractRegisterWithBorrow_H(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "SBB", "SBB H", 1),
    /**Subtract register with borrow L (SBB L); hex machine code 0x9D.*/
    SubtractRegisterWithBorrow_L(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "SBB", "SBB L", 1),
    /**Subtract memory with borrow (SBB M); hex machine code 0x9E.*/
    SubtractMemoryWithBorrow(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{7}, new byte[]{2}, Instruction.REGISTER_INDIRECT, "SBB", "SBB M", 1),
    /**Subtract register with borrow A (SBB A); hex machine code 0x9F.*/
    SubtractRegisterWithBorrow_A(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "SBB", "SBB A", 1),
    /**AND register B (ANA B); hex machine code 0xA0.*/
    ANDRegister_B(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "ANA", "ANA B", 1),
    /**AND register C (ANA C); hex machine code 0xA1.*/
    ANDRegister_C(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "ANA", "ANA C", 1),
    /**AND register D (ANA D); hex machine code 0xA2.*/
    ANDRegister_D(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "ANA", "ANA D", 1),
    /**AND register E (ANA E); hex machine code 0xA3.*/
    ANDRegister_E(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "ANA", "ANA E", 1),
    /**AND register H (ANA H); hex machine code 0xA4.*/
    ANDRegister_H(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "ANA", "ANA H", 1),
    /**AND register L (ANA L); hex machine code 0xA5.*/
    ANDRegister_L(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "ANA", "ANA L", 1),
    /**AND memory (ANA M); hex machine code 0xA6.*/
    ANDMemory(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{7}, new byte[]{2}, Instruction.REGISTER_INDIRECT, "ANA", "ANA M", 1),
    /**AND register A (ANA A); hex machine code 0xA7.*/
    ANDRegister_A(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "ANA", "ANA A", 1),
    /**XOR register B (XRA B); hex machine code 0xA8.*/
    XORRegister_B(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "XRA", "XRA B", 1),
    /**XOR register C (XRA C); hex machine code 0xA9.*/
    XORRegister_C(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "XRA", "XRA C", 1),
    /**XOR register D (XRA D); hex machine code 0xAA.*/
    XORRegister_D(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "XRA", "XRA D", 1),
    /**XOR register E (XRA E); hex machine code 0xAB.*/
    XORRegister_E(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "XRA", "XRA E", 1),
    /**XOR register H (XRA H); hex machine code 0xAC.*/
    XORRegister_H(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "XRA", "XRA H", 1),
    /**XOR register L (XRA L); hex machine code 0xAD.*/
    XORRegister_L(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "XRA", "XRA L", 1),
    /**XOR memory (XRA M); hex machine code 0xAE.*/
    XORMemory(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{7}, new byte[]{2}, Instruction.REGISTER_INDIRECT, "XRA", "XRA M", 1),
    /**XOR register A (XRA A); hex machine code 0xAF.*/
    XORRegister_A(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "XRA", "XRA A", 1),
    /**OR register B (ORA B); hex machine code 0xBO.*/
    ORRegister_B(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "ORA", "ORA B", 1),
    /**OR register C (ORA C); hex machine code 0xB1.*/
    ORRegister_C(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "ORA", "ORA C", 1),
    /**OR register D (ORA D); hex machine code 0xB2.*/
    ORRegister_D(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "ORA", "ORA D", 1),
    /**OR register E (ORA E); hex machine code 0xB3.*/
    ORRegister_E(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "ORA", "ORA E", 1),
    /**OR register H (ORA H); hex machine code 0xB4.*/
    ORRegister_H(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "ORA", "ORA H", 1),
    /**OR register L (ORA L); hex machine code 0xB5.*/
    ORRegister_L(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "ORA", "ORA L", 1),
    /**OR memory (ORA M); hex machine code 0xB6.*/
    ORMemory(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{7}, new byte[]{2}, Instruction.REGISTER_INDIRECT, "ORA", "ORA M", 1),
    /**OR register A (ORA A); hex machine code 0xB7.*/
    ORRegister_A(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "ORA", "ORA A", 1),
    /**Compare register B (CMP B); hex machine code 0xB8.*/
    CompareRegister_B(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "CMP", "CMP B", 1),
    /**Compare register C (CMP C); hex machine code 0xB9.*/
    CompareRegister_C(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "CMP", "CMP C", 1),
    /**Compare register D (CMP D); hex machine code 0xBA.*/
    CompareRegister_D(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "CMP", "CMP D", 1),
    /**Compare register E (CMP E); hex machine code 0xBB.*/
    CompareRegister_E(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "CMP", "CMP E", 1),
    /**Compare register H (CMP H); hex machine code 0xBC.*/
    CompareRegister_H(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "CMP", "CMP H", 1),
    /**Compare register L (CMP L); hex machine code 0xBD.*/
    CompareRegister_L(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "CMP", "CMP L", 1),
    /**Compare memory (CMP M); hex machine code 0xBE.*/
    CompareMemory(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{7}, new byte[]{2}, Instruction.REGISTER_INDIRECT, "CMP", "CMP M", 1),
    /**Compare register A (CMP A); hex machine code 0xBF.*/
    CompareRegister_A(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "CMP", "CMP A", 1),
    /**Return on nonzero (RNZ); hex machine code 0xC0.*/
    ReturnOnNonZero(NOFLAGS, new byte[]{6,12}, new byte[]{1,3}, Instruction.REGISTER_INDIRECT, "RNZ", "RNZ", 1),
    /**Pop BC (POP B); hex machine code 0xC1.*/
    Pop_B(NOFLAGS, new byte[]{10}, new byte[]{3}, Instruction.REGISTER_INDIRECT, "POP", "POP B", 1),
    /**Jump on nonzero (JNZ); hex machine code 0xC2.*/
    JumpOnNonZero(NOFLAGS, new byte[]{7,10}, new byte[]{2,3}, Instruction.IMMEDIATE, "JNZ", "JNZ address", 3),
    /**Jump (JMP); hex machine code 0xC3.*/
    Jump(NOFLAGS, new byte[]{10}, new byte[]{3}, Instruction.IMMEDIATE, "JMP", "JMP address", 3),
    /**Call on nonzero (CNZ); hex machine code 0xC4.*/
    CallOnNonZero(NOFLAGS, new byte[]{9,18}, new byte[]{2,5}, Instruction.IMMEDIATE | Instruction.REGISTER_INDIRECT, "CNZ", "CNZ address", 3),
    /**Push BC (PUSH B); hex machine code 0xC5.*/
    Push_B(NOFLAGS, new byte[]{12}, new byte[]{3}, Instruction.REGISTER_INDIRECT, "PUSH", "PUSH B", 1),
    /**Add immediate (ADI); hex machine code 0xC6.*/
    AddImmediate(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{7}, new byte[]{2}, Instruction.IMMEDIATE, "ADI", "ADI data 8", 2),
    /**Restart 0 (RST 0); hex machine code 0xC7.*/
    Restart_0(NOFLAGS, new byte[]{12}, new byte[]{3}, Instruction.REGISTER_INDIRECT, "RST", "RST 0", 1),
    /**Return on zero (RZ); hex machine code 0xC8.*/
    ReturnOnZero(NOFLAGS, new byte[]{6,12}, new byte[]{1,3}, Instruction.REGISTER_INDIRECT, "RZ", "RZ", 1),
    /**Return (RET); hex machine code 0xC9.*/
    Return(NOFLAGS, new byte[]{10}, new byte[]{3}, Instruction.REGISTER_INDIRECT, "RET", "RET", 1),
    /**Jump on zero (JZ); hex machine code 0xCA.*/
    JumpOnZero(NOFLAGS, new byte[]{7,10}, new byte[]{2,3}, Instruction.IMMEDIATE, "JZ", "JZ address", 3),
    /**Unused instruction at slot 0xCB.*/
    @Deprecated Unused_CB,
    /**Call on zero (CZ); hex machine code 0xCC.*/
    CallOnZero(NOFLAGS, new byte[]{9,18}, new byte[]{2,5}, Instruction.IMMEDIATE | Instruction.REGISTER_INDIRECT, "CZ", "CZ address", 3),
    /**Call (CALL); hex machine code 0xCD.*/
    Call(NOFLAGS, new byte[]{18}, new byte[]{5}, Instruction.IMMEDIATE | Instruction.REGISTER_INDIRECT, "CALL", "CALL address", 3),
    /**Add immediate with carry (ACI); hex machine code 0xCE.*/
    AddImmediateWithCarry(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{7}, new byte[]{2}, Instruction.IMMEDIATE, "ACI", "ACI data 8", 2),
    /**Restart 1 (RST 1); hex machine code 0xCF.*/
    Restart_1(NOFLAGS, new byte[]{12}, new byte[]{3}, Instruction.REGISTER_INDIRECT, "RST", "RST 1", 1),
    /**Return on NOT carry flag (RNC); hex machine code 0xD0.*/
    ReturnOnNotCarry(NOFLAGS, new byte[]{6,12}, new byte[]{1,3}, Instruction.REGISTER_INDIRECT, "RNC", "RNC", 1),
    /**Pop DE (POP D); hex machine code 0xD1.*/
    Pop_D(NOFLAGS, new byte[]{10}, new byte[]{3}, Instruction.REGISTER_INDIRECT, "POP", "POP D", 1),
    /**Jump on NOT carry flag (JNC); hex machine code 0xD2.*/
    JumpOnNotCarry(NOFLAGS, new byte[]{7,10}, new byte[]{2,3}, Instruction.IMMEDIATE, "JNC", "JNC address", 3),
    /**Output (OUT); hex machine code 0xD3.*/
    Output(NOFLAGS, new byte[]{10}, new byte[]{3}, Instruction.DIRECT, "OUT", "OUT port", 2),
    /**Call on NOT carry flag (CNC); hex machine code 0xD4.*/
    CallOnNotCarry(NOFLAGS, new byte[]{9,18}, new byte[]{2,5}, Instruction.IMMEDIATE | Instruction.REGISTER_INDIRECT, "CNC", "CNC address", 3),
    /**Push DE (PUSH D); hex machine code 0xD5.*/
    Push_D(NOFLAGS, new byte[]{12}, new byte[]{3}, Instruction.REGISTER_INDIRECT, "PUSH", "PUSH D", 1),
    /**Subtract immediate (SUI); hex machine code 0xD6.*/
    SubtractImmediate(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{7}, new byte[]{2}, Instruction.IMMEDIATE, "SUI", "SUI data 8", 2),
    /**Restart 2 (RST 2); hex machine code 0xD7.*/
    Restart_2(NOFLAGS, new byte[]{12}, new byte[]{3}, Instruction.REGISTER_INDIRECT, "RST", "RST 2", 1),
    /**Return on carry flag (RC); hex machine code 0xD8.*/
    ReturnOnCarry(NOFLAGS, new byte[]{6,12}, new byte[]{1,3}, Instruction.REGISTER_INDIRECT, "RC", "RC", 1),
    /**Unused instruction at slot 0xD9.*/
    @Deprecated Unused_D9,
    /**Jump on carry flag (JC); hex machine code 0xDA.*/
    JumpOnCarry(NOFLAGS, new byte[]{7,10}, new byte[]{2,3}, Instruction.IMMEDIATE, "JC", "JC address", 3),
    /**Input (IN); hex machine code 0xDB.*/
    Input(NOFLAGS, new byte[]{10}, new byte[]{3}, Instruction.DIRECT, "IN", "IN port", 2),
    /**Call on carry flag (CC); hex machine code 0xDC.*/
    CallOnCarry(NOFLAGS, new byte[]{9,18}, new byte[]{2,5}, Instruction.IMMEDIATE | Instruction.REGISTER_INDIRECT, "CC", "CC address", 3),
    /**Unused instruction at slot 0xDD.*/
    @Deprecated Unused_DD,
    /**Subtract immediate with borrow (SBI); hex machine code 0xDE.*/
    SubtractImmediateWithBorrow(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{7}, new byte[]{2}, Instruction.IMMEDIATE, "SBI", "SBI data 8", 2),
    /**Restart 3 (RST 3); hex machine code 0xDF.*/
    Restart_3(NOFLAGS, new byte[]{12}, new byte[]{3}, Instruction.REGISTER_INDIRECT, "RST", "RST 3", 1),
    /**Return on parity odd (RPO); hex machine code 0xE0.*/
    ReturnOnParityOdd(NOFLAGS, new byte[]{6,12}, new byte[]{1,3}, Instruction.REGISTER_INDIRECT, "RPO", "RPO", 1),
    /**Pop HL (POP H); hex machine code 0xE1.*/
    Pop_H(NOFLAGS, new byte[]{10}, new byte[]{3}, Instruction.REGISTER_INDIRECT, "POP", "POP H", 1),
    /**Jump on parity odd (JPO); hex machine code 0xE2.*/
    JumpOnParityOdd(NOFLAGS, new byte[]{7,10}, new byte[]{2,3}, Instruction.IMMEDIATE, "JPO", "JPO address", 3),
    /**Exchange stack top with HL (XTHL); hex machine code 0xE3.*/
    ExchangeStackTopWithHL(NOFLAGS, new byte[]{16}, new byte[]{5}, Instruction.REGISTER_INDIRECT, "XTHL", "XTHL", 1),
    /**Call on parity odd (CPO); hex machine code 0xE4.*/
    CallOnParityOdd(NOFLAGS, new byte[]{9,18}, new byte[]{2,5}, Instruction.IMMEDIATE | Instruction.REGISTER_INDIRECT, "CPO", "CPO address", 3),
    /**Push HL (PUSH H); hex machine code 0xE5.*/
    Push_H(NOFLAGS, new byte[]{12}, new byte[]{3}, Instruction.REGISTER_INDIRECT, "PUSH", "PUSH H", 1),
    /**AND immediate (ANI); hex machine code 0xE6.*/
    ANDImmediate(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{7}, new byte[]{2}, Instruction.IMMEDIATE, "ANI", "ANI data 8", 2),
    /**Restart 4 (RST 4); hex machine code 0xE7.*/
    Restart_4(NOFLAGS, new byte[]{12}, new byte[]{3}, Instruction.REGISTER_INDIRECT, "RST", "RST 4", 1),
    /**Return on parity even (RPE); hex machine code 0xE8.*/
    ReturnOnParityEven(NOFLAGS, new byte[]{6,12}, new byte[]{1,3}, Instruction.REGISTER_INDIRECT, "RPE", "RPE", 1),
    /**Jump HL indirect, <strong>OR</strong> move HL to PC (PCHL); hex machine code 0xE9.*/
    PC_HL(NOFLAGS, new byte[]{6}, new byte[]{1}, Instruction.REGISTER, "PCHL", "PCHL", 1),
    /**Jump on parity even (JPE); hex machine code 0xEA.*/
    JumpOnParityEven(NOFLAGS, new byte[]{7,10}, new byte[]{2,3}, Instruction.IMMEDIATE, "JPE", "JPE address", 3),
    /**Exchange HL with DE (XCHG); hex machine code 0xEB.*/
    ExchangeHLWithDE(NOFLAGS, new byte[]{10}, new byte[]{3}, Instruction.DIRECT, "XCHG", "XCHG", 1),
    /**Call on parity even (CPE); hex machine code 0xEC.*/
    CallOnParityEven(NOFLAGS, new byte[]{9,18}, new byte[]{2,5}, Instruction.IMMEDIATE | Instruction.REGISTER_INDIRECT, "CPE", "CPE address", 3),
    /**Unused instruction at slot 0xED.*/
    @Deprecated Unused_ED,
    /**XOR immediate (XRI); hex machine code 0xEE.*/
    XORImmediate(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{7}, new byte[]{2}, Instruction.IMMEDIATE, "XRI", "XRI data 8", 2),
    /**Restart 5 (RST 5); hex machine code 0xEF.*/
    Restart_5(NOFLAGS, new byte[]{12}, new byte[]{3}, Instruction.REGISTER_INDIRECT, "RST", "RST 5", 1),
    /**Return on Plus (RP); hex machine code 0xF0.*/
    ReturnOnPlus(NOFLAGS, new byte[]{6,12}, new byte[]{1,3}, Instruction.REGISTER_INDIRECT, "RP", "RP", 1),
    /**Pop processor status word (POP PSW); hex machine code 0xF1.*/
    PopProcessorStatusWord(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{10}, new byte[]{3}, Instruction.REGISTER_INDIRECT, "POP", "POP PSW", 1),
    /**Jump on Plus (JP); hex machine code 0xF2.*/
    JumpOnPlus(NOFLAGS, new byte[]{7,10}, new byte[]{2,3}, Instruction.IMMEDIATE, "JP", "JP address", 3),
    /**Disable interrupts (DI); hex machine code 0xF3.*/
    DisableInterrupts(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.NOACCESS, "DI", "DI", 1),
    /**Call on Plus (CP); hex machine code 0xF4.*/
    CallOnPlus(NOFLAGS, new byte[]{9,18}, new byte[]{2,5}, Instruction.IMMEDIATE | Instruction.REGISTER_INDIRECT, "CP", "CP address", 3),
    /**Push processor status word (PUSH PSW); hex machine code 0xF5.*/
    PushProcessorStatusWord(NOFLAGS, new byte[]{12}, new byte[]{3}, Instruction.REGISTER_INDIRECT, "PUSH", "PUSH PSW", 1),
    /**OR immediate (ORI); hex machine code F6.*/
    ORImmediate(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{7}, new byte[]{2}, Instruction.IMMEDIATE, "ORI", "ORI data 8", 2),
    /**Restart 6 (RST 6); hex machine code 0xF7.*/
    Restart_6(NOFLAGS, new byte[]{12}, new byte[]{3}, Instruction.REGISTER_INDIRECT, "RST", "RST 6", 1),
    /**Return on Minus (RM); hex machine code 0xF8.*/
    ReturnOnMinus(NOFLAGS, new byte[]{6,12}, new byte[]{1,3}, Instruction.REGISTER_INDIRECT, "RM", "RM", 1),
    /**Move HL to SP (SPHL); hex machine code 0xF9.*/
    SP_HL(NOFLAGS, new byte[]{6}, new byte[]{1}, Instruction.REGISTER, "SPHL", "SPHL", 1),
    /**Jump on Minus (JM); hex machine code 0xFA.*/
    JumpOnMinus(NOFLAGS, new byte[]{7,10}, new byte[]{2,3}, Instruction.IMMEDIATE, "JM", "JM address", 3),
    /**Enable interrupts (EI); hex machine code 0xFB.*/
    EnableInterrupts(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.NOACCESS, "EI", "EI", 1),
    /**Call on Minus (CM); hex machine code 0xFC.*/
    CallOnMinus(NOFLAGS, new byte[]{9,18}, new byte[]{2,5}, Instruction.IMMEDIATE | Instruction.REGISTER_INDIRECT, "CM", "CM address", 3),
    /**Unused instruction at slot 0xFD.*/
    @Deprecated Unused_FD,
    /**Compare immediate (CPI); hex machine code FE.*/
    CompareImmediate(ZERO | SIGN | PARITY | CARRY | AUXILIARY_CARRY, new byte[]{7}, new byte[]{2}, Instruction.IMMEDIATE, "CPI", "CPI data 8", 2),
    /**Restart 7 (RST 7); hex machine code 0xFF.*/
    Restart_7(NOFLAGS, new byte[]{12}, new byte[]{3}, Instruction.REGISTER_INDIRECT, "RST", "RST 7", 1);
    
    //Bit masks for flag registers affected by this instruction.
    
    /** Flag mask; the bits in {@code flagMask} which are set indicate the particular 
     *  flags in the flag register influenced by this instruction. The masks are defined in the 
     * {@link simulators.Intel8085.core.FlagRegisterConstants} interface. This is 
     * {@link simulators.Intel8085.core.FlagRegisterConstants#NOFLAGS} (no flags affected) on instructions
     * which do not affect flags (as well as unused instructions).
     * @see #isSignFlagAffected() 
     * @see #isZeroFlagAffected() 
     * @see #isAuxiliaryCarryFlagAffected()
     * @see #isParityFlagAffected() 
     * @see #isCarryFlagAffected() 
     * @see #isAnyFlagAffected() 
     */
    public final byte flagMask;
    /** The number of bytes required by this instruction; with a minimum of 1. Will be 0 for instruction slots which
     * are unused.*/
    public final byte bytesRequired;
    
    /** Holds an array, containing the number of T-states (clock pulses) used by this instruction. Usually the length
     * of this array is 1, the single number indicating the number of T-states used by this instruction. If any 
     * instruction has multiple possible values for this property (for example a conditional instruction) then all
     * possible values are contained in this array in ascending order. This is {@code null} in case of unused instruction 
     * slots.
     * @see #Tstates() 
     */
    private final byte[] Tstates;
    /** Holds an array, containing the number of machine cycles used by this instruction. Usually the length
     * of this array is 1, the single number indicating the number of machine cycles used by this instruction. If any 
     * instruction has multiple possible values for this property (for example a conditional instruction) then all
     * possible values are contained in this array in ascending order. This is {@code null} in case of unused instruction
     * slots.
     * @see #cycles() 
     */
    private final byte[] cycles;
    
    //List addressing modes. We will use bit masks for this.
    
    /** Register addressing mode; the instruction will specify a register which is to be accessed.
     * @see #unused_addressingModes */
    private static final byte REGISTER          = (byte)0b0000_0001;
    /** Immediate addressing mode; the instruction will be immediately followed by the required operand data.
     * @see #unused_addressingModes */
    private static final byte IMMEDIATE         = (byte)0b0000_0010;
    /** Register indirect addressing mode; the operand data must be accessed from memory, the address of which is 
     * located in a register (usually the HL pair)
     * @see #unused_addressingModes*/
    private static final byte REGISTER_INDIRECT = (byte)0b0000_0100;
    /** Direct addressing mode; the operand data must be accessed from memory, the address of which is supplied 
     * immediately after where this instruction is located.
     * @see #unused_addressingModes*/
    private static final byte DIRECT            = (byte)0b0000_1000;
    /** Indicates that no operand can be specified for this instruction. 
     * @see #unused_addressingModes*/
    private static final byte NOACCESS          = (byte)0b0000_0000;
    /** Bit flag to {@link #unused_addressingModes} to indicate that this instruction (slot) is unused.*/
    private static final byte UNUSED            = (byte)0b1000_0000;
    
    /** Holds the addressing modes used by this instruction as a bit pattern; and a bit flag at the most significant
     * position to indicate whether this instruction is unused or not.
     * @see #REGISTER
     * @see #IMMEDIATE
     * @see #REGISTER_INDIRECT
     * @see #DIRECT
     * @see #NOACCESS
     * @see #UNUSED
     * @see #isRegisterAddressingUsed() 
     * @see #isImmediateAddressingUsed() 
     * @see #isRegisterIndirectAddressingUsed() 
     * @see #isDirectAddressingUsed() 
     * @see #isAnyAddressingUsed() 
     * @see #isUnusedInstructionSlot()
     */
    private final byte unused_addressingModes;
    /** Holds the (uppercase) mnemonic for this instruction (which will be used in assembly language code). This is 
     * {@code null} if this particular instruction is unused by the 8085.
     * 
     * <p><strong>Note:</strong> for multiple combinations for the same basic instruction (say the 63 combinations of
     * {@code MOV} in a single opcode byte) the value for this field is the same ({@code "MOV"}). The {@link #useDesc} 
     * will be different in that case.</p>
     */
    public final String mnemonic;
    /** Holds a sample use description of how the instruction will be written in assembly code (for example, {@code MOV
     * A, B} or {@code LDA address}). For instructions that indicate the opcode and operands in a single byte (like the
     * {@code MOV} instruction mentioned) this value will also contain the operands ({@code "MOV A, B"} for example). 
     * For instruction slots that are unused by the 8085; this field will be {@code null}.
     * @see #mnemonic
     */
    public final String useDesc;
    
    /** Creates an unused instruction slot. These instructions (opcodes) are invalid and are (usually) never accepted
     *  by an 8085 implementation.*/
    private Instruction() {
        flagMask = NOFLAGS; unused_addressingModes = UNUSED;
        mnemonic = useDesc = null; Tstates = cycles = null; bytesRequired = 0;
    }
    /** Creates a (valid) instruction slot.
     * @param flagMask Bit mask indicating the flags that are affected by this instruction. See 
     * {@link simulators.Intel8085.core.FlagRegisterConstants}.
     * @param Tstates Array containing the different number of T-states (clock pulses) required by this instruction.
     * See {@link #Tstates}.
     * @param cycles Array containing the different number of machine cycles required by this instruction. See
     * {@link #cycles}.
     * @param addressingModes Bit mask indicating the various addressing modes used by this instruction. See 
     * {@link #unused_addressingModes}.
     * @param mnemonic Instruction mnemonic for this instruction (recognized by the assembler). See {@link #mnemonic}.
     * @param useDesc Use description for the instruction (as an example for assembler source code). See {@link #useDesc}.
     * @param bytesReqd Number of bytes required by this instruction. See {@link #bytesRequired}.
     */
    private Instruction(int flagMask, byte[] Tstates, byte[] cycles, int addressingModes, String mnemonic, String useDesc, int bytesReqd) {
        this.flagMask = (byte)(flagMask & 0xFF); 
        this.Tstates = Tstates; //no copy because this is internal to this enum class.
        this.cycles = cycles;
        unused_addressingModes = (byte)(addressingModes & 0x7F); //do not allow the highest significant bit to pass
        this.mnemonic = mnemonic; this.useDesc = useDesc;
        bytesRequired = (byte)(bytesReqd & 0x7F);
    }
    
    /** Returns the actual bytecode or the machine code of this instruction (the first byte for multi-byte 
     * instructions) which is directly recognized by the 8085. This method throws an {@code UnsupportedOperationException} if
     * this instruction slot is not used/recognized by the 8085. The opcode can also be obtained by taking the least
     * significant byte of the integer returned by the {@link #ordinal()} method.
     * 
     * <p>A possible implementation of this method is:
     * <pre><code>
     * if(isUnusedInstructionSlot()) throw new UnsupportedOperationException();
     * else return (byte)(ordinal() &amp; 0xFF);
     * </code></pre></p>
     * @return the bytecode/opcode of this instruction
     * @throws UnsupportedOperationException if this is an unused instruction slot not recognized by the 8085
     * @see #isUnusedInstructionSlot() 
     */
    public final byte opcode() {
        if(isUnusedInstructionSlot()) throw new UnsupportedOperationException();
        else return (byte)(ordinal() & 0xFF);
    }
    
    /** Returns {@code true} if the sign (S) flag of the 8085 is affected by this instruction, otherwise {@code false.} 
     * @return a boolean indicating whether the S flag is affected by this instruction or not
     * @see simulators.Intel8085.core.FlagRegisterConstants#SIGN
     * @see #flagMask
     */
    public final boolean isSignFlagAffected() {return (flagMask & SIGN) != 0;}
    /** Returns {@code true} if the zero (Z) flag of the 8085 is affected by this instruction, otherwise {@code false.} 
     * @return a boolean indicating whether the Z flag is affected by this instruction or not
     * @see simulators.Intel8085.core.FlagRegisterConstants#ZERO
     * @see #flagMask
     */
    public final boolean isZeroFlagAffected() {return (flagMask & ZERO) != 0;}
    /** Returns {@code true} if the auxiliary carry (AC) flag of the 8085 is affected by this instruction, 
     * otherwise {@code false.} 
     * @return a boolean indicating whether the AC flag is affected by this instruction or not
     * @see simulators.Intel8085.core.FlagRegisterConstants#AUXILIARY_CARRY
     * @see #flagMask
     */
    public final boolean isAuxiliaryCarryFlagAffected() {return (flagMask & AUXILIARY_CARRY) != 0;}
    /** Returns {@code true} if the parity (P) flag of the 8085 is affected by this instruction, otherwise {@code false.} 
     * @return a boolean indicating whether the P flag is affected by this instruction or not
     * @see simulators.Intel8085.core.FlagRegisterConstants#PARITY
     * @see #flagMask
     */
    public final boolean isParityFlagAffected() {return (flagMask & PARITY) != 0;}
    /** Returns {@code true} if the carry (CY) flag of the 8085 is affected by this instruction, otherwise {@code false.} 
     * @return a boolean indicating whether the CY flag is affected by this instruction or not
     * @see simulators.Intel8085.core.FlagRegisterConstants#CARRY
     * @see #flagMask
     */
    public final boolean isCarryFlagAffected() {return (flagMask & CARRY) != 0;}
    /** Returns {@code false} if this instruction does not affect any flags of the 8085, otherwise {@code true}.
     * @return a boolean indicating whether any flag is affected by this instruction or not
     * @see simulators.Intel8085.core.FlagRegisterConstants#NOFLAGS
     * @see #flagMask
     */
    public final boolean isAnyFlagAffected() {return flagMask != NOFLAGS;}
    
    /** Returns an array containing the different number of T-states (clock pulses) required for this instruction. 
     * Usually the length of the returned array is 1. For instructions that can have multiple values for this property
     * (for example conditional instructions) the returned array holds all possible values for T-states in ascending order.
     * Changes made to the returned array do not affect any enum constants of this enum class. For unused instruction
     * slots, this method returns {@code null}.
     * @return all possible values of the number of T-states required by this instruction
     * @see #cycles()
     */
    public final byte[] Tstates() {return Arrays.copyOf(Tstates, Tstates.length);}
    /** Returns an array containing the different number of machine cycles required for this instruction. 
     * Usually the length of the returned array is 1. For instructions that can have multiple values for this property
     * (for example conditional instructions) the returned array holds all possible values for T-states in ascending order.
     * Changes made to the returned array do not affect any enum constants of this enum class. For unused instruction
     * slots, this method returns {@code null}.
     * @return all possible values of the number of machine cycles required by this instruction
     * @see #Tstates()
     */
    public final byte[] cycles() {return Arrays.copyOf(cycles, cycles.length);}
    
    /** Returns {@code true} if register addressing mode is used by this instruction, otherwise {@code false}. 
     * Register addressing is used when the instruction directly refers to a register as an operand.
     * @return a boolean indicating if this instruction uses register addressing mode
     * @see #isImmediateAddressingUsed() 
     * @see #isRegisterIndirectAddressingUsed() 
     * @see #isDirectAddressingUsed() 
     * @see #isAnyAddressingUsed() 
     */
    public final boolean isRegisterAddressingUsed() {return (unused_addressingModes & REGISTER) != 0;}
    /** Returns {@code true} if immediate addressing mode is used by this instruction, otherwise {@code false}. 
     * Immediate addressing is used when an operand for an instruction is available immediately following the 
     * instruction opcode.
     * @return a boolean indicating if this instruction uses immediate addressing mode
     * @see #isRegisterAddressingUsed() 
     * @see #isRegisterIndirectAddressingUsed() 
     * @see #isDirectAddressingUsed() 
     * @see #isAnyAddressingUsed() 
     */
    public final boolean isImmediateAddressingUsed() {return (unused_addressingModes & IMMEDIATE) != 0;}
    /** Returns {@code true} if register indirect addressing mode is used by this instruction, otherwise {@code false}.
     * Register indirect addressing mode indicates that the operand for this instruction is to be accessed from memory,
     * the address of which is available in a register pair (usually HL).
     * @return a boolean indicating if this instruction uses register indirect addressing mode
     * @see #isRegisterAddressingUsed() 
     * @see #isImmediateAddressingUsed() 
     * @see #isDirectAddressingUsed() 
     * @see #isAnyAddressingUsed() 
     */
    public final boolean isRegisterIndirectAddressingUsed() {return (unused_addressingModes & REGISTER_INDIRECT) != 0;}
    /** Returns {@code true} if direct addressing mode is used by this instruction, otherwise {@code false}. 
     * Direct addressing mode indicates that the operand for this instruction is to be accessed from memory, the 
     * address of which is available immediately following the instruction opcode.
     * @return a boolean indicating if this instruction uses direct addressing mode
     * @see #isRegisterAddressingUsed() 
     * @see #isImmediateAddressingUsed() 
     * @see #isRegisterIndirectAddressingUsed()  
     * @see #isAnyAddressingUsed() 
     */
    public final boolean isDirectAddressingUsed() {return (unused_addressingModes & DIRECT) != 0;}
    /** Returns {@code true} if any operand addressing mode is used by this instruction. This is {@code false} if this 
     * instruction does not take any operands.
     * @see #isRegisterAddressingUsed() 
     * @see #isImmediateAddressingUsed() 
     * @see #isRegisterIndirectAddressingUsed() 
     * @see #isDirectAddressingUsed() 
     */
    public final boolean isAnyAddressingUsed() {return unused_addressingModes != NOACCESS;}
    
    /** Returns {@code true} in the case in which this instruction slot is not used or understood by the 8085. There
     * are 10 such slots in the available 256 different opcodes available in 8 bits. If this is a valid instruction
     * understood by the 8085, the function returns {@code false}.
     * @return a boolean indicating whether this instruction slot is unused or not.
     */
    public final boolean isUnusedInstructionSlot() {return unused_addressingModes == UNUSED;}
    
    /** Returns a textual representation of the instruction. This representation lists the {@link #mnemonic}, 
     * {@link #useDesc}, {@link #opcode()} in 0-padded right-justified hexadecimal (in caps), flags affected
     * by this instruction, {@link #bytesRequired}, {@link #Tstates}, {@link #cycles}, and the addressing modes
     * used by this instruction.
     * <p>For unused instruction slots, the string returned simply says "Unused" along with the unused hex machine code.</p>
     * @return a textual representation of this instruction
     * @see #flagMask
     * @see #isRegisterAddressingUsed() 
     * @see #isImmediateAddressingUsed() 
     * @see #isRegisterIndirectAddressingUsed() 
     * @see #isDirectAddressingUsed() 
     * @see #isAnyAddressingUsed() 
     * @see #isUnusedInstructionSlot()
     */
    @Override public final String toString() {
        if(isUnusedInstructionSlot()) return String.format("Unused[hex=%02X]", ordinal());
        else {
            StringBuilder b = new StringBuilder(super.toString()).append("[")
                    .append(mnemonic)
                    .append(", useDesc=\"").append(useDesc).append("\"")
                    .append(String.format(", hex=%02X", ordinal()))
                    .append(", flags=[").append(FlagRegisterConstants.createFlagDescription(flagMask)).append("]")
                    .append(", bytesRequired=").append(bytesRequired)
                    .append(", tstates=").append(Arrays.toString(Tstates))
                    .append(", cycles=").append(Arrays.toString(cycles));
            ArrayDeque<String> temp = new ArrayDeque<>();
            if(isRegisterAddressingUsed()) temp.addLast("Register");
            if(isImmediateAddressingUsed()) temp.addLast("Immediate");
            if(isRegisterIndirectAddressingUsed()) temp.addLast("Register Indirect");
            if(isDirectAddressingUsed()) temp.addLast("Direct");
            if(temp.isEmpty()) b = b.append(", addressingModes=none]");
            else b = b.append(", addressingModes=").append(temp).append("]");
            return b.toString();
        }
    }
    
    /** A set containing the valid instruction set of the 8085 (that is, not containing the unused instruction slots).
     * The {@link java.util.Set#size()} of this set is 246 and it is unmodifiable and sorted in ascending order with respect to
     * instruction opcode.
     * @see #opcode() 
     */
    public static final Set<Instruction> INSTRUCTION_SET
            = Collections.unmodifiableSet(EnumSet.complementOf(EnumSet.of(Unused_08, Unused_10, Unused_18, Unused_28, 
                    Unused_38, Unused_CB, Unused_D9, Unused_DD, Unused_ED, Unused_FD)));
    /** This is equal to {@link #values()} and is simply used as a mapping from opcode (as index) to Instruction.*/
    private static final Instruction[] opcode2Instruction = Instruction.values();
    
    /** Returns the instruction corresponding to the opcode (first byte) passed as a parameter. This method will throw
     * an {@code IllegalArgumentException} if the given opcode is not understood by the 8085.
     * @param opcode machine code/bytecode of the first byte of the instruction
     * @return the instruction corresponding to the given opcode
     * @throws IllegalArgumentException if the given opcode is not understood by the 8085
     */
    public static final Instruction disassemble(byte opcode) {
        Instruction i = opcode2Instruction[opcode & 0xFF];
        if(i.isUnusedInstructionSlot()) throw new IllegalArgumentException();
        else return i;
    }
}
