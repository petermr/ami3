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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.graphics.AbstractCMElement;

import nu.xom.Element;
import nu.xom.Node;

/** supports defs
 * 
 * @author pm286
 *
 */
public class SVGDefs extends SVGElement {
	private static final Logger LOG = Logger.getLogger(SVGDefs.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public final static String ALL_DEFS_XPATH = ".//svg:defs";

	public final static String TAG ="defs";
	/** constructor
	 */
	public SVGDefs() {
		super(TAG);
		init();
	}
	
	/** constructor
	 */
	public SVGDefs(SVGElement element) {
        super(element);
	}
	
	/** constructor
	 */
	public SVGDefs(Element element) {
        super((SVGElement) element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new SVGDefs(this);
    }

	

	/**
	 * @return tag
	 */
	public String getTag() {
		return TAG;
	}

	public static void removeDefs(AbstractCMElement svgElement) {
		List<SVGDefs> defsList = extractSelfAndDescendantDefs(svgElement);
		for (SVGDefs defs : defsList) {
			defs.detach();
		}
	}
	
	public static List<SVGDefs> extractSelfAndDescendantDefs(AbstractCMElement svgElem) {
		return SVGDefs.extractDefss(SVGUtil.getQuerySVGElements(svgElem, ALL_DEFS_XPATH));
	}

	/** makes a new list composed of the defs in the list
	 * 
	 * @param elements
	 * @return
	 */
	public static List<SVGDefs> extractDefss(List<SVGElement> elements) {
		List<SVGDefs> defsList = new ArrayList<SVGDefs>();
		for (AbstractCMElement element : elements) {
			if (element instanceof SVGDefs) {
				defsList.add((SVGDefs) element);
			}
		}
		return defsList;
	}
	
    /** return null
     * 
     */
	public Real2Range getBoundingBox() {
		return null;
	}

	
	


	
}
