/**
 * Copyright (C) 2012 pm286 <peter.murray.rust@googlemail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.contentmine.pdf2svg.util;

import org.apache.pdfbox.util.Matrix;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.RealMatrix;
import org.contentmine.eucl.xml.XMLConstants;
import org.contentmine.graphics.svg.SVGElement;

import nu.xom.Attribute;

public class PDF2SVGUtil {

	public final static String SVGX_NS = "http://www.xml-cml.org/schema/svgx";
	public final static String SVGX_PREFIX = "svgx";
	public static final String CHARACTER_WIDTH = "width";
	public static final String CHARACTER_CODE = "charCode";
	public static final String CHARACTER_HEX = "hexCode";
	public static final String CHARACTER_NAME = "charName";
	public static final String CHARACTER_NEW_CODE = "newCode";
	public static final String FONT_ENCODING = "fontEnc";
	public static final String LIGATURE = "ligature";
	public static final String TEXT_CHAR = "textChar";
	public static final String TEXT_HEX = "textHex";
	
	public static void setSVGXAttribute(SVGElement svgElement, String attName, String value) {
		if (attName != null && value != null) {
			Attribute attribute = new Attribute(SVGX_PREFIX+XMLConstants.S_COLON+attName, SVGX_NS, value);
			svgElement.addAttribute(attribute);
		}
	}

	public static String getSVGXAttribute(SVGElement svgElement, String attName) {
		Attribute attribute = svgElement.getAttribute(attName, SVGX_NS);
		return (attribute == null) ? null : attribute.getValue();
	}

	/** extracts PDMatrix to RealMatrix
	 * 
	 * @param fontMatrix
	 * @return
	 */
	public static RealMatrix getRealMatrix(Matrix fontMatrix) {
		RealMatrix rm = new RealMatrix(2, 3);
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 3; j++) {
				rm.setElementAt(i,  j, fontMatrix.getValue(i, j));
			}
		}
		return rm;
	}

	/** extracts PDMatrix to array of doubles in RealArray
	 * 
	 * @param fontMatrix
	 * @return
	 */
	public static RealArray getRealArray(Matrix fontMatrix) {
		double[] dd = new double[9];
		int kk = 0;
		int nrow = 2;
		int ncol = 3;
		for (int irow = 0; irow < nrow; irow++) {
			for (int jcol = 0; jcol < ncol; jcol++) {
				dd[kk++] = fontMatrix.getValue(irow, jcol);
			}
		}
		RealArray ra = new RealArray(dd);
		return ra;
	}


}
