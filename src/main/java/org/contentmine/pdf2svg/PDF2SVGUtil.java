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
package org.contentmine.pdf2svg;

import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.util.Matrix;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.RealMatrix;
import org.contentmine.eucl.xml.XMLConstants;

public class PDF2SVGUtil {

	public static final String CHARACTER_WIDTH = "width";
	public static final String CHARACTER_CODE = "charCode";
	public static final String CHARACTER_NAME = "charName";
	public static final String CHARACTER_NEW_CODE = "newCode";
	public static final String FONT_ENCODING = "fontEnc";
	public static final String LIGATURE = "ligature";
	private static final String DOT = XMLConstants.S_PERIOD;
	private static final String DOT_DOT = DOT+XMLConstants.S_PERIOD;
	
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

	/** removes ./ and ../ from a resources name
	 * e.g. a/../b => b
	 * a/./b => a/b
	 * a/b../../c/d => c/d
	 * @param resource
	 * @return
	 */
	public static String normalizeResource(String resource) {
		resource = resource.trim();
		String[] ss = resource.split(XMLConstants.S_SLASH);
		List<String> sList = new ArrayList<String>();
		for (int i = 0; i <ss.length; i++) {
			if (ss[i].trim().length() == 0){
				// avoid creating "//" fails on some systems
			} else if (ss[i].equals(DOT)){
				continue;
			} else if (ss[i].equals(DOT_DOT)) {
				if (sList.size() == 0) {
					throw new RuntimeException("Cannot start resource with ../ or unbalanced ../");
				}
				sList.remove(sList.size()-1);
			} else {
				sList.add(ss[i]);
			}
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0 ; i < sList.size(); i++) {
			if (i > 0) {
				sb.append(XMLConstants.S_SLASH);
			}
			sb.append(sList.get(i));
		}
		if (resource.endsWith(XMLConstants.S_SLASH)) {
			sb.append(XMLConstants.S_SLASH);
		}
		return sb.toString();
	}


}
