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

/**
 * Enumeration listing all possible instructions (1-byte opcodes) for the 8085 
 * in the order of their bit-wise pattern; such that the ordinal value (see {@link java.lang.Enum#ordinal()})
 * has the actual instruction opcode (1st byte) in its lowest significant byte.
 * This means that some of the instructions will be repeated multiple times for
 * multiple combinations (for example the MOV instruction has 63 combinations) and
 * the number of constants for this enumeration is 256; this enum also has <code>Unused_<i>XX</i></code> constants 
 * which indicate slots in the 8-bit code space (hexadecimal code <i>XX</i>) which are not used for any instruction by 
 * the 8085.
 * @author Chirantan Nath
 */
public enum Instruction implements FlagRegisterConstants {//TODO: not finished yet!
    /**No operation (NOP); hex machine code 00.*/
    NoOperation(NOFLAGS, new byte[]{4}, new byte[]{1}, Instruction.NOACCESS, "NOP", "NOP"),
    /**Load register pair immediate BC (LXI B); hex machine code 01.*/
    LoadRegisterPairImmediate_B(NOFLAGS, new byte[]{10}, new byte[]{3}, Instruction.IMMEDIATE, "LXI", "LXI B, data 16"),
    /**Store accumulator indirect BC (STAX B); hex machine code 02.*/
    StoreAccumulatorIndirect_B(NOFLAGS, new byte[]{7}, new byte[]{2}, Instruction.REGISTER_INDIRECT, "STAX", "STAX B"),
    /**Increment register pair BC (INX B); hex machine code 03.*/
    IncrementRegisterPair_B(NOFLAGS, new byte[]{6}, new byte[]{1}, Instruction.REGISTER, "INX", "INX B"),
    /**Increment register B (INR B); hex machine code 04.*/
    IncrementRegister_B(ZERO | SIGN | PARITY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "INR", "INR B"),
    /**Decrement register B (DCR B); hex machine code 05.*/
    DecrementRegister_B(ZERO | SIGN | PARITY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "DCR", "DCR B"),
    /**Move immediate B (MVI B); hex machine code 06.*/
    MoveImmediate_B(NOFLAGS, new byte[]{7}, new byte[]{2}, Instruction.IMMEDIATE, "MVI", "MVI B, data 8"),
    /**Rotate left; hex machine code 07.*/
    RotateLeft(CARRY, new byte[]{4}, new byte[]{1}, Instruction.NOACCESS, "RLC", "RLC"),
    /**Unused instruction at slot 0x08.*/
    @Deprecated Unused_08,
    /**Double/direct add register pair BC to HL (DAD B); hex machine code 09.*/
    DoubleAddRegisterPair_B(CARRY, new byte[]{10}, new byte[]{3}, Instruction.REGISTER, "DAD", "DAD B"),
    /**Load accumulator indirect BC (LDAX B); hex machine code 0A.*/
    LoadAccumulatorIndirect_B(NOFLAGS, new byte[]{7}, new byte[]{2}, Instruction.REGISTER_INDIRECT, "LDAX", "LDAX B"),
    /**Decrement register pair BC (DCX B); hex machine code 0B.*/
    DecrementRegisterPair_B(NOFLAGS, new byte[]{6}, new byte[]{1}, Instruction.REGISTER, "DCX", "DCX B"),
    /**Increment register C (INR C); hex machine code 0C.*/
    IncrementRegister_C(ZERO | SIGN | PARITY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "INR", "INR C"),
    /**Decrement register C (DCR C); hex machine code 0D.*/
    DecrementRegister_C(ZERO | SIGN | PARITY | AUXILIARY_CARRY, new byte[]{4}, new byte[]{1}, Instruction.REGISTER, "DCR", "DCR C"),
    /**Move immediate C (MVI C); hex machine code 0E.*/
    MoveImmediate_C(NOFLAGS, new byte[]{7}, new byte[]{2}, Instruction.IMMEDIATE, "MVI", "MVI C, data 8"),
    /**Rotate right; hex machine code 0F.*/
    RotateRight(CARRY, new byte[]{4}, new byte[]{1}, Instruction.NOACCESS, "RRC", "RRC"),
    /**Unused instruction at slot 0x10.*/
    @Deprecated Unused_10;
    //17 instruction slots done; 239 left
    
    //Bit masks for flag registers affected by this instruction.
    
    /** *  Flag mask; the bits in {@code flagMask} which are set indicate the particular 
     *  flags in the flag register influenced by this instruction. The masks are defined in the 
     * {@link simulators.Intel8085.core.FlagRegisterConstants} interface. This is 0 (no flags affected) on instructions
     * which do not affect flags (as well as unused instructions).
     * @see #isSignFlagAffected() 
     * @see #isZeroFlagAffected() 
     * @see #isAuxiliaryCarryFlagAffected()
     * @see #isParityFlagAffected() 
     * @see #isCarryFlagAffected() 
     * @see #isAnyFlagAffected() 
     */
    public final byte flagMask;
    
    /** Holds an array, containing the number of T-states (clock pulses) used by this instruction. Usually the length
     * of this array is 1, the single number indicating the number of T-states used by this instruction. If any 
     * instruction has multiple possible values for this property (for example a conditional instruction) then all
     * possible values are contained in this array in sorted order. This is {@code null} in case of unused instruction 
     * slots.
     * @see #Tstates() 
     */
    private final byte[] Tstates;
    /** Holds an array, containing the number of machine cycles used by this instruction. Usually the length
     * of this array is 1, the single number indicating the number of machine cycles used by this instruction. If any 
     * instruction has multiple possible values for this property (for example a conditional instruction) then all
     * possible values are contained in this array in sorted order. This is {@code null} in case of unused instruction
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
    /** Indicates that no access to operands is made in this instruction. 
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
    
    //WARNING: This class may be removed in a future version.
    /** This class holds static final objects as if we declare them as a part of the Instruction enum, then they 
     * cannot be accessed as static final objects are not created before the enumeration constants themselves. */
    private static final class InternalResources {
        /** Prevent instantiation.*/
        private InternalResources() {}
        /** This is a set which holds all the usable instructions (246 of them) for the 8085. In other words; this
         * is all the instructions MINUS the slots which are unused. (Note that objects of {@link java.util.EnumSet} 
         * are modifiable.)
         */
        static final EnumSet<Instruction> usableInstructions = EnumSet.noneOf(Instruction.class);
        /** An array in the order of the 256 available opcodes to the 8085 in which they appear (including unused
         * slots). Given an opcode {@code opcode}, the instruction is {@code opcode2Instruction[opcode & 0xFF]}.
         */
        static final Instruction[] opcode2Instruction = Instruction.values();
    }
    
    /** Creates an unused instruction slot. These instructions (opcodes) are invalid and are (usually) never accepted
     *  by an 8085 implementation.*/
    private Instruction() {
        flagMask = NOFLAGS; unused_addressingModes = UNUSED;
        mnemonic = useDesc = null; Tstates = cycles = null;
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
     * @param mnemonic Instruction mnemonic for this instruction (recognized by the assembler). See {@link #mnemonic}
     * @param useDesc Use description for the instruction (as an example for assembler source code). See 
     * {@link #useDesc}.
     */
    private Instruction(int flagMask, byte[] Tstates, byte[] cycles, int addressingModes, String mnemonic, String useDesc) {
        this.flagMask = (byte)(flagMask & 0xFF); 
        this.Tstates = Tstates; //no copy because this is internal to this enum class.
        this.cycles = cycles;
        unused_addressingModes = (byte)(addressingModes & 0x7F); //do not allow the highest significant bit to pass
        this.mnemonic = mnemonic; this.useDesc = useDesc;
        InternalResources.usableInstructions.add(this);
    }
    
    /** Returns the actual bytecode or the machine code of this instruction (the first byte for multi-byte 
     * instructions) which is directly recognized by the 8085. This method throws an {@code IllegalStateException} if
     * this instruction slot is not used/recognized by the 8085. The opcode can also be obtained by taking the least
     * significant byte of the integer returned by the {@link #ordinal()} method.
     * 
     * <p>A possible implementation of this method is:
     * <pre><code>
     * if(isUnusedInstructionSlot()) throw new IllegalStateException();
     * else return (byte)(ordinal() &amp; 0xFF);
     * </code></pre></p>
     * @return the bytecode/opcode of this instruction
     * @throws IllegalStateException if this is an unused instruction slot not recognized by the 8085
     * @see #isUnusedInstructionSlot() 
     */
    public final byte opcode() {
        if(isUnusedInstructionSlot()) throw new IllegalStateException();
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
     * (for example conditional instructions) the returned array holds all possible values for T-states in sorted order.
     * Changes made to the returned array do not affect any enum constants of this enum class. For unused instruction
     * slots, this method returns {@code null}.
     * @return all possible values of the number of T-states required by this instruction
     * @see #cycles()
     */
    public final byte[] Tstates() {return Arrays.copyOf(Tstates, Tstates.length);}
    /** Returns an array containing the different number of machine cycles required for this instruction. 
     * Usually the length of the returned array is 1. For instructions that can have multiple values for this property
     * (for example conditional instructions) the returned array holds all possible values for T-states in sorted order.
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
    /** Returns {@code true} if any addressing mode is used by this instruction. This is {@code false} if this 
     * instruction does not perform any kind of operand/memory access.
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
    
    /** A set containing the valid instruction set of the 8085 (that is, not containing the unused instruction slots).
     * The {@link java.util.Set#size()} of this set is 246 and it is unmodifiable and sorted with respect to
     * instruction opcode.
     * @see #opcode() 
     */
    public static final Set<Instruction> INSTRUCTION_SET = Collections.unmodifiableSet(InternalResources.usableInstructions);
    /** Returns the instruction corresponding to the opcode (first byte) passed as a parameter. This method will throw
     * an {@code IllegalArgumentException} if the given opcode is not understood by the 8085.
     * @param opcode machine code/bytecode of the first byte of the instruction
     * @return the instruction corresponding to the given opcode
     * @throws IllegalArgumentException if the given opcode is not understood by the 8085
     */
    public static final Instruction disassemble(byte opcode) {
        Instruction i = InternalResources.opcode2Instruction[opcode & 0xFF];
        if(i.isUnusedInstructionSlot()) throw new IllegalArgumentException();
        else return i;
    }
}
