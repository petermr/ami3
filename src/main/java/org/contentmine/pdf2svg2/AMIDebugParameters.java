package org.contentmine.pdf2svg2;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/** DTO parameters for debugging
 * 
 * @author pm286
 *
 */
public class AMIDebugParameters {
	private static final Logger LOG = LogManager.getLogger(AMIDebugParameters.class);
// default - show everything
	public boolean showAnnotation = true;
	public boolean showAppendRectangle = /*true*/ false;
	public boolean showBeginMarked = true;
	public boolean showBeginText = true;
	public boolean showChar = true;
	public boolean showClip = true;
	public boolean showClosePath = true;
	public boolean showColor = true;
	public boolean showCurveTo = true;
	public boolean showCurrentPoint = true;
	public boolean showDrawImage = true;
	public boolean showDrawPage = true;
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
	public boolean checkViewBox = true;
	public boolean maxPrimitives = true;

	public AMIDebugParameters() {
		
	}

	public static AMIDebugParameters getDefaultParameters() {
		AMIDebugParameters parameters = new AMIDebugParameters();
		parameters.showAnnotation = true;
		parameters.showAppendRectangle = /*true */false;
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
		parameters.checkViewBox = true;
		parameters.maxPrimitives = true;
		
		parameters.debugGraphics = true;
		return parameters;
	}
	
	public static AMIDebugParameters getBriefParameters() {
		AMIDebugParameters parameters = new AMIDebugParameters();
		parameters.showAnnotation = false;
		parameters.showAppendRectangle = false /*true*/;
		parameters.showBeginMarked = false;
		parameters.showBeginText = false;
		parameters.showChar = false;
		parameters.showClip = /*false*/ false;
		parameters.showClosePath = false;
		parameters.showColor = false;
		parameters.showCurveTo = false;
		parameters.showCurrentPoint = false;
		parameters.showDrawImage = false;
		parameters.showEndMarked = false;
		parameters.showEndPath = false;
		parameters.showEndText = false;
		parameters.showFillPath = false;
		parameters.showFillAndStrokePath = false;
		parameters.showFontGlyph = false;
		parameters.showForm  = false;
		parameters.showLineTo = false;
		parameters.showMoveTo = false;
		parameters.showPaint = false;
		parameters.showShadingFill = false;
		parameters.showStrokePath = false;
		parameters.showTransGrp = false;
		parameters.checkViewBox = false;
		parameters.maxPrimitives = false;
		
		parameters.debugGraphics = false;
		return parameters;
	}
	
}
