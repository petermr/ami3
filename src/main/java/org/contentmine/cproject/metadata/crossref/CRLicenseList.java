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

public class CRLicenseList implements Iterable<CRLicense> {

	private static final Logger LOG = Logger.getLogger(CRLicenseList.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private List<CRLicense> licenseList;
	
	public CRLicenseList() {
		this.licenseList = new ArrayList<CRLicense>();
	}
	
	public Iterator<CRLicense> iterator() {
		return licenseList.iterator();
	}
	
	public static CRLicenseList createFrom(JsonElement value) {
		CRLicenseList licenseList1 = new CRLicenseList();
		JsonArray licenses = value.getAsJsonArray();
		for (int i = 0; i < licenses.size(); i++) {
			JsonElement license = licenses.get(i);
			JsonObject jsonlicense = license.getAsJsonObject();
			CRLicense crLicense = CRLicense.createFrom(jsonlicense);
			licenseList1.add(crLicense);
		}
		return licenseList1;
	}

	private void add(CRLicense license) {
		licenseList.add(license);
	}

	public int size() {
		return licenseList.size();
	}
	
	public List<Multiset.Entry<String>> getEntriesSortedByCount() {
		Multiset<String> set = HashMultiset.create();
		for (CRLicense license : licenseList) {
			set.add(license.toString());
		}
		return CMineUtil.getEntryListSortedByCount(set);
	}
	
	public void addAll(CRLicenseList licenses) {
		licenseList.addAll(licenses.licenseList);
		
	}
	
	public String toString() {
		return licenseList.size()+": "+licenseList.toString();
	}
	
}
