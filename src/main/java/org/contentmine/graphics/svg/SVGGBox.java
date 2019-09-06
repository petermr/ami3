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

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.AbstractCMElement;

public class SVGGBox extends SVGG {
	private static final Logger LOG = Logger.getLogger(SVGGBox.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public static final String BOX = "box";

	public SVGGBox() {
		this.setSVGClassName(BOX);
	}
	
	public void addRect(SVGRect newRect) {
		List<SVGElement> boxList = SVGUtil.getQuerySVGElements(this, "./*[local-name()='"+SVGG.TAG+"' and @class='"+BOX+"']");
		for (AbstractCMElement box0 : boxList) {
			SVGGBox box = SVGGBox.createBox(box0);
		}
	}

	private static SVGGBox createBox(AbstractCMElement box0) {
		LOG.warn("createBox NYI");
		return null;
	}
	
	
	
//	static SVGGBox copy(SVGGBox box) {
//		SVGGBox newBox = new SVGGBox();
//	    newBox.copyAttributesFrom(box);
//	    createSubclassedChildren(box, newBox);
//		return newBox;
//	}
	
//	private static SVGGBox createSVGGBox(SVGSVG svgSvg) {
//		SVGGBox box = null;
//		Elements childSVGs = svgSvg.getChildElements();
//		if (childSVGs.size() == 1) {
//			box = createSVGGBox((SVGG)childSVGs.get(0));
//		} else {
//			box = new SVGGBox();
//			SVGGBox lastBox = null;
//			for (int i = 0; i < childSVGs.size(); i++) {
//				SVGGBox childBox = createSVGGBox((SVGG)childSVGs.get(i));
//				if (lastBox != null) {
//					SVGShape rect = lastBox.getRect();
//					LOG.trace("RECT "+rect);
//					Transform2 t2 = lastBox.getTransform2FromAttribute();
//					LOG.trace(">>>"+t2);
//					childBox.debug("SVGRRRRRRRRRR");
//				}
//				box.appendChild(childBox);
//				lastBox = childBox;
//			}
//		}
//		return box;
//	}
	
//	private static SVGGBox createSVGGBox(SVGG svgg) {
//		SVGGBox newBox = new SVGGBox();
//	    newBox.copyAttributesFrom(svgg);
//	    createSubclassedChildren(svgg, newBox);
//		return newBox;
//	}
	
//	private SVGRect getRect() {
//		Nodes rects = this.query("./*[local-name()='"+SVGRect.TAG+"']");
//		return (rects.size() == 1) ? (SVGRect) rects.get(0) : null;
//	}
	
//	private Real2 getOffset(SVGLayout layout) {
//		SVGRect rect = this.getRect();
//		Real2 offset = new Real2();
//		if (rect != null) {
//			if (SVGLayout.LEFT2RIGHT.equals(layout)) {
//				offset.x = rect.getWidth();
//			} else {
//				offset.y = rect.getHeight();
//			}
//		}
//		return offset;
//	}
	
//	private void addSVGG(SVGGBox childBox) {
//		Transform2 childTransform = childBox.getTransform2FromAttribute();
//		if (childTransform == null) {
//			childTransform = new Transform2();
//		}
//		Real2 totalDelta = new Real2();
//		List<SVGGBox> previousChildBoxes = this.getSVGGBoxChildren();
//		if (previousChildBoxes.size() > 0) {
//			for (SVGGBox previousChildBox : previousChildBoxes) {
//				Real2 delta = previousChildBox.getOffset(this.layout);
//				totalDelta.plusEquals(delta);
//			}
//			childTransform = childTransform.concatenate(new Transform2(new Vector2(totalDelta)));
//		}
//		SVGGBox g = SVGGBox.copy(childBox);
//		g.setTransform(childTransform);
//		this.appendChild(g);
//	}
	
//	private List<SVGGBox> getSVGGBoxChildren() {
//		// "./g"
//		Nodes gNodes = this.query("./*[local-name()='"+SVGG.TAG+"']");
//		List<SVGGBox> gList = new ArrayList<SVGGBox>(gNodes.size());
//		for (int i = 0; i < gNodes.size(); i++) {
//			gList.add(SVGGBox.createSVGGBox((SVGG)gNodes.get(i)));
//		}
//		return gList;
//	}
	
}
