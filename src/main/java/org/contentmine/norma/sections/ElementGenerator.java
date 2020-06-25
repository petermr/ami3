package org.contentmine.norma.sections;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.contentmine.cproject.util.CMineUtil;
import org.contentmine.eucl.euclid.Util;

public class ElementGenerator {

	static String SRC_DIR = "/Users/pm286/workspace/cmdev/ami3/src/main/java/org/contentmine/norma/sections/";
	private String tag;

	public ElementGenerator() {
		
	}
	
	public void readElelement(String tag) {
		this.tag = tag;
	}

	public String createClass() {
		StringBuilder sb = new StringBuilder();
		sb.append(""
				+ "package org.contentmine.norma.sections;\n"
				+ "\n"
				+ "import org.apache.log4j.Level;\n"
				+ "import org.apache.logging.log4j.Logger;\n"
				+ "import org.apache.logging.log4j.LogManager;\n"
				+ "import nu.xom.Element;\n"
				+ "\n"
				+ "public class "+createClassName()+" extends JATSElement {\n"
				+ "    private static final Logger LOG = LogManager.getLogger("+createClassName()+".class);\n"
				+ "\n"
				+ "    public static String TAG = \"" + tag + "\";\n"
				+ "\n"
				+ "    public "+createClassName()+"(Element element) {\n"
				+ "        super(element);\n"
				+ "    }\n"
				+ ""
				+ "}\n"
				);
		return sb.toString();
	}

	public String createFactorySnippet() {
		StringBuilder sb = new StringBuilder();
		sb.append(""
				+ "        } else if("+createClassName()+".TAG.equals(tag)) {\n"
				+ "            sectionElement = new "+createClassName()+"(element);\n"
				+ "            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);\n"
				+ "\n"
				);
		return sb.toString();
	}


	private String createClassName() {
		return "JATS" + Util.hyphensToUpperCamelCase(tag) + "Element";
	}
	
	public void writeClasses(List<String> tagList) throws IOException {
		StringBuilder sb = new StringBuilder();
		for (String tag : tagList) {
			readElelement(tag);
			FileUtils.write(new File(SRC_DIR, createClassName()+".java"), createClass(), CMineUtil.UTF8_CHARSET);
			sb.append(createFactorySnippet());
		}
		File snippetsFile = new File(SRC_DIR, "Snippets.java");
		FileUtils.write(snippetsFile, sb.toString(), CMineUtil.UTF8_CHARSET);
	}
	
	public static void main(String[] args) throws IOException {
		ElementGenerator elementGenerator = new ElementGenerator();
		List<String> tagList = new ArrayList<>(Arrays.asList(new String[] {
"anonymous",
"author-comment",
"city",
"conf-sponsor",
"count",
"disp-formula-group",
"fax",
"free_to_read",
"inline-supplementary-material",
"issue-part",
"issue-sponsor",
"license_ref",
"mmultiscripts",
"mprescripts",
"none",
"on-behalf-of",
"open-access",
"part-title",
"preformat",
"related-object",
"roman",
"series-title",
"state",
"trans-abstract",
"trans-title-group",
"version",
/*
"app",
"app-group",
"award-group",
"bio",
"comment",
"custom-meta",
"custom-meta-group",
"date-in-citation",
"def",
"def-item",
"funding-group",
"funding-statement",
"inline-formula",
"institution-wrap",
"kwd-group",
"license",
"license-p",
"list-item",
"math",
"mfenced",
"mfrac",
"mixed-citation",
"mover",
"mrow",
"msqrt",
"mstyle",
"msub",
"msubsup",
"msup",
"mtable",
"mtd",
"mtr",
"munder",
"munderover",
"named-content",
"principal-award-recipient",
"sec-meta",
"string-name",
"supplement",
"tfoot",
"alt-text",
"attrib",
"award-id",
"break",
"chapter-title",
"collab",
"conf-date",
"conf-loc",
"conf-name",
"contrib-id",
"copyright-holder",
"country",
"degrees",
"edition",
"email",
"equation-count",
"ext-link",
"fig-count",
"funding-source",
"hr",
"inline-graphic",
"institution",
"institution-id",
"isbn",
"issn-l",
"issue-id",
"kwd",
"maligngroup",
"malignmark",
"meta-name",
"meta-value",
"mi",
"mn",
"mo",
"monospace",
"mroot",
"mspace",
"mtext",
"note",
"page-count",
"patent",
"phone",
"prefix",
"ref-count",
"sc",
"season",
"self-uri",
"series",
"size",
"strike",
"styled-content",
"suffix",
"table-count",
"term",
"trans-title",
"underline",
"uri",
"word-count",
"disp-formula"
*/
}));
		elementGenerator.writeClasses(tagList);
	}
}