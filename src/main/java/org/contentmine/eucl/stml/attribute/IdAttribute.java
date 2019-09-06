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

package org.contentmine.eucl.stml.attribute;

import nu.xom.Attribute;
import nu.xom.Node;

/**
 * user-modifiable class supporting "id". 
 */
public class IdAttribute extends StringSTAttribute {

	/** id */
    public final static String NAME = "id";
    String argName = "null";
    int start = 0;
    int end = 0;
    /**
     * constructor.
     * 
     */
    public IdAttribute() {
        super(NAME);
    }

    /** constructor.
     * @param value
     */
    public IdAttribute(String value) {
        super(NAME);
        this.setSTMLValue(value);
    }

    /**
     * constructor from element with IdAttribute
     * 
     * @param att
     * @exception RuntimeException
     */
    public IdAttribute(Attribute att) throws RuntimeException {
        super(att);
    }

    /** copy constructor.
     * @return IdAttribute copy
     */
    public Node copy() {
    	return new IdAttribute(this);
    }
    
    /** set value and process.
     * 
     * @param value
     * @exception RuntimeException bad value
     */
    public void setSTMLValue(String value) throws RuntimeException {
        if (value == null) {
            throw new RuntimeException("null IdAttribute value");
        } else if (value.trim().equals(S_EMPTY)) {
            // seems to get called with empty string initially
            // this is a bug
        } else {
            super.setSTMLValue(value);
        }
    }
    
}
