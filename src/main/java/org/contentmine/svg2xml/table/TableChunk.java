package org.contentmine.svg2xml.table;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.euclid.RealRange.Direction;
import org.contentmine.eucl.euclid.RealRangeArray;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.text.structure.TextStructurer;

/** superclass of cells, rows, etc in table
 * 
 * @author pm286
 *
 */
public class TableChunk {

	private final static Logger LOG = Logger.getLogger(TableChunk.class);
	private final static PrintStream SYSOUT = System.out;

	protected List<SVGElement> elementList;
	protected RealRangeArray horizontalMask;
	protected RealRangeArray verticalMask;

	private RealRangeArray horizontalGaps;

	private Real2Range bbox;

	protected TableChunk() {
		elementList = new ArrayList<SVGElement>();
	}
	
	public TableChunk(RealRangeArray horizontalMask, RealRangeArray verticallMask) {
		this.horizontalMask = horizontalMask;
		this.verticalMask = verticallMask;
	}

	public TableChunk(List<? extends SVGElement> elementList) {
		this.setElementList(elementList);
	}

	public void add(SVGElement element) { 
		elementList.add(element);
	}

	/** populate with SVGElements
	 * this may redefine or overflow the current extent of the cell
	 * @param elementList
	 */
	public void setElementList(List<? extends SVGElement> elemList) {
		this.elementList = new ArrayList<SVGElement>();
		for (SVGElement elem : elemList) {
			this.elementList.add(elem);
		}
	}

	public List<? extends SVGElement> getElementList() {
		return elementList;
	}

	public String getValue() {
		StringBuilder sb = new StringBuilder();
		for (AbstractCMElement element: elementList) {
			sb.append(element.getValue());
		}
		return sb.toString();
	}
	

	public RealRangeArray getHorizontalMask() {
		return horizontalMask;
	}

	public void setHorizontalMask(RealRangeArray horizontalMask) {
		this.horizontalMask = horizontalMask;
	}
	
	public void splitHorizontally() {
		LOG.warn("HOR MASK NYI "+horizontalMask);
	}
	public RealRangeArray getVerticalMask() {
		return verticalMask;
	}

	public void setVerticalMask(RealRangeArray verticalMask) {
		this.verticalMask = verticalMask;
	}
	
	public void splitVertically() {
		LOG.warn("VERT MASK NYI "+verticalMask);
	}

	public void debug() {
		SYSOUT.println("AbTabChunk >>> "+toString());
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Value: "+getValue()+"\n");
		sb.append("HorizontalMask: "+getHorizontalMask());
		if (getHorizontalMask() != null) {
			sb.append(getHorizontalMask().toString());
		}
		sb.append("VerticalMask: "+getVerticalMask());
		if (getVerticalMask() != null) {
			sb.append(getVerticalMask().toString());
		}
		return sb.toString();
	}

	public void createHorizontalMask(RealRangeArray horizontalGaps) {
			this.horizontalGaps = horizontalGaps;
			this.setHorizontalMask(horizontalGaps.inverse());
			if (horizontalMask != null && horizontalMask.size() > 1) {
				LOG.trace("HM"+horizontalMask);
			}
		}

	RealRangeArray createHorizontalMask() {
		horizontalMask = createMask(Direction.HORIZONTAL);
		return horizontalMask;
	}

	RealRangeArray createHorizontalMaskWithTolerance(double tolerance) {
		horizontalMask = createMask(Direction.HORIZONTAL);
		horizontalMask.extendRangesBy(tolerance);
		horizontalMask.sortAndRemoveOverlapping();
		return horizontalMask;
	}

	RealRangeArray createVerticalMask() {
		verticalMask = createMask(Direction.VERTICAL);
		return verticalMask;
	}

	RealRangeArray createHorizontalInverseMask(Real2Range bbox) {
		horizontalMask = createInverseMask(Direction.HORIZONTAL, bbox);
		return horizontalMask;
	}

	RealRangeArray createVerticalInverseMask(Real2Range bbox) {
		verticalMask = createInverseMask(Direction.VERTICAL, bbox);
		return verticalMask;
	}

	private RealRangeArray createMask(Direction direction) {
		RealRangeArray mask = new RealRangeArray();
		for (SVGElement element : elementList) {
			Real2Range bbox = element.getBoundingBox();
			RealRange range = (Direction.HORIZONTAL.equals(direction)) ? 
					bbox.getXRange() : bbox.getYRange();
			mask.add(range);
		}
		mask.sortAndRemoveOverlapping();
		mask.setDirection(direction);
		return mask;
	}
	
	private RealRangeArray createInverseMask(Direction direction, Real2Range bbox) {
		RealRangeArray mask = createMask(direction);
		mask.addCaps(bbox, direction);
		RealRangeArray inverseMask = mask.inverse();
		inverseMask.setDirection(direction);
		return inverseMask;
	}

	public String getHorizontalChunk(int serial) {
		RealRange range = getHorizontaRange(serial);
		StringBuilder sb = new StringBuilder();
		for (SVGElement element : elementList) {
			Real2Range bbox = element.getBoundingBox();
			RealRange xbbox = bbox.getXRange();
			if (range.includes(xbbox)) {
				sb.append(element.getValue());
			}
		}
		String s = sb.toString();
		LOG.trace("["+s+"]");
		return s;
	}

	public RealRangeArray getHorizontalGaps() {
		return horizontalGaps;
	}

	public RealRange getHorizontaRange(int serial) {
		return getHorizontalMask().get(serial);
	}

	public void populateChunk(List<SVGText> textList, RealRange pathBoxXRange,
				RealRange yGap) {
			List<? extends SVGElement> elementList = SVGElement.getElementListFilteredByRange(
					textList, yGap, RealRange.Direction.VERTICAL); 
			this.setElementList(elementList);
			RealRangeArray horizontalRanges = SVGElement.getRealRangeArray(elementList, RealRange.Direction.HORIZONTAL);
			horizontalRanges.addTerminatingCaps(pathBoxXRange.getMin(), pathBoxXRange.getMax());
			RealRangeArray horizontalGaps = horizontalRanges.inverse();
			horizontalGaps.removeLessThan(5.0);
			this.createHorizontalMask(horizontalGaps);
			this.createVerticalMask();
		}

	public Real2Range getBoundingBox() {
		if (bbox == null) {
			if (elementList != null) {
				bbox = SVGElement.createBoundingBox(elementList);
			}
		}
		return bbox;
	}

	public HtmlElement createHtmlElement() {
		throw new RuntimeException("Must overide createHtmlElement()");
	}

	protected HtmlElement createHtmlThroughTextStructurer() {
		List<SVGText> characters = SVGText.extractTexts((List<SVGElement>) this.getElementList());
		TextStructurer textStructurer = TextStructurer.createTextStructurerWithSortedLines(characters);
		HtmlElement htmlElement = textStructurer.createHtmlElement();
		return htmlElement;
	}
	
}
