package org.contentmine.ami.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.DebugPrint;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.norma.sections.JATSSectionTagger;
import org.contentmine.norma.sections.JATSSectionTagger.SectionTag;

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
description = "analyzes bitmaps - generally binary, but may be oligochrome. Creates pixelIslands "
)

public class AMISectionTool extends AbstractAMITool {
	
	private static final Logger LOG = Logger.getLogger(AMISectionTool.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

    @Option(names = {"--sections"},
    		arity = "0..*",
    		required = true,
            description = "sections to extract (uses JATSSectionTagger) %n"
            		+ "if no args, lists tags%n"
            		+ "ALL selects all tags ")
    private List<SectionTag> sectionTagList = new ArrayList<>();

    @Option(names = {"--write"},
    		arity = "0",
            description = "write section files (may be customised later)")
	public boolean writeFiles = true;

	private JATSSectionTagger tagger;
		
	public AMISectionTool() {
		
	}

	public static void main(String[] args) {
    	new AMISectionTool().runCommands(args);
	}

    @Override
	protected void parseSpecifics() {
		normalizeSectionTags();
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
	}

	private void writeSectionComponents(boolean deleteExisting, SectionTag sectionTag) {
		List<Element> sectionList = tagger.getSections(sectionTag);
		if (writeFiles && sectionList.size() > 0) {
			File sectionDir = cTree.makeSectionDir(sectionTag.getName(), deleteExisting);
			for (int i = 0; i < sectionList.size(); i++) {
				Element section = sectionList.get(i);
				String title = createTitleForSection(section);
				try {
					File xmlFile = new File(sectionDir, ((title != null) ? title : "elem") + "_" + i + "." + CTree.XML);
					XMLUtil.debug(section, xmlFile, 1);
				} catch (IOException e) {
					System.err.println(">cannot write file> "+e.getMessage());
				}
			}
		}
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
