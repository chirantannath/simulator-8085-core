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
 * <p>This interface makes no guarantees about thread safety and the details are left to the implementation.</p>
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
     * @return 
     */
    short getRegisterPairBC();
    /**
     * 
     * @param word 
     */
    void setRegisterPairBC(short word);
    
    public default byte getRegisterB() {return (byte)((getRegisterPairBC() >>> 8) & 0xFF);}
    
    public default void setRegisterB(byte value) {setRegisterPairBC((short)(((getRegisterPairBC() & 0x00FF) | ((value << 8) & 0xFF00)) & 0xFFFF));}
    
    public default byte getRegisterC() {return (byte)(getRegisterPairBC() & 0xFF);}
    
    public default void setRegisterC(byte value) {setRegisterPairBC((short)(((getRegisterPairBC() & 0xFF00) | (value & 0x00FF)) & 0xFFFF));}
    
}
