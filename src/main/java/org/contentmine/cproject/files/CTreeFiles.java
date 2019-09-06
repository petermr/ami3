package org.contentmine.cproject.files;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.xml.XMLUtil;

import nu.xom.Attribute;
import nu.xom.Element;

/** files extracted from CTree by filepath search.
 * 
 * @author pm286
 *
 */
public class CTreeFiles extends Element implements Iterable<File>{



	private static final Logger LOG = Logger.getLogger(CTreeFiles.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public static final String C_TREE_FILES = "cTreeFiles";
	private static final String FILE = "file";
	private static final String NAME = "name";
	private static final String C_TREE = "cTree";
	
	private List<File> fileList;
	
	protected CTreeFiles() {
		super(C_TREE_FILES);
	}
	
	public CTreeFiles(CTree cTree) {
		this();
		ensureFileList();		
		addDirectoryAttribute(cTree);
	}

	private void addDirectoryAttribute(CTree cTree) {
		if (cTree != null) {
			File directory = cTree.getDirectory();
			if (directory != null) {
				this.addAttribute(new Attribute(C_TREE, directory.toString()));
			}
		}
	}

	public CTreeFiles(CTree cTree, List<File> elementList) {
		this(cTree);
		this.fileList = new ArrayList<File>(elementList);
		cleanAndAddChildren();
	}

	public CTreeFiles(Element element) {
		this();
		XMLUtil.copyAttributes(element, this);
		ensureFileList();
		List<Element> childElements = XMLUtil.getQueryElements(element, "*");
		for (Element childElement : childElements) {
			File file = new File(childElement.getAttributeValue(NAME));
			fileList.add(file);
			this.appendChild(childElement.copy());
		}
	}

	public Iterator<File> iterator() {
		ensureFileList();
		return fileList.iterator();
	}

	public int size() {
		ensureFileList();
		return this.fileList == null ? -1 : fileList.size();
	}

	public File get(int i) {
		ensureFileList();
		return i >= fileList.size() || i < 0 ? null : fileList.get(i);
	}
	
	public void sort() {
		ensureFileList();
		Collections.sort(fileList);
		cleanAndAddChildren();
	}

	private void cleanAndAddChildren() {
		// delete children
		Element junk = new Element("junk");
		XMLUtil.transferChildren(this, junk);
		for (File file : fileList) {
			this.appendChild(createElement(file));
		}
	}

	public String toString() {
		return this.toXML();
	}

	public void add(File file) {
		ensureFileList();
		fileList.add(file);
		this.appendChild(createElement(file));
	}

	private Element createElement(File file) {
		Element fileElement = new Element(FILE);
		fileElement.addAttribute(new Attribute(NAME, file.toString()));
		return fileElement;
	}

	private void ensureFileList() {
		if (fileList == null) {
			fileList = new ArrayList<File>();
		}
	}

	

}
