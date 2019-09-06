package org.contentmine.ami.plugins.regex;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/** contains the regexLists to use.
 * 
 * @author pm286
 *
 */
public class CompoundRegexList implements Iterable<CompoundRegex> {

	
	private static final Logger LOG = Logger.getLogger(CompoundRegexList.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private List<CompoundRegex> compoundRegexList;

	public CompoundRegexList() {
	}

	
	private void ensureCompoundRegexList() {
		if (compoundRegexList == null) {
			compoundRegexList = new ArrayList<CompoundRegex>();
		}
	}

	@Override
	public String toString() {
		StringBuilder sb =  new StringBuilder("CompoundRegexList:\n");
		for (CompoundRegex compoundRegex : compoundRegexList) {
			sb.append(compoundRegex.toString()+"\n");
		}
		return sb.toString();
	}

	public Iterator<CompoundRegex> iterator() {
		ensureCompoundRegexList();
		return compoundRegexList.iterator();
	}

	public void add(CompoundRegex compoundRegex) {
		ensureCompoundRegexList();
		compoundRegexList.add(compoundRegex);
	}

	public int size() {
		ensureCompoundRegexList();
		return compoundRegexList.size();
	}

}
