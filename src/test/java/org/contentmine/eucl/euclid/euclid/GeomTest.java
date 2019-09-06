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

package org.contentmine.eucl.euclid.euclid;

import org.contentmine.eucl.euclid.Line3;
import org.contentmine.eucl.euclid.Plane3;
import org.contentmine.eucl.euclid.Point3;
import org.contentmine.eucl.euclid.Transform3;
import org.contentmine.eucl.euclid.Vector3;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * parent class for geom tests.
 * 
 * @author pmr
 * 
 */
public class GeomTest {

	Line3 l0;

	Line3 l100000;

	Line3 l123456;

	Plane3 pl0;

	Plane3 pl1000;

	Plane3 pl0100;

	Plane3 pl0010;

	Plane3 pl1234;

	Plane3 pl1111;

	Point3 p0;

	Point3 p000;

	Point3 p100;

	Point3 p010;

	Point3 p001;

	Point3 p111;

	Point3 p123;

	Point3 p321;

	Transform3 tr0;

	Transform3 tr1;

	Transform3 tr2;

	Vector3 v0;

	Vector3 v000;

	Vector3 v100;

	Vector3 v010;

	Vector3 v001;

	Vector3 v123;

	Vector3 v321;

	final static double s14 = Math.sqrt(14.);

	final static double s3 = Math.sqrt(3.);

	final static double s2 = Math.sqrt(2.);

	/**
	 * setup.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		l0 = new Line3();
		l100000 = new Line3(new Point3(0., 0., 0.), new Vector3(1., 0., 0.));
		l123456 = new Line3(new Point3(4., 5., 6.), new Vector3(1., 2., 3.));

		pl0 = new Plane3();
		pl1000 = new Plane3(1., 0., 0., 0);
		pl0100 = new Plane3(0., 1., 0., 0.);
		pl0010 = new Plane3(new double[] { 0., 0., 1., 0. });
		pl1234 = new Plane3(new double[] { 1., 2., 3. }, 4.);
		pl1111 = new Plane3(new Vector3(1., 1., 1.), 1.);

		p0 = new Point3();
		p000 = new Point3(0., 0., 0.);
		p100 = new Point3(1., 0., 0.);
		p010 = new Point3(0., 1., 0.);
		p001 = new Point3(0., 0., 1.);
		p111 = new Point3(1., 1., 1.);
		p123 = new Point3(1., 2., 3.);
		p321 = new Point3(3., 2., 1.);

		tr0 = new Transform3();
		tr1 = new Transform3("x, -y, z");
		tr2 = new Transform3("-x, -y, z");

		v0 = new Vector3();
		v000 = new Vector3(0., 0., 0.);
		v100 = new Vector3(1., 0., 0.);
		v010 = new Vector3(0., 1., 0.);
		v001 = new Vector3(0., 0., 1.);
		v123 = new Vector3(1., 2., 3.);
		v321 = new Vector3(3., 2., 1.);
	}

	/** test */
	@Test
	public void testDummy() {
		Assert.assertNotNull(p0);
	}

}
