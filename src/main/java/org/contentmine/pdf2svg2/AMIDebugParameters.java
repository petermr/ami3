package org.contentmine.pdf2svg2;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/** DTO parameters for debugging
 * 
 * @author pm286
 *
 */
public class AMIDebugParameters {
	private static final Logger LOG = Logger.getLogger(AMIDebugParameters.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	// default - show everything
	public boolean showAnnotation = true;
	public boolean showAppendRectangle = true;
	public boolean showBeginMarked = true;
	public boolean showBeginText = true;
	public boolean showChar = true;
	public boolean showClip = true;
	public boolean showClosePath = true;
	public boolean showColor = true;
	public boolean showCurveTo = true;
	public boolean showCurrentPoint = true;
	public boolean showDrawImage = true;
	public boolean showEndMarked = true;
	public boolean showEndPath = true;
	public boolean showEndText = true;
	public boolean showFillPath = true;
	public boolean showFillAndStrokePath = true;
	public boolean showFontGlyph = true;
	public boolean showForm  = true;
	public boolean showLineTo = true;
	public boolean showMoveTo = true;
	public boolean showPaint = true;
	public boolean showShadingFill = true;
	public boolean showStrokePath = true;
	public boolean showTransGrp = true;

	public boolean debugGraphics = true;

	public AMIDebugParameters() {
		
	}

	public static AMIDebugParameters getDefaultParameters() {
		AMIDebugParameters parameters = new AMIDebugParameters();
		parameters.showAnnotation = true;
		parameters.showAppendRectangle = /*true*/ false;
		parameters.showBeginMarked = true;
		parameters.showBeginText = true;
		parameters.showChar = true;
		parameters.showClip = /*true*/ false;
		parameters.showClosePath = true;
		parameters.showColor = true;
		parameters.showCurveTo = true;
		parameters.showCurrentPoint = true;
		parameters.showDrawImage = true;
		parameters.showEndMarked = true;
		parameters.showEndPath = true;
		parameters.showEndText = true;
		parameters.showFillPath = true;
		parameters.showFillAndStrokePath = true;
		parameters.showFontGlyph = true;
		parameters.showForm  = true;
		parameters.showLineTo = true;
		parameters.showMoveTo = true;
		parameters.showPaint = true;
		parameters.showShadingFill = true;
		parameters.showStrokePath = true;
		parameters.showTransGrp = true;
		
		parameters.debugGraphics = true;
		return parameters;
	}
	
}
