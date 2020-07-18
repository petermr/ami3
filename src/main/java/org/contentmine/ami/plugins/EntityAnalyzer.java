package org.contentmine.ami.plugins;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.plugins.OccurrenceAnalyzer.OccurrenceType;
import org.contentmine.ami.plugins.OccurrenceAnalyzer.SubType;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.norma.Norma;

/** analyzes chunks/caches for entities in context.
 * starting with documents and sentences
 * 
 * @author pm286
 *
 */
public class EntityAnalyzer {
	private static final String ANCESTORS0 = ".*";
//	private static final String ANCESTORS = ".*/";
	private static final String RESULTS_XML = "results\\.xml";
	private static final Logger LOG = LogManager.getLogger(EntityAnalyzer.class);
	private static final Object MOSQUITO = "mosquito";
	private static final String PLANT = "plant";
	
	private List<OccurrenceAnalyzer> occurrenceAnalyzerList; 
	private List<CooccurrenceAnalyzer> cooccurrenceAnalyzerList;
	private File projectDir; 
	private String code;
	private boolean forceRun = true;
	private boolean writeCsv = false;
	private boolean writeSVG = false;

	public EntityAnalyzer() {
		
	}

	/** creates {@link OccurrenceAnalyzer} with default type OccurrenceType.STRING. and no {@link SubType}
	 * use this for most dictionaries
	 * 
	 * @param name
	 * @return
	 */
	public OccurrenceAnalyzer createAndAddOccurrenceAnalyzer(String name) {
		// trim actual filenames
		name = FilenameUtils.getBaseName(name);
		OccurrenceType type = OccurrenceType.getTypeByName(name);
		OccurrenceAnalyzer occurrenceAnalyzer = this.createOccurrenceAnalyzer(type, name);
		occurrenceAnalyzer.setName(name);
		return occurrenceAnalyzer;
	}
	
	public OccurrenceAnalyzer createOccurrenceAnalyzer(OccurrenceType occurrenceType, String name) {
		String resultsXMLFileRegex = createResultsXMLFileRegex(occurrenceType, (SubType) null, name);
		return createOccurrenceAnalyzerAndAdd(occurrenceType, resultsXMLFileRegex);
	}

	/** create {@link OccurrenceAnalyzer} for types without subType or name
	 * Example is {@link OccurrenceAnalyzer}.BINOMIAL
	 * 
	 * @param type
	 * @return
	 */
	public OccurrenceAnalyzer createAndAddOccurrenceAnalyzer(OccurrenceType type) {
		return createOccurrenceAnalyzer(type, (SubType)null, null);
	}
	
	/** create {@link OccurrenceAnalyzer} for types with subType but no name
	 * Example is {@link OccurrenceAnalyzer}.GENE with {@link SubType}.HUMAN
	 * 
	 * @param type
	 * @return
	 */
	public OccurrenceAnalyzer createAndAddOccurrenceAnalyzer(OccurrenceType type, SubType subType) {
		String resultsXMLFileRegex = createResultsXMLFileRegex(type, subType, null);
		OccurrenceAnalyzer occurrenceAnalyzer = createOccurrenceAnalyzerAndAdd(type, resultsXMLFileRegex);
		occurrenceAnalyzer.setSubType(subType);
		return occurrenceAnalyzer;
	}
	
	/** creates {@link OccurrenceAnalyzer} with SubType and name 
	 * never called by user as either SubType or name is usually null
	 * 
	 * @param occurrenceType
	 * @param subType may be null
	 * @param name may be null
	 * @return
	 */
	private OccurrenceAnalyzer createOccurrenceAnalyzer(OccurrenceType occurrenceType, SubType subType, String name) {
		LOG.debug("OccAnn>"+name);
		String resultsXMLFile = createResultsXMLFileRegex(occurrenceType, subType, name);
		OccurrenceAnalyzer occurrenceAnalyzer = createOccurrenceAnalyzerAndAdd(occurrenceType, resultsXMLFile).setSubType(subType);
		return occurrenceAnalyzer;
	}

	/** create the actual {@link OccurrenceAnalyzer}
	 * all methods come through this
	 * 
	 * @param occurrenceType
	 * @param resultsXMLFileRegex
	 * @return
	 */
	private OccurrenceAnalyzer createOccurrenceAnalyzerAndAdd(OccurrenceType occurrenceType, String resultsXMLFileRegex) {
		OccurrenceAnalyzer occurrenceAnalyzer = new OccurrenceAnalyzer();
		occurrenceAnalyzer.setType(occurrenceType);
		occurrenceAnalyzer.setResultsDirRegex(resultsXMLFileRegex);
		occurrenceAnalyzer.setEntityAnalyzer(this);
		ensureOccurrenceAnalyzerLists();
		occurrenceAnalyzerList.add(occurrenceAnalyzer);
		return occurrenceAnalyzer;
	}

	/** create filename regex for xmlFile to be read.
	 * Everything is routed through this
	 * 
	 * @param type
	 * @param subType
	 * @param name
	 * @return
	 */
	private String createResultsXMLFileRegex(OccurrenceType type, SubType subType, String name) {
		String xmlFileRegex = null;
		if (OccurrenceType.STRING.equals(type)) {
			if (name == null) {
				throw new RuntimeException("name must not be null for STRING");
			}
			xmlFileRegex = createResultsXMLFileRegex(name, (String)null);
		} else if (subType != null) {
			xmlFileRegex = createResultsXMLFileRegex(type.getName(), subType.getName());
		} else {
			xmlFileRegex = createResultsXMLFileRegex(type.getName(), (String) null);
		}
		if (!File.separator.contentEquals("/") && xmlFileRegex.indexOf("/") != -1) {
			LOG.error("regex not normalised for non-/ syntax "+xmlFileRegex);
		}
		return xmlFileRegex;
	}

	private String createResultsXMLFileRegex(String code, String subCode) {
		// Horrible but I think necessary
		String separator = File.separator;
		// Windows needs escaping
		if (separator.contentEquals("\\")) {
			separator = "\\" + separator;
		}
		String s = ANCESTORS0 + separator;
		s += code + separator;
		if (subCode != null) {
			s += subCode + separator;
		}
		s += RESULTS_XML;
		return s;
	}

	private void ensureOccurrenceAnalyzerLists() {
		if (occurrenceAnalyzerList == null) {
			occurrenceAnalyzerList = new ArrayList<OccurrenceAnalyzer>();
		}
		if (cooccurrenceAnalyzerList == null) {
			cooccurrenceAnalyzerList = new ArrayList<CooccurrenceAnalyzer>();
		}
	}

	public EntityAnalyzer setProjectDir(File projectDir) {
		this.projectDir = projectDir;
		return this;
	}

	public File getProjectDir() {
		return projectDir;
	}

	public static EntityAnalyzer createEntityAnalyzer(File projectDir) {
		String fileroot = projectDir.getName();
		EntityAnalyzer entityAnalyzer = new EntityAnalyzer().setCode(fileroot).setProjectDir(projectDir);
		return entityAnalyzer;
	}

	public String getCode() {
		return code;
	}

	public EntityAnalyzer setCode(String code) {
		this.code = code;
		return this;
	}
	
	

	public CooccurrenceAnalyzer createCooccurrenceAnalyzer(OccurrenceAnalyzer rowAnalyzer,
			OccurrenceAnalyzer colAnalyzer) {
		CooccurrenceAnalyzer coocurrenceAnalyzer = new CooccurrenceAnalyzer(this).setRowAnalyzer(rowAnalyzer).setColAnalyzer(colAnalyzer);
		cooccurrenceAnalyzerList.add(coocurrenceAnalyzer);
		return coocurrenceAnalyzer;
	}

	public void createAllCooccurrences() {
		int deltax = 720;
		int deltay = 720;
		int size = occurrenceAnalyzerList.size();
		int xwindow = size * deltax;
		int ywindow = size * deltay;
		SVGSVG totalSvg = new SVGSVG();
		SVGG topG = new SVGG();
		totalSvg.appendChild(topG);
		double scale = 1.2 / size;
		topG.setTransform(Transform2.applyScale(scale));
		totalSvg.setWidth(xwindow * scale);
		totalSvg.setHeight(ywindow * scale);
		for (int irow = 0; irow < size; irow++) {
			int xoffset = irow * deltax;
			OccurrenceAnalyzer rowAnalyzer = occurrenceAnalyzerList.get(irow);
			if (writeCsv) {
				try {
					rowAnalyzer.writeCSV();
				} catch (IOException e) {
					throw new RuntimeException("Cannot write row: "+rowAnalyzer, e);
				}
			}
			if (writeSVG) {
				try {
					rowAnalyzer.writeSVG();
				} catch (IOException e) {
					throw new RuntimeException("Cannot write row: "+rowAnalyzer, e);
				}
			}
			// allow diagonal entries
			for (int jcol = irow; jcol < occurrenceAnalyzerList.size(); jcol++) {
				int yoffset = jcol * deltay;
				OccurrenceAnalyzer colAnalyzer = occurrenceAnalyzerList.get(jcol);
				CooccurrenceAnalyzer rowColCoocAnalyzer = createCooccurrenceAnalyzer(rowAnalyzer, colAnalyzer);
				rowColCoocAnalyzer.analyze();
				if (writeCsv) {
					try {
						rowColCoocAnalyzer.writeCSV();
					} catch (IOException e) {
						throw new RuntimeException("Cannot write CSV", e);
					}
				}
				if (writeSVG) {
					SVGG g = writeCooccurrenceSVG(xoffset, yoffset, rowColCoocAnalyzer);
					topG.appendChild(g);
				}
			}
			File outputFile = new File(this.createCooccurenceTop(), "allPlots.svg");
			try {
				XMLUtil.debug(totalSvg, outputFile, 1);
			} catch (IOException e) {
				throw new RuntimeException("Cannot write SVG", e);
			}
			
		}
							
	}

	private SVGG writeCooccurrenceSVG(int xoffset, int yoffset, CooccurrenceAnalyzer rowColCoocAnalyzer) {
		SVGG g = new SVGG();
		try {
			SVGSVG svg = rowColCoocAnalyzer.writeSVG();
			if (svg != null) {
				SVGSVG svgCopy = (SVGSVG) svg.copy();
				Real2 translation = new Real2(xoffset, yoffset);
				Transform2 t2 = Transform2.getTranslationTransform(translation);
	//			SVGG g = new SVGG();
				g.copyChildrenFrom(svgCopy);
				g.setTransform(t2);
			}
//			totalSvg.appendChild(g);
		} catch (IOException e) {
			throw new RuntimeException("Cannot write SVG", e);
		}
		return g;
	}

	/** writes CSV files for each occurrence analyzer.
	 * 
	 */
	public void writeCSVFiles() {
		for (int irow = 0; irow < occurrenceAnalyzerList.size(); irow++) {
			OccurrenceAnalyzer rowAnalyzer = occurrenceAnalyzerList.get(irow);
			try {
				rowAnalyzer.writeCSV();
			} catch (IOException e) {
				LOG.error("Cannot write: "+rowAnalyzer.getFullName());
			}
		}
	}

	public boolean analyzePlantCoocurrences() {
		if (forceRun ) {
			runNormaNLM();
		}
		String cmd = "word(frequencies)xpath:@count>20~w.stopwords:pmcstop.txt_stopwords.txt"
		+ " species(binomial)"
		+ " gene(human) "
		+ " search(auxin)"
		+ " search(plantDevelopment)"
		+ " search(pectin)"
		+ " search(plantparts)"
		+ " search(synbio)"
		
	
	    ;
		if (forceRun) {
			if (!runCommand(cmd)) return false;
		}
		
		createAndAddOccurrenceAnalyzer(OccurrenceType.BINOMIAL).setMaxCount(25);
		createAndAddOccurrenceAnalyzer(OccurrenceType.GENE, SubType.HUMAN).setMaxCount(30);
		createAndAddOccurrenceAnalyzer("auxin").setMaxCount(20);
		
		createAllCooccurrences();
		return true;
	}

	public boolean analyzeMosquitoCoocurrences() {
		if (forceRun ) {
			runNormaNLM();
		}
		String cmd = "word(frequencies)xpath:@count>20~w.stopwords:pmcstop.txt_stopwords.txt"
		+ " species(binomial)"
		+ " gene(human) "
		+ " search(disease)"
		+ " search(country)"
		+ " search(funders)"
		+ " search(inn)"
		+ " search(insecticide)"
	
	    ;
		if (forceRun) {
			if (!runCommand(cmd)) return false;
		}
		
		createAndAddOccurrenceAnalyzer(OccurrenceType.GENE, SubType.HUMAN).setMaxCount(30);
		createAndAddOccurrenceAnalyzer(OccurrenceType.BINOMIAL).setMaxCount(25);
		createAndAddOccurrenceAnalyzer("disease").setMaxCount(20);
		createAndAddOccurrenceAnalyzer("country").setMaxCount(20);
		createAndAddOccurrenceAnalyzer("funders").setMaxCount(20);
		createAndAddOccurrenceAnalyzer("inn").setMaxCount(20);
		createAndAddOccurrenceAnalyzer("insecticide").setMaxCount(20);
		
		createAllCooccurrences();
		return true;
	}

	private boolean runCommand(String cmd) {
		try {
			CommandProcessor.main((getProjectDir()+" "+cmd).split("\\s+"));
		} catch (IOException ioe) {
			LOG.error("cannot run command: "+ioe);
				return false;
		}
		return true;
	}

	public boolean analyzeCoocurrences(List<String> searchList) throws IOException {
		if (forceRun ) {
			runNormaNLM();
		}
		String cmd = createSearchStringAndCooccurrenceAnalyzers(searchList);
	
		if (forceRun) {
			if (!runCommand(cmd)) return false;
		}
		
		createAllCooccurrences();
		return true;
	}

	private String createSearchStringAndCooccurrenceAnalyzers(List<String> searchList) {
		String cmd = "";
		for (String search : searchList) {
			search = search.toLowerCase();
			if (search.equals("word")) {
				cmd += "word(frequencies)xpath:@count>20~w.stopwords:pmcstop.txt_stopwords.txt";
			} else if (search.equals("species")) {
				cmd += " species(binomial) ";
				createAndAddOccurrenceAnalyzer(OccurrenceType.BINOMIAL).setMaxCount(30);
			} else if (search.equals("gene")) {
				cmd += " gene(human) ";
				createAndAddOccurrenceAnalyzer(OccurrenceType.GENE, SubType.HUMAN).setMaxCount(30);
			} else {
				cmd += " search("+search+")";
				createAndAddOccurrenceAnalyzer(search).setMaxCount(20);
			}
		}
		LOG.debug("cmd:"+cmd);
		LOG.debug("cooccurrence analyzers: "+cooccurrenceAnalyzerList);
		return cmd;
	}


	private void runNormaNLM() {
		// check for html and xml
		boolean runNorma = true;
		runNorma = false;
		if (runNorma) {
			LOG.debug("NORMA");
			String args = "-i fulltext.xml -o scholarly.html --transform nlm2html --project " + getProjectDir();
			new Norma().run(args);
			LOG.debug("NORMAX");
		}
	}

	public boolean isForceRun() {
		return forceRun;
	}

	public EntityAnalyzer setForceRun(boolean forceRun) {
		this.forceRun = forceRun;
		return this;
	}

	public EntityAnalyzer setWriteCSV(boolean writeCsv) {
		this.writeCsv = writeCsv;
		return this;
	}

	public boolean isWriteCsv() {
		return writeCsv;
	}

	public EntityAnalyzer setWriteSVG(boolean writeSVG) {
		this.writeSVG = writeSVG;
		return this;
	}

	public boolean isWriteSVG() {
		return writeSVG;
	}

	public static void main(String[] args) {
		if (args.length == 0) {
			help();
			return;
		} 
		List<String> argList = new ArrayList<String>(Arrays.asList(args));
		File inputDir = makeInputFile(argList.get(0));
		if (inputDir == null) return;
		argList.remove(0);
		
		EntityAnalyzer entityAnalyzer = EntityAnalyzer.createEntityAnalyzer(inputDir);
//		entityAnalyzer.setWriteCSV(true);
		entityAnalyzer.setForceRun(true);

		if (argList.size() == 0) {
			entityAnalyzer.runDefaults();
		} else if (PLANT.equals(argList.get(0))) {
			entityAnalyzer.analyzePlantCoocurrences();
		} else if (MOSQUITO.equals(argList.get(0))) {
			entityAnalyzer.analyzeMosquitoCoocurrences();
		} else {
			LOG.warn("NYI "+argList);
		}
		
	}

	private void runDefaults() {
		LOG.error("runDefaults NYI");
	}

	File createCooccurenceTop() {
		File cooccurrenceTop = new File(getProjectDir(), CooccurrenceAnalyzer.COOCCURRENCE);
		return cooccurrenceTop;
	}

	private static File makeInputFile(String fileroot) {
		File inputDir = null;
		if (fileroot.startsWith("/")) {
			// absolute
			inputDir = new File(fileroot);
		} else {
			// relative to current directory
			inputDir = new File(".", fileroot);
		}
		if (!inputDir.exists() || !inputDir.isDirectory()) {
			try {
				LOG.error("File does not exist or is not a directory: "+inputDir.getCanonicalPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
			inputDir = null;
		}
		return inputDir;
	}

	private static void help() {
		LOG.error("ami-frequency cproject [args] (//is comments)");
		
		LOG.error("                            // default dictionaries [disease][country]");
		LOG.error("OR    plant                 // default dictionaries");
		LOG.error("OR  mosquito                // default dictionaries");
		LOG.error("OR  <list of dictionaries>  // from gene species country disease ...");
		LOG.error("");
		LOG.error("qualifiers (? means optional)");
		LOG.error(" --cooccur                  // optional coocurrence plots");
		LOG.error(" --csv csvfile              // write csv file");
		LOG.error(" --limit limit             // limit output or display to <limit> default 25");
		LOG.error("EXAMPLE: " + "ami-frequency //  searches for disease and country");
		LOG.error("EXAMPLE: " + "ami-frequency plant // searches for gene species auxin plantparts");
		LOG.error("EXAMPLE: " + "ami-frequency mosquito --limit 100 // searches by mosquito dicts and increases output");
		LOG.error("EXAMPLE: " + "ami-frequency disease poverty country  // uses 3 dictionaries");
		LOG.error("EXAMPLE: " + "ami-frequency disease poverty  --cooccur --csv foo/disease-poverty.csv  ");
	}
	
	
}
