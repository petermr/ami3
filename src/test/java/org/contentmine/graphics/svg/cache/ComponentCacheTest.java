package org.contentmine.graphics.svg.cache;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.util.CSVUtil;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.cache.ComponentCache.Feature;
import org.contentmine.graphics.svg.util.ColorStore;
import org.contentmine.graphics.svg.util.ColorStore.ColorizerType;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Multiset;

import junit.framework.Assert;

/** tests the detection of graphics components (rects, lines, etc.) and
 * maybe makes decisions on processing.
 * 
 * @author pm286
 *
 */
//@Ignore // too long
public class ComponentCacheTest {
	private static final Logger LOG = Logger.getLogger(ComponentCacheTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private static final String DOT_SVG = ".svg";
	private static final String TABLE_SVG = "table.svg";
	private static final String TARGET_TABLE_TYPES_DIR = "target/table/types";

	@Test
	public void testSummarizeGraphicsComponents() throws IOException {
		List<List<String>> bodyList = new ArrayList<List<String>>();
		List<String> headers = new ArrayList<String>();
		headers.add(ComponentCache.FILE);
		headers.addAll(Feature.getAbbreviations(Feature.TEXT_SHAPE_FEATURES));
		LOG.trace("files: "+SVGHTMLFixtures.TABLE_TYPES.length);
		for (File typesDir : SVGHTMLFixtures.TABLE_TYPES) {
			LOG.trace("types: "+typesDir);
			File[] svgFiles = typesDir.listFiles();
			if (svgFiles != null) {
				List<File> fileList = Arrays.asList(svgFiles);
				Collections.sort(fileList);
				LOG.trace("subfiles: "+fileList.size());
				for (File svgFile : fileList) {
					if (svgFile.toString().endsWith(DOT_SVG)) {
						List<String> row = new ArrayList<String>();
						String filename = svgFile.getName();
						LOG.trace(filename);
						row.add(filename);
						AbstractCMElement svgElement = SVGElement.readAndCreateSVG(svgFile);
						ComponentCache cache = new ComponentCache();
						cache.readGraphicsComponentsAndMakeCaches(svgElement);
						List<String> featureValues = cache.getFeatureValues(Feature.TEXT_SHAPE_FEATURES);
						row.addAll(featureValues);
						bodyList.add(row);
					}
				}
			}
		}
		File csvFile = new File(TARGET_TABLE_TYPES_DIR, "graphics.csv");
		Assert.assertEquals("headers", 
				"[file, htxt, htsty, vtxt, vtsty, lines, rects, paths, circs, ellips, pgons, plines, shapes]",
				headers.toString());
		CSVUtil.writeCSV(csvFile.toString(), headers, bodyList);
		Assert.assertEquals("cache types", "["
				+ "[AA_Kranke.g.2.3.svg, , , 3150, 4, 5, , , , , , , ], "
				+ "[ACR.g.7.2.svg, 901, 4, , , 4, , , , , , , ], "
				+ "[ADA1.g.4.0.svg, 724, 6, , , 6, 1, , , , , , ], "
				+ "[ADA2.g.4.3.svg, 1174, 6, , , 6, , , , , , , ], "
				+ "[APA_Nuitjen.svg, 1015, 6, , , 9, , , , , , , ], "
				+ "[ELS2.g.4.17.svg, 415, 4, , , 6, , , , , , , ], "
				+ "[ELSPetaja.g.4.3.svg, 380, 2, , , 5, , , , , , , ], "
				+ "[LPW_Reisinger.g.4.5.svg, 872, 6, , , 7, , , , , , , ], "
				+ "[LWW61463_TABLE.g.2.9.svg, 218, 4, , , 6, , , , , , , ], "
				+ "[Wiley44386.g.4.1.svg, 1704, 4, , , 3, , , , , , , ], "
				+ "[aa_kranke2000-page2.svg, 47, 2, 3150, 4, 5, , , , , , , ], "
				+ "[brandon_table1.svg, 897, 5, , , 9, , , , , , , ], "
				+ "[brandon_table2.svg, 939, 5, , , 17, , , , , , , ], "
				+ "[pollak_table1.svg, 1038, 4, , , 8, , , , , , , ], "
				+ "[pollak_table1cont.svg, 640, 2, , , 15, , , , , , , ], "
				+ "[pollak_table2.svg, 544, 5, , , 13, , , , , , , ], "
				+ "[pollak_table3.svg, 380, 5, , , 13, , , , , , , ], "
				+ "[pollak_table4cont2rot.svg, , , 179, 1, , , , , , , , ], "
				+ "[pollak_table4controt.svg, , , 1128, 3, 26, , , , , , , ], "
				+ "[pollak_table4rot.svg, , , 827, 5, 14, , , , , , , ], "
				+ "[LANCET.g.6.3.svg, 1710, 2, , , 1, 110, 1, , , , 2, ], "
				+ "[NEJMOA.g.4.1.svg, 1962, 4, , , 8, 65, , , , , , ], "
				+ "[PLOS57170.g.2.8.svg, 1439, 6, , , 10, 240, , , , , , ], "
				+ "[bericht.page22.svg, 1437, 3, , , 199, 218, , , , , , ], "
				+ "[bericht.page6.svg, 1733, 4, , , 23, 21, , , , , , ], "
				+ "[NATURE.g.6.0.svg, 1948, 10, , , 404, 7, 1, , , , , ], "
				+ "[TEX_Ausloos2016.g.5.0.svg, 681, 2, , , 103, , , , , , , ], "
				+ "[TEX_Ausloos2016.g.5.1.svg, 490, 2, , , 77, , , , , , , ], "
				+ "[Springer68755.g.7.0.svg, 953, 6, , , 5, , , , , , , ], "
				+ "[BMJ312529.g.4.1.svg, 1444, 8, , , 3, 2, , , , , , ], "
				+ "[AMA_Dobson.g.6.4.svg, 2186, 4, , , 38, , , , , , , ]"
				+ "]",
				bodyList.toString());
		
	}
	
	/** extracts all text styles from documents and creates a table.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testSummarizeTextStyles() throws IOException {
		List<List<String>> bodyList = new ArrayList<List<String>>();
		List<String> headers = new ArrayList<String>();
		headers.add(ComponentCache.FILE);
		TextCache textCache = null;
		for (File typeDir : SVGHTMLFixtures.TABLE_TYPES) {
			String typeName = typeDir.getName();
			File outParent = new File(TARGET_TABLE_TYPES_DIR, typeName+"/");
			File[] svgFiles = typeDir.listFiles();			
			for (File svgFile : svgFiles) {
				String filename = svgFile.toString();
				if (filename.endsWith(DOT_SVG)) {
					List<String> row = new ArrayList<String>();
					String baseName = FilenameUtils.getBaseName(filename);
					LOG.trace(baseName);
					row.add(filename);
					AbstractCMElement svgElement = SVGElement.readAndCreateSVG(svgFile);
					ComponentCache cache = new ComponentCache();
					cache.readGraphicsComponentsAndMakeCaches(svgElement);
					textCache = cache.getOrCreateTextCache();
					Multiset<String> styleSet = textCache.createAbbreviatedHorizontalTextStyleMultiset();
					row.addAll(textCache.createRowOfStyles( styleSet));
					bodyList.add(row);
					File outfile = new File(outParent, baseName+DOT_SVG);
					SVGSVG.wrapAndWriteAsSVG(cache.getOrCreateConvertedSVGElement(), outfile);
				}
			}
		}
		// messy because only now do we know the textCaches;
		for (int i = 0; i < textCache.getMaxStylesInRow(); i++) {
			headers.add("style"+i);
			headers.add("count"+i);
		}
		File csvFile = new File(TARGET_TABLE_TYPES_DIR, "fonts.csv");
		CSVUtil.writeCSV(csvFile.toString(), headers, bodyList);
	}

	@Test
	public void testLinesAndRects() {
		List<List<String>> csvBodyList = new ArrayList<List<String>>();
		List<String> headers = new ArrayList<String>();
		headers.add(ComponentCache.FILE);
		headers.addAll(Feature.getAbbreviations(Feature.RECT_LINE_FEATURES));
		for (File dir : SVGHTMLFixtures.TABLE_TYPES) {
			File[] svgFiles = dir.listFiles();
			for (File svgFile : svgFiles) {
				if (svgFile.toString().endsWith(DOT_SVG)) {
					csvBodyList.add(createCSVRow(svgFile, Feature.RECT_LINE_FEATURES));
				}
			}
		}
		File csvFile = new File(TARGET_TABLE_TYPES_DIR, "lines.csv");
		CSVUtil.writeCSV(csvFile.toString(), headers, csvBodyList);
		
	}
	
	@Test
	public void testMoreLinesAndRects() {
		List<List<String>> csvBodyList = new ArrayList<List<String>>();
		List<String> headers = new ArrayList<String>();
		headers.add(ComponentCache.FILE);
		headers.addAll(Feature.getAbbreviations(Feature.RECT_LINE_FEATURES));
		File jb = new File("/Users/pm286/workspace/cm-ucliijkb/corpus-oa-uclii-01");
		if (!jb.exists()) {LOG.info("No file: "+jb+"; so test skipped"); return;}
		// tedious - need RegexFileFilter here but it's in cproject - needs moving
		File[] cTreeFiles = jb.listFiles();
		List<File> svgTables = extractSVGTables(csvBodyList, cTreeFiles);
		for (File svgTableFile : svgTables) {
			csvBodyList.add(createCSVRow(svgTableFile, Feature.RECT_LINE_FEATURES));
		}
		File csvFile = new File(TARGET_TABLE_TYPES_DIR, "moreRectLines.csv");
		CSVUtil.writeCSV(csvFile.toString(), headers, csvBodyList);
	}

	private static String createCTreeTableName(File svgTableFile) {
		return svgTableFile.getParentFile().getParentFile().getParentFile().getName()+"/"+svgTableFile.getParentFile().getName();
	}

	/** this should go to CProject later.
	 * 
	 * @param bodyList
	 * @param cTreeFiles
	 * @return
	 */
	private List<File> extractSVGTables(List<List<String>> bodyList, File[] cTreeFiles) {
		List<File> tables = new ArrayList<File>();
		for (File cTreeFile : cTreeFiles) {
			if (!cTreeFile.isDirectory()) continue;
			for (File tablesDir : cTreeFile.listFiles()) {
				if (!tablesDir.isDirectory()) continue;
				for (File tablenDir : tablesDir.listFiles()) {
					for (File svgFile : tablenDir.listFiles()) {
						if (svgFile.getName().equals(TABLE_SVG)) {
							tables.add(svgFile);
						}
					}
				}
			}
		}
		return tables;
	}

	@Test
	public void testPaintTextStyles() throws IOException {
		List<List<String>> bodyList = new ArrayList<List<String>>();
		ColorStore colorStore = ColorStore.createColorizer(ColorizerType.CONTRAST);
		for (File typeDir : SVGHTMLFixtures.TABLE_TYPES) {
			String typeName = typeDir.getName();
			File outParent = new File(TARGET_TABLE_TYPES_DIR, typeName+"/");
			File[] svgFiles = typeDir.listFiles();			
			for (File svgFile : svgFiles) {
				String filename = svgFile.toString();
				if (filename.endsWith(DOT_SVG)) {
					List<String> row = new ArrayList<String>();
					String baseName = FilenameUtils.getBaseName(filename);
					LOG.trace(baseName);
					row.add(filename);
					ComponentCache cache = new ComponentCache();
					cache.readGraphicsComponentsAndMakeCaches(svgFile);
					TextCache textCache = cache.getOrCreateTextCache();
					textCache.createCompactedTextsAndReplace();
					SVGG g = new SVGG();
					g.appendChild(textCache.createColoredTextStyles());
					g.appendChild(cache.getOrCreateLineCache().createColoredHorizontalLineStyles());
					File outfile = new File(outParent, baseName+DOT_SVG);
					SVGSVG.wrapAndWriteAsSVG(g, outfile);
				}
				continue;
			}
		}
	}

	// ==============================
	
	private static List<String> createCSVRow(File svgFile, List<Feature> features) {
		String filename = createCTreeTableName(svgFile);
		List<String> rowx = new ArrayList<String>();
		rowx.add(filename);
		AbstractCMElement svgElement = SVGElement.readAndCreateSVG(svgFile);
		ComponentCache cache = new ComponentCache();
		cache.readGraphicsComponentsAndMakeCaches(svgElement);
		List<String> featureValues = cache.getFeatureValues(features);
		rowx.addAll(featureValues);
		return rowx;
	}
	


}
