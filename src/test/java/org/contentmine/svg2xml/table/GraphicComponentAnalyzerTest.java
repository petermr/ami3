package org.contentmine.svg2xml.table;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/** tests the detection of graphics components (rects, lines, etc.) and
 * maybe makes decisions on processing.
 * 
 * @author pm286
 *
 */
@Deprecated //"move to package svg";
public class GraphicComponentAnalyzerTest {
	private static final Logger LOG = Logger.getLogger(GraphicComponentAnalyzerTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	private static final String FILE = "file";

//	@Test
//	public void testSummarizeGraphicsComponents() throws IOException {
//		List<List<String>> bodyList = new ArrayList<List<String>>();
//		List<String> headers = new ArrayList<String>();
//		headers.add(FILE);
//		List<Feature> features = Arrays.asList(new Feature[] {
//			Feature.HORIZONTAL_TEXT_COUNT,
//			Feature.VERTICAL_TEXT_COUNT,
//			Feature.LINE_COUNT,
//			Feature.RECT_COUNT,
//			Feature.PATH_COUNT,
//			Feature.CIRCLE_COUNT,
//			Feature.ELLIPSE_COUNT,
//			Feature.POLYGONS_COUNT,
//			Feature.POLYLINE_COUNT,
//			Feature.SHAPE_COUNT,
//		}
//		);
//		headers.addAll(Feature.getAbbreviations(features));
//		for (File dir : SVG2XMLFixtures.TABLE_TYPES) {
//			File[] svgFiles = dir.listFiles();
//			for (File svgFile : svgFiles) {
//				if (svgFile.toString().endsWith(".svg")) {
//					List<String> row = new ArrayList<String>();
//					String filename = svgFile.getName();
//					LOG.debug(filename);
//					row.add(filename);
//					SVGElement svgElement = SVGElement.readAndCreateSVG(svgFile);
//					GraphicCache cache = new GraphicCache();
//					cache.readGraphicsComponentsAndMakeCaches(svgElement);
//					List<String> featureValues = cache.getFeatureValues(features);
//					row.addAll(featureValues);
//					bodyList.add(row);
//				}
//			}
//		}
//		File csvFile = new File("target/table/types/graphics.csv");
//		CSVUtil.writeCSV(csvFile.toString(), headers, bodyList);
//	}
//	
//	@Test
//	public void testSummarizeTextStyles() throws IOException {
//		List<List<String>> bodyList = new ArrayList<List<String>>();
//		int maxStyles = 5;
//		List<String> headers = new ArrayList<String>();
//		headers.add(FILE);
//		for (int i = 0; i < maxStyles; i++) {
//			headers.add("style"+i);
//			headers.add("count"+i);
//		}
//		for (File dir : SVG2XMLFixtures.TABLE_TYPES) {
//			File[] svgFiles = dir.listFiles();
//			for (File svgFile : svgFiles) {
//				if (svgFile.toString().endsWith(".svg")) {
//					List<String> row = new ArrayList<String>();
//					String filename = svgFile.getName();
//					LOG.debug(filename);
//					row.add(filename);
//					SVGElement svgElement = SVGElement.readAndCreateSVG(svgFile);
//					GraphicCache cache = new GraphicCache();
//					cache.readGraphicsComponentsAndMakeCaches(svgElement);
//					Multiset<String> styleSet = cache.getOrCreateTextCache().createAbbreviatedHorizontalTextStyleMultiset();
//					List<Multiset.Entry<String>> entryList = MultisetUtil.createStringListSortedByCount(styleSet);
//					int entryCount = entryList.size();
//					int filled = Math.min(entryCount, maxStyles);
//					int empty = Math.max(0, maxStyles - entryCount);
//					for (int i = 0; i < filled; i++) {
//						Multiset.Entry<String> entry = entryList.get(i);
//						row.add(entry.getElement());
//						row.add(String.valueOf(entry.getCount()));
//					}
//					for (int i = 0; i < empty; i++) {
//						row.add("");
//						row.add("");
//					}
//					bodyList.add(row);
//				}
//			}
//		}
//		File csvFile = new File("target/table/types/fonts.csv");
//		CSVUtil.writeCSV(csvFile.toString(), headers, bodyList);
//	}
//	
//	@Test
//	public void testLinesAndRects() {
//		List<List<String>> bodyList = new ArrayList<List<String>>();
//		List<String> headers = new ArrayList<String>();
//		headers.add(FILE);
//		List<Feature> features = Arrays.asList(new Feature[] {
//			Feature.LONG_HORIZONTAL_RULE_COUNT,
//			Feature.SHORT_HORIZONTAL_RULE_COUNT,
//			Feature.TOP_HORIZONTAL_RULE_COUNT,
//			Feature.BOTTOM_HORIZONTAL_RULE_COUNT,
//			Feature.LONG_HORIZONTAL_RULE_THICKNESS_COUNT,
//			Feature.HORIZONTAL_PANEL_COUNT,
//		}
//		);
//		headers.addAll(Feature.getAbbreviations(features));
//		headers.add(FILE);
//		for (File dir : SVG2XMLFixtures.TABLE_TYPES) {
//			File[] svgFiles = dir.listFiles();
//			for (File svgFile : svgFiles) {
//				if (svgFile.toString().endsWith(".svg")) {
//					List<String> row = new ArrayList<String>();
//					String filename = svgFile.getName();
//					LOG.debug(filename);
//					row.add(filename);
//					SVGElement svgElement = SVGElement.readAndCreateSVG(svgFile);
//					GraphicCache cache = new GraphicCache();
//					cache.readGraphicsComponentsAndMakeCaches(svgElement);
//					List<String> featureValues = cache.getFeatureValues(features);
//					row.addAll(featureValues);
//					bodyList.add(row);
//				}
//			}
//		}
//		File csvFile = new File("target/table/types/lines.csv");
//		CSVUtil.writeCSV(csvFile.toString(), headers, bodyList);
//		
//	}
}
