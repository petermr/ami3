package org.contentmine.ami.tools.download.sd;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.tools.download.AbstractDownloader;
import org.contentmine.ami.tools.download.AbstractMetadataEntry;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlA;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlSpan;

import nu.xom.Element;

/** holds metadata for ScienceDirect search result
 * 

 * @author pm286
 *
 */
public class SDMetadataEntry extends AbstractMetadataEntry {
	
	private static final Logger LOG = LogManager.getLogger(SDMetadataEntry.class);
public SDMetadataEntry() {
		super();
	}

	public SDMetadataEntry(AbstractDownloader downloader) {
		super(downloader);
	}

	@Override
	protected String extractDOIFromUrl() {
		throw new RuntimeException("NYI");
	}

	@Override
	protected void extractMetadata() {
		throw new RuntimeException("NYI");
	}

	@Override
	protected String getDOI() {
		throw new RuntimeException("NYI");
	}

	@Override
	protected List<String> getAuthors() {
		throw new RuntimeException("NYI");
	}

	@Override
	public String getCitationLink() {
		throw new RuntimeException("NYI");
	}
	
	


}
