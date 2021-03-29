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

/** Constants representing the various interrupt masks for the 8085, accessed by the SIM and RIM instructions. This
 * interface defines only constants and no abstract methods.
 * @author Chirantan Nath (emergency.jasper@gmail.com)
 * @see Instruction#ReadInterruptMasks
 * @see Instruction#SetInterruptMasks
 */
//TODO: Learn external interrupts and IO!
public interface InterruptMaskConstants {
    //for RIM
    
    byte MASK_5_5               = (byte)0b0000_0001;
    
    byte MASK_6_5               = (byte)0b0000_0010;
    
    byte MASK_7_5               = (byte)0b0000_0100;
    
    byte INTERRUPT_ENABLE       = (byte)0b0000_1000; 
    
    byte INTERRUPT_5_5_PENDING  = (byte)0b0001_0000;
    
    byte INTERRUPT_6_5_PENDING  = (byte)0b0010_0000;
    
    byte INTERRUPT_7_5_PENDING  = (byte)0b0100_0000;
    
    byte SERIAL_INPUT_DATA_HIGH = (byte)0b1000_0000;
    
    //for SIM
    //MASK_5_5
    //MASK_6_5
    //MASK_7_5
    
    byte MASK_SET_ENABLE         = (byte)0b0000_1000;
    
    byte RESET_RST_7_5_FF        = (byte)0b0001_0000;
    
    byte SERIAL_OUTPUT_ENABLE    = (byte)0b0100_0000;
    
    byte SERIAL_OUTPUT_DATA_HIGH = (byte)0b1000_0000;
}
