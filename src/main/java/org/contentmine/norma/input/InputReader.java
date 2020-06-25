package org.contentmine.norma.input;

import java.io.IOException;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.norma.InputFormat;
import org.contentmine.norma.RawInput;
import org.contentmine.norma.image.ocr.HOCRReaderOLD;
import org.contentmine.norma.input.html.HtmlReader;

public class InputReader {

	private static final Logger LOG = LogManager.getLogger(InputReader.class);

	public static InputReader createReader(InputFormat type) {
		InputReader reader = null;
		if (type == null) {
			LOG.debug("no input type");
		} else if (type.equals(InputFormat.HTML)) {
			reader = new HtmlReader();
		} else if (type.equals(InputFormat.HOCR)) {
			reader = new HOCRReaderOLD();
		} else {
			throw new RuntimeException("Unknown/unsupported input type: "+type);
		}
		return reader;
	}

	public RawInput read(InputStream inputStream) throws IOException {
		byte[] rawBytes = IOUtils.toByteArray(inputStream);
		RawInput rawInput = new RawInput(rawBytes);
		return rawInput;
	}

}
