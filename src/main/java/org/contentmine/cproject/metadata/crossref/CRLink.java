package org.contentmine.cproject.metadata.crossref;

import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 *       {
        "intended-application": "text-mining",
        "content-version": "vor",
        "content-type": "application/pdf",
        "URL": "http://api.wiley.com/onlinelibrary/tdm/v1/articles/10.1002%2Fchem.201600718"
      }

 * @author pm286
 *
 */
public class CRLink {

	private static final Logger LOG = Logger.getLogger(CRLink.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public final static String INTENDED_APPLICATION = "intended-application";
	public final static String CONTENT_VERSION = "content-version";
	public final static String CONTENT_TYPE = "content-type";
	public final static String URL = "URL";
	
	private String intendedApplication;
	private String contentVersion;
	private String contentType;
	private String url;

	public CRLink() {
		
	}

	public static CRLink createFrom(JsonObject jsonAuthor) {
		CRLink link = new CRLink();
		for (Map.Entry<String, JsonElement> entry : jsonAuthor.entrySet()) {
			String key = entry.getKey();
			JsonElement value = entry.getValue();
			if (INTENDED_APPLICATION.equals(key)) {
				link.intendedApplication = value.getAsString();
			} else if (CONTENT_VERSION.equals(key)) {
				link.contentVersion = value.getAsString();
			} else if (CONTENT_TYPE.equals(key)) {
				link.contentType = value.getAsString();
			} else if (URL.equals(key)) {
				link.url = value.getAsString();
			} else {
				LOG.warn("unknown link field: "+key);
			}
		}
		return link;
	}

	public String toString() {
		String s = "";
		if (contentType != null) {
			s = contentType;
		}
		if (url != null) {
			s += " "+url;
		}
		return s;
	}
	

}
