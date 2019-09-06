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

public class CRPersonList implements Iterable<CRPerson> {

	private static final Logger LOG = Logger.getLogger(CRPersonList.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private List<CRPerson> personList;
	
	public CRPersonList() {
		this.personList = new ArrayList<CRPerson>();
	}
	
	public Iterator<CRPerson> iterator() {
		return personList.iterator();
	}
	
	public static CRPersonList createFrom(JsonElement value) {
		CRPersonList PersonList1 = new CRPersonList();
		JsonArray Persons = value.getAsJsonArray();
		for (int i = 0; i < Persons.size(); i++) {
			JsonElement Person = Persons.get(i);
			JsonObject jsonPerson = Person.getAsJsonObject();
			CRPerson crPerson = CRPerson.createFrom(jsonPerson);
			PersonList1.add(crPerson);
		}
		return PersonList1;
	}

	private void add(CRPerson Person) {
		personList.add(Person);
	}

	public int size() {
		return personList.size();
	}
	
	public String toString() {
		return personList.size()+": "+personList.toString();
	}

	public List<CRPerson> getPersonList() {
		return personList;
	}

	public List<String> getPersonsAsStrings() {
		List<String> Persons = new ArrayList<String>();
		for (CRPerson Person : personList) {
			Persons.add(Person.toString());
		}
		return Persons;
	}

	public List<Multiset.Entry<String>> getEntriesSortedByCount() {
		Multiset<String> set = HashMultiset.create();
		for (CRPerson Person : personList) {
			set.add(Person.toString());
		}
		return CMineUtil.getEntryListSortedByCount(set);
	}
	public void addAll(CRPersonList Persons) {
		personList.addAll(Persons.personList);
		
	}

	public void addAll(List<CRPerson> personList) {
		this.personList.addAll(personList);
	}
	
}
