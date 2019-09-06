package org.contentmine.pdf2svg.cmap;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import nu.xom.Attribute;
import nu.xom.Serializer;

/** not finished and I am not sure it's necessary for PDF2SVG
 * 
 * @author pm286
 *
 */
public class NumericSerializer extends Serializer {

	private OutputStream os;

	public NumericSerializer(OutputStream os, String encoding) throws UnsupportedEncodingException {
		super(os, encoding);
		this.os = os;
	}
	
//	@Override
	protected void writeOld(Attribute attribute) {
		String name = attribute.getLocalName();
		String value = attribute.getValue();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < value.length(); i++) {
			char character = value.charAt(i);
			int characterValue = (int) character;
			if (characterValue > 127) {
				sb.append("&#"+characterValue+";");
			} else {
				sb.append(characterValue);
			}
		}
		String attributeValue = sb.toString();
		String attributeString = " "+name+"="+"\""+attributeValue+"\" ";
		System.out.println(attributeString);
		byte[] bytes = attributeString.getBytes();
		try {
//			os.write(bytes);
			writeAttributeValue(attributeValue);
		} catch (IOException e) {
			throw new RuntimeException("Cannot write attribute");
		}
	}

//	@Override
//	protected void write(Attribute attribute) throws IOException {
//		
//	}

	@Override
	protected void write(Attribute attribute) throws IOException {
		String name = attribute.getLocalName();
		String value = attribute.getValue();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < value.length(); i++) {
			char character = value.charAt(i);
			int characterValue = (int) character;
			if (characterValue > 127) {
				sb.append("&#"+characterValue+";");
			} else if (character == '&' || character == '\'' || character == '\"' ) {
				sb.append("&#"+characterValue+";");
			} else {
				sb.append(character);
			}
		}
		String attributeValue = sb.toString();
		Attribute newAttribute = new Attribute(name, attributeValue);
		super.write(newAttribute);
	}

}
