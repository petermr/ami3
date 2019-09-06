package org.contentmine.cproject.files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.util.CMineUtil;
import org.contentmine.graphics.svg.cache.GenericAbstractList;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

/** list of CTree objects.
 * 
 * the list is sorted before use (might cause small performance hit...)
 * 
 * @author pm286
 *
 */
public class CTreeList extends GenericAbstractList<CTree> {
	
	public static final String FORBIDDEN_PREFIX = "__";
	private static final Logger LOG = Logger.getLogger(CTreeList.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private Map<String, CTree> cTreeByName;
	
	public CTreeList() {
		ensureCTreeList();
	}

	public CTreeList(List<CTree> cTrees) {
		ensureCTreeList();
		for (CTree cTree : cTrees) {
			add(cTree);
		}
		sort();
	}

	private void ensureCTreeList() {
		if (genericList == null) {
			genericList = new ArrayList<CTree>();
			cTreeByName = new HashMap<String, CTree>();
		}
	}

//	public int size() {
//		ensureCTreeList();
//		return list.size();
//	}

//	public Iterator<CTree> iterator() {
//		ensureCTreeList();
//		return cTreeList.iterator();
//	}
	
//	public CTree get(int i) {
//		ensureCTreeList();
//		return cTreeList.get(i);
//	}
	
	/** gets CTree by directory name
	 * 
	 * @param name
	 * @return null if not found 
	 */
	public CTree get(String name) {
		ensureCTreeList();
		return cTreeByName.get(name);
	}
	
	/** adds CTree and also updates cTreeByName.
	 * 
	 */
	public boolean add(CTree cTree) {
		ensureCTreeList();
		boolean added = false;
		if (isValidCTreename(cTree)) {
			added = genericList.add(cTree);
			ensureCTreeByName();
			cTreeByName.put(cTree.getDirectory().getName(), cTree);
		}
		return added;
	}
	
	private boolean isValidCTreename(CTree cTree) {
		String cTreename = cTree == null ? null : cTree.getName();
		return cTreename != null && !cTreename.startsWith(FORBIDDEN_PREFIX);
	}

	private void ensureCTreeByName() {
		if (cTreeByName == null) {
			cTreeByName = new HashMap<String, CTree>();
		}
	}

	public Set<CTree> asSet() {
		return new HashSet<CTree>(genericList);
	}
	
	public CTreeList not(CTreeList cTreeList) {
		Set<CTree> newSet = new HashSet<CTree>(this.asSet());
		newSet.removeAll(cTreeList.asSet());
		List<CTree> newList = new ArrayList<CTree>(newSet);
		CTreeList newCTreeList = new CTreeList(newList);
		return newCTreeList;
	}
	
	public CTreeList and(CTreeList cTreeList) {
		Set<CTree> newSet = new HashSet<CTree>(this.asSet());
		newSet.retainAll(cTreeList.asSet());
		List<CTree> newList = new ArrayList<CTree>(newSet);
		CTreeList newCTreeList = new CTreeList(newList);
		return newCTreeList;
	}
	
	public CTreeList or(CTreeList cTreeList) {
		Set<CTree> newSet = new HashSet<CTree>(this.asSet());
		newSet.addAll(cTreeList.asSet());
		List<CTree> newList = new ArrayList<CTree>(newSet);
		CTreeList newCTreeList = new CTreeList(newList);
		return newCTreeList;
	}

//	/** removes from list.
//	 * 
//	 * @param cTree
//	 * @return
//	 */
//	public boolean remove(CTree cTree) {
//		return cTreeList.remove(cTree);
//	}

	/** removes from list and deletes directory.
	 * 
	 * Cannot be undone
	 * 
	 * @param cTree
	 * @return
	 * @throws IOException 
	 */
	public boolean delete(CTree cTree) throws IOException {
		if (genericList.remove(cTree)) {
			FileUtils.deleteDirectory(cTree.getDirectory());
			return true;
		}
		return false;
	}
	
	public List<CTree> getCTreeList() {
		if (genericList != null) {
			Collections.sort(genericList);
		}
		return genericList;
	}

	/** directories in CTrees
	 *  
	 * may include nulls
	 * 
	 * @return
	 */
	public List<File> getCTreeDirectoryList() {
		List<File> directoryList = new ArrayList<File>();
		if (genericList != null) {
			Collections.sort(genericList);
			for (CTree cTree : genericList) {
				File directory = cTree.getDirectory();
				directoryList.add(directory);
			}
		}
		return directoryList;
	}

	public CTreeList sort() {
		if (genericList != null) {
			Collections.sort(genericList);
		}
		return this;
	}

	/** does this cTreeList contain a directory of the same name as cTree2
	 * 
	 * @param cTree2
	 * @return
	 */
	public boolean containsName(CTree cTree2) {
		String name2 = cTree2 == null ? null : cTree2.getDirectory().getName();
		return this.containsName(name2);
	}

	/** does this cTreeList contain a directory of the same name as name
	 * 
	 * @param cTree2
	 * @return
	 */
	public boolean containsName(String name) {
		for (CTree cTree : genericList) {
			if (cTree.getDirectory().getName().equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	/** does this cTreeList contain a directory of the same name as name
	 * 
	 * @param cTree2
	 * @return
	 */
	public int indexOf(String name) {
		for (int i = 0; i < genericList.size(); i++) {
			CTree cTree  = genericList.get(i);
			if (cTree.getDirectory().getName().equals(name)) {
				return i;
			}
		}
		return -1;
	}
	
	public static List<CTreeList> getCTreeListsSortedByCount(Multimap<String, CTree> map) {
		List<Multiset.Entry<String>> sortedKeys = CMineUtil.getObjectKeysSortedByCount(map);
		List<CTreeList> listList = new ArrayList<CTreeList>();
		for (Multiset.Entry<String> key : sortedKeys) {
			CTreeList list = new CTreeList(new ArrayList<CTree>(map.get(key.getElement())));
			listList.add(list);
		}
		return listList;
	}

	@Override
	public String toString() {
		return getCTreeDirectoryList().toString();
	}

	/** extracts all fulltext.html files.
	 * 
	 * @return
	 */
	public List<File> getFulltextHtmlFiles() {
		List<File> htmlFiles = new ArrayList<File>();
		for (CTree cTree : this) {
			File fullTextHtml = cTree.getExistingFulltextHTML();
			if (fullTextHtml != null) htmlFiles.add(fullTextHtml);
		}
		return htmlFiles;
	}
	
	/** extracts all fulltext.html files.
	 * 
	 * @return
	 */
	public List<File> getFulltextPDFFiles() {
		List<File> pdfFiles = new ArrayList<File>();
		for (CTree cTree : this) {
			File fulltextPDF = cTree.getExistingFulltextPDF();
			if (fulltextPDF != null) pdfFiles.add(fulltextPDF);
		}
		return pdfFiles;
	}
}
