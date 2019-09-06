package org.contentmine.svg2xml.flow;

import java.util.ArrayList;
import java.util.List;

import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGPath;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGShape;
import org.contentmine.graphics.svg.linestuff.Path2ShapeConverter;
import org.contentmine.graphics.svg.text.build.TextChunk;
import org.contentmine.graphics.svg.text.structure.TextStructurer;

public class FlowStructurer {

	private TextStructurer textStructurer;
	private TextChunk phraseListList;

	public FlowStructurer(TextChunk phraseListList) {
		this.phraseListList = phraseListList;
	}

	public void setTextStructurer(TextStructurer textStructurer) {
		this.textStructurer = textStructurer;
	}

	public List<SVGShape> makeShapes() {
		AbstractCMElement svgChunk = textStructurer.getSVGChunk();
		List<SVGPath> pathList = SVGPath.extractSelfAndDescendantPaths(svgChunk);
		Path2ShapeConverter converter = new Path2ShapeConverter(pathList);
		converter.setSplitPolyLines(true);
		List<SVGShape> shapeList = converter.convertPathsToShapes(pathList);
		
		return shapeList;
	}
	
	public static List<SVGRect> extractRects(List<SVGShape> shapeList) {
		List<SVGRect> rectList = new ArrayList<SVGRect>();
		for (SVGElement shape : shapeList) {
			if (shape instanceof SVGRect) {
				SVGRect rect = (SVGRect) shape;
				rectList.add(rect);
			}
		}
		return rectList;
	}
	

}
