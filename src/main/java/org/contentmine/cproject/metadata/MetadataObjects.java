package org.contentmine.cproject.metadata;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.metadata.crossref.CRPerson;
import org.contentmine.cproject.util.CMineUtil;
import org.contentmine.cproject.util.RectangularTable;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/** accumulated objects from metadata searching.
 * 
 * @author pm286
 *
 */
public class MetadataObjects {

	private static final Logger LOG = Logger.getLogger(MetadataObjects.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private Set<String> stringKeys;
	private Set<String> stringListKeys;
	private Multiset<String> licenseMultiset;
	private Multiset<String> finalLicenseMultiset;
	private Multiset<String> prefixMultiset;
	private Multiset<String> publisherMultiset;
	private Multiset<String> keywordMultiset;
	private Multiset<String> finalKeywordMultiset;
	private Multiset<CRPerson> personMultiset;
	private RectangularTable csvTable;
	private AbstractMDAnalyzer metadataAnalyzer;

	public MetadataObjects() {
		init();
	}
	
	public MetadataObjects(MetadataObjects metadataObjects) {
		init();
		addObjects(metadataObjects);
	}

	private void addObjects(MetadataObjects metadataObjects) {
		this.stringKeys.addAll(metadataObjects.stringKeys);
		this.stringListKeys.addAll(metadataObjects.stringListKeys);
		this.addAll(licenseMultiset,metadataObjects.licenseMultiset);
		this.addAll(finalLicenseMultiset,metadataObjects.finalLicenseMultiset);
		this.addAll(prefixMultiset,metadataObjects.prefixMultiset);
		this.addAll(publisherMultiset,metadataObjects.publisherMultiset);
		this.addAll(keywordMultiset,metadataObjects.keywordMultiset);
		this.addAll(finalKeywordMultiset,metadataObjects.finalKeywordMultiset);
		this.addAllP(personMultiset,metadataObjects.personMultiset);
		if (this.csvTable != null && csvTable != null) {
			this.csvTable.addAll(csvTable);
		}
		this.metadataAnalyzer = metadataAnalyzer;
	}
	
	private boolean addAll(Multiset<String> to, Multiset<String> from) {
		if (to != null && from != null) {
			to.addAll(from);
			return true;
		}
		return false;
	}

	private boolean addAllP(Multiset<CRPerson> to, Multiset<CRPerson> from) {
		if (to != null && from != null) {
			to.addAll(from);
			return true;
		}
		return false;
	}

	public void addAll(MetadataObjects metadataObjects) {
		addObjects(metadataObjects);
	}

	public Set<String> getStringKeys() {
		return stringKeys;
	}

	public void setStringKeys(Set<String> stringKeys) {
		this.stringKeys = stringKeys;
	}

	public Set<String> getStringListKeys() {
		return stringListKeys;
	}

	public void setStringListKeys(Set<String> stringListKeys) {
		this.stringListKeys = stringListKeys;
	}

	public Multiset<String> getLicenseSet() {
		return licenseMultiset;
	}

	/** this is a kludge. Need to fix the JSON.
	 * 
	 * @return
	 */
	public Multiset<String> getFinalLicenseSet() {
		if (finalLicenseMultiset == null) {
			finalLicenseMultiset = HashMultiset.create();
			splitIntoFinalSet(licenseMultiset, finalLicenseMultiset, 4, 1);
		}
		return finalLicenseMultiset;
	}

	private void splitIntoFinalSet(Multiset<String> multiset, Multiset<String> finalMultiset, int start, int end) {
		for (Multiset.Entry<String> entry : multiset.entrySet()) {
			String value = entry.getElement();
			int count = entry.getCount();
			if (value.length() > start && value.length() > end) {
				value = value.substring(start,  value.length() - end); // remove brackets " : [...]"
			}
			String[] parts = value.split(", ");
			for (String part : parts) {
				finalMultiset.add(part, count);
			}
		}
	}

	public void setLicenseSet(Multiset<String> licenseSet) {
		this.licenseMultiset = licenseSet;
	}

	public Multiset<String> getPrefixSet() {
		return prefixMultiset;
	}

	public void setPrefixSet(Multiset<String> prefixSet) {
		this.prefixMultiset = prefixSet;
	}

	public Multiset<String> getPublisherMultiset() {
		return publisherMultiset;
	}

	public void setPublisherSet(Multiset<String> publisherSet) {
		this.publisherMultiset = publisherSet;
	}

	public Multiset<String> getKeywordSet() {
		return keywordMultiset;
	}

	public void setKeywordSet(Multiset<String> keywordSet) {
		this.keywordMultiset = keywordSet;
	}

	public Multiset<String> getFinalKeywordSet() {
		if (finalKeywordMultiset == null) {
			finalKeywordMultiset = HashMultiset.create();
			splitIntoFinalSet(keywordMultiset, finalKeywordMultiset, 2, 2);
		}
		return finalKeywordMultiset;
	}

	public Multiset<CRPerson> getPersonSet() {
		return personMultiset;
	}

	public void setPersonSet(Multiset<CRPerson> personSet) {
		this.personMultiset = personSet;
	}

	public AbstractMDAnalyzer getMetadataAnalyzer() {
		return metadataAnalyzer;
	}

	private void init() {
		stringKeys = new HashSet<String>();
		stringListKeys = new HashSet<String>();
		licenseMultiset = HashMultiset.create();
		prefixMultiset = HashMultiset.create();
		publisherMultiset = HashMultiset.create();
		keywordMultiset = HashMultiset.create();
		personMultiset = HashMultiset.create();
		this.csvTable = new RectangularTable();

	}

	public void setMetadataAnalyzer(AbstractMDAnalyzer metadataAnalyzer) {
		this.metadataAnalyzer = metadataAnalyzer;
		metadataAnalyzer.setMetadataObjects(this);
	}


	public RectangularTable getOrCreateCSVTable() {
		if (csvTable == null) {
			csvTable = new RectangularTable();
			addDefaults();
		}
		return csvTable;
	}

	private void addDefaults() {
		csvTable.setTruncate(60);
//		List<String> headers = AbstractMetadata.getDefaultHeaders();
//		csvTable.addRow(headers);
	}
	
	public void writeStringKeys(String filename) throws IOException {
		File file = new File(filename);
		List<String> keysList = Arrays.asList(getStringKeys().toArray(new String[0]));
		FileUtils.writeLines(file, keysList, "\n");
	}

	public void writeMultisetSortedByCount(Multiset<String> multiset, File file) throws IOException {
		writeMultisetSortedByCount(multiset, file.toString());
	}

	public void writeMultisetSortedByCount(Multiset<String> multiset, String filename) throws IOException {
		Iterable<Multiset.Entry<String>> entries = CMineUtil.getEntriesSortedByCount(multiset);
		List<String> lines = new ArrayList<String>();
		for (Multiset.Entry<String> entry : entries) {
			lines.add(entry.toString());
		}
		FileUtils.writeLines(new File(filename), lines, "\n");
	}

	public RectangularTable getOrCreateCSVTable(List<String> headers) {
		csvTable = getOrCreateCSVTable();
		csvTable.setHeader(headers);
		return csvTable;
	}

}
