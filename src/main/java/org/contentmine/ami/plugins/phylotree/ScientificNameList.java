package org.contentmine.ami.plugins.phylotree;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.contentmine.norma.NAConstants;

/** holds list of geneus and binomials (initially from NCBI)
 * 
 * @author pm286
 *
 */
public class ScientificNameList {

	private static final String SCIENTIFIC_NAME = "scientific name";
	private static final Pattern BINOMIAL = Pattern.compile("([A-Z][a-z]+\\s+[a-z\\-]+)\\s+.*");
	private static final Pattern GENUS = Pattern.compile("([A-Z][a-z]+)\\s+.*");
	private static final Pattern CLASS = Pattern.compile(".*(<[a-z][^>]*>).*");
	private final static File TAXDUMP = new File(NAConstants.MAIN_AMI_DIR+"/plugins/phylotree/taxdump/");

	public ScientificNameList() {
		
	}
	
	/**
|19	|	NBRC 103641	|		|	type material	|
|19	|	Pelobacter carbinolicus	|		|	scientific name	|
|19	|	Pelobacter carbinolicus Schink 1984	|		|	authority	|
|19	|	strain Gra Bd 1	|		|	type material	|
	 * @param file
	 * @throws IOException
	 */
	public void readTaxdump(File file) throws IOException {
		List<String> lines = FileUtils.readLines(file);
		Set<String> binomialSet = new HashSet<String>();
		Set<String> genusSet = new HashSet<String>();
		Set<String> roleSet = new HashSet<String>();
		Set<String> classSet = new HashSet<String>();
			for (String line : lines) {
			// remove tabs
			line = line.replaceAll("\\s+", " ");
			line = line.trim();
			// remove outer fenceposts
			line = line.substring(1,  line.length()-1);
			String[] parts = line.split("\\|");
			parts[1] = parts[1].trim();
			parts[2] = parts[2].trim();
			parts[3] = parts[3].trim();
			Matcher classMatcher = CLASS.matcher(parts[2]);
			if (classMatcher.matches()) {
				classSet.add(classMatcher.group(1));
			}
			roleSet.add(parts[3]);
			if (parts[1].contains("virus")) continue;
			parts[1] = parts[1]+" ";
			if (SCIENTIFIC_NAME.equals(parts[3])) {
				Matcher matcher = BINOMIAL.matcher(parts[1]);
				if (matcher.matches()) {
					binomialSet.add(matcher.group(1));
				} else {
					matcher = GENUS.matcher(parts[1]);
					if (matcher.matches()) {
						genusSet.add(matcher.group(1));
					}
				}
			}
		}
		writeSortedSet(new File(TAXDUMP, "binomial.txt"), binomialSet);
		writeSortedSet(new File(TAXDUMP, "genus.txt"), genusSet);
		writeSortedSet(new File(TAXDUMP, "class.txt"), classSet);
		writeSortedSet(new File(TAXDUMP, "role.txt"), roleSet);
	}

	private void writeSortedSet(File output, Set<String> set) throws IOException {
		List<String> names = new ArrayList<String>(set);
		Collections.sort(names);
		FileUtils.writeLines(output, names);
	}
	
	public static void main(String[] args) throws Exception {
		ScientificNameList names = new ScientificNameList();
		names.readTaxdump(new File(NAConstants.MAIN_AMI_DIR+"/plugins/phylotree/taxdump/names.dmp"));
	}
}
