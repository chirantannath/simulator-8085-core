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

/** Default/reference implementation to {@link ProcessorState}.
 *
 * <p>Note that this class is not thread-safe. Thread safety can be achieved by 
 * {@link ProcessorState#synchronizedProcessorState(simulators.Intel8085.core.ProcessorState)}.</p>
 * @author Chirantan Nath (emeergency.jasper@gmail.com)
 */
public class DefaultProcessorState {
    
    protected byte a = 0;
    
    protected byte b = 0;
    
    protected byte c = 0;
    
    protected byte d = 0;
    
    protected byte e = 0;
    
    protected byte f = 0;
    
    protected short hl = 0;
    
    protected short sp = 0;
    
    protected short pc = 0;
}
