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

/**
 * 
 */
package org.contentmine.eucl.stml.attribute;

import org.apache.log4j.Logger;
import org.contentmine.eucl.stml.STMLAttribute;
import org.contentmine.eucl.stml.STMLConstants;

/**
 * @author pm286
 *
 */
public class AttributeFactory implements STMLConstants {

    private static final String DELIMITER = "delimiter";
    private static final String SIZE = "size";
    
	private static final String SCALAR = "scalar";
	private static final String ARRAY = "array";
	
	final static Logger LOG = Logger.getLogger(AttributeFactory.class);
	
    // singleton
    /** singleton attribute factory */
	public final static AttributeFactory attributeFactory = new AttributeFactory();

	
	public STMLAttribute getAttribute(String name, String extent) {
		if (DELIMITER.equals(name) && ARRAY.equals(extent)) {
			return new DelimiterAttribute(name);
//		} else if (SIZE.equals(name) && ARRAY.equals(extent)) {
//			return new SizeAttribute(name);
		}
		return null;
	}

}
