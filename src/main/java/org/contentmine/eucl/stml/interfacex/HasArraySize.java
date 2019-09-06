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

import java.util.List;


/**
 * interface for STMLArray or STMLList
 */
public interface HasArraySize extends HasDataType {

    /** get size of array.
     * @return size
     */
    int getArraySize();
    
    /** get array elements.
     * recalcuates each time so best cached for frequent use
     * @return elements as String
     */
    List<String> getStringValues();
    
    /**
     * gets values of element;
     * 
     * @return integer values
     */
    int[] getInts();

    /**
     * gets values of element;
     * 
     * @return double values
     */
    double[] getDoubles();

}
