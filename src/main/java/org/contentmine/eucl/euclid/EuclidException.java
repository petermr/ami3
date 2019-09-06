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
/**
 * replaces all Exceptions in jumbo.euclid with a single Exception. The old
 * Exceptions were too numerous and confused signatures
 * 
 * @author (C) P. Murray-Rust, 1996
 */
public class EuclidException extends Exception {
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3617576011412288051L;
    /**
     * constructor.
     */
    public EuclidException() {
        super();
    }
    /**
     * constructor.
     * 
     * @param s
     */
    public EuclidException(String s) {
        super(s);
    }
}
