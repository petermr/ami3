package org.contentmine.ami.plugins;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.plugins.CooccurrenceAnalyzer;
import org.contentmine.ami.plugins.EntityAnalyzer;
import org.contentmine.ami.plugins.OccurrenceAnalyzer;
import org.contentmine.eucl.euclid.IntMatrix;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.svg.SVGSVG;
import org.junit.Ignore;
import org.junit.Test;

public class CooccurrenceAnalyzerTest {
	private static final Logger LOG = Logger.getLogger(CooccurrenceAnalyzerTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
//	@Ignore
	public void testColorMatrix() throws IOException {
		String matrixS = "{25,18}\n" 
        + "(23,8,8,11,6,4,2,5,1,1,2,3,3,2,2,0,2,1)\n"
        + "(21,6,6,8,6,4,4,7,3,2,3,3,2,3,0,0,2,1)\n"
        + "(6,8,2,3,0,5,0,1,0,2,0,0,1,1,1,0,1,0)\n"
        + "(15,5,6,4,5,3,2,3,0,2,2,0,1,1,1,0,1,1)\n"
        + "(13,7,5,7,1,4,0,4,1,2,1,1,2,2,1,0,2,1)\n"
        + "(8,8,5,5,0,3,0,2,0,2,0,0,1,1,2,1,1,0)\n"
        + "(35,5,6,9,10,4,3,7,2,2,1,3,2,2,0,0,2,1)\n"
        + "(13,5,5,5,4,3,1,6,1,2,1,1,1,3,2,0,1,0)\n"
        + "(6,7,5,3,0,5,0,2,0,1,1,0,1,1,0,0,1,0)\n"
        + "(4,2,2,3,1,2,0,1,1,0,0,0,1,1,0,0,1,0)\n"
        + "(9,1,4,2,4,1,2,8,5,1,2,0,0,1,0,0,0,0)\n"
        + "(13,4,2,9,4,2,1,3,1,1,1,2,1,1,1,0,1,0)\n"
        + "(3,3,1,0,0,2,0,1,1,2,0,0,0,0,0,0,0,1)\n"
        + "(5,1,2,1,3,1,2,3,1,1,1,0,0,0,0,0,0,1)\n"
        + "(10,6,11,3,1,3,0,3,0,0,0,2,2,2,2,0,1,1)\n"
        + "(4,1,2,1,0,1,0,3,1,1,0,0,0,0,1,0,0,0)\n"
        + "(8,2,2,1,2,1,1,5,1,1,1,1,0,2,1,1,0,1)\n"
        + "(0,5,6,0,1,3,0,0,0,0,0,0,1,0,0,1,0,1)\n"
        + "(8,2,2,2,1,2,1,4,2,1,0,0,0,2,1,0,1,0)\n"
        + "(1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)\n"
        + "(5,2,1,2,1,2,1,1,1,0,0,0,1,1,1,0,1,0)\n"
        + "(2,0,1,0,3,0,2,1,1,0,1,0,0,2,0,0,0,0)\n"
        + "(10,1,2,3,1,0,1,2,0,0,0,1,0,2,1,0,0,1)\n"
        + "(2,0,1,2,0,0,0,0,0,0,0,1,0,0,0,0,1,0)\n"
        + "(1,2,2,1,0,2,0,0,0,1,0,0,0,0,0,0,0,0)\n"
        ;
		File projectDir = new File("target/cooccurrence/");
		IntMatrix cooccurrenceMatrix = IntMatrix.readMatrix(matrixS);
		EntityAnalyzer entityAnalyzer = EntityAnalyzer.createEntityAnalyzer(projectDir);
		CooccurrenceAnalyzer cooccurrenceAnalyzer = new CooccurrenceAnalyzer(entityAnalyzer);
		cooccurrenceAnalyzer.setCooccurrenceMatrix(cooccurrenceMatrix);
		SVGSVG svg = cooccurrenceAnalyzer.createSVG();
		XMLUtil.debug(svg, new File(projectDir, "svgColorMatrix.svg"), 1);
	}
	
	@Test
	public void testDrawCooccurrenceMap() throws IOException {
		String rowName = "photosynth";
		String rowMultisetString = "["                 // 25
			+ "chlorophyll x 746, thylakoid x 564, ATP x 400, leaves x 309, Photosynthesis x 295,"
			+ " leaf x 290, Chloroplast x 239, Photosystem x 198, CCM x 143, stroma x 137,"
			+ " RuBisCO x 90, photorespiration x 84, CO2 x 68, CAM x 52, Calvin-Benson cycle x 51,"
			+ " Carbon dioxide x 29, photosynthetic membrane x 23, Carbon fixation x 23, antenna protein x 21, photophosphorylation x 10,"
			+ " Carbon concentrating mechanism x 10, Calvin-Benson cyclez x 10, Calvin-Benson cyclex x 9, Sunlight x 7, Light-dependent reaction x 3"
			+ "]";
		
		OccurrenceAnalyzer rowAnalyzer = new OccurrenceAnalyzer();
		rowAnalyzer.createFromStrings(rowName, rowMultisetString);
		LOG.debug(rowAnalyzer.getOrCreateEntriesSortedByImportance());
		
		String colName = "plantparts";
		String colMultisetString = "["               // 24
			+ "leaf x 290, seed x 149, root x 131, branch x 75, fruit x 53,"
			+ " resin x 52, stem x 47, shoot x 32, flower x 24, needle x 19,"
			+ " whole plant x 16, wood x 14, spike x 13, tuber x 13, gum x 13,"
			+ " fresh leaf x 8, pericarp x 7, latex x 6, aerial parts x 6, stalk x 6,"
			+ " leaflet x 5, whole plantx x 3, petal x 3, peel x 2"
			+ "]";
		OccurrenceAnalyzer colAnalyzer = new OccurrenceAnalyzer();
		colAnalyzer.createFromStrings(colName, colMultisetString);
				
		String matrixS = ""
			+ "{25,24}\n"
			+ "(88,58,48,28,18,16,30,12,14,10,10,6,8,4,6,5,3,3,5,2,3,1,3,2)\n"
			+ "(78,40,32,22,16,17,18,12,10,10,8,6,8,8,4,3,3,1,1,2,3,3,1,0)\n"
			+ "(66,38,34,16,10,11,20,6,4,6,6,2,6,6,2,1,1,3,5,4,3,3,1,0)\n"
			+ "(88,48,36,20,16,18,16,14,12,10,8,6,8,4,2,5,3,1,5,4,1,3,3,2)\n"
			+ "(70,40,34,16,16,11,16,8,8,6,8,4,8,8,2,5,3,3,5,2,1,3,1,2)\n"
			+ "(128,46,40,18,18,10,24,18,10,8,10,6,8,6,4,5,3,3,3,4,3,3,3,2)\n"
			+ "(64,34,20,20,6,9,18,8,2,6,6,4,6,8,2,5,1,3,3,2,3,3,1,0)\n"
			+ "(42,20,14,12,8,7,10,4,6,6,4,4,2,4,6,1,1,3,3,2,1,3,1,0)\n"
			+ "(18,10,8,4,4,2,6,2,2,2,4,2,2,6,2,1,1,1,1,2,1,3,1,0)\n"
			+ "(36,14,12,4,4,6,8,8,4,2,8,2,2,6,2,1,1,1,1,2,1,1,1,0)\n"
			+ "(20,8,6,2,2,5,8,2,2,4,4,2,4,6,2,1,1,1,1,2,1,3,1,0)\n"
			+ "(30,18,10,6,6,4,6,4,4,2,6,2,2,6,2,1,1,3,1,2,1,3,3,0)\n"
			+ "(21,15,17,5,7,1,1,1,3,3,3,3,3,5,1,3,3,1,1,1,1,1,1,0)\n"
			+ "(14,6,12,4,4,2,6,2,2,2,4,2,2,4,2,1,1,1,1,2,1,1,1,0)\n"
			+ "(8,6,4,4,4,4,2,2,4,2,4,2,2,8,2,1,1,1,1,2,1,3,1,0)\n"
			+ "(9,5,3,1,3,1,1,3,1,1,3,1,1,1,1,1,1,1,1,1,1,1,1,0)\n"
			+ "(8,6,4,2,2,4,2,2,2,2,2,2,2,2,2,1,1,1,1,2,1,1,1,0)\n"
			+ "(2,2,6,2,2,2,2,2,2,2,2,2,4,2,2,1,1,1,1,2,1,1,1,0)\n"
			+ "(9,9,7,5,1,3,3,1,1,3,1,1,1,1,1,1,1,1,1,1,1,1,1,0)\n"
			+ "(6,4,4,0,0,0,0,2,2,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0)\n"
			+ "(1,3,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0)\n"
			+ "(4,2,2,2,2,0,0,0,2,0,2,0,0,4,0,0,0,0,0,0,0,0,0,0)\n"
			+ "(3,3,3,3,3,1,1,1,3,1,3,1,1,3,1,1,1,1,1,1,1,1,1,0)\n"
			+ "(5,3,5,1,3,1,1,1,1,1,3,1,1,3,1,1,3,1,1,1,1,1,1,0)\n"
			+ "(3,1,1,3,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0)"
			;
		IntMatrix cooccurrenceMatrix = IntMatrix.readMatrix(matrixS);
		SVGSVG svg = new CooccurrenceAnalyzer(null).createSVG(cooccurrenceMatrix, rowAnalyzer, colAnalyzer);
		XMLUtil.debug(svg, new File("target/debug/svgColorMatrix1.svg"), 1);

	}
	
	

}
