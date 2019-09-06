package org.contentmine.pdf2svg2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.IntRange;

/** manages in/exclusion of page numbers
 * 
 * @author pm286
 *
 */
public class PageIncluder {
    

	private static final Logger LOG = Logger.getLogger(PageIncluder.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private TreeSet<Integer> includeZeroBasedSortedPageNumbers;
	private TreeSet<Integer> excludeZeroBasedSortedPageNumbers;

	public PageIncluder() {
		
	}

	/** include page by number.
	 * 
	 * if (pages have been included) return true if in list else false
	 * else if (pages have been excluded) return false if in list else true
	 * else return true
	 * 
	 * @param iPage
	 * @return
	 */
	public boolean pageIsIncluded(PageSerial pageSerial) {
		boolean include = true;
		int iPage = pageSerial.getZeroBasedPage();

		if (includeZeroBasedSortedPageNumbers != null) {
			include = includeZeroBasedSortedPageNumbers.contains(iPage);
		} else if (excludeZeroBasedSortedPageNumbers != null) {
			include = !excludeZeroBasedSortedPageNumbers.contains(iPage);
		}
		if (!include) {
			LOG.trace("excluded page "+iPage);
		}
		return include;
	}

	public void addZeroNumberedIncludePages(List<Integer> includePages) {
		getOrCreateZeroNumberedIncludePageList().addAll(includePages);
	}

	public void addZeroNumberedIncludePages(IntRange includePages) {
		List<Integer> pages = includePages.createArray().getIntegerList();
		addZeroNumberedIncludePages(pages);
	}
	public void setZeroNumberedIncludePages(IntRange includePages) {
		this.includeZeroBasedSortedPageNumbers = null;
		addZeroNumberedIncludePages(includePages);
	}

	public void addZeroNumberedIncludePages(Integer ...includePages) {
		getOrCreateZeroNumberedIncludePageList().addAll(new ArrayList<Integer>(Arrays.asList(includePages)));
	}

	public TreeSet<Integer> getOrCreateZeroNumberedIncludePageList() {
		if (includeZeroBasedSortedPageNumbers == null) {
			this.includeZeroBasedSortedPageNumbers = new TreeSet<Integer>();
		}
		return includeZeroBasedSortedPageNumbers;
	}

	public void addZeroNumberedExcludePages(List<Integer> excludePages) {
		getOrCreateZeroBasedExcludePageList().addAll(excludePages);
	}

	public void addZeroNumberedExcludePages(IntRange excludePages) {
		List<Integer> pages = excludePages.createArray().getIntegerList();
	}

	public void addZeroNumberedExcludePages(Integer ... excludePages) {
		getOrCreateZeroBasedExcludePageList().addAll(new ArrayList<Integer>(Arrays.asList(excludePages)));
	}

	public TreeSet<Integer> getOrCreateZeroBasedExcludePageList() {
		if (excludeZeroBasedSortedPageNumbers == null) {
			this.excludeZeroBasedSortedPageNumbers = new TreeSet<Integer>();
		}
		return excludeZeroBasedSortedPageNumbers;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (includeZeroBasedSortedPageNumbers != null) {
			sb.append("include: " + includeZeroBasedSortedPageNumbers.toString()+"\n");
		} else if (excludeZeroBasedSortedPageNumbers != null) {
			sb.append("exclude: "+excludeZeroBasedSortedPageNumbers.toString()+"\n");
		} else {
			sb.append("include all");
		}
		return sb.toString();
		
	}

}
