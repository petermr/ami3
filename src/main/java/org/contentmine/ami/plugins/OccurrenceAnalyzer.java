package org.contentmine.ami.plugins;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.util.CMineGlobber;
import org.contentmine.cproject.util.CMineUtil;
import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.Angle.Units;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.eucl.euclid.util.MultisetUtil;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.SVGText.RotateText;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

import nu.xom.Element;
import nu.xom.Node;

/** creates occurrence and coocurrence stats
 * 
 * @author pm286
 *
 */
// FIXME put this into CorpusCache

public class OccurrenceAnalyzer {
	private static final Logger LOG = Logger.getLogger(OccurrenceAnalyzer.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	/** mess.
	 * Species defaults to binomial
	 * @author pm286
	 *
	 */
	public enum OccurrenceType {
		BINOMIAL("binomial", "match"),
		STRING(null, "exact"), 
		GENE("gene", "exact"), 
		;

		public static String SPECIES = "species";
		private final String name;
		private final String matchMethod;
		
		private OccurrenceType(String name, String matchMethod) {
			this.name = name;
			this.matchMethod = matchMethod;
		}
		public String getName() {
			return name;
		}
		public String getMatchMethod() {
			return matchMethod;
		}
		@Override
		public String toString() {
			return "{type: " + name + " / " + matchMethod + "}";
		}
		/** get type by name.
		 * defaults to STRING;
		 * @param name
		 * @return
		 */
		public static OccurrenceType getTypeByName(String name) {
			for (OccurrenceType type : values()) {
				if (name.equals(type.name)) {
					return type;
				}
				if (name.equals(SPECIES)) return BINOMIAL;
			}
			return OccurrenceType.STRING;
		}
	}

	/** this is messy until we get a wider idea of what problems we are tackling.
	 * 
	 * @author pm286
	 *
	 */
	public enum SubType {
		HUMAN("human"),
		MOUSE("mouse"),
		DROSOPHILA("drosophila"),
		ARABIDOPSIS("arabidopsis"),
		;
		
		private final String name;
		private SubType(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		@Override
		public String toString() {
			return "{subtype: " + name +"}";
		}
	}

//	public final static String COOCCURRENCE = "cooccurrence";
	public static final String HISTOGRAM = "histogram";

	private List<Multiset.Entry<String>> resultsByImportance;
	private Map<String, Integer> serialByTermImportance;
	private Map<File, List<Multiset.Entry<String>>> entryListByFile;

	private String name;
	private OccurrenceType type = OccurrenceType.STRING;
	private SubType subType;
	private String resultsDirRegex;
	private int maxCount;
	private List<File> resultsFiles; // descendants of cTree
	private List<File> cTreeFiles;   // ancestor of results (?1:1 map)
	private EntityAnalyzer entityAnalyzer;
	
	public OccurrenceAnalyzer() {
		setDefaults();
	}
	
	private void setDefaults() {
		type = OccurrenceType.STRING;
		maxCount = 20;
	}

	public OccurrenceAnalyzer setResultsDirRegex(String resultsDirRegex) {
		this.resultsDirRegex = resultsDirRegex;
		return this;
	}

	public String getMatchMethod() {
		return type.getMatchMethod();
	}

	public String getName() {
		return OccurrenceType.STRING.equals(type) ? name : type.getName();
	}

	private List<Multiset.Entry<String>> getAllMatchedSpeciesInFileSortedByCount(File binomialFile) {
		Element element = XMLUtil.parseQuietlyToDocument(binomialFile).getRootElement();
		String method = getMatchMethod();
		String xpath = ".//*[local-name()='result']/@" + method;
		LOG.trace("xp "+xpath);
		List<Node> matches = XMLUtil.getQueryNodes(element, xpath); // match expands species
		LOG.trace(">>match: "+method+">"+matches);
		Multiset<String> multiSet = HashMultiset.create();
		for (Node match : matches) {
			multiSet.add(match.getValue());
		}
		List<Multiset.Entry<String>> entries = CMineUtil.getEntryListSortedByCount(multiSet);
		return entries;
	}

	public Map<File, List<Multiset.Entry<String>>> getOrCreateEntryListByCTreeFile() {
		if (entryListByFile == null) {
			entryListByFile = new HashMap<File, List<Multiset.Entry<String>>>();
			resultsFiles = getOrCreateResultsFiles();
			LOG.trace("entryList "+resultsFiles);
			for (File resultsFile : resultsFiles) {
				List<Multiset.Entry<String>> resultsEntries = getAllMatchedSpeciesInFileSortedByCount(resultsFile);
				LOG.trace("match "+resultsEntries);
				File cTreeFile = getCTreeFileAncestorFromResultsFile(resultsFile);
				entryListByFile.put(cTreeFile, resultsEntries);
			}
		}
		return entryListByFile;
	}

	public List<File> getOrCreateResultsFiles() {
		if (resultsFiles == null) {
			resultsFiles = new CMineGlobber().setRegex(resultsDirRegex).setLocation(getProjectDir()).listFiles();
		}
		return resultsFiles;
	}

	private File getProjectDir() {
		return entityAnalyzer == null ? null : entityAnalyzer.getProjectDir();
	}

	public List<Multiset.Entry<String>> getOrCreateEntriesSortedByImportance() {
		if (resultsByImportance == null) {
			getOrCreateEntryListByCTreeFile();
			Multiset<String> resultsMedianSet = getMedianMultiSet(entryListByFile);
			LOG.trace(">med> "+resultsMedianSet);
			resultsByImportance = CMineUtil.getEntryListSortedByCount(resultsMedianSet);
		}
		return resultsByImportance;
	}

	/** uses sqrt scaing for frequency.
	 * arbitrary
	 * 
	 * @param resultsSetListByFile
	 * @return
	 */
	private Multiset<String> getMedianMultiSet(Map<File, List<Entry<String>>> resultsSetListByFile) {
		LOG.trace("occ "+resultsSetListByFile);
		Multiset<String> resultsSet = HashMultiset.create();
		for (List<Entry<String>> entryList : resultsSetListByFile.values()) {
			for (Entry<String> entry : entryList) {
				int newCount = (int) Math.ceil(Math.sqrt((double)entry.getCount()));
				resultsSet.add(entry.getElement(), newCount);
			}
		}
		return  resultsSet;
	}

	public OccurrenceAnalyzer setMaxCount(int maxCount) {
		this.maxCount = maxCount;
		return this;
	}


	/** a simple lookup for ordering in matrixes, etc.
	 * 
	 * @param termsByImportance
	 * @return
	 */
	public Map<String, Integer> getOrCreateSerialByTermImportance() {
		if (serialByTermImportance == null) {
			serialByTermImportance = new HashMap<String, Integer>();
			if (resultsByImportance != null) {
				for (Integer serial = 0; serial < resultsByImportance.size(); serial++) {
					Entry<String> termEntry = resultsByImportance.get(serial);
					String term = termEntry.getElement();
					serialByTermImportance.put(term, serial);
				}
			}
		}
		return serialByTermImportance;
	}

	public List<File> getOrCreateCTreeFiles() {
		if (cTreeFiles == null) {
			cTreeFiles = new ArrayList<File>();
			getOrCreateResultsFiles();
			for (File resultsFile : resultsFiles) {
				File cTreeFile = getCTreeFileAncestorFromResultsFile(resultsFile);
				cTreeFiles.add(cTreeFile);
			}
		}
		return cTreeFiles;
	}

	private File getCTreeFileAncestorFromResultsFile(File resultsFile) {
		File cTreeFile = resultsFile.getParentFile().getParentFile().getParentFile().getParentFile();
		return cTreeFile;
	}

	public List<Integer> getSerialList(List<Entry<String>> termEntryList) {
		getOrCreateSerialByTermImportance();
		List<Integer> serialList = new ArrayList<Integer>();
		if (termEntryList != null) {
			for (Entry<String> termEntry : termEntryList) {
				if (termEntry != null) {
					Integer serial = serialByTermImportance.get(termEntry.getElement());
					if (serial != null) {
						serialList.add(serial);
					}
				}
			}
		}
		return serialList;
	}

	public int getMaxCount() {
		return maxCount;
	}

	public OccurrenceType getType() {
		return type;
	}

	public OccurrenceAnalyzer setType(OccurrenceType type) {
		this.type = type;
		return this;
	}

	public SubType getSubType() {
		return subType;
	}

	public OccurrenceAnalyzer setSubType(SubType type) {
		this.subType = type;
		return this;
	}

	public OccurrenceAnalyzer setEntityAnalyzer(EntityAnalyzer entityAnalyzer) {
		this.entityAnalyzer = entityAnalyzer;
		return this;
	}

	/** gets sorted entries */
	public List<Entry<String>> debug() {
		List<Entry<String>> cellsByImportance = this.getOrCreateEntriesSortedByImportance();
		String message = getFullName();
		LOG.trace("analyze: " + message + "\n" + cellsByImportance);
		return cellsByImportance;
	}

	public String getFullName() {
		String fullName = this.name;
		if (fullName == null) {
			fullName = type.name + (subType == null ? "" : "|"+subType.name);
		}
		return fullName;
	}

	public OccurrenceAnalyzer setName(String name) {
		this.name = name;
		return this;
	}

	/** writes to cProject directory: 
	 * 
	 * creates <fullName>.csv in format 
	 * name,count
	 * where fullName is generated from type and subType (getFullName())
	 * 
	 * @throws IOException 
	 * 
	 */
	public void writeCSV() throws IOException {
		getOrCreateEntriesSortedByImportance();
		File csvFile = createFileByType(CTree.CSV);
		MultisetUtil.writeCSV(csvFile, resultsByImportance, getName());
	}

	public File createFileByType(String suffix) {
		File cooccurrenceTop = entityAnalyzer.createCooccurenceTop();
		File typeTop = new File(cooccurrenceTop, OccurrenceType.STRING.equals(type) ? name : type.name);
		File dir = subType == null ? typeTop : new File(typeTop, subType.getName());
		return new File(dir, HISTOGRAM + "." + suffix);
	}

	/** currently no-op, have to write a simple histogram
	 * 
	 * @throws IOException
	 */
	public void writeSVG() throws IOException {
		File file = createFileByType(CTree.SVG);
		SVGElement svgElement = createHistogram(500, 250);
		SVGSVG.wrapAndWriteAsSVG(svgElement, file);
	}



	private SVGElement createHistogram(int width, int height) {
		int max = -1;
		double shrink = 0.9;
		double x0 = 10;
		for (Entry<String> entry : resultsByImportance) {
			max = Math.max(max, entry.getCount());
		}
		double barWidth = ((double) width) / (double) resultsByImportance.size(); 
		SVGG g = plotBars(resultsByImportance, width, height, max, shrink, x0, barWidth);
		
		return g;
	}

	private SVGG plotBars(List<Entry<String>> entriesSortedByImportance, int width, int height, int max, double shrink,
			double x0, double barWidth) {
		SVGG g = new SVGG();
		double y = 10.;
		for (int i = 0; i < entriesSortedByImportance.size(); i++) {
			Entry<String> entry = entriesSortedByImportance.get(i);
			int x = (int) ((double) i * barWidth + x0); 
			Real2 xy = new Real2(x, y);
			SVGRect rect = plotBar(height, max, shrink, barWidth, y, entry, x);
			g.appendChild(rect);

			double fontSize = 20.0;
			SVGText text = plotText(entry, xy, fontSize);
			g.appendChild(text);
			

//			text.rotateTextAboutPoint(xy, t2);
			
		}
		return g;
	}

	private SVGText plotText(Entry<String> entry, Real2 xy, double fontSize) {
		String name = entry.getElement();
		name = name.substring(0,  Math.min(15, name.length()));
		SVGText text = new SVGText(xy, name);
		text.setFontSize(fontSize);
		Transform2 t2 = Transform2.getRotationAboutPoint(new Angle(Math.PI / 2.,Units.RADIANS), xy);
		text.applyTransform(t2, RotateText.FALSE);
		return text;
	}

	private SVGRect plotBar(int height, int max, double shrink, double barWidth, double y, Entry<String> entry,
			int x) {
		int barHeight = (int) ( (double) (entry.getCount() * shrink * height)  / max); 
		SVGRect rect = new SVGRect(x, y - barHeight, barWidth, barHeight);
		rect.setOpacity(0.3);
		rect.setFill("cyan");
		return rect;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(type.toString()+" // "+name+" / "+resultsDirRegex+" / "+maxCount);
		return sb.toString();
	}
	
	/** number fo cTree Files
	 * 
	 * @return
	 */
	public int getSize() {
		return cTreeFiles == null ? 0 : cTreeFiles.size();
	}

	int getTermCount() {
		return getOrCreateSerialByTermImportance().size();
	}

	public List<Multiset.Entry<String>> getResultsByImportance() {
		return resultsByImportance;
	}

	/** only use this for debugging.
	 * 
	 * @param resultsByImportance
	 */
	public void setResultsByImportance(List<Multiset.Entry<String>> resultsByImportance) {
		this.resultsByImportance = resultsByImportance;
	}

	public void createFromStrings(String rowName, String rowMultisetString) {
		Multiset<String> rowMultiset = MultisetUtil.createMultiset(rowMultisetString);
		List<Multiset.Entry<String>> rowEntries = MultisetUtil.createListSortedByCount(rowMultiset);
		setResultsByImportance(rowEntries);
		setName(rowName);
	}
}
