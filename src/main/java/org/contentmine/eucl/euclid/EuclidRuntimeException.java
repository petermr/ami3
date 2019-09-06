/**
 *    Copyright 2011 Peter Murray-Rust
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

/**
 * 
 */
package org.contentmine.eucl.euclid;


/**
 * 
 * <p>
 * runtime exception for Euclid
 * </p>
 * 
 * @author Joe Townsend
 * @version 5.0
 * 
 */
public class EuclidRuntimeException extends RuntimeException implements EuclidConstants {

	/** constructor
	 * 
	 * @param message
	 * @param cause
	 */
    public EuclidRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
     * 
     */
    private static final long serialVersionUID = 3618697517584169017L;

    protected EuclidRuntimeException() {
        super();
    }

    /**
     * creates EuclidRuntime with message.
     * 
     * @param msg
     */
    public EuclidRuntimeException(String msg) {
        super(msg);
    }

    /**
     * creates EuclidRuntime from EuclidException.
     * 
     * @param exception
     */
    public EuclidRuntimeException(EuclidException exception) {
        this(S_EMPTY + exception);
    }
}
