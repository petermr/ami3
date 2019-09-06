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

/**
 * a 2-D vector relationship with Complex and Polar not fully worked out. It may
 * simply be a matter of style which is used.
 * 
 * @author (C) P. Murray-Rust, 1996
 */
public class Vector2 extends Real2 {

    /**
     * constructor.
     * 
     * @param r
     *            coordinates
     */
    public Vector2(Real2 r) {
        super(r);
    }

    /**
     * constructor.
     * 
     * @param x
     * @param y
     */
    public Vector2(double x, double y) {
        this(new Real2(x, y));
    }

    /**
     * I *think* I have written this so that the angle is positive as this
     * rotates anticlockwise to vector.
     * 
     * @param v
     * @return angle or null
     */
    public Angle getAngleMadeWith(Vector2 v) {
    	Angle angle = null;
    	if (v != null) {
	        double theta0 = Math.atan2(v.x, v.y);
	        double theta1 = Math.atan2(this.x, this.y);
	        angle = new Angle(theta0 - theta1);
    	}
    	return angle;
    }

    /** is vector parallel to another
     * calculates angle between vectors
     * @param v
     * @param eps tolerance in radians (should be non-negative)
     * @return true if abs(angle) (rad) < eps
     */
    public boolean isParallelTo(Vector2 v, double eps) {
    	Angle a = this.getAngleMadeWith(v);
    	return Math.abs(a.getRadian()) < eps;
    }


    /** is vector antiparallel to another
     * calculates angle between vectors
     * @param v
     * @param eps tolerance in radians (should be non-negative)
     * @return true if abs(angle) (rad) < eps
     */
    public boolean isAntiParallelTo(Vector2 v, double eps) {
    	Angle a = this.getAngleMadeWith(v);
    	return Math.abs(Math.abs(a.getRadian())-Math.PI) < eps;
    }

    /** perp product (Hill).
     * this.getX() * vector.getY() - this.getY() * vector.getX();
     * @param v
     * @return product 
     */
    public double getPerpProduct(Vector2 v) {
    	return this.getX() * v.getY() - this.getY() * v.getX();
    }
    
    /**
     * projection of this onto vector. does not alter this. result = vector.norm() *
     * (this.norm() dot vector.norm())
     *
     * @param v vector to project onto
     * @return projected vector
     */
    public Vector2 projectOnto(Vector2 v2) {
        Real2 unit2 = v2.getUnitVector();
        Real2 unit = this.getUnitVector();
        double dot = unit2.dotProduct(unit);
        Vector2 projection = new Vector2(unit2.multiplyBy(this.getLength() * dot));
        return projection;
    }

}
