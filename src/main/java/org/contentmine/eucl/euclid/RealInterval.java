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

/** an interval on the real line.
 * may be negative
 * it is allowed for the interval to be of zero length
 * @author pm286
 *
 */
public class RealInterval implements EuclidConstants {

	double x1;
	double x2;

	/** constructor.
	 * defaults to 0,1
	 *
	 */
	public RealInterval() {
		this(0., 1.0);
	}
	/** construct from Real2.
	 * 
	 * @param x1
	 * @param x2
	 */
	public RealInterval(double x1, double x2) {
		this.x1 = x1;
		this.x2 = x2;
	}
	
	/** copy constructor.
	 * 
	 * @param interval
	 */
	public RealInterval(RealInterval interval) {
		this.x1 = interval.x1;
		this.x2 = interval.x2;
	}

	/** get length.
	 * could be negative
	 * @return length
	 */
	public double getLength() {
		return x2 - x1;
	}
	
	/** constructor from range.
	 * interval direction is therefore always positive
	 * @param range
	 */
	public RealInterval(RealRange range) {
		this(range.getMin(), range.getMax());
	}
	
	/** get scale to other interval.
	 * scale is interval.length / this.length
	 * @param interval
	 * @return scale may be NaN
	 */
	public double scaleTo(RealInterval interval) {
		double scale = Double.NaN;
		try {
			scale = interval.getLength() / this.getLength();
		} catch (Throwable t) {
			//
		}
		return scale;
	}
	
	/** offset to translate xthis to xthat after scaling.
	 * X = (x - xthis)*scale + Xthat 
	 * X = offset + scale*xthis
	 * so offset = xthat - scale*xthis
	 * @param scale
	 * @param xthis
	 * @param xthat
	 * @return offset
	 */
    static double offsetTo(double scale, double xthis, double xthat) {
		return (xthat - xthis * scale);
	}
	
	/** offset to map one interval onto another.
	 * precise mapping
	 * @param interval
	 * @return offset applied after scaling
	 */
	public double offsetTo(RealInterval interval) {
		return this.offsetTo(interval, this.scaleTo(interval));
	}

	/** offset to map one interval onto another.
	 * maps x1 of each onto eachg other
	 * @param interval to map to
	 * @param scale
	 * @return offset applied after scaling
	 */
	public double offsetTo(RealInterval interval, double scale) {
		return interval.x1 - scale * this.x1;
	}

	/** gets midpoint.
	 * 
	 * @return midpoint
	 */
	public double midPoint() {
		return (x1 + x2) / 2.0;
	}
}
