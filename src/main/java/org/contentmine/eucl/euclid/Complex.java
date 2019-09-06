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
 * A complex number derived from Real2
 * 
 * Complex represents a complex number A reasonable number of arithmetic
 * operations are included DeMoivre's theorem is used for some of them so there
 * may be quicker implementations elsewhere.
 * 
 * @author (C) P. Murray-Rust, 1996
 */
public class Complex extends Real2 {

    /**
     * constructor.
     */
    public Complex() {
        super();
    }

    /**
     * real component only
     * 
     * @param a
     */
    public Complex(double a) {
        super(a, 0.0);
    }

    /**
     * from components
     * 
     * @param a
     * @param b
     */
    public Complex(double a, double b) {
        super(a, b);
    }

    /**
     * from base class
     * 
     * @param a
     */
    public Complex(Real2 a) {
        this.x = a.x;
        this.y = a.y;
    }

    /**
     * in polar coords
     * 
     * @param r
     * @param th
     */
    public Complex(double r, Angle th) {
        Polar p = new Polar(r, th);
        x = p.getX();
        y = p.getY();
    }

    /**
     * construct from polar
     * 
     * @param p
     */
    public Complex(Polar p) {
        x = p.getX();
        y = p.getY();
    }

    /**
     * copy constructor
     * 
     * @param a
     */
    public Complex(Complex a) {
        this.x = a.x;
        this.y = a.y;

    }

    /**
     * gets real part.
     * 
     * @return real part
     */
    public double getReal() {
        return x;
    }

    /**
     * gets imaginary part.
     * 
     * @return imaginary
     * 
     */
    public double getImaginary() {
        return y;
    }

    /**
     * unary minus MODIFIES object
     */
    public void negative() {
        this.x = -this.x;
        this.y = -this.y;
    }

    /**
     * multiply a complex by a complex.
     * 
     * @param f
     * @return complex
     */
    public Complex multiply(Complex f) {
        Complex temp = new Complex(this);
        temp.x = x * f.x - y * f.y;
        temp.y = x * f.y + y * f.x;
        return temp;
    }

    /**
     * divide a complex by a complex.
     * 
     * @param f
     * @return complex
     * @throws EuclidRuntimeException
     */
    public Complex divideBy(Complex f) throws EuclidRuntimeException {
        double denom = f.x * f.x + f.y * f.y;
        if (Real.isZero(denom, Real.getEpsilon())) {
            throw new EuclidRuntimeException("cannot divide by zero");
        }
        Complex temp = new Complex(f.x, -f.y);
        temp = new Complex((temp.multiply(this)).multiplyBy(1 / denom));
        return temp;
    }

    /**
     * get as polar coords.
     * 
     * @return radius
     */
    public double getR() {
        double t = Math.sqrt(x * x + y * y);
 //       double t = x * x + y * y;
        return t;
    }

    /**
     * angle.
     * 
     * @return the angle
     */
    public Angle getTheta() {
        return new Angle(y, x);
    }

    /**
     * polar object.
     * 
     * @return polar object
     */
    public Polar getPolar() {
        return new Polar(getR(), getTheta());
    }

    /**
     * complex square root.
     * 
     * @param a
     * @return complex sqrt
     */
    public static Complex sqrt(Complex a) {
        Polar temp = new Polar(a);
        temp.r = Math.sqrt(temp.r);
        temp.theta *= 0.5;
        return new Complex(temp);
    }

    /**
     * to string.
     * 
     * @return string
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(x + EC.S_COMMA + y);
        return sb.toString();
    }
}
