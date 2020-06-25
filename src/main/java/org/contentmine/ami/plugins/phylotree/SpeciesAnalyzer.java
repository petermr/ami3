package org.contentmine.ami.plugins.phylotree;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.lookups.WikipediaLookup;
import org.contentmine.ami.plugins.phylotree.nexml.NexmlElement;
import org.contentmine.ami.plugins.phylotree.nexml.NexmlNEXML;
import org.contentmine.ami.plugins.phylotree.nexml.NexmlOtu;
import org.contentmine.ami.plugins.phylotree.nexml.NexmlOtus;
import org.contentmine.cproject.util.CMineUtil;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

/** extracts species from trees and analyzes the stats.
 * 
 * @author pm286
 *
 */
public class SpeciesAnalyzer {

	private static final Logger LOG = LogManager.getLogger(SpeciesAnalyzer.class);
private Multiset<String> binomialSet;
	private Multiset<String> genusSet;
	private Iterable<Entry<String>> genusByValues;
	private Iterable<Entry<String>> genusByCount;
	private Iterable<Entry<String>> speciesByValues;
	private List<Entry<String>> speciesListByValue;
	private Iterable<Entry<String>> speciesByCount;
	private Map<String, String> wikidataBySpecies;

	public SpeciesAnalyzer() {
		
	}
	
	public void analyzeTrees() throws IOException {
		genusSet = this.createGenusSetFromBinomialSet();
		genusByValues = CMineUtil.getEntriesSortedByValue(genusSet);
		genusByCount = CMineUtil.getEntriesSortedByCount(genusSet);
		speciesByValues = CMineUtil.getEntriesSortedByValue(binomialSet);
		speciesListByValue = Lists.newArrayList(speciesByValues);
		speciesByCount = CMineUtil.getEntriesSortedByCount(binomialSet);
//		File multipleFile = new File(CTreeLogAnalysisTest.IJSEM, "mutipleSpecies.txt");
//		File frequentFile = new File(CTreeLogAnalysisTest.IJSEM, "frequentSpecies.txt");
//		int minFrequency = 6;
//		int multiple = this.writeEntries(speciesByCount, multipleFile, frequentFile, minFrequency);
//		LOG.debug("all species: "+binomialSet.size()+"; of which "+multiple+" are multiple");
	}

	public void extractAndAddSpeciesFromDirectory(File dir) {
		ensureBinomialSet();
		List<File> nexmlFiles = new ArrayList<File>(FileUtils.listFiles(dir, new String[]{"nexml.xml"}, true));
		for (File nexmlFile : nexmlFiles) {
			LOG.trace(""+nexmlFile);
			NexmlNEXML nexml = null;
			try {
				nexml = (NexmlNEXML) NexmlElement.readAndCreateNEXML(nexmlFile);
				NexmlOtus nexmlOtus = nexml.getSingleOtusElement();
				List<NexmlOtu> otuList = nexmlOtus.getNexmlOtuList(); 
				for (NexmlOtu otu : otuList) {
					String binomial = otu.getBinomial();
					if (binomial != null) {
						binomialSet.add(binomial);
					}
				}
			} catch (Exception e) {
				LOG.error("could not read: "+nexmlFile+"; "+e);
			}
		}
	}

	private void ensureBinomialSet() {
		if (binomialSet == null) {
			binomialSet = HashMultiset.create();
		}
	}
	
	public void lookupWikidataSpeciesByValue() {
		WikipediaLookup lookup = new WikipediaLookup();
		for (Entry<String> entry : speciesListByValue) {
			String species = entry.getElement().replace("_", " ");
			String result = null;
			try {
				result = lookup.lookup(species);
			} catch (IOException e) {
				LOG.error("failed to lookup "+species+"; "+e);
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
	}

	public void lookupWikidataSpeciesByCount(File mapFile) {
		String NN = System.getProperty( "line.separator" );
		wikidataBySpecies = new HashMap<String, String>();
		WikipediaLookup lookup = new WikipediaLookup();
		for (Entry<String> entry : speciesByCount) {
			String species = entry.getElement().replace("_", " ");
			String result = null;
			try {
				result = lookup.lookup(species);
				LOG.debug(result);
				wikidataBySpecies.put(species, result);
				try
				{
				    FileWriter fw = new FileWriter(mapFile,true); //the true will append the new data
				    fw.write(species+"="+result+NN);//appends the string to the file
				    fw.close();
				} catch(IOException ioe) {
				    LOG.error("IOException: " + ioe.getMessage());
				}
			} catch (IOException e) {
				LOG.error("failed to lookup "+species+"; "+e);
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
	}
	
	public Map<String, String> getWikidataBySpecies() {
		return wikidataBySpecies;
	}

	public Multiset<String> createGenusSetFromBinomialSet() {
		genusSet = HashMultiset.create();
		for (Entry<String> entry : binomialSet.entrySet()) {
			String genus = entry.getElement().split("[_\\s]+")[0];
			genusSet.add(genus, entry.getCount());
		}
		return genusSet;
	}

	/** this is a mess.
	 * 
	 * @param entries
	 * @param multipleFile
	 * @param frequentFile
	 * @param minFrequency
	 * @return
	 * @throws IOException
	 */
	public int writeEntries(Iterable<Entry<String>> entries,
			File multipleFile, File frequentFile, int minFrequency)
			throws IOException {
		int multiple = 0;
		FileWriter multipleFileWriter = new FileWriter(multipleFile);
		FileWriter frequentFileWriter = new FileWriter(frequentFile);
		for (Entry<String> entry : entries) {
			String entry1 = entry.toString().replace("_",  " ");
			if (entry.getCount() > 1) {
				multipleFileWriter.write(entry1+"\n");
				if (entry.getCount() >= minFrequency) {
					frequentFileWriter.write(entry1+"\n");
				}
				multiple++;
			}
		}
		multipleFileWriter.close();
		frequentFileWriter.close();
		return multiple;
	}

	public static void writeEntries(Iterable<Entry<String>> species, File file)
			throws IOException {
		FileWriter fileWriter = new FileWriter(file);
		for (Entry<String> entry : species) {
			fileWriter.write(entry.toString().replace("_", " ")+"\n");
		}
		fileWriter.close();
	}

	public Multiset<String> getGenusSet() {
		return genusSet;
	}

	public Iterable<Entry<String>> getGenusByValues() {
		return genusByValues;
	}

	public Multiset<String> getBinomialSet() {
		return binomialSet;
	}

	public Iterable<Entry<String>> getGenusByCount() {
		return genusByCount;
	}

	public Iterable<Entry<String>> getSpeciesByValues() {
		return speciesByValues;
	}

	public List<Entry<String>> getEntryListByValue() {
		return speciesListByValue;
	}

	public void writeGenusByValues(File file) throws IOException {
		SpeciesAnalyzer.writeEntries(genusByValues, file);
	}
	
	public void writeGenusByCount(File file) throws IOException {
		SpeciesAnalyzer.writeEntries(genusByCount, file);
	}
	
	public void writeSpeciesByValues(File file) throws IOException {
		SpeciesAnalyzer.writeEntries(speciesByValues, file);
	}
	
	

}
