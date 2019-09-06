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
 * Polar coordinates (r, theta)
 * 
 * @author (C) P. Murray-Rust, 1996
 */
public class Polar implements EuclidConstants {
    /**
     * the radius from the origin
     */
    double r = 0.0;
    /**
     * the angle with the X-axis (anticlockwise)
     */
    double theta = 0.0;
    /**
     * constructor.
     */
    public Polar() {
        super();
    }
    /**
     * constructor.
     * 
     * @param x
     * @param y
     */
    public Polar(double x, double y) {
        super();
        r = Math.sqrt(x * x + y * y);
        theta = Math.atan2(y, x);
    }
    /**
     * constructor.
     * 
     * @param a
     * @param b
     */
    public Polar(double a, Angle b) {
        r = a;
        theta = b.getAngle();
    }
    /**
     * constructor.
     * 
     * @param c
     */
    public Polar(Complex c) {
        r = c.getR();
        theta = c.getTheta().getAngle();
    }
    /**
     * constructor.
     * 
     * @param a
     */
    public Polar(Polar a) {
        r = a.r;
        theta = a.theta;
    }
    /**
     * gets radial part
     * 
     * @return radius
     */
    public double getR() {
        return r;
    }
    /**
     * gets angular part
     * 
     * @return angle
     */
    public Angle getTheta() {
        return new Angle(theta);
    }
    /**
     * add two polars
     * 
     * @param a2
     * @return new polar
     */
    public Polar plus(Polar a2) {
        Complex tmp = new Complex(getX() + a2.getX(), getY() + a2.getY());
        Polar temp = new Polar(tmp);
        return temp;
    }
    /**
     * subtract two polars
     * 
     * @param a2
     * @return new polar
     */
    public Polar subtract(Polar a2) {
        Complex tmp = new Complex(getX() - a2.getX(), getY() - a2.getY());
        Polar temp = new Polar(tmp);
        return temp;
    }
    /**
     * unary minus
     */
    public void subtract() {
        theta = theta + Math.PI;
    }
    /**
     * multiply a polar by a polar
     * 
     * @param f
     * @return new polar
     */
    public Polar multiplyBy(Polar f) {
        Polar temp = new Polar(this);
        temp.r = r * f.r;
        temp.theta = theta + f.theta;
        return temp;
    }
    /**
     * multiply a polar by a scalar
     * 
     * @param f
     * @return new polar
     */
    public Polar multiplyBy(double f) {
        Polar temp = new Polar(this);
        temp.r *= f;
        return temp;
    }
    /**
     * divide a polar by a polar
     * 
     * @param f
     * @return polar
     * @throws EuclidRuntimeException
     */
    public Polar divideBy(Polar f) throws EuclidRuntimeException {
        Polar temp = new Polar(this);
        if (Real.isZero(f.r, Real.getEpsilon())) {
            throw new EuclidRuntimeException();
        }
        temp.r = r / f.r;
        temp.theta = theta - f.theta;
        return temp;
    }
    /**
     * are two polar equal
     * 
     * @param a
     * @return equals
     */
    public boolean isEqualTo(Polar a) {
        return Real.isEqual(r, a.r) && Real.isEqual(theta, a.theta);
    }
    /**
     * get X, Y and XY coords
     * 
     * @return coord
     */
    public double getX() {
        double temp = r * Math.cos(theta);
        return temp;
    }
    /**
     * get y.
     * 
     * @return coord
     */
    public double getY() {
        double temp = r * Math.sin(theta);
        return temp;
    }
    /**
     * get coordinates.
     * 
     * @return coordinates
     */
    public Real2 getXY() {
        return new Real2(r * Math.cos(theta), r * Math.sin(theta));
    }
    /**
     * to string.
     * 
     * @return string
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Polar: " + r + EC.S_COMMA + theta);
        return sb.toString();
    }
}
