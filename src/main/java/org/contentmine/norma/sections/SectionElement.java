package org.contentmine.norma.sections;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.CTreeList;
import org.contentmine.eucl.xml.XMLUtil;

import nu.xom.Attribute;
import nu.xom.Element;

/** analyses mulltiple directory trees
 * 
 * @author pm286
 *
 */
public class SectionElement extends Element {
	private static final Logger LOG = LogManager.getLogger(SectionElement.class);
private static final String COUNT = "count";
	private static final String ID = "id";
	private static final String NODE = "node";
	private static final String TITLE = "title";
	public static final String C_PROJECT = "cProject";
	
	public SectionElement() {
		super(NODE);
	}
	
	public SectionElement(String title) {
		this();
		setTitle(title);
	}
	
	public SectionElement(File file) {
		this();
		String title = file.getName().replaceAll("\\d+_", "");
		title = title.replaceAll("^_*", "");
		title = title.replaceAll("_*$", "");
//		System.out.println(">>"+title);
		this.setTitle(title);
	}

	public SectionElement(String title, File file) {
		this(file);
		setTitle(title);
	}
	
	public SectionElement setTitle(String title) {
		this.addAttribute(new Attribute(TITLE, title));
		return this;
	}
	
	public String getTitle() {
		return this.getAttributeValue(TITLE);
	}

	public SectionElement setId(String id) {
		this.addAttribute(new Attribute(ID, id));
		return this;
	}
	
	public String getId() {
		return this.getAttributeValue(ID);
	}
	
	public SectionElement incrementCount(int delta) {
		int i = getCount();
		this.addAttribute(new Attribute(COUNT, String.valueOf(i + delta)));
		return this;
	}
	
	public int getCount() {
		String count = this.getAttributeValue(COUNT);
		if (count == null) {
			count = "0";
			this.addAttribute(new Attribute(COUNT, count));
		}
		return Integer.parseInt(count);
	}
	
	public void addToTree(SectionElement element, File file) {
		this.appendChild(element);
		element.incrementCount(1);
		File[] childFiles = file.isDirectory() ? file.listFiles(
				new FilenameFilter() {

					@Override
					public boolean accept(File dir, String name) {
						return !(
							name.endsWith(".html") 
							|| name.endsWith(".svg") 
							|| name.endsWith(".xml")); 
					}
					
				})
				: null;
		if (childFiles != null) {
			for (File childFile : childFiles) {
				SectionElement childElement = new SectionElement(childFile);
				element.addToTree(childElement, childFile);
			}
		}
	}

	public void mergeDescendants() {
		List<? extends SectionElement> childElements = this.getChildSectionElements();
		
//		childElements.forEach(e -> System.out.println("t: "+e.getTitle()));
		for (int slow = childElements.size() - 1; slow > 0; slow--) {
			SectionElement slowElement = childElements.get(slow);
			String slowTitle = slowElement.getTitle();
			for (int fast = slow - 1; fast >= 0; fast--) {
				SectionElement fastElement = childElements.get(fast);
				String fastTitle = fastElement.getTitle();
				if (fastTitle.equals(slowTitle)) {
					fastElement.mergeSibling(slowElement);
					fastElement.incrementCount(slowElement.getCount());
					childElements.remove(slow);
					slowElement.detach();
					break;
				}
			}
		}
	}

	private void mergeSibling(SectionElement siblingElement) {
		List<? extends SectionElement> siblingChildElements = siblingElement.getChildSectionElements();
		for (SectionElement siblingChildElement : siblingChildElements) {
			siblingChildElement.detach();
			this.appendChild(siblingChildElement);
		}
		this.mergeDescendants();
		
	}

	private List<SectionElement> getChildSectionElements() {
		List<Element> childElements = XMLUtil.getQueryElements(this, "*");
		List<SectionElement> newList = new ArrayList<>();
		for (Element element : childElements) {
			newList.add((SectionElement) element);
		}
		return newList;
	}
	
	public void sortDescendantsByCount() {
		List<SectionElement> childElements = this.getChildSectionElements();
		Collections.sort(childElements, new CountComparator());
		
		for (int i = 0; i < childElements.size(); i++) {
			childElements.get(i).detach();
		}
		for (int i = 0; i < childElements.size(); i++) {
			this.appendChild(childElements.get(i));
		}
		childElements = this.getChildSectionElements();
		for (int i = 0; i < childElements.size(); i++) {
			childElements.get(i).sortDescendantsByCount();
		}

	}

	/** top entry point for creating hypertrees.
	 * 
	 */
	public static SectionElement createAndPopulateHypertree(CProject cProject) {
		CTreeList cTreeList = cProject.getOrCreateCTreeList();
		SectionElement hypertree = new SectionElement(C_PROJECT);
		for (CTree cTree : cTreeList) {
			hypertree.addToTree(new SectionElement(CTree.C_TREE), cTree.getDirectory());
			hypertree.mergeDescendants();
			hypertree.sortDescendantsByCount();
		}
		return hypertree;
	}
	
}
class CountComparator implements Comparator<SectionElement> {

	@Override
	public int compare(SectionElement o1, SectionElement o2) {
		int dd = o1.getCount() - o2.getCount();
		dd = -dd;
		if (dd == 0) {
			dd = o1.getTitle().compareTo(o2.getTitle());
		}
//		System.err.println(">"+dd);
		return dd;
	}
	
	
}
