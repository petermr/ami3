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

package org.contentmine.eucl.euclid;

import org.apache.log4j.Logger;

/**
 * super class of array methods
 * 
 * @author (C) P. Murray-Rust, 1996
 */
public abstract class ArrayBase implements EuclidConstants {
    final static Logger LOG = Logger.getLogger(ArrayBase.class);
    /** */
    public enum Trim {
        /** */
        ABOVE(1),
        /** */
        BELOW(2);
        /** */
        public int trim;
        private Trim(int t) {
            this.trim = t;
        }
    }
    /** splits string versions of arrays. 
     * 
     */
	public final static String ARRAY_REGEX = "\\s+|\\s*\\|\\s*|\\s*\\,\\s*";

}
