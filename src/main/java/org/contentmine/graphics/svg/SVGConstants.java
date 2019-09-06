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

package org.contentmine.graphics.svg;
import org.contentmine.eucl.xml.XMLConstants;
import org.contentmine.graphics.AbstractCMElement;

import nu.xom.XPathContext;

public interface SVGConstants extends XMLConstants {

	/** standard namespace for SVG
	 * 
	 */
	public static final String SVG_NAMESPACE = "http://www.w3.org/2000/svg";
	public static final String XLINK_NS = "http://www.w3.org/1999/xlink";

	public final static String SVGX_NS = "http://www.xml-cml.org/schema/svgx";
	public final static String SVGX_PREFIX = "svgx";

	public final static double EPS = 0.5;

    /** XPathContext for SVG.
     */
    XPathContext SVG_XPATH = new XPathContext("svg", SVG_NAMESPACE);

	String getNamespaceURIForPrefix(String prefix);

	void copyNamespaces(AbstractCMElement element);
    
}
