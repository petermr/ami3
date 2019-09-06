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

public class CRFunderList implements Iterable<CRFunder> {

	private static final Logger LOG = Logger.getLogger(CRFunderList.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private List<CRFunder> funderList;
	
	public CRFunderList() {
		this.funderList = new ArrayList<CRFunder>();
	}
	
	public Iterator<CRFunder> iterator() {
		return funderList.iterator();
	}
	
	public static CRFunderList createFrom(JsonElement value) {
		CRFunderList funderList1 = new CRFunderList();
		JsonArray funders = value.getAsJsonArray();
		for (int i = 0; i < funders.size(); i++) {
			JsonElement funder = funders.get(i);
			JsonObject jsonfunder = funder.getAsJsonObject();
			CRFunder crFunder = CRFunder.createFrom(jsonfunder);
			funderList1.add(crFunder);
		}
		return funderList1;
	}

	private void add(CRFunder funder) {
		funderList.add(funder);
	}

	public int size() {
		return funderList.size();
	}

	public List<Multiset.Entry<String>> getEntriesSortedByCount() {
		Multiset<String> set = HashMultiset.create();
		for (CRFunder funder : funderList) {
			set.add(funder.toString());
		}
		return CMineUtil.getEntryListSortedByCount(set);
	}
	
	public void addAll(CRFunderList funders) {
		funderList.addAll(funders.funderList);
		
	}
	

	public String toString() {
		return funderList.size()+": "+funderList.toString();
	}
	
	
}
