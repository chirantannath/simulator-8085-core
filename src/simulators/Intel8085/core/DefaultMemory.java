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

/** Default implementation for {@link Memory}. This class can be extended to further modify and adapt for various
 * applications as desired; although the implementation given should suffice in most cases.
 * @author Chirantan Nath (emergency.jasper@gmail.com)
 */
public class DefaultMemory /*implements Memory (WARNING: DOES NOT IMPLEMENT YET)*/ {
    private static final long serialVersionUID = 13194014014019340L;
    //TODO: DOCUMENTATION NOT PROPER YET
    /**
     * @param index
     * @param allowedAddressBitmask
     * @param fixedAddressBitmask
     * @return 
     * @throws IndexOutOfBoundsException
     */
    public static int mapToAddress(int index, final int allowedAddressBitmask, final int fixedAddressBitmask) {
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
    //TODO: DOCUMENTATION PENDING
    /** 
     * @param address
     * @param allowedAddressBitmask
     * @return 
     */
    public static int mapToIndex(int address, final int allowedAddressBitmask) {
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
     * the bits in the address values are allowed to change (and thus can refer to different memory locations).
     * @see Memory#getAllowedAddressBitmask() 
     */
    protected short allowedAddressBitmask;
    /** Fixed address bit-mask for 16-bit addresses to this memory interface. This 16-bit vector is:
     * <ol>
     * <li>{@code (fixedAddressBitmask} {@code & }{@link #allowedAddressBitmask}{@code ) == 0}</li>
     * <li>In the places where the 16-bit vector {@code allowedAddressBitmask} is 0, this bit vector will indicate the
     * fixed values attached to the fixed "address lines" of this memory interface.</li>
     * </ol>
     * @see Memory#getFixedAddressBitmask() 
     */
    protected short fixedAddressBitmask;
    /** This is the byte vector which stores our memory. Its length is (and should be) always an exact power of 2 
     * (following the semantics of {@link Memory#getMemorySize()}). The mapping from addresses to an index in this vector
     * is given by {@link #mapToIndex(int, int)} (at least for the default implementation for this class).
     */
    protected byte[] memory;
    
    
}
