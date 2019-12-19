package org.contentmine.norma.pdf;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.CTreeList;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.eucl.euclid.ArrayBase.Trim;
import org.contentmine.eucl.euclid.Int2Range;
import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.eucl.euclid.IntRangeArray;
import org.contentmine.eucl.euclid.Real;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Array;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.html.HtmlStyle;
import org.contentmine.graphics.html.HtmlTable;
import org.contentmine.graphics.html.HtmlTd;
import org.contentmine.graphics.html.HtmlTr;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGPolyline;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.util.ImageIOUtil;
import org.contentmine.image.ImageUtil;
import org.contentmine.image.diagram.DiagramAnalyzer;
import org.contentmine.image.pixel.AxialPixelFrequencies;
import org.contentmine.image.pixel.PixelEdge;
import org.contentmine.image.pixel.PixelEdgeList;
import org.contentmine.image.pixel.PixelGraph;
import org.contentmine.image.pixel.PixelIsland;
import org.contentmine.image.pixel.PixelIslandList;
import org.contentmine.image.pixel.PixelList;
import org.contentmine.image.pixel.PixelRing;
import org.contentmine.image.pixel.PixelRingList;
import org.contentmine.pdf2svg2.CustomPageDrawer;
import org.junit.Ignore;
import org.junit.Test;

import junit.framework.Assert;

@Ignore // no longer working on this
public class WellIT {
	private static final File WELL_DIR = new File("/Users/pm286/ContentMine/well/testfiles");
	public static final Logger LOG = Logger.getLogger(WellIT.class);
	private static String TEST_FILES_ROOT;
	static {
		LOG.setLevel(Level.DEBUG);
	}
	


	@Test
	public void testWell() throws Exception {
		File sourceDir = WELL_DIR;
		if (!sourceDir.exists()) {
			LOG.info("skipped pmr only test");			return;
		}
		File targetDir = sourceDir;
		CTreeList cTreeList = null;
		CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
		CProject cProject = new CProject(targetDir);
		cTreeList = cProject.getOrCreateCTreeList();
		for (CTree cTree : cTreeList) {
			LOG.debug("******* "+cTree+" **********");
		    cTree.extractSVGAndPDFImages();
		}
	}

	@Test
	public void testWellTable() throws Exception {
		File sourceDir = WELL_DIR;
		File targetDir = sourceDir;
		CProject cProject = new CProject(targetDir);
		CTreeList cTreeList = cProject.getOrCreateCTreeList();
		for (CTree cTree : cTreeList) {
			LOG.debug("******* "+cTree+" **********");
		    cTree.extractSVGAndPDFImages();
		}
	}

	@Test
	public void test3brP1() throws Exception {
		File page2svg = new File(WELL_DIR, "3br/svg/fulltext-page.1.svg");
		SVGElement svgElement = SVGElement.readAndCreateSVG(
				XMLUtil.parseQuietlyToRootElement(new FileInputStream(page2svg)));
		SVGG g = new SVGG();
		List<SVGText> texts = SVGText.extractSelfAndDescendantTexts(svgElement);
		HtmlHtml html = HtmlHtml.createUTF8Html();
		HtmlStyle style = html.getOrCreateHead().getOrCreateHtmlStyle();
		style.appendChild(" td {border : solid red; font-family : monospace;}");
		HtmlTable htmlTable = new HtmlTable();
		html.appendChild(htmlTable);
		double ylast = -10.;
		HtmlTr tr = new HtmlTr();
		htmlTable.addRow(tr);
		for (SVGText text : texts) {
			double y = text.getY();
			if (y > 90 && y < 240) {
				if(!Real.isEqual(y,  ylast, 0.2)) {
					tr = new HtmlTr();
					htmlTable.addRow(tr);
				}
				HtmlTd td = new HtmlTd();
				tr.appendChild(td);
				String value = text.getValue();
				td.appendChild(value);
				text.setFontSize(8.0);
				g.appendChild(text.copy());
			}
		}
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/well/3brp1.svg"));
		XMLUtil.debug(html, new File("target/well/3brp1.html"), 1);
		
	}

	
	@Test
	public void testTraceIslands() throws IOException {
		String root = "1.1.clip";
		String project = "s.croce_001";
		File imageFile = new File(WELL_DIR, project + "/images/page."+root+".png");
		Assert.assertTrue(""+imageFile, imageFile.exists());
		DiagramAnalyzer diagramAnalyzer = new DiagramAnalyzer();
		PixelIslandList pixelIslandList = diagramAnalyzer.createDefaultPixelIslandList(imageFile);
		pixelIslandList.removeIslandsWithBBoxesLessThan(new Real2Range(new RealRange(0, 10), new RealRange(0, 10)));
		Assert.assertEquals(20, pixelIslandList.size());
		SVGSVG.wrapAndWriteAsSVG(pixelIslandList.getOrCreateSVGG(), new File("target/well/" + project + "/" + root + ".svg"));
	}
	
	@Test
	public void testTracePixelRings() throws IOException {
		String root = "1.1.clip";
		String project = "s.croce_001";
		File imageFile = new File(WELL_DIR, project + "/images/page."+root+".png");
		Assert.assertTrue(""+imageFile, imageFile.exists());
		DiagramAnalyzer diagramAnalyzer = new DiagramAnalyzer();
		diagramAnalyzer.readAndProcessInputFile(imageFile);
		PixelRingList pixelRingList = diagramAnalyzer.createDefaultPixelRings();
		Assert.assertEquals("pixelRings", 5, pixelRingList.size());
		SVGSVG.wrapAndWriteAsSVG(pixelRingList.plotPixels(), new File("target/well/" + project + "/" + root + ".rings.svg"));
	}

	@Test
	public void testGridIslands() throws IOException {
		String root = "1.1";
		String ctree = "nusco_002";
		File imageFile = new File(WELL_DIR, ctree+"/images/page."+root+".png");
		Assert.assertTrue(""+imageFile, imageFile.exists());
		DiagramAnalyzer diagramAnalyzer = new DiagramAnalyzer();
		diagramAnalyzer.readAndProcessInputFile(imageFile);
		PixelIslandList pixelIslandList = diagramAnalyzer.createDefaultPixelIslandList();
		pixelIslandList.removeIslandsWithBBoxesLessThan(new Real2Range(new RealRange(0, 10), new RealRange(0, 10)));
		Assert.assertEquals(20, pixelIslandList.size());
		SVGSVG.wrapAndWriteAsSVG(pixelIslandList.getOrCreateSVGG(), new File("target/well/"+ctree+"/page."+root+".svg"));

	}
	
	@Test
	@Ignore // runs for too long
	public void testGridPixelRings() throws IOException {
		String root = "1.1";
		String project = "nusco_002";
		File imageFile = new File(WELL_DIR, project + "/images/page."+root+".png");
		Assert.assertTrue(""+imageFile, imageFile.exists());
		DiagramAnalyzer diagramAnalyzer = new DiagramAnalyzer();
		diagramAnalyzer.readAndProcessInputFile(imageFile);
		PixelRingList pixelRingList = diagramAnalyzer.createDefaultPixelRings();
		Assert.assertEquals("pixelRings", 5, pixelRingList.size());
		SVGSVG.wrapAndWriteAsSVG(pixelRingList.plotPixels(), new File("target/well/" + project + "/" + root + ".rings.svg"));
	}


	@Test
	public void testGridClipThinMinBoxMinSize() throws IOException {
		String root = "1.1.clip";
		String ctree = "nusco_002";
		File imageFile = new File(WELL_DIR, ctree+"/images/page."+root+".png");
		Assert.assertTrue(""+imageFile, imageFile.exists());
		DiagramAnalyzer diagramAnalyzer = new DiagramAnalyzer();
		diagramAnalyzer.readAndProcessInputFile(imageFile);
		PixelIslandList pixelIslandList = diagramAnalyzer.createDefaultPixelIslandList();
		pixelIslandList.removeIslandsWithBBoxesLessThan(new Real2Range(new RealRange(0, 10), new RealRange(0, 10)));
		pixelIslandList.removeIslandsWithLessThanPixelCount(50);
		SVGSVG.wrapAndWriteAsSVG(pixelIslandList.getOrCreateSVGG(), new File("target/well/"+ctree+"/page."+root+".svg"));
		for (PixelIsland pixelIsland : pixelIslandList) {
			PixelGraph graph = pixelIsland.getOrCreateGraph();
			pixelIsland.getOrCreateEdgeList();
			
			PixelEdgeList edgeList = graph.getOrCreateEdgeList();
			LOG.debug("E E"+edgeList.size());
			PixelEdgeList removeEdgeList = new PixelEdgeList();
			for (PixelEdge edge : edgeList) {
				PixelEdge edge1 = edge.createSegmentedEdge(1);
				SVGG line = edge1.createLineSVG();
				List<SVGLine> lineList = SVGLine.findHorizontalOrVerticalLines(line, 0.1);
				if (lineList.size() == 1 ) {
					removeEdgeList.add(edge);
				}
			}
			LOG.debug("REM "+removeEdgeList.size());
			pixelIsland.removePixelEdgeList(removeEdgeList);
		}

	}
	
	
	@Test
	public void testClipImage() throws IOException {
		String root = "1.1.a.clip";
		String root1 = "1.1.c.clip";
		String ctree = "nusco_002";
		File testFile = WELL_DIR;
		File imageFile = new File(testFile, ctree+"/images/page."+root+".png");
		Assert.assertTrue(""+imageFile, imageFile.exists());
		DiagramAnalyzer diagramAnalyzer = new DiagramAnalyzer();
		diagramAnalyzer.setThinning(null);
		diagramAnalyzer.readAndProcessInputFile(imageFile);
		BufferedImage oldImage = diagramAnalyzer.getImage();
		int xoff = 0;
		int yoff = 364;
		int newHeight = 1015;
		int newWidth = oldImage.getWidth();
		BufferedImage image = ImageUtil.createClippedImage(oldImage, xoff, yoff, newWidth, newHeight);
		File imageFileNew = new File(testFile, ctree+"/images/page."+root1+".png");
		ImageIOUtil.writeImageQuietly(image, imageFileNew);
	}

	
	@Test
	public void testFindGrid() throws IOException {
		String root = "1.1.c.clip";
		String ctree = "nusco_002";
		File testFile = WELL_DIR;
		File imageFile = new File(testFile, ctree+"/images/page."+root+".png");
		Assert.assertTrue(""+imageFile, imageFile.exists());
		DiagramAnalyzer diagramAnalyzer = new DiagramAnalyzer();
		diagramAnalyzer.setThinning(null);
		diagramAnalyzer.readAndProcessInputFile(imageFile);
		diagramAnalyzer.createAxialPixelFrequencies();
		AxialPixelFrequencies axialPixelFrequencies = diagramAnalyzer.getAxialPixelFrequencies();
		RealArray xFrequencies = new RealArray(axialPixelFrequencies.getXFrequencies());
//		LOG.debug("x freq "+xFrequencies);
		Assert.assertEquals("xfreq", "(57.0,57.0,54.0,50.0,50.0,53.0,54.0,54.0,55.0,62.0,58.0,64.0,61.0,55.0,51.0,51.0,"
				+ "51.0,55.0,58.0,58.0,64.0,63.0,60.0,62.0,62.0,63.0,63.0,63.0,65.0,66.0,65.0,88.0,358.0,931.0,434.0,"
				+ "81.0,71.0,64.0,55.0,52.0,51.0,50.0,48.0,49.0,48.0,49.0,49.0,41.0,42.0,40.0,42.0,42.0,44.0,41.0,44.0,42."
				+ "0,41.0,45.0,39.0,40.0,43.0,42.0,44.0,43.0,41.0,43.0,47.0,44.0,49.0,45.0,45.0,42.0,42.0,40.0,41.0,40.0,"
				+ "41.0,37.0,33.0,39.0,39.0,44.0,44.0,44.0,44.0,57.0,81.0,304.0,689.0,131.0,61.0,55.0,48.0,43.0,43.0,41.0,"
				+ "42.0,37.0,39.0,37.0,35.0,38.0,38.0,35.0,32.0,40.0,37.0,38.0,44.0,46.0,44.0,45.0,41.0,39.0,37.0,39.0,34.0,"
				+ "38.0,37.0,38.0,36.0,42.0,45.0,45.0,46.0,46.0,43.0,44.0,39.0,35.0,42.0,47.0,45.0,48.0,43.0,42.0,42.0,40.0,49.0,54.0,72.0,402.0,747.0,129.0,61.0,54.0,50.0,44.0,38.0,39.0,42.0,38.0,36.0,44.0,46.0,48.0,45.0,58.0,58.0,60.0,62.0,63.0,65.0,65.0,74.0,76.0,76.0,69.0,71.0,69.0,69.0,65.0,62.0,63.0,66.0,65.0,67.0,70.0,71.0,74.0,72.0,75.0,75.0,74.0,79.0,81.0,83.0,75.0,77.0,78.0,78.0,76.0,85.0,86.0,83.0,87.0,154.0,878.0,453.0,97.0,83.0,77.0,77.0,78.0,80.0,80.0,75.0,74.0,85.0,80.0,81.0,80.0,79.0,71.0,78.0,80.0,78.0,75.0,84.0,83.0,78.0,79.0,81.0,80.0,77.0,76.0,71.0,70.0,69.0,72.0,71.0,77.0,73.0,64.0,61.0,56.0,58.0,56.0,57.0,61.0,67.0,63.0,65.0,72.0,67.0,64.0,66.0,72.0,79.0,83.0,89.0,123.0,633.0,342.0,107.0,100.0,102.0,99.0,92.0,88.0,80.0,88.0,84.0,83.0,82.0,85.0,81.0,91.0,94.0,97.0,97.0,98.0,90.0,84.0,80.0,75.0,71.0,66.0,66.0,68.0,65.0,70.0,78.0,86.0,84.0,90.0,87.0,88.0,81.0,82.0,82.0,83.0,87.0,82.0,76.0,75.0,71.0,74.0,62.0,68.0,71.0,77.0,82.0,93.0,143.0,548.0,696.0,281.0,79.0,71.0,67.0,56.0,48.0,43.0,37.0,36.0,39.0,43.0,36.0,37.0,43.0,41.0,40.0,42.0,47.0,46.0,43.0,48.0,49.0,45.0,51.0,58.0,58.0,57.0,60.0,64.0,65.0,68.0,68.0,71.0,67.0,66.0,73.0,69.0,66.0,70.0,78.0,82.0,79.0,75.0,74.0,80.0,72.0,84.0,84.0,82.0,88.0,90.0,92.0,133.0,641.0,451.0,118.0,107.0,99.0,91.0,90.0,87.0,89.0,86.0,82.0,84.0,75.0,79.0,74.0,79.0,79.0,79.0,76.0,72.0,70.0,69.0,68.0,64.0,71.0,75.0,73.0,91.0,86.0,81.0,95.0,95.0,90.0,74.0,66.0,65.0,69.0,65.0,60.0,66.0,68.0,74.0,73.0,72.0,81.0,79.0,70.0,80.0,64.0,63.0,64.0,64.0,72.0,75.0,140.0,503.0,557.0,213.0,71.0,60.0,51.0,47.0,45.0,36.0,40.0,37.0,38.0,41.0,42.0,38.0,39.0,36.0,45.0,45.0,47.0,48.0,51.0,48.0,46.0,51.0,51.0,54.0,51.0,49.0,48.0,48.0,50.0,50.0,53.0,56.0,49.0,48.0,48.0,46.0,42.0,45.0,40.0,42.0,39.0,45.0,46.0,42.0,45.0,43.0,45.0,44.0,54.0,64.0,226.0,517.0,520.0,81.0,56.0,51.0,45.0,41.0,41.0,40.0,40.0,42.0,46.0,42.0,41.0,41.0,42.0,45.0,40.0,37.0,40.0,41.0,40.0,39.0,41.0,40.0,40.0,39.0,37.0,39.0,41.0,43.0,45.0,46.0,48.0,46.0,47.0,49.0,52.0,47.0,46.0,44.0,37.0,41.0,42.0,43.0,44.0,45.0,47.0,45.0,46.0,48.0,49.0,54.0,57.0,59.0,70.0,183.0,629.0,1015.0,1015.0,944.0,400.0,12.0,9.0,9.0,8.0,8.0,9.0,9.0,12.0,24.0,40.0,48.0,77.0,90.0,82.0,72.0,62.0,59.0,54.0,52.0,53.0,55.0,56.0,64.0,66.0,68.0,68.0,58.0,68.0,69.0,61.0,52.0,61.0,56.0,27.0,14.0,9.0,8.0,10.0,10.0,10.0,9.0,10.0,10.0,11.0,10.0,10.0,10.0,10.0,10.0,10.0,10.0,10.0,12.0,10.0,10.0,10.0,10.0,10.0,9.0,9.0,10.0,10.0,10.0,11.0,11.0,11.0,11.0,11.0,11.0,11.0,11.0,11.0,11.0,"
				+ "10.0,10.0,10.0,10.0,10.0,10.0,10.0,10.0,10.0,10.0,10.0,10.0,10.0,10.0,10.0,10.0)", xFrequencies.toString());
		RealArray corrArray = xFrequencies.createSimpleAutoConvolution(200, 2000).multiplyBy(0.000001).format(1);
//		LOG.debug("x corr " + " "+corrArray);  
		Assert.assertEquals("xcorr", "(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,"
				+ "0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,"
				+ "0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0)", corrArray.toString());  
		RealArray yFrequencies = new RealArray(axialPixelFrequencies.getYFrequencies());
//		LOG.debug("y freq "+yFrequencies);
		Assert.assertEquals("yfreq", "(68.0,223.0,207.0,115.0,78.0,54.0,47.0,44.0,49.0,60.0,42.0,36.0,24.0,"
				+ "15.0,15.0,17.0,18.0,22.0,17.0,13.0,15.0,16.0,24.0,27.0,21.0,22.0,25.0,26.0,22.0,21.0,21.0,20.0,"
				+ "21.0,22.0,21.0,20.0,24.0,26.0,59.0,129.0,100.0,133.0,89.0,33.0,30.0,28.0,22.0,24.0,25.0,25.0,23.0,23.0,20.0,21.0,24.0,23.0,21.0,15.0,20.0,20.0,20.0,20.0,24.0,21.0,18.0,17.0,16.0,16.0,19.0,22.0,22.0,20.0,20.0,23.0,22.0,26.0,31.0,31.0,49.0,150.0,86.0,145.0,59.0,26.0,29.0,36.0,54.0,49.0,38.0,28.0,23.0,29.0,26.0,24.0,23.0,21.0,20.0,14.0,17.0,19.0,19.0,25.0,32.0,41.0,45.0,38.0,29.0,27.0,29.0,30.0,38.0,39.0,49.0,55.0,50.0,39.0,47.0,229.0,426.0,545.0,542.0,396.0,159.0,26.0,22.0,18.0,17.0,19.0,26.0,30.0,27.0,17.0,12.0,20.0,30.0,33.0,31.0,22.0,21.0,21.0,26.0,36.0,61.0,70.0,63.0,37.0,44.0,37.0,24.0,16.0,17.0,16.0,15.0,16.0,20.0,23.0,19.0,19.0,35.0,107.0,111.0,116.0,126.0,25.0,19.0,17.0,17.0,18.0,16.0,18.0,18.0,18.0,17.0,15.0,16.0,18.0,30.0,35.0,33.0,23.0,21.0,26.0,21.0,18.0,18.0,18.0,17.0,17.0,25.0,28.0,28.0,24.0,20.0,19.0,20.0,20.0,25.0,23.0,26.0,120.0,155.0,278.0,186.0,33.0,22.0,18.0,18.0,16.0,19.0,18.0,17.0,18.0,22.0,24.0,21.0,18.0,22.0,19.0,18.0,19.0,18.0,19.0,22.0,20.0,22.0,25.0,27.0,23.0,18.0,18.0,19.0,20.0,22.0,23.0,28.0,35.0,36.0,32.0,36.0,138.0,179.0,237.0,155.0,30.0,25.0,25.0,20.0,22.0,23.0,24.0,20.0,24.0,22.0,21.0,24.0,27.0,26.0,25.0,20.0,19.0,20.0,20.0,20.0,21.0,22.0,18.0,18.0,21.0,21.0,21.0,19.0,18.0,21.0,20.0,25.0,24.0,30.0,33.0,43.0,170.0,239.0,250.0,42.0,24.0,22.0,21.0,22.0,23.0,24.0,28.0,28.0,27.0,23.0,18.0,23.0,22.0,24.0,26.0,25.0,25.0,25.0,23.0,23.0,28.0,29.0,30.0,26.0,27.0,25.0,25.0,26.0,27.0,31.0,34.0,32.0,32.0,31.0,34.0,52.0,193.0,308.0,204.0,39.0,32.0,28.0,27.0,29.0,31.0,31.0,33.0,34.0,36.0,38.0,33.0,28.0,22.0,21.0,23.0,23.0,26.0,25.0,27.0,30.0,28.0,30.0,25.0,25.0,25.0,26.0,26.0,25.0,25.0,30.0,30.0,28.0,27.0,29.0,33.0,61.0,164.0,253.0,229.0,59.0,35.0,39.0,36.0,31.0,27.0,31.0,32.0,35.0,30.0,25.0,24.0,26.0,29.0,31.0,28.0,29.0,28.0,27.0,24.0,27.0,25.0,26.0,22.0,23.0,23.0,25.0,24.0,23.0,26.0,28.0,27.0,27.0,25.0,27.0,32.0,61.0,190.0,387.0,138.0,30.0,29.0,25.0,25.0,27.0,23.0,20.0,22.0,23.0,27.0,27.0,24.0,23.0,27.0,25.0,25.0,25.0,22.0,22.0,20.0,19.0,19.0,20.0,20.0,19.0,16.0,20.0,21.0,21.0,19.0,22.0,22.0,22.0,23.0,26.0,49.0,232.0,302.0,49.0,31.0,24.0,22.0,25.0,22.0,26.0,29.0,30.0,25.0,20.0,21.0,22.0,24.0,23.0,22.0,21.0,22.0,23.0,24.0,22.0,20.0,21.0,21.0,21.0,20.0,21.0,21.0,22.0,25.0,27.0,27.0,30.0,31.0,31.0,33.0,31.0,66.0,274.0,291.0,44.0,25.0,20.0,25.0,26.0,25.0,22.0,19.0,21.0,24.0,28.0,31.0,33.0,29.0,30.0,31.0,33.0,34.0,27.0,22.0,21.0,26.0,32.0,38.0,36.0,52.0,73.0,81.0,62.0,48.0,38.0,36.0,48.0,51.0,55.0,51.0,50.0,80.0,383.0,265.0,48.0,41.0,39.0,37.0,38.0,34.0,32.0,32.0,34.0,39.0,34.0,26.0,20.0,21.0,28.0,35.0,40.0,34.0,38.0,39.0,36.0,36.0,46.0,45.0,50.0,45.0,45.0,42.0,59.0,87.0,199.0,201.0,176.0,67.0,23.0,29.0,117.0,116.0,128.0,163.0,33.0,22.0,18.0,15.0,19.0,22.0,17.0,17.0,15.0,15.0,16.0,15.0,16.0,17.0,19.0,17.0,15.0,16.0,20.0,24.0,27.0,30.0,32.0,23.0,23.0,26.0,23.0,22.0,25.0,24.0,26.0,26.0,26.0,24.0,27.0,52.0,137.0,398.0,207.0,31.0,27.0,23.0,23.0,25.0,23.0,17.0,17.0,19.0,19.0,20.0,22.0,23.0,23.0,21.0,23.0,23.0,22.0,24.0,20.0,20.0,23.0,22.0,22.0,23.0,22.0,22.0,22.0,25.0,31.0,27.0,26.0,25.0,25.0,25.0,28.0,109.0,431.0,615.0,620.0,620.0,430.0,98.0,17.0,12.0,11.0,9.0,7.0,8.0,8.0,11.0,13.0,15.0,16.0,15.0,13.0,10.0,9.0,9.0,11.0,14.0,15.0,14.0,11.0,14.0,16.0,15.0,20.0,20.0,19.0,18.0,19.0,20.0,21.0,19.0,23.0,34.0,154.0,293.0,35.0,27.0,22.0,20.0,23.0,23.0,23.0,23.0,22.0,19.0,19.0,22.0,21.0,17.0,18.0,17.0,18.0,20.0,20.0,21.0,21.0,21.0,21.0,20.0,20.0,22.0,23.0,22.0,25.0,24.0,18.0,21.0,21.0,22.0,23.0,26.0,31.0,85.0,237.0,337.0,46.0,29.0,24.0,23.0,21.0,22.0,25.0,26.0,28.0,32.0,30.0,35.0,45.0,56.0,49.0,44.0,32.0,28.0,25.0,22.0,24.0,24.0,23.0,21.0,22.0,25.0,28.0,31.0,28.0,23.0,21.0,25.0,29.0,29.0,31.0,33.0,98.0,193.0,333.0,94.0,43.0,38.0,35.0,29.0,29.0,27.0,24.0,23.0,21.0,21.0,20.0,19.0,21.0,24.0,23.0,22.0,24.0,33.0,39.0,37.0,37.0,33.0,29.0,21.0,22.0,23.0,24.0,20.0,20.0,22.0,22.0,23.0,25.0,25.0,23.0,26.0,30.0,68.0,462.0,111.0,32.0,27.0,23.0,25.0,24.0,22.0,28.0,31.0,33.0,23.0,17.0,18.0,18.0,20.0,20.0,21.0,24.0,25.0,27.0,34.0,44.0,37.0,25.0,25.0,28.0,32.0,29.0,29.0,30.0,33.0,34.0,28.0,26.0,27.0,28.0,31.0,79.0,237.0,306.0,35.0,21.0,20.0,21.0,18.0,20.0,20.0,21.0,24.0,25.0,21.0,17.0,17.0,19.0,21.0,19.0,21.0,19.0,20.0,25.0,26.0,24.0,25.0,25.0,27.0,114.0,289.0,412.0,537.0,620.0,418.0,277.0,212.0,100.0,17.0,18.0,33.0,66.0,74.0,93.0,55.0,27.0,18.0,13.0,12.0,12.0,11.0,12.0,13.0,14.0,14.0,12.0,12.0,14.0,16.0,21.0,28.0,31.0,25.0,17.0,16.0,22.0,29.0,37.0,40.0,41.0,45.0,45.0,44.0,47.0,46.0,48.0,46.0,108.0,203.0,388.0,531.0,521.0,359.0,161.0,39.0,23.0,22.0,16.0,13.0,13.0,13.0,16.0,19.0,29.0,25.0,19.0,16.0,17.0,14.0,13.0,25.0,35.0,34.0,19.0,17.0,20.0,23.0,24.0,25.0,26.0,30.0,37.0,33.0,32.0,26.0,23.0,19.0,20.0,21.0,27.0,72.0,103.0,91.0,305.0,32.0,22.0,20.0,20.0,19.0,21.0,21.0,23.0,18.0,18.0,19.0,21.0,20.0,26.0,26.0,27.0,23.0,24.0,25.0,23.0,21.0,22.0,22.0,23.0,19.0,21.0,21.0,21.0,21.0,23.0,21.0,23.0,23.0,24.0,29.0,29.0,45.0,201.0,427.0,63.0,34.0,32.0,34.0,30.0,26.0,17.0,18.0,21.0,22.0,18.0,19.0,17.0,18.0,19.0,19.0,22.0,20.0,22.0)", yFrequencies.toString());
		RealArray yCorrArray = yFrequencies.createSimpleAutoConvolution(200, 2000).multiplyBy(0.000001).format(1);
		Assert.assertEquals("ycorr", "(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,"
				+ "0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,"
				+ "0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0)", yCorrArray.toString());  
//		LOG.debug("y corr " + " "+yCorrArray1);  

		
		BufferedImage newImage = diagramAnalyzer.getImage();
		ImageUtil.setImageWhite(newImage);
		IntArray xFrequencies2 = axialPixelFrequencies.getXFrequencies();
		xFrequencies2.subtractMean();
		xFrequencies = xFrequencies.trim(Trim.BELOW, 0);
		LOG.debug("x "+xFrequencies2);
		
		IntArray yFrequencies2 = axialPixelFrequencies.getYFrequencies();
		yFrequencies2.subtractMean();
		yFrequencies2 = yFrequencies2.trim(Trim.BELOW, 0);
		LOG.debug("y "+yFrequencies2);
		int ysize = yFrequencies2.size();
		LOG.debug(newImage.getWidth()+"/"+ysize);
		writeYProjection(newImage, yFrequencies2);
		writeXProjection(newImage, xFrequencies2);
		ImageIOUtil.writeImageQuietly(newImage, new File(testFile, ctree+"/images/page."+root+".new.png"));

	}

	@Test
	public void testEraseGridLines() {
		String root = "1.1.c.clip";
		String root1 = "1.1.c.clip.clear";
		String ctree = "nusco_002";
		File testFile = WELL_DIR;
		File imageFile = new File(testFile, ctree+"/images/page."+root+".png");
		File outFile = new File(testFile, ctree+"/images/page."+root1+".png");
		Assert.assertTrue(""+imageFile, imageFile.exists());
		BufferedImage image = ImageUtil.readImage(imageFile);
		int width = image.getWidth();
		int height = image.getHeight();
		int y0 = 1;
//		int dy = 3;
		int dy = 2;
		double ystep = 39.7;
		int nystep = 25;
		for (int i = 0; i < nystep; i++) {
			int y = y0 + (int) ((float)i * ystep);
			Int2Range box = new Int2Range(
					new IntRange(0, width - 1), 
					new IntRange(y - dy, y + dy));
			ImageUtil.setImageColor(image, box, 0xffffff);
		}
		int x0 = 34;
//		int dx = 2;
		int dx = 1;
		double xstep = 109.3 / 2;
		int nxstep = 9;
		for (int i = 0; i < nxstep; i++) {
			int x = x0 + (int) ((float) i * xstep);
			Int2Range box = new Int2Range(
					new IntRange(x - dx, x + dx),
					new IntRange(0, height - 1)
					);
			ImageUtil.setImageColor(image, box, 0xffffff);
		}
		
		ImageIOUtil.writeImageQuietly(image, outFile);

	}

	@Test
	public void testPixelRingsN() throws IOException {
		String root = "1.1.c.clip";
		String root1 = "1.1.c.clip.rings";
		String ctree = "nusco_002";

		
		File imageFile = new File(WELL_DIR, ctree+"/images/page."+root+".png");
		File outfileSvg = new File(WELL_DIR,  ctree + "/" +"svg/" + root1 + ".svg");
		File outfilePng = new File(WELL_DIR,  ctree + "/" +"png/" + root1 + ".png");
		File outfilePngA = new File(WELL_DIR,  ctree + "/" +"png/" + root1 + ".a.png");
		File outfilePng1 = new File(WELL_DIR,  ctree + "/" +"png/" + root1 + ".missing.png");
		File outfileSvg1 = new File(WELL_DIR,  ctree + "/" +"svg/" + root1 + ".1.svg");
		Assert.assertTrue(""+imageFile, imageFile.exists());
		DiagramAnalyzer diagramAnalyzer = new DiagramAnalyzer();
		diagramAnalyzer.readAndProcessInputFile(imageFile);
		PixelRingList pixelRingList = diagramAnalyzer.createDefaultPixelRings();
		Assert.assertNotNull(pixelRingList);
		Assert.assertEquals(4, pixelRingList.size());
		BufferedImage image = diagramAnalyzer.getImage();
		int width = image.getWidth();
		int height = image.getHeight();
		Assert.assertEquals("w",  620, width);
		Assert.assertEquals("h",  1015, height);
		SVGSVG.wrapAndWriteAsSVG(pixelRingList.plotPixels(), outfileSvg);
		PixelRing pixelRingBase = pixelRingList.get(1);
		LOG.debug("writing SVG1 to "+outfileSvg1);
		SVGSVG.wrapAndWriteAsSVG(pixelRingBase.plotPixels("blue"), outfileSvg1);
		// clip LH axial line
		width = 520;
		DiagramAnalyzer diagramAnalyzerBase = DiagramAnalyzer.createDiagramAnalyzer(width, height, pixelRingBase);
		BufferedImage imagexx = diagramAnalyzerBase.getImage();
		LOG.debug("pixelRing base "+ImageUtil.createString(imagexx));
		LOG.debug("writing PNG to "+outfilePng);
		ImageIOUtil.writeImageQuietly(imagexx, outfilePngA);
		diagramAnalyzerBase.writeImage(outfilePng);
		
		diagramAnalyzerBase.createAxialPixelFrequencies();
		AxialPixelFrequencies axialPixelFrequencies = diagramAnalyzerBase.getAxialPixelFrequencies();
		IntArray yFrequencies = axialPixelFrequencies.getYFrequencies();
		LOG.debug(yFrequencies);
		PixelList missing = new PixelList();
		PixelRing ring0 = pixelRingList.get(0); // lowest ring
		for (int y = 0; y < yFrequencies.size(); y++) {
			if (yFrequencies.elementAt(y) <= 1) {
//				LOG.debug(y);
				PixelList pixelList = ring0.getPixelListByYCoordinate(y);
				missing.addAll(pixelList);
			}
		}
		missing.addAll(pixelRingBase);
		DiagramAnalyzer missingAnalyzer = DiagramAnalyzer.createDiagramAnalyzer(width, height, missing);
		LOG.debug("writing PNG1 to "+outfilePng1);
		missingAnalyzer.writeImage(outfilePng1);
	}

	@Test
	public void Ia() throws IOException {
		String root = "1.1.clip";
		String ctree = "s_croce_img_log";

		File imageFile = new File(WELL_DIR,  ctree+"/images/page."+root+".png");
		File outfileSvg = new File(WELL_DIR,  ctree + "/" +"svg/" + root + ".svg");
		File outfilePng = new File(WELL_DIR,  ctree + "/" +"png/" + root + ".png");
		File outfilePngA = new File(WELL_DIR,  ctree + "/" +"png/" + root + ".a.png");
		File outfilePng1 = new File(WELL_DIR,  ctree + "/" +"png/" + root + ".missing.png");
		File outfileSvg1 = new File(WELL_DIR,  ctree + "/" +"svg/" + root + ".1.svg");
		Assert.assertTrue(""+imageFile, imageFile.exists());
		DiagramAnalyzer diagramAnalyzer = new DiagramAnalyzer();
		diagramAnalyzer.readAndProcessInputFile(imageFile);
		PixelRingList pixelRingList = diagramAnalyzer.createDefaultPixelRings();
		Assert.assertNotNull(pixelRingList);
		Assert.assertEquals(5, pixelRingList.size());
		BufferedImage image = diagramAnalyzer.getImage();
		int width = image.getWidth();
		int height = image.getHeight();
		Assert.assertEquals("w",  270, width);
		Assert.assertEquals("h",  17576, height);
		PixelRing pixelRingBase = pixelRingList.get(1);
		SVGG pixelsG = pixelRingBase.plotPixels("blue");
		// clip LH axial line
		width = 520;
		int ymin = pixelRingBase.getIntBoundingBox().getYRange().getMin();
		DiagramAnalyzer diagramAnalyzerBase = DiagramAnalyzer.createDiagramAnalyzer(width, height, pixelRingBase);
		IntRangeArray yslices = diagramAnalyzerBase.getYslices();
		Assert.assertNotNull(yslices);
		Assert.assertEquals(11012, yslices.size());
		Real2Array points = yslices.generateYMidpointArray();
		points.plusEquals(new Real2(0.0, ymin));
		SVGPolyline polyline = new SVGPolyline(points);
		polyline.setStroke("red");
		polyline.setFill("none");
		polyline.setStrokeWidth(2.0);
		pixelsG.appendChild(polyline);
		LOG.debug("writing SVG1 to "+outfileSvg1);
		SVGSVG.wrapAndWriteAsSVG(pixelsG, outfileSvg1);
		SVGSVG.wrapAndWriteAsSVG(pixelRingList.plotPixels(), outfileSvg);

	}

	
	@Test
	public void testClipNuscoHandriting() {
		String root = "1.1";
		String ctree = "nusco_img_log";
		File inDir = new File(WELL_DIR, ctree);

		File imageFile = new File(inDir,  "images/page."+root+".png");
		Assert.assertTrue(imageFile.exists());
		BufferedImage rawImage = ImageUtil.readImage(imageFile);
		BufferedImage writingImg = ImageUtil.clipSubImage(rawImage, 
				new Int2Range(new IntRange(3358, 3765), new IntRange(2267, 2400)));
		ImageIOUtil.writeImageQuietly(writingImg, new File(inDir, "png/page" + root + ".clip" + ".png"));
		
	}

	// ================================
	
	private void writeYProjection(BufferedImage newImage, IntArray yFrequencies) {
		int ysize = yFrequencies.size();
		for (int j = 0; j < ysize; j++) {
			for (int i = 0; i < yFrequencies.elementAt(j); i++) {
				newImage.setRGB(i, j, 0xff0000);
			}
		}
	}
	
	private void writeXProjection(BufferedImage newImage, IntArray xFrequencies) {
		int xsize = xFrequencies.size();
		for (int i = 0; i < xsize; i++) {
			for (int j = 0; j < xFrequencies.elementAt(i); j++) {
				newImage.setRGB(i, j, 0x00ffff);
			}
		}
	}
	
	

}
