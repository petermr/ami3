package org.contentmine.pdf2svg2.old;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.Vector;
import org.contentmine.eucl.euclid.Util;

public class FontGlyph {

	private Matrix textMatrix;
	private PDFont font;
	private int code;
	private String unicode;
	private Vector displacement;
	private int ndecxy = 2;
	private Double translateX;
	private Double translateY;
	private Double scaleX;
	private Double scaleY;

	public FontGlyph(Matrix textRenderingMatrix, PDFont font, int code, String unicode, Vector displacement) {
		this.textMatrix = textRenderingMatrix;
		this.font = font;
		this.code = code;
		this.unicode = unicode;
		this.displacement = displacement;
		this.translateX = Util.format(textRenderingMatrix.getTranslateX(), ndecxy);
		this.translateY = Util.format(textRenderingMatrix.getTranslateY(), ndecxy);
		this.scaleX = Util.format(textRenderingMatrix.getScaleX(), ndecxy);
		this.scaleY = Util.format(textRenderingMatrix.getScaleY(), ndecxy);
	}

	public PDFont newFont(FontGlyph oldFontGlyph) {
		return (oldFontGlyph == null || !oldFontGlyph.font.equals(font)) ? font : null;
	}
	public Double newTranslateX(FontGlyph oldFontGlyph) {
		return (oldFontGlyph == null || !oldFontGlyph.translateX.equals(translateX)) ? translateX : null;
	}
	public Double newTranslateY(FontGlyph oldFontGlyph) {
		return (oldFontGlyph == null || !oldFontGlyph.translateY.equals(translateY)) ? translateY : null;
	}
	public Double newScaleX(FontGlyph oldFontGlyph) {
		return (oldFontGlyph == null || !oldFontGlyph.scaleX.equals(scaleX)) ? scaleX : null;
	}
	public Double newScaleY(FontGlyph oldFontGlyph) {
		return (oldFontGlyph == null || !oldFontGlyph.scaleY.equals(scaleY)) ? scaleY : null;
	}

	public PDFont getFont() {
		return font;
	}
}
