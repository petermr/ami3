package org.contentmine.svg2xml.page;

import java.io.File;

import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.text.structure.TextStructurer;
import org.contentmine.svg2xml.table.TableStructurer;

/** this is a hack during the refactoring of Text but not Table to svghtml
 * 
 * It provides access to table methods which are not present in TextStructurer
 * 
 * UNFINISHED
 * 
 * @author pm286
 *
 */
public class TableTextStructurer extends TextStructurer {

	private TableTextStructurer() {
		super();
	}
	
	/** copy internals by reference from textStructurer (pseudo copy constructor)
	 * messy but manageable
	 * @param textStructurer
	 */
	public TableTextStructurer(TextStructurer textStructurer) {
		super(textStructurer);
	}
	
	/** this is messy because the two TextStructurer is in a different project
	 * 
	 * @param svgElement
	 * @return
	 */
	public static TableTextStructurer createTableTextStructurerWithSortedLines(SVGElement svgElement) {
		TextStructurer textStructurer = TextStructurer.createTextStructurerWithSortedLines(svgElement);
		return new TableTextStructurer(textStructurer);
	}

	/** this is messy because the two TextStructurer is in a different project
	 * 
	 * @param svgElement
	 * @return
	 */
	public static TableTextStructurer createTableTextStructurerWithSortedLines(File file) {
		TextStructurer textStructurer = TextStructurer.createTextStructurerWithSortedLines(file);
		return new TableTextStructurer(textStructurer);
	}

	public SVGElement rotateClockwise() {
		throw new RuntimeException("Rotated tables NYI; use Caches");
//		SVGG rotatedVerticalText = (SVGG) createChunkFromVerticalText(new Angle(-1.0 * Math.PI / 2));
//		TableStructurer tableStructurer = createTableStructurer();
//		SVGElement chunk = getSVGChunk();
//		Angle angle = new Angle(-1.0 * Math.PI / 2);
//		List<SVGShape> shapeList = tableStructurer.getOrCreateShapeList();
//		SVGElement.rotateAndAlsoUpdateTransforms(shapeList, chunk.getCentreForClockwise90Rotation(), angle);
//		chunk.removeChildren();
//		XMLUtil.transferChildren(rotatedVerticalText, chunk);
//		for (SVGShape shape : shapeList) {
//			shape.detach();
//			chunk.appendChild(shape);
//		}
//		return chunk;
	}

	public TableStructurer createTableStructurer() {
		return TableStructurer.createTableStructurer(this);
	}



}
