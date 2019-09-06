package org.contentmine.cproject.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

public class URLShuffler {

	public static final Logger LOG = Logger.getLogger(URLShuffler.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static final String SEP = "___"; // 
	
	// allows for pigeon holes larger than required. Not currently used.
	private static int TOL = 1;
	private List<String> urls;
	private Multiset<String> hosts;
	private Map<String, Integer> currentIndexByHost;
	private Map<String, Integer> countByHost;
	private List<String> outputUrls;
	
	private String host; // e.g. dx.doi.org
	private String protocol; // e.g. http
	private String file; // e.g. /10.1023/foo/bar

	private boolean pseudoHost;

	private String link;

	public URLShuffler() {
		
	}
	
	public void readURLs(List<String> urls) {
		this.urls= urls;
		hosts = HashMultiset.create();
		for (String url : urls) {
			String host;
			try {
				host = getHost(url);
				hosts.add(host);
			} catch (MalformedURLException e) {
				LOG.error("bad URL", e);
			}
		}
	}

	private String getHost(String urlString) throws MalformedURLException {
		URL url = new URL(urlString);
		host = url.getHost(); 
		protocol = url.getProtocol(); 
		file = url.getFile(); 
		if (pseudoHost) {
			addLeadingFileToHost();
		}
		return host;
	}

	private void addLeadingFileToHost() {
		// starts with "/"
		int idx = file.substring(1).indexOf("/");
		if (idx != -1) {
			link = file.substring(1,  idx+1);
			file = file.substring(idx + 1);
			host += SEP + link;
		}
	}

	public void setPseudoHost(boolean pseudoHost) {
		this.pseudoHost = pseudoHost;
	}
	
	public List<String> getShuffledUrls() {
		currentIndexByHost = new HashMap<String, Integer>();
		countByHost = new HashMap<String, Integer>();
		
		outputUrls = new ArrayList<String>();
		for (int i = 0; i < urls.size() * TOL; i++) {
			outputUrls.add("");
		}
		for (Multiset.Entry<String> entry : CMineUtil.getEntriesSortedByCount(hosts)) {
			countByHost.put(entry.getElement(), entry.getCount());
			currentIndexByHost.put(entry.getElement(), entry.getCount() - 1);
		}
		for (String url : urls) {
			String host = null;
			try {
				host = getHost(url);
			} catch (MalformedURLException e) {
				LOG.error("bad URL "+url);
				continue;
			}
			Integer currentIndex = currentIndexByHost.get(host);
			Integer count = countByHost.get(host);
			int slot = (urls.size() * currentIndex * TOL) / count;
			currentIndexByHost.put(host, currentIndex - 1);
			addUrl(url, slot);
		}
		
		return outputUrls;
	}

	private void addUrl(String url, int slot) {
		boolean filled = fillLower(url, slot);
		if (!filled) {
			fillUpper(url, slot);
		}
	}

	private boolean fillUpper(String url, int slot) {
		for (int i = slot; i < outputUrls.size(); i++) {
			if (fill(url, i)) {
				return true;
			}
		}
		return false;
	}

	private boolean fillLower(String url, int slot) {
		for (int i = slot; i >= 0; i--) {
			if (fill(url, i)) {
				return true;
			}
		}
		return false;
	}

	private boolean fill(String url, int slot) {
		if (outputUrls.get(slot).equals("")) {
			outputUrls.set(slot, url);
			return true;
		}
		return false;
	}
}
