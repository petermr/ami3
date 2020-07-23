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
 * interface for STMLScalar
 */
public interface HasScalar extends HasDataType {

    /**
     * gets value of element;
     * 
     * @return data
     */
    String getString();

    /**
     * gets value of element;
     * 
     * @return integer value
     */
    int getInt();

    /**
     * gets value of element;
     * 
     * @return double value
     */
    double getDouble();

}
