/**
 *    Copyright 2011 Peter Murray-Rust et. al.
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

package org.contentmine.eucl.stml.interfacex;

import nu.xom.Attribute;


/**
 * interface for STMLArray or STMLMatrix
 */
public interface HasDelimiter {

	/**
	 * 
	 * @return delimiter string
	 */
    String getDelimiter();
    
    /**
     * sets delimiter (should be a single printable character or single space (char)32
     * @param delim
     */
    void setDelimiter(String delim);

    /** removes any attribute of the form
     * delimiter=" " or delimiter=""
     */
    void removeWhitespaceDelimiterAttribute();

	Attribute getDelimiterAttribute();
}
