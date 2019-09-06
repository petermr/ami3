package org.contentmine.image.ocr;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGText;

import nu.xom.Attribute;

public class HOCRTitle {
	
	private static final double TEXT_SIZE_ANNOT = 7.0;
	private static final Logger LOG = Logger.getLogger(HOCRTitle.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private static final String BASELINE = "baseline";
	private static final String BBOX = "bbox";
	private static final String IMAGE = "image";
	private static final String PPAGENO = "ppageno";
	private static final String TEXTANGLE = "textangle";
	private static final String X_ASCENDERS = "x_ascenders";
	private static final String X_DESCENDERS = "x_descenders";
	private static final String X_SIZE = "x_size";
	private static final String X_WCONF = "x_wconf";
	
	private final static Pattern BASELINE_PATTERN = Pattern.compile("\\s*baseline\\s+(-?\\d*\\.?\\d+)\\s+(-?\\d*\\.?\\d+)\\s*");
	private final static Pattern BBOX_PATTERN = Pattern.compile("\\s*bbox\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s*");
	private final static Pattern IMAGE_NAME_PATTERN = Pattern.compile("\\s*image\\s+\"(.*)\"\\s*");
	private final static Pattern PPAGENO_PATTERN = Pattern.compile("\\s*ppageno\\s+(\\d+)\\s*");
	private final static Pattern TEXTANGLE_PATTERN = Pattern.compile("\\s*textangle\\s+(-?\\d*\\.?\\d+)\\s*");
	private final static Pattern X_ASCENDERS_PATTERN = Pattern.compile("\\s*x_ascenders\\s+(\\-?\\d+\\.?\\d*)\\s*");
	private final static Pattern X_DESCENDERS_PATTERN = Pattern.compile("\\s*x_descenders\\s+(\\-?\\d+\\.?\\d*)\\s*");
	private final static Pattern X_SIZE_PATTERN = Pattern.compile("\\s*x_size\\s+(\\d+\\.?\\d*)\\s*");
	private final static Pattern X_WCONF_PATTERN = Pattern.compile("\\s*x_wconf\\s+(\\d+)\\s*");

	private String[] fields;
	private String title;
	private Real2 baseline;
	private Real2Range bbox;
	private String imageName;
	private Integer ppageno;
	private Double textangle;
	private Double xAscenders;
	private Double xDescenders;
	private Double xSize;
	private Integer xwconf;
	
	public HOCRTitle(String title) {
		this.title = title;
		processTitle();
	}
	
	private void processTitle() {
		fields = title.trim().split("\\s*;\\s*");
		for (String field : fields) {
			if (field.startsWith(BBOX)) {
				bbox = createBBox(field);
			} else if (field.startsWith(IMAGE)) {
				imageName = createImageName(field);
				LOG.debug("IMAGE: "+imageName);
			} else if (field.startsWith(X_WCONF)) {
				xwconf = createXWConf(field);
//				LOG.debug("XWCONFtt: "+xwconf+" : "+title);
			} else if (field.startsWith(PPAGENO)) {
				ppageno = createPPageNo(field);
				LOG.debug("PAGENO: "+ppageno);
			} else if (field.startsWith(BASELINE)) {
				baseline = createBaseline(field);
			} else if (field.startsWith(TEXTANGLE)) {
				textangle = Util.format(createTextangle(field), 1);
			} else if (field.startsWith(X_ASCENDERS)) {
				xAscenders = Util.format(createXAscenders(field), 1);
			} else if (field.startsWith(X_DESCENDERS)) {
				xDescenders = Util.format(createXDescenders(field), 1);
			} else if (field.startsWith(X_SIZE)) {
				xSize = Util.format(createXSize(field), 1);
			} else {
				LOG.warn("********** unknown title field: "+field+ " ************");
			}
		}		
	}

	private Double createTextangle(String field) {
		Matcher matcher = TEXTANGLE_PATTERN.matcher(field);
		if (matcher.matches()) {
			textangle = new Double(matcher.group(1));
		} else {
			throw new RuntimeException("Cannot parse textangle: "+field);
		}
		return textangle;
	}

	private Integer createPPageNo(String field) {
		Matcher matcher = PPAGENO_PATTERN.matcher(field);
		if (matcher.matches()) {
			ppageno = new Integer(matcher.group(1));
		} else {
			throw new RuntimeException("Cannot parse ppageno: "+field);
		}
		return ppageno;
	}

	private Double createXSize(String field) {
		Matcher matcher = X_SIZE_PATTERN.matcher(field);
		if (matcher.matches()) {
			xSize = new Double(matcher.group(1));
		} else {
			throw new RuntimeException("Cannot parse x_size: "+field);
		}
		return xSize;
	}

	private Double createXAscenders(String field) {
		Matcher matcher = X_ASCENDERS_PATTERN.matcher(field);
		if (matcher.matches()) {
			xAscenders = new Double(matcher.group(1));
		} else {
			throw new RuntimeException("Cannot parse x_ascenders: "+field);
		}
		return xAscenders;
	}

	private Double createXDescenders(String field) {
		Matcher matcher = X_DESCENDERS_PATTERN.matcher(field);
		if (matcher.matches()) {
			xDescenders = new Double(matcher.group(1));
		} else {
			throw new RuntimeException("Cannot parse x_descenders: "+field);
		}
		return xDescenders;
	}


	private Integer createXWConf(String field) {
		Matcher matcher = X_WCONF_PATTERN.matcher(field);
		if (matcher.matches()) {
			xwconf = new Integer(matcher.group(1));
		} else {
			throw new RuntimeException("Cannot parse xwconf: "+field);
		}
		return xwconf;
	}

	private String createImageName(String field) {
		Matcher matcher = IMAGE_NAME_PATTERN.matcher(field);
		if (matcher.matches()) {
			imageName = matcher.group(1);
		} else {
			throw new RuntimeException("Cannot parse imagename: "+field);
		}
		return imageName;
	}

	private Real2 createBaseline(String field) {
		Matcher matcher = BASELINE_PATTERN.matcher(field);
		if (matcher.matches()) {
			Double x = new Double(matcher.group(1));
			Double y = new Double(matcher.group(2));
			baseline = new Real2(x, y);
		} else {
			throw new RuntimeException("Cannot parse baseline: "+field);
		}
		return baseline;
	}
	
	private Real2Range createBBox(String field) {
		Matcher matcher = BBOX_PATTERN.matcher(field);
		if (matcher.matches()) {
			Integer x0 = new Integer(matcher.group(1));
			Integer y0 = new Integer(matcher.group(2));
			Integer x1 = new Integer(matcher.group(3));
			Integer y1 = new Integer(matcher.group(4));
			bbox = new Real2Range(new RealRange(x0, x1), new RealRange(y0, y1));
		} else {
			throw new RuntimeException("Cannot parse bbox: "+field);
		}
		return bbox;
	}
	
	public Real2Range getBoundingBox() {
		return bbox;
	}

	public static HOCRTitle getHOCRTitle(HtmlElement sp) {
		HOCRTitle hocrTitle = null;
		String title = sp == null ? null : sp.getTitle();
		if (title != null) {
			hocrTitle = new HOCRTitle(title);
		}
		return hocrTitle;
	}

	public Double getTextangle() {
		return textangle;
	}

	public Double getAscender() {
		return xAscenders;
	}

	public Double getDescender() {
		return xDescenders;
	}

	public Double getSize() {
		return xSize;
	}

	public Real2 getBaseline() {
		return baseline;
	}

	public String getImageName() {
		return imageName;
	}

	/** get confidence estimation */
	public Integer getWConf() {
		if (xwconf != null) {
//			LOG.debug("XWCONF "+xwconf);
		}
		return xwconf;
	}

	public void addAttributes(SVGG g) {
		if (baseline != null) {
			g.addAttribute(new Attribute(BASELINE, String.valueOf(baseline)));
		}
		if (textangle != null) {
			g.addAttribute(new Attribute(TEXTANGLE, String.valueOf(textangle)));
		}
		if (xwconf != null) {
			g.addAttribute(new Attribute(X_WCONF, String.valueOf(xwconf)));
		}
	}
	
	public String getTitle() {
		return title;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("textangle="+textangle+"; ");
		sb.append("ascender="+xAscenders+"; ");
		sb.append("descender="+xDescenders+"; ");
		sb.append("size="+xSize+"; ");
		sb.append("imageName="+imageName+"; ");
		sb.append("conf="+xwconf+"; ");
		return sb.toString();
	}

	void addTextSizeAnnotation(SVGElement svgElement, Real2Range bbox) {
		Double size = getSize();
		if (size != null) {
			SVGText text = new SVGText(bbox.getLLURCorners()[0], ""+size);
			text.setFontSize(TEXT_SIZE_ANNOT);
			svgElement.appendChild(text);
		}
	}

}
