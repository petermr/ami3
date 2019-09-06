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

package org.contentmine.graphics.math;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/** draws a straight line.
 * 
 * @author pm286
 *
 */
public class MathMLCn extends MathMLElement {
	private static final Logger LOG = Logger.getLogger(MathMLCn.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public final static String TAG = "cn";

	/** constructor
	 */
	public MathMLCn() {
		super(TAG);
	}
	
	/** constructor
	 */
	public MathMLCn(MathMLElement element) {
        super(element);
	}
	

}
