package org.contentmine.eucl.euclid;

/** manages the contents of a Bin in Univariate as a sub-Univariate
 * 
 * 
 * @author pm286
 *
 */
public class UnivariateBin {

	private RealArray array;
	
	public UnivariateBin() {
		array = new RealArray();
	}
	
	public void add(double x) {
		array.addElement(x);
	}
	
	public Univariate getUnivariate() {
		return new Univariate(array);
	}

	public RealArray getArray() {
		return array;
	}

	public int getCount() {
		return array.size();
	}

}
