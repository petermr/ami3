package org.contentmine.ami.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.CharSet;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.DebugPrint;
import org.contentmine.eucl.euclid.util.CMFileUtil;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.norma.NormaTransformer;
import org.contentmine.norma.sections.JATSSectionTagger;
import org.contentmine.norma.sections.JATSSectionTagger.SectionTag;
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

    @Option(names = {"--sections"},
    		arity = "0..*",
    		required = true,
            description = "sections to extract (uses JATSSectionTagger) %n"
            		+ "if no args, lists tags%n"
            		+ "ALL selects all tags ")
    private List<SectionTag> sectionTagList = new ArrayList<>();

    @Option(names = {"--write"},
    		arity = "0",
            description = "write section files (may be customised later); ")
	public boolean writeFiles = true;

	private JATSSectionTagger tagger;

	private NormaTransformer normaTransformer;
		
	public AMISectionTool() {
		
	}

	public static void main(String[] args) {
    	new AMISectionTool().runCommands(args);
	}

    @Override
	protected void parseSpecifics() {
		normalizeSectionTags();
		System.out.println("xslt                    " + xsltName);
		System.out.println("sectionList             " + sectionTagList);
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
		File sectionsDir = cTree.getSectionsDirectory();
		boolean debug = false;
		if (!CMFileUtil.shouldMake(forceMake, sectionsDir, debug, sectionsDir)) {
			if (debug) LOG.debug("skipped: "+sectionsDir);
			return;
		}
		LOG.debug(" "+cTree.getName());
		boolean deleteExisting = false;
		tagger = new JATSSectionTagger();
		cTree.setHtmlTagger(tagger);
		for (SectionTag sectionTag : sectionTagList) {
			if (sectionTag == null) {
				System.err.println("AMISectionTool null section tag");
			} else {
				writeSectionComponents(deleteExisting, sectionTag);
			}
		}
		// release tagger
		cTree.setHtmlTagger(null);
	}

	private void writeSectionComponents(boolean deleteExisting, SectionTag sectionTag) {
		List<Element> sectionList = tagger.getSections(sectionTag);
//		System.err.println("section: "+sectionTag);
		if (writeFiles && sectionList.size() > 0) {
			File sectionDir = cTree.makeSectionDir(sectionTag.getName(), deleteExisting);
			for (int serial = 0; serial < sectionList.size(); serial++) {
				writeSection(sectionList, sectionDir, serial);
			}
		}
	}

	private NormaTransformer getOrCreateNormaTransformer() {
		if (normaTransformer == null) {
			normaTransformer = new NormaTransformer();
		}
		return normaTransformer;
	}

	private void writeSection(List<Element> sectionList, File sectionDir, int serial) {
		normaTransformer = getOrCreateNormaTransformer();
		Element section = sectionList.get(serial);
		String title = createTitleForSection(section);
		File xmlFile = writeXML(sectionDir, serial, section, title, CTree.XML);
		if (xsltName != null) {
			long millis = System.currentTimeMillis();
			createAndWriteHtml(sectionDir, serial, title, xmlFile);
			long tt = System.currentTimeMillis() - millis;
			if (tt > 100) LOG.debug("sect "+ title+ " " +tt);
		}
	}

	private void createAndWriteHtml(File sectionDir, int serial, String title, File xmlFile) {
		try {
			Document xslDocument = normaTransformer.createW3CStylesheetDocument(xsltName);
			String sectionHtmlString = normaTransformer.transform(xslDocument, xmlFile);
			File htmlFile = createFileDescriptor(sectionDir, serial, title, CTree.HTML);
			IOUtils.write(sectionHtmlString, new FileOutputStream(htmlFile), Charset.forName("UTF-8"));
		} catch (IOException ioe) {
			throw new RuntimeException("failed to convert/write XML to HTML");
		}
	}

	private File writeXML(File sectionDir, int serial, Element section, String title, String suffix) {
		File xmlFile = null;
		try {
			xmlFile = createFileDescriptor(sectionDir, serial, title, suffix);
			XMLUtil.debug(section, xmlFile, 1);
		} catch (IOException e) {
			System.err.println(">cannot write file> "+e.getMessage());
		}
		return xmlFile;
	}

	private File createFileDescriptor(File sectionDir, int serial, String title, String suffix) {
		File xmlFile;
		xmlFile = new File(sectionDir, ((title != null) ? title : "elem") + "_" + serial + "." + suffix);
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
		String title = XMLUtil.getSingleValue(section, ".//*[local-name()='div' and @class='title']");
		if (title != null) {
			title = title.toLowerCase().replaceAll("\\s+", "_");
			title = title.substring(0, Math.min(15, title.length()));
		}
		return title;
	}

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
