package org.contentmine.cproject.lookup;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.IntArray;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import nu.xom.Element;

public abstract class AbstractLookup {
	
	private static final Logger LOG = Logger.getLogger(AbstractLookup.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private HashMap<String, String> lookupRefByMatch;
	private String name = "MUST_SET_THIS";
	protected String outputFormat;
	protected URL url;
	protected String urlString;
	private String outputType;
	
	public AbstractLookup() {
		
	}
	
	public abstract String lookup(String key) throws IOException;

	protected IntArray getIdentifierArray(JsonElement jsonElement, String arrayName) {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		JsonArray itemsArray = jsonObject.getAsJsonArray(arrayName);
		int size = itemsArray.size();
		IntArray intArray = new IntArray();
		for (int i = 0; i < size; i++) {
			intArray.addElement(((JsonElement)itemsArray.get(i)).getAsInt());
		}
		return intArray;
	}

	protected List<Integer> getIdentifierList(JsonElement jsonElement, String arrayName) {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		JsonArray itemsArray = jsonObject.getAsJsonArray(arrayName);
		int size = itemsArray.size();
		List<Integer> intList = new ArrayList<Integer>();
		for (int i = 0; i < size; i++) {
			intList.add(((JsonElement)itemsArray.get(i)).getAsInt());
		}
		return intList;
	}

	public Element getResponseXML(URL url) throws IOException {
		String content = this.getResponse(url);
		Element element = org.contentmine.eucl.xml.XMLUtil.parseXML(content);
		return element;
	}

	/** gets content of URL as a string.
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
    protected String getResponse() throws IOException {
		createUrl();
		return getResponse(url);
    }

	/** gets content of URL as a string.
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
    protected String getResponse(URL url) throws IOException {
    	if (url != null) {
	    	LOG.trace("url: "+url);
	        URLConnection urlc = url.openConnection();
	        //use post mode
	        urlc.setDoOutput(true);
	        InputStream is = null;
	        try {
	        	urlc.setAllowUserInteraction(false);
	        	is = urlc.getInputStream();
	        } catch (java.net.UnknownHostException uhe) {
	        	LOG.error("UnknownHostException: (might be offline) "+url);
	        } catch (Error e) {
	        	LOG.error("cannot connect: "+e+"; "+url);
	        }
	        return is == null ? null : IOUtils.toString(is);
    	} else {
    		return null;
    	}
    }

	public Map<String, String> getOrCreateLookupRefByMatch() {
		if (lookupRefByMatch == null) {
			lookupRefByMatch = new HashMap<String, String>();
		}
		return lookupRefByMatch;
	}

	public String getName() {
		return name;
	}

	public void setOutputFormat(String format) {
		this.outputFormat = format;
	}

	public void setOutputType(String type) {
		this.outputType = type;
	}

	protected URL createUrl() {
		url = null;
		if (urlString != null) {
			String urlStringFull = urlString;
			if (outputFormat != null) {
				urlStringFull += outputFormat;
			}
			if (outputType != null) {
				urlStringFull += outputType;
			}
			try {
				this.url = new URL(urlStringFull);
			} catch (MalformedURLException mule) {
				LOG.error("Malformed URL: "+urlStringFull);
			}
		}
		return url;
	}

}
