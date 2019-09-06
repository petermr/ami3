package org.contentmine.graphics.svg.cache;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

/** tests the detection of graphics components (rects, lines, etc.) and
 * maybe makes decisions on processing.
 * 
 * @author pm286
 *
 */
@Ignore // too long
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
		for (File typesDir : SVGHTMLFixtures.TABLE_TYPES) {
			File[] svgFiles = typesDir.listFiles();
			if (svgFiles == null) continue;
			for (File svgFile : svgFiles) {
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
		File csvFile = new File(TARGET_TABLE_TYPES_DIR, "graphics.csv");
		CSVUtil.writeCSV(csvFile.toString(), headers, bodyList);
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
		if (!jb.exists()) {LOG.error("No file: "+jb); return;}
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
