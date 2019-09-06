package org.contentmine.image.moments;

import java.awt.image.BufferedImage;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2Array;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.image.pixel.MainPixelProcessor;
import org.contentmine.image.pixel.Pixel;
import org.contentmine.image.pixel.PixelIslandList;
import org.contentmine.image.pixel.PixelList;

/**
 * see http://en.wikipedia.org/wiki/Image_moment
 * 
 * Central moments are defined as

 \mu_{pq} = \int\limits_{-\infty}^{\infty} \int\limits_{-\infty}^{\infty} (x - \bar{x})^p(y - \bar{y})^q f(x,y) \, dx \, dy 
where \bar{x}=\frac{M_{10}}{M_{00}} and \bar{y}=\frac{M_{01}}{M_{00}} are the components of the centroid.

If ƒ(x, y) is a digital image, then the previous equation becomes

\mu_{pq} = \sum_{x} \sum_{y} (x - \bar{x})^p(y - \bar{y})^q f(x,y)
The central moments of order up to 3 are:
 * 
 * 
\mu_{00} = M_{00},\,\!
\mu_{01} = 0,\,\!
\mu_{10} = 0,\,\!
\mu_{11} = M_{11} - \bar{x} M_{01} = M_{11} - \bar{y} M_{10},
\mu_{20} = M_{20} - \bar{x} M_{10}, 
\mu_{02} = M_{02} - \bar{y} M_{01}, 
\mu_{21} = M_{21} - 2 \bar{x} M_{11} - \bar{y} M_{20} + 2 \bar{x}^2 M_{01}, 
\mu_{12} = M_{12} - 2 \bar{y} M_{11} - \bar{x} M_{02} + 2 \bar{y}^2 M_{10}, 
\mu_{30} = M_{30} - 3 \bar{x} M_{20} + 2 \bar{x}^2 M_{10}, 
\mu_{03} = M_{03} - 3 \bar{y} M_{02} + 2 \bar{y}^2 M_{01}. 
 * @author pm286
 *
 */
public class ImageMomentGenerator {

	private final static Logger LOG = Logger.getLogger(ImageMomentGenerator.class);

	private static final int PMAX = 2;
	private static final int QMAX = 2;
	private Real2Array pixelCoords;
	private BufferedImage image;
	private PixelIslandList islandList;
	private PixelList pixelList;
	private Double sumX;
	private Double sumY;
	private Double meanX;
	private Double meanY;
	private RealArray xCoords;
	private RealArray yCoords;
	private double moment[][] = new double[PMAX+1][QMAX+1];  // 
	private double mu[][] = new double[PMAX+1][QMAX+1];  // 
	
	public ImageMomentGenerator() {
		
	}

	/** read binarized image
	 * convenience method
	 * assume it can be transformed int PixelIslandList
	 * 
	 * @param image
	 */
	public void readImage(BufferedImage image) {
		if (image == null) {
			throw new RuntimeException("null image");
		}
		this.image = image;
		MainPixelProcessor pixelProcessor = new MainPixelProcessor(image);
		islandList = pixelProcessor.getOrCreatePixelIslandList();
		readPixelList(islandList);
	}
	
	public void readPixelList(PixelIslandList islandList) {
		pixelList = islandList.getPixelList();
		readPixels(pixelList);
	}
	
	private void readPixels(PixelList pixelList) {
		pixelCoords = Pixel.createReal2Array(pixelList);
		LOG.trace("pixels: "+pixelCoords.size());
		calculateMoments();
		// disable printing
//		printMoments(moment, "moment");
//		printMoments(mu, "mu");
	}

	/** at present calculate up to 2.
	 * 
	 */
	public void calculateMoments() {
		calculateSumAndMeans();
		for (int i = 0; i <= PMAX; i++) {
			for (int j = 0; j <= QMAX; j++) {
				calculateMoment(i, j);
				calculateMu(i, j);
			}
		}
	}

	/** at present calculate up to 2.
	 * 
	 */
	public void printMoments(double[][] moment, String title) {
		System.out.println(title);
		for (int i = 0; i <= PMAX; i++) {
			System.out.print(i+": ");
			for (int j = 0; j <= QMAX; j++) {
				System.out.print((long)moment[i][j]+ " ");
			}
			System.out.println();
		}
	}

	/** calculate mu(p,q)
	 * 
	 * \mu_{pq} = \sum_{x} \sum_{y} (x - \bar{x})^p(y - \bar{y})^q f(x,y)
	 * 
	 * @param p
	 * @param q
	 * @return
	 */
	public double calculateMu(int p, int q) {
		
		LOG.trace("p: "+p+", q: "+q);
		double sum = 0.0;
		for (int i = 0; i < xCoords.size(); i++) {
			double x = xCoords.get(i);
			double dxp = Math.pow((x), p);
			for (int j = 0; j < yCoords.size(); j++) {
				double fxy = 1.0; // in case we ever get non-binary
				double y = yCoords.get(j);
				double dyq = Math.pow((y), q);
				sum += dxp * dyq * fxy;
			}
		}
		moment[p][q] = sum;
		return moment[p][q];
	}


	/** calculate mu(p,q)
	 * 
	 * \mu_{pq} = \sum_{x} \sum_{y} (x - \bar{x})^p(y - \bar{y})^q f(x,y)
	 * 
	 * @param p
	 * @param q
	 * @return
	 */
	public double calculateMoment(int p, int q) {
		
		double sum = 0.0;
		for (int i = 0; i < xCoords.size(); i++) {
			double x = xCoords.get(i);
			double dxp = Math.pow((x - meanX), p);
			for (int j = 0; j < yCoords.size(); j++) {
				double fxy = 1.0; // in case we ever get non-binary
				double y = yCoords.get(j);
				double dyq = Math.pow((y - meanY), q);
				sum += dxp * dyq * fxy;
			}
		}
		mu[p][q] = sum;
		return mu[p][q];
	}

	private void calculateSumAndMeans() {
		xCoords = pixelCoords.getXArray();
		this.sumX = xCoords.sumAllElements();
		this.meanX = xCoords.getMean();
		yCoords = pixelCoords.getYArray();
		this.sumY = yCoords.sumAllElements();
		this.meanY = yCoords.getMean();
		LOG.trace("> "+sumX+" "+meanX+" "+sumY+" "+meanY);
	}

}
