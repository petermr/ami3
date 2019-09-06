/**
 *    Copyright 2011 Peter Murray-Rust
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.contentmine.eucl.euclid;

/** a two dimensional area.
 * may have negative ranges
 * @author pm286
 *
 */
public class Real2Interval implements EuclidConstants {

	RealInterval xInterval;
	RealInterval yInterval;
	/** constructor.
	 * defaults to 0,1 0,1
	 *
	 */
	public Real2Interval() {
		this(new RealInterval(), new RealInterval());
	}
	
	/** constructor from 1-D intervals.
	 * 
	 * @param xInterval
	 * @param yInterval
	 */
	public Real2Interval(RealInterval xInterval, RealInterval yInterval) {
		this.xInterval = new RealInterval(xInterval);
		this.yInterval = new RealInterval(yInterval);
	}
	
	/** copy constructor.
	 * 
	 * @param xyInterval
	 */
	public Real2Interval(Real2Interval xyInterval) {
		this(xyInterval.xInterval, xyInterval.yInterval);
	}
	
	/** constructor from range.
	 * interval direction is therefore always positive
	 * @param range2
	 */
	public Real2Interval(Real2Range range2) {
		this(new RealInterval(range2.getXRange()),
			 new RealInterval(range2.getYRange()));
	}

	/** get x and y scales to other interval2.
	 * exact scaling
	 * @param interval2
	 * @return scales never null but component(s) may be NaN
	 */
	public double[] scalesTo(Real2Interval interval2) {
		double[] scales = new double[2];
		scales[0] = xInterval.scaleTo(interval2.xInterval);
		scales[1] = yInterval.scaleTo(interval2.yInterval);
		return scales;
	}
	
	/** get isotropic scale to other interval2.
	 * takes minimum value of x and y scales
	 * @param interval2
	 * @return scale may be NaN
	 */
	public double scaleTo(Real2Interval interval2) {
		double scale = Double.NaN;
		double[] scales = scalesTo(interval2);
		if (Double.isNaN(scales[0])) {
			scale = scales[1];
		} else if (Double.isNaN(scales[1])) {
			scale = scales[0];
		} else {
			scale = Math.min(scales[0], scales[0]);
		}
		return scale;
	}
	
	/** offsets to overlap origins in each
	 * scales these by scales and then calculates offsets to overlap midpoints
	 * @param interval2
	 * @param scales (some might be NaN)
	 * @return offsets (components might be NaN.
	 */
	public double[] offsetsTo(Real2Interval interval2, double[] scales) {
		double[] offsets = new double[2];
		offsets[0] = xInterval.offsetTo(interval2.xInterval, scales[0]);
		offsets[1] = yInterval.offsetTo(interval2.yInterval, scales[1]);
		return offsets;
	}
	
	/** offsets to overlap origins in each
	 * isotropic scales
	 * scales these by scale 
	 * @param interval2
	 * @param scale
	 * @return offsets (components might be NaN.
	 */
	public double[] offsetsTo(Real2Interval interval2, double scale) {
		return offsetsTo(interval2, new double[]{scale, scale});
	}
	
	/** offset to map one interval onto another.
	 * exact mapping in both directions
	 * @param interval2
	 * @return offset applied after scaling
	 */
	public double[] offsetsTo(Real2Interval interval2) {
		double[] offsets = new double[2];
		offsets[0] = xInterval.offsetTo(interval2.xInterval);
		offsets[1] = yInterval.offsetTo(interval2.yInterval);
		return offsets;
		
	}

	/** gets midpoint.
	 * 
	 * @return midpoint of both axes
	 */
	public Real2 midPoint() {
		return new Real2(xInterval.midPoint(), yInterval.midPoint());
	}
	
//	/** offsets to overlap given points in each
//	 * scales these by scales and then calculates offsets to overlap midpoints
//	 * @param interval2
//	 * @param scales (some might be NaN)
//	 * @return offsets (components might be NaN.
//	 */
//	public double[] offsetsTo(Real2Interval interval2, double scale, Real2 xyThis, Real2 xy2) {
//		double[] offsets = new double[2];
//		offsets[0] = xInterval.offsetTo(interval2.xInterval, scales[0]);
//		offsets[1] = yInterval.offsetTo(interval2.yInterval, scales[1]);
//		return offsets;
//	}
	
}
