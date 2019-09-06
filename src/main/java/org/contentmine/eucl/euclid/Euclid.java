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
 * These routines have evolved over >10 years and have now settled down to
 * primitive dataTypes to support CML. In general a CML data element (scalar,
 * array, matrix) * (double, int, String) will have a euclid primitive unless it
 * is already provided by Java or not useful. Almost all double types are
 * supported but some int and String types (e.g. IntSquareMatrix, StringArray,
 * etc.) are missing. The emphasis is on algebrra and geometry.
 * 
 * In some CML routines (e.g. atom.geXYZ() a Point3 is returned, but in others
 * the wrapped type (e.g. STMLArray) is used. Please let us know if this design
 * works satisfactorily.
 * 
 * @author pmr 2005
 * 
 */
public @interface Euclid {

}
