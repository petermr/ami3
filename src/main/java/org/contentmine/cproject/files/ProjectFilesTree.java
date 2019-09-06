package org.contentmine.cproject.files;

import java.io.File;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.xml.XMLUtil;

import nu.xom.Attribute;
import nu.xom.Element;

/** Holds a CTreeFiles object as XML.
 * 
 * 
 * @author pm286
 *
 */
public class ProjectFilesTree extends Element {
	
	private static final Logger LOG = Logger.getLogger(ProjectFilesTree.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	private static final String C_TREE_FILES_TREE = "cTreeFilesTree";
	
	private CContainer cProject;

	private List<Element> childCTreeElements;
	
	public ProjectFilesTree(CContainer cProject) {
		super(C_TREE_FILES_TREE); 
		addProject(cProject);
	}

	private void addProject(CContainer cProject) {
		this.cProject = cProject;
		File directory = cProject == null ? null : cProject.getDirectory();
		if (directory != null) {
			this.addAttribute(new Attribute("project", directory.toString()));
		}
	}
	
	public void add(CTreeFiles cTreeFiles) {
		// dont add empty trees
		if (cTreeFiles.size() > 0) {
			this.appendChild(cTreeFiles);
		}
		childCTreeElements = null;
	}

	public int size() {
		ensureChildCTrees();
		return childCTreeElements.size();
	}
	
	public CTreeFiles get(int i) {
		ensureChildCTrees();
		return new CTreeFiles(childCTreeElements.get(i));
	}

	private void ensureChildCTrees() {
		if (childCTreeElements == null) {
			childCTreeElements = XMLUtil.getQueryElements(this, CTreeFiles.C_TREE_FILES);
		}
	}
	
	public String toString() {
		return this.toXML();
	}
}
