package org.contentmine.graphics.svg.plot;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.JodaDate;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGPath;
import org.joda.time.DateTime;

/** a logger for SVG transformations and components.
 * May contain copies of actual elements
 *  
 * @author pm286
 *
 */
public class SVGLogger {
	private static final Logger LOG = Logger.getLogger(SVGLogger.class);
	
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private SVGG topG;
	private List<Pair<String, Object>> nvList;

	public SVGLogger() {
		init();
	}
	
	private void init() {
		this.topG = new SVGG();
		topG.setSVGClassName("extractedSVG");
		String dateString = JodaDate.formatDate(new DateTime());
		topG.setDate("creationDate", dateString);
	}

	public void write(String name, List<SVGPath> pathList) {
		ensureNVList();
		Pair<String, Object> nv = new MutablePair<String, Object>(name, (Integer) pathList.size());
		nvList.add(nv);
	}

	private void ensureNVList() {
		nvList = new ArrayList<Pair<String, Object>>();
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Pair nv : nvList) {
			sb.append(nv.getLeft()+" = "+nv.getRight()+"\n");
		}
		return sb.toString();
	}

}
