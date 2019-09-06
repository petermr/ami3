package org.contentmine.svg2xml.text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.text.TextCoordinate;
import org.contentmine.graphics.svg.text.build.Phrase;
import org.contentmine.graphics.svg.text.build.PhraseChunk;
import org.contentmine.graphics.svg.text.build.TextChunk;
import org.contentmine.graphics.svg.text.build.Word;
import org.contentmine.graphics.svg.text.line.TabbedTextLine;
import org.contentmine.graphics.svg.text.line.TextLine;
import org.contentmine.graphics.svg.text.structure.RawWords;
import org.contentmine.graphics.svg.text.structure.TextStructurer;
import org.contentmine.svg2xml.SVG2XMLFixtures;
import org.contentmine.svg2xml.util.SVG2XMLConstantsX;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Multiset;

public class TextStructurerTest {

	public static File OUTPUT_TEXT_DIR = new File("target/text/");
	private static final File ROTATED_PHRASE_FILE_1 = new File(OUTPUT_TEXT_DIR, "phraseRotate1.svg");

	private final static Logger LOG = Logger.getLogger(TextStructurerTest.class);
	
	private static final String GEOTABLE_7 = "geotable-7.";
	private List<File> geoFileList;
	private double angleEpsilon = 0.00001;
	
	@Before
	public void setup() {
		Assert.assertTrue(SVG2XMLFixtures.BMC_DIR.exists());
		File[] files = SVG2XMLFixtures.BMC_DIR.listFiles();
		Assert.assertTrue(files.length > 0);
		geoFileList = new ArrayList<File>();
		for (File file : files) {
			String name = file.getName();
			if (name.startsWith(GEOTABLE_7) && name.endsWith(SVG2XMLConstantsX.DOT_SVG) &&
					!name.equals(GEOTABLE_7+"svg")) {
				geoFileList.add(file);
			}
		}
		Assert.assertTrue(geoFileList.size() == 7);
	}
	
	@Test
	public void testMultilineFonts() {
		TextStructurer textContainer = TextStructurer.createTextStructurer(SVG2XMLFixtures.PARA_SUSCRIPT_SVG);
		Multiset<String> fontFamilyMultiset = textContainer.getFontFamilyMultiset();
		Assert.assertEquals("font occurrences", 523, fontFamilyMultiset.size());
		Set<String> entrySet = fontFamilyMultiset.elementSet();
		Assert.assertEquals("different fonts", 3, entrySet.size());
		Assert.assertEquals("Times-Roman", 3, fontFamilyMultiset.count("Times-Roman"));
		Assert.assertEquals("MTSYN", 7, fontFamilyMultiset.count("MTSYN"));
		Assert.assertEquals("TimesNewRoman", 513, fontFamilyMultiset.count("TimesNewRoman"));
	}
	
	@Test
	public void testMultilineCommonestFontFamily() {
		TextStructurer textContainer = TextStructurer.createTextStructurer(SVG2XMLFixtures.PARA_SUSCRIPT_SVG);
		Assert.assertEquals("commonest fontfamily", "TimesNewRoman", textContainer.getCommonestFontFamily());
	}
	
	@Test
	public void testReadBMCGeotable() {
		Assert.assertEquals(7, geoFileList.size());
	}
	
	@Test
	public void testReadBMCGeotableContainers() {
		for (File geoFile : geoFileList) {
			TextStructurer container = TextStructurer.createTextStructurer(geoFile);
		}
	}
	
	@Test
	@Ignore
	public void testCommonestBMCGeotableFontSizes() {
		double[] sizes = {7.97, 7.97, 9.76, 10.26, 9.76, 10.26, 9.76};
		int i = 0;
		for (File geoFile : geoFileList) {
			TextStructurer container = TextStructurer.createTextStructurer(geoFile);
			TextCoordinate size = container.getCommonestFontSize();
			Assert.assertEquals("file"+i, sizes[i], size.getDouble(), 0.001);
			i++;
		}
	}
	
	@Test
	@Ignore
	public void testCommonestBMCGeotableFontFamilies() {
		String[] family = {"AdvOT46dcae81", "AdvOT46dcae81", "AdvOTa9103878", "AdvOTa9103878", 
				           "AdvOTa9103878", "AdvOTa9103878", "AdvOTa9103878"};
		int i = 0;
		for (File geoFile : geoFileList) {
			TextStructurer container = TextStructurer.createTextStructurer(geoFile);
			String fontFamily = container.getCommonestFontFamily();
			Assert.assertEquals("file"+i, family[i], fontFamily);
			i++;
		}
	}
	
	@Test
	@Ignore
	public void testBMCGeotableFontFamilyDiversity() {
		int[] nfont = {3, 1, 3, 3, 1, 5, 3};
		int i = 0;
		for (File geoFile : geoFileList) {
			TextStructurer container = TextStructurer.createTextStructurer(geoFile);
			Assert.assertEquals("file"+i, nfont[i], container.getFontFamilyCount());
			i++;
		}
	}
	
	@Test
	public void testBMCGeotableTextLines() {
		File geoFile2 = geoFileList.get(2);
		TextStructurer container = TextStructurer.createTextStructurer(geoFile2);
	}
	
	@Test
	@Ignore // fails
	public void testFullTables() {
		TextStructurer textStructurer = 
				TextStructurer.createTextStructurerWithSortedLines(SVG2XMLFixtures.BERICHT_PAGE6_SVG);
		List<TabbedTextLine> tabbedTextLineList = textStructurer.createTabbedLineList();
//		Assert.assertNotNull(tabbedTextLineList);
//		for (int i = 0; i < tabbedTextLineList.size(); i++) {
//			System.out.println(">"+i+"> "+tabbedTextLineList.get(i));
//		}

	}

	@Test
	@Ignore
	public void testWordListCollection() {
		TextStructurer textStructurer = 
				TextStructurer.createTextStructurerWithSortedLines(SVG2XMLFixtures.BERICHT_PAGE6_SVG);
		List<TextLine> textLineList = textStructurer.getLinesInIncreasingY();
		for (int i = 0; i < textLineList.size(); i++) {
			System.out.println(">"+i+"> "+textLineList.get(i));
		}

	}

	@Test
	public void testHOText() {
		TextStructurer textStructurer = 
				TextStructurer.createTextStructurerWithSortedLines(SVG2XMLFixtures.IMAGE_2_11_HO_SVG);
		List<RawWords> wordList = textStructurer.createRawWordsListFromTextLineList();
		Assert.assertEquals("ho", "{HO}", wordList.get(0).toString());
	}
	
	@Test
	public void testSubscriptedText() {
		TextStructurer textStructurer = 
				TextStructurer.createTextStructurerWithSortedLines(SVG2XMLFixtures.IMAGE_2_11_NO2_SVG);
		List<RawWords> wordList = textStructurer.createRawWordsListFromTextLineList();
		Assert.assertEquals("no2", 2, wordList.size());
		Assert.assertEquals("no", "{NO}", wordList.get(0).toString());
		Assert.assertEquals("xy", "(299.7,525.78)", wordList.get(0).get(0).getXY().toString());
		Assert.assertEquals("no", "{2}", wordList.get(1).toString());
		Assert.assertEquals("xys", "(312.42,527.7)", wordList.get(1).get(0).getXY().toString());
	}
	
	@Test
	public void test2_11() {
		TextStructurer textStructurer = 
				TextStructurer.createTextStructurerWithSortedLines(SVG2XMLFixtures.IMAGE_2_11_SVG);
		List<RawWords> wordList = textStructurer.createRawWordsListFromTextLineList();
		Assert.assertEquals("2.11", 3, wordList.size());
		Assert.assertEquals("1", "{HO........NO}", wordList.get(0).toString());
		Assert.assertEquals("2", "{2}", wordList.get(1).toString());
		Assert.assertEquals("2", "{O}", wordList.get(2).toString());
	}
	
	
	@Test
	public void test2_15() {
		TextStructurer textStructurer = 
				TextStructurer.createTextStructurerWithSortedLines(SVG2XMLFixtures.IMAGE_2_15_SVG);
		
		List<RawWords> wordList = textStructurer.createRawWordsListFromTextLineList();
		Assert.assertEquals("words", 6, wordList.size());
		Assert.assertEquals("0", "{O}", wordList.get(0).toString());
		Assert.assertEquals("1", "{N}", wordList.get(1).toString());
		Assert.assertEquals("2", "{H}", wordList.get(2).toString());
		Assert.assertEquals("3", "{H}", wordList.get(3).toString());
		Assert.assertEquals("4", "{OH..O}", wordList.get(4).toString());
		Assert.assertEquals("5", "{Cyclopiazonic.acid}", wordList.get(5).toString());
	}

	@Test
	/* SHOWCASE 
	 * this has a vertical axis label. "cumulative mortality"
	 * after rotation and horizontal filtering should be visible as horizontal
	 * Result is PhraseListList(0) ->  PhraseList(0) ->  Phrase(0) -> Word(0)["cumulative"] + Word(1)["mortality"] 
	 * WORKS
	 */
	public void testRotatePhrasesAndExtractPhraseList() throws Exception {
		TextChunk phraseListList; PhraseChunk phraseList; Phrase phrase; Word word0, word1;
		File graphTextFile = new File(SVG2XMLFixtures.PLOT_DIR, "BLK_SAM.g.4.0.svg");
		TextStructurer textStructurer;
		phraseListList = getUnrotatedPhrases(graphTextFile, 36, "HD-73//1//antibiotic free diet//0.9//0.8//y//t//i//l//0.7//a//t//r//o//0.6//m//e//0.5//v//i//t//a//0.4//l//u//m//0.3//u//rifampicin//c//0.2//diet//0.1//0//1 2 3 4 5//days//");

		// horizontal phrases
		Assert.assertEquals("HD-73", phraseListList.get(0).getStringValue());
		Assert.assertEquals("antibiotic free diet", phraseListList.get(2).getStringValue());
		Assert.assertEquals("rifampicin", phraseListList.get(28).getStringValue());
		Assert.assertEquals("diet", phraseListList.get(31).getStringValue());
		Assert.assertEquals("1 2 3 4 5", phraseListList.get(34).getStringValue());
		Assert.assertEquals("days", phraseListList.get(35).getStringValue());
		
		// y-values in ladder with 12.3/12.4 delta
		int[] phraseIndexes = new int[]{1, 3, 4, 9, 14, 17, 22, 26, 30, 32, 33};
		String[] phraseValues = {"1", "0.9", "0.8", "0.7", "0.6", "0.5", "0.4", "0.3", "0.2", "0.1", "0"};
		double xValue = 76.6;
		double deltaY = 12.3;
		double yEps = 0.11;
		assertLadder(phraseListList, phraseIndexes, phraseValues, xValue, deltaY, yEps);
		
		// rotated text produces single character phrases. 
		int[] rotated = new int[]{5,6,7,8,10,11,12,13,15,16,18,19,20,21,23,24,25,27,29,};
		// reverse as it goes upwards on screen - "c" has largest Y.
		String vtext = StringUtils.reverse("cumulativemortality"); 
		for (int phraseIndex = 0; phraseIndex < rotated.length; phraseIndex++) {
			Assert.assertEquals(String.valueOf(vtext.charAt(phraseIndex)), 
					phraseListList.get(rotated[phraseIndex]).getStringValue().trim());
		}

		// now process rotated text - this is common y-axis text orientation
		// rotation centre is arbitrary, angle is clockwise
		TextStructurer textStructurer2 = new TextStructurer();
		textStructurer2.setRotatable(true);
		SVGG rotatedVerticalText = textStructurer2.createChunkFromVerticalText(new Real2(200., 200.), new Angle(-1.0 * Math.PI / 2));
		LOG.trace("rot text "+rotatedVerticalText.toXML());
//		LOG.error("FAILS");
		if (1 == 1) {
			LOG.warn("aborting as tests not finished");
			return;
		};
//		Assert.assertEquals(19, SVGText.extractSelfAndDescendantTexts(rotatedVerticalText).size());

		File outFile = new File(OUTPUT_TEXT_DIR, "rotatedVerticalText.svg");
		SVGSVG.wrapAndWriteAsSVG(rotatedVerticalText, outFile);
		
		// reread and analyze the horizontal (previously vertical) lines;
		textStructurer = TextStructurer.createTextStructurerWithSortedLines(rotatedVerticalText);
		phraseListList = textStructurer.getTextChunkList().getLastTextChunk();
		phraseListList.getStringValue(); // computes if not already known
		Assert.assertEquals(1, phraseListList.size());
		Assert.assertEquals("cumulative mortality //", phraseListList.getStringValue());
		phraseList = phraseListList.get(0);
		Assert.assertEquals("cumulative mortality ", phraseList.getStringValue());
		Assert.assertEquals(1, phraseList.size());
		phrase = phraseList.get(0);
		Assert.assertEquals("cumulative mortality", phrase.getStringValue());
		Assert.assertEquals(2, phrase.size());
		word0 = phrase.getOrCreateWordList().get(0);
		Assert.assertEquals("cumulative", word0.getStringValue());
		word1 = phrase.getOrCreateWordList().get(1);
		Assert.assertEquals("mortality", word1.getStringValue());
		SVGG gg = new SVGG();
		gg.appendChild(phraseListList.copy());
		SVGSVG.wrapAndWriteAsSVG(gg, new File(OUTPUT_TEXT_DIR, "phrasesRotate.svg"));
	}

	@Test
	/* SHOWCASE 
	 * this has a vertical axis label. "cumulative mortality"
	 * after rotation and horizontal filtering should be visible as horizontal
	 * Result is PhraseListList(0) ->  PhraseList(0) ->  Phrase(0) -> Word(0)["cumulative"] + Word(1)["mortality"] 
	 * WORKS
	 */
	@Ignore // FIXME
	public void testRotatePhrasesAndExtractPhraseList1() throws Exception {
		
		File graphTextFile = new File(SVG2XMLFixtures.PLOT_DIR, "BLK_SAM.g.4.0.svg");
		String outputRoot = "blkSam40";

		String totalStringValue = ""
				+ "HD-73//1//antibiotic free diet//"
				+ "0.9//0.8//y//t//i//l//0.7//a//t//r//o//0.6//m//e//0.5//v//i//t//a//0.4//l//u//m//0.3//u//"
				+ "rifampicin//c//0.2//diet//0.1//0//1 2 3 4 5//days//";
		// horizontal phrases
		String[] horizontalPhraseValues = {
				"HD-73",
				"antibiotic free diet",
				"rifampicin", 
				"diet", 
				"1 2 3 4 5", 
				"days",
		};
		int phraseListListSize = 36;
		int[] horizontalPhraseIndexes = {0, 2, 28, 31, 34, 35};
		
		// ladderValues
		int[] ladderPhraseIndexes = new int[]{1, 3, 4, 9, 14, 17, 22, 26, 30, 32, 33};
		String[] ladderPhraseValues = {"1", "0.9", "0.8", "0.7", "0.6", "0.5", "0.4", "0.3", "0.2", "0.1", "0"};
		double ladderX = 76.6;
		double ladderDeltaY = 12.3;
		double yEps = 0.11;

		Real2 rotCentre = new Real2(200., 200.);
		int verticalCharacterCount = /*19 */94;
		String[] verticalValues = {"cumulative mortality"};

		// unrotated text
		assertExtractedTextAndOutputSVG(graphTextFile, outputRoot, 
				phraseListListSize, totalStringValue, horizontalPhraseValues, horizontalPhraseIndexes,
				ladderPhraseIndexes, ladderPhraseValues, ladderX, ladderDeltaY, yEps, 
				rotCentre, verticalCharacterCount, verticalValues);
	}
	@Test
	/* Histogram. 2-line Y-label
	 */
	@Ignore // FIXME
	public void testRotatePhrasesAndExtractPhraseListHistogram() throws Exception {
		
		File graphTextFile = new File(SVG2XMLFixtures.FIGURE_DIR, "histogram.svg");
		String outputRoot = "histogram";

		int phraseListListSize = 19;
		String totalStringValue = ""
				+ " //y//g//c//n//n//i//l//e//p//u//q//m//e//a//r//S f//"
				+ "0 2 4 6 8 10 12 14//"
				+ "Time (Myr)//";
		// horizontal phrases
		String[] horizontalPhraseValues = {
				"0 2 4 6 8 10 12 14",
				"Time (Myr)"
		};
		int[] horizontalPhraseIndexes = {17, 18};
		
		// ladderValues
		int[] ladderPhraseIndexes = null;
		String[] ladderPhraseValues = null;
		double ladderX = 76.6;
		double ladderDeltaY = 12.3;
		double yEps = 0.11;

		Real2 rotCentre = new Real2(200., 200.);
		int verticalCharacterCount = 39;
		String[] verticalValues = {"Sampling", "frequency"};

		// unrotated text
		assertExtractedTextAndOutputSVG(graphTextFile, outputRoot, 
				phraseListListSize, totalStringValue, horizontalPhraseValues, horizontalPhraseIndexes,
				ladderPhraseIndexes, ladderPhraseValues, ladderX, ladderDeltaY, yEps, 
				rotCentre, verticalCharacterCount, verticalValues);
	}

	@Test
	/* Scatterplot with lines. Simple axes
	 */
	@Ignore // FIXME
	public void testScatterPlot() throws Exception {
		
		File graphTextFile = new File(SVG2XMLFixtures.FIGURE_DIR, "lineplots.g.10.2.svg");
		String outputRoot = "scatter";

		int phraseListListSize = 40;
		String totalStringValue = ""
				+ "60//50//d//e//s//i//l//i//t//r//40//e//f// //s//g//g//e//30// //f//o// //r//e//b//20//m//u//n// //l//a//t//10//o//T//0//16 17 18 19//"
				+ "Phenotypic tarsus (mm)//";
		// horizontal phrases
		String[] horizontalPhraseValues = {
				"16 17 18 19",
				"Phenotypic tarsus (mm)",
		};
		int[] horizontalPhraseIndexes = {38, 39};
		
		// ladderValues
		int[] ladderPhraseIndexes = null;
		String[] ladderPhraseValues = null;
		double ladderX = 76.6;
		double ladderDeltaY = 12.3;
		double yEps = 0.11;

		Real2 rotCentre = new Real2(200., 200.);
		int verticalCharacterCount = 74;
		String[] verticalValues = {"Total number of eggs fertilised"};

		assertExtractedTextAndOutputSVG(graphTextFile, outputRoot, 
				phraseListListSize, totalStringValue, horizontalPhraseValues, horizontalPhraseIndexes,
				ladderPhraseIndexes, ladderPhraseValues, ladderX, ladderDeltaY, yEps, 
				rotCentre, verticalCharacterCount, verticalValues);
	}

	@Test
	/* X-Y plot with lines. Simple axes
	 */
	@Ignore // FIXME

	public void testMultiAxes() throws Exception {
		
		File graphTextFile = new File(SVG2XMLFixtures.FIGURE_DIR, "maths.g.6.8.svg");
		String outputRoot = "multiaxes";

		int phraseListListSize = 58;
		String totalStringValue = ""
				+ "4 4//.//.//1 1//)//)//λ//μ//(//(//  2 2// //. .//e//e//1 1//t//t//a//a//r//r// // //0 0//"
				+ ". .//n//n//1 1//o//o//i//i//t//t//a//c//i//8 8//. .//n//c//i//0 0//e//t//x//p//E//S//6 6//"
				+ ". .//0 0//4 4//. .//0 0//"
				+ "25 50 75 100//Taxon sampling (%)//";
		// horizontal phrases
		String[] horizontalPhraseValues = {
				"25 50 75 100",
				"Taxon sampling (%)",
		};
		int[] horizontalPhraseIndexes = {56, 57};
		
		// ladderValues
		int[] ladderPhraseIndexes = null;
		String[] ladderPhraseValues = null;
		double ladderX = 76.6;
		double ladderDeltaY = 12.3;
		double yEps = 0.11;

		Real2 rotCentre = new Real2(400., 200.);
		int verticalCharacterCount = 101;
		// FIXME BUG - the first RH axis has been garbled. Don't know  yet
		String[] verticalValues = {
				"Speciation rate (λ)",
				"0.4 0.6 0.8 1.0 1.2 1.4",
				"4 . .",
				"0. 0.6 08 10 1.2 1.4",
				"Extinction rate (μ)",
				};

		assertExtractedTextAndOutputSVG(graphTextFile, outputRoot, 
				phraseListListSize, totalStringValue, horizontalPhraseValues, horizontalPhraseIndexes,
				ladderPhraseIndexes, ladderPhraseValues, ladderX, ladderDeltaY, yEps, 
				rotCentre, verticalCharacterCount, verticalValues);
	}

	@Test
	/* X-Y plot with lines. Simple axes
	 */
	@Ignore // FIXME
	public void testSingleScatterplot() throws Exception {
		
		File graphTextFile = new File(SVG2XMLFixtures.FIGURE_DIR, "scatterplot.g.7.2.svg");
		String outputRoot = "scatterplot";

		int phraseListListSize = 20;
		String totalStringValue = ""
				+ " //0.04  //0.03  // //b//u//t//-//Ε// //f//0.02  //o// //N//d//"
				+ "0.01  //0.00  //0.00   0.01   0.02   0.03   0.04   0.05  //dN of EF-1Δ //";
		// horizontal phrases
		String[] horizontalPhraseValues = {
				"0.00   0.01   0.02   0.03   0.04   0.05",
				"dN of EF-1Δ", // Note the delta is font-size 0 but can still be processed
		};
		int[] horizontalPhraseIndexes = {18, 19};
		
		// ladderValues
		int[] ladderPhraseIndexes = null;
		String[] ladderPhraseValues = null;
		double ladderX = 76.6;
		double ladderDeltaY = 12.3;
		double yEps = 0.11;

		Real2 rotCentre = new Real2(400., 200.);
		int verticalCharacterCount = 91;
		String[] verticalValues = {
			"dN of Ε-tub",
		};

		assertExtractedTextAndOutputSVG(graphTextFile, outputRoot, 
				phraseListListSize, totalStringValue, horizontalPhraseValues, horizontalPhraseIndexes,
				ladderPhraseIndexes, ladderPhraseValues, ladderX, ladderDeltaY, yEps, 
				rotCentre, verticalCharacterCount, verticalValues);
	}

	@Test
	/* 5 X-Y plots with lines. Simple axes but many instances
	 */
	@Ignore // FIXME
	public void testScatterplot5() throws Exception {
		
		File graphTextFile = new File(SVG2XMLFixtures.FIGURE_DIR, "scatterplot5.g.7.2.svg");
		String outputRoot = "scatterplot5";

		int phraseListListSize = 133;
		String totalStringValue = ""
				+ "B  C //A //0.14  //0.50  //0.04  // //r//e//t//0.12  //s//u//0.40  //l//c// //0.03  //0.10  //A//"
				+ " // //b//N b//u//u//D//t 0.30  //t//r//0.08  //-//-// //f//Ε//Ε// // //o//f//f// //0.02  //"
				+ "o//o//e// // //0.06  //c//S//N//0.20  //n//d//d//a//t//s//0.04  //i//d//0.01  //-//0.10  //p//"
				+ "0.02  //0.00   0.00  //0.00  //0.00   0.01   0.02   0.03   0.04   0.05   0.00   0.01   0.02   0.03   0.04   0.05//"
				+ "0.00   0.01   0.02   0.03   0.04   0.05  //dN of EF-1Δ  dN of Ε -tub //dN of Ε -tub ////D  E //Δ//0.60  //1//-//"
				+ "0.40  //F//E// //n//0.50  //i//h//t////i//0.30  //Δ//w//1// //-//0.40  //n//F//o//E//r// //t//"
				+ "f//n//o//i// //0.30  // //0.20  //f//S//o//d// //e//c//0.20  //n//a//t//0.10  //s//i//d//0.10  //"
				+ "-//p//0.00   0.00  //0.00   0.01   0.02   0.03   0.04   0.05   0.00   0.01   0.02   0.03   0.04   0.05  //"
				+ "dN of Ε -tub  dN of Ε -tub //";
		// horizontal phrases
		String[] horizontalPhraseValues = {
				"0.00   0.01   0.02   0.03   0.04   0.05   0.00   0.01   0.02   0.03   0.04   0.05 ", // two plots
				"0.00   0.01   0.02   0.03   0.04   0.05", 
				"dN of EF-1Δ  dN of Ε -tub",
				"dN of Ε -tub",
				"0.00   0.01   0.02   0.03   0.04   0.05   0.00   0.01   0.02   0.03   0.04   0.05",
				"dN of Ε -tub  dN of Ε -tub"
		};
		int[] horizontalPhraseIndexes = {69, 70, 71, 72, 131, 132};
		
		// ladderValues
		int[] ladderPhraseIndexes = null;
		String[] ladderPhraseValues = null;
		double ladderX = 76.6;
		double ladderDeltaY = 12.3;
		double yEps = 0.11;

		Real2 rotCentre = new Real2(400., 200.);
		int verticalCharacterCount = 531;
		String[] verticalValues = {
				"dS of EF-1Δ",
				"dN of Ε-tub",
				"p-distance of rDNA cluster",
				"p-distance of intron within EF-1Δ",
				"dS of Ε-tub",
				};

		assertExtractedTextAndOutputSVG(graphTextFile, outputRoot, 
				phraseListListSize, totalStringValue, horizontalPhraseValues, horizontalPhraseIndexes,
				ladderPhraseIndexes, ladderPhraseValues, ladderX, ladderDeltaY, yEps, 
				rotCentre, verticalCharacterCount, verticalValues);
	}


	@Test
	/* Two large flat panels and a smaller insert.
	 * Many separated chunks of horizontal text
	 * and a large caption
	 */
	@Ignore // FIXME
	public void testMultiPanel1() throws Exception {
		
		File graphTextFile = new File(SVG2XMLFixtures.FIGURE_DIR, "maths.g.7.2.svg");
		String outputRoot = "multipanel1";

		int phraseListListSize = 65;
		String totalStringValue = ""
				+ "1)//0.4//A//1) 2) 3)//2)// //)//r//(//0.3// //e//t//a//r// //n//o//i//t//"
				+ "0.2 0.3 0.4//0 0.1 0.2 0.3 0.4//0 0.1//a//0.2//c//f//i//s//r//e//3)//v//i//D//0.1//0//"
				+ "0 2 4 6 8 10 12 14//B// //y//g//c//n//n//i//l//e//p//u//q//m//e//a//r//S f//"
				+ "0 2 4 6 8 10 12 14//Time (Myr)//"
				+ "Figure 3 Rates-through-time plot. Diversification rates through time resulting from the analysis of 100 "
				+ "phylogenies simulated under a fivefold//increase in diversification rates. The upper plot (A) shows the "
				+ "marginal rates for 1 Myr time categories (line) and the 95% highest posterior//density (error bars). "
				+ "The x-axis represents time (Myr), and the y-axis is the average per-lineage diversification rate (spp/Myr). "
				+ "The insert displays//three examples of marginal distributions of the diversification rate for three "
				+ "points along the phylogenies (indicated by arrows on the rates//through time plot): "
				+ "1) close to the tips (2 Mya), 2) at the point of rate shift (5 Mya), and 3) towards to root of the "
				+ "trees (10 Mya). Note the//bimodal distribution of rates when a rat-shift is found (both the lower "
				+ "and higher rates are sampled). In the lower plot (B), the frequencies of a//rate shift are proportional "
				+ "to the probability of a rate shift in that time frame.//";
		// horizontal phrases
		String[] horizontalPhraseValues = {
				"1) 2) 3)",
				"0.2 0.3 0.4",
				"0 0.1 0.2 0.3 0.4",
				"0 0.1",
				"0 2 4 6 8 10 12 14",
				"0 2 4 6 8 10 12 14",
				"Time (Myr)", 
				"Figure 3 Rates-through-time plot. Diversification rates through time resulting from the analysis of 100 phylogenies simulated under a fivefold"
		};
		int[] horizontalPhraseIndexes = {3, 20, 21, 22, 37, 56, 57, 58};
		
		// ladderValues
		int[] ladderPhraseIndexes = null;
		String[] ladderPhraseValues = null;
		double ladderX = 76.6;
		double ladderDeltaY = 12.3;
		double yEps = 0.11;

		Real2 rotCentre = new Real2(400., 200.);
		int verticalCharacterCount = 922;
		String[] verticalValues = {
				"Sampling",
				"Diversif cation rate (r)",  // FIXME bug
				"frequency",
			};

		assertExtractedTextAndOutputSVG(graphTextFile, outputRoot, 
				phraseListListSize, totalStringValue, horizontalPhraseValues, horizontalPhraseIndexes,
				ladderPhraseIndexes, ladderPhraseValues, ladderX, ladderDeltaY, yEps, 
				rotCentre, verticalCharacterCount, verticalValues);
	}

	@Test
	/* Two large flat panels and a smaller insert.
	 * Many separated chunks of horizontal text
	 * and a large caption
	 */
	@Ignore // FIXME
	public void testCompleteTable() throws Exception {
		
		File graphTextFile = new File(SVG2XMLFixtures.TABLE_DIR, "aa_kranke2000-page2.svg");
		String outputRoot = "rotatedTable";

		int phraseListListSize = 488;
		String totalStringValue = ""
				+ "ANESTH ANALG LETTERS TO THE EDITOR 1005//2000;90:1000 –8//y//t//i//l//8 3 3 3 2 8 6 6 6 8 6 2 8 2 2 6 3 5 8 3 8//"
				+ "t//i//4 0 0 0 0 5 6 6 3 4 3 9 4 5 9 3 0 3 5 0 5//n b//1 1 1 1 0 2 0 1 1 1 1 1 5 9 1 1 1 4 2 1 2//i//"
				+ "P//a//2 0 0 0 0 0 0 0 0 2 0 0 0 1 0 0 0 0 0 0 0//o//. . . . . . . . . . . . . . . . . . . . .//b//J//"
				+ "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0//o//r//p//1 8 8 8 7 8 3 3 1 1 1 5 6 9 5 1 8 3 8 8 8//"
				+ "1 1 1 1 1 0 1 2 2 1 2 2 1 0 2 2 1 1 0 1 0//- - - - - - - - - - - - - - - - - - - - -//0//"
				+ "E E E E E E E E E E E E E E E E E E E E E//1//6 4 4 4 2 7 7 5 7 6 7 6 1 1 6 7 4 1 7 4 7//"
				+ "2 3 3 3 8 1 7 1 2 2 2 7 7 7 7 2 3 4 1 3 1//. . . . . . . . . . . . . . . . . . . . .//"
				+ "7 7 7 7 1 4 1 2 5 7 5 2 1 2 2 5 7 1 4 7 4//://s//t//n//9 5 5 5 4 7 1 9 7 9 7 1 4 8 1 7 5 1 7 5 7//e//i//"
				+ "0 1 1 1 1 0 1 1 1 0 1 2 1 0 2 1 1 1 0 1 0//t//- - - - - - - - - - - - - - - - - - - - -//a//"
				+ "E E E E E E E E E E E E E E E E E E E E E//9//p//1 6 6 6 8 0 3 3 3 1 3 8 9 6 8 3 6 6 0 6 0//"
				+ "9 1 1 1 1 8 6 4 5 9 5 5 1 4 5 5 1 4 8 1 8//. . . . . . . . . . . . . . . . . . . . .//n//"
				+ "3 7 7 7 2 9 3 1 1 3 1 3 8 8 3 1 7 2 9 7 9//s//e//h//p//c//7 2 2 2 1 5 9 6 4 7 4 7 1 6 7 4 2 9 5 2 5//u//a//"
				+ "0 1 1 1 1 0 0 1 1 0 1 1 1 0 1 1 1 0 0 1 0//d - - - - - - - - - - - - - - - - - - - - -//o//a//r//"
				+ "E E E E E E E E E E E E E E E E E E E E E//8//e//1 1 1 1 8 6 2 4 8 1 8 5 6 5 5 8 1 3 6 1 6//G//h//"
				+ "5 8 8 8 1 5 3 6 2 5 2 1 3 9 1 2 8 7 5 8 5//. . . . . . . . . . . . . . . . . . . . .//l//"
				+ "1 3 3 3 1 1 4 4 2 1 2 2 2 1 2 2 3 2 1 3 1//m//a//o//c//r//i//f//6 9 9 9 9 4 7 3 1 6 1 4 9 5 4 1 9 7 4 9 4//"
				+ "t//0 0 0 0 0 0 0 1 1 0 1 1 0 0 1 1 0 0 0 0 0//r//- - - - - - - - - - - - - - - - - - - - -//n//e//"
				+ "E E E E E E E E E E E E E E E E E E E E E//f//7//e//f//1 6 6 6 0 2 3 4 9 1 9 6 7 5 6 9 6 6 2 6 2//"
				+ "d 1 0 0 0 7 6 8 1 6 1 6 8 9 2 8 6 0 8 6 0 6//u//. . . . . . . . . . . . . . . . . . . . .//I//s//"
				+ "4 1 1 1 2 1 2 7 1 4 1 5 3 3 5 1 1 1 1 1 1//l//l//g//i//n//5 7 7 7 7 3 6 0 9 5 9 1 7 4 1 9 7 6 3 7 3//"
				+ "w//i//0 0 0 0 0 0 0 1 0 0 0 1 0 0 1 0 0 0 0 0 0//s//- - - - - - - - - - - - - - - - - - - - -//n//t//i//"
				+ "E E E E E E E E E E E E E E E E E E E E E//6//n//9 6 6 6 6 6 6 6 1 9 1 5 8 1 5 1 6 5 6 6 6//a//e//t//"
				+ "6 4 4 4 3 0 6 9 9 6 9 9 7 8 9 9 4 4 0 4 0//i//. . . . . . . . . . . . . . . . . . . . .//t//b//"
				+ "7 1 1 1 2 1 9 4 5 7 5 6 3 3 6 5 1 7 1 1 1//a//p//O//n//4 6 6 6 6 3 4 7 7 4 7 8 5 3 8 7 6 4 3 6 3//f//"
				+ "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0//f//o//- - - - - - - - - - - - - - - - - - - - -//o//"
				+ "E E E E E E E E E E E E E E E E E E E E E//5//s//r//3 7 7 7 3 6 8 6 9 3 9 8 3 3 8 9 7 7 6 7 6//e//e//"
				+ "5 1 1 1 0 0 5 4 0 5 0 3 9 0 3 0 1 6 0 1 0//. . . . . . . . . . . . . . . . . . . . .//i//b//"
				+ "t 9 9 9 9 7 4 1 1 9 9 9 3 1 3 3 9 9 1 4 9 4//i//m//l//i//u//3 4 4 4 5 3 3 5 5 3 5 6 4 2 6 5 4 3 3 4 3//n//b//"
				+ "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0//l//a//- - - - - - - - - - - - - - - - - - - - -//a//"
				+ "E E E E E E E E E E E E E E E E E E E E E//b//c 4//i//5 8 8 8 5 3 2 5 4 5 4 2 7 4 2 4 8 2 3 8 3//o//t//"
				+ "4 3 3 3 0 4 1 6 5 4 5 1 8 5 1 5 3 9 4 3 4//r . . . . . . . . . . . . . . . . . . . . .//n//"
				+ "7 2 2 2 6 8 1 1 5 7 5 6 4 1 6 5 2 1 8 2 8//e//P//d//i//d//2 3 3 3 4 3 3 4 3 2 3 4 3 2 4 3 3 2 3 3 3//e//e//"
				+ "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0//h//t - - - - - - - - - - - - - - - - - - - - -//t//"
				+ "E E E E E E E E E E E E E E E E E E E E E//a//3//s//l//9 8 8 8 8 0 0 7 5 9 5 6 4 7 6 5 8 1 0 8 0//p//"
				+ "3 1 1 1 1 4 9 1 1 3 1 5 4 6 5 1 1 0 4 1 4//u//. . . . . . . . . . . . . . . . . . . . .//u//"
				+ "c 3 2 2 2 1 8 2 6 1 3 1 3 5 4 3 1 2 1 8 2 8//o//l//r//a//g//2 3 3 3 5 3 3 3 3 2 3 3 2 2 3 3 3 2 3 3 3//l//C//"
				+ "l//0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0//- - - - - - - - - - - - - - - - - - - - -//a//"
				+ "d E E E E E E E E E E E E E E E E E E E E E//2//n//5 5 5 5 6 2 4 3 2 5 2 2 3 8 2 2 5 1 2 5 2//n i//"
				+ "9 4 4 4 5 3 1 9 3 9 3 2 2 3 2 3 4 0 3 4 3//. . . . . . . . . . . . . . . . . . . . .//a//t//"
				+ "7 5 5 5 3 3 2 5 6 7 6 5 2 7 5 6 5 2 3 5 3//a//s//h//t//n//2 3 3 3 7 4 4 3 3 2 3 2 2 2 2 3 3 2 4 3 4//y//o//"
				+ "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0//t//i//- - - - - - - - - - - - - - - - - - - - -//i//t l//"
				+ "i E E E E E E E E E E E E E E E E E E E E E//1//a//1 4 4 4 3 5 1 6 9 1 9 2 7 4 2 9 4 6 5 4 5//b//c//"
				+ "6 3 3 3 2 6 8 2 7 6 7 2 3 8 2 7 3 0 6 3 6//a//. . . . . . . . . . . . . . . . . . . . .//i//b//l//"
				+ "7 2 2 2 8 3 2 9 5 7 5 1 2 4 1 5 2 1 3 2 3//o//b//r//u P//2 5 5 5 0 6 6 4 4 2 4 3 3 3 3 4 5 4 6 5 6//P//"
				+ "0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0//- - - - - - - - - - - - - - - - - - - - -//"
				+ "E E E E E E E E E E E E E E E E E E E E E//0//d//8 9 9 9 9 2 9 1 2 8 2 4 8 1 4 2 9 8 2 9 2//e//"
				+ "6 4 4 4 5 7 0 5 8 6 8 4 7 4 4 8 4 3 7 4 7//. . . . . . . . . . . . . . . . . . . . .//t//"
				+ "1 5 5 5 2 4 2 7 2 1 2 1 2 7 1 2 5 6 4 5 4//a//g//i//t//5//s//4//6//e/////s//3//v//p//n//u//I//"
				+ "o//r//5//e//g//4//5//h/////d//t//3//e//t//r//n//i//o//p//0 0 0 5 0 2 5 0 0 5 5 0 0//e//"
				+ "3 3 3 4 4 2 2 3 2 2 3 3 3//p//4//r/// / / / / / / / / / / / ///u//s 2 2 2 3 2 1 1 2 2 2 3 3 3//t//o//"
				+ "n//r//e//i//G//t//0 0 0 5 0 0 2 5 0 0 3 6 5 0 0 0 0 0//a//3 3 3 4 5 4 2 2 3 2 2 2 3 3 3 5 3 5//"
				+ "3//r//p/// / / / / / / / / / / / / / / / / ///e//2 2 2 3 4 2 1 1 2 2 2 2 3 2 2 4 3 4//l//l//p//"
				+ "a/////e//e//5 0 0 0 5 0 0 2 5 5 5 0 3 0 0 5 0 0 0 0 0//h//2 3 3 3 4 5 4 2 2 2 2 2 2 3 2 2 3 3 5 3 5//"
				+ "h//2//c//c/// / / / / / / / / / / / / / / / / / / / ///a//a//2 2 2 2 3 4 2 1 2 2 2 2 2 2 2 2 3 2 3 3 4//"
				+ "d//d//a//a//e//e//5 0 0 0 5 0 0 2 5 5 5 0 4 0 0 5 0 0 0 0 0//H//2 3 3 3 4 5 4 2 2 2 2 2 2 3 2 2 3 3 5 3 5//"
				+ "1//H/// / / / / / / / / / / / / / / / / / / / ///2 2 2 2 3 4 2 1 1 2 2 2 2 2 2 2 3 3 4 3 4//f//o//; ;//"
				+ "d d d d//e//9 9//: : : : : : : : : : : ://c n n n n//://9 9//2 2 3 3 3 3 3 4 4 4 5 5//a a a a//5//: ://"
				+ "9 9//n//4 4 4 4 4 4 4 4 4 4 4 4//c c c c//8 ; ; ; ; ; ; ; ; ; ; ; ;//1 1//1 1//;//e//0//S S S S//"
				+ "5 5 6 6 6 6 6 7 7 7 8 8//8 8//7//; ; l l//7//9 9 9 9 9 9 9 9 9 9 9 9//l l l l//d//9//8 8//9 1 4 7//"
				+ "o o//i//e 9 9 9 9 9 9 9 9 9 9 9 9//o o o o//i i//9//9 9//4 1 2 5//i i i i//c//1 1 1 1 1 1 1 1 1 1 1 1//"
				+ "c//s s//1//s s s s//9 9//7 – 2 6//e e//n//e e e e//n//1 1//– 7 – –//h h h h h h h h h h h h//e//h h//"
				+ "g//I//t t t t t t t t t t t t//6 6 0 3//h h h h//r//l t t//9//t t t t//s s s s s s s s s s s s//h h//"
				+ "4 1 2 5 9 2//s s//e//a//t t//7//s s s s//e e e e e e e e e e e e//.//f//7 1 2 6 9 3//e e//s s//"
				+ "n//: : : : 5 3//e e e e//a a a a a a a a a a a a//e//1 7 9 2 0 6 4 4 0 2 7 3 4 6 4//a a//e e//"
				+ "1 1 2 2 6 –//a a a a//n n n n n n n n n n n n//A 1 8 9 9 5 1 6 1 1 7 9 2 5 4//R//n n//a a//4 4 4 4 – 6//"
				+ "n n n n//; ; ; ; 9 3 3 3 8 8 1 6 – – 2 4 8 1 5//n n A A A A A A A A A A A A//2 7//e//A A//h//"
				+ "7 7 8 8 – – – – – 3 – – 5 9 – – – – –//A A A A//l//t 6 3//J J J J J J J J J J J J//"
				+ "9 9 9 9 3 A 7 A 0 7 2 – 0 0 9 2 3 9 0 3 1 : ://J J//s//a a a a//b 9 9 9 9 1 8 9 8 5 5 1 6 0 2 7 8 2 5 4 6 6//"
				+ "e//J J//t t t t//n n n n n n n n n n n n r r//1 1 1 1 9 3 3 3 8 3 1 6 1 1 2 4 8 1 5 1 1//a//c c c c n//"
				+ "a a a a a a a a a a a a//r r u u//T A A A A A B B C C C C C C C C C C C C E E//";
		// horizontal phrases
		String[] horizontalPhraseValues = {
				"ANESTH ANALG LETTERS TO THE EDITOR 1005",
				"2000;90:1000 –8",
		};
		int[] horizontalPhraseIndexes = {0, 1};
		
		// ladderValues
		int[] ladderPhraseIndexes = null;
		String[] ladderPhraseValues = null;
		double ladderX = 76.6;
		double ladderDeltaY = 12.3;
		double yEps = 0.11;

		Real2 rotCentre = new Real2(450., 400.);
		int verticalCharacterCount = 3197 /*3150*/;
		String[] verticalValues = {
				"Table 1. Incidence of Headache per Group in the Investigated Publications and Calculated Probabilities of Obtaining Identical Groups",
				"Joint",
				"Headache/all patients reported groups Probability that in all groups the identical number of n patients will suffer from headache n patients:",
				"probability",
				"Reference 1 2 3 4 5 6 0 1 2 3 4 5 6 7 8 9 10 P",
				"Acta Anaesthesiol Scand 2/25 2/25 1.68E-02 7.61E-02 7.95E-02 3.39E-02 7.45E-03 9.53E-04 7.69E-05 4.11E-06 1.51E-07 3.91E-09 7.26E-11 0.2148",
				"1997;41:746–749",
				"Acta Anaesthesiol Scand 2/30 2/30 2/30 2/30 5.49E-05 2.34E-03 5.45E-03 2.18E-03 2.38E-04 9.17E-06 1.46E-07 1.06E-09 3.81E-12 7.16E-15 7.34E-18 0.0103",
				"1997;41:1167–1170",
				"Acta Anaesthesiol Scand 2/30 2/30 2/30 2/30 5.49E-05 2.34E-03 5.45E-03 2.18E-03 2.38E-04 9.17E-06 1.46E-07 1.06E-09 3.81E-12 7.16E-15 7.34E-18 0.0103",
				"1998;42:220–224",
				"Acta Anaesthesiol Scand 2/30 2/30 2/30 2/30 5.49E-05 2.34E-03 5.45E-03 2.18E-03 2.38E-04 9.17E-06 1.46E-07 1.06E-09 3.81E-12 7.16E-15 7.34E-18 0.0103",
				"1998;42:653–657",
				"Anesth Analg 1997;85: 3/45 3/45 3/45 3/45 3/45 3/45 2.59E-10 8.23E-07 3.56E-05 1.18E-04 6.05E-05 7.03E-06 2.36E-07 2.70E-09 1.18E-11 2.18E-14 1.82E-17 0.0002",
				"913–917",
				"Br J Anaesth 1998;81: 4/50 4/50 4/50 4.72E-06 3.65E-04 3.32E-03 8.40E-03 8.43E-03 4.06E-03 1.06E-03 1.62E-04 1.56E-05 9.80E-07 4.17E-08 0.0258",
				"387–389",
				"Br J Anaesth 1998;81: 2/40 2/40 2/40 2/40 2.09E-06 2.81E-04 2.14E-03 2.90E-03 1.12E-03 1.58E-04 9.66E-06 2.83E-07 4.32E-09 3.63E-11 1.77E-13 0.0066",
				"390–392",
				"Can J Anaesth 1995;42: 1/22 1/22 1/22 1/22 7.51E-04 9.26E-03 5.93E-03 6.17E-04 1.65E-05 1.46E-07 4.96E-10 7.14E-13 4.64E-16 1.43E-19 2.15E-23 0.0166",
				"387–390",
				"Can J Anaesth 1995;42: 1/25 2/25 1/25 1/25 2.82E-04 5.79E-03 6.32E-03 1.15E-03 5.54E-05 9.09E-07 5.91E-09 1.69E-11 2.28E-14 1.53E-17 5.27E-21 0.0136",
				"852–856",
				"Can J Anaesth 1996;43: 2/25 2/25 1.68E-02 7.61E-02 7.95E-02 3.39E-02 7.45E-03 9.53E-04 7.69E-05 4.11E-06 1.51E-07 3.91E-09 7.26E-11 0.2148",
				"35–38",
				"Can J Anaesth 1996;43: 2/25 2/25 2/30 2/30 2.82E-04 5.79E-03 6.32E-03 1.15E-03 5.54E-05 9.09E-07 5.91E-09 1.69E-11 2.28E-14 1.53E-17 5.27E-21 0.0136",
				"110–114",
				"Can J Anaesth 1996;43: 2/20 2/20 2/20 2/20 1.44E-03 1.22E-02 5.22E-03 3.56E-04 6.12E-06 3.38E-08 6.95E-11 5.86E-14 2.15E-17 3.58E-21 2.76E-25 0.0192",
				"660–664",
				"Can J Anaesth 1996;43: 2/24 2/23 2/23 2.78E-03 2.37E-02 2.23E-02 5.44E-03 4.87E-04 1.93E-05 3.78E-07 3.97E-09 2.36E-11 8.19E-14 1.71E-16 0.0548",
				"1095–1099",
				"Can J Anaesth 1996;43: 2/30 2/30 7.41E-03 4.84E-02 7.38E-02 4.67E-02 1.54E-02 3.03E-03 3.81E-04 3.25E-05 1.95E-06 8.46E-08 2.71E-09 0.1952",
				"1229–1232",
				"Can J Anaesth 1997;44: 2/20 2/20 2/26 2/25 1.44E-03 1.22E-02 5.22E-03 3.56E-04 6.12E-06 3.38E-08 6.95E-11 5.86E-14 2.15E-17 3.58E-21 2.76E-25 0.0192",
				"273–277",
				"Can J Anaesth 1997;44: 2/25 2/25 3/35 3/35 2.82E-04 5.79E-03 6.32E-03 1.15E-03 5.54E-05 9.09E-07 5.91E-09 1.69E-11 2.28E-14 1.53E-17 5.27E-21 0.0136",
				"489–493",
				"Can J Anaesth 1997;44: 3/30 3/30 2/30 3/30 5.49E-05 2.34E-03 5.45E-03 2.18E-03 2.38E-04 9.17E-06 1.46E-07 1.06E-09 3.81E-12 7.16E-15 7.34E-18 0.0103",
				"820–824",
				"Can J Anaesth 1998;45: 3/30 2/30 2/30 6.38E-04 1.06E-02 2.01E-02 1.01E-02 1.92E-03 1.67E-04 7.45E-06 1.86E-07 2.73E-09 2.46E-11 1.41E-13 0.0435",
				"153–156",
				"Can J Anaesth 1998;45: 4/50 3/50 4/50 4.72E-06 3.65E-04 3.32E-03 8.40E-03 8.43E-03 4.06E-03 1.06E-03 1.62E-04 1.56E-05 9.80E-07 4.17E-08 0.0258",
				"541–544",
				"Eur J Anaesthesiol 1999; 3/30 3/30 3/30 3/30 5.49E-05 2.34E-03 5.45E-03 2.18E-03 2.38E-04 9.17E-06 1.46E-07 1.06E-09 3.81E-12 7.16E-15 7.34E-18 0.0103",
				"16:62–65",
				"Eur J Anaesthesiol 1999; 4/50 4/50 4/50 4.72E-06 3.65E-04 3.32E-03 8.40E-03 8.43E-03 4.06E-03 1.06E-03 1.62E-04 1.56E-05 9.80E-07 4.17E-08 0.0258",
				"16:376–379",
				};

		assertExtractedTextAndOutputSVG(graphTextFile, outputRoot, 
				phraseListListSize, totalStringValue, horizontalPhraseValues, horizontalPhraseIndexes,
				ladderPhraseIndexes, ladderPhraseValues, ladderX, ladderDeltaY, yEps, 
				rotCentre, verticalCharacterCount, verticalValues);
	}

	@Test
	/* Phylogenetic tree with vertical text and diferent fonts
	 */
	@Ignore // FIXME
	public void testPhyloTree() throws Exception {
		
		File graphTextFile = new File(SVG2XMLFixtures.FONT_DIR, "image.g.3.2.svg");
		String outputRoot = "phylotree";

		int phraseListListSize = 246;
		String totalStringValue = ""
				+ "-//p//Luscinia  //(Muscicapidae)//a//*//Ficedula//c//*//i//c a//(Turdidae)//Turdus//s//*//e//u//"
				+ "(Mimidae)//Mimus//d//i//*//M o//Sturnus (Sturnidae)//*//0.5//(Troglodytidae)//Certhi-//Troglodytes//"
				+ "*//oidea//Sitta (Sittidae)//Regulus//(Regulidae)//Zosterops(Zosteropidae)//0.77//*//0.66//(Timaliidae)//"
				+ "Leiothrix//a//0.93//-//d//Phylloscopus(Phylloscopidae)//i a//i//r//v//e//l//Pycnonotus//(Pycnonotidae)//"
				+ "e//d//y//i//*//s//(Donacobiidae)//Donacobius//S o//s//*//(Acrocephalidae)//Acrocephalus//a//0.73//0.83//"
				+ "P//Hirundo//(Hirundinidae)//(Paridae)//Parus//*//Icterus (Icteridae)//*//Dendroica//(Parulidae)//-//*//r//"
				+ "(Emberizidae)//Emberiza//e//*//a//s//(Fringillidae)//Serinus//e//*//s//0.71//d//a//i//Motacilla//"
				+ "(Motacillidae)//*//P o//s//0.91//Lonchura//(Estrildidae)//e//(Nectariniidae)//Nectarinia//n//i//*//"
				+ "(Promeropidae)//Promerops//c//Petroica//s//(Petroicidae)//*//Eopsaltria//O//Ptiloris//*//"
				+ "Paradisaea (Paradisaeidae)//*//'//Manucodia//a//*//e//Pica//*//d//*//i//(Corvidae)//Corvus//*//o//"
				+ "Cyanocorax//v//*//r//(Dicruridae)//o Dicrurus//C//Rhipidura//(Rhipiduridae)// //*//e//Vireo//r//0.72 a//"
				+ "(Vireonidae)//5//*//o//Cyclarhis//4//*//c//*//'//(Oriolidae)//Oriolus//3//c//Coracina//(Campephagidae)//"
				+ "0.78//Toxorhamphus//(Melanocharitidae)//0.84//*//6//(Cnemophilidae)//Cnemophilus//b 0.94//"
				+ "Philesturnus (Callaeidae)//Orthonyx//(Orthonychidae)//*//(Pomatostomidae)//Pomatostomus//0.5//"
				+ "Lichenostomus (Meliphagidae)//b *//2//Amytornis #//(Maluridae)//*//(Acanthizidae)//Gerygone//"
				+ "Ailuroedus//*//(Ptilonorhynchidae)//*//Sericulus//a//2//Menura//(Menuridae)//Tyrannus//s//*//e//"
				+ "Myiarchus (Tyrannidae)//*//*//n//Camptostoma//*//i//(Tityridae)//c//Onychorhynchus//*//1 b//s//"
				+ "(Pipridae)//Manacus//*//*//o//Hypocnemis//b//*//(Thamnophilidae)//*//Phlegopsis//1 a//u//(Pittidae)//"
				+ "Pitta//S//Acanthisitta (Acanthisittidae)//0.07//a b//1 2//3 5//ambiguous//4 6//UVS          VS//–//"
				+ ", VS          UVS//,//";
		// horizontal phrases
		String[] horizontalPhraseValues = {
				"Luscinia",
				"(Muscicapidae)",
				"Ficedula",
				"Phylloscopus(Phylloscopidae)",
				"Icterus (Icteridae)",
				"Philesturnus (Callaeidae) ",
				"Acanthisitta (Acanthisittidae)"
				
		};
		int[] horizontalPhraseIndexes = {2, 3, 6, 44, 75, 178, 235};
		
		// ladderValues
		int[] ladderPhraseIndexes = null;
		String[] ladderPhraseValues = null;
		double ladderX = 76.6;
		double ladderDeltaY = 12.3;
		double yEps = 0.11;

		Real2 rotCentre = new Real2(400., 200.);
		int verticalCharacterCount = 1381;
		String[] verticalValues = {
				"Suboscines",
				"Oscines",
				"'core Corvoidea' Passer- Sylvi- Muscicap-",
				"oidea oidea oidea",
				"Passerida",
				};

		assertExtractedTextAndOutputSVG(graphTextFile, outputRoot, 
				phraseListListSize, totalStringValue, horizontalPhraseValues, horizontalPhraseIndexes,
				ladderPhraseIndexes, ladderPhraseValues, ladderX, ladderDeltaY, yEps, 
				rotCentre, verticalCharacterCount, verticalValues);
	}


	@Test
	/** not yest successful - may abandon and rewrite
	 * 
	 */
	public void testRotateTextLines() {

		TextStructurer textStructurer = 
				TextStructurer.createTextStructurerWithSortedLines(SVG2XMLFixtures.RAWWORDS_SVG);
		textStructurer.rotateAsBlock(new Real2(100., 100.), new Angle(Math.PI / 2 ));
		textStructurer.formatTextLineTransforms(5);
		List<TextLine> textLineList = textStructurer.getTextLineList();
		SVGG g = new SVGG();
		for (TextLine textLine : textLineList) {
			for (AbstractCMElement character : textLine.getSVGTextCharacters()) {
				g.appendChild(character.copy());
			}
		}
		File rotatedFile = new File(OUTPUT_TEXT_DIR, "textLinesRotate.svg");
		SVGSVG.wrapAndWriteAsSVG(g, rotatedFile);

		textStructurer = 
				TextStructurer.createTextStructurerWithSortedLines(rotatedFile);
		textLineList = textStructurer.getTextLineList();
		LOG.trace("TXT>"+textLineList.size());
		for (TextLine textLine : textLineList) {
			LOG.trace("LINE: "+textLine);
		}
		textStructurer.rotateAsBlock(new Real2(100., 100.), new Angle(Math.PI / 2 ));
		textStructurer.formatTextLineTransforms(5);
		textLineList = textStructurer.getTextLineList();
		g = new SVGG();
		for (TextLine textLine : textLineList) {
			for (AbstractCMElement character : textLine.getSVGTextCharacters()) {
				g.appendChild(character.copy());
			}
		}
		File outputFile1 = new File(OUTPUT_TEXT_DIR, "textLinesRotate1.svg");
		SVGSVG.wrapAndWriteAsSVG(g, outputFile1);
	}

// ===========================================
	
	private void assertLadder(TextChunk phraseListList, int[] phraseIndexes, String[] phraseValues, double xValue,
			double deltaY, double yEps) {
		Phrase phrase;
		if (phraseIndexes != null) {
			for (int i = 0; i < phraseIndexes.length; i++) {
				phrase = phraseListList.get(phraseIndexes[i]).get(0);
				Assert.assertEquals(phraseValues[i], phrase.getStringValue());
				Real2 xy = phrase.get(0).getCentrePointOfLastCharacter().format(1);
				Assert.assertEquals(xValue, xy.getX(), 0.0);
				if (i > 0) {
					Real2 lastXY = phraseListList.get(phraseIndexes[i - 1]).get(0).get(0).getCentrePointOfLastCharacter().format(1);
					Assert.assertEquals(deltaY, xy.subtract(lastXY).getY(), yEps);
				}
			}
		}
	}

	private void assertExtractedTextAndOutputSVG(File graphTextFile, String outputRoot, 
			int phraseListListSize, String totalStringValue, String[] horizontalPhraseValues, int[] horizontalPhraseIndexes, 
			int[] ladderPhraseIndexes, String[] ladderPhraseValues, double ladderX, double ladderDeltaY, double yEps, 
			Real2 rotCentre, int verticalCharacterCount, String[] verticalValues) {
		
		File outDir = new File(OUTPUT_TEXT_DIR, outputRoot);
		outDir.mkdirs();
		File outFile1 = new File(outDir, "rotatedVerticalText.svg");
		File outfile2 = new File(outDir, "phrasesRotate.svg");
		Assert.assertTrue("graphTextFile exists", graphTextFile.exists());
		TextChunk phraseListList = getUnrotatedPhrases(graphTextFile, phraseListListSize, totalStringValue);
		Assert.assertNotNull("phraseListList not null", phraseListList);

		assertHorizontalPhrases(phraseListList, horizontalPhraseValues, horizontalPhraseIndexes);
		// y-values in ladder with 12.3/12.4 delta
		assertLadder(phraseListList, ladderPhraseIndexes, ladderPhraseValues, ladderX, ladderDeltaY, yEps);
		
		// now process rotated text - this is common y-axis text orientation
		// rotation centre is arbitrary, angle is clockwise
		TextStructurer textStructurer = TextStructurer.createTextStructurerWithSortedLines(graphTextFile);
		SVGG rotatedVerticalText = textStructurer.createChunkFromVerticalText(rotCentre, new Angle(-1.0 * Math.PI / 2));
		Assert.assertEquals(verticalCharacterCount, SVGText.extractSelfAndDescendantTexts(rotatedVerticalText).size());

		SVGSVG.wrapAndWriteAsSVG(rotatedVerticalText, outFile1);
		
		// reread and analyze the horizontal (previously vertical) lines;
		TextStructurer textStructurer2 = TextStructurer.createTextStructurerWithSortedLines(rotatedVerticalText);
		phraseListList = textStructurer2.getTextChunkList().getLastTextChunk();
		phraseListList.format(1);
		phraseListList.getStringValue(); // computes if not already known
		for (int i = 0; i < phraseListList.size(); i++) {
			PhraseChunk phraseList = phraseListList.get(i);
			LOG.trace(">"+i+">"+phraseList.getBoundingBox()+"/"+phraseList.getStringValue());
		}

//		Assert.assertEquals(verticalValues.length, phraseListList.size());
//		for (int i = 0; i < verticalValues.length; i++) {
//			Assert.assertEquals("vert "+i, verticalValues[i].trim(), phraseListList.get(i).getStringValue().trim());
//		}
		SVGG gg = new SVGG();
		gg.appendChild(phraseListList.copy());
		SVGSVG.wrapAndWriteAsSVG(gg, outfile2);
	}

	/** can use this to print out the expected values 
	 * 
	 * @param phraseListList
	 * @param horizontalPhraseValues
	 * @param horizontalPhraseIndexes
	 */
	private void assertHorizontalPhrases(TextChunk phraseListList, String[] horizontalPhraseValues,
			int[] horizontalPhraseIndexes) {
		LOG.trace(phraseListList.size());
		for (int i = 0; i < phraseListList.size(); i++) {
			LOG.trace(">"+i+">"+phraseListList.get(i).getStringValue());
		}
		// horizontal phrases
		for (int i = 0; i < horizontalPhraseValues.length; i++) {
			Assert.assertEquals(">"+i+">", horizontalPhraseValues[i].trim(), phraseListList.get(horizontalPhraseIndexes[i]).getStringValue().trim());
		}
	}

	private TextChunk getUnrotatedPhrases(File graphTextFile, int phraseListListSize, String totalStringValue) {
		TextChunk phraseListList;
		TextStructurer textStructurer = TextStructurer.createTextStructurerWithSortedLines(graphTextFile);
		phraseListList = textStructurer.getTextChunkList().getLastTextChunk();
		Assert.assertNotNull("phraseListList not null", phraseListList);
		phraseListList.getStringValue(); // computes if not already known
		Assert.assertEquals("total String value",  totalStringValue, phraseListList.getStringValue());
		Assert.assertEquals(phraseListListSize, phraseListList.size());
		return phraseListList;
	}


}
