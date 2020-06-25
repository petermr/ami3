package org.contentmine.cproject.files;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/** largely a DataTransferObject
 * holds templates and switches for IO in CProject and CTrees
 * 
 * @author pm286
 *
 */
public class CProjectIO {
	private static final Logger LOG = LogManager.getLogger(CProjectIO.class);
private boolean writeSVGPages;
	private boolean writeRawImages;

	
	public CProjectIO() {
		setDefaults();
	}


	private void setDefaults() {
		this.setWriteSVGPages(true);
		this.setWriteRawImages(true);
	}


	public boolean isWriteSVGPages() {
		return writeSVGPages;
	}


	public void setWriteSVGPages(boolean writeSVGPages) {
		this.writeSVGPages = writeSVGPages;
	}


	public boolean isWriteRawImages() {
		return writeRawImages;
	}


	public void setWriteRawImages(boolean writeRawImages) {
		this.writeRawImages = writeRawImages;
	}

	
	
}
