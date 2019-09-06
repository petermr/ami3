package org.contentmine.cproject.metadata;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CContainer;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.CTreeList;
import org.contentmine.cproject.metadata.AbstractMetadata.Type;
import org.contentmine.cproject.util.URLShuffler;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/** holds all the metadata for a CProject.
 * still evolving
 * probably subclassable for different types of metadata.
 * 
 * @author pm286
 *
 */
public class ProjectAnalyzer {
	
	private static final Logger LOG = Logger.getLogger(ProjectAnalyzer.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

 	private List<AbstractMetadata> metadataList;
	private CTreeList cTreeList;
	private CProject cProject;
	private String jsonType;
	private Multiset<String> allKeySet;
	private Type sourceType;
	private List<String> urlList;
	private boolean shuffleUrls;
	private boolean pseudoHost;
	private AbstractMetadata.Type type;

	public ProjectAnalyzer() {
		
	}
	
	public ProjectAnalyzer(CProject cProject) {
		this();
		this.cProject = cProject;
		cProject.setProjectAnalyzer(this);
	}
	
		/** 
	 * NOTE: metadata elements will be null if no metadata found
	 * 
	 * @return
	 */
	public List<AbstractMetadata> getOrCreateMetadataList() {
		if (metadataList == null) {
			cTreeList = cProject.getOrCreateCTreeList();
			metadataList = new ArrayList<AbstractMetadata>();
			for (CTree cTree : cTreeList) {
				AbstractMetadata metadata = AbstractMetadata.getCTreeMetadata(cTree, sourceType);
				metadataList.add(metadata);
			}
		}
		return metadataList;
	}

	public CContainer getCProject() {
		return cProject;
	}

	public List<AbstractMetadata> getMetadataList() {
		return metadataList;
	}

	public String getJsonType() {
		return jsonType;
	}

	public int size() {
		return metadataList.size();
	}
	public boolean contains(Object o) {
		return metadataList.contains(o);
	}
	public Iterator<AbstractMetadata> iterator() {
		return metadataList.iterator();
	}
	public boolean add(AbstractMetadata e) {
		return metadataList.add(e);
	}
	public boolean addAll(Collection<? extends AbstractMetadata> c) {
		return metadataList.addAll(c);
	}
	public AbstractMetadata get(int index) {
		return metadataList.get(index);
	}
	
	public Multiset<String> getOrCreateAllKeys() {
		if (allKeySet == null) {
			allKeySet = HashMultiset.create();
			for (AbstractMetadata metadata : metadataList) {
				if (metadata != null) {
					Set<String> keys = metadata.extractKeys();
					allKeySet.addAll(keys);
				}
			}
		}
		return allKeySet;
	}

	/** list may include nulls
	 * 
	 * @return
	 */
	public List<String> extractURLs() {
		urlList = new ArrayList<String>();
		getOrCreateMetadataList();
		for (AbstractMetadata metadata : metadataList) {
			if (metadata != null) {
				String url = metadata.getURL();
				urlList.add(url);
			}
		}
		if (shuffleUrls) {
			shuffleUrls();
		}
		return urlList;
	}

	public void shuffleUrls() {
		URLShuffler shuffler = new URLShuffler();
		shuffler.setPseudoHost(pseudoHost);
		shuffler.readURLs(urlList);
		urlList = shuffler.getShuffledUrls();
	}

	public void extractURLsToFile(File file) throws IOException {
		extractURLs();
		List<String> nonNullUrls = new ArrayList<String>();
		for (String url : urlList) {
			if (url != null) {
				nonNullUrls.add(url);
			}
		}
		FileUtils.writeLines(file, nonNullUrls, "\n");
	}

	public void setShuffleUrls(boolean shuffleUrls) {
		this.shuffleUrls = shuffleUrls;
	}
	
	public void setPseudoHost(boolean pseudoHost) {
		this.pseudoHost = pseudoHost;
	}

	public void setMetadataType(Type sourceType) {
		this.metadataList = null;
		this.sourceType = sourceType;
		this.jsonType = sourceType.getCProjectMDFilename();
	}
	

}
