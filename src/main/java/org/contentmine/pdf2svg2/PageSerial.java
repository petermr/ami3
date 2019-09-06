package org.contentmine.pdf2svg2;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/** identifier for page and subcomponents
 * 
 * Computer scientists and modules count from 0
 * Libraries and bibliographers count from 1
 * 
 * This holds both schemes and allows access though named methods
 * 
 * Can hold both the page numbers and indexes within the page (subPage, e.g. repeated figures)
 * 
 * May also hold other ids (such as Figures, etc.)
 * generally there is a PageSerial for chunks of processed information - pages, images
 * 
 * 
 * */

public class PageSerial implements Comparable {
	private static final Logger LOG = Logger.getLogger(PageSerial.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private Integer zeroBasedPage;
	private Integer zeroBasedSubPage;

	private PageSerial() {
		
	}

	public Integer getZeroBasedPage() {
		return zeroBasedPage;
	}

	public Integer getOneBasedPage() {
		return zeroBasedPage + 1;
	}

	public void setZeroBasedPage(Integer zeroBasedPage) {
		this.zeroBasedPage = zeroBasedPage;
	}

	public Integer getZeroBasedSubPage() {
		return zeroBasedSubPage;
	}

	public Integer getOneBasedSubPage() {
		return zeroBasedSubPage + 1;
	}

	public void setZeroBasedSubPage(Integer subPage) {
		this.zeroBasedSubPage = subPage;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((zeroBasedPage == null) ? 0 : zeroBasedPage.hashCode());
		result = prime * result + ((zeroBasedSubPage == null) ? 0 : zeroBasedSubPage.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PageSerial other = (PageSerial) obj;
		if (zeroBasedPage == null) {
			if (other.zeroBasedPage != null)
				return false;
		} else if (!zeroBasedPage.equals(other.zeroBasedPage))
			return false;
		if (zeroBasedSubPage == null) {
			if (other.zeroBasedSubPage != null)
				return false;
		} else if (!zeroBasedSubPage.equals(other.zeroBasedSubPage))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PageSerial [page=" + zeroBasedPage + ", subPage=" + zeroBasedSubPage + "]";
	}

	/** 
	 *  this.serial smaller than o.serial, return -1
	 *  this.serial larger than o.serial,  return 1
	 *  this.serial equal to o.serial
	 *    if this.subSerial smaller than o.subSerial return -1
	 *    if this.subSerial larger  than o.subSerial return 1
	 *    if this.subSerial equals  to   o.subSerial return 0
	 *    if this.subSerial null and o.subSerial null return 0
	 */
	@Override
	public int compareTo(Object o) {
		if (o instanceof PageSerial) {
			PageSerial p = (PageSerial) o;
			if (this.zeroBasedPage < p.zeroBasedPage) return -1;
			if (this.zeroBasedPage > p.zeroBasedPage) return 1;
			if (this.zeroBasedSubPage == null && p.zeroBasedSubPage == null) return 0;
			if (this.zeroBasedSubPage == null && p.zeroBasedSubPage != null) return -1;
			if (this.zeroBasedSubPage != null && p.zeroBasedSubPage == null) return 1;
			return this.zeroBasedSubPage - p.zeroBasedSubPage;
		}
		return 0;
	}
	
	public String getZeroBasedSerialString() {
		String pageS = String.valueOf(zeroBasedPage);
		if (zeroBasedSubPage != null) {
			pageS += "."+zeroBasedSubPage;
		}
		return pageS;
	}

	public String getOneBasedSerialString() {
		String pageS = String.valueOf(getOneBasedPage());
		if (zeroBasedSubPage != null) {
			pageS += "."+getOneBasedSubPage();
		}
		return pageS;
	}

	public static PageSerial createFromZeroBasedPage(Integer iPage) {
		PageSerial pageSerial = new PageSerial();
		pageSerial.zeroBasedPage = iPage;
		return pageSerial;
	}
	
	public static PageSerial createFromZeroBasedPages(Integer iPage, Integer iSubPage) {
		PageSerial pageSerial = new PageSerial();
		pageSerial.zeroBasedPage = iPage;
		pageSerial.zeroBasedSubPage = iSubPage;
		return pageSerial;
	}


}
