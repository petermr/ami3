package org.contentmine.pdf2svg2;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.graphics.PDLineDashPattern;
import org.apache.pdfbox.pdmodel.graphics.state.PDGraphicsState;
import org.apache.pdfbox.pdmodel.graphics.state.PDTextState;

/**
 * saves graphic state from PageDrawer
 * Stil finding my way around this
 * @author pm286
 *
 */
public class AMIGraphicsState {
	private static final Logger LOG = Logger.getLogger(AMIGraphicsState.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private Color textColor;
	private Stroke textStroke;
	private Shape textClip;
	
	PDLineDashPattern dashPattern;
	private Float lineWidth;
	private PDTextState textState;
	private PDGraphicsState graphicsState;


	public static AMIGraphicsState createSavedTextState(Graphics2D graphics) {
		if (graphics == null) {
			return null;
		}
        AMIGraphicsState amiGraphicsState = new AMIGraphicsState();
        amiGraphicsState.textColor = graphics.getColor();
        amiGraphicsState.textStroke = graphics.getStroke();
        amiGraphicsState.textClip = graphics.getClip();
		return amiGraphicsState;
	}

	public void restoreText(Graphics2D graphics) {
        graphics.setStroke(textStroke);
        graphics.setColor(textColor);
        graphics.setClip(textClip);
 	}

	public static AMIGraphicsState createGraphicsState(PDGraphicsState graphicsState) {
        AMIGraphicsState amiGraphicsState = new AMIGraphicsState();
        amiGraphicsState.graphicsState = graphicsState;
        amiGraphicsState.setDashPattern(graphicsState.getLineDashPattern());
        amiGraphicsState.setLineWidth(graphicsState.getLineWidth());
        amiGraphicsState.textState = graphicsState.getTextState();
		return amiGraphicsState;
	}

	public PDGraphicsState restoreGraphicsState() {
		if (graphicsState != null) {
	        graphicsState.setLineDashPattern(getDashPattern());
	        graphicsState.setLineWidth(getLineWidth());
	        graphicsState.setTextState(textState);
		}
        return graphicsState;
 	}

	public PDLineDashPattern getDashPattern() {
		return dashPattern;
	}

	public void setDashPattern(PDLineDashPattern dashPattern) {
		this.dashPattern = dashPattern;
	}

	public Float getLineWidth() {
		return lineWidth;
	}

	public void setLineWidth(Float lineWidth) {
		this.lineWidth = lineWidth;
	}

}
