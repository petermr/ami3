package org.contentmine.ami.plugins.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.norma.NAConstants;
import org.junit.Before;
import org.junit.Test;

/** OCR using Tesseract
 * 
 * I think this was to support phylo
 * 
 *  will skip Tesseract if not installed
*/
public class TesseractTest {

	
	private static final Logger LOG = Logger.getLogger(TesseractTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private static final File TESSERACTED_DIR = new File(NAConstants.TEST_AMI_DIR+"/word/peterijsem/tesseracted/");
	private static final File TESS_CLEAN_DIR = new File("target/peterijsem/tessclean/");
//	private static final Pattern SPEC_STRAIN_ACCESS = Pattern.compile("([A-Z](?:\\.|[a-z]+)\\s*(.*)\\s*\\(([A-Z]{1,2}\\d{1,6})\\)\\s*");
	private static final Pattern SPEC_STRAIN_ACCESS = Pattern.compile("([A-Z](?:\\.|[a-z]+)\\s+[a-z]+)\\s*(.*)\\s+\\(([A-Z]{1,2}\\d{5,6})\\)\\s*");
	private List<List<String>> linesList;
	private List<File> txtFiles;
	

	@Before
	public void setup() throws IOException {
		if (linesList == null) {
			LOG.trace("setup files: TESSERACTED_DIR");
			if (!TESSERACTED_DIR.exists()) {
				throw new RuntimeException("File: "+TESSERACTED_DIR+" does not exist");
			}
			txtFiles = new ArrayList<File>(FileUtils.listFiles(TESSERACTED_DIR, new String[]{"txt"}, false));
			linesList = new ArrayList<List<String>>();
			for (File txtFile : txtFiles) {
				List<String> lines = FileUtils.readLines(txtFile, "iso-8859-1");
				lines = clean(lines);
				linesList.add(lines);
				String filename = FilenameUtils.getBaseName(txtFile.toString());
				File newFile = new File(TESS_CLEAN_DIR, filename+".txt");
				FileUtils.writeLines(newFile, lines);
			}
		}
	}

	private List<String> clean(List<String> lines) {
		List<String> newLines = new ArrayList<String>();
		for (String line : lines) {
			// remove leading numbers
			line = line.replaceAll("^[\\*\\-\\d\\.]+\\s*", "");
			// substitute slashes by "l"
			line = line.replaceAll("/", "l");
			line = line.trim();
			if (line.length() > 0) {
				newLines.add(line);
			}
		}
		return newLines;
	}
	
	@Test
	public void testWriteFiles() throws IOException {
		int i = 0;
		for (File txtFile : txtFiles) {
			List<String>lines = linesList.get(i++);
			String filename = FilenameUtils.getBaseName(txtFile.toString());
			File newFile = new File(TESS_CLEAN_DIR, filename+".txt");
			FileUtils.writeLines(newFile, lines);
		}
	}
	
	@Test
	public void testRegexes() {
		int i = 0;
		for (List<String> lines : linesList) {
			for (String line : lines) {
				Matcher matcher = SPEC_STRAIN_ACCESS.matcher(line);
				if (matcher.matches()) {
					LOG.trace(matcher.group(1)+"//"+matcher.group(2)+"//"+matcher.group(3));
				}
			}
		}
		
	}
}
