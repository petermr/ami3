package org.contentmine.ami.lookups;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.cproject.lookup.AbstractLookup;
import org.contentmine.eucl.sternLibrary.string.DamerauLevenshteinAlgorithm;
import org.contentmine.norma.NAConstants;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/** looks up species and genus against NCBI Taxdump.
 * 
 * @author pm286
 *
 */
public class TaxdumpLookup extends AbstractLookup {

	
	private static final Logger LOG = LogManager.getLogger(TaxdumpLookup.class);
public enum TaxonType {
		GENUS,
		BINOMIAL,
	}
	
	private final static File TAXDUMP_DIR = new File(NAConstants.MAIN_AMI_DIR + "/plugins/phylotree/taxdump");    
	private final static File GENUS_FILE = new File(TAXDUMP_DIR, "genus.txt");
	private final static File BINOMIAL_FILE = new File(TAXDUMP_DIR, "binomial.txt");
	public static final Set<String> GENUS = null;
	private Set<String> genusSet;
	private Set<String> binomialSet;
	private Multimap<String, String> speciesByGenusSet;
	
	public TaxdumpLookup() {
		setup();
	}
	
	private void setup() {
		LOG.trace("start setup");
		genusSet = readSet(GENUS_FILE);
		binomialSet = readSet(BINOMIAL_FILE);
		createSpeciesForGenusSet(binomialSet);
		LOG.trace("end setup");
	}

	private void createSpeciesForGenusSet(Set<String> binomialSet) {
		speciesByGenusSet = HashMultimap.create();
		for (String binomial : binomialSet) {
			String genus = binomial.split("\\s+")[0];
			String species = binomial.split("\\s+")[1];
			speciesByGenusSet.put(genus, species);
		}
	}

	private Set<String> readSet(File file) {
		List<String> lines = null;
		try {
			lines = FileUtils.readLines(file, NAConstants.UTF_8);
		} catch (IOException e) {
			throw new RuntimeException("Cannote read set from "+file, e);
		}
		if (lines == null || lines.size() == 0) {
			throw new RuntimeException("Empty file: "+file);
		}
		return new HashSet<String>(lines);
	}

	public boolean isValidGenus(String genus) {
		return genusSet.contains(genus);
	}

	public boolean isValidBinomial(String genus, String species)  {
		return binomialSet.contains(genus+" "+species);
	}

	public List<String> lookupSpeciesList(String genus) {
		LOG.trace("TAXDUMP "+genus);
		List<String> speciesList = new ArrayList<String>(speciesByGenusSet.get(genus));
		Collections.sort(speciesList);
		return speciesList;
	}

	@Override
	// no-op (for compatibility)
	public String lookup(String key) throws IOException {
		return null;
	}

	public List<String> getClosest(Collection<String> existingSet, String target, int maxDelta) {
		DamerauLevenshteinAlgorithm dl = new DamerauLevenshteinAlgorithm(1, 1, 1, 1);
		List<String> bestFits = new ArrayList<String>();
		int lowestD = maxDelta + 1;
		for (String existing : existingSet) {
			if (Math.abs(existing.length() - target.length()) <= maxDelta) {
				int d = dl.execute(existing, target);
				if (d <= lowestD) {
					if (d < lowestD) {
						bestFits = new ArrayList<String>();
						lowestD = d;
					}
					if (!bestFits.contains(existing)) {
						bestFits.add(existing);
					}
				}
			}
		}
		return bestFits;
	}

	public Set<String> getGenusSet() {
		return genusSet;
	}

	public Set<String> getBinomialSet() {
		return binomialSet;
	}

	public Multimap<String, String> getSpeciesByGenusSet() {
		return speciesByGenusSet;
	}

	public static String getBinomial(String genus, String species) {
		return genus+" "+species;
	}


		
}
