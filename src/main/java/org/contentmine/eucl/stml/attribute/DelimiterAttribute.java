/**
 *    Copyright 2011 Peter Murray-Rust et. al.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.contentmine.eucl.stml.attribute;

import org.contentmine.eucl.euclid.Util;

import nu.xom.Attribute;

/**
 * user-modifiable class supporting "ref", a pointer
 * to a STML object. default is to clone the object and then process it.
 * For that reason the referenced species should be pre-declared.
 */
public class DelimiterAttribute extends StringSTAttribute {

	/** action when ensuringAttribute
	 * 
	 * @author pm286
	 *
	 */
	public enum Action {
		/** reset to null */
		RESET,
		/** preserve existing */
		PRESERVE;
		private Action() {
		}
	}
    /** dewisott */
    public final static String NAME = "delimiter";
    
    private String splitter = null;
    private String concat = null;
	/**
     * constructor.
     * 
     */
    public DelimiterAttribute() {
        super(NAME);
    }

    /** constructor.
     * @param value
     */
    public DelimiterAttribute(String value) {
        super(NAME);
        this.setSTMLValue(value);
    }

    /**
     * constructor.
     * 
     * @param att
     * @exception RuntimeException
     */
    public DelimiterAttribute(Attribute att) throws RuntimeException {
        super(att);
        this.setSTMLValue(att.getValue());
    }
    
    /** set value and process.
     * 
     * @param value
     * @exception RuntimeException bad value
     */
    public void setSTMLValue(String value) throws RuntimeException {
        if (value == null) {
        	value = S_SPACE;
        }
        value = value.trim();
        if (value.equals(S_EMPTY)) {
        	value = S_SPACE;
        } else if (value.length() > 1) {
        	throw new RuntimeException("Non-whitespace delimiter must only be single character");
        }
        super.setSTMLValue(value);
        if (value.equals(S_SPACE)) {
        	setSplitter(S_WHITEREGEX);
        	setConcat(S_SPACE);
        } else {
        	setSplitter(getEscapedDelimiter(value));
        	setConcat(value);
        }
    }
    
    /**
	 * @return the concat
	 */
	public String getConcat() {
		return concat;
	}

	/**
	 * @param concat the concat to set
	 */
	public void setConcat(String concat) {
		this.concat = concat;
	}

	/**
	 * @return the splitter
	 */
	public String getSplitter() {
		return splitter;
	}

	/**
	 * @param splitter the splitter to set
	 */
	public void setSplitter(String splitter) {
		this.splitter = splitter;
	}

    /**
     * adds escape for regex metacharacters. e.g. '|' transforms to '\\|'
     *
     * @param delim
     * @return the escaped string
     */
    private static String getEscapedDelimiter(String delim) {
        String delim1 = delim;
        if (delim.length() == 1) {
	        // FIXME - need to add other regex characters
	        if (delim.equals(S_PIPE) ||
		        delim.equals(S_QUERY) ||
		        delim.equals(S_STAR) ||
		        delim.equals(S_PERIOD)) {
	            delim1 = "\\" + delim;
	        }
        }
        return delim1;

    }

    /**
     * 
     * @param content
     * @return split strings
     */
    public String[] getSplitContent(String content) {
        String[] ss = new String[0];
	    content = content.trim();
	    if (content.length() > 0) {
	    	if (!isWhitespace()) {
	            if (content.startsWith(concat)) {
	                content = content.substring(1);
	            }
	            if (content.endsWith(concat)) {
	                content = content.substring(0, content.length() - concat.length());
	            }
	    	}
	        ss = content.split(splitter);
	    }
	    return ss;
    }

    /**
     * checks that components does not clash with delimiter.
     *
     * @param s string to check
     * @throws RuntimeException if d is part of s
     */
    public void checkDelimiter(String s) throws RuntimeException {
        if (s.split(splitter).length > 1) {
            throw new RuntimeException("cannot delimit {" + s + "} with {" + concat + S_RCURLY);
        }
    }

    /**
     * set text content. if delimiter is not whitespace, prepend and append it
     *
     * @param s
     * @return string
     */
    public String getDelimitedXMLContent(String s) {
    	if (s == null) {
    		s = S_EMPTY;
    	}
        if (!s.equals(S_EMPTY)) {
        	if (!isWhitespace()) {
	          	if (!s.startsWith(concat)) {
	        		s = concat + s;
	        	}
	        	if (!s.endsWith(concat)) {
	        		s += concat;
	        	}
        	}
        }
        return s;
    }

    /**
     * append to text content. if delimiter is not whitespace, prepend and
     * append it
     *
     * @param s previous string
     * @param snew to append
     * @return xml content
     */
    public String appendXMLContent(String s, String snew) {
    	s = getDelimitedXMLContent(s);
        if (!isWhitespace()) {
            if (s.length() == 0) {
                s = concat + snew + concat;
            } else {
                s += (snew + concat);
            }
        } else {
            s += (concat + snew);
        }
        return s;
    }
    
    private boolean isWhitespace() {
    	return S_WHITEREGEX.equals(splitter);
    }

    /** set text content. if delimiter is not whitespace, prepend and append it
     *
     * @param ss
     * @return string
     */
    public String getDelimitedXMLContent(String[] ss) {
    	for (String s : ss) {
    		checkDelimiter(s);
    	}
    	String s = Util.concatenate(ss, concat);
        if (!isWhitespace()) {
            s = concat + s + concat;
        }
        return s;
    }
    
    /** set double content. if delimiter is not whitespace, prepend and append it
     *
     * @param dd array of doubles
     * @return string
     */
    public String getDelimitedXMLContent(boolean[] bb) {
    	for (boolean b: bb) {
    		checkDelimiter(S_EMPTY+b);
    	}
    	String s = Util.concatenate(bb, concat);
        if (!isWhitespace()) {
            s = concat + s + concat;
        }
        return s;
    }
    
    
    /** set double content. if delimiter is not whitespace, prepend and append it
     *
     * @param dd array of doubles
     * @return string
     */
    public String getDelimitedXMLContent(double[] dd) {
    	for (double d: dd) {
    		checkDelimiter(S_EMPTY+d);
    	}
    	String s = Util.concatenate(dd, concat);
        if (!isWhitespace()) {
            s = concat + s + concat;
        }
        return s;
    }
    
    /** set int content. if delimiter is not whitespace, prepend and append it
     *
     * @param ii int array
     * @return content
     */
    public String getDelimitedXMLContent(int[] ii) {
    	for (int i : ii) {
    		checkDelimiter(S_EMPTY+i);
    	}
    	String s = Util.concatenate(ii, concat);
        if (!isWhitespace()) {
            s = concat + s + concat;
        }
        return s;
    }
    
    /** debug.
     * 
     * @param s name of debug output
     */
    public void debug(String s) {
    	Util.println("-------- "+s+" -------");
    	Util.println(this+" .. "+this.getValue()+" .. "+this.splitter+" .. "+concat);
    }
}
