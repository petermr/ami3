package org.contentmine.ami.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.DebugPrint;
import org.contentmine.cproject.util.CMineUtil;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.eucl.euclid.util.CMFileUtil;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlA;
import org.contentmine.graphics.html.HtmlCaption;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlLabel;
import org.contentmine.graphics.html.HtmlTable;
import org.contentmine.graphics.html.HtmlTd;
import org.contentmine.graphics.html.HtmlTh;
import org.contentmine.graphics.html.HtmlThead;
import org.contentmine.graphics.html.HtmlTr;
import org.contentmine.graphics.html.util.HtmlUtil;
import org.contentmine.norma.NormaTransformer;
import org.contentmine.norma.sections.JATSArticleElement;
import org.contentmine.norma.sections.JATSBoldElement;
import org.contentmine.norma.sections.JATSCaptionElement;
import org.contentmine.norma.sections.JATSElement;
import org.contentmine.norma.sections.JATSFactory;
import org.contentmine.norma.sections.JATSFloatsGroupElement;
import org.contentmine.norma.sections.JATSLabelElement;
import org.contentmine.norma.sections.JATSPElement;
import org.contentmine.norma.sections.JATSSecElement;
import org.contentmine.norma.sections.JATSSectionTagger;
import org.contentmine.norma.sections.JATSSectionTagger.SectionTag;
import org.contentmine.norma.sections.JATSSectionTagger.SectionType;
import org.eclipse.jetty.util.log.Log;
import org.w3c.dom.Document;

import nu.xom.Attribute;
import nu.xom.Element;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/** analyses sections in XML/HTML
 * 
 * @author pm286
 *
 */
@Command(
name = "section",
description = {
		"Splits XML files into sections using XPath.",
		"Creates names from titles of sections (or 'elem<num>.xml' if cannot)%n"
				+ "optionally writes HTML (slow) using specified stylesheet%n"
				+ "examples:%n"
				+ "    --sections ALL --html nlm2html%n"
				+ "         //not sure this works"
				+ "    --sections ABSTRACT ACK_FUND --write false%n"
				+ "%n"
				+ "    --forcemake --extract table fig --summary figure table "
				+ "        // this seems to create sections OK, use this?"
})
public class AMISectionTool extends AbstractAMITool {
	
	private static final String SUMMARY_HTML = "summary.html";
	private static final String BOLD_FIRST_CHILD_OF_PARA_IN_SEC =
			".//*[local-name()='"+JATSSecElement.TAG+"' "
					+ "and *[local-name()='"+JATSPElement.TAG+"' "
							+ "and *[local-name()='"+JATSBoldElement.TAG+"' and position()=1]]]";
	private static final String FLOATS_GROUP_REGEX = ".*/\\d+_"+JATSFloatsGroupElement.TAG.replaceAll("\\-", "\\\\-");
	
	private static final String FIGURE_ = "figure_";
	private static final String FIGURES_DIR = "figures";
	private static final String FIGURE_FILE_REGEX = ".*/\\d+_fig(ure)?_+(\\d+)\\.xml";
	public static final String FIGURE_SUMMARY_DIRNAME = "__figures";

	/** probably not yet used */
	private static final String RESULTS = "results";
	private static final String RESULTS_DIR = "results";
	private static final String RESULTS_FILE_REGEX = ".*/results.xml";
	public static final String RESULTS_SUMMARY_DIRNAMR = "__results";

	private static final String SUPPLEMENTARY_ = "supplementary_";
	private static final String SUPPLEMENTARY_DIR = "supplementary";
	private static final String SUPPLEMENTARY_FILE_REGEX = ".*/(\\d+)_supp.*\\.xml";
	public static final String SUPPLEMENTARY_SUMMARY_DIRNAME = "__supplementary";

	private static final String TABLE_ = "table_";
	private static final String TABLES_DIR = "tables";
	private static final String TABLE_FILE_REGEX = ".*/\\d+_tab(le)?_+(\\d+)\\.xml";
	public static final String TABLE_SUMMARY_DIRNAME = "__tables";
	

	private static final Logger LOG = Logger.getLogger(AMISectionTool.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public enum SummaryType {
		figure(FIGURES_DIR, FIGURE_SUMMARY_DIRNAME),
		results(RESULTS_DIR, RESULTS_SUMMARY_DIRNAMR),
		supplementary(SUPPLEMENTARY_DIR, SUPPLEMENTARY_SUMMARY_DIRNAME),
		table(TABLES_DIR, TABLE_SUMMARY_DIRNAME),;
		private String path;
		private String summaryPath;
		private SummaryType(String path, String summaryPath) {
			this.path = path;
			this.summaryPath = summaryPath;
		}
		public String getPath() {
			return path;
		}
		public String getSummaryPath() {
			return summaryPath;
		}
	}
	
	private enum FloatType {
		fig("fig"),
		supplementary("supplementary-material"),
		table("table-wrap"),
		;
		private String path;
		private FloatType(String path) {
			this.path = path;
		}
		public String getPath() {
			return path;
		}
	}
	
    @Option(names = {"--boldsections"},
            description = "convert paras with bold first sentence/phrase into subsections.%n"
            		+ "e.g. <sec id='s2.1'><p><bold>Extraction of Oils.</bold>. more text...</p></sec>%n"
            		+ "=>  <sec id='s2.1'><sec id='s2.1.1'><title>Extraction of Oils.</title>. <p>more text...</p></sec>%n")
    private boolean makeBoldSections = false;
    
    @Option(names = {"--html"},
    		arity = "1",
            description = "convert sections to HTML using stylesheet (convention as in --transform)."
            		+ " recommend: nlm2html; if omitted defaults to no HTML"
            		+ " currently 201909 very slow since XSLT seems to be slow, "
            		+ " seems to be size related (references can take 1 sec)")
    private String xsltName = null;

    @Option(names = {"--extract"},
    		arity = "0..*",
            description = "extract float elements to subdirectory,"
            		+ "default table, fig, supplementary) ")
    private List<FloatType> extractList = new ArrayList<FloatType>(
    		Arrays.asList(new FloatType[]{FloatType.table, FloatType.fig, FloatType.supplementary}));

//    @Option(names = {"--figures"},
//    		arity = "0..*",
//            description = "extract float figure elements to subdirectory and make index; under development ")
//    private List<String> figureList = null;

    @Option(names = {"--sections"},
    		arity = "0..*",
//    		required = true,
            description = "sections to extract (uses JATSSectionTagger) %n"
            		+ "if none, lists Tagger tags%n"
            		+ "ALL selects all tags in Tagger%n"
            		+ "AUTO creates hierchical tree based on JATS and heuristics (default)%n,"
            		)
    private List<SectionTag> sectionTagList = 
                        new ArrayList<>(Arrays.asList(new SectionTag[]{SectionTag.AUTO}));

    @Option(names = {"--sectiontype"},
    		arity = "1",
            description = "Type of section (XML or HTML) default XML. Probably only used in development")
    private SectionType sectionType = SectionType.XML;

    @Option(names = {"--summary"},
    		arity = "1..*",
            description = "create summary files for sections")
    private List<SummaryType> summaryList = new ArrayList<>();

//    @Option(names = {"--tables"},
//    		arity = "0..*",
//            description = "extract float table elements to subdirectory and make index; under development ")
//    private List<String> tableList = null;
    
    @Option(names = {"--write"},
    		arity = "0",
            description = "write section files (may be customised later); ")
	public boolean writeFiles = true;

	private JATSSectionTagger tagger;
	SectionNumber sectionNumber;
	private NormaTransformer normaTransformer;
	private File floatsDir;
	private File sectionsDir;
	private File existingFulltextXML;
	private int maxrows = 10;
	private int currentSerial;
		
	public AMISectionTool() {
		
	}

	public static void main(String[] args) {
    	new AMISectionTool().runCommands(args);
	}

    @Override
	protected void parseSpecifics() {
		normalizeSectionTags();
		System.out.println("xslt                    " + xsltName);
		System.out.println("boldSections            " + makeBoldSections);
		System.out.println("extract                 " + extractList);
		System.out.println("sectionList             " + sectionTagList);
		System.out.println("sectiontype             " + sectionType);
		System.out.println("summaryList             " + summaryList);
		System.out.println("write                   " + writeFiles);
		System.out.println();
	}


    @Override
    protected void runSpecifics() {
		if (sectionTagList.size() == 0) {
			System.err.println("section values: "+Arrays.asList(SectionTag.values()));
		} else if (processTrees()) {
			writeSummaries();
    	} else {
			DebugPrint.debugPrint(Level.ERROR, "must give cProject or cTree");
	    }
    }

	private void writeSummaries() {
		if (cProject != null) {
			for (SummaryType summaryType : summaryList) {
				HtmlTable totalSummaryTable = createTotalSummary(summaryType);
				File summaryDir = new File(cProject.getDirectory(), summaryType.getSummaryPath());
				summaryDir.mkdirs();
				XMLUtil.writeQuietly(totalSummaryTable, new File(summaryDir, SUMMARY_HTML), 1);
			}
		}
	}

	public boolean processTree() {
		processedTree = true;
		sectionsDir = cTree.getSectionsDirectory();
		boolean debug = false;
		if (!CMFileUtil.shouldMake(getForceMake(), sectionsDir, debug, sectionsDir)) {
			if (debug) LOG.debug("skipped: "+sectionsDir);
			processedTree = false;
			return processedTree;
		}
		boolean deleteExisting = false;
		if (cTree == null || !cTree.hasExistingFulltextXML()) {
			System.err.println("no fulltext.xml");
		} else if (sectionTagList.size() == 1 && SectionTag.AUTO.equals(sectionTagList.get(0))) {
			createSections();
		} else {
			tagWithJATSTagger(deleteExisting); // oldStyle
		}
		return processedTree;
	}

	private void createSections() {
		createSectionsRecursively();
		floatsDir = getFloatsDir();
		if (extractList.contains(FloatType.fig)) {
			renameFloatsFilesAndCreateSummary(FIGURE_FILE_REGEX, FIGURES_DIR, FIGURE_);
		}
		if (extractList.contains(FloatType.supplementary)) {
			renameFloatsFilesAndCreateSummary(SUPPLEMENTARY_FILE_REGEX, SUPPLEMENTARY_DIR, SUPPLEMENTARY_);
		}
		if (extractList.contains(FloatType.table)) {
			renameFloatsFilesAndCreateSummary(TABLE_FILE_REGEX, TABLES_DIR, TABLE_);
		}
	}

	/** moves files from floats-group to their own directory 
	 * also renames to standard filenames
	 * 
	 * @param fileRegex
	 * @param outdirname
	 * @param filePrefix
	 */
	private void renameFloatsFilesAndCreateSummary(String fileRegex, String outdirname, String filePrefix) {
		List<File> files = Util.listFilesFromPaths(floatsDir, fileRegex);
		Collections.sort(files, new SectionComparator());
//		LOG.debug("FILES: "+files);
		Pattern pattern = Pattern.compile(fileRegex);
		if (files.size() > 0) {
			File newDirectory = new File(sectionsDir, outdirname);
			newDirectory.mkdirs();
			HtmlTable summaryTable = createSummaryTableStub();
			for (File file : files) {
				moveFilesCreateSummaryRowCreateHTML(filePrefix, pattern, newDirectory, file, summaryTable);
			}
			XMLUtil.writeQuietly(summaryTable, new File(newDirectory, SUMMARY_HTML), 1);
		}
	}
	
	/** transfers floats-group or other float to custom directory
	 * e.g. tables are relocated to ./sections/tables/table_d.xml
	 * then converted to HTML
	 * 
	 * @param filePrefix   e.g. figure_
	 * @param pattern eg.  .* /\d+_fig(ure)?_+(\d+)\.xml
	 * @param newDirectory e.g. PMC4391421/sections/figures/
	 * @param file  e.g. PMC4391421/sections/3_floats-group/1_fig__1.xml
	 * @param tbody of summary file
	 * @return renamed/moved file 
	 */
	private void moveFilesCreateSummaryRowCreateHTML(String filePrefix, Pattern pattern, File newDirectory, File file, HtmlTable summaryTable) {
		File destFile = moveFiles(filePrefix, pattern, newDirectory, file);
		HtmlTr tr = createSummaryRow(destFile);
		if (tr != null) {
			summaryTable.getOrCreateTbody().appendChild(tr);
		}
		HtmlElement htmlElement = createAndWriteHTML(destFile);
		if (htmlElement instanceof HtmlTable) {
			HtmlTable denormalizedheader = ((HtmlTable) htmlElement).getDenormalizedHeader();
			LOG.trace("DH "+denormalizedheader.toXML());
			File denormalizedFile = new File(destFile.toString().replace("."+CTree.XML, "."+"denorm"+"."+CTree.HTML));
			LOG.debug("wrote denorm: "+denormalizedFile);
			XMLUtil.writeQuietly(denormalizedheader, denormalizedFile, 1);
		}
	}

	private HtmlTable createSummaryTableStub() {
		HtmlTable summaryTable = new HtmlTable();
		HtmlCaption caption = new HtmlCaption(cTree.getName());
		summaryTable.appendChild(caption);
		HtmlTr tr = new HtmlTr();
		summaryTable.getOrCreateThead().appendChild(tr);
		tr.appendChild((HtmlTh) HtmlTh.createAndWrapText(JATSLabelElement.TAG));
		tr.appendChild((HtmlTh) HtmlTh.createAndWrapText(JATSCaptionElement.TAG));
		return summaryTable;
	}

	/** transfers floats-group or other float to custom directory
	 * e.g. tables are relocated to ./sections/tables/table_d.xml
	 * 
	 * @param filePrefix   e.g. figure_
	 * @param pattern eg.  .* /\d+_fig(ure)?_+(\d+)\.xml
	 * @param newDirectory e.g. PMC4391421/sections/figures/
	 * @param file  e.g. PMC4391421/sections/3_floats-group/1_fig__1.xml
	 * @return renamed/moved file 
	 */
	private File moveFiles(String filePrefix, Pattern pattern, File newDirectory, File file) {
		Matcher matcher = pattern.matcher(file.toString());
		if (!matcher.matches()) {
			throw new RuntimeException("cannot match file: "+matcher.matches()+" "+file+" ~~~ "+pattern);
		}
		currentSerial = new Integer(matcher.group(matcher.groupCount()));
		File destFile = new File(newDirectory, filePrefix + currentSerial + "." + CTree.XML);
		try {
			if (!destFile.exists()) {
				FileUtils.moveFile(file, destFile);
			}
			FileUtils.deleteQuietly(file);
		} catch (IOException e) {
			throw new RuntimeException("Cannot move file: ", e);
		}
		return destFile;
	}

	private HtmlTr createSummaryRow(File destFile) {
		HtmlElement htmlElement = HtmlElement.create(destFile);
		
		String tag = htmlElement.getLocalName();
		HtmlTr tr = createRowWithLabelAndCaption(htmlElement);
		// table may have a Thead OR may have a Tr/Th row
		if (HtmlTable.TAG.equals(tag)) {
			addTableColumnNames(htmlElement, tr);
		}
		return tr;
	}

	private HtmlElement createAndWriteHTML(File xmlFile) {
		Element element = XMLUtil.parseQuietlyToRootElement(xmlFile);
		Element newElement = new JATSFactory().create(element);
		HtmlElement htmlElement = null;
		if (newElement instanceof HtmlElement) {
			htmlElement = (HtmlElement) newElement;
		} else {
			JATSElement jatsElement = (JATSElement) new JATSFactory().create(element);
			htmlElement = ((JATSElement)jatsElement).createHTML();
			if (htmlElement == null) {
				LOG.error("Null JATS->HTML");
				return null;
			}
		}
		htmlElement.tidy();		
		
		String basename = FilenameUtils.getBaseName(xmlFile.toString());
		File htmlFile = new File(xmlFile.getParentFile(), basename+"."+CTree.HTML);
		htmlElement.setId(cTree.getName()+"/"+basename);
		XMLUtil.writeQuietly(htmlElement, htmlFile, 1);
		return htmlElement;
	}


	
	private HtmlTr createRowWithLabelAndCaption(HtmlElement htmlElement) {
		String label = XMLUtil.getSingleValue(htmlElement, "./*[local-name()='"+JATSLabelElement.TAG+"']");
		label = label == null ? "" : label.trim();
		String caption = XMLUtil.getSingleValue(htmlElement, "./*[local-name()='"+JATSCaptionElement.TAG+"']");
		caption = caption == null ? "" : caption.trim();
		HtmlTr tr = new HtmlTr();
		// a href="../PMC4391421/sections/tables/table_1.xml">
//		String href = "../"+cTree.getName()+"/sections/tables/"+"table_"+currentSerial+"."+CTree.XML;
		String href = "table_"+currentSerial+"."+CTree.XML;
		HtmlA a = new HtmlA();
		a.setHref(href);
		a.setContent(label);
		HtmlTd td = new HtmlTd();
		td.appendChild(a);
		tr.appendChild(td);
		String truncatedTitle = Util.truncateAndAddEllipsis(caption.replaceAll("\\s*\\n\\s*", " "), 60);
		tr.appendChild((HtmlTd) HtmlTd.createAndWrapText(truncatedTitle));
		return tr;
	}

	private void addTableColumnNames(HtmlElement htmlElement, HtmlTr tr) {
		HtmlTable table = (HtmlTable) XMLUtil.getSingleElement(htmlElement, "/*[local-name()='"+HtmlTable.TAG+"']");
		HtmlThead thead = table == null ? null : table.getThead();
		HtmlTr trth = (thead != null) ? thead.getOrCreateChildTr() : table.getSingleLeadingTrThChild();
		addThValuesToTr(trth, tr);
	}

	private void addThValuesToTr(HtmlTr trth, HtmlTr tr) {
		if (trth != null) {
			List<String> tdCellValues = trth.getThCellValues();
			for (int i = 0; i < Math.min(tdCellValues.size(), maxrows); i++) {
				String cellValue = tdCellValues.get(i);
				HtmlTd td = HtmlTd.createAndWrapText(cellValue);
				tr.appendChild(td);
			}
		}
	}

	private File getFloatsDir() {
		List<File> floatsDirList = new ArrayList<>();
		try {
			floatsDirList = Util.listFilesFromPaths(cTree.getSectionsDirectory(), FLOATS_GROUP_REGEX);
		} catch (Exception e) {
			System.err.println("ERROR, skipped: "+e.getMessage());
		}
		return (floatsDirList.size() == 1) ? floatsDirList.get(0) : null;
	}

	private void createSectionsRecursively() {
		JATSFactory factory = new JATSFactory();
		existingFulltextXML = cTree.getExistingFulltextXML();
		if (existingFulltextXML == null) {
			System.err.println("No fulltext.xml");
		} else {
			try {
				JATSArticleElement articleElement = factory.readArticle(existingFulltextXML);
				if (makeBoldSections) {
					makeBoldSections(articleElement);
				}
				articleElement.writeSections(cTree);
			} catch (Exception e) {
				System.err.println("Cannot read article, SKIPPING "+e.getMessage());
			}
		}
	}

	private void makeBoldSections(JATSArticleElement articleElement) {
		List<Element> secWithParaBoldList = XMLUtil.getQueryElements(
				articleElement, BOLD_FIRST_CHILD_OF_PARA_IN_SEC);
		for (Element element : secWithParaBoldList) {
			((JATSSecElement) element).makeBoldSections();
		}
	}

	private void tagWithJATSTagger(boolean deleteExisting) {
		tagger = new JATSSectionTagger();
		tagger.setSectionType(sectionType);
		cTree.setHtmlTagger(tagger);
		for (SectionTag sectionTag : sectionTagList) {
			if (sectionTag == null) {
				System.err.println("AMISectionTool null section tag");
			} else {
//				writeXMLSectionComponents(deleteExisting, sectionTag);
				writeSectionComponents(deleteExisting, sectionTag);
			}
		}
		// release tagger
		cTree.setHtmlTagger(null);
	}

	private void writeSectionComponents(boolean deleteExisting, SectionTag sectionTag) {
		List<Element> sectionList = tagger.getSections(sectionTag);
		System.err.println("section: "+sectionTag);
		sectionNumber = new SectionNumber();
		if (writeFiles && sectionList != null && sectionList.size() > 0) {
			LOG.debug(sectionTag+": "+sectionList.size());
			File sectionDir = cTree.makeSectionDir(sectionTag.getName(), deleteExisting);
			for (int serial = 0; serial < sectionList.size(); serial++) {
				Element section = sectionList.get(serial);
				LOG.debug("SECT "+section.toXML());
				writeSection(section, sectionDir);
				sectionNumber.incrementSerial();
			}
		}
	}

	private NormaTransformer getOrCreateNormaTransformer() {
		if (normaTransformer == null) {
			normaTransformer = new NormaTransformer();
		}
		return normaTransformer;
	}

	private void writeSection(Element section, File sectionDir) {
		normaTransformer = getOrCreateNormaTransformer();
		String title = createTitleForSection(section);
		File xmlFile = writeXML(sectionDir, section, title, CTree.XML);
		if (xsltName != null) {
			long millis = System.currentTimeMillis();
			createAndWriteHtml(sectionDir, title, xmlFile);
			long tt = System.currentTimeMillis() - millis;
			if (tt > 100) LOG.debug("sect "+ title+ " " +tt);
		}
	}

	private void createAndWriteHtml(File sectionDir, String title, File xmlFile) {
		try {
			Document xslDocument = normaTransformer.createW3CStylesheetDocument(xsltName);
			String sectionHtmlString = normaTransformer.transform(xslDocument, xmlFile);
			File htmlFile = createFileDescriptor(sectionDir, title, CTree.HTML);
			IOUtils.write(sectionHtmlString, new FileOutputStream(htmlFile), CMineUtil.UTF8_CHARSET);
		} catch (IOException ioe) {
			throw new RuntimeException("failed to convert/write XML to HTML");
		}
	}

	private File writeXML(File sectionDir, Element section, String title, String suffix) {
		File xmlFile = null;
		try {
			xmlFile = createFileDescriptor(sectionDir, title, suffix);
			XMLUtil.debug(section, xmlFile, 1);
		} catch (IOException e) {
			System.err.println(">cannot write file> "+e.getMessage());
		}
		return xmlFile;
	}

	private File createFileDescriptor(File sectionDir, String title, String suffix) {
		File xmlFile;
		String filename = ((title != null) ? title : "elem") + "_" + sectionNumber.toString() + "." + suffix;
		xmlFile = new File(sectionDir, filename);
		return xmlFile;
	}

	private void normalizeSectionTags() {
		if (sectionTagList.size() == 0) {
			List<JATSSectionTagger.SectionTag> tags = SectionTag.getAllTags();
			int i = 1;
			for (SectionTag tag : tags) {
				System.out.print("  "+tag);
				if (i++%8 == 0) System.out.println();
			}
			System.out.println();
		} else if (sectionTagList.size() == 1 && SectionTag.ALL.equals(sectionTagList.get(0))) {
			sectionTagList = SectionTag.getAllTags();
		}
	}

	private String createTitleForSection(Element section) {
		/** <div abstract-type="summary" class="abstract" xmlns="http://www.w3.org/1999/xhtml">
			    <div class="title">Author Summary</div> 
		*/
		String title = null;
		String xpath = SectionType.HTML.equals(sectionType) 
				? ".//*[local-name()='div' and @class='title']" 
				: ".//*[local-name()='title']";
		List<Element> titleElements = XMLUtil.getQueryElements(section, xpath);
		if (titleElements.size() == 0) {
//			System.err.println("no title in: "+section.toXML());
		} else if (titleElements.size() == 1) {
			Element titleElement = titleElements.get(0);
			title = createTitleFromElement(titleElement);
		} else {
			Element titleElement = titleElements.get(0);
			title = createTitleFromElement(titleElement);
			title += "__"+titleElements.size();
//			System.err.println(">multiple titles: "+section.toXML());
//			for (Element titleElementx : titleElements) {
//				System.err.println(">>"+titleElementx.getValue());
//			}
		}
		title = Util.makeLowercaseAndDespace(title, 15);
		return title;
	}

	private String createTitleFromElement(Element titleElement) {
		String title;
		title = titleElement.getValue();
		// slashes create false directories
		title = title == null ? null : title.replaceAll("/", "_");
		return title;
	}

	/** may lead to dirty fields */
	private JATSSectionTagger getOrCreateJATSTagger() {
		if (tagger == null) {
			tagger = new JATSSectionTagger();
		}
		return tagger;
	}

	private HtmlTable createTotalSummary(SummaryType summaryType) {
		HtmlTable totalSummaryTable = new HtmlTable();
		HtmlThead thead = totalSummaryTable.getOrCreateThead();
		HtmlTr theadrow = thead.getOrCreateChildTr();
		theadrow.appendChild(HtmlTh.createAndWrapText("Art-id"));
		theadrow.appendChild(HtmlTh.createAndWrapText(HtmlLabel.TAG));
		theadrow.appendChild(HtmlTh.createAndWrapText(HtmlCaption.TAG));
		for (CTree cTree : cProject.getOrCreateCTreeList()) {
			sectionsDir = cTree.getExistingSectionsDir();
			List<HtmlTr> rows = createSummaryRows(summaryType);
			for (HtmlTr row : rows) {
				totalSummaryTable.appendChild(row);
			}
		}
		return totalSummaryTable;
	}
	
	/** read existing summary file 
<table xmlns="http://www.w3.org/1999/xhtml">
<thead>
<caption>PMC3921638</caption>
<tr>
<th>label</th>
<th>caption</th>
</tr>
</thead>
<tbody>
<tr>
<td>
<a href="table_1.xml">Table 1</a>
</td>
<td>List of observed datasets with symbols used, along with the ... </td>
</tr>
<tr>
<td>
	 */

	private List<HtmlTr> createSummaryRows(SummaryType summaryType) {
		List<HtmlTr> rows = new ArrayList<>();
		File sectionSubDirectory = new File(sectionsDir, summaryType.getPath());
		File summaryFile = new File(sectionSubDirectory, SUMMARY_HTML);
		if (!summaryFile.exists()) {
			return rows;
		}
		HtmlElement htmlElement = HtmlElement.create(summaryFile);
		HtmlTable table = (HtmlTable) XMLUtil.getSingleElement(htmlElement, "//*[local-name()='"+HtmlTable.TAG+"']");
		if (table != null) {
			Element caption = XMLUtil.getSingleElement(table, ".//*[local-name()='"+HtmlCaption.TAG+"']");
			String captionValue = (caption == null) ? "" : caption.getValue();
			List<HtmlTr> trows = table.getRows();
			for (HtmlTr trow : trows) {
				HtmlTd td0 = trow.getTd(0);
				List<HtmlElement> aList = HtmlUtil.getQueryHtmlElements(td0, "./*[local-name()='"+HtmlA.TAG+"']");
				HtmlA a = aList.size() == 0 ? null : (HtmlA) aList.get(0);
				if (a == null) {
					LOG.debug("no AHREF");
					continue;
				}
				String href = a.getHref();
				String href1 = "../"+captionValue+"/"+sectionsDir.getName()+"/"+summaryType.getPath()+"/"+href;
				a.setHref(href1);
				HtmlTr row = (HtmlTr) HtmlElement.create(trow);
				
				row.insertChild(HtmlTd.createAndWrapText(captionValue), 0);
				rows.add(row);
			}
		}
		return rows;
	}

	private void runHelp() {
		DebugPrint.debugPrint("sections recognized in documents");
		for (SectionTag tag : JATSSectionTagger.SectionTag.values()) {
			DebugPrint.debugPrint(tag.name()+": "+tag.getDescription());
		}
	}

}

class SectionComparator implements Comparator<File> {
/** file is of form .../[1-9][0-9]*_name_[1-9][0-9]*.xml
 * 
 */
	@Override
	public int compare(File o1, File o2) {
		if (o1 == null ||  o2 == null) return 0;
		int serial1 = getFirstSerial(o1);
		int serial2 = getFirstSerial(o2);
		return serial1 - serial2;
	}

private int getFirstSerial(File o1) {
	String name1 = o1.getName();
	int serial = Integer.parseInt(name1.split("_")[0]);
	return serial;
}

}
