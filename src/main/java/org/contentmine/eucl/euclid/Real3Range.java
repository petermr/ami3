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

import org.contentmine.eucl.euclid.Axis.Axis3;

/**
 * 3-D double limits
 * 
 * Contains 3 RealRanges. Can therefore be used to describe 3-dimensional limits
 * (for example axes of 3-D graphs, boxes in graphics, limits of a molecule,
 * etc.)
 * <P>
 * Default is three invalid RealRange components.
 * 
 * @author (C) P. Murray-Rust, 1996
 */

public class Real3Range implements EuclidConstants {

    private RealRange[] xyzrange = new RealRange[3];

    /**
     * default is three default RealRanges
     */
    public Real3Range() {
        for (int i = 0; i < 3; i++) {
            xyzrange[i] = new RealRange();
        }
    }

    /**
     * initialise with min and max values; takes COPIES
     * 
     * @param xr
     * @param yr
     * @param zr
     */
    public Real3Range(RealRange xr, RealRange yr, RealRange zr) {
        setRanges(xr, yr, zr);
    }

    /** reset ranges.
     * clears any existing content
     * @param xr
     * @param yr
     * @param zr
     */
    public void setRanges(RealRange xr, RealRange yr, RealRange zr) {
        xyzrange[0] = new RealRange(xr);
        xyzrange[1] = new RealRange(yr);
        xyzrange[2] = new RealRange(zr);
    }

    /**
     * copy constructor
     * 
     * @param r
     */
    public Real3Range(Real3Range r) {
        for (int i = 0; i < 3; i++) {
            xyzrange[i] = new RealRange(r.xyzrange[i]);
        }
    }
    
    /**
     * is equal to.
     * 
     * @param r3
     * @return tru if equals
     */
    public boolean isEqualTo(Real3Range r3, double eps) {
        return (xyzrange[0].isEqualTo(r3.xyzrange[0], eps)
                && xyzrange[1].isEqualTo(r3.xyzrange[1], eps) && xyzrange[2]
                .isEqualTo(r3.xyzrange[2], eps));
    }

    /**
     * is equal to.
     * 
     * @param r3
     * @return tru if equals
     */
    @Deprecated
    public boolean isEqualTo(Real3Range r3) {
        return (xyzrange[0].isEqualTo(r3.xyzrange[0])
                && xyzrange[1].isEqualTo(r3.xyzrange[1]) && xyzrange[2]
                .isEqualTo(r3.xyzrange[2]));
    }

    /**
     * add two ranges. applies plus to each of x, y, z. creates minima and
     * maxima of this, r3
     * 
     * @param r3
     * @return new range
     */
    public Real3Range plus(Real3Range r3) {
        return new Real3Range(xyzrange[0].plus(r3.xyzrange[0]), xyzrange[1]
                .plus(r3.xyzrange[1]), xyzrange[2].plus(r3.xyzrange[2]));
    }

    /**
     * get xrange
     * 
     * @return range
     */
    public RealRange getXRange() {
        return xyzrange[0];
    }

    /**
     * get yrange
     * 
     * @return range
     */
    public RealRange getYRange() {
        return xyzrange[1];
    }

    /**
     * get zrange
     * 
     * @return range
     */
    public RealRange getZRange() {
        return xyzrange[2];
    }

    /**
     * add a single value
     * 
     * @param ax
     * @param value
     */
    public void add(Axis3 ax, double value) {
        xyzrange[ax.value].add(value);
    }

    /**
     * add a single value
     * 
     * @param ax
     * @param range
     */
    public void add(Axis3 ax, RealRange range) {
        xyzrange[ax.value] = range;
    }

    /**
     * add a single value - not for general use
     * 
     * @param axis
     * @param value
     */
    protected void add(int axis, double value) {
        xyzrange[axis].add(value);
    }

    /**
     * is a Point3 within a Real3Range
     * 
     * @param p
     * @return includes
     */
    public boolean includes(Point3 p) {
        double[] coords = p.getArray();
        return (xyzrange[0].includes(coords[0])
                && xyzrange[1].includes(coords[1]) && xyzrange[2]
                .includes(coords[2]));
    }

    /**
     * add a Point3 to a range
     * 
     * @param p
     */
    public void add(Point3 p) {
        double[] coords = p.getArray();
        xyzrange[0].add(coords[0]);
        xyzrange[1].add(coords[1]);
        xyzrange[2].add(coords[2]);
    }
    
    /** get point with min x, y, z.
     * 
     * @return the point.
     */
    public Point3 getMinPoint3() {
        return new Point3(
            xyzrange[0].getMin(), xyzrange[1].getMin(), xyzrange[2].getMin());
    }

    /** get point with max x, y, z.
     * 
     * @return the point.
     */
    public Point3 getMaxPoint3() {
        return new Point3(
            xyzrange[0].getMax(), xyzrange[1].getMax(), xyzrange[2].getMax());
    }

    /** transforms range.
     * modifies this
     * @param transform
     */
    public void transformEquals(Transform3 transform) {
        Point3 minxyz = getMinPoint3();
        Point3 maxxyz = getMaxPoint3();
        minxyz.transformEquals(transform);
        maxxyz.transformEquals(transform);
        Real3Range newRange3 = new Real3Range();
        newRange3.add(minxyz);
        newRange3.add(maxxyz);
        int i = 0;
        for (RealRange xyzr : newRange3.xyzrange) {
            this.xyzrange[i++] = new RealRange(xyzr);
        }
    }
    
    /**
     * to string.
     * 
     * @return string
     */
    public String toString() {
        return EC.S_LBRAK + xyzrange[0] + EC.S_COMMA + xyzrange[1] + EC.S_COMMA
                + xyzrange[2] + EC.S_RBRAK;
    }
}
