package org.contentmine.image.diagram.contour;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.Angle.Units;
import org.contentmine.eucl.euclid.Int2;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGPath;
import org.contentmine.graphics.svg.SVGPoly;
import org.contentmine.graphics.svg.SVGPolygon;
import org.contentmine.graphics.svg.SVGPolyline;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGUtil;
import org.contentmine.image.ImageAnalysisFixtures;
import org.contentmine.image.diagram.DiagramAnalyzer;
import org.contentmine.image.pixel.PixelEdge;
import org.contentmine.image.pixel.PixelEdgeList;
import org.contentmine.image.pixel.PixelIsland;
import org.contentmine.image.pixel.PixelIslandList;
import org.contentmine.image.processing.ZhangSuenThinning;
import org.junit.Test;

import junit.framework.Assert;
import nu.xom.Element;
import nu.xom.Node;

public class ContourTest {
	private static final Logger LOG = Logger.getLogger(ContourTest.class);
	private static final int NSEG = 100;
	private static final int POINTS_PER_SEG = 10;
	private Map<Integer, SVGPolyline> polylinesByIndex;
	private List<Int2> origins;
	private List<SVGG> boxList;
	private Map<Integer, List<Integer>> childIdsById;
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	/** an unthinned set of contours on a single diagram.
	 * contours of Arthur's seat Edinburgh, thankx Alf Eaton.
	 * 
	 * 
	 * @throws IOException
	 */
	@Test
	public void testContour() throws IOException {
		String root = "arthur36a";
		String project = "contour";
		File imageFile = new File(new File(ImageAnalysisFixtures.DIAGRAMS_DIR, project), root+".png");
		String[] col = {
				"red", "green", "blue", "pink", "magenta", "cyan", "gray", "brown", "purple", "orange"
		};
		
		SVGG contoursSvg = createContourIndex();
		
		File contourFile = new File(new File(ImageAnalysisFixtures.DIAGRAMS_DIR, project), root+".svg");
		SVGSVG actualContoursG = (SVGSVG) SVGUtil.parseToSVGElement(
				new FileInputStream(contourFile));
		List<Element> elems = XMLUtil.getQueryElements(actualContoursG, "//*[local-name()='polyline']");
		polylinesByIndex = new HashMap<Integer, SVGPolyline>();
		for (Element elem : elems) {
			SVGPolyline actualPath = (SVGPolyline) elem;
			String id = actualPath.getId();
			int iid = Integer.parseInt(id);
			polylinesByIndex.put(iid, actualPath);
		}
		
		childIdsById = getChildContours(contoursSvg);
		for (Integer id : childIdsById.keySet()) {
			List<Integer> intList = childIdsById.get(id); 
		}
		 origins = Arrays.asList(
//					new Int2[] {
//							new Int2(0,0),
//							new Int2(10,10),
//							new Int2(20,20),
//							new Int2(30,30),
//							new Int2(40,40),
//							new Int2(50,50),
//							new Int2(60,60),
//							new Int2(70,70),
//							new Int2(80,80),
//							new Int2(90,90),
//							new Int2(100,100),
//							new Int2(110,110),
//							new Int2(120,120),
//							new Int2(130,130),
//							new Int2(140,140),
//							new Int2(150,150),
//							new Int2(160,160),
//							new Int2(170,170),
//							}
					new Int2[] {
							new Int2(0,0),
							new Int2(1700,0),
							new Int2(1500,1200),
							new Int2(0,1200),
							new Int2(2650,00),
							
							new Int2(2300,1000),
							
							new Int2(000,1700),
							new Int2(000,2000),
							new Int2(600,1700), //
							new Int2(1200,1700), //
							new Int2(1500,1900), //
							new Int2(1900,2000),
							
							new Int2(1800,1900), //
							new Int2(2300,1600), //
							new Int2(2600,1900), //
							new Int2(2900,1600), //
							new Int2(3200,1400), //
							new Int2(3500,1400), //
							}
			);
		boxList = makeCompleteBoxes();
		SVGG gg = new SVGG();
		for (SVGG box : boxList) {
			gg.appendChild(box);
		}
		gg.appendChild(makeBase().copy());
//		SVGSVG.wrapAndWriteAsSVG(gg, new File("target/contour/"+root+ ".pathsbase.svg"), 10000, 5000);
		SVGSVG.wrapAndWriteAsSVG(gg, new File("/Users/pm286/36a.pathsbase.svg"), 10000, 5000);
				

		SVGG allG = new SVGG();
		allG.appendChild(makeBase().copy());
//		allG.appendChild(makeStruts().copy());
		
		SVGSVG.wrapAndWriteAsSVG(allG, new File( "/Users/pm286/36a.base.svg"));
	}

	private Map<Integer, List<Integer>> getChildContours(SVGG contoursSvg) {
		List<SVGPath> contourList = SVGPath.extractPaths(contoursSvg);
		Map<Integer, List<Integer>> childrenByParentId = new HashMap<Integer, List<Integer>>();
		for (int i = 0; i < contourList.size() - 1; i++) {
			SVGPath contour0 = contourList.get(i);
			Integer id = Integer.parseInt(contour0.getId());
			List<Node> childIds = XMLUtil.getQueryNodes(contour0, "./*[local-name()='path']/@id");
			List<Integer> childArray = new ArrayList<Integer>();
			for (Node childId : childIds) {
				childArray.add(Integer.parseInt(childId.getValue()));
			}
			LOG.debug(childrenByParentId);
			childrenByParentId.put(id, childArray);
		}
		return childrenByParentId;
	}

	// ================================
	
	/** ingestion
	 * 
	 * @param imageFile
	 * @return
	 */
	private SVGG createPolylinesG(File imageFile) {
		SVGG polylinesG = new SVGG();
		DiagramAnalyzer diagramAnalyzer = new DiagramAnalyzer();
		diagramAnalyzer.setThinning(new ZhangSuenThinning());
		diagramAnalyzer.getOrCreateGraphList(imageFile);
		PixelIslandList pixelIslandList = diagramAnalyzer.getOrCreatePixelIslandList();
		Assert.assertEquals(18, pixelIslandList.size());
		for (int i = 0; i < pixelIslandList.size(); i++) {
			PixelIsland pixelIsland = pixelIslandList.get(i);
			pixelIsland.fillSingleHoles();
			pixelIsland.removeCorners();
			PixelEdgeList edgeList = pixelIsland.getOrCreateEdgeList();
			PixelEdge pixelEdge = edgeList.get(0);
			if (pixelEdge != null) {
				PixelEdge edge = pixelEdge.createSegmentedEdge(pixelEdge.size() / POINTS_PER_SEG);
				SVGPoly polyline = edge.createPolylineFromSegmentList();
				if (polyline != null) {
					polyline = new SVGPolygon(polyline);
					polyline.setFill("none");
					polyline.setStroke("green");
					polyline.setStrokeWidth(2.);
					polylinesG.appendChild(polyline.copy());
				} else {
					LOG.debug("null polyline");
				}
			}
		}
		return polylinesG;
	}

	private SVGG createContourIndex() {
		String contoursXML = ""
		+ "<g>"
		+ "  <path id='0' contour='50' level='1'>"
		+ "    <path id='1' contour='70' level='2'>"
		+ "      <path id='3' contour='90' level='3'>"
		+ "        <path id='2' contour='110' level='4'>"
		+ "          <path id='4' contour='130' level='5'>"
		+ "            <path id='5' contour='150' level='6'>"
		+ "              <path id='6' contour='170' level='7'>"
		+ "                <path id='8' contour='190' level='8'>"
		+ "                  <path id='9' contour='210' level='9'>"
		+ "                    <path id='14' contour='230' level='10'/>"
		+ "                    <path id='15' contour='230' level='10'/>"
		+ "                    <path id='17' contour='230' level='10'/>"
		+ "                  </path>"
		+ "                </path>"
		+ "              </path>"
		+ "              <path id='11' contour='150' level='6'/>"
		+ "            </path>"
		+ "          </path>"
		+ "          <path id='7' contour='170' level='7'>"
		+ "            <path id='10' contour='190' level='8'>"
		+ "              <path id='16' contour='210' level='9'/>"
		+ "            </path>"
		+ "          </path>"
		+ "          <path id='12' contour='110' level='4'/>"
		+ "          <path id='13' contour='90' level='3'/>"
		+ "        </path>"
		+ "      </path>"
		+ "    </path>"
		+ "  </path>"
		+ "</g>"
		;
		SVGG contoursSvg = (SVGG) SVGUtil.parseToSVGElement(contoursXML);
		return contoursSvg;
	}
	
	public List<SVGG> makeCompleteBoxes() {
		List<SVGG> boxList = new ArrayList<SVGG>();
		for (Integer i = 0; i < 17; i++) {
			SVGG boxG = new SVGG();
			SVGPolyline element = polylinesByIndex.get(i);
			if (element == null) {
				LOG.debug("bad index: "+i);
				continue;
			}
			SVGPolyline polylinex = new SVGPolyline(element);
			SVGPolygon polygon = new SVGPolygon(polylinex);
			Real2 delta2 = new Real2(origins.get(i));
			Transform2 t2 = Transform2.getTranslationTransform(delta2);
//			Real2Range box = polygon.getBoundingBox();
//			SVGRect rect = SVGRect.createFromReal2Range(box);
//			rect.setTransform(t2);
//			rect.setFill("none");
//			rect.setStroke("green");
//			rect.setStrokeWidth(1.0);
//			boxG.appendChild(rect);
			polygon.setTransform(t2);
			polygon.setFill("none");
			SVGPath path = polygon.createPath();
			boxG.appendChild(path);
			boxList.add(boxG);
			
			List<Integer> childIndexes = childIdsById.get(i);
			if (childIndexes == null) {
				LOG.debug("bad index: "+i);
				continue;
			}
			for (Integer childIdx : childIndexes) {
				SVGPolygon childPoly = new SVGPolygon(polylinesByIndex.get(childIdx)); 
				childPoly.setFill("red");
				childPoly.setStroke("red");
				childPoly.setStrokeWidth(1.0);
//				childPoly.setOpacity(0.3);
				childPoly.setTransform(t2);
				childPoly.applyTransformAttributeAndRemove();
				SVGPath path1 = childPoly.createPath();
				boxG.appendChild(path1);
			}

		}
		return boxList;
			
	}

	private SVGG makeStruts() {
		double width = 150;
		double thick = 25;
		SVGG gg = new SVGG();
		addPillarSlot(gg, 
				new SVGRect(230.0, 790.0, width, thick), 
				new Angle(Math.PI * 0.75, Units.RADIANS));
		addPillarSlot(gg, 
				new SVGRect(850.0, 1050.0,width, thick), 
				new Angle(Math.PI * 0.25, Units.RADIANS));
		addPillarSlot(gg, 
				new SVGRect(950.0, 600.0,width, thick), 
				new Angle(Math.PI * 0.25, Units.RADIANS));
		gg.setFill("none");
		gg.setStroke("black");
		gg.setStrokeWidth(1.0);
		addPillar(gg, 1200, 1700, width, 300.); 
		addPillar(gg, 1400, 1700, width, 150.); 
		addPillar(gg, 1600, 1700, width, 150.); 
		return gg;
	}

	private void addPillar(SVGG gg, double x, double y, double width, double height) {
		SVGRect rect = new SVGRect(x, y, width, height);
		rect.setCSSStyle("fill:none);stroke:black;");
		gg.appendChild(rect);
	}

	private void addPillarSlot(SVGG gg, SVGRect rect, Angle angle) {
		rect.setCSSStyle("fill:none;stroke:black;stroke-width:1 px;");
		Transform2 t2 = Transform2.getRotationAboutPoint(
				angle, rect.getBoundingBox().getCentroid());
		rect.setTransform(t2);
		gg.appendChild(rect);
	}

	private SVGG makeBase() {
		String xmlString = ""
		+ "<g id='base' xmlns='http://www.w3.org/2000/svg'"
		+ "  style='font-family:helvetica;font-size:40;font-weight:bold;'>"
//O my Luve's like a red, red rose,
//That's newly sprung in June:
//O my Luve's like the melodie,
//That's sweetly play'd in tune.
//
//As fair art thou, my bonie lass,
//So deep in luve am I;
//And I will luve thee still, my dear,
//Till a' the seas gang dry.
//
		+ "  <g>"
		+ "    <g id='where'>"
		+ "      <text x='120' y='60'  style='fill:black;font-weight:bold;font-family:helvetica;font-size:40;'>Arthur&apos;s Seat, Edinburgh, 2017</text>"
		+ "      <text x='220' y='270' style='fill:black;font-weight:bold;font-family:helvetica;font-size:80;'>Liz and Neil</text>"
		+ "    </g>"
		+ "    <rect x='0' y='0' width='1900' height='1550' "
		+ "      style='fill:none;stroke:red;stroke-width:1.5 px;'/>"
		+ "    <g id='north'>"
		+ "      <line x1='950' y1='50' x2='950' y2='200' stroke='black' stroke-width='5'/>"
		+ "      <line x1='950' y1='50' x2='920' y2='100' stroke='black' stroke-width='5'/>"
		+ "      <line x1='950' y1='50' x2='980' y2='100' stroke='black' stroke-width='5'/>"
		+ "      <text x='970' y='130' style='fill:black;font-size:40;font-weight:bold;font-family:helvetica;'>N</text>"
		+ "    </g>"
		+ "    <g id='poem'>"
		+ "      <text x='150' y='1450' style='fill:black;font-weight:bold;font-family:helvetica;font-size:40;'>Till a&apos; the seas gang dry, my dear,</text>"
		+ "      <text x='150' y='1500' style='fill:black;font-weight:bold;font-family:helvetica;font-size:40;'>And the rocks melt wi&apos; the sun;</text>"
		+ "      <text x='1100' y='1450' style='fill:black;font-weight:bold;font-family:helvetica;font-size:40;'>And I will luve thee still, my dear,</text>"
		+ "      <text x='1100' y='1500' style='fill:black;font-weight:bold;font-family:helvetica;font-size:40;'>While the sands o&apos; life shall run.</text>"
		+ "    </g>"
		+ "  </g>"
		+ "</g>"
		+ "";
//
//And fare-thee-weel, my only Luve!
//And fare-thee-weel, a while!
//And I will come again, my Luve,
//Tho' 'twere ten thousand mile! <"

		SVGG g = (SVGG) SVGUtil.parseToSVGElement(xmlString);
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/contour/base.svg"));
		return g;
	}

}
