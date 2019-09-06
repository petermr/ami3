package org.contentmine.cproject.metadata.crossref;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.util.CMineUtil;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CRLinkList implements Iterable<CRLink> {

	private static final Logger LOG = Logger.getLogger(CRLinkList.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private List<CRLink> linkList;
	
	public CRLinkList() {
		this.linkList = new ArrayList<CRLink>();
	}
	
	public Iterator<CRLink> iterator() {
		return linkList.iterator();
	}
	
	public static CRLinkList createFrom(JsonElement value) {
		CRLinkList linkList1 = new CRLinkList();
		JsonArray links = value.getAsJsonArray();
		for (int i = 0; i < links.size(); i++) {
			JsonElement link = links.get(i);
			JsonObject jsonlink = link.getAsJsonObject();
			CRLink crLink = CRLink.createFrom(jsonlink);
			linkList1.add(crLink);
		}
		return linkList1;
	}

	private void add(CRLink link) {
		linkList.add(link);
	}

	public int size() {
		return linkList.size();
	}
	
	public List<Multiset.Entry<String>> getEntriesSortedByCount() {
		Multiset<String> set = HashMultiset.create();
		for (CRLink link : linkList) {
			set.add(link.toString());
		}
		return CMineUtil.getEntryListSortedByCount(set);
	}
	
	public void addAll(CRLinkList links) {
		linkList.addAll(links.linkList);
		
	}
	

	
	public String toString() {
		return linkList.size()+": "+linkList.toString();
	}
	
}
