package org.contentmine.ami.tools.download;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class ResultSet {
	private static final Logger LOG = Logger.getLogger(ResultSet.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private List<AbstractMetadataEntry> metadataEntryList;
	private String urlString;
	private String base;
	private URL url;
	
	public ResultSet() {
		this.metadataEntryList = new ArrayList<>();
	}

	public ResultSet(List<AbstractMetadataEntry> metadataEntryList) {
		this.metadataEntryList = new ArrayList<>(metadataEntryList);
	}

	public int size() {
		return metadataEntryList.size();
	}

	public void setUrl(String urlString) {
		this.urlString = urlString;
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			throw new RuntimeException("malformed URL "+urlString, e);
		}
		this.base = createBaseFromUrl();
	}

	private String createBaseFromUrl() {
//		System.err.println("url "+url);
//		System.err.println("protocol "+url.getProtocol());
//		System.err.println("host "+url.getHost());
//		System.err.println("file "+url.getFile());
//		System.err.println("query "+url.getQuery());
//		System.err.println("auth "+url.getAuthority());
//		System.err.println("path "+url.getPath());
		base = url.getProtocol() + "://" + url.getHost();
//		System.err.println("BASE "+base);
		return this.base;
	}
	
	public String getBase() {
		return base;
	}
}
