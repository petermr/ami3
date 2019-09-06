package org.contentmine.cproject.files.schema;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.html.HtmlA;
import org.contentmine.graphics.html.HtmlB;
import org.contentmine.graphics.html.HtmlBig;
import org.contentmine.graphics.html.HtmlBody;
import org.contentmine.graphics.html.HtmlBr;
import org.contentmine.graphics.html.HtmlCaption;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlEm;
import org.contentmine.graphics.html.HtmlFrame;
import org.contentmine.graphics.html.HtmlFrameset;
import org.contentmine.graphics.html.HtmlGeneric;
import org.contentmine.graphics.html.HtmlH1;
import org.contentmine.graphics.html.HtmlH2;
import org.contentmine.graphics.html.HtmlH3;
import org.contentmine.graphics.html.HtmlHead;
import org.contentmine.graphics.html.HtmlHr;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.html.HtmlI;
import org.contentmine.graphics.html.HtmlImg;
import org.contentmine.graphics.html.HtmlLi;
import org.contentmine.graphics.html.HtmlLink;
import org.contentmine.graphics.html.HtmlMeta;
import org.contentmine.graphics.html.HtmlOl;
import org.contentmine.graphics.html.HtmlP;
import org.contentmine.graphics.html.HtmlS;
import org.contentmine.graphics.html.HtmlScript;
import org.contentmine.graphics.html.HtmlSmall;
import org.contentmine.graphics.html.HtmlSpan;
import org.contentmine.graphics.html.HtmlStrong;
import org.contentmine.graphics.html.HtmlStyle;
import org.contentmine.graphics.html.HtmlSub;
import org.contentmine.graphics.html.HtmlSup;
import org.contentmine.graphics.html.HtmlTable;
import org.contentmine.graphics.html.HtmlTbody;
import org.contentmine.graphics.html.HtmlTd;
import org.contentmine.graphics.html.HtmlTfoot;
import org.contentmine.graphics.html.HtmlTh;
import org.contentmine.graphics.html.HtmlThead;
import org.contentmine.graphics.html.HtmlTr;
import org.contentmine.graphics.html.HtmlTt;
import org.contentmine.graphics.html.HtmlUl;

import nu.xom.Element;
import nu.xom.Node;

/** schema that constrains what is in CProject tree. 
 * 
 * @author pm286
 *
 */
public abstract class AbstractSchemaElement extends AbstractCMElement {
	private static final Logger LOG = Logger.getLogger(AbstractSchemaElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public enum IteratorType {
		NONDIRECTORY("file"),
		DIRECTORY("dir");
		
		private String typeName;
		private IteratorType(String typeName) {
			this.typeName = typeName;
		}
		public String getTypeName() {
			return typeName;
		}
	}

	public static final String C_PROJECT_TEMPLATE_XML = "cProjectTemplate.xml";
	public static final String C_TREE_TEMPLATE_XML = "cTreeTemplate.xml";
	public static final String COUNT = "count";
	public static final String NAME = "name";
	public static final String REGEX = "regex";
	public static final String SCHEMA = "schema";
	public static final String TYPE = "type";

	private List<File> directoryList;
	private List<File> fileList;
	private FilenameSets filenameSets;
	private FilenameSets dirnameSets;
	private Pattern pattern;

	/** constructor.
	 * 
	 * @param name
	 * @param namespace
	 */
	protected AbstractSchemaElement(String name) {
		super(name);
	}
	
	/** creates subclassed elements.
	 * 
	 * if an error is encountered and abort = false, outputs message and
	 * continues, else fails;
	 * 
	 * @param element
	 * @param abort 
	 * @param ignores namespaces
	 * @return
	 */
	public static AbstractSchemaElement create(Element element) {
		AbstractSchemaElement schemaElement = null;
		String tag = element.getLocalName();
		if (false) {
		} else if(CProjectSchema.TAG.equalsIgnoreCase(tag)) {
			schemaElement = new CProjectSchema();
		} else if(CTreeSchema.TAG.equalsIgnoreCase(tag)) {
			schemaElement = new CProjectSchema();
		} else if(DirSchema.TAG.equalsIgnoreCase(tag)) {
			schemaElement = new DirSchema();
		} else if(FileSchema.TAG.equalsIgnoreCase(tag)) {
			schemaElement = new FileSchema();
		} else if(PSchema.TAG.equalsIgnoreCase(tag)) {
			schemaElement = new PSchema();
		} else {
			String msg = "Unknown scheme tag "+tag;
			throw new RuntimeException(msg);
		}
		XMLUtil.copyAttributes(element, schemaElement);
		for (int i = 0; i < element.getChildCount(); i++) {
			Node child = element.getChild(i);
			if (child instanceof Element) {
				AbstractCMElement htmlChild = AbstractSchemaElement.create((Element)child);
				if (schemaElement != null) {	
					schemaElement.appendChild(htmlChild);
				}
			} else {
				if (schemaElement != null) {
					schemaElement.appendChild(child.copy());
				}
			}
		}
		return schemaElement;
		
	}

	public FilenameSets getFilenameSets() {
		if (filenameSets == null) {
			filenameSets = new FilenameSets(this, IteratorType.NONDIRECTORY);
		}
		return filenameSets;
	}

	public FilenameSets getDirnameSets() {
		if (dirnameSets == null) {
			dirnameSets = new FilenameSets(this, IteratorType.DIRECTORY);
		}
		return dirnameSets;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getLocalName()); 
		String type = getType();
		if (type != null) {sb.append(" type=" + type);}
		String name = getName();
		if (name != null) {sb.append(" name=" + name);}
		String regex = getRegex();
		if (regex != null) {sb.append(" regex=" + regex);}
		String schema = getSchema();
		if (schema != null) {sb.append(" schema=" + schema);}
		String count = getCount();
		if (count != null) {sb.append(" count=" + count);}
		return sb.toString();
	}

	public String getLabel() {
		return getName() != null ? getName() : getRegex();
	}
	
	public String getCount() {
		return this.getAttributeValue(COUNT);
	}

	public String getSchema() {
		return this.getAttributeValue(SCHEMA);
	}

	public String getRegex() {
		return this.getAttributeValue(REGEX);
	}

	public String getName() {
		return this.getAttributeValue(NAME);
	}

	public String getType() {
		return this.getAttributeValue(TYPE);
	}

	public Pattern getPattern() {
		if (pattern == null && getRegex() != null) {
			pattern = Pattern.compile(getRegex());
		}
		return pattern;
	}
}
