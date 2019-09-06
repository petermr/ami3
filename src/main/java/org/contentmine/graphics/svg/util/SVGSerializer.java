package org.contentmine.graphics.svg.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;

import nu.xom.Serializer;
import nu.xom.Text;

public class SVGSerializer extends Serializer {
	private final static Logger LOG = Logger.getLogger(SVGSerializer.class);
	public SVGSerializer(OutputStream os) {
		super(os);
	}

	public SVGSerializer(OutputStream os, String encoding) throws UnsupportedEncodingException {
		super(os, encoding);
	}


	@Override
	/**
	 * replaces occurrences of (char)12345 by &#12345; in outputStream
	 */
	public void write(Text text) throws IOException {
		String s = text.getValue();
		LOG.trace(s.length());
		StringBuilder sb = new StringBuilder();
		int codePointCount = s.codePointCount(0, s.length());
		int charIndex = 0;
		for (int i = 0; i < codePointCount; i++) {
			int codepoint = s.codePointAt(charIndex);
			int charCount = Character.charCount(codepoint);
			LOG.trace(codepoint+" "+charCount);
			charIndex += charCount;
			if (codepoint > 127) {
				sb.append("&#");
				sb.append(codepoint);
				sb.append(";");
// escape the main XML characters				
			} else if (codepoint == (char)'&') {
				sb.append("&amp;");
			} else if (codepoint == (char)'<') {
				sb.append("&lt;");
			} else if (codepoint ==(char)'>') {
				sb.append("&gt;");
			} else if (codepoint ==(char)'\'') {
				sb.append("&apos;");
			} else if (codepoint ==(char)'"') {
				sb.append("&quot;");
			} else {
				sb.append((char)codepoint);
			}
		}
		writeRaw(sb.toString());
	}

}
