package org.contentmine.graphics.svg.path;

import java.awt.geom.GeneralPath;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Array;
import org.contentmine.graphics.svg.SVGPathPrimitive;
import org.contentmine.graphics.svg.path.Arc.Direction;

public class LinePrimitive extends SVGPathPrimitive {

	private final static Logger LOG = Logger.getLogger(LinePrimitive.class);
	
	public final static String TAG = "L";

	public LinePrimitive(Real2 real2) {
		this.coordArray = new Real2Array();
		coordArray.add(real2);
	}

	public String getTag() {
		return TAG;
	}
	
	public String toString() {
		return TAG + formatCoords(coordArray.get(0));
	}

	@Override
	public void operateOn(GeneralPath path) {
		if (coordArray != null) {	
			Real2 coord = coordArray.elementAt(0);
			path.lineTo(coord.x, coord.y);
		}
	}

	@Override
	/** returns zero
	 * @return 0.0
	 */
	public Angle getAngle() {
		return new Angle(0.0);
	}

	/** get skeleton from arcs running in different directions.
	 * 
	 * calls getSkeleton(arc, ANTIPARALLEL). This is the most common case.
	 * 
	 * @param arc
	 * @return
	 */
	public LinePrimitive calculateMeanLine(LinePrimitive line) {
		return calculateMeanLine(line, Direction.ANTIPARALLEL);
	}

	/** average of two lines to give central one.
	 * 
	 * <p>
	 * This is intricate as we cannot directly average coords for Direction.ANTIPARALLEL.
	 * We need to use Zeroth coordinate in a staggered mode:
	 * <pre>
	 *    Z   0  ---> top
	 *    0   Z  <--- bottom
	 * </pre>
	 * The result is a coordArray running in the same direction as "this". To reverse the direction
	 * for bottom use getReverseLinePrimitive()
	 * </p>
	 * @param line
	 * @return
	 */
	public LinePrimitive calculateMeanLine(LinePrimitive line, Arc.Direction direction) {
		Real2 thisZerothCoord = this.getZerothCoord();
		Real2 lineZerothCoord = line.getZerothCoord();
		Real2 thisCoord = this.getCoordArray().get(0);
		Real2 lineCoord = line.getCoordArray().get(0);

		Real2 newZerothCoord = null;
		Real2 newCoord = null;
		if (direction.equals(Direction.PARALLEL)) {
			newCoord = thisCoord.getMidPoint(lineCoord);
			newZerothCoord = thisZerothCoord.getMidPoint(lineZerothCoord);
		} else {
			newCoord = thisCoord.getMidPoint(lineZerothCoord);
			newZerothCoord = thisZerothCoord.getMidPoint(lineCoord);
		}
		LinePrimitive meanLine = new LinePrimitive(newCoord);
		meanLine.setZerothCoord(newZerothCoord);
		return meanLine;
	}

	public Real2Array getReverseCoordArray() {
		Real2Array real2Array = new Real2Array(1);
		real2Array.setElement(0, zerothCoord);
		return real2Array;
	}
	
}
