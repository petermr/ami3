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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.graphics.AbstractCMElement;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import nu.xom.Element;
import nu.xom.Node;

/** supports clipPath (dummy at present)
 * 
 * @author pm286
 *
 */
public class SVGClipPath extends SVGElement {

	private static Logger LOG = Logger.getLogger(SVGClipPath.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static final String CLIP_PATH_ATT = "clip-path";
	public final static String TAG = "clipPath";
	public final static String ALL_SVG_PATH_XPATH = ".//svg:clipPath";
//	clip-path="url(#clipPath1)"
	private static Pattern CLIP_PATH_PATTERN = Pattern.compile("url\\(#([^\\)]*)\\)") ;

//	private SVGPath path;
	
	/** constructor
	 */
	public SVGClipPath() {
		super(TAG);
		init();
	}
	
	protected void init() {
		
	}
	
	/** constructor
	 */
	public SVGClipPath(SVGElement element) {
        super(element);
	}
	
	/** constructor
	 */
	public SVGClipPath(Element element) {
        super((SVGElement) element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new SVGClipPath(this);
    }

	/**
	 * @return tag
	 */
	public String getTag() {
		return TAG;
	}

	/** 
	 * Convenience method to extract list of svgPaths in element
	 * 
	 * @param svgElement
	 * @return
	 */
	public static List<SVGClipPath> extractClipPaths(AbstractCMElement svgElement) {
		return SVGClipPath.extractClipPaths(SVGUtil.getQuerySVGElements(svgElement, ALL_SVG_PATH_XPATH));
	}

	/** 
	 * Makes a new list composed of the paths in the list
	 * 
	 * @param elements
	 * @return
	 */
	public static List<SVGClipPath> extractClipPaths(List<SVGElement> elements) {
		List<SVGClipPath> clipPathList = new ArrayList<SVGClipPath>();
		for (AbstractCMElement element : elements) {
			if (element instanceof SVGClipPath) {
				clipPathList.add((SVGClipPath) element);
			}
		}
		return clipPathList;
	}

	public static SVGClipPath getLargestClipPath(List<SVGClipPath> clipPathList) {
		if (clipPathList == null || clipPathList.size() == 0) return null;
		Real2Range largestBox = null;
		SVGClipPath largestClipPath = clipPathList.get(0);
		largestBox = clipPathList.get(0).getBoundingBox();
		for (int i = 1; i < clipPathList.size(); i++) {
			SVGClipPath clipPath = clipPathList.get(i);
			Real2Range box = clipPath.getBoundingBox();
			if (box.includes(largestBox)) {
				largestBox = box;
				largestClipPath = clipPath;
			}
		}
		return largestClipPath;
	}

	public static Multimap<String, SVGElement> getElementsByClipPath(AbstractCMElement svgElement) {
		Multimap<String, SVGElement> svgByClipPathId = ArrayListMultimap.create();
		List<SVGElement> clippedElements = SVGUtil.getQuerySVGElements(svgElement, ".//*[@clip-path]");
		for (SVGElement clippedElement : clippedElements) {
			String clipPathId = SVGClipPath.getClipPathRef(clippedElement);
			svgByClipPathId.put(clipPathId, clippedElement);
		}
		return svgByClipPathId;
	}

	public static String getClipPathRef(SVGElement element) {
		String pathRef = null;
		if (element != null) {
			String clipPathAttVal = element.getAttributeValue(CLIP_PATH_ATT);
			if (clipPathAttVal != null) {
				Matcher matcher = CLIP_PATH_PATTERN.matcher(clipPathAttVal);
				if (matcher.matches()) {
					pathRef = matcher.group(1);
				}
			}
			
		}
		return pathRef;
	}

	public static Map<String, SVGClipPath> getClipPathById(AbstractCMElement svgElement) {
		Map<String, SVGClipPath> clipPathById = new HashMap<String, SVGClipPath>();
		List<SVGClipPath> clipPathList = SVGClipPath.extractClipPaths(svgElement);
		for (SVGClipPath clipPath : clipPathList) {
			String id = clipPath.getId();
			clipPathById.put(id, clipPath);
		}
		return clipPathById;
	}


	public SVGPath getOrCreatePath() {
		List<SVGPath> paths = SVGPath.extractPaths(this);
		return paths.size() == 1 ? paths.get(0) : null;
	}
	
	public String getSignature() {
		return getOrCreatePath() == null ? null : getOrCreatePath().getOrCreateSignatureAttributeValue();
	}
	public String toString() {
		String s = "("+this.getId()+" "+getSignature()+") "+getBoundingBox(); 
		return s;
	}

	/** find only clipPaths which are used in elements.
	 * 
	 * @param svgElement
	 * @return
	 */
	public static List<SVGClipPath> extractUsedClipPaths(AbstractCMElement svgElement) {
		List<SVGClipPath> clipPaths = extractClipPaths(svgElement);
		LOG.trace("clipPaths: "+clipPaths.size());
		for (int i = clipPaths.size() - 1; i >= 0; i--) {
			SVGClipPath clipPath = clipPaths.get(i);
			String id = clipPath.getId();
			List<SVGElement> elementsWithClipPathId = findElementsWithClipPath(svgElement, id);
			if (elementsWithClipPathId.size() == 0) {
				LOG.trace("removed "+id);
				clipPaths.remove(i);
			} else {
				LOG.trace(""+id+"; "+elementsWithClipPathId.size());
			}
		}
		return clipPaths;
	}

	/** finds elements with a clip-path attribute with given value.
	 * 
	 * @param svgElement
	 * @param id
	 * @return
	 */
	public static List<SVGElement> findElementsWithClipPath(AbstractCMElement svgElement, String id) {
		List<SVGElement> elementsWithClipPathId = SVGUtil.getQuerySVGElements(
				svgElement, ".//*[contains(@clip-path,'"+id+"')]");
		return elementsWithClipPathId;
	}

	/** removes clipPaths that are no used in body of element.
	 * 
	 * Frequently clipPaths are defined that are not used and this clutters later analysis.
	 * removes all unused clipPaths from svgElement and from clipPathList
	 * 
	 * @param svgElement
	 * @param clipPathList
	 */
	public static void detachUnusedClipPathElements(AbstractCMElement svgElement, List<SVGClipPath> clipPathList) {
		if (clipPathList != null) {
			int nclip = clipPathList.size();
			for (int i = nclip - 1; i >= 0; i--) {
				SVGClipPath clipPath = clipPathList.get(i);
				String id = clipPath.getId();
				if (SVGClipPath.findElementsWithClipPath(svgElement, id).size() == 0) {
					clipPath.detach();
					clipPathList.remove(i);
				}
			}
		}
	}
}
