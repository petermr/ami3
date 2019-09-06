package org.contentmine.eucl.euclid.euclid;


import org.contentmine.eucl.euclid.Bivariate;
import org.contentmine.eucl.euclid.Real2Array;
import org.contentmine.eucl.euclid.RealArray;
import org.junit.Assert;
import org.junit.Test;

public class BivariateTest {

	@Test
	public void testBivariate() {
		Real2Array r2a = new Real2Array(
				new RealArray(new double[]{1.47, 1.50, 1.52, 1.55, 1.57, 1.60, 1.63, 1.65, 1.68, 1.70, 1.73, 1.75, 1.78, 1.80, 1.83}),
				new RealArray(new double[]{52.21, 53.12, 54.48, 55.84, 57.20, 58.57, 59.93, 61.29, 63.11, 64.47, 66.28, 68.10, 69.92, 72.19, 74.46})
				);
		Bivariate bivariate = new Bivariate(r2a);
		Double slope = bivariate.getSlope();
		Assert.assertEquals("slope", 61.272, slope, 0.001);
		Double intercept = bivariate.getIntercept();
		Assert.assertEquals("intercept", -39.062, intercept, 0.001);
		Double r = bivariate.getCorrelationCoefficient();
		Assert.assertEquals("r", 0.9945, r, 0.001);
	}
}
