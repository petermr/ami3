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

/** a 2D window 
* (bounding box, viewport, etc)
* Used with Transform2
@author (C) P. Murray-Rust, 2001
*/
public class Window2 extends Object {
	
	public final static int X = 0;
	public final static int Y = 1;
	Real2 origin;
	Real2 farPoint;
	Real2 dim;    
    public Window2(Real2 origin, Real2 farPoint) {
    	this.origin = new Real2(origin);
    	this.farPoint = new Real2(farPoint);
    	getDim();
    }
    public Real2 getDim() {
    	this.dim = new Real2(farPoint.getX() - origin.getX(), farPoint.getY() - origin.getY());
    	return this.dim;
    }
    public Real2 getOrigin() {return origin;}
    public Real2 getFarPoint() {return farPoint;}
/** get extent of a given axis (X or Y); */
    public Real2 getExtent(int axis) {
    	if (axis == X) return new Real2(origin.getX(), farPoint.getX());
    	if (axis == Y) return new Real2(origin.getY(), farPoint.getY());
    	return null;
    }
    
    /** convenience because negative lengths not allowed in awt */
    public void drawRect(java.awt.Graphics g) {
    	int x1 = (int) origin.getX();
    	int x2 = (int) farPoint.getX();
    	int y1 = (int) origin.getY();
    	int y2 = (int) farPoint.getY();
    	if (x1 > x2) {int t = x2; x2 = x1; x1 = t;}
    	if (y1 > y2) {int t = y2; y2 = y1; y1 = t;}
    	g.drawRect(x1, y1, x2-x1, y2-y1);
    }
    /** convenience because negative lengths not allowed in awt */
    public void fillRect(java.awt.Graphics g) {
    	int x1 = (int) origin.getX();
    	int x2 = (int) farPoint.getX();
    	int y1 = (int) origin.getY();
    	int y2 = (int) farPoint.getY();
    	if (x1 > x2) {int t = x2; x2 = x1; x1 = t;}
    	if (y1 > y2) {int t = y2; y2 = y1; y1 = t;}
    	g.fillRect(x1, y1, x2-x1, y2-y1);
    }
    public void transformBy(Transform2 tr) {
    	origin.transformBy(tr);
    	farPoint.transformBy(tr);
    	getDim();
	}
	public String toString() {
		return "Window2: "+origin+"/"+farPoint+"("+dim+")";
	}
}
