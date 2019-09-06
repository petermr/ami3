package org.contentmine.cproject.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/** gets InputStream from string.
 * 
 *  heuristic ducktype to get input stream;
	 * 
	 * four types, drops through in order
	 * 
	 * SYMBOL resolved by sourceByName.get(name)
	 * RESOURCE resolved by this.getClass().getResourceAsStream(name)
	 * URL resolved by new URL(name).openStream()
	 * FILE resolved by new FileInputStream(name)
	 * 
	 * sourceByName has to be set by user.
	 * 
	 * first looks to see if name is a key in sourceByName. If so, takes the 
	 * result and passes it to getInputStream(String name); if not, passes the
	 * original name
	 * 
	 * e.g. getInputStream("abc", map) might resolve to http://foo.org/abc.xml
	 * which is then resolved to a stream. 
	 * 
	 * 

 * @author pm286
 *
 */
public class ResourceLocation {

	private static final Logger LOG = Logger.getLogger(ResourceLocation.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	enum ResourceType {
		URL,
		FILE,
		RESOURCE,
		SYMBOL;
	}
	
	public static final String DOI = "doi:";
	public static final String HTTP = "http://";
	public static final String HTTPS = "https://";
	public static final String HTM = "htm";
	public static final String HTML = "html";
	public static final String PDF = "pdf";
	public static final String SVG = "svg";
	public static final String XML = "xml";
	
	public static final String LINE_NUMBER = "lineNumber";
	public static final String LINE_VALUE = "lineValue";
	public static final String XPATH = "xpath";
	
	private ResourceLocation.ResourceType resourceType;
	private Map<String, String> sourceByName;
	
	public ResourceLocation() {
		this.resourceType = null;
	}
	
	public static boolean endsWithSeparator(String filename) {
		return filename != null && FilenameUtils.indexOfLastSeparator(filename) == filename.length()-1;
	}

	/** crude tool to guess whether is URL from name.
	 * 
	 * @param name
	 * @return
	 */
	public static boolean isURL(String name) {
		return name.startsWith(HTTP) || name.startsWith(HTTPS);
	}

	/** heuristic ducktype to get input stream;
	 * 
	 * First assumes name is symbol (and sourceByName has been set).
	 * First assumes name is resource on classpath.
	 * if fails; tries it as http:// or https:// URL
	 * if fails; tries as filename
	 * 
	 * @param name (of resource, URL, or filename)
	 * @return Opened stream, or null if not found
	 */
	public InputStream getInputStreamHeuristically(String name) {
		return getInputStreamHeuristically(this.getClass(), name);
	}

	public InputStream getInputStreamHeuristically(Class clazz, String name) {
		InputStream is = null;
		is = getStreamFromResource(clazz, name);

		if (is == null) {
			is = getStreamFromURL(name);
		}
		if (is == null) {
			try {
				File f = new File(name);
				if (!f.exists()) {
					throw new FileNotFoundException("file "+f);
				}
				LOG.debug("FILE "+f.getAbsolutePath());
				is = new FileInputStream(name);
			} catch (FileNotFoundException e) {
				if (ResourceLocation.ResourceType.FILE.equals(resourceType)) {
					throw new RuntimeException("failed to resolve as FILE: "+name);
				}
			}
		}
		return is;
	}


	private InputStream getStreamFromURL(String name) {
		InputStream is = null;
		try {
			is = new URL(name).openStream();
			resourceType = ResourceType.URL;
		} catch (Exception e) {
			if (ResourceLocation.ResourceType.URL.equals(resourceType)) {
				throw new RuntimeException("failed to resolve as URL: "+name);
			}
		}
		return is;
	}

	private InputStream getStreamFromResource(String name) {
		return getStreamFromResource(ResourceLocation.class, name);
	}

	private InputStream getStreamFromResource(Class clazz, String name) {
		InputStream is = clazz.getResourceAsStream(name);
		if (is == null ) {
			if (ResourceLocation.ResourceType.RESOURCE.equals(resourceType)) {
				throw new RuntimeException("failed to resolve as RESOURCE: "+name);
			}
		}
		return is;
	}

	public InputStream getStreamFromSymbol(String name) {
		InputStream is = null;
		if (name != null && sourceByName != null) {
			String sourceName = sourceByName.get(name);
			if (sourceName != null) {
				is = this.getInputStreamHeuristically(sourceName);
				if (is == null) {
					if (ResourceLocation.ResourceType.SYMBOL.equals(resourceType)) {
						throw new RuntimeException("failed to resolve as SYMBOL: "+name);
					}
				}
			}
		}
		return is;
	}

	public ResourceLocation.ResourceType getResourceType() {
		return resourceType;
	}

	public void setResourceType(ResourceLocation.ResourceType resourceType) {
		this.resourceType = resourceType;
	}

	public Map<String, String> getSourceByName() {
		return sourceByName;
	}

	public void setSourceByName(Map<String, String> sourceByName) {
		this.sourceByName = sourceByName;
	}



}
