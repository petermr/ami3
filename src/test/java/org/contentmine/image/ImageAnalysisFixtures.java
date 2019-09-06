package org.contentmine.image;

import java.awt.Point;
import java.io.File;
import java.util.Arrays;

import org.contentmine.image.pixel.Pixel;
import org.contentmine.image.pixel.PixelIsland;
import org.contentmine.image.pixel.PixelList;

public class ImageAnalysisFixtures {

	public final static File TEST_RESOURCE_DIR = new File("src/test/resources");
	public final static File TEST_CONTENTMINE_DIR = new File(TEST_RESOURCE_DIR, "org/contentmine");
	public final static File TEST_IMAGE_DIR = new File(TEST_CONTENTMINE_DIR, "image");
	public final static File TEST_DIAGRAM_DIR = new File(TEST_IMAGE_DIR, "diagrams");
	public final static File TEST_DIAGRAM_FOREST_DIR = new File(TEST_DIAGRAM_DIR, "forest");
	public final static File TEST_DIAGRAM_PLOT_DIR = new File(TEST_DIAGRAM_DIR, "plot");
	public final static File TEST_DIAGRAM_PLOT_MISC_DIR = new File(TEST_DIAGRAM_PLOT_DIR, "misc");
	public final static File TARGET_DIR = new File("target/");
	
	public final static File JOURNAL_HTML = new File(TEST_IMAGE_DIR, "journal.html");
	
	public final static String LINES = "lines";
	public final static File LINES_DIR = new File(TEST_IMAGE_DIR, "lines");
	public final static File TARGET_LINES_DIR = new File(TARGET_DIR, LINES+"/");

	
	public final static File TREE_PNG = new File(LINES_DIR, "tree.png");
	public final static File TREE1_PNG = new File(LINES_DIR, "tree1.png");
	public static final File ETHANE_PNG = new File(LINES_DIR, "ethane.png");
	public static final File ETHANE1_PNG = new File(LINES_DIR, "ethane1.png");
	public static final File ETHENE_PNG = new File(LINES_DIR, "ethene.png");
	public static final File PROPANE_PNG = new File(LINES_DIR, "propane.png");
	public static final File MALTORYZINE_PNG = new File(LINES_DIR, "maltoryzine.png");
	public static final File ETHANE_CANNY_0_PNG = new File(LINES_DIR, "ethaneCanny0.png");
	public static final File ETHANE_CANNY_1_PNG = new File(LINES_DIR, "ethaneCanny1.png");
	public static final File ETHANE_CANNY_2_PNG = new File(LINES_DIR, "ethaneCanny2.png");
	public static final File MOLECULE_CANNY_1_PNG = new File(LINES_DIR, "moleculeCanny1.png");
	public static final File MOLECULE_20131119_A_JPG = new File(LINES_DIR, "IMG_20131119a.jpg");
	public static final File MOLECULE_20131119_BINARY_PNG = new File(LINES_DIR, "molecule20131119Binary.png");
	public static final File MOLECULE_BINARY_CANNY_1_PNG = new File(LINES_DIR, "moleculeBinaryCanny1.png");
	public static final File MOLECULE_BINARY_CANNY_1_BMP = new File(LINES_DIR, "moleculeBinaryCanny1.bmp");
	
	public final static File PROCESSING_DIR = new File(TEST_IMAGE_DIR, "processing");
	public static final File HISTOGRAM_JPG = new File(PROCESSING_DIR, "300px-Unequalized_Hawkes_Bay_NZ.jpg");
	public static final File HISTOGRAM_PNG = new File(PROCESSING_DIR, "300px-Unequalized_Hawkes_Bay_NZ.png");
	public static final File MOLECULE_20131119_JPG = new File(PROCESSING_DIR, "IMG_20131119_180112.jpg");
	public static final File MOLECULE_CANNY_PNG = new File(PROCESSING_DIR, "moleculeCanny.png");
	public static final File MALTORYZINE_BINARY_PNG = new File(PROCESSING_DIR, "maltoryzineBinary.png");
	public static final File MALTORYZINE_FLIPPED_PNG = new File(PROCESSING_DIR, "maltoryzineFlipped.png");
	public static final File MALTORYZINE_THINNED_PNG = new File(PROCESSING_DIR, "thinnedMaltoryzine.png");
	public static final File BRANCH0_PNG = new File(PROCESSING_DIR, "branch0.png");
	public static final File BRANCH_PNG = new File(PROCESSING_DIR, "branch.png");
	public static final File HEXAGON_PNG = new File(PROCESSING_DIR, "hexagon.png");
	public static final File LINE_PNG = new File(PROCESSING_DIR, "line.png");
	public static final File MALTORYZINE0_PNG = new File(PROCESSING_DIR, "maltoryzine0.png");
	public static final File TERMINAL_PNG = new File(PROCESSING_DIR, "terminalnode.png");
	public static final File TERMINALS_PNG = new File(PROCESSING_DIR, "terminalnodes.png");
	public static final File ZIGZAG_PNG = new File(PROCESSING_DIR, "zigzag.png");
	
	public final static File PIXEL_DIR = new File(TEST_IMAGE_DIR, "pixel");
	public static final File PHYLO_14811_2_PNG = new File(PIXEL_DIR, "ijs.0.014811-0-002.pbm.png");


	public final static File TEXT_DIR = new File(TEST_IMAGE_DIR, "text");
	public static final File NRRL_PNG = new File(TEXT_DIR, "NRRL.png");
	public static final File NO2 = new File(TEXT_DIR, "NO2.png");
	public static final File GIBBONS_PNG = new File(TEXT_DIR, "gibbons.png");
	public static final File HELVETICA_PNG = new File(TEXT_DIR, "helvetica.png");
	public static final File HELVETICA_BOLD_PNG = new File(TEXT_DIR, "helveticaBold.png");
	public static final File LUCIDA_PNG = new File(TEXT_DIR, "lucida.png");
	public static final File MONOSPACE_PNG = new File(TEXT_DIR, "monospace.png");
	public static final File TIMESROMAN_JPG = new File(TEXT_DIR, "timesroman.jpg");
	public static final File TIMES_GIF = new File(TEXT_DIR, "times.gif");
	public static final File LARGE_PHYLO_JPG = new File(ImageAnalysisFixtures.LINES_DIR, "1471-2148-13-93-1-l.jpg");

	public static final File COMPOUND_DIR = new File(TEST_IMAGE_DIR, "compound/");
	public final static File DIAGRAMS_DIR = new File(TEST_IMAGE_DIR, "diagrams/");
	
	public final static String BIO = "bio";
	public final static File BIO_DIR = getDiagramsDirectory(BIO);
	public final static File TARGET_BIO_DIR = new File(TARGET_DIR, BIO+"/");
	
	public final static String ELECTRONIC = "electronic";
	public final static File ELECTRONIC_DIR = getDiagramsDirectory(ELECTRONIC);

	public final static String FOREST = "forest";
	public final static File FOREST_DIR = getDiagramsDirectory(FOREST);
	public final static File TARGET_FOREST_DIR = new File(TARGET_DIR, FOREST+"/");
	
	public final static File TARGET_ELECTRONIC_DIR = new File(TARGET_DIR, ELECTRONIC+"/");
	public final static String PLOT = "plot";
	public final static File PLOT_DIR = getDiagramsDirectory(PLOT);
	public final static File TARGET_PLOT_DIR = new File(TARGET_DIR, PLOT+"/");

	
	public final static File FUNNEL_DIR = new File(DIAGRAMS_DIR, "funnel/");

	public static final File GENERAL_DIR = new File(TEST_IMAGE_DIR, "general");
	public static final File REFFONT_DIR = new File(GENERAL_DIR, "refFont");

	private static final File IMAGE_MAIN_DIR = new File("src/main/resources/org/contentmine/image");
	public static final File FONTS_MAIN_DIR = new File(IMAGE_MAIN_DIR, "text/fonts");
	public static final File HELVETICA_MAIN_DIR = new File(FONTS_MAIN_DIR, "helvetica");
	
	private static File getDiagramsDirectory(String subtopic) {
		return new File(DIAGRAMS_DIR, subtopic+"/");
	}


	public static final File TEST_HELVETICA = new File(ImageAnalysisFixtures.TEST_IMAGE_DIR, "text/fonts/helvetica");
	public static final String[] FILL = {
		"red",
		"green",
		"blue",
		"yellow",
		"magenta",
		"cyan",
		"orange",
		"purple",
		
	};
	
	public static PixelList LINE_LIST = new PixelList(Arrays.asList(new Pixel[] {
			new Pixel(new Point(1, 1)), 
			new Pixel(new Point(1, 2)),
			new Pixel(new Point(1, 3)), 
			new Pixel(new Point(1, 4)), 
			new Pixel(new Point(1, 5)), 
			}));
	
	public static PixelList DIAG_LIST = new PixelList(Arrays.asList(new Pixel[] {
			new Pixel(new Point(1, 1)), 
			new Pixel(new Point(2, 2)),
			new Pixel(new Point(3, 3)), 
			new Pixel(new Point(4, 4)), 
			new Pixel(new Point(5, 5)), 
			}));
	

	/**
	 * Tests the pixels below.
	 * 
	 * X is right Y is down 
	 * 
	 * +
	 * +
	 * +++
	 * +
	 * +
	 */
	public static PixelList T_LIST = new PixelList(Arrays.asList(new Pixel[] {
			new Pixel(new Point(1, 1)), 
			new Pixel(new Point(1, 2)),
			new Pixel(new Point(1, 3)), 
			new Pixel(new Point(1, 4)), 
			new Pixel(new Point(1, 5)), 
			new Pixel(new Point(2, 3)), 
			new Pixel(new Point(3, 3)), 
			}));
		
	public static PixelList L_LIST = new PixelList(Arrays.asList(new Pixel[] {
			new Pixel(new Point(1, 1)), 
			new Pixel(new Point(1, 2)),
			new Pixel(new Point(1, 3)), 
			new Pixel(new Point(2, 3)), 
			new Pixel(new Point(3, 3)), 
			}));

	/**
	 * 
	 * X is right Y is down 
	 * 
	 * +
	 * +
	 * ++
	 * +
	 */
	public static PixelList LONG_T_LIST = new PixelList(Arrays.asList(new Pixel[] {
			new Pixel(new Point(1, 1)), 
			new Pixel(new Point(1, 2)),
			new Pixel(new Point(1, 3)), 
			new Pixel(new Point(1, 4)), 
			new Pixel(new Point(2, 3)), 
			}));
	
	public static File CHAR_DIR = new File(ImageAnalysisFixtures.TEXT_DIR, "chars");

	public static PixelIsland CREATE_DOUBLE_Y_ISLAND() {
		PixelIsland island = new PixelIsland();
		island.setDiagonal(true);
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,0));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,1));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,2));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(1,3));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(-1,3));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(2,4));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(-2,4));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(3,5));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(-3,5));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,-1));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,-2));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(1,-3));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(-1,-3));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(2,-4));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(-2,-4));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(3,-5));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(-3,-5));
		return island;
	}

	public static PixelIsland CREATE_TRISPIKED_HEXAGON_ISLAND() {
		PixelIsland island = new PixelIsland();
		island.setDiagonal(true);
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,0));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,1));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,2));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(1,3));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(-1,3));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(1,4));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(-1,4));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(-2,5));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,5));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(2,5));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(-3,6));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(3,6));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(-4,7));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(4,7));
		return island;
	}

	public static PixelIsland CREATE_Y_ISLAND() {
		PixelIsland island = new PixelIsland();
		island.setDiagonal(true);
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,0));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,1));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,2));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,3));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(-1, -1));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(-2, -2));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(-3, -3));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(1, -1));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(2, -2));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(3, -3));
		return island;
	}

	public static PixelIsland CREATE_T_ISLAND() {
		PixelIsland island = new PixelIsland();
		island.setDiagonal(true);
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(-1,-1));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,0));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(1,0));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(2,1));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(3,1));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(4,1));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(5,1));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(6,0));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(7,0));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(3,2));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(3,3));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(3,4));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(3,5));
		return island;
	}

	public static PixelIsland CREATE_DOT_ISLAND() {
		PixelIsland island = new PixelIsland();
		island.setDiagonal(true);
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,0));
		return island;
	}

	public static PixelIsland CREATE_LINE_ISLAND() {
		PixelIsland island = new PixelIsland();
		island.setDiagonal(true);
		// simple wiggly line
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(2,0));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(1,0));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,1));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(-1,2));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0, 3));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0, 4));
		return island;
	}

	public static PixelIsland CREATE_CYCLE_ISLAND() {
		PixelIsland island = new PixelIsland();
		island.setDiagonal(true);
		// simple diamond cycle
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(1,0));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,1));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(-1,0));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0, -1));
		return island;
	}

	/**
	 * +
	 *  $$
	 *   $$
	 *     +
	 *     
	 * @return
	 */
	public static PixelIsland CREATE_RHOMBUS_ISLAND() {
		PixelIsland island = new PixelIsland();
		island.setDiagonal(true);
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,0));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(1,1));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(2,1));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(2,2));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(3,2));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(4,3));
		return island;
	}

	/**
	 * +  +
	 *  $$
	 *   $$
	 *  +  +
	 * @return
	 */
	public static PixelIsland CREATE_ZNUCLEUS_ISLAND() {
		PixelIsland island = new PixelIsland();
		island.setDiagonal(true);
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,0));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(1,1));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(2,1));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(3,0));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(2,2));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(3,2));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(1,3));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(4,3));
		return island;
	}

	public static PixelIsland CREATE_ZIGZAG_ISLAND() {
		PixelIsland island = new PixelIsland();
		island.setDiagonal(true);
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,0));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(1,1));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(2,2));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(3,3));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(4,4));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(5,5));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(6,4));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(7,3));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(8,2));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(9,1));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(10,0));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(11,1));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(12,2));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(13,3));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(14,4));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(15,5));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(16,4));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(17,3));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(18,2));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(19,1));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(20,0));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(20,1));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(20,2));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(20,3));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(20,4));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(20,5));
		return island;
	}

	/**
	 *$     $
	 * $   $
	 *  $ $
	 *   $
	 *   $
	 *   $
	 *   $
	 *   $
	 *  $ $
	 * $   $
	 *$     $
	 *
	 * @return
	 */
	public static PixelIsland CREATE_SHORTEDGE_ISLAND() {
		PixelIsland island = new PixelIsland();
		island.setDiagonal(true);
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,0));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,1));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,2));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(1,3));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(-1,3));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(2,4));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(-2,4));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(3,5));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(-3,5));
		
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,-1));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,-2));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(1,-3));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(-1,-3));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(2,-4));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(-2,-4));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(3,-5));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(-3,-5));
		return island;
	}


	/**
	 *   $
	 *   $
	 *   $
	 *  $$$
	 * $ $ $
	 *$  $  $
	 * $ $ $
	 *  $$$
	 *   $
	 *   $
	 *   $
	 *
	 * @return
	 */
	public static PixelIsland CREATE_PHI_ISLAND() {
		PixelIsland island = new PixelIsland();
		island.setDiagonal(true);
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,5));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,4));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,3));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,2));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(1,2));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(-1,2));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,1));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(2,1));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(-2,1));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,0));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(3,0));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(-3,0));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,-5));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,-4));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,-3));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,-2));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(1,-2));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(-1,-2));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,-1));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(2,-1));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(-2,-1));
		
		
		return island;
	}

}

