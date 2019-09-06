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
 *
 * <p>
 * Tool providing methods for working with doubles.
 * </p>
 *
 * @author Sam Adams
 *
 */
public class DoubleTool {

    /**
     * tests equality of doubles.
     *
     * @param a
     * @param b
     * @param eps
     *            margin of identity
     * @return true if a == b within eps
     */
    public static boolean equals(double a, double b, double eps) {
        return (Math.abs(a - b) < Math.abs(eps));
    }

    /**
     * tests equality of double arrays. arrays must be of same length
     *
     * @param a
     *            first array
     * @param b
     *            second array
     * @param eps
     *            margin of identity
     * @return array elements equal within eps
     */
    public static boolean equals(double[] a, double[] b, double eps) {
        boolean result = false;
        if (a.length == b.length) {
            result = true;
            for (int i = 0; i < a.length; i++) {
                if (Math.abs(a[i] - b[i]) > Math.abs(eps)) {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }

}
