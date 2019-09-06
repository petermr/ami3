package org.contentmine.svg2xml.table;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGGBox;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.cache.ComponentCache;
import org.junit.Test;

public class FilledTableAnalyzerIT {

	@Test
	public void testCreateAndDrawRows() {
		Pattern filePattern = Pattern.compile("^.*/([^/]*)/tables/(table[^/]*)/.*$");
		for (File svgFile : FilledTableAnalyzerTest.FILLED_ELEM_FILES) {
			String f = svgFile.toString();
			Matcher matcher = filePattern.matcher(f);
			if (!matcher.matches()) throw new RuntimeException("bad match");
			String doi = matcher.group(1);
			String tableName = matcher.group(2);
			
			FilledTableAnalyzer filledTableAnalyzer = new FilledTableAnalyzer();
			filledTableAnalyzer.readSVGElement(svgFile);
			SVGG g = filledTableAnalyzer.createBoundaryListsAndProcessRows();
			SVGSVG.wrapAndWriteAsSVG(g, new File("target/"+FilledTableAnalyzerTest.FILLED+"/"+doi+"/"+tableName+"/"+"rows.svg"));
		}
	}

	@Test
	public void testCreateRowsWithEmptyCells() {
		Pattern filePattern = Pattern.compile("^.*/([^/]*)/tables/(table[^/]*)/.*$");
		for (File svgFile : FilledTableAnalyzerTest.FILLED_ELEM_FILES) {
			String f = svgFile.toString();
			Matcher matcher = filePattern.matcher(f);
			if (!matcher.matches()) throw new RuntimeException("bad match");
			String doi = matcher.group(1);
			String tableName = matcher.group(2);
			
			FilledTableAnalyzer filledTableAnalyzer = new FilledTableAnalyzer();
			filledTableAnalyzer.readSVGElement(svgFile);
			List<CellRow> cellRows = filledTableAnalyzer.createCellRowList();
			SVGG g = filledTableAnalyzer.createBoundaryListsAndProcessRows();
			SVGSVG.wrapAndWriteAsSVG(g, new File("target/"+FilledTableAnalyzerTest.FILLED+"/"+doi+"/"+tableName+"/"+"emptyCells.svg"));
		}
	}

	/**
	<defs>
	    <radialGradient id="grad1" cx="50%" cy="50%" r="50%" fx="50%" fy="50%">
	      <stop offset="0%" style="stop-color:rgb(255,255,255);
	      stop-opacity:0" />
	      <stop offset="100%" style="stop-color:rgb(0,0,255);stop-opacity:1" />
	    </radialGradient>
	  </defs>
	  <ellipse cx="200" cy="70" rx="85" ry="55" fill="url(#grad1)" />
	  
	  	 */
		/** rotate element positions position
		 * @throws FileNotFoundException 
		 * 
		 */
		@Test
		public void testReadGraphicsComponents() throws FileNotFoundException {
			for (File svgFile : FilledTableAnalyzerTest.FILLED_FILES) {
				ComponentCache svgStore = new ComponentCache();
				svgStore.readGraphicsComponentsAndMakeCaches(svgFile);
				SVGElement svgElement = (SVGElement) svgStore.getExtractedSVGElement();
				// this is inefficient but OK for now
				List<SVGElement> descendants = SVGElement.extractSelfAndDescendantElements(svgElement);
				List<SVGRect> rectList = SVGRect.extractSelfAndDescendantRects(svgElement);
				List<SVGGBox> boxList = new ArrayList<SVGGBox>();
				SVGSVG.wrapAndWriteAsSVG(svgElement, new File("target/"+FilledTableAnalyzerTest.FILLED+"/"+svgFile.getPath()+".elems.svg"));
			}
		}

}
