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



/**
 * attached to elements that can carry units. 
 * examples are scalar, array, matrix
 * 
 * @author pmr
 * 
 */
public interface HasUnits {

    /**
     * sets value on units attribute. example: setUnits("myUnits", "floop");
     * 
     * @param prefix
     * @param idRef
     * @param namespaceURI
     */
    void setUnits(String prefix, String idRef, String namespaceURI);

    /**
     * 
     * @return units as String
     */
    String getUnits();

    // removed in this version
//    /**
//     * converts a real scalar to SI. only affects scalar with units attribute
//     * and dataType='xsd:double' replaces the value with the converted value and
//     * the units with the SI Units
//     * 
//     * @param unitListMap
//     *            map to resolve the units attribute
//     */
//    void convertToSI(NamespaceToUnitListMap unitListMap);

}