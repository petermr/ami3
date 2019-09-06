package org.contentmine.graphics.svg.path;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Array;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.eucl.euclid.Vector2;

public class Arc {

	public enum Direction {
		PARALLEL,
		ANTIPARALLEL
	}
	
	private final static Logger LOG = Logger.getLogger(Arc.class);
	
	private Angle angle;
	private Angle angleEps = new Angle(0.01); // radians
	private Double radiusEps = 0.3;
	private CubicPrimitive cubicPrimitive;
	private Real2 zerothCoordinate;
	private Double radius;

	private Real2 centre;
	
	public Arc(CubicPrimitive cubic) {
		this.cubicPrimitive = cubic;
		this.angle = cubicPrimitive.getAngle();
		cubicPrimitive.getZerothCoord();
	}

//	/** creates arc with empty cubicPrimitive.
//	 * 
//	 */
//	private Arc() {
//		this.cubicPrimitive = new CubicPrimitive(new Real2Array(3));
//	}

	public void setAngleEps(Angle eps){
		this.angleEps = eps;
	}
	/** returns right angle change of direction.
	 * 
	 * see Angle.getRightAngle()
	 * 
	 * @return 0 if no angle or change != +- PI/2
	 */
	public Integer getRightAngle() {
		return (angle == null) ? 0 : angle.getRightAngle(angleEps);
	}

	/** try to interpret as arc of circle.
	 * 
	 * <p>
	 * Span is zeroth-> lastCoord. Length is d. Change in direction is angle
	 * Find mid-point and construct
	 * perpendicular of length (d/2) / tan(angle/2) to locate centre
	 * </p>
	 * 
	 * can we assume control points are nearly on circle?
	 * 
	 * @return null if impossible
	 */
	public Real2 getCentre() {
		if (centre == null) {
			Angle angle2 = angle.multiplyBy(0.5);
			double tana2 = angle2.tan();
			Integer ra = angle.greaterThan(new Angle(0)) ? 1 : -1;
			Real2 zerothCoord = cubicPrimitive.getZerothCoord();
			Real2 lastCoord = cubicPrimitive.getLastCoord();
			if (zerothCoord != null) {
				Real2Array coordArray = cubicPrimitive.getCoordArray();
				Real2 midPointSpan = zerothCoord.getMidPoint(lastCoord);
				Vector2 spanVector = new Vector2(lastCoord.subtract(zerothCoord));
				Vector2 vcentre = new Vector2(spanVector.getUnitVector().multiplyBy(1./tana2));
				Transform2 t2 = new Transform2(new double[]{
						0.0, -ra, 0.0, 
						ra, 0.0, 0.0, 
						0.0, 0.0, 1.0});
				vcentre.transformBy(t2);
				centre = midPointSpan.plus(vcentre);
				radius = zerothCoord.getDistance(centre);
				double radius0 = centre.getDistance(coordArray.get(0));
				double radius1 = centre.getDistance(coordArray.get(1));
				double delta0 = Math.abs(radius0 - radius);
				double delta1 = Math.abs(radius1 - radius);
				if (delta0 > radiusEps || delta1 > radiusEps) {
					centre = null;
				} else {
					LOG.trace("centre "+centre+" radius "+radius+" "+radius0+" "+radius1);
				}
			}
		}
		return centre;
	}
	
	public Double getRadius() {
		return radius;
	}

	/** get skeleton from arcs running in different directions.
	 * 
	 * calls getSkeleton(arc, ANTIPARALLEL). This is the most common case.
	 * 
	 * @param arc
	 * @return
	 */
	public Arc calculateMeanArc(Arc arc) {
		return calculateMeanArc(arc, Direction.ANTIPARALLEL);
	}

	/** average of two arcs to give central one.
	 * 
	 * <p>
	 * This is intricate as we cannot directly average coords for Direction.ANTIPARALLEL.
	 * We need to use Zeroth coordinate in a staggered mode:
	 * <pre>
	 *    Z   0   1   2  ---> top
	 *    2   1   0   Z  <--- bottom
	 * </pre>
	 * The result is a coordArray running in the same direction as "this". To reverse the direction
	 * for bottom use getReverseCubicPrimitive()
	 * </p>
	 * @param arc
	 * @return
	 */
	public Arc calculateMeanArc(Arc arc, Arc.Direction direction) {
		
		Real2Array thisCoordArray = cubicPrimitive.getCoordArray();
		Real2Array arcCoordArray = arc.cubicPrimitive.getCoordArray();

		Real2 thisZerothCoord = this.cubicPrimitive.getZerothCoord();
		Real2 arcZerothCoord = arc.cubicPrimitive.getZerothCoord();

		Real2 newZerothCoord = null;
		Real2Array newCoordArray = null;
		if (direction.equals(Direction.PARALLEL)) {
			newCoordArray = thisCoordArray.getMidPointArray(arcCoordArray);
			newZerothCoord = thisZerothCoord.getMidPoint(arcZerothCoord);
			LOG.trace(direction+"\n"+thisZerothCoord+" / "+thisCoordArray+"\n"+arcZerothCoord+" / "+arcCoordArray+"\n"+newZerothCoord+" / "+newCoordArray);
		} else {
			newZerothCoord = thisZerothCoord.getMidPoint(arcCoordArray.get(2));
			newCoordArray = new Real2Array(3);
			newCoordArray.setElement(0, thisCoordArray.get(0).getMidPoint(arcCoordArray.get(1)));
			newCoordArray.setElement(1, thisCoordArray.get(1).getMidPoint(arcCoordArray.get(0)));
			newCoordArray.setElement(2, thisCoordArray.get(2).getMidPoint(arcZerothCoord));
			LOG.trace(direction+"\n"+thisZerothCoord+" / "+thisCoordArray+"\n"+arcCoordArray+" / "+arcZerothCoord+"\n"+newZerothCoord+" / "+newCoordArray);
		}
		CubicPrimitive cubic = new CubicPrimitive(newCoordArray);
		Arc meanArc= new Arc(cubic);
		meanArc.centre = this.getCentre().getMidPoint(arc.getCentre());
		meanArc.radius = (this.getRadius() + arc.getRadius()) /2.;
		meanArc.setZerothCoord(newZerothCoord);
		return meanArc;
	}

	private void setZerothCoord(Real2 zerothCoord) {
		this.zerothCoordinate = zerothCoord;
	}

	public CubicPrimitive getCubicPrimitive() {
		return cubicPrimitive;
	}

	public CubicPrimitive getReverseCubicPrimitive() {
		Real2Array reverseArray = new Real2Array(3);
		Real2Array coordArray = cubicPrimitive.getCoordArray();
		reverseArray.setElement(0 , coordArray.get(1));
		reverseArray.setElement(1 , coordArray.get(0));
		reverseArray.setElement(2 , zerothCoordinate);
		CubicPrimitive reverseCubicPrimitive = new CubicPrimitive(reverseArray);
		return reverseCubicPrimitive;
	}

}
