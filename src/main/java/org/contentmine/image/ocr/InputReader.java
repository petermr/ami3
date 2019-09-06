package org.contentmine.image.ocr;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.contentmine.eucl.InputFormat;
import org.contentmine.eucl.RawInput;

public class InputReader {

	private static final Logger LOG = Logger.getLogger(InputReader.class);

	public static InputReader createReader(InputFormat type) {
		InputReader reader = null;
		if (type == null) {
			LOG.debug("no input type");
		} else if (type.equals(InputFormat.HTML)) {
//			reader = new HtmlReader();
			throw new RuntimeException("skipping HTML Reader till refactored");
		} else if (type.equals(InputFormat.HOCR)) {
			reader = new HOCRReader();
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
