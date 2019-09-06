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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * 
 Univariate stats.
 * 
 * @author (C) P. Murray-Rust, 2001, 2004
 */

public class Univariate {

	private static Logger LOG = Logger.getLogger(Univariate.class);

	RealArray realArray;
	double[] array;
	int count;
	double mean = Double.NaN;
	double sum = Double.NaN;
	double median = Double.NaN;
	double variance = Double.NaN;
	double xMax = Double.NaN;
	double xMin = Double.NaN;
	double lowXRange = Double.NaN;
	double stdev = Double.NaN;
	RealArray deviateArray = null;
	RealArray zValueArray = null;

	boolean gotMean = false;
	boolean gotVariance = false;
	boolean gotStandardDeviation = false;
	boolean isSorted = false;

	int binCount;
	int[] binCounts; // count of each bin
	int[] binStart; // start index of each bin
	double deltaX = Double.NaN; // bin step
	RealArray binArray = null; // lower bin values
	private List<UnivariateBin> binList;

	private List<Real2> valueFrequencyList;

	/** default constructor. */
	public Univariate() {
		init();
		realArray = new RealArray();
	}

	/**
	 * creates from data array. copies realArray
	 * 
	 * @param realArray
	 *            the data
	 */
	public Univariate(RealArray realArray) {
		init();
		setArray(realArray);
	}

	void init() {
		setBinCount(10);
	}
	
	/**
	 * sets data copies realArray
	 * 
	 * @param realArray
	 *            the data
	 */
	public void setArray(RealArray realArray) {
		this.realArray = new RealArray(realArray);
		getCount();
	}

	/**
	 * set bin count.
	 * 
	 * @param binCount
	 *            the number of bins
	 */
	public void setBinCount(int binCount) {
		this.binCount = binCount;
	}

	/**
	 * get bin count.
	 * 
	 * @return binCount (default 10)
	 */
	public int getBinCount() {
		return binCount;
	}

	/**
	 * get number of data points.
	 * 
	 * @return count
	 */
	public int getCount() {
		count = realArray.size();
		return count;
	}

	/**
	 * get minimum value.
	 * 
	 * @return minimum value
	 */
	public double getMin() {
		xMin = realArray.smallestElement();
		return xMin;
	}

	/**
	 * get maximum value.
	 * 
	 * @return maximum value
	 */
	public double getMax() {
		xMax = realArray.largestElement();
		return xMax;
	}

	/**
	 * get mean value. @ not enough points
	 * 
	 * @return mean value; Double NaN if no points
	 */
	public double getMean() {
		if (!gotMean || mean == Double.NaN) {
			count = realArray.size();
			if (count == 0) {
				mean = Double.NaN;
			} else {
				sum = realArray.sumAllElements();
				mean = sum / (double) count;
				gotMean = true;
			}
		}
		return mean;
	}

	/**
	 * get variance. @ not enough points
	 * 
	 * @return variance
	 */
	public double getVariance() {
		if (!gotVariance) {
			getCount();
			if (count < 2) {
				throw new RuntimeException("Only one point");
			}
			getMean();
			double sumx2 = 0.0;
			getDeviateValues();
			double x[] = deviateArray.getArray();
			for (int i = 0; i < count; i++) {
				sumx2 += x[i] * x[i];
			}
			// variance = (sumx2 - count * mean * mean) / (double) (count - 1);
			variance = (sumx2) / (double) (count - 1);
			gotVariance = true;
		}
		return variance;
	}

	/**
	 * get standard deviation. @ not enough points
	 * 
	 * @return standard deviation
	 */
	public double getStandardDeviation() {
		getVariance();
		stdev = Math.sqrt(variance);
		return stdev;
	}

	/**
	 * get standard error. @ not enough points
	 * 
	 * @return standard error
	 */
	public double getStandardError() {
		getVariance();
		return (Math.sqrt(variance / (double) count));
	}

	/**
	 * get data points.
	 * 
	 * @return the points
	 */
	public double[] getArray() {
		array = realArray.getArray();
		return array;
	}

	/**
	 * get sorted data points. modifies this
	 * 
	 * @return the points
	 */
	public double[] getSortedArray() {
		if (!isSorted) {
			realArray.sortAscending();
			getArray();
			isSorted = true;
		}
		return array;
	}

	/**
	 * get normalized values. array transformed by subtracting mean and dividing
	 * by standard deviation. result (the "z"-values") therefore have mean of
	 * zero and stdev of 1.0. does not modify this.
	 * 
	 * @ arrays too small
	 * 
	 * @return the normalized values
	 */
	public RealArray getNormalizedValues() {
		double[] array = realArray.getArray();
		getMean();
		getDeviateValues();
		double[] dvArray = deviateArray.getArray();
		getStandardDeviation();
		double[] normArray = new double[array.length];
		for (int i = 0; i < array.length; i++) {
			normArray[i] = (dvArray[i]) / stdev;
		}
		zValueArray = new RealArray(normArray);
		return zValueArray;
	}

	/**
	 * get deviate values. array transformed by subtrating mean. result
	 * therefore has mean of zero. does not modify this.
	 * 
	 * @ arrays too small
	 * 
	 * @return the deviate values
	 */
	public RealArray getDeviateValues() {
		double[] array = realArray.getArray();
		getMean();
		double[] dvArray = new double[array.length];
		for (int i = 0; i < array.length; i++) {
			dvArray[i] = (array[i] - mean);
		}
		deviateArray = new RealArray(dvArray);
		return deviateArray;
	}

	/**
	 * get quantile.
	 * 
	 * @param q
	 *            the value of the quantile (0 =< q =< 1.0) @ not enough points
	 * @return quantile
	 */
	public double getQuantile(double q) {
		double quantile = Double.NaN;
		if (q > 1.0 || q < 0.0) {
			throw new RuntimeException("Quantile value out of range: " + q);
		}
		getSortedArray();
		// count of quantile element
		double dindex = (count + 1) * q;
		int index = (int) dindex;
		int flindex = (int) Math.floor(dindex);
		int cindex = (int) Math.ceil(dindex);
		// exact match
		if ((dindex - index) == 0) {
			quantile = array[index - 1];
		} else {
			quantile = q * array[flindex - 1] + (1 - q) * array[cindex - 1];
		}
		return quantile;
	}

	/**
	 * get median. @ not enough points
	 * 
	 * @return median
	 */
	public double getMedian() {
		median = getQuantile(0.50);
		return median;
	}

	void calculateSummaryStats() {
		try {
			getMin();
			getMax();
			getMean();
			getVariance();
			getStandardDeviation();
		} catch (Exception e) {
			LOG.warn("too few points");
		}
	}

	/**
	 * gets the xvalues for the bins.
	 * 
	 * These are the low value for each bin. bin(i) runs from
	 * xvalue(i)-xvalue(i+1)
	 * 
	 * the median value for the bin is xvalue(i)+deltax
	 * 
	 * the last bin is from xvalue(binCount-1) ... xvalue(binCount-1)+deltaX
	 * 
	 * @return xvalues
	 */
	public RealArray getXValues() {
		getLowXRangeAndDeltaX();
		binArray = new RealArray(binCount, xMin, deltaX);
		return binArray;
	}

	/**
	 * gets the median xvalues for the bins.
	 * 
	 * These are the median for each bin. bin(i) runs from xvalue(i)-xvalue(i+1)
	 * 
	 * the median value for the bin is xvalue(i)+deltax
	 * 
	 * @return xvalues
	 */
	public RealArray getMedianXValues() {
		getLowXRangeAndDeltaX();
		binArray = new RealArray(binCount, xMin + deltaX / 2.0, deltaX);
		return binArray;
	}

	private void getLowXRangeAndDeltaX() {
		lowXRange = realArray.getRange().getRange() + Real.getEpsilon();
		deltaX = lowXRange / binCount;
		getMin();
	}

	public double getBinWidth() {
		return deltaX;
	}

	/**
	 * this sorts the bins in order of frequency.
	 * 
	 * A simple way of finding the local maxima
	 * 
	 * @return
	 */
	public RealArray getMedianBinValuesSortedByFrequency() {
		IntArray intArray = getIndexOfBinsSortedByDescendingFrequency();
		RealArray sortedBins = new RealArray(getBinCount());
		RealArray medians = getMedianXValues();
		for (int i : intArray.getArray()) {
			sortedBins
					.setElementAt(i, medians.elementAt(intArray.elementAt(i)));
		}
		return sortedBins;
	}

	/**
	 * gets pointers to bins sorted by descending frequency.
	 * 
	 * array.elementAt(0) points to largest bin
	 * 
	 * @return pointers
	 */
	public IntArray getIndexOfBinsSortedByDescendingFrequency() {
		IntArray counts = new IntArray(this.getHistogramCounts());
		IntSet indexSet = counts.indexSortDescending();
		return new IntArray(indexSet.getElements());
	}

	/**
	 * gets bin values and frequencies sorted by frequency
	 * 
	 * each Real2 is x-value, frequency
	 * 
	 * zero counts are not reported
	 * 
	 * @return
	 */
	public List<Real2> getBinsSortedByFrequency() {
		if (valueFrequencyList == null) {
			valueFrequencyList = new ArrayList<Real2>();
			for (int i = 0; i < getBinCount(); i++) {
				valueFrequencyList.add(new Real2());
			}
			IntArray indexes = getIndexOfBinsSortedByDescendingFrequency();
			RealArray medians = getMedianXValues();
			int[] counts = this.getHistogramCounts();
			int numNonZero = 0;
			for (int ii = 0; ii < getBinCount(); ii++) {
				int i = indexes.elementAt(ii);
				if (counts[i] > 0.1) {
					valueFrequencyList.set(ii, new Real2(medians.elementAt(i),
							counts[i]));
					numNonZero++;
				} else {
					valueFrequencyList.set(ii, null);
					// this was a bug?
//					numNonZero++;
				}
			}
			List<Real2> array1 = new ArrayList<Real2>();
			for (int i = 0; i < numNonZero; i++) {
				array1.add(valueFrequencyList.get(i));
			}
			valueFrequencyList = array1;
		}
		return valueFrequencyList;
	}

	public RealRange getRange() {
		getXValues();
		return new RealRange(binArray.get(0), binArray.getLast() + deltaX);
	}

	public List<Univariate> getUnivariatesForBins() {
		getBins();
		List<Univariate> univariateList = new ArrayList<Univariate>();
		for (int i = 0; i < binCount; i++) {
			RealArray array = binList.get(i).getArray();
			Univariate univariate = new Univariate(array);
			univariateList.add(univariate);
		}
		return univariateList;
	}

	/**
	 * return bins for Histogram. @ not enough points
	 * 
	 * @return the counts in each bin
	 */
	public int[] getHistogramCounts() {
		getBins();
		binCounts = new int[binCount];
		for (int i = 0; i < binCount; i++) {
			binCounts[i] = binList.get(i).getCount();
		}
		return binCounts;
	}

	private List<UnivariateBin> getBins() {
		calculateSummaryStats();

		getXValues();
		getSortedArray();
		binList = new ArrayList<UnivariateBin>();
		for (int i = 0; i < binCount; i++) {
			binList.add(new UnivariateBin());
		}
		binStart = new int[binCount];
		int lastBin = -1;
		for (int i = 0; i < count; i++) {
			double x = realArray.getArray()[i];
			double diff = x - xMin;
			int binNumber = (int) (diff / deltaX);
			if (binNumber < 0)
				binNumber = 0;
			if (binNumber >= binCount)
				binNumber = binCount - 1;
			if (binNumber > lastBin) {
				binStart[binNumber] = i;
				lastBin = binNumber;
			}
			UnivariateBin bin = binList.get(binNumber);
			bin.add(x);
		}
		return binList;
	}

	/**
	 * get normal parameters
	 * 
	 * I think... variate should be normally distributed about 1.0 with sd = 1.0
	 * 
	 * @param count
	 *            number of points
	 * @return the normal distribution
	 */
	public static Univariate getNormalParams(int count) {
		double a = (count <= 10) ? 0.375 : 0.500;
		double[] z = new double[count];
		for (int i = 0; i < count; i++) {
			z[i] = Univariate.qnorm((i + 1 - a) / (count + 1 - 2 * a));
		}
		return (new Univariate(new RealArray(z)));
	}

	/**
	 * percentage points of normal distribution. upper is true.
	 * 
	 * @param p
	 *            (0 <= p <= 1)
	 * @return the percentage point (?)
	 */
	public static double qnorm(double p) {
		return qnorm(p, true);
	}

	/**
	 * percentage points of normal distribution. J. D. Beasley and S. G.
	 * Springer Algorithm AS 111:
	 * "The Percentage Points of the Normal Distribution" Applied Statistics
	 * 
	 * @param p
	 *            (0 <= p <= 1)
	 * @param upper
	 *            if true use upper half (??)
	 * @return the percentage point (?)
	 */
	public static double qnorm(double p, boolean upper) {
		if (p < 0 || p > 1) {
			throw new IllegalArgumentException("Illegal argument " + p
					+ " for qnorm(p).");
		}
		double split = 0.42, a0 = 2.50662823884, a1 = -18.61500062529, a2 = 41.39119773534, a3 = -25.44106049637, b1 = -8.47351093090, b2 = 23.08336743743, b3 = -21.06224101826, b4 = 3.13082909833, c0 = -2.78718931138, c1 = -2.29796479134, c2 = 4.85014127135, c3 = 2.32121276858, d1 = 3.54388924762, d2 = 1.63706781897, q = p - 0.5;
		double r, ppnd;
		if (Math.abs(q) <= split) {
			r = q * q;
			ppnd = q * (((a3 * r + a2) * r + a1) * r + a0)
					/ ((((b4 * r + b3) * r + b2) * r + b1) * r + 1);
		} else {
			r = p;
			if (q > 0)
				r = 1 - p;
			if (r > 0) {
				r = Math.sqrt(-Math.log(r));
				ppnd = (((c3 * r + c2) * r + c1) * r + c0)
						/ ((d2 * r + d1) * r + 1);
				if (q < 0)
					ppnd = -ppnd;
			} else {
				ppnd = 0;
			}
		}
		if (upper)
			ppnd = 1 - ppnd;
		return (ppnd);
	}

	/**
	 * percentage points of normal distribution. J. D. Beasley and S. G.
	 * Springer Algorithm AS 111:
	 * "The Percentage Points of the Normal Distribution" Applied Statistics
	 * 
	 * @param p
	 *            (0 <= p <= 1)
	 * @param upper
	 *            if true use upper half (??)
	 * @param mu
	 *            mean
	 * @param sigma2
	 *            the variance(?)
	 * @return the percentage point (?)
	 */
	public static double qnorm(double p, boolean upper, double mu, double sigma2) {
		return (qnorm(p, upper) * Math.sqrt(sigma2) + mu);
	}

	/**
	 * normal integral. I. D. Hill Algorithm AS 66: "The Normal Integral"
	 * Applied Statistics
	 * 
	 * @param z
	 * @param upper
	 *            if true use upper half (??)
	 * @return the integral (?)
	 */
	public static double pnorm(double z, boolean upper) {
		/*
		 * Reference:
		 */
		double ltone = 7.0, utzero = 18.66, con = 1.28, a1 = 0.398942280444, a2 = 0.399903438504, a3 = 5.75885480458, a4 = 29.8213557808, a5 = 2.62433121679, a6 = 48.6959930692, a7 = 5.92885724438, b1 = 0.398942280385, b2 = 3.8052e-8, b3 = 1.00000615302, b4 = 3.98064794e-4, b5 = 1.986153813664, b6 = 0.151679116635, b7 = 5.29330324926, b8 = 4.8385912808, b9 = 15.1508972451, b10 = 0.742380924027, b11 = 30.789933034, b12 = 3.99019417011;
		double y, alnorm;

		if (z < 0) {
			upper = !upper;
			z = -z;
		}
		if (z <= ltone || upper && z <= utzero) {
			y = 0.5 * z * z;
			if (z > con) {
				alnorm = b1
						* Math.exp(-y)
						/ (z - b2 + b3
								/ (z + b4 + b5
										/ (z - b6 + b7
												/ (z + b8 - b9
														/ (z + b10 + b11
																/ (z + b12))))));
			} else {
				alnorm = 0.5
						- z
						* (a1 - a2 * y
								/ (y + a3 - a4 / (y + a5 + a6 / (y + a7))));
			}
		} else {
			alnorm = 0;
		}
		if (!upper) {
			alnorm = 1 - alnorm;
		}
		return (alnorm);
	}

	/**
	 * normal integral. I. D. Hill Algorithm AS 66: "The Normal Integral"
	 * Applied Statistics
	 * 
	 * @param x
	 * @param upper
	 *            if true use upper half (??)
	 * @param mu
	 *            mean
	 * @param sigma2
	 *            the variance(?)
	 * @return the integral (?)
	 */
	public static double pnorm(double x, boolean upper, double mu, double sigma2) {
		return (pnorm((x - mu) / Math.sqrt(sigma2), upper));
	}

	/**
	 * Student's t-quantiles. Algorithm 396: Student's t-quantiles by G.W. Hill
	 * CACM 13(10), 619-620, October 1970
	 * 
	 * @param p
	 *            (0 <= p <= 1)
	 * @param ndf
	 *            degrees of freedom >= 1
	 * @param lower_tail
	 * @return the integral (?)
	 */
	public static double qt(double p, double ndf, boolean lower_tail) {
		if (p <= 0 || p >= 1 || ndf < 1) {
			throw new IllegalArgumentException(
					"Invalid p or df in call to qt(double,double,boolean).");
		}
		double eps = 1e-12;
		double M_PI_2 = 1.570796326794896619231321691640; // pi/2
		boolean neg;
		double P, q, prob, a, b, c, d, y, x;
		if ((lower_tail && p > 0.5) || (!lower_tail && p < 0.5)) {
			neg = false;
			P = 2 * (lower_tail ? (1 - p) : p);
		} else {
			neg = true;
			P = 2 * (lower_tail ? p : (1 - p));
		}

		if (Math.abs(ndf - 2) < eps) { /* df ~= 2 */
			q = Math.sqrt(2 / (P * (2 - P)) - 2);
		} else if (ndf < 1 + eps) { /* df ~= 1 */
			prob = P * M_PI_2;
			q = Math.cos(prob) / Math.sin(prob);
		} else { /*-- usual case;    including, e.g.,    df = 1.1 */
			a = 1 / (ndf - 0.5);
			b = 48 / (a * a);
			c = ((20700 * a / b - 98) * a - 16) * a + 96.36;
			d = ((94.5 / (b + c) - 3) / b + 1) * Math.sqrt(a * M_PI_2) * ndf;
			y = Math.pow(d * P, 2 / ndf);
			if (y > 0.05 + a) {
				/* Asymptotic inverse expansion about normal */
				x = qnorm(0.5 * P, false);
				y = x * x;
				if (ndf < 5) {
					c += 0.3 * (ndf - 4.5) * (x + 0.6);
				}
				c = (((0.05 * d * x - 5) * x - 7) * x - 2) * x + b + c;
				y = (((((0.4 * y + 6.3) * y + 36) * y + 94.5) / c - y - 3) / b + 1)
						* x;
				y = a * y * y;
				if (y > 0.002) {/*
								 * FIXME: This cutoff is machine-precision
								 * dependent
								 */
					y = Math.exp(y) - 1;
				} else { /* Taylor of e^y -1 : */
					y = (0.5 * y + 1) * y;
				}
			} else {
				y = ((1 / (((ndf + 6) / (ndf * y) - 0.089 * d - 0.822)
						* (ndf + 2) * 3) + 0.5 / (ndf + 4))
						* y - 1)
						* (ndf + 1) / (ndf + 2) + 1 / y;
			}
			q = Math.sqrt(ndf * y);
		}
		if (neg) {
			q = -q;
		}
		return q;
	}

	/**
	 * T-test. ALGORITHM AS 3 APPL. STATIST. (1968) VOL.17, P.189 Computes P(T <
	 * t)
	 * 
	 * @param t
	 * @param df
	 *            degrees of freedom
	 * @return probability (?)
	 */
	public static double pt(double t, double df) {
		double a, b, idf, im2, ioe, s, c, ks, fk, k;
		double g1 = 0.3183098862;// =1/pi;
		if (df < 1) {
			throw new IllegalArgumentException(
					"Illegal argument df for pt(t,df).");
		}
		idf = df;
		a = t / Math.sqrt(idf);
		b = idf / (idf + t * t);
		im2 = df - 2;
		ioe = idf % 2;
		s = 1;
		c = 1;
		idf = 1;
		ks = 2 + ioe;
		fk = ks;
		if (im2 >= 2) {
			for (k = ks; k <= im2; k += 2) {
				c = c * b * (fk - 1) / fk;
				s += c;
				if (s != idf) {
					idf = s;
					fk += 2;
				}
			}
		}
		if (ioe != 1) {
			return 0.5 + 0.5 * a * Math.sqrt(b) * s;
		}
		if (df == 1) {
			s = 0;
		}
		return 0.5 + (a * b * s + Math.atan(a)) * g1;
	}

	/**
	 * chiSquared. Posten, H. (1989) American Statistician 43 p. 261-265
	 * 
	 * @param q
	 * @param df
	 *            degrees of freedom
	 * @return chisq (?)
	 */
	public double pchisq(double q, double df) {
		double df2 = df * .5;
		double q2 = q * .5;
		int n = 5, k;
		double tk, CFL, CFU, prob;
		if (q <= 0 || df <= 0) {
			throw new IllegalArgumentException("Illegal argument " + q + " or "
					+ df + " for qnorm(p).");
		}
		if (q < df) {
			tk = q2 * (1 - n - df2)
					/ (df2 + 2 * n - 1 + n * q2 / (df2 + 2 * n));
			for (k = n - 1; k > 1; k--) {
				tk = q2 * (1 - k - df2)
						/ (df2 + 2 * k - 1 + k * q2 / (df2 + 2 * k + tk));
			}
			CFL = 1 - q2 / (df2 + 1 + q2 / (df2 + 2 + tk));
			prob = Math.exp(df2 * Math.log(q2) - q2 - lnfgamma(df2 + 1)
					- Math.log(CFL));
		} else {
			tk = (n - df2) / (q2 + n);
			for (k = n - 1; k > 1; k--) {
				tk = (k - df2) / (q2 + k / (1 + tk));
			}
			CFU = 1 + (1 - df2) / (q2 + 1 / (1 + tk));
			prob = 1 - Math.exp((df2 - 1) * Math.log(q2) - q2 - lnfgamma(df2)
					- Math.log(CFU));
		}
		return prob;
	}

	/**
	 * betainv ALGORITHM AS 63 APPL. STATIST. VOL.32, NO.1 Computes P(Beta>x)
	 * 
	 * @param x
	 * @param p
	 * @param q
	 * @return betainv (?)
	 */
	public static double betainv(double x, double p, double q) {
		double beta = lnfbeta(p, q), acu = 1E-14;
		double cx, psq, pp, qq, x2, term, ai, betain, ns, rx, temp;
		boolean indx;
		if (p <= 0 || q <= 0) {
			return (-1.0);
		}
		if (x <= 0 || x >= 1) {
			return (-1.0);
		}
		psq = p + q;
		cx = 1 - x;
		if (p < psq * x) {
			x2 = cx;
			cx = x;
			pp = q;
			qq = p;
			indx = true;
		} else {
			x2 = x;
			pp = p;
			qq = q;
			indx = false;
		}
		term = 1;
		ai = 1;
		betain = 1;
		ns = qq + cx * psq;
		rx = x2 / cx;
		temp = qq - ai;
		if (ns == 0) {
			rx = x2;
		}
		while (temp > acu && temp > acu * betain) {
			term = term * temp * rx / (pp + ai);
			betain = betain + term;
			temp = Math.abs(term);
			if (temp > acu && temp > acu * betain) {
				ai++;
				ns--;
				if (ns >= 0) {
					temp = qq - ai;
					if (ns == 0)
						rx = x2;
				} else {
					temp = psq;
					psq += 1;
				}
			}
		}
		betain *= Math.exp(pp * Math.log(x2) + (qq - 1) * Math.log(cx) - beta)
				/ pp;
		if (indx)
			betain = 1 - betain;
		return (betain);
	}

	/**
	 * betainv ALGORITHM AS 63 APPL. STATIST. VOL.32, NO.1 Computes P(F>x)
	 * 
	 * @param x
	 * @param df1
	 * @param df2
	 * @return (?)
	 */
	public static double pf(double x, double df1, double df2) {
		return (betainv(df1 * x / (df1 * x + df2), 0.5 * df1, 0.5 * df2));
	}

	public static void test() {
		Util.println("--------------Testing Univariate--------------\n");
		RandomNumberGenerator rng = new RandomNumberGenerator();
		int npoints1 = 1000;
		double[] data1 = new double[npoints1];
		for (int i = 0; i < npoints1; i++) {
			data1[i] = rng.nextGaussian();
		}
		RealArray dataArray1 = new RealArray(data1);
		Univariate univ1 = new Univariate(dataArray1);
		Util.println("mean: " + univ1.getMean());
		Util.println("sdev: " + univ1.getStandardDeviation());

		int npoints2 = 300;
		double[] data2 = new double[npoints2];
		for (int i = 0; i < npoints2; i++) {
			data2[i] = rng.nextGaussian() * 0.5 + 5.0;
		}
		RealArray dataArray2 = new RealArray(data2);
		Univariate univ2 = new Univariate(dataArray2);
		Util.println("mean: " + univ2.getMean());
		Util.println("sdev: " + univ2.getStandardDeviation());

		dataArray1.addArray(dataArray2);
		Univariate univ3 = new Univariate(dataArray1);
		Util.println("mean: " + univ3.getMean());
		Util.println("sdev: " + univ3.getStandardDeviation());
		Util.println("min: " + univ3.getMin());
		Util.println("q1: " + univ3.getQuantile(0.25));
		Util.println("median: " + univ3.getMedian());
		Util.println("q3: " + univ3.getQuantile(0.75));
		Util.println("max: " + univ3.getMax());

		univ3.setBinCount(30);
		int[] bins = univ3.getHistogramCounts();
		for (int i = 0; i < bins.length; i++) {
			Util.println("bin: " + bins[i]);
		}

		Univariate norm = Univariate.getNormalParams(1000);
		Util.println("mean: " + norm.getMean());
		Util.println("sdev: " + norm.getStandardDeviation());
		Util.println("min: " + norm.getMin());
		Util.println("q1: " + norm.getQuantile(0.25));
		Util.println("median: " + norm.getMedian());
		Util.println("q3: " + norm.getQuantile(0.75));
		Util.println("max: " + norm.getMax());

	}

	public static double lnfgamma(double c) {
		int j;
		double x, y, tmp, ser;
		double[] cof = { 76.18009172947146, -86.50532032941677,
				24.01409824083091, -1.231739572450155, 0.1208650973866179e-2,
				-0.5395239384953e-5 };
		y = x = c;
		tmp = x + 5.5 - (x + 0.5) * Math.log(x + 5.5);
		ser = 1.000000000190015;
		for (j = 0; j <= 5; j++) {
			ser += (cof[j] / ++y);
		}
		return (Math.log(2.5066282746310005 * ser / x) - tmp);
	}

	public static double lnfbeta(double a, double b) {
		return (lnfgamma(a) + lnfgamma(b) - lnfgamma(a + b));
	}

	public static double fbeta(double a, double b) {
		return Math.exp(lnfbeta(a, b));
	}

	public static double fgamma(double c) {
		return Math.exp(lnfgamma(c));
	}

	public static double fact(int n) {
		return Math.exp(lnfgamma(n + 1));
	}

	public static double lnfact(int n) {
		return lnfgamma(n + 1);
	}

	public static double nCr(int n, int r) {
		return Math.exp(lnfact(n) - lnfact(r) - lnfact(n - r));
	}

	public static double nPr(int n, int r) {
		return Math.exp(lnfact(n) - lnfact(r));
	}

	public static void main(String[] args) {
		test();
	}
}
