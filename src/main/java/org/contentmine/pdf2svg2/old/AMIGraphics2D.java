package org.contentmine.pdf2svg2.old;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.contentmine.pdf2svg2.PageParserOne;

public class AMIGraphics2D {

	private Graphics2D graphics;
	private Color background;
	private Color color;
	private Composite composite;
	private Font font;
	private Paint paint;
	private Stroke stroke;
	private AffineTransform transform;
	private Rectangle2D rect2d;
	private Point2D currentPoint;
	private Integer windingRule;

	
	public AMIGraphics2D() {
		
	}
		
	public AMIGraphics2D(Graphics2D graphics, GeneralPath linePath) {
		this.graphics = graphics;
		background = graphics.getBackground();
		color = graphics.getColor();
		composite = graphics.getComposite();
		font = graphics.getFont();
		paint = graphics.getPaint();
		stroke = graphics.getStroke();
		transform = graphics.getTransform();
		//
		rect2d = linePath.getBounds2D();
		currentPoint = linePath.getCurrentPoint();
		windingRule = linePath.getWindingRule();
		
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((background == null) ? 0 : background.hashCode());
		result = prime * result + ((color == null) ? 0 : color.hashCode());
		result = prime * result + ((composite == null) ? 0 : composite.hashCode());
		result = prime * result + ((font == null) ? 0 : font.hashCode());
		result = prime * result + ((graphics == null) ? 0 : graphics.hashCode());
		result = prime * result + ((paint == null) ? 0 : paint.hashCode());
		result = prime * result + ((stroke == null) ? 0 : stroke.hashCode());
		result = prime * result + ((transform == null) ? 0 : transform.hashCode());
		
		result = prime * result + ((rect2d == null) ? 0 : rect2d.hashCode());
		result = prime * result + ((currentPoint == null) ? 0 : currentPoint.hashCode());
		result = prime * result + ((windingRule == null) ? 0 : windingRule.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AMIGraphics2D other = (AMIGraphics2D) obj;
		if (background == null) {
			if (other.background != null)
				return false;
		} else if (!background.equals(other.background))
			return false;
		if (color == null) {
			if (other.color != null)
				return false;
		} else if (!color.equals(other.color))
			return false;
		if (composite == null) {
			if (other.composite != null)
				return false;
		} else if (!composite.equals(other.composite))
			return false;
		if (font == null) {
			if (other.font != null)
				return false;
		} else if (!font.equals(other.font))
			return false;
		if (graphics == null) {
			if (other.graphics != null)
				return false;
		} else if (!graphics.equals(other.graphics))
			return false;
		if (paint == null) {
			if (other.paint != null)
				return false;
		} else if (!paint.equals(other.paint))
			return false;
		if (stroke == null) {
			if (other.stroke != null)
				return false;
		} else if (!stroke.equals(other.stroke))
			return false;
		if (transform == null) {
			if (other.transform != null)
				return false;
		} else if (!transform.equals(other.transform))
			return false;

		if (rect2d == null) {
			if (other.rect2d != null)
				return false;
		} else if (!rect2d.equals(other.rect2d))
			return false;
		if (currentPoint == null) {
			if (other.currentPoint != null)
				return false;
		} else if (!currentPoint.equals(other.currentPoint))
			return false;
		if (windingRule == null) {
			if (other.windingRule != null)
				return false;
		} else if (!windingRule.equals(other.windingRule))
			return false;
return true;
	}
	
	
	
	public AMIGraphics2D  createDiffGraphics2D(AMIGraphics2D g2d) {
		AMIGraphics2D diff = new AMIGraphics2D();
		if (!this.background.equals(g2d.background)) {
			diff.background = g2d.background;
		}
		if (!this.color.equals(g2d.color)) {
			diff.color = g2d.color;
		}
		if (!this.composite.equals(g2d.composite)) {
			diff.composite = g2d.composite;
		}
		if (!this.font.equals(g2d.font)) {
			diff.font = g2d.font;
		}
		if (!this.graphics.equals(g2d.graphics)) {
			diff.graphics = g2d.graphics;
		}
		if (!this.paint.equals(g2d.paint)) {
			diff.paint = g2d.paint;
		}
		if (!this.stroke.equals(g2d.stroke)) {
			diff.stroke = g2d.stroke;
		}
		if (!this.transform.equals(g2d.transform)) {
			diff.transform = g2d.transform;
		}
		if (!this.rect2d.equals(g2d.rect2d)) {
			diff.rect2d = g2d.rect2d;
		}
		if (this.currentPoint == null || !this.currentPoint.equals(g2d.currentPoint)) {
			diff.currentPoint = g2d.currentPoint;
		}
		if (this.windingRule == null || this.windingRule != g2d.windingRule) {
			diff.windingRule = g2d.windingRule;
		}
		return diff;
	}

	public String getNonNullFields() {
		StringBuilder sb = new StringBuilder();
		appendNonNull(sb, "bg", this.background);
		appendNonNull(sb, "col", this.color);
		appendNonNull(sb, "comp", this.composite);
		appendNonNull(sb, "font", this.font);
		appendNonNull(sb, "gr", this.graphics);
		appendNonNull(sb, "pai", this.paint);
		appendNonNull(sb, "str", this.stroke);
		appendNonNull(sb, "xf", this.transform);
		
		appendNonNull(sb, "rect", this.rect2d);
		appendNonNull(sb, "curp", this.currentPoint);
		appendNonNull(sb, "wr", this.windingRule);
		return sb.toString();
	}

	private void appendNonNull(StringBuilder sb, String title, Color col) {
		if (col != null) sb.append(title+": "+PageParserOne.toRGB(col)+"\n");
	}

	private void appendNonNull(StringBuilder sb, String title, Object obj) {
		if (obj != null) sb.append(title+": "+String.valueOf(obj)+"\n");
	}

	
}
