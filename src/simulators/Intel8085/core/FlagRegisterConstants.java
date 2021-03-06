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

import java.util.ArrayDeque;

/** Flag register bit mask constants. These indicate the positions (bit masks) of the various flags in the flag 
 * register. This interface defines no abstract methods.
 * @see simulators.Intel8085.core.Instruction
 * @author Chirantan Nath (emergency.jasper@gmail.com)
 */
public interface FlagRegisterConstants {
    /** Sign (S) flag is set when the 8-bit result of an operation is negative (most significant bit is 1); otherwise it 
     * is 0.
     */
    byte SIGN            = (byte)0b1000_0000;
    /** Zero (Z) flag is set when the result is zero; otherwise it is zero.*/
    byte ZERO            = (byte)0b0100_0000;
    /** Auxiliary carry (AC) flag is set when a carry generated by bit at position 3 (from least significant end; starting 
     * from 0) in the result is carried over to bit at position 4. This flag is used for binary-coded decimal (BCD) 
     * operations.
     */
    byte AUXILIARY_CARRY = (byte)0b0001_0000;
    /** If the result has an even number of 1s; parity (P) flag is set otherwise it is reset.*/
    byte PARITY          = (byte)0b0000_0100;
    /** If the arithmetic operation results in a carry, carry (CY) flag is set otherwise it is reset.*/
    byte CARRY           = (byte)0b0000_0001;
    /** Indicates that no flags have been affected.*/
    byte NOFLAGS         = (byte)0b0000_0000;
    
    /** Returns a string description of the flags switched ON in the parameter {@code flags}. For example, if 
     * {@code flags == ZERO | PARITY | CARRY } then the string returned is "Z,P,CY". These are the names used for
     * the various flags:
     * <table border=1>
     * <thead><th>Flag</th><th>Name</th></thead>
     * <tbody>
     * <tr><td>{@link #SIGN}</td><td>S</td></tr>
     * <tr><td>{@link #ZERO}</td><td>Z</td></tr>
     * <tr><td>{@link #AUXILIARY_CARRY}</td><td>AC</td></tr>
     * <tr><td>{@link #PARITY}</td><td>P</td></tr>
     * <tr><td>{@link #CARRY}</td><td>CY</td></tr>
     * </tbody>
     * </table>
     * The names will always be separated by a comma; but there is no guarantee of order. If {@code flags} is equal to
     * {@link #NOFLAGS}, this method will always return an empty string.
     * @param flags bit vector indicating various 8085 processor flags
     * @return a string describing the flags indicated by {@code flags}
     */
    public static String createFlagDescription(byte flags) {
        if(flags == NOFLAGS) return "";
        ArrayDeque<String> l = new ArrayDeque<>(10); //prevent preallocations
        if((flags & SIGN)!=0) l.addLast("S");
        if((flags & ZERO)!=0) l.addLast("Z");
        if((flags & AUXILIARY_CARRY)!=0) l.addLast("AC");
        if((flags & PARITY)!=0) l.addLast("P");
        if((flags & CARRY)!=0) l.addLast("CY");
        return String.join(",", l);
    }
}
