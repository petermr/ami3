package org.contentmine.cproject.files;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/** wrapper for collection of ResultsElement.
 * 
 * @author pm286
 *
 */
public class ResultsElementList implements Iterable<ResultsElement> {

	private static final Logger LOG = Logger.getLogger(ResultsElementList.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	protected List<ResultsElement> resultsElementList;
	private List<String> titles;
	private Multiset<String> stringMultiset;
	
	public ResultsElementList() {
	}

	public void add(ResultsElement resultsElement) {
		ensureResultsElementList();
		resultsElementList.add(resultsElement);
	}

	protected void ensureResultsElementList() {
		if (resultsElementList == null) {
			resultsElementList = new ArrayList<ResultsElement>();
		}
	}

	public Iterator<ResultsElement> iterator() {
		ensureResultsElementList();
		return resultsElementList.iterator();
	}

	public int size() {
		ensureResultsElementList();
		return resultsElementList.size();
	}

	public ResultsElement get(int i) {
		ensureResultsElementList();
		return resultsElementList.get(i);
	}
	
	public List<ResultsElement> getResultsElementList() {
		ensureResultsElementList();
		return resultsElementList;
	}

	/** sorts list by title attribute
	 * 
	 */
	public void sortByTitle() {
		titles = getTitles();
		Collections.sort(titles);
		if (!checkTitles()) {
			LOG.warn("non-unique title list");
			return;
		}
		List<ResultsElement> newResultsElementList = new ArrayList<ResultsElement>();
		for (String title : titles) {
			for (ResultsElement resultsElement : resultsElementList) {
				String resultsTitle = resultsElement.getTitle();
				if (title.equals(resultsTitle)) {
					newResultsElementList.add(resultsElement);
					break;
				}
			}
		}
		if (newResultsElementList.size() != this.size()) {
			throw new RuntimeException("Some resultsElement/s do not have titles");
		} 
		this.resultsElementList = newResultsElementList;
	}

/** checks that sorted titles are all non-null/non-empty and there are no duplicates
 * 
 * @return
 */
	private boolean checkTitles() {
		boolean ok = true;
		for (int i = 0 ; i < titles.size(); i++) {
			String title = titles.get(i);
			if (title == null || title.trim().equals("")) {
				LOG.warn("Missing title on resultsList");
				ok = false;
				continue;
			}
			if (i > 0 && titles.get(i-1).equals(title)) {
				LOG.warn("Duplicate title on resultsList: "+title);
				ok = false;
			}
		}
		return ok;
	}

	private List<String> getTitles() {
		titles = new ArrayList<String>();
		if (resultsElementList != null) {
			for (ResultsElement resultsElement : resultsElementList) {
				titles.add(resultsElement.getTitle());
			}
		}
		return titles;
	}

	public void addToMultiset(ResultsElement summaryResultsElement) {
		ensureStringMultiset();
		List<ResultElement> resultElementList = summaryResultsElement.getOrCreateResultElementList();
		for (ResultElement resultElement : resultElementList) {
			LOG.trace(">>"+resultElement.toXML());
			Integer count = resultElement.getCount();
			count = (count == null) ? 1 : count;
			stringMultiset.add(resultElement.getValue(), count);
		}
	}

	private void ensureStringMultiset() {
		if (stringMultiset == null) {
			stringMultiset = HashMultiset.create();
		}
	}
	
	public Multiset<String> getMultisetSortedByCount() {
		return stringMultiset;
	}
	
}
