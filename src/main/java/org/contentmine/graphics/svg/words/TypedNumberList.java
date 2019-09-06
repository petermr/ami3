package org.contentmine.graphics.svg.words;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.eucl.xml.XMLConstants;
import org.contentmine.graphics.svg.SVGTSpan;
import org.contentmine.graphics.svg.SVGText;

public class TypedNumberList implements Iterable<TypedNumber> {
	List<TypedNumber> typedNumberList;
	public String dataType;
	private RealArray realArray;
	private IntArray intArray;
	
	private TypedNumberList() {
		
	}
	
	public static TypedNumberList createFromTextSpans(SVGText text) {
		TypedNumberList typedNumberList = null;
		List<SVGTSpan> tSpanList = text.getChildTSpans();
		if (tSpanList.size() > 0) {
			typedNumberList = TypedNumberList.createList(tSpanList);
		}
		return typedNumberList;
	}
	
	public static TypedNumberList createList(List<SVGTSpan> spanList) {
		TypedNumberList typedNumberList = null;
		if (spanList != null && spanList.size() > 0) {
			typedNumberList = new TypedNumberList();
			typedNumberList.dataType = null;
			for (SVGTSpan tSpan : spanList) {
				TypedNumber typedNumber = new TypedNumber(tSpan);
				if (typedNumber == null || typedNumber.getDataType() == null) {
					return null;
				}
				if (typedNumberList.dataType == null || typedNumberList.dataType.equals(XMLConstants.XSD_INTEGER)) {
					typedNumberList.dataType = typedNumber.getDataType();
				}
				if (XMLConstants.XSD_DOUBLE.equals(typedNumberList.getDataType())) {
					typedNumber.convertToDouble();
				}
				typedNumberList.add(typedNumber);
			}
			for (TypedNumber typedNumber : typedNumberList) {
				typedNumber.setDataType(typedNumberList.getDataType());
			}
		}
		return typedNumberList;
	}

	
	/** return zero if empty
	 * 
	 * @return
	 */
	public Integer size() {
		return typedNumberList == null ? 0 : typedNumberList.size();
	}

	public TypedNumber get(int i) {
		Integer size = size();
		return i < 0 || i >= size ? null : typedNumberList.get(i);
	}

	public Iterator<TypedNumber> iterator() {
		return typedNumberList == null ? null : typedNumberList.iterator();
	}

	public String getDataType() {
		return dataType;
	}

	public List<TypedNumber> getTypedNumberList() {
		return typedNumberList;
	}

	public void add(TypedNumber typedNumber) {
		ensureTypedNumberList();
		typedNumberList.add(typedNumber);
	}

	private void ensureTypedNumberList() {
		if (typedNumberList == null) {
			typedNumberList = new ArrayList<TypedNumber>();
		}
	}

	public RealArray getRealArray() {
		if (realArray == null) {
			if (XMLConstants.XSD_DOUBLE.equals(dataType)) {
				realArray = new RealArray(typedNumberList.size());
				for (int i = 0; i < typedNumberList.size(); i++) {
					realArray.setElementAt(i, (Double) typedNumberList.get(i).getNumber());
				}
			}
		}
		return realArray;
	}
	
	public IntArray getIntArray() {
		if (intArray == null) {
			if (XMLConstants.XSD_INTEGER.equals(dataType)) {
				intArray = new IntArray(typedNumberList.size());
				for (int i = 0; i < typedNumberList.size(); i++) {
					intArray.setElementAt(i, (Integer) typedNumberList.get(i).getNumber());
				}
			}
		}
		return intArray;
	}
	
	public String getNumberString() {
		String[] strings = null;
		if (XMLConstants.XSD_DOUBLE.equals(dataType)) {
			RealArray realArray = getRealArray();
			strings = realArray.getStringValues();
		} else if (XMLConstants.XSD_INTEGER.equals(dataType)) {
			IntArray intArray = getIntArray();
			strings = intArray.getStringValues();
		}
		String string = null;
		if (strings != null) {
			string = Util.concatenate(strings, XMLConstants.S_SPACE);
		}
		return string;
	}

}
