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
 * 2-dimensional point class
 * PROBABLY OBSOLETE - WE USE Real2 instead
 * 
 * Point2 represents a 2-dimensional point. It is one of a set of primitives
 * which can be combined to create and manipulate complex 2-dimensional objects.
 * Points can be transformed with rotation matrices or rotation-translation
 * matrices (Transform2), can be calculated from other primitives or can be used
 * to generate other primitives.
 * 
 * Default point is 0.0, 0.0
 * 
 * @author <A HREF=mailto:@p.murray-rust@mail.cryst.bbk.ac.uk>Peter Murray-Rust</A>
 * 
 * @author (C) P. Murray-Rust, 1996
 */

public class Point2 extends Real2 {

    /**
     * the coordinates of the point
     */
//    protected double[] p2_array;

    /**
     * constructor.
     */
    public Point2() {
        super();
    }

    /**
     * formed from point components
     * 
     * @param x
     * @param y
     */
    public Point2(double x, double y) {
        super(x, y);
    }

    /**
     * copy constructor
     * 
     * @param p
     */
    public Point2(Point2 p) {
        super(p);
    }

    /**
     * constructor from a double[] (or a RealArray)
     * 
     * @param f
     */
    public Point2(double[] f) {
        super(f);
    }

}
