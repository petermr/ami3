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
 * attached to elements that can carry dataType. 
 * examples are scalar, array, matrix
 * 
 * @author pmr
 * 
 */
public interface HasDataType extends HasDictRef {

    /**
     * sets value on dataType attribute. example: setDataType("xsd:double");
     * 
     * @param type
     */
    void setDataType(String type);

    /**
     * gets value on dataType attribute. example: setDataType("xsd:double");
     * 
     * @return type
     */
    String getDataType();

    /**
     * gets value of element;
     * 
     * @return data
     */
    String getXMLContent();

    /**
     * sets value of element;
     * 
     * @param content
     */
    void setXMLContent(String content);

}