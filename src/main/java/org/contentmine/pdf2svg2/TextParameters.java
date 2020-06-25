package org.contentmine.pdf2svg2;

import java.awt.geom.AffineTransform;
import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDFontDescriptor;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.Vector;
import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.Real;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.RealSquareMatrix;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.graphics.svg.SVGText;

public class TextParameters {
	private static final Logger LOG = LogManager.getLogger(TextParameters.class);
public final static double EPS = 1e-10;
	public final static double SCALE_EPS = 1e-5;

	private Matrix matrix;
	private PDFont font;
	private Transform2 transform2;
	private Angle angle;
	private PDFontDescriptor fdescriptor;
	private String style;

	public TextParameters(Matrix matrix, PDFont font) {
		if (matrix == null) {
			throw new RuntimeException("null matrix");
		}
		this.matrix = matrix;
		this.font = font;
		double[][] array = matrix.getValuesAsDouble();
		RealSquareMatrix rsm = new RealSquareMatrix(array);
		transform2 = new Transform2(rsm);
		angle = transform2.getAngleOfRotation();
		LOG.trace("angle "+angle);
		if (font == null) {
			throw new RuntimeException("null font");
		}
    	LOG.trace(matrix.getScaleX()+
    			"/"+matrix.getScaleY()+
    			"/"+matrix.getScalingFactorX()+
    			"/"+matrix.getScalingFactorY()+
    			"//"+matrix.getTranslateX()+
    			"/"+matrix.getTranslateY()+
    			""
    			);
    	
    	 //Font/TrueType/KAJWHP+Helvetica/org.apache.pdfbox.pdmodel.font.PDFontDescriptor@26aa12dd/[0.001,0.0,0.0,0.001,0.0,0.0]
    			 
    	fdescriptor = font.getFontDescriptor();
    	if (fdescriptor != null) {
	    	// more later
			LOG.trace("fw "+font.getAverageFontWidth()+   // 472.5 
	    			"/sw "+font.getSpaceWidth()+            // 633.78906
	    			"/ty "+font.getType()+                  // Font
	    			"/st "+font.getSubType()+               // TrueType
	    			"/nm "+font.getName()+                  // KAJWHP+Helvetica
	    			"/"+fdescriptor+        // object
	    			"{"+
	    			    "ff: "+fdescriptor.getFontFamily()+";"+
	    			    "wt: "+fdescriptor.getFontWeight()+";"+
	    			    "it: "+fdescriptor.isItalic()+";"+
	    			    "sy: "+fdescriptor.isSymbolic()+";"+
	    			"}"+
	    			"/"+font.getFontMatrix()+            // /[0.001,0.0,0.0,0.001,0.0,0.0]  3*2 ??
	    			"");
    	}
	}

	/** create TextParameters from previously processed/generated SVGText.
	 * 
	 * @param text
	 */
	public TextParameters(SVGText text) {
		Transform2 t2 = text.getTransform();
		if (t2 != null) {
			AffineTransform at = t2.getAffineTransform();
			matrix = new Matrix(at);
		} else {
			LOG.info("text has no Transform");
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((font == null) ? 0 : font.hashCode());
		result = prime * result + ((matrix == null) ? 0 : matrix.hashCode());
		return result;
	}

	/** I don't understand all these but they may be useful.
	 * 
	 * @param code
	 * @return
	 */
	public Real2 getDisplacement(int code) {
		Vector v = null;
		try {
			v = font.getDisplacement(code);
		} catch (IOException e) {
			LOG.error("Bad code: "+code);
		}
		return v == null ? null : new Real2(v.getX(), v.getY());
	}
	
	public String getFontName() {
		return fdescriptor == null ? null : fdescriptor.getFontName();
	}
	
	public String getFontFamily() {
		return fdescriptor == null ? null : fdescriptor.getFontFamily();
	}
	public Double getFontWeight() {
        return fdescriptor == null ? null : (Double) (double) fdescriptor.getFontWeight();
	}
	
	public boolean isAllCap() {
		return fdescriptor == null ? false : fdescriptor.isAllCap();
	}
	
    public boolean isForceBold() {
    	return fdescriptor == null ? false : fdescriptor.isForceBold();
    }
    
    public boolean isItalic() {
    	if (fdescriptor != null) {
    		boolean italic = fdescriptor.isItalic();
    		if (italic) {
//    			LOG.debug("F "+fdescriptor.getFontName());
//    			throw new RuntimeException("ITALIC");
    		}
    		return italic;
    	}
    	return false;
    }
    
	public boolean isSerif() {
		return fdescriptor == null ? false : fdescriptor.isSerif();
	}
	
	public boolean isScript() {
		return fdescriptor == null ? false : fdescriptor.isScript();
	}
	
	public boolean isSmallCap() {
		return fdescriptor == null ? false : fdescriptor.isSmallCap();
	}
	
    public boolean isSymbolic() {
    	return fdescriptor == null ? false : fdescriptor.isSymbolic();
    }

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TextParameters other = (TextParameters) obj;
		if (font == null) {
			if (other.font != null)
				return false;
		} else if (!font.equals(other.font))
			return false;
		if (matrix == null) {
			if (other.matrix != null)
				return false;
		} else if (!matrix.equals(other.matrix))
			return false;
		return true;
	}
	
	public boolean hasEqualFont(TextParameters textParameters) {
		if (textParameters == null) return false;
		PDFont tFont = textParameters.font;
		if (this.font.equals(tFont)) {
			return true;
		}
//		LOG.debug("fontDiff" + this+"===> \n"+textParameters);
		return false;
	}

	public Matrix getMatrix() {
		return matrix;
	}

	public PDFont getFont() {
		return font;
	}

	public Real2 getScales() {
		return new Real2(matrix.getScaleX(), matrix.getScaleY());
	}

	public Double getFontSize() {
		return new Double(matrix.getScaleX());
	}

	public Transform2 getTransform2() {
		return transform2;
	}

	public Angle getAngle() {
		return angle;
	}
	
	public String getStyle() {
		return style;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(""+matrix+"\n");
		sb.append(""+font+"\n"+font.getFontDescriptor());
		return sb.toString();
	}

	public boolean hasNormalOrientation() {
		// any shear is not normal
		return 
			Real.isZero(matrix.getShearX(), EPS) &&
			Real.isZero(matrix.getShearY(), EPS) &&
		// both scales should be positive
		   matrix.getScaleX() > 0.0 &&
		   matrix.getScaleY() > 0.0
		   ;
	}

	public String getCSSTransformValue() {
		return "matrix("
				+matrix.getValue(0, 0)+" "
				+matrix.getValue(0, 1)+" "
				+matrix.getValue(1, 0)+" "
				+matrix.getValue(1, 1)+" "
				+matrix.getValue(0, 2)+" "
				+matrix.getValue(1, 2)+
				")";
	}

	public boolean isScaleChanged(TextParameters lastTextParameters) {
		return lastTextParameters == null ||
			(!Real.isEqual(lastTextParameters.getScaleX(), this.getScaleX(), SCALE_EPS) ||
			!Real.isEqual(lastTextParameters.getScaleY(), this.getScaleY(), SCALE_EPS)
			);
	}

	private Double getScaleX() {
		return matrix == null ? null : (double) matrix.getScaleX();
	}

	private Double getScaleY() {
		return matrix == null ? null : (double) matrix.getScaleY();
	}
}
