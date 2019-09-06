package org.contentmine.cproject.metadata;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.CMineFixtures;
import org.contentmine.cproject.util.RectangularTable;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Multiset;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

public class ScraperTest {
	
	public static final Logger LOG = Logger.getLogger(ScraperTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}


	/** EXTRACT SCRAPER KEYS FROM SCRAPER DIRECTORIES.
	 * 
	 * THIS SHOWS THE SCRAPER TERMS THAT AUTHORS USE
	 * 
	 * @throws JsonSyntaxException
	 * @throws IOException
	 */
	@Test
	// PMR
	public void testCreateScraperKeys() throws JsonSyntaxException, IOException {
		if (!CMineFixtures.exist(CMineFixtures.SCRAPER_DIR)) return;
		ScraperSet scraperSet = new ScraperSet(CMineFixtures.SCRAPER_DIR);
		Multiset<String> keys = scraperSet.getOrCreateScraperKeys();
		Assert.assertTrue("keys "+keys.size(), keys.size() > 500);
		Assert.assertTrue("keys1 "+keys.entrySet().size(),  keys.entrySet().size() > 80);
		Assert.assertTrue("desc", keys.toString().contains("description"));
		
//		Assert.assertEquals(553,  keys.size());
//		Assert.assertEquals(90,  keys.entrySet().size());
//		Assert.assertEquals("[date x 31, author_contrib_html x 2, copyright x 13, references x 3, keywords x 3,"
//				+ " supplementary_material_richtext, supplementary_material x 11, fulltext_ePUB x 2,"
//				+ " supplementary_material_ms-excel, supplementary_material_encapsulated-postscript, caption x 2,"
//				+ " language x 10, section, supplementary_material_audio, source x 5, supplementary_material_ascii,"
//				+ " supplementary_material_ms-word, date_accepted x 2, date_published x 2, htmlBodyAuthors x 3,"
//				+ " supplementary_material_wordperfect, identifier x 5, date_submitted x 2, abstract2 x 2, issue x 21,"
//				+ " author x 6, corresponding_author_email x 3, lastpage x 6, discussion_html x 2, volume x 24, fulltext_html x 31,"
//				+ " onlineDate x 3, coordinates_cif x 2, license x 15, issn x 10, methods_html x 2, fulltext_pdf x 33, publisher x 27,"
//				+ " author_institution x 2, smallfigure, authors x 23, doi x 31, author_name x 2, csv4, results_html x 2, csv5,"
//				+ " csv2, conference, supplementary_material_movie, csv3, supplementary_material_postscript, firstpage x 20,"
//				+ " figure_caption x 8, supplementary_file x 2, csv1, creators x 5, supplementary_material_mpg, htmlCitations x 2,"
//				+ " description x 19, supplementary_material_html, supplementary_material_sbml, abstract_text, title x 31,"
//				+ " introduction_html x 2, html_title, references_html x 2, fulltext_xml x 6, figures_image x 2,"
//				+ " journal x 16, editor_name x 2, supplementary_material_xml, conclusion_html, competing_interests_html,"
//				+ " fulltext_html_frameset x 2, csv6, structure_factors_cif, tables_html x 2, figure x 14, largefigure,"
//				+ " htmlBodyAuthorUrls x 3, supplementary_material_owl, author_institutions x 3, journal_name x 2,"
//				+ " abstract x 19, supplementary_material_pdf, citationDate x 3, abstract_html x 6, figures_html x 2,"
//				+ " journal_issn x 2, contributors x 5]", keys.toString());
	}

	/** CREATES SPREADSHEET OF WHICH KEYS IN WHICH SCRAPERS.
	 * 
	 * @throws JsonSyntaxException
	 * @throws IOException
	 */
	@Test
	public void testCreateScraperSpreadsheet() throws JsonSyntaxException, IOException {
		if (!CMineFixtures.exist(CMineFixtures.SCRAPER_DIR)) return;
		ScraperSet scraperSet = new ScraperSet(CMineFixtures.SCRAPER_DIR);
		Map<File, JsonElement> elementsByFile = scraperSet.getJsonElementByFile();
		List<Multiset.Entry<String>> elements = scraperSet.getOrCreateScraperElementsByCount();
		List<String> headings = new ArrayList<String>();
		for (Multiset.Entry<String> entry : elements) {
			headings.add(entry.getElement());
		}
//		Assert.assertEquals(90,  headings.size());
		RectangularTable csvTable = new RectangularTable();
		csvTable.addRow(headings);
		List<File> files = new ArrayList<File>(elementsByFile.keySet());
		for (File file : files) {
			List<String> row = new ArrayList<String>();
			for (int i = 0; i < headings.size(); i++) {
				row.add("");
			}
			JsonElement element = elementsByFile.get(file);
			JsonElement elements1 = element.getAsJsonObject().get(ScraperSet.ELEMENTS);
			Set<Map.Entry<String, JsonElement>> entries = elements1.getAsJsonObject().entrySet();
			for (Map.Entry<String, JsonElement> entry : entries) {
				String name = entry.getKey();
				int idx = headings.indexOf(name);
				if (idx ==  -1) {
					LOG.error("bad key "+name);
				}
				row.set(idx, name);
			}
			csvTable.addRow(row);
		}
		File csvFile = new File(CMineFixtures.SCRAPER_DIR, ScraperSet.SCRAPERS_CSV);
		csvTable.writeCsvFile(csvFile.toString());
		List<String> lines = FileUtils.readLines(csvFile);
		Assert.assertTrue(lines.size() >= 30);
	}
}
