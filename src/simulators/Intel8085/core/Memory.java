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

/** Represents/models an interface to the (random accessible; 8-bit line, 16-bit address) memory "chip"
 * attached to the 8085 chip. This interface permits read/write access to both any potential 8085 "interpreters" and any
 * class which wishes to directly interface a memory model compatible with the 8085. This interface forces objects
 * of implementing classes to be serializable, to permit saving of state of the memory used at any instant of time.
 * 
 * <p><strong>Note:</strong> All addresses, although accepted as {@code short}s, must actually be manipulated as 
 * {@code unsigned short}s, since that is the address model used by the 8085. Also note that this interface makes
 * no guarantees about thread safety.</p>
 * 
 * <p>Several of the methods defined can throw a {@link ReadOnlyException} if the implementing class wishes to
 * implement a read-only memory interface.</p>
 * @author Chirantan Nath (emergency.jasper@gmail.com)
 */
public interface Memory extends java.util.RandomAccess, java.io.Serializable {
    /** Returns a bit-mask when, bit-wise ANDed with any address (and then bit-wise ORed with the return value of 
     * {@link #getFixedAddressBitmask()}); evaluates to an address accepted by this memory 
     * interface. In other words, the bit pattern returned is 1 in places where the address bits accepted by this memory
     * interface is variable (connected to a potential 8085 chip). The returned value is to be thought of as an
     * {@code unsigned short}. We can also say that valid 16-bit addresses to this memory interface are valid <i>if and only if</i>
     * {@code (address & ~getAllowedAddressBitmask() & 0xFFFF) == (getFixedAddressBitmask() & 0xFFFF)}
     * is true.
     * 
     * <p>Note that this value also reflects the size (in bytes/addressable locations) of the memory interface/chip. If
     * the number of 1's in the binary representation of the returned value is N, then the number of addressable 
     * locations or size of this memory interface is 2<sup>N</sup>; see {@link #getMemorySize()}.</p>
     * @return a bit-mask for valid addresses accepted by this chip
     * @see #getFixedAddressBitmask()
     * @see #isValidAddress(simulators.Intel8085.core.Memory, short) 
     */
    short getAllowedAddressBitmask();
    /** Returns the size (number of <i>uniquely</i> addressable locations) of this memory interface. This method
     * <strong>must</strong> agree with the returned value of {@link #getAllowedAddressBitmask()} following this rule:
     * <strong>the number of 1's in the returned binary integer from {@code getAllowedAddressBitmask()} is the exact base 2 
     * logarithm of the number returned by {@code getMemorySize()}.</strong> This also implies that the value returned
     * by this method is an exact power of 2. The default implementation for this method calculates the value using the
     * rule given and should suffice for most use cases. It is recommended that classes implementing this interface 
     * make this method {@code final}. The returned value must be always in the range [0, 65535] inclusive.
     * 
     * <p>A possible default implementation for this method is:
     * <pre><code>
     * size = 1;
     * for(i = 1; i &lt;= 0b1000_0000_0000_0000; i &lt;&lt;= 1) if((i &amp; getAllowedAddressBitmask()) != 0) size &lt;&lt;= 1;
     * return size;
     * </code></pre>
     * @return the size of this memory interface
     */
    public default int getMemorySize() {
        final short allowedAddressBitmask = getAllowedAddressBitmask();
        int size = 1;
        for(int i = 1; i <= 0b1000_0000_0000_0000; i <<= 1) if((i & allowedAddressBitmask) != 0) size <<= 1;
        return size;
    }
    /** Returns the bit pattern which is the lowest valid address accepted by this memory interface. In other words;
     * <ol>
     * <li>{@code (getFixedAddressBitmask() & }{@link #getAllowedAddressBitmask()}{@code  & 0xFFFF) == 0}</li>
     * <li>The bit positions other than the 1's set in the binary pattern returned by {@code getAllowedAddressBitmask()}
     * are the exact fixed binary values attached to the other "address lines" of this memory interface.</li>
     * </ol>
     * We can also say that valid 16-bit addresses to this memory interface are valid <i>if and only if</i>
     * {@code (address & ~getAllowedAddressBitmask() & 0xFFFF) == (getFixedAddressBitmask() & 0xFFFF)}
     * is true. The returned value is to be thought of as an {@code unsigned short}.
     * @return a bit-pattern representing the fixed binary signals given to inaccessible address lines of this memory
     * interface
     * @see #isValidAddress(simulators.Intel8085.core.Memory, short) 
     */
    short getFixedAddressBitmask();
    /** Checks if the 16-bit unsigned address given is a valid address that can be accepted by the memory interface {@code m}, which must
     * not be null. This method is guaranteed to be equivalent to the following check:
     * <pre><code>
     * (address &amp; ~m.getAllowedAddressBitmask() &amp; 0xFFFF) == (m.getFixedAddressBitmask() &amp; 0xFFFF)
     * </code></pre>
     * @param m the memory interface to check for
     * @param address the 16-bit address (unsigned) to check
     * @return {@code true} if {@code address} is a valid address for {@code m}, otherwise {@code false}
     * @throws NullPointerException if {@code m} is null
     * @see #getFixedAddressBitmask() 
     * @see #getAllowedAddressBitmask() 
     */
    public static boolean isValidAddress(Memory m, short address) {
        final int fixedAddressBitmask = m.getFixedAddressBitmask();
        final int allowedAddressBitmask = m.getAllowedAddressBitmask();
        return (address & ~allowedAddressBitmask & 0xFFFF) == (fixedAddressBitmask & 0xFFFF);
    }
    /** Gets the byte value stored at address {@code address}, which is to be thought of as an {@code unsigned short}.
     * 
     * <p>Implementations must not give an error result (or throw any exception) in cases when the given address is not
     * in the address space accepted by this memory interface. In other words; the parameter, if required, must be
     * bit-wise ANDed with the value returned by {@link #getAllowedAddressBitmask()} and then, bit-wise ORed with the
     * return value of {@link #getFixedAddressBitmask()} (all as unsigned 16-bit integers) to produce a valid address.
     * This is to permit page mirroring which is also present in real 8085 hardware.
     * </p>
     * @param address the memory location to access as a 16-bit unsigned integer
     * @return the 8-bit data stored at the given {@code address}
     * @see #set(short, byte) 
     */
    byte get(short address);
    /** *  Sets the byte value stored at address {@code address}, which is to be thought of as an {@code unsigned short}.
     * Implementing classes can throw a {@link ReadOnlyException} in cases when the implemented memory is 
     * read-only.
     * 
     * <p>Implementations must not give an error result (or throw any exception) in cases when the given address is not
     * in the address space accepted by this memory interface. In other words; the {@code address} parameter, if required, must be
     * bit-wise ANDed with the value returned by {@link #getAllowedAddressBitmask()} and then, bit-wise ORed with the
     * return value of {@link #getFixedAddressBitmask()} (all as unsigned 16-bit integers) to produce a valid address.
     * This is to permit page mirroring/fold-back which is also present in real 8085 hardware.
     * </p>
     * @param address the memory location to access
     * @param value the value to store
     * @throws ReadOnlyException if this memory interface is read-only
     * @see #get(short) 
     */
    void set(short address, byte value);
    
    //TODO: How do assemblers put code into memory? Does the program counter round back to 0 after reaching address 0xFF?
    
    /** Gets the direct reference of the byte array used to store the actual data stored in the memory interface. The 
     * returned array reference must be backed by this memory interface, in other words; changing the values of the
     * returned array directly reflects the possible values returned by {@link #get(short) } and vice versa (from
     * {@link #set(short, byte) }). This method is optional; classes not wishing to allow direct access to the memory
     * can return {@code null}; as well as in cases where this memory interface should be read-only, as well in cases
     * if the internal memory storage is implemented by any other method (sparse arrays for example).
     * 
     * <p>If a backing array <i>is</i> returned; it's length must be exactly equal to the value returned by
     * {@link #getMemorySize()}; and there must exist a bijective correspondence between the indexes of the returned array
     * and <i>valid</i> addressable locations accepted by this memory interface. (Exactly what kind of mapping is used
     * is left to the implementation.)</p>
     * @return a direct reference to the backing array used to store the data of this memory interface, or {@code null}
     * if direct access is not allowed
     * @see #getFixedAddressBitmask()
     * @see #getAllowedAddressBitmask()
     * @see #isValidAddress(simulators.Intel8085.core.Memory, short) 
     */
    byte[] getBackingArray();
    /** Returns a copy of the memory contained/interfaced by this memory interface. Changing the values of the array
     * returned does not affect the state of this object in any way. The length of the array must be exactly 
     * {@link #getMemorySize()}. The returned values in the array, however, must have the same bijective correspondence as 
     * indicated in the semantics of {@link #getBackingArray()}. In other words, if {@code getBackingArray()} returns a 
     * non-null reference, the array returned by this method and the array returned by {@code getBackingArray()} must be 
     * equal in value (exactly the same length and sequence of values) but must not be identically the same object; at
     * least up until this memory interface object is modified in some way (say by a call to {@link #set(short, byte) }).
     * The array returned is a snapshot of the internal state of this memory object at that instant of time.
     * @return a copy of the memory contained/interfaced by this object
     * @see java.util.Arrays#equals(byte[], byte[])  
     */
    byte[] createMemoryCopy();
    /** *  Wraps the memory interface {@code m} into a read-only interface which will throw {@link ReadOnlyException}
     * or return {@code null} if an attempt is made to change the contents of the returned object. The returned object
     * is backed by {@code m}; all changes made directly to {@code m} will be reflected in the returned object (but
     * not vice versa). This method throws {@code NullPointerException} if {@code m} is {@code null}.
     * @param m the memory interface to wrap
     * @return a read-only wrapper around {@code m}
     * @throws NullPointerException if {@code m} is {@code null}
     */
    public static Memory readOnlyMemory(final Memory m) {
        Objects.requireNonNull(m);
        return new Memory() {
            private static final long serialVersionUID = 13252636522123456L;
            @Override public final short getAllowedAddressBitmask() {return m.getAllowedAddressBitmask();}
            @Override public final int getMemorySize() {return m.getMemorySize();}
            @Override public final short getFixedAddressBitmask() {return m.getFixedAddressBitmask();}
            @Override public final byte get(short address) {return m.get(address);}
            @Override public final void set(short address, byte value) {throw new ReadOnlyException();}
            @Override public final byte[] getBackingArray() {return null;}
            @Override public final byte[] createMemoryCopy() {return m.createMemoryCopy();}
        };
    }
    /** Wraps the memory interface {@code m} into a thread-safe ({@code synchronized}) implementation. The returned object is
     * backed by {@code m}; all changes made directly to {@code m} will be reflected in the returned object and vice 
     * versa. This method throws {@code NullPointerException} if {@code m} is {@code null}.
     * @param m the memory interface to wrap
     * @return a {@code synchronized} wrapper around {@code m}
     * @throws NullPointerException if {@code m} is {@code null}
     */
    public static Memory synchronizedMemory(final Memory m) {
        Objects.requireNonNull(m);
        return new Memory() {
            private static final long serialVersionUID = 1345465432456L;
            @Override public synchronized final short getAllowedAddressBitmask() {return m.getAllowedAddressBitmask();}
            @Override public synchronized final int getMemorySize() {return m.getMemorySize();}
            @Override public synchronized final short getFixedAddressBitmask() {return m.getFixedAddressBitmask();}
            @Override public synchronized final byte get(short address) {return m.get(address);}
            @Override public synchronized final void set(short address, byte value) {m.set(address, value);}
            @Override public synchronized final byte[] getBackingArray() {return m.getBackingArray();}
            @Override public synchronized final byte[] createMemoryCopy() {return m.createMemoryCopy();}
        };
    }
}
