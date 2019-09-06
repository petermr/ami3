package org.contentmine.eucl.euclid;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * Parses common crystallographic representations of a single component of a symmetry operator
 * 
 * Lowercases everything
 * 
 * e.g. x
 * y+2/3
 * 0.5-Z
 * @author pm286
 *
 */
public class ParsedSymop {

	private final static Logger LOG = Logger.getLogger(ParsedSymop.class);
    /** use regex
     * 
     * ([\+\-]?)(x|y|z)                 x,+y,-z
     * ([\+\-]?)(\d*\.\d+)\s*([\+\-])(x|y|z)     0.25+x, .5-y, -0.5-z 
     * ([\+\-]?)(x|y|z)\s*([\+\-])(\d*\.\d+)     x-.25, -y+0.5, z-0.5
     * ([\+\-]?)(1|2|3|4|5|6|7|8|9|10|11)\/(2|3|4|6|12)\s*[\+\-]\s*(x|y|z))  1/2+x, 2/3-y, -7/12+z
     * ([\+\-]?)((x|y|z)\s*[\+\-]\s*(1|2|3|4|5|6|7|8|9|10|11)\/(2|3|4|6|12)) x-1/2, -y+11/12, z+5/6
     */

	private final static Pattern FRACT = Pattern.compile("(\\-?\\d+)/(\\d+)");
	private final static String SIGNED_NUMBER = "([\\+\\-]?\\d*\\.\\d+)";
	private final static String SIGNED_FRACT = "([\\+\\-]?(1|2|3|4|5|6|7|8|9|10|11)\\/(2|3|4|6|12))";
	private final static String SIGNED_XYZ = "(([\\+\\-]?)(x|y|z))";
	public final static Pattern XYZ =  
			Pattern.compile(SIGNED_XYZ+"("+SIGNED_XYZ+"?)");  // signedxyz signedxyz?
	public final static Pattern NUMB_XYZ =  
			Pattern.compile(SIGNED_NUMBER+SIGNED_XYZ+"("+SIGNED_XYZ+"?)");  // signednumber, signedxyz signedxyz?
	public final static Pattern XYZ_NUMB =  
			Pattern.compile(SIGNED_XYZ+"("+SIGNED_XYZ+"?)"+SIGNED_NUMBER);  // signedxyz signedxyz? signednumber
	public final static Pattern FRACT_XYZ =  
			Pattern.compile(SIGNED_FRACT+SIGNED_XYZ+"("+SIGNED_XYZ+"?)");  // signedfract, signedxyz signedxyz?
	public final static Pattern XYZ_FRACT =  
			Pattern.compile(SIGNED_XYZ+"("+SIGNED_XYZ+"?)"+SIGNED_FRACT);  // xyzsign, xyz, numsign, num, denom
	private String xyz;
	private String xyz1;
	private String numberS;
	private String fractS;
	private Double number;
			

	public ParsedSymop() {
		// TODO Auto-generated constructor stub
	}

	public final static ParsedSymop createSymop(String s) {
		ParsedSymop symop = new ParsedSymop();
		try {
			symop.parse(s);
		} catch (RuntimeException e) {
			throw (e);
		}
		return symop;
	}
	
	public final static Transform3 createTransform( String[] ss) {
		return createTransform(new Transform3(), ss);
	}
	/** populate transform with rows for each symop.
	 * 
	 * recoomended to use createTransform( String[] ss)
	 * 
	 * @param t3 empty transform
	 * @param ss
	 * @return
	 */
	public final static Transform3 createTransform(Transform3 t3, String[] ss) {
		for (int i = 0; i < 3; i++) {
			ParsedSymop symop = ParsedSymop.createSymop(ss[i]);
			double[] row = symop.getRow();
			for (int j = 0; j < 4; j++) {
				t3.setElementAt(i, j, row[j]);
			}
		}
		return t3;
	}
			
	private void parse(String s) {
		s = s.toLowerCase();
		s = s.replaceAll(" ", "");
		xyz = null;
		xyz1 = null;
		numberS = null;
		fractS = null;
		// these return at the first match. The xyz, then fracts are commonest
		boolean matched = matchXYZ(s) 
			|| matchFRACT_XYZ(s) || matchXYZ_FRACT(s)
			|| matchNUMB_XYZ(s) || matchXYZ_NUMB(s);
		if (!matched) {
			throw new RuntimeException("Cannot parse as symmetry operator: "+s);
		}
		number = (numberS != null) ? new Double(numberS) : ((fractS != null) ? calculateFract() : null);
	}

	public String getXyz() {
		return xyz;
	}

	public String getXyz1() {
		return xyz1;
	}

	public Double getNumber() {
		return number;
	}

	private Double calculateFract() {
		Matcher matcher = FRACT.matcher(fractS);
		if (!matcher.matches()) {
			throw new RuntimeException("Cannot parse as fraction: "+fractS);
		}
		return new Double(matcher.group(1))/new Double(matcher.group(2));
	}

//	Pattern.compile(SIGNED_XYZ+"("+SIGNED_XYZ+"?)");  // signedxyz signedxyz?
	private boolean matchXYZ(String s) {
		Matcher matcher = XYZ.matcher(s);
		LOG.trace("XYZ "+XYZ+" "+s);
		if (matcher.matches()) {
			debug(matcher);
			LOG.trace("groups"+matcher.groupCount()+matcher);
			xyz = deplus(matcher.group(1));
			xyz1 = deplus(matcher.group(4));
			return true;
		}
		return false;
	}
	
	//	Pattern.compile(SIGNED_XYZ+"("+SIGNED_XYZ+")"+SIGNED_FRACT);  // xyzsign, xyz, numsign, num, denom
	private boolean matchXYZ_FRACT(String s) {
		Matcher matcher = XYZ_FRACT.matcher(s);
		LOG.trace("XYZ_FRACT "+XYZ_FRACT+" "+s);
		if (matcher.matches()) {
			debug(matcher);
			xyz = deplus(matcher.group(1));
			xyz1 = deplus(matcher.group(4));
			fractS = deplus(matcher.group(8));
			return true;
		}
		return false;
	}

//	Pattern.compile(SIGNED_FRACT+SIGNED_XYZ+"("+SIGNED_XYZ+")");  // signedfract, signedxyz signedxyz?
	private boolean matchFRACT_XYZ(String s) {
		Matcher matcher = FRACT_XYZ.matcher(s);
		LOG.trace("FRACT_XYZ "+FRACT_XYZ+" "+s);
		if (matcher.matches()) {
			debug(matcher);
			xyz = deplus(matcher.group(4));
			xyz1 = deplus(matcher.group(7));
			fractS = deplus(matcher.group(1));
			return true;
		}
		return false;
	}

//	Pattern.compile(SIGNED_NUMBER+SIGNED_XYZ+"("+SIGNED_XYZ+")");  // signednumber, signedxyz signedxyz?
	private boolean matchNUMB_XYZ(String s) {
		Matcher matcher = NUMB_XYZ.matcher(s);
		LOG.trace("NUMB_XYZ "+NUMB_XYZ+" "+s);
		if (matcher.matches()) {
			debug(matcher);
			xyz = deplus(matcher.group(2));
			xyz1 = deplus(matcher.group(5));
			numberS = deplus(matcher.group(1));
			return true;
		}
		return false;
	}
	
//	Pattern.compile(SIGNED_XYZ+"("+SIGNED_XYZ+")"+SIGNED_NUMBER);  // signedxyz signedxyz? signednumber
	private boolean matchXYZ_NUMB(String s) {
		Matcher matcher = XYZ_NUMB.matcher(s);
		if (matcher.matches()) {
			debug(matcher);
			xyz = deplus(matcher.group(1));
			xyz1 = deplus(matcher.group(4));
			numberS = deplus(matcher.group(8));
			return true;
		}
		return false;
	}
	
	private void debug(Matcher matcher) {
		for (int i = 1; i < matcher.groupCount()+1; i++) {
			LOG.trace(matcher.group(i));
		}
	}

	private String deplus(String xyz) {
		return xyz.startsWith("+") ? xyz.substring(1) : xyz;
	}

	public double[] getRow() {
		double[] row = new double[4];
		setRow(xyz, row);
		setRow(xyz1, row);
		row[3] = number;
		return row;
	}

	private void addXyz(String xyz, double[][] flmat, int i) {
		int sign = 1;
		String xyz0 = xyz;
		if (xyz.startsWith("-")) {
			sign = -1;
			xyz0 = xyz.substring(1);
		}
		if (xyz0.length() != 1) {
			throw new RuntimeException("Cannot process x/y/z: "+xyz);
		}
		int j = xyz0.charAt(0) - (int) 'x'; // rely on numeric order xyx
		flmat[i][j] = sign;
	}

	private void setRow(String xyz, double[] row) {
		int sign = 1;
		String xyz0 = xyz;
		if (xyz.startsWith("-")) {
			sign = -1;
			xyz0 = xyz.substring(1);
		}
		if (xyz0.length() != 1) {
			throw new RuntimeException("Cannot process x/y/z: "+xyz);
		}
		int j = xyz0.charAt(0) - (int) 'x'; // rely on numeric order xyx
		row[j] = sign;
	}
}
