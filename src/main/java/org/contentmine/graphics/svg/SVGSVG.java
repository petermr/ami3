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

import java.awt.Graphics2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.xml.XMLConstants;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.text.SVGWordPage;
import org.contentmine.graphics.svg.text.SVGWordPageList;

import nu.xom.Attribute;
import nu.xom.Node;

/** container for SVG
 * "svg"
 * @author pm286
 *
 */
public class SVGSVG extends SVGElement {

	private static final Logger LOG = Logger.getLogger(SVGSVG.class);
	
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public final static String TAG = "svg";
	private static String svgSuffix = "svg";
	private static final Double BBOX_MARGIN_X = 10.0;
	private static final Double BBOX_MARGIN_Y = 10.0;
	
	private Double begin = null;
	private Double dur = null;
	private SVGWordPageList wordPageList;
	
	/** constructor.
	 * 
	 */
	public SVGSVG() {
		super(TAG);
		addDefaults();
	}
	
	private void addDefaults() {
		this.addMarkerDefs();
	}

	private void addMarkerDefs() {
		SVGDefs defs = new SVGDefs();
//		this.appendChild(defs);
//		defs.appendChild(SVGMarker.ZEROLINE.copy());
//		defs.appendChild(SVGMarker.ZEROPATH.copy());
	}

	/** constructor
	 */
	public SVGSVG(SVGSVG element) {
        super(element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new SVGSVG(this);
    }

	/**
	 * @return tag
	 */

	public String getTag() {
		return TAG;
	}

	protected void drawElement(Graphics2D g2d) {
		super.drawElement(g2d);
	}
	
	public void setId(String id) {
		this.addAttribute(new Attribute("id", id));
	}
	
	public String getId() {
		return this.getAttributeValue("id");
	}

	/** defaults to height=800 width=700.
	 * 
	 * */
	public static SVGSVG wrapAndWriteAsSVG(List<? extends SVGElement> svgList, File file) {
		SVGG g = new SVGG();
		if (svgList != null) {
			for (SVGElement element : svgList) {
				g.appendChild(element.copy());
			}
		}
		g.setBoundingBoxCached(false);
		g.getBoundingBox();
//		LOG.debug("wrote "+file.getAbsolutePath());
		return wrapAndWriteAsSVG(g, file);
	}
	
	/** defaults to heigh=800 width=700.
	 * 
	 * */
	public static SVGSVG wrapAndWriteAsSVG(SVGElement svgg, File file) {
		if (svgg == null) {
			LOG.warn("NULL svgg");
			return null;
		}
		Real2Range bbox = svgg.getBoundingBox();
		if (bbox == null || !bbox.isValid()) {
			LOG.trace("***ERROR*** NULL bbox: "+bbox+" // "+svgg.toXML());
			svgg = new SVGText(new Real2(5., 5.), "null/empty bbox: ");
			svgg.setFontSize(100.);
			svgg.setFontWeight(FontWeight.BOLD);
			return wrapAndWriteAsSVG(svgg, file, 1000, 500.);
		}
		return wrapAndWriteAsSVG(svgg, file, bbox.getXMax() + BBOX_MARGIN_X, bbox.getYMax() + BBOX_MARGIN_Y);
	}
	
	/**	creates an SVGSVG wrapper for any element and outputs to file.
	 * 
	 *   <p>mainly for debugging.</p>
	 *   
	 * @param svgg
	 * @param file
	 * @param height
	 * @param width
	 * @return
	 */
	public static SVGSVG wrapAndWriteAsSVG(AbstractCMElement svgg, File file, double width, double height) {
		SVGSVG svgsvg = svgg instanceof SVGSVG ? (SVGSVG) svgg : new SVGSVG();
		if (svgg != null) {
			svgsvg = wrapAsSVG(svgg);
			svgsvg.setHeight(height);
			svgsvg.setWidth(width);
			try {
				LOG.trace("Writing SVG "+file.getAbsolutePath());
				svgsvg.writeQuietly(file);
			} catch (Exception e) {
				throw new RuntimeException("cannot write svg to "+file, e);
			}
		}
		return svgsvg;
	}

	public static SVGSVG wrapAsSVG(AbstractCMElement svgg) {
		SVGSVG svgsvg = null;
		if (svgg != null) {
			if (svgg.getParent() != null) {
				svgg.detach();
			}
			if (!(svgg instanceof SVGSVG)) {
				svgsvg = new SVGSVG();
		//		svgsvg.setNamespaceURI(SVGConstants.SVGX_NS);
				svgsvg.appendChild(svgg);
			} else {
				svgsvg = (SVGSVG) svgg;
			}
		}
		return svgsvg;
	}

	public static String createFileName(String id) {
		return id + XMLConstants.S_PERIOD+svgSuffix ;
	}

	public void setDur(Double d) {
		this.dur  = d;
	}

	public void setBegin(Double d) {
		this.begin = d;
	}
	
//	/** traverse all children recursively
//	 * @return bbox
//	 */
//	public Real2Range getBoundingBox() {
//		if (boundingBoxNeedsUpdating()) {
//			aggregateBBfromSelfAndDescendants();
//		}
//		return boundingBox;
//	}


	/**
	 * adds a new svg:g between element and its children
	 * this can be used to set scales, rendering, etc.
	 * @param element to amend (is changed)
	 */
	public static AbstractCMElement interposeGBetweenChildren(AbstractCMElement element) {
		AbstractCMElement g = new SVGG();
		element.appendChild(g);
		while (element.getChildCount() > 1) {
			Node child = element.getChild(0);
			child.detach();
			g.appendChild(child);
		}
		return g;
	}

	public SVGWordPage getSingleSVGPage() {
		getSVGPageList();
		return wordPageList == null ? null : (SVGWordPage) XMLUtil.getSingleElement(wordPageList, "*[@class='"+SVGWordPage.CLASS+"']");
	}

	public SVGWordPageList getSVGPageList() {
		wordPageList = (SVGWordPageList) XMLUtil.getSingleElement(this, "*[@class='"+SVGWordPageList.CLASS+"']");
		return wordPageList;
	}

	public void setMarker(SVGMarker marker) {
		appendChild(new SVGMarker(marker));
	}

	public SVGDefs getOrCreateDefs() {
		SVGDefs defs = (SVGDefs) XMLUtil.getSingleElement(this, "*[local-name()='"+SVGDefs.TAG+"']");
		if (defs == null) {
			defs = new SVGDefs();
			this.insertChild(defs, 0);
		}
		return defs;
	}

	public void writeQuietly(File file) {
		if (file == null) {
			LOG.trace("ERROR Cannot write null file");
		} else {
			try {
				File parentFile = file.getParentFile();
				if (parentFile != null) parentFile.mkdirs();
				FileOutputStream fos = new FileOutputStream(file);
				SVGUtil.debug(this, fos, 1);
				fos.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

}
