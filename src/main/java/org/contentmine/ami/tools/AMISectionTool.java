package org.contentmine.ami.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.DebugPrint;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.eucl.euclid.util.CMFileUtil;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlTable;
import org.contentmine.graphics.html.HtmlTbody;
import org.contentmine.graphics.html.HtmlTd;
import org.contentmine.graphics.html.HtmlTh;
import org.contentmine.graphics.html.HtmlThead;
import org.contentmine.graphics.html.HtmlTr;
import org.contentmine.norma.NormaTransformer;
import org.contentmine.norma.sections.JATSArticleElement;
import org.contentmine.norma.sections.JATSCaptionElement;
import org.contentmine.norma.sections.JATSFactory;
import org.contentmine.norma.sections.JATSFloatsGroupElement;
import org.contentmine.norma.sections.JATSLabelElement;
import org.contentmine.norma.sections.JATSSectionTagger;
import org.contentmine.norma.sections.JATSSectionTagger.FloatSection;
import org.contentmine.norma.sections.JATSSectionTagger.SectionTag;
import org.contentmine.norma.sections.JATSSectionTagger.SectionType;
import org.w3c.dom.Document;

import nu.xom.Element;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/** analyses sections in XML/HTML
 * 
 * @author pm286
 *
 */
@Command(
name = "ami-section", 
aliases = "section",
version = "ami-section 0.1",
description = "splits XML files into sections using XPath%n"
		+ "creates names from titles of sections (or 'elem<num>.xml' if cannot)%n"
		+ "optionally writes HTML (slow) using specified stylesheet%n"
		+ "examples:%n"
		+ "    --sections ALL --html nlm2html%n"
		+ "    --sections ABSTRACT ACK_FUND --write false%n"
)

public class AMISectionTool extends AbstractAMITool {
	
	private static final String FLOATS_GROUP_REGEX = ".*/\\d+_"+JATSFloatsGroupElement.TAG.replaceAll("\\-", "\\\\-");
	private static final String FIGURE_ = "figure_";
	private static final String FIGURE_DIR = "figures/";
	private static final String FIGURE_FILE_REGEX = ".*/\\d+_fig(ure)?_+(\\d+)\\.xml";
	private static final String TABLE_ = "table_";
	private static final String TABLE_DIR = "tables/";
	private static final String TABLE_FILE_REGEX = ".*/\\d+_tab(le)?_+(\\d+)\\.xml";

	private static final Logger LOG = Logger.getLogger(AMISectionTool.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

    @Option(names = {"--html"},
    		arity = "1",
            description = "convert sections to HTML using stylesheet (convention as in --transform)."
            		+ " recommend: nlm2html; if omitted defaults to no HTML"
            		+ " currently 201909 very slow since XSLT seems to be slow, "
            		+ " seems to be size related (references can take 1 sec)")
    private String xsltName = null;

    @Option(names = {"--extract"},
    		arity = "1..*",
            description = "extract float elements to subdirectory) ")
    private List<FloatSection> extractList = new ArrayList<>();

    @Option(names = {"--figures"},
    		arity = "0..*",
            description = "extract float figure elements to subdirectory and make index; under development ")
    private List<String> figureList = null;

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

    @Option(names = {"--tables"},
    		arity = "0..*",
            description = "extract float table elements to subdirectory and make index; under development ")
    private List<String> tableList = null;
    
    @Option(names = {"--write"},
    		arity = "0",
            description = "write section files (may be customised later); ")
	public boolean writeFiles = true;

	private JATSSectionTagger tagger;
	SectionNumber sectionNumber;
	private NormaTransformer normaTransformer;
	private File floatsDir;
	private File sectionsDir;
		
	public AMISectionTool() {
		
	}

	public static void main(String[] args) {
    	new AMISectionTool().runCommands(args);
	}

    @Override
	protected void parseSpecifics() {
		normalizeSectionTags();
		System.out.println("xslt                    " + xsltName);
		System.out.println("extract                 " + extractList);
		System.out.println("figureList              " + figureList);
		System.out.println("sectionList             " + sectionTagList);
		System.out.println("sectiontype             " + sectionType);
		System.out.println("tableList               " + tableList);
		System.out.println("write                   " + writeFiles);
		System.out.println();
	}


    @Override
    protected void runSpecifics() {
		if (sectionTagList.size() == 0) {
			System.err.println("section values: "+Arrays.asList(SectionTag.values()));
		} else if (processTrees()) { 
    	} else {
			DebugPrint.debugPrint(Level.ERROR, "must give cProject or cTree");
	    }
    }

	public void processTree() {
		sectionsDir = cTree.getSectionsDirectory();
		boolean debug = false;
		if (!CMFileUtil.shouldMake(forceMake, sectionsDir, debug, sectionsDir)) {
			if (debug) LOG.debug("skipped: "+sectionsDir);
			return;
		}
//		LOG.debug(" "+cTree.getName());
		boolean deleteExisting = false;
		if (cTree == null || !cTree.hasExistingFulltextXML()) {
			System.err.println("no fulltext.xml");
		} else if (sectionTagList.size() == 1 && SectionTag.AUTO.equals(sectionTagList.get(0))) {
			createSections();
		} else {
			tagWithJATSTagger(deleteExisting); // oldStyle
		}
	}

	private void createSections() {
		createSectionsRecursively();
		floatsDir = getFloatsDir();
		if (figureList != null) {
			renameFloatsFiles(FIGURE_FILE_REGEX, FIGURE_DIR, FIGURE_);
		}
		if (tableList != null) {
			renameFloatsFiles(TABLE_FILE_REGEX, TABLE_DIR, TABLE_);
		}
	}

	/** moves files from floats-group to their own directory 
	 * also renames to standard filenames
	 * 
	 * @param fileRegex
	 * @param outdirname
	 * @param filePrefix
	 */
	private void renameFloatsFiles(String fileRegex, String outdirname, String filePrefix) {
		List<File> files = Util.listFilesFromPaths(floatsDir, fileRegex);
		Pattern pattern = Pattern.compile(fileRegex);
		if (files.size() > 0) {
			File newDirectory = new File(sectionsDir, outdirname);
			newDirectory.mkdirs();
			HtmlTable table = new HtmlTable();
			HtmlThead thead = table.getOrCreateThead();
			HtmlTr tr = new HtmlTr();
			thead.appendChild(tr);
			tr.appendChild((HtmlTh) HtmlTh.createAndWrapText(JATSLabelElement.TAG));
			tr.appendChild((HtmlTh) HtmlTh.createAndWrapText(JATSCaptionElement.TAG));
			HtmlTbody tbody = table.getOrCreateTbody();
			for (File file : files) {
				moveFilesAndCreateHTMLTable(filePrefix, pattern, newDirectory, file, tbody);
			}
//			System.out.println("ttt "+table.toXML());
			XMLUtil.writeQuietly(table, new File(newDirectory, "summary.html"), 1);
		}
	}

	private void moveFilesAndCreateHTMLTable(String filePrefix, Pattern pattern, File newDirectory, File file, HtmlTbody tbody) {
		Matcher matcher = pattern.matcher(file.toString());
		if (!matcher.matches()) {
			throw new RuntimeException("cannot match file: "+matcher.matches()+" "+file+" ~~~ "+pattern);
		}
		int serial = new Integer(matcher.group(2));
		File destFile = new File(newDirectory, filePrefix + serial + "." + CTree.XML);
		try {
			if (!destFile.exists()) {
				FileUtils.moveFile(file, destFile);
			}
			FileUtils.deleteQuietly(file);
		} catch (IOException e) {
			throw new RuntimeException("Cannot move file: ", e);
		}
		Element element = XMLUtil.parseQuietlyToRootElement(destFile);
		String label = XMLUtil.getSingleValue(element, "./*[local-name()='"+JATSLabelElement.TAG+"']");
		String caption = XMLUtil.getSingleValue(element, "./*[local-name()='"+JATSCaptionElement.TAG+"']");
		caption = caption == null ? "" : caption.trim();
		HtmlTr tr = new HtmlTr();
		tbody.appendChild(tr);
		tr.appendChild((HtmlTd) HtmlTd.createAndWrapText(label));
		tr.appendChild((HtmlTd) HtmlTd.createAndWrapText(Util.truncateAndAddEllipsis(caption.replaceAll("\\s*\\n\\s*", " "), 60)));
	}
	
//	private List<File> extractTables() {
//		return Util.listFilesFromPaths(floatsDir, TABLE_FILE_REGEX);
//	}
	
	private File getFloatsDir() {
		List<File> floatsDirList = Util.listFilesFromPaths(cTree.getSectionsDirectory(), FLOATS_GROUP_REGEX);
		return (floatsDirList.size() == 1) ? floatsDirList.get(0) : null;
	}

	private void createSectionsRecursively() {
		JATSFactory factory = new JATSFactory();
		File existingFulltextXML = cTree.getExistingFulltextXML();
		if (existingFulltextXML == null) {
			System.err.println("No fulltext.xml");
		} else {
			JATSArticleElement articleElement = factory.readArticle(existingFulltextXML);
			articleElement.writeSections(cTree);
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
			File sectionDir = cTree.makeSectionDir(sectionTag.getName(), deleteExisting);
			for (int serial = 0; serial < sectionList.size(); serial++) {
				Element section = sectionList.get(serial);
//				LOG.debug("SECT "+section.toXML());
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
			IOUtils.write(sectionHtmlString, new FileOutputStream(htmlFile), Charset.forName("UTF-8"));
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

	private void runHelp() {
		DebugPrint.debugPrint("sections recognized in documents");
		for (SectionTag tag : JATSSectionTagger.SectionTag.values()) {
			DebugPrint.debugPrint(tag.name()+": "+tag.getDescription());
		}
	}

	
}
