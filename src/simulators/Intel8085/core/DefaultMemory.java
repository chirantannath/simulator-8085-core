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

/** Default/reference read/write memory implementation for {@link Memory}. This class can be extended to further modify 
 * and adapt for various applications as desired; although the implementation given should suffice in most cases.
 * 
 * <p>3 protected variables {@link #allowedAddressBitmask}, {@link #fixedAddressBitmask} and {@link #memory} exist for
 * subclasses to suitably manipulate. This class defines no instance methods other than those specified by 
 * {@code Memory}, and all those methods defined in {@code Memory} can be overriden.</p>
 * 
 * <p>Note that this class is not thread-safe. Thread safety can be achieved from 
 * {@link Memory#synchronizedMemory(simulators.Intel8085.core.Memory)}.</p>
 * @see Memory#getAllowedAddressBitmask() 
 * @see Memory#getFixedAddressBitmask() 
 * @author Chirantan Nath (emergency.jasper@gmail.com)
 */
public class DefaultMemory implements Memory, java.io.Serializable, java.util.RandomAccess {
    private static final long serialVersionUID = 13194014014019340L;
    /** Maps a possible {@code index} to the internal {@code byte[]} buffer used to a <i>valid</i> 16-bit unsigned 
     * address for the 8085 (as defined by {@code allowedAddressBitmask} and {@code fixedAddressBitmask}, see 
     * {@link Memory}). If only valid addresses are considered, for fixed values of {@code allowedAddressBitmask} and
     * {@code fixedAddressBitmask} this is a bijective function from possible indexes to possible valid addresses 
     * (inverse as {@link #mapToIndex(int, int, int)}). 
     * 
     * <p>As per the semantics of {@code Memory}, an {@code IndexOutOfBoundsException} is thrown if {@code index} is
     * greater than or equal to 2<sup>the number of 1s in the binary representation of 
     * {@code allowedAddressBitmask & 0xFFFF}</sup>; or if {@code index} is negative. The returned value is guaranteed 
     * to be <i>valid</i> as defined by the condition 
     * {@code (address & ~allowedAddressBitmask() & 0xFFFF) == (fixedAddressBitmask() & 0xFFFF)}.</p>
     * 
     * <p>This method will throw an {@code IllegalArgumentException} in case if
     * {@code (allowedAddressBitmask & fixedAddressBitmask & 0xFFFF) != 0}; as per semantics required by {@code Memory}.
     * </p>
     * 
     * <p>The inverse of this method ({@code mapToIndex}) is used by {@code DefaultMemory} to map given addresses to
     * an index into {@link #memory}. The mapping is guaranteed to be bijective as defined above, but the exact 
     * implementation may change from version to version.</p>
     * @param index an index starting from 0 and ending at the value as specified above
     * @param allowedAddressBitmask 16-bit vector indicating variable "address lines" to an 8085 "chip"; see 
     * {@link Memory#getAllowedAddressBitmask()}
     * @param fixedAddressBitmask 16-bit vector indicating values to fixed "address lines" to an 8085 "chip"; see
     * {@link Memory#getFixedAddressBitmask()}
     * @return a valid 16-bit unsigned address for an 8085 corresponding to an index for a {@code byte[]}
     * buffer
     * @throws IndexOutOfBoundsException if {@code index} is larger than or equal to the number of permissible uniquely
     * addressable locations as defined by {@code allowedAddressBitmask}
     * @throws IllegalArgumentException if {@code (allowedAddressBitmask & fixedAddressBitmask & 0xFFFF) != 0}
     */
    public static int mapToAddress(int index, final int allowedAddressBitmask, final int fixedAddressBitmask) {
        if((allowedAddressBitmask & fixedAddressBitmask & 0xFFFF) != 0) throw new IllegalArgumentException();
        int bitmaskBitPosition, indexBitPosition;
        int addressVector = 0;
        if(index < 0) throw new IndexOutOfBoundsException("< 0");
        for(bitmaskBitPosition = indexBitPosition = 1; bitmaskBitPosition <= 0b1000_0000_0000_0000; bitmaskBitPosition <<= 1)
            if((allowedAddressBitmask & bitmaskBitPosition) != 0) {
                if((index & indexBitPosition) != 0) addressVector |= bitmaskBitPosition;
                indexBitPosition <<= 1;
            }
        if(index >= indexBitPosition) throw new IndexOutOfBoundsException(">= "+indexBitPosition);
        return (addressVector & allowedAddressBitmask | fixedAddressBitmask) & 0xFFFF;
    }
    /** Maps a possible 16-bit unsigned {@code address} to an index suitable for a {@code byte[]} buffer that can be
     * used for storage of a {@link #Memory} object. The address is not required to be <i>valid</i>, indexes may mirror
     * and/or fold-back as defined by the semantics of {@code Memory} and by {@code allowedAddressBitmask} and 
     * {@code fixedAddressBitmask}. If only valid addresses are considered, for fixed values of {@code allowedAddressBitmask} and
     * {@code fixedAddressBitmask} this is a bijective function from possible valid addresses to possible indexes 
     * (inverse as {@link #mapToAddress(int, int, int)}).
     * 
     * <p>As per semantics of {@code Memory}, the returned index is greater than or equal to 0, and 1 less than 2
     * <sup>the number of 1s in the binary representation of {@code allowedAddressBitmask & 0xFFFF}</sup>.</p>
     * 
     * <p>This method will throw an {@code IllegalArgumentException} in case if
     * {@code (allowedAddressBitmask & fixedAddressBitmask & 0xFFFF) != 0}; as per semantics required by {@code Memory}.
     * </p>
     * 
     * <p>This method is used by {@code DefaultMemory} to map given addresses to
     * an index into {@link #memory}. The mapping is guaranteed to be bijective as defined above, but the exact 
     * implementation may change from version to version.</p>
     * @param address any 16-bit unsigned address to be used by an 8085 "chip"
     * @param allowedAddressBitmask 16-bit vector indicating variable "address lines" to an 8085 "chip"; see 
     * {@link Memory#getAllowedAddressBitmask()} 
     * @param fixedAddressBitmask 16-bit vector indicating values to fixed "address lines" to an 8085 "chip"; see
     * {@link Memory#getFixedAddressBitmask()}
     * @return an index to a {@code byte[]} buffer that can be used to implement {@code Memory}
     * @throws IllegalArgumentException if {@code (allowedAddressBitmask & fixedAddressBitmask & 0xFFFF) != 0}
     */
    public static int mapToIndex(int address, final int allowedAddressBitmask, final int fixedAddressBitmask /*unused*/) {
        //fixedAddressBitmask is unused by this implementation other than the following check.
        if((allowedAddressBitmask & fixedAddressBitmask & 0xFFFF) != 0) throw new IllegalArgumentException();
        int bitmaskBitPosition, indexBitPosition;
        int indexVector = 0;
        address &= 0xFFFF & allowedAddressBitmask;
        for(bitmaskBitPosition = indexBitPosition = 1; bitmaskBitPosition <= 0b1000_0000_0000_0000; bitmaskBitPosition <<= 1)
            if((allowedAddressBitmask & bitmaskBitPosition) != 0) {
                if((address & bitmaskBitPosition) != 0) indexVector |= indexBitPosition; 
                indexBitPosition <<= 1;
            }
        return indexVector;
    }
    
    /** Allowed address bit-mask for 16-bit addresses to this memory interface. This 16-bit vector is 1 in places where
     * the bits in the address values are allowed to change (and thus can refer to different memory locations). This 
     * value is returned by this class's implementation of {@link #getAllowedAddressBitmask()}.
     * @see Memory#getAllowedAddressBitmask() 
     */
    protected short allowedAddressBitmask;
    /** Fixed address bit-mask for 16-bit addresses to this memory interface. This 16-bit vector follows the following
     * rules:
     * <ol>
     * <li>{@code (fixedAddressBitmask} {@code & }{@link #allowedAddressBitmask}{@code ) == 0}</li>
     * <li>In the places where the 16-bit vector {@code allowedAddressBitmask} is 0, this bit vector will indicate the
     * fixed values attached to the fixed "address lines" of this memory interface.</li>
     * </ol>
     * This value is returned by this class's implementation of {@link #getFixedAddressBitmask()}.
     * @see Memory#getFixedAddressBitmask() 
     */
    protected short fixedAddressBitmask;
    /** This is the byte vector which stores our memory. Its length is (and should be) always an exact power of 2 
     * (following the semantics of {@link Memory#getMemorySize()}). The mapping from addresses to an index in this vector
     * is given by {@link #mapToIndex(int, int)} (at least for the default implementation for this class). Its length
     * is the one returned by {@code DefaultMemory}'s implementation of {@link #getMemorySize()}.
     */
    protected byte[] memory;
    
    /** Creates/allocates memory for the 8085 with the given fixed and allowed address bit-masks (for an explanation 
     * see {@link Memory#getAllowedAddressBitmask()} and {@link Memory#getFixedAddressBitmask()}). As per the semantics
     * required by {@link #Memory}, an {@code IllegalArgumentException} is thrown if 
     * {@code (allowedAddressBitmask & fixedAddressBitmask) != 0} (compared in 16-bit unsigned mode). 
     * Allocated memory (in bytes) is 2<sup>number of 1s in the binary representation of 
     * {@code allowedAddressBitmask}</sup>. (This means that at least 1 byte is always allocated.)
     * @param allowedAddressBitmask allowed address bit-mask (bit-vector is 1 in places where the "address lines" are 
     * connected to an 8085 chip). This value is returned by {@link #getAllowedAddressBitmask()}
     * @param fixedAddressBitmask bit-vector which gives values assigned to those "address lines" which are kept fixed
     * (not connected to an 8085 "chip"). This value is returned by {@link #getFixedAddressBitmask()}
     * @throws IllegalArgumentException if {@code (allowedAddressBitmask & fixedAddressBitmask) != 0}
     * @see #getAllowedAddressBitmask() 
     * @see #getMemorySize() 
     * @see #getFixedAddressBitmask() 
     */
    public DefaultMemory(short allowedAddressBitmask, short fixedAddressBitmask) {
        if((allowedAddressBitmask & fixedAddressBitmask & 0xFFFF) != 0) throw new IllegalArgumentException();
        this.allowedAddressBitmask = allowedAddressBitmask;
        this.fixedAddressBitmask = fixedAddressBitmask;
        memory = new byte[Memory.super.getMemorySize()];
    }
    /** Creates/allocates memory for the 8085 which has <i>at least</i> {@code minimumMemorySize} number of bytes (the
     * size allocated is an exact power of 2 as required by semantics of {@link Memory#getMemorySize()}). This 
     * constructor throws an {@link IllegalArgumentException} if {@code minimumMemorySize} is greater than 65,536 or
     * less than 0. {@link #getFixedAddressBitmask()} always returns 0 and {@link #getAllowedAddressBitmask()} returns 
     * {@link #getMemorySize()}{@code  - 1} (1 less than an exact power of 2). If finer control is desired, consider 
     * {@link #DefaultMemory(short, short)}.
     * @param minimumMemorySize allocate at least this much amount of memory, in bytes
     * @throws IllegalArgumentException if {@code minimumMemorySize} is invalid
     * @see #getMemorySize() 
     * @see DefaultMemory(short, short)
     */
    public DefaultMemory(int minimumMemorySize) {
        if(minimumMemorySize > 65536 || minimumMemorySize < 0) throw new IllegalArgumentException();
        //calculate ceil(log2(minimumMemorySize))
        int memsize = 1;
        for(; memsize > minimumMemorySize; memsize <<= 1);
        memory = new byte[memsize];
        fixedAddressBitmask = 0; allowedAddressBitmask = (short)((memsize-1)&0xFFFF);
    }
    /** Allocates the full memory range possible by the 8085 (all 65,536 bytes). {@link #getMemorySize()} returns 65535,
     * {@link #getAllowedAddressBitmask()} returns 0xFFFF (16-bit unsigned) and {@link #getFixedAddressBitmask()} 
     * returns 0,
     * @see DefaultMemory(int)
     * @see DefaultMemory(short, short)
     */
    public DefaultMemory() {
        memory = new byte[65536]; fixedAddressBitmask = 0;
        allowedAddressBitmask = (short)0xFFFF;
    }
    /** {@inheritDoc}
     * <p>This value is set during construction of an object of this class.</p>
     * @return {@inheritDoc}
     * @see Memory#getAllowedAddressBitmask() 
     * @see #getFixedAddressBitmask()
     */
    @Override public short getAllowedAddressBitmask() {return allowedAddressBitmask;}
    /** {@inheritDoc}
     * <p>The returned value is the size (in bytes) allocated for this memory interface object. This value will be 1
     * at minimum (at least for this implementation). This value is set in the constructor of this class.</p> 
     * @return {@inheritDoc}
     * @see Memory#getMemorySize() 
     * @see #getAllowedAddressBitmask() 
     */
    @Override public int getMemorySize() {return memory.length;}
    /** {@inheritDoc}
     * <p>This value is set in the constructor of this class.</p>
     * @return {@inheritDoc}
     * @see Memory#getFixedAddressBitmask() 
     * @see #getAllowedAddressBitmask()
     */
    @Override public short getFixedAddressBitmask() {return fixedAddressBitmask;}
    /** {@inheritDoc}
     * <p>The implementation for {@code DefaultMemory} does not give any error value (or throw an exception) for any
     * possible value of {@code address}, to implement mirroring/fold-back mechanisms. {@code address} directly maps to
     * the array returned by {@link #getBackingArray()} by {@link #mapToIndex(int, int)}.</p>
     * @param address {@inheritDoc}
     * @return {@inheritDoc}
     * @see Memory#get(short) 
     * @see #set(short, byte) 
     */
    @Override public byte get(short address) {return memory[mapToIndex(address & 0xFFFF, allowedAddressBitmask & 0xFFFF, fixedAddressBitmask & 0xFFFF)];}
    /** {@inheritDoc} 
     * <p>The implementation for {@code DefaultMemory} does not give any error value (or throw an exception) for any
     * possible value of {@code address}, to implement mirroring/fold-back mechanisms. {@code address} directly maps to
     * the array returned by {@link #getBackingArray()} by {@link #mapToIndex(int, int)}.</p>
     * @param address {@inheritDoc}
     * @param value {@inheritDoc}
     */
    @Override public void set(short address, byte value) {memory[mapToIndex(address & 0xFFFF, allowedAddressBitmask & 0xFFFF, fixedAddressBitmask & 0xFFFF)] = value;}
    /** Returns the internal buffer/array used ({@link #memory}) to store the bytes in this memory interface 
     * implementation. The indexes in the returned array map to addresses as given by the methods 
     * {@link #mapToAddress(int, int, int)} and {@link #mapToIndex(int, int)}. Changing the values of this array 
     * directly affects the internal state of this object and vice versa. The returned array is of length
     * {@link #getMemorySize()}.
     * @return a reference to the internal array used to implement memory for the 8085
     * @see Memory#getBackingArray() 
     * @see #createMemoryCopy() 
     */
    @Override public byte[] getBackingArray() {return memory;}
    /** Creates a distinct separate copy of the array returned by {@link #getBackingArray()} and returns it. The 
     * returned array is distinct from this object in the sense that changing values in the returned array does not
     * change the internal state of this memory interface object and vice versa. The returned array is of 
     * {@link #getMemorySize()}.
     * 
     * <p>If the state of this memory object is modified while the internal buffer is being copied, the behavior of
     * this method is undefined.</p>
     * @return a copy of {@code getBackingArray()} at the instant at which this method was called.
     * @see Memory#createMemoryCopy() 
     */
    @Override public byte[] createMemoryCopy() {return java.util.Arrays.copyOf(memory, memory.length);}
}
