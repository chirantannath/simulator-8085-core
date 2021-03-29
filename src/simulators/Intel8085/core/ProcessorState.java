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

import java.util.Objects;

/** Represents an interface for obtaining (and "externally" mutating) the state of an 8085 "chip" (the contents of its
 * registers). This interface considers only "programmable" registers (A, B, C, D, E, H, L, flags, stack pointer, and
 * program counter). It does not consider any internal register arrays, memory pointer registers, or output flags 
 * which are dependent on an implementation supporting the 8085 instruction set. This interface extends
 * {@link java.io.Serializable} because it must be possible to "save" a snapshot of the processor state at any instant.
 * All {@code byte} and {@code short} values accepted and returned by this interface's methods are to be treated as 
 * {@code unsigned}.
 * 
 * <p>Note that the definition of "processor state" as defined by this interface does not include the memory/RAM 
 * attached to the 8085; instead memory is modeled by {@link Memory} and its implementing classes.</p>
 * 
 * <p>This interface makes no guarantees about thread safety and the details are left to the implementation. In case
 * a synchronized version a desired, consider 
 * {@link #synchronizedProcessorState(simulators.Intel8085.core.ProcessorState)}.</p>
 * @see FlagRegisterConstants
 * @author chiru
 */
public interface ProcessorState extends FlagRegisterConstants, java.io.Serializable{
    /** Gets the current value of the processor status word for the 8085. This is a 16-bit unsigned integer which has:
     * <ol>
     * <li>The current 8-bit content of the accumulator (A) register as its higher significant byte. This value must
     * <i>always</i> be equal with the return value of {@link #getAccumulatorRegister()}</li>
     * <li>The current 8-bit content of the flag (F) register as its lower significant byte.</li>
     * </ol>
     * Just after initialization, the value returned by this method is undefined (can be random or some default value)
     * until a call to {@link #setProcessorStatusWord(short)} is made.
     * @return the current value of the processor status word
     * @see #setProcessorStatusWord(short) 
     */
    short getProcessorStatusWord();
    /** Sets the new value of the processor status word for the 8085. In other words;
     * <ol>
     * <li>The new value for the accumulator (A) register is taken from the higher significant byte of {@code word}.
     * This should be the new value returned by a subsequent call to {@link #getAccumulatorRegister()}</li>
     * <li>The new value for the flag (F) register is taken from the lower significant byte of {@code word}.</li>
     * </ol>
     * @param word the new value for the processor status word
     * @see #getProcessorStatusWord()
     * @see #setAccumulatorRegister(byte) 
     */
    void setProcessorStatusWord(short word);
    /** Gets the current 8-bit content of the accumulator (A) register. This value must <i>always</i> be equal to the 
     * higher significant byte of the return value of {@link #getProcessorStatusWord()}. Just after initialization of 
     * this object, the returned value can be undefined (can be random or some default value) until a call to 
     * {@link #setAccumulatorRegister(byte)} or {@link #setProcessorStatusWord(short)} is made.
     * 
     * <p>The default implementation takes care of the above requirements by manipulating the value returned by
     * {@code getProcessorStatusWord()}.</p>
     * @return the current value of the accumulator register
     */
    public default byte getAccumulatorRegister() {return (byte)((getProcessorStatusWord() >>> 8) & 0xFF);}
    /** Sets the new value for the accumulator (A) register. This value must be reflected by a subsequent call to
     * {@link #getAccumulatorRegister()} and in the higher significant byte of the value returned by
     * {@link #getProcessorStatusWord()}. 
     * 
     * <p>The default implementation takes care of the above requirements by manipulating the value returned by
     * {@code getProcessorStatusWord()} and sending it to {@link #setProcessorStatusWord(short)}.</p>
     * @param value the new value for the accumulator register.
     */
    public default void setAccumulatorRegister(byte value) {setProcessorStatusWord((short)(((getProcessorStatusWord() & 0x00FF) | ((value << 8) & 0xFF00)) & 0xFFFF));}
    /** Gets the current 8-bit content of the flag (F) register. This value must <i>always</i> be equal to the lower
     * significant byte of the return value of {@link #getProcessorStatusWord()}. Just after initialization of this
     * object, the returned value can be undefined (can be random or some default value). Note that the bit values
     * in the bit positions 5, 3 and 1 (starting from 0 at the least significant bit) are ignored and undefined.
     * 
     * <p>The default implementation takes care of the above requirements by manipulating the value returned by
     * {@code getProcessorStatusWord()}.</p>
     * @return the current value of the flag register
     * @see #setFlagRegister(byte) 
     * @see #setProcessorStatusWord(short)
     * @see #isSignFlagSet()  
     * @see #isZeroFlagSet() 
     * @see #isAuxiliaryCarryFlagSet()  
     * @see #isParityEven() 
     * @see #isParityOdd() 
     * @see #isCarryFlagSet()  
     */
    public default byte getFlagRegister() {return (byte)(getProcessorStatusWord() & 0xFF);}
    /** Sets the new value for the flag (F) register. This value must be reflected by a subsequent call to
     * {@link #getFlagRegister()} and in the lower significant byte of the value returned by 
     * {@link #getProcessorStatusWord()}. Note that the bit values in the bit positions 5, 3 and 1 (starting from 0 at 
     * the least significant bit) are ignored and undefined.
     * 
     * <p>The default implementation takes care of the above requirements by manipulating the value returned by 
     * {@code getProcessorStatusWord()} and sending it to {@link #setProcessorStatusWord(short)}.</p>
     * @param value the new value for the flag register
     * @see #setSignFlagValue(boolean) 
     * @see #setZeroFlagValue(boolean)
     * @see #setAuxiliaryCarryFlagValue(boolean) 
     * @see #setParityFlagValue(boolean) 
     * @see #setCarryFlagValue(boolean) 
     */
    public default void setFlagRegister(byte value) {setProcessorStatusWord((short)(((getProcessorStatusWord() & 0xFF00) | (value & 0x00FF)) & 0xFFFF));}
    /** Returns {@code true} if the sign (S) flag is set in the flag (F) register, otherwise {@code false}. This value
     * must <i>always</i> agree with the particular bit in the return value of {@link #getFlagRegister()}.
     * 
     * <p>The default implementation extracts the particular bit from the return value of {@code getFlagRegister()}.</p>
     * @return the current value of the sign flag
     * @see #setSignFlagValue(boolean) 
     * @see #isZeroFlagSet() 
     * @see #isAuxiliaryCarryFlagSet() 
     * @see #isParityEven() 
     * @see #isParityOdd()
     * @see #isCarryFlagSet() 
     */
    public default boolean isSignFlagSet() {return (getFlagRegister() & SIGN) != 0;}
    /** Sets the bit value (0 or 1) of the sign (S) flag according to whether {@code flag} is {@code false} or 
     * {@code true} respectively. This bit must be reflected in a subsequent call to {@link #isSignFlagSet()} and by
     * the return value of {@link #getFlagRegister()}.
     * 
     * <p>The default implementation takes care of the above requirements by manipulating the value returned by
     * {@code getFlagRegister()} and sending it to {@link #setFlagRegister(byte)}.</p>
     * @param flag the new value for sign (S) flag
     * @see #setZeroFlagValue(boolean) 
     * @see #setAuxiliaryCarryFlagValue(boolean) 
     * @see #setParityFlagValue(boolean) 
     * @see #setCarryFlagValue(boolean) 
     */
    public default void setSignFlagValue(boolean flag) {setFlagRegister((byte)((getFlagRegister() & ~SIGN) | (flag ? SIGN : 0)));}
    /** Returns {@code true} if the zero (Z) flag is set in the flag (F) register, otherwise {@code false}. This value
     * must <i>always</i> agree with the particular bit in the return value of {@link #getFlagRegister()}.
     * 
     * <p>The default implementation extracts the particular bit from the return value of {@code getFlagRegister()}.</p>
     * @return the current value of the zero flag
     * @see #setZeroFlagValue(boolean) 
     * @see #isSignFlagSet() 
     * @see #isAuxiliaryCarryFlagSet() 
     * @see #isParityEven() 
     * @see #isParityOdd()
     * @see #isCarryFlagSet() 
     */
    public default boolean isZeroFlagSet() {return (getFlagRegister() & ZERO) != 0;}
    /** Sets the bit value (0 or 1) of the zero (Z) flag according to whether {@code flag} is {@code false} or 
     * {@code true} respectively. This bit must be reflected in a subsequent call to {@link #isZeroFlagSet()} and by
     * the return value of {@link #getFlagRegister()}.
     * 
     * <p>The default implementation takes care of the above requirements by manipulating the value returned by
     * {@code getFlagRegister()} and sending it to {@link #setFlagRegister(byte)}.</p>
     * @param flag the new value for zero (Z) flag
     * @see #setSignFlagValue(boolean) 
     * @see #setAuxiliaryCarryFlagValue(boolean) 
     * @see #setParityFlagValue(boolean) 
     * @see #setCarryFlagValue(boolean) 
     */
    public default void setZeroFlagValue(boolean flag) {setFlagRegister((byte)((getFlagRegister() & ~ZERO) | (flag ? ZERO : 0)));}
    /** Returns {@code true} if the auxiliary carry (AC) flag is set in the flag (F) register, otherwise {@code false}. 
     * This value must <i>always</i> agree with the particular bit in the return value of {@link #getFlagRegister()}.
     * 
     * <p>The default implementation extracts the particular bit from the return value of {@code getFlagRegister()}.</p>
     * @return the current value of the auxiliary carry flag
     * @see #setAuxiliaryCarryFlagValue(boolean) 
     * @see #isSignFlagSet() 
     * @see #isZeroFlagSet() 
     * @see #isParityEven() 
     * @see #isParityOdd()
     * @see #isCarryFlagSet() 
     */
    public default boolean isAuxiliaryCarryFlagSet() {return (getFlagRegister() & SIGN) != 0;}
    /** Sets the bit value (0 or 1) of the auxiliary carry (AC) flag according to whether {@code flag} is {@code false}  
     * or {@code true} respectively. This bit must be reflected in a subsequent call to 
     * {@link #isAuxiliaryCarryFlagSet()} and by the return value of {@link #getFlagRegister()}.
     * 
     * <p>The default implementation takes care of the above requirements by manipulating the value returned by
     * {@code getFlagRegister()} and sending it to {@link #setFlagRegister(byte)}.</p>
     * @param flag the new value for auxiliary carry (AC) flag
     * @see #setSignFlagValue(boolean) 
     * @see #setZeroFlagValue(boolean) 
     * @see #setParityFlagValue(boolean) 
     * @see #setCarryFlagValue(boolean) 
     */
    public default void setAuxiliaryCarryFlagValue(boolean flag) {setFlagRegister((byte)((getFlagRegister() & ~AUXILIARY_CARRY) | (flag ? AUXILIARY_CARRY : 0)));}
    /** Returns {@code true} if the parity (P) flag is set in the flag (F) register, otherwise {@code false}. This value
     * is set when the last operation resulted in an even number of 1 bits. This value must <i>always</i> agree with the 
     * particular bit in the return value of {@link #getFlagRegister()}.
     * 
     * <p>The default implementation extracts the particular bit from the return value of {@code getFlagRegister()}.</p>
     * @return the current value of the parity flag
     * @see #setParityFlagValue(boolean) 
     * @see #isSignFlagSet() 
     * @see #isZeroFlagSet() 
     * @see #isAuxiliaryCarryFlagSet() 
     * @see #isParityOdd()
     * @see #isCarryFlagSet() 
     */
    public default boolean isParityEven() {return (getFlagRegister() & PARITY) != 0;} //even number of 1s
    /** Returns {@code true} if the parity (P) flag is 0 in the flag (F) register, otherwise {@code false}. This value
     * is set when the last operation resulted in an odd number of 1 bits. This value must <i>always</i> agree with the 
     * particular bit in the return value of {@link #getFlagRegister()}. This value must always be the complement of
     * the value returned by {@link #isParityEven()}.
     * 
     * <p>The default implementation complements the return value of {@code isParityEven()}.</p>
     * @return the complement current value of the parity flag
     * @see #setParityFlagValue(boolean) 
     * @see #isSignFlagSet() 
     * @see #isZeroFlagSet() 
     * @see #isAuxiliaryCarryFlagSet() 
     * @see #isCarryFlagSet() 
     */
    public default boolean isParityOdd() {return !isParityEven();}
    /** Sets the bit value (0 or 1) of the parity (P) flag according to whether {@code flag} is {@code false} or 
     * {@code true} respectively. This bit must be reflected in a subsequent call to {@link #isParityOdd()}, 
     * {@link #isParityEven()} and by the return value of {@link #getFlagRegister()}.
     * 
     * <p>The default implementation takes care of the above requirements by manipulating the value returned by
     * {@code getFlagRegister()} and sending it to {@link #setFlagRegister(byte)}.</p>
     * @param flag the new value for parity (P) flag
     * @see #setSignFlagValue(boolean) 
     * @see #setZeroFlagValue(boolean) 
     * @see #setAuxiliaryCarryFlagValue(boolean) 
     * @see #setCarryFlagValue(boolean) 
     */
    public default void setParityFlagValue(boolean flag) {setFlagRegister((byte)((getFlagRegister() & ~PARITY) | (flag ? PARITY : 0)));}
    /** Returns {@code true} if the carry (CY) flag is set in the flag (F) register, otherwise {@code false}. This value
     * must <i>always</i> agree with the particular bit in the return value of {@link #getFlagRegister()}.
     * 
     * <p>The default implementation extracts the particular bit from the return value of {@code getFlagRegister()}.</p>
     * @return the current value of the carry flag
     * @see #setCarryFlagValue(boolean) 
     * @see #isSignFlagSet() 
     * @see #isZeroFlagSet() 
     * @see #isAuxiliaryCarryFlagSet() 
     * @see #isParityEven() 
     * @see #isParityOdd()
     */
    public default boolean isCarryFlagSet() {return (getFlagRegister() & CARRY) != 0;}
    /** Sets the bit value (0 or 1) of the carry (CY) flag according to whether {@code flag} is {@code false} or 
     * {@code true} respectively. This bit must be reflected in a subsequent call to {@link #isCarryFlagSet()} and by 
     * the return value of {@link #getFlagRegister()}.
     * 
     * <p>The default implementation takes care of the above requirements by manipulating the value returned by
     * {@code getFlagRegister()} and sending it to {@link #setFlagRegister(byte)}.</p>
     * @param flag the new value for carry (CY) flag
     * @see #setSignFlagValue(boolean) 
     * @see #setZeroFlagValue(boolean) 
     * @see #setAuxiliaryCarryFlagValue(boolean) 
     * @see #setParityFlagValue(boolean) 
     */
    public default void setCarryFlagValue(boolean flag) {setFlagRegister((byte)((getFlagRegister() & ~CARRY) | (flag ? CARRY : 0)));}
    
    /** Gets the current value of the BC register pair as a 16-bit unsigned integer. The higher significant byte 
     * returned must be the same as {@link #getRegisterB()} and the lower significant byte the same as 
     * {@link #getRegisterC()}. Just after initialization of this object, the value returned by this method is undefined
     * (can be random or some default value) until a call to {@link #setRegisterPairBC(short)},
     * {@link #setRegisterB(byte)} or {@link #setRegisterC(byte)} is made.
     * @return the current value of the BC register pair
     */
    short getRegisterPairBC();
    /** Sets the new value of the BC register pair; passed as the 16-bit unsigned {@code word}. The value passed must be
     * reflected in a subsequent call to {@link #getRegisterPairBC()} as well as the higher significant byte in 
     * {@link #getRegisterB()} and lower significant byte in {@link #getRegisterC()}. 
     * @param word the new value for BC register pair.
     * @see #setRegisterB(byte) 
     * @see #setRegisterC(byte)
     */
    void setRegisterPairBC(short word);
    /** Gets the current value of the B register. This is to be the same as the higher significant byte of the value 
     * returned by {@link #getRegisterPairBC()}. Just after initialization of this object, the value returned by this
     * method is undefined (can be random or some default value) until a call to {@link #setRegisterB(byte)} or 
     * {@link #setRegisterPairBC(short)} is made.
     * 
     * <p>The default implementation takes care of the above requirements by manipulating the return value from 
     * {@code getRegisterPairBC()}.</p>
     * @return the current value of the B register
     */
    public default byte getRegisterB() {return (byte)((getRegisterPairBC() >>> 8) & 0xFF);}
    /** Sets the new value of the B register. The value passed must be reflected by a subsequent call to 
     * {@link #getRegisterB()} and in the higher significant byte of {@link #getRegisterPairBC()}. 
     * 
     * <p>The default implementation takes care of the above requirements by manipulating the return value from 
     * {@code getRegisterPairBC()} and sending it to {@link #setRegisterPairBC(short)}.</p>
     * @param value the new value for B register
     */
    public default void setRegisterB(byte value) {setRegisterPairBC((short)(((getRegisterPairBC() & 0x00FF) | ((value << 8) & 0xFF00)) & 0xFFFF));}
    /** Gets the current value of the C register. This is to be the same as the lower significant byte of the value 
     * returned by {@link #getRegisterPairBC()}. Just after initialization of this object, the value returned by this
     * method is undefined (can be random or some default value) until a call to {@link #setRegisterC(byte)} or 
     * {@link #setRegisterPairBC(short)} is made.
     * 
     * <p>The default implementation takes care of the above requirements by manipulating the return value from 
     * {@code getRegisterPairBC()}.</p>
     * @return the current value of the C register
     */
    public default byte getRegisterC() {return (byte)(getRegisterPairBC() & 0xFF);}
    /** Sets the new value of the C register. The value passed must be reflected by a subsequent call to 
     * {@link #getRegisterC()} and in the lower significant byte of {@link #getRegisterPairBC()}. 
     * 
     * <p>The default implementation takes care of the above requirements by manipulating the return value from 
     * {@code getRegisterPairBC()} and sending it to {@link #setRegisterPairBC(short)}.</p>
     * @param value the new value for C register
     */
    public default void setRegisterC(byte value) {setRegisterPairBC((short)(((getRegisterPairBC() & 0xFF00) | (value & 0x00FF)) & 0xFFFF));}
    
    /** Gets the current value of the DE register pair as a 16-bit unsigned integer. The higher significant byte 
     * returned must be the same as {@link #getRegisterD()} and the lower significant byte the same as 
     * {@link #getRegisterE()}. Just after initialization of this object, the value returned by this method is undefined
     * (can be random or some default value) until a call to {@link #setRegisterPairDE(short)},
     * {@link #setRegisterD(byte)} or {@link #setRegisterE(byte)} is made.
     * @return the current value of the DE register pair
     */
    short getRegisterPairDE();
    /** Sets the new value of the DE register pair; passed as the 16-bit unsigned {@code word}. The value passed must be
     * reflected in a subsequent call to {@link #getRegisterPairDE()} as well as the higher significant byte in 
     * {@link #getRegisterD()} and lower significant byte in {@link #getRegisterE()}. 
     * @param word the new value for DE register pair.
     * @see #setRegisterD(byte) 
     * @see #setRegisterE(byte)
     */
    void setRegisterPairDE(short word);
    /** Gets the current value of the D register. This is to be the same as the higher significant byte of the value 
     * returned by {@link #getRegisterPaiDE()}. Just after initialization of this object, the value returned by this
     * method is undefined (can be random or some default value) until a call to {@link #setRegisterD(byte)} or 
     * {@link #setRegisterPairDE(short)} is made.
     * 
     * <p>The default implementation takes care of the above requirements by manipulating the return value from 
     * {@code getRegisterPairDE()}.</p>
     * @return the current value of the D register
     */
    public default byte getRegisterD() {return (byte)((getRegisterPairDE() >>> 8) & 0xFF);}
    /** Sets the new value of the D register. The value passed must be reflected by a subsequent call to 
     * {@link #getRegisterD()} and in the higher significant byte of {@link #getRegisterPairDE()}. 
     * 
     * <p>The default implementation takes care of the above requirements by manipulating the return value from 
     * {@code getRegisterPairDE()} and sending it to {@link #setRegisterPairDE(short)}.</p>
     * @param value the new value for D register
     */
    public default void setRegisterD(byte value) {setRegisterPairDE((short)(((getRegisterPairDE() & 0x00FF) | ((value << 8) & 0xFF00)) & 0xFFFF));}
    /** Gets the current value of the E register. This is to be the same as the lower significant byte of the value 
     * returned by {@link #getRegisterPairDE()}. Just after initialization of this object, the value returned by this
     * method is undefined (can be random or some default value) until a call to {@link #setRegisterE(byte)} or 
     * {@link #setRegisterPairDE(short)} is made.
     * 
     * <p>The default implementation takes care of the above requirements by manipulating the return value from 
     * {@code getRegisterPairDE()}.</p>
     * @return the current value of the E register
     */
    public default byte getRegisterE() {return (byte)(getRegisterPairDE() & 0xFF);}
    /** Sets the new value of the E register. The value passed must be reflected by a subsequent call to 
     * {@link #getRegisterE()} and in the lower significant byte of {@link #getRegisterPairDE()}. 
     * 
     * <p>The default implementation takes care of the above requirements by manipulating the return value from 
     * {@code getRegisterPairDE()} and sending it to {@link #setRegisterPairDE(short)}.</p>
     * @param value the new value for E register
     */
    public default void setRegisterE(byte value) {setRegisterPairDE((short)(((getRegisterPairDE() & 0xFF00) | (value & 0x00FF)) & 0xFFFF));}
    
    /** Gets the current value of the HL register pair as a 16-bit unsigned integer. The higher significant byte 
     * returned must be the same as {@link #getRegisterH()} and the lower significant byte the same as 
     * {@link #getRegisterL()}. Just after initialization of this object, the value returned by this method is undefined
     * (can be random or some default value) until a call to {@link #setRegisterPairHL(short)},
     * {@link #setRegisterH(byte)} or {@link #setRegisterL(byte)} is made.
     * @return the current value of the HL register pair
     */
    short getRegisterPairHL();
    /** Sets the new value of the HL register pair; passed as the 16-bit unsigned {@code word}. The value passed must be
     * reflected in a subsequent call to {@link #getRegisterPairHL()} as well as the higher significant byte in 
     * {@link #getRegisterH()} and lower significant byte in {@link #getRegisterL()}. 
     * @param word the new value for HL register pair.
     * @see #setRegisterH(byte) 
     * @see #setRegisterL(byte)
     */
    void setRegisterPairHL(short word);
    /** Gets the current value of the H register. This is to be the same as the higher significant byte of the value 
     * returned by {@link #getRegisterPaiHL()}. Just after initialization of this object, the value returned by this
     * method is undefined (can be random or some default value) until a call to {@link #setRegisterH(byte)} or 
     * {@link #setRegisterPairHL(short)} is made.
     * 
     * <p>The default implementation takes care of the above requirements by manipulating the return value from 
     * {@code getRegisterPairHL()}.</p>
     * @return the current value of the D register
     */
    public default byte getRegisterH() {return (byte)((getRegisterPairHL() >>> 8) & 0xFF);}
    /** Sets the new value of the H register. The value passed must be reflected by a subsequent call to 
     * {@link #getRegisterH()} and in the higher significant byte of {@link #getRegisterPairHL()}. 
     * 
     * <p>The default implementation takes care of the above requirements by manipulating the return value from 
     * {@code getRegisterPairHL()} and sending it to {@link #setRegisterPairHL(short)}.</p>
     * @param value the new value for H register
     */
    public default void setRegisterH(byte value) {setRegisterPairHL((short)(((getRegisterPairHL() & 0x00FF) | ((value << 8) & 0xFF00)) & 0xFFFF));}
    /** Gets the current value of the L register. This is to be the same as the lower significant byte of the value 
     * returned by {@link #getRegisterPairHL()}. Just after initialization of this object, the value returned by this
     * method is undefined (can be random or some default value) until a call to {@link #setRegisterL(byte)} or 
     * {@link #setRegisterPairHL(short)} is made.
     * 
     * <p>The default implementation takes care of the above requirements by manipulating the return value from 
     * {@code getRegisterPairHL()}.</p>
     * @return the current value of the L register
     */
    public default byte getRegisterL() {return (byte)(getRegisterPairHL() & 0xFF);}
    /** Sets the new value of the L register. The value passed must be reflected by a subsequent call to 
     * {@link #getRegisterL()} and in the lower significant byte of {@link #getRegisterPairHL()}. 
     * 
     * <p>The default implementation takes care of the above requirements by manipulating the return value from 
     * {@code getRegisterPairHL()} and sending it to {@link #setRegisterPairHL(short)}.</p>
     * @param value the new value for L register
     */
    public default void setRegisterL(byte value) {setRegisterPairHL((short)(((getRegisterPairHL() & 0xFF00) | (value & 0x00FF)) & 0xFFFF));}
    
    /** Returns the current value of the stack pointer (SP) register as a 16-bit unsigned integer. Just after 
     * initialization of this object, this value may be undefined (can be random or some default value) until a call to
     * {@link #setStackPointerRegister(short)} is made.
     * @return the current value of SP register
     */
    short getStackPointerRegister();
    /** Sets the new value of the stack pointer (SP) register to the 16-bit unsigned integer passed in {@code word}.
     * This value must be reflected in a subsequent call to {@link #getStackPointerRegister()}.
     * @param word the new value for SP register
     */
    void setStackPointerRegister(short word);
    /** Returns the current value of the program counter (PC) register as a 16-bit unsigned integer. Just after 
     * initialization of this object, this value may be undefined (can be random or some default value) until a call to
     * {@link #setProgramCounterRegister(short)} is made.
     * @return the current value of PC register
     */
    short getProgramCounterRegister();
    /** Sets the new value of the program counter (PC) register to the 16-bit unsigned integer passed in {@code word}.
     * This value must be reflected in a subsequent call to {@link #getProgramCounterRegister()}.
     * @param word the new value for PC register
     */
    void setProgramCounterRegister(short word);
    
    /** Returns  a read-only wrapper around {@code ps}. The methods which mutate the state throw 
     * {@link ReadOnlyException} on call. The returned object is backed by {@code ps}, changes in {@code ps} are 
     * reflected in the returned object. This method throws {@code NullPointerException} if {@code ps} is {@code null}.
     * @param ps the processor state object to wrap
     * @return a read-only wrapper around {@code ps}.
     * @throws NullPointerException if {@code ps} is {@code null}
     */
    public static ProcessorState readOnlyProcessorState(final ProcessorState ps) {
        return new ProcessorState() {
            private static final long serialVersionUID = 24357342456L;
            @Override public final short getProcessorStatusWord() {return ps.getProcessorStatusWord();}
            @Override public final void setProcessorStatusWord(short word) {throw new ReadOnlyException();}
            @Override public final byte getAccumulatorRegister() {return ps.getAccumulatorRegister();}
            @Override public final void setAccumulatorRegister(byte value) {throw new ReadOnlyException();}
            @Override public final byte getFlagRegister() {return ps.getFlagRegister();}
            @Override public final void setFlagRegister(byte value) {throw new ReadOnlyException();}
            @Override public final boolean isSignFlagSet() {return ps.isSignFlagSet();}
            @Override public final void setSignFlagValue(boolean flag) {throw new ReadOnlyException();}
            @Override public final boolean isZeroFlagSet() {return ps.isZeroFlagSet();}
            @Override public final void setZeroFlagValue(boolean flag) {throw new ReadOnlyException();}
            @Override public final boolean isAuxiliaryCarryFlagSet() {return ps.isAuxiliaryCarryFlagSet();}
            @Override public final void setAuxiliaryCarryFlagValue(boolean flag) {throw new ReadOnlyException();}
            @Override public final boolean isParityEven() {return ps.isParityEven();}
            @Override public final boolean isParityOdd() {return ps.isParityOdd();}
            @Override public final void setParityFlagValue(boolean flag) {throw new ReadOnlyException();}
            @Override public final boolean isCarryFlagSet() {return ps.isCarryFlagSet();}
            @Override public final void setCarryFlagValue(boolean flag) {throw new ReadOnlyException();}
            @Override public final short getRegisterPairBC() {return ps.getRegisterPairBC();}
            @Override public final void setRegisterPairBC(short word) {throw new ReadOnlyException();}
            @Override public final byte getRegisterB() {return ps.getRegisterB();}
            @Override public final void setRegisterB(byte value) {throw new ReadOnlyException();}
            @Override public final byte getRegisterC() {return ps.getRegisterC();}
            @Override public final void setRegisterC(byte value) {throw new ReadOnlyException();}
            @Override public final short getRegisterPairDE() {return ps.getRegisterPairDE();}
            @Override public final void setRegisterPairDE(short word) {throw new ReadOnlyException();}
            @Override public final byte getRegisterD() {return ps.getRegisterD();}
            @Override public final void setRegisterD(byte value) {throw new ReadOnlyException();}
            @Override public final byte getRegisterE() {return ps.getRegisterE();}
            @Override public final void setRegisterE(byte value) {throw new ReadOnlyException();}
            @Override public final short getRegisterPairHL() {return ps.getRegisterPairHL();}
            @Override public final void setRegisterPairHL(short word) {throw new ReadOnlyException();}
            @Override public final byte getRegisterH() {return ps.getRegisterH();}
            @Override public final void setRegisterH(byte value) {throw new ReadOnlyException();}
            @Override public final byte getRegisterL() {return ps.getRegisterL();}
            @Override public final void setRegisterL(byte value) {throw new ReadOnlyException();}
            @Override public final short getStackPointerRegister() {return ps.getStackPointerRegister();}
            @Override public final void setStackPointerRegister(short word) {throw new ReadOnlyException();}
            @Override public final short getProgramCounterRegister() {return ps.getProgramCounterRegister();}
            @Override public final void setProgramCounterRegister(short word) {throw new ReadOnlyException();}
        };
    }
    /** Synchronized wrapper around {@code ProcessorState}. This is a separate class so we can extend it later in
     * {@link Processor#synchronizedProcessor(simulators.Intel8085.core.Processor)}.
     *///TODO: Create Processor interface!
    static final class SynchronizedProcessorState implements ProcessorState {
        private static final long serialVersionUID = 14243645754L;
        final ProcessorState ps;
        SynchronizedProcessorState(final ProcessorState ps) {this.ps = ps;}
        @Override public synchronized final short getProcessorStatusWord() {return ps.getProcessorStatusWord();}
        @Override public synchronized final void setProcessorStatusWord(short word) {ps.setProcessorStatusWord(word);}
        @Override public synchronized final byte getAccumulatorRegister() {return ps.getAccumulatorRegister();}
        @Override public synchronized final void setAccumulatorRegister(byte value) {ps.setAccumulatorRegister(value);}
        @Override public synchronized final byte getFlagRegister() {return ps.getFlagRegister();}
        @Override public synchronized final void setFlagRegister(byte value) {ps.setFlagRegister(value);}
        @Override public synchronized final boolean isSignFlagSet() {return ps.isSignFlagSet();}
        @Override public synchronized final void setSignFlagValue(boolean flag) {ps.setSignFlagValue(flag);}
        @Override public synchronized final boolean isZeroFlagSet() {return ps.isZeroFlagSet();}
        @Override public synchronized final void setZeroFlagValue(boolean flag) {ps.setZeroFlagValue(flag);}
        @Override public synchronized final boolean isAuxiliaryCarryFlagSet() {return ps.isAuxiliaryCarryFlagSet();}
        @Override public synchronized final void setAuxiliaryCarryFlagValue(boolean flag) {ps.setAuxiliaryCarryFlagValue(flag);}
        @Override public synchronized final boolean isParityEven() {return ps.isParityEven();}
        @Override public synchronized final boolean isParityOdd() {return ps.isParityOdd();}
        @Override public synchronized final void setParityFlagValue(boolean flag) {ps.setParityFlagValue(flag);}
        @Override public synchronized final boolean isCarryFlagSet() {return ps.isCarryFlagSet();}
        @Override public synchronized final void setCarryFlagValue(boolean flag) {ps.setCarryFlagValue(flag);}
        @Override public synchronized final short getRegisterPairBC() {return ps.getRegisterPairBC();}
        @Override public synchronized final void setRegisterPairBC(short word) {ps.setRegisterPairBC(word);}
        @Override public synchronized final byte getRegisterB() {return ps.getRegisterB();}
        @Override public synchronized final void setRegisterB(byte value) {ps.setRegisterB(value);}
        @Override public synchronized final byte getRegisterC() {return ps.getRegisterC();}
        @Override public synchronized final void setRegisterC(byte value) {ps.setRegisterC(value);}
        @Override public synchronized final short getRegisterPairDE() {return ps.getRegisterPairDE();}
        @Override public synchronized final void setRegisterPairDE(short word) {ps.setRegisterPairDE(word);}
        @Override public synchronized final byte getRegisterD() {return ps.getRegisterD();}
        @Override public synchronized final void setRegisterD(byte value) {ps.setRegisterD(value);}
        @Override public synchronized final byte getRegisterE() {return ps.getRegisterE();}
        @Override public synchronized final void setRegisterE(byte value) {ps.setRegisterE(value);}
        @Override public synchronized final short getRegisterPairHL() {return ps.getRegisterPairHL();}
        @Override public synchronized final void setRegisterPairHL(short word) {ps.setRegisterPairHL(word);}
        @Override public synchronized final byte getRegisterH() {return ps.getRegisterH();}
        @Override public synchronized final void setRegisterH(byte value) {ps.setRegisterH(value);}
        @Override public synchronized final byte getRegisterL() {return ps.getRegisterL();}
        @Override public synchronized final void setRegisterL(byte value) {ps.setRegisterL(value);}
        @Override public synchronized final short getStackPointerRegister() {return ps.getStackPointerRegister();}
        @Override public synchronized final void setStackPointerRegister(short word) {ps.setStackPointerRegister(word);}
        @Override public synchronized final short getProgramCounterRegister() {return ps.getProgramCounterRegister();}
        @Override public synchronized final void setProgramCounterRegister(short word) {ps.setProgramCounterRegister(word);}
    }
    /** Returns a synchronized/thread-safe wrapper around {@code ps}. The returned object is backed by {@code ps},
     * changes to {@code ps} are reflected in the returned object and vice versa. This method throws 
     * {@code NullPointerException} if {@code ps} is {@code null}.
     * @param ps the processor state object to wrap
     * @return a synchronized wrapper around {@code ps}
     * @throws NullPointerException if {@code ps} is {@code null}
     */
    public static ProcessorState synchronizedProcessorState(final ProcessorState ps) {Objects.requireNonNull(ps); return new SynchronizedProcessorState(ps);}
}