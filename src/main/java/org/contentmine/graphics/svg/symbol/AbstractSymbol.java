package org.contentmine.graphics.svg.symbol;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.graphics.svg.GraphicsElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGText;

/** 
 * Symbols on plots.
 * <p>
 * All classes have a graphic representation, e.g. an SVGPolyline / SVGPolygon.
 * <p>
 * The SVG output will use standard SVG, enhanced by annotated with class, e.g.
 * &lt;g class="TRIANGLE"&gt;&lt;polygon points="0 0 0 1 0.5 0.886"/&gt;&lt;/g&gt;.
 * These classes will try to track abstraction of the Unicode.
 * <p>
 * Many symbols have Unicode equivalents / near-equivalents. This class will develop according to 
 * what people use. This is sometimes switched by colour (e.g. TRIANGLE / fill="black" is
 * a different Unicode from white triangle).
 * <p>
 * Not sure yet whether rotation matters. ROT0 is the "normal" orientation.
 * <p>
 * The graphic representation may be one or more single SVGElements.
 * 
 * @author pm286
 */
public abstract class AbstractSymbol extends SVGG {

	private final static Logger LOG = Logger.getLogger(AbstractSymbol.class);
	
	public enum SymbolOrientation {
		NONE,
		ROT0,
		ROTPI4,
		ROTPI2,
		ROTPI,
		ROT3PI4,
	}

    public enum SymbolFill {
		NONE,
		ALL,
		LEFT,
		RIGHT,
		BOTTOM,
		TOP,
		NE,
		SE,
		SW,
		NW
	}

    // "reasonable defaults"
	private String unicodeString = "?!"; 
	private SymbolFill fill = SymbolFill.NONE;
	private SymbolOrientation orientation = SymbolOrientation.ROT0;
	private boolean isDotted = false;
	private SVGText text;

	protected AbstractSymbol() {
		super();
		this.createSVGElement();
	}
	
	protected void setOrientation(SymbolOrientation orientation) {
		this.orientation = orientation;
	}
	
	public SymbolOrientation getOrientation() {
		return orientation;
	}

	protected void setSymbolFill(SymbolFill fill) {
		this.fill = fill;
	} 

	public SymbolFill getSymbolFill() {
		return fill;
	}

	protected void setUnicodeString(String unicodeString) {
		this.unicodeString = unicodeString;
		text.setText(unicodeString);
	}
	
	public String getUnicode() {
		return unicodeString;
	}
	
	protected void setDotted(boolean dotted) {
		this.isDotted = dotted;
	}
	
	public boolean isDotted() {
		return isDotted;
	}
	
	@Override
	/** sets file to fill and then stroke to "none".
	 * 
	 * Do not allow separate stroke.
	 * 
	 */
	public GraphicsElement setFill(String fill) {
		super.setFill(fill);
		super.setStroke(null);
		return this;
	}

	@Override
	/** sets fill to stroke and then stroke to "none".
	 * 
	 * Do not allow separate stroke.
	 * 
	 */
	public GraphicsElement setStroke(String stroke) {
		this.setFill(stroke);
		return this;
	}
	

	protected void createSVGElement() {
		this.deleteExistingChildNodes();
		text = new SVGText(new Real2(0.001, 0.001), getUnicode());
		text.setFontSize(null);
		this.appendChild(text);
	}

	protected void deleteExistingChildNodes() {
		int nchild = this.getChildCount();
		for (int i = 0; i < nchild; i++) {
			this.getChild(0).detach();
		}
	}
	
}
