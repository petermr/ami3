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

	public boolean showFontGlyph = true;
	public boolean showColor = true;
	public boolean showBeginText = true;
	public boolean showEndText = true;
	public boolean showAppendRectangle = true;
	public boolean showStrokePath = true;
	public boolean showFillPath = true;
	public boolean showFillAndStrokePath = true;
	public boolean showMoveTo = true;
	public boolean showClip = true;
	public boolean showEndMarked = true;
	public boolean showBeginMarked = true;
	public boolean showShowTransGrp = true;
	public boolean showForm  = true;
	public boolean showAnnotation = true;
	public boolean showShadingFill = true;
	public boolean showLineTo = true;
	public boolean showCurveTo = true;
	public boolean showCurrentPoint = true;
	public boolean showClosePath = true;
	public boolean showEndPath = true;
	public boolean showDrawImage = true;
	
	public AMIDebugParameters() {
		
	}

	public static AMIDebugParameters getDefaultParameters() {
		AMIDebugParameters parameters = new AMIDebugParameters();
		parameters.showFontGlyph = true;
		parameters.showColor = true;
		parameters.showBeginText = true;
		parameters.showEndText = true;
		parameters.showAppendRectangle = true;
		parameters.showStrokePath = true;
		parameters.showFillPath = true;
		parameters.showFillAndStrokePath = true;
		parameters.showMoveTo = true;
		parameters.showClip = true;
		parameters.showEndMarked = true;
		parameters.showBeginMarked = true;
		parameters.showShowTransGrp = true;
		parameters.showForm  = true;
		parameters.showAnnotation = true;
		parameters.showShadingFill = true;
		parameters.showLineTo = true;
		parameters.showCurveTo = true;
		parameters.showCurrentPoint = true;
		parameters.showClosePath = true;
		parameters.showEndPath = true;
		parameters.showDrawImage = true;
		return parameters;
	}
	
}
