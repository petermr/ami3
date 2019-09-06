package org.contentmine.cproject.files;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.xml.XMLUtil;

import nu.xom.Element;

public class SnippetsTree extends Element {

	private static final Logger LOG = Logger.getLogger(SnippetsTree.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public static final String SNIPPETS_TREE = "snippetsTree";
	public static final Pattern FILE_PATTERN = Pattern.compile("(.*)/(.*)/results/(.*)/(.*)/results\\.xml");

	private List<Element> snippetsElementList;
	private List<XMLSnippets> snippetsList;
	private String topFilename;
	private String cTreeName;
	private String pluginName;
	private String optionName;
	
	public SnippetsTree() {
		super(SNIPPETS_TREE);
	}

	public static SnippetsTree createSnippetsTree(Element snippets) {
		SnippetsTree snippetsTree = null;
		if (snippets != null && snippets.getLocalName().equals(SnippetsTree.SNIPPETS_TREE)) {
			snippetsTree = new SnippetsTree();
			XMLUtil.copyAttributes(snippets, snippetsTree);
			List<Element> childElements = XMLUtil.getQueryElements(snippets, "*");
			for (Element childElement : childElements) {
				XMLSnippets xmlSnippets = XMLSnippets.createXMLSnippets(childElement);
				if (xmlSnippets == null) {
					LOG.debug("non-snippet child: "+childElement.getLocalName());
				} else {
					snippetsTree.appendChild(xmlSnippets);
				}
			}
		}
		return snippetsTree;
	}


	public Iterator<Element> iterator() {
		getOrCreateSnippetsChildren();
		return snippetsElementList.iterator();
	}

	private List<Element> getOrCreateSnippetsChildren() {
		if (snippetsElementList == null) {
			snippetsElementList = XMLUtil.getQueryElements(this, XMLSnippets.SNIPPETS);
		}
		return snippetsElementList;
	}

	public void add(XMLSnippets snippets) {
		this.appendChild(snippets.copy());
		snippetsElementList = null;
	}

	public int size() {
		return getOrCreateSnippetsChildren().size();
	}

	public XMLSnippets get(int i) {
		getOrCreateSnippetsChildren();
		Element snippets = (i >= snippetsElementList.size() || i < 0) ? null : snippetsElementList.get(i);
		return snippets == null ? null : XMLSnippets.createXMLSnippets(snippets);
	}

	public String toString() {
		return this.toXML();
	}

	/**
	 * Returns the value of the filename attribute of the first snippet in the snippetlist
	 * The separators of this are converted to unix format if written on windows
	 * will return null if the xml attribute called 'file' isn't set in the element
	 * 
	 * @return String | null
	 */
	public String getFilename() {
		List<XMLSnippets> snippetsList = getSnippetsList();
		return snippetsList.size() == 0 ? null : FilenameUtils.separatorsToUnix(snippetsList.get(0).getFilename());
	}

	public List<XMLSnippets> getSnippetsList() {
		if (snippetsList == null) {
			createSnippetsList();
		}
		return snippetsList;
	}
	
	@Deprecated
	public List<XMLSnippets> getOrCreateSnippetsList() {
		return getSnippetsList();
	}
	
	private void createSnippetsList() {
			getOrCreateSnippetsChildren();
			snippetsList = new ArrayList<XMLSnippets>();
			for (Element element : snippetsElementList) {
				XMLSnippets snippets = XMLSnippets.createXMLSnippets(element);
				LOG.trace("snippets:"+snippets);
				snippetsList.add(snippets);
			}
	}

	public String getCTreeName() {
		if (cTreeName == null) {
			getFilenameComponents();
		}
		return cTreeName;
	}

	public String getTopFilename() {
		if (topFilename == null) {
			getFilenameComponents();
		}
		return topFilename;
	}

	public String getPluginName() {
		if (pluginName == null) {
			getFilenameComponents();
		}
		return pluginName;
	}

	public String getOptionName() {
		if (optionName == null) {
			getFilenameComponents();
		}
		return optionName;
	}

	/**
	 * Sets attributes of the snippetsTree by parsing (using a regex)
	 * the 'file' attribute of the 1st XML snippet
	 */
	private void getFilenameComponents() {
		String filename = getFilename();
		if (filename != null) {
			Matcher matcher = FILE_PATTERN.matcher(filename);
			if (matcher.matches()) {
				topFilename = matcher.group(1);
				cTreeName = matcher.group(2);
				pluginName = matcher.group(3);
				optionName = matcher.group(4);
			}
		}
	}

	public PluginOption getPluginOption() {
		return new PluginOption(getPluginName(), getOptionName());
	}
}
