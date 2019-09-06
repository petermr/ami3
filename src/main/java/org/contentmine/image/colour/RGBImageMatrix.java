package org.contentmine.image.colour;

import java.awt.image.BufferedImage;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.ArrayBase.Trim;
import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.eucl.euclid.IntMatrix;
import org.contentmine.eucl.euclid.RealMatrix;
import org.contentmine.image.ImageUtil;

/** holds the RGB channels for a buffered image as matrices
 * 
 * @author pm286
 *
 */
public class RGBImageMatrix {
	private static final Logger LOG = Logger.getLogger(RGBImageMatrix.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public static int TYPE13 = 13;  // no idea what is does but it works
	
	/** matrices are m[x][y] , i.e. m[cols][rows]
	 * 
	 * 
	 */
	/** 3 matrices , R, G, B channels 
	 * 
	 */
	private IntMatrix[] mrgb;

/** create matrix with references.
	 * requires at least one row and column
	 * 
	 * @param mr
	 * @param mg
	 * @param mb
	 * @return null if mr, mg null or incompatible
	 */
	public static RGBImageMatrix createMatrix(int[][] mr, int[][] mg, int[][] mb) {
		RGBImageMatrix rgbMatrix = null;
		if (mr != null && mg != null && mb != null) {
			int cols = mr.length;
			if (cols > 0 && mg.length == cols && mb.length == cols) {
				rgbMatrix = new RGBImageMatrix();
				rgbMatrix.createMatrixArray(mr, mg, mb);
			}
		}
		return rgbMatrix;
	}

	private void createMatrixArray(int[][] mr, int[][] mg, int[][] mb) {
		mrgb = new IntMatrix[3];
		mrgb[ImageUtil.RED_INDEX] = createIntMatrixFromArray(mr);
		mrgb[ImageUtil.GREEN_INDEX] = createIntMatrixFromArray(mg);
		mrgb[ImageUtil.BLUE_INDEX] = createIntMatrixFromArray(mb);
	}

	private IntMatrix createIntMatrixFromArray(int[][] mr) {
		int cols = mr.length;
		int rows = mr[0].length;
		IntMatrix matrix = new IntMatrix(rows, cols);
		for (int iRow = 0; iRow < rows; iRow++) {
			for (int jCol = 0; jCol < cols; jCol++) {
				matrix.setElementAt(iRow, jCol, mr[jCol][iRow]);
			}
		}
		return matrix;
	}

	private RGBImageMatrix() {
	}

	public int getRows() {
		return (mrgb == null || mrgb.length != 3) ? 0 : mrgb[0].getRows();
	}

	public int getCols() {
		return (mrgb == null || mrgb.length != 3) ? 0 : mrgb[0].getCols();
	}


	/** extract 3 real Matrixes carrying the RGB channels as doubles.
	 * for transformation and filtering
	 * 
	 * @param image
	 * @return
	 */
	public static RGBImageMatrix extractMatrix(BufferedImage image) {
		int ymax = image.getHeight();
		int xmax = image.getWidth();
		int[][] mr = new int[xmax][ymax];
		int[][] mg = new int[xmax][ymax];
		int[][] mb = new int[xmax][ymax];
		for (int x = 0; x < xmax; x++) {
			mr[x] = new int[ymax];
			mg[x] = new int[ymax];
			mb[x] = new int[ymax];
			for (int y = 0; y < ymax; y++) {
				int rgb = image.getRGB(x, y);
				mr[x][y] = ImageUtil.getRed(rgb);
				mg[x][y] = ImageUtil.getGreen(rgb);
				mb[x][y] = ImageUtil.getBlue(rgb);
			}
		}
		RGBImageMatrix rgbMatrix = RGBImageMatrix.createMatrix(mr, mg, mb);
		return rgbMatrix;
	}

	public BufferedImage createImage(int imageType) {
		
		BufferedImage image = null;
		int xmax = this.getCols();
		int ymax = this.getRows();
		IntMatrix rm = mrgb[ImageUtil.RED_INDEX];
		IntMatrix gm = mrgb[ImageUtil.GREEN_INDEX];
		IntMatrix bm = mrgb[ImageUtil.BLUE_INDEX];
		image = ImageUtil.createARGBBufferedImage(xmax, ymax);
		for (int y = 0; y < ymax; y++) {
			for (int x = 0; x < xmax; x++) {
				// x and y run differently in image ?? do they??
				// old failing code
				int red = rm.elementAt(y, x);
				int green = gm.elementAt(y, x);
				int blue = bm.elementAt(y, x);
//				// new to re-test
//				int newred = rm.elementAt(x, y);
//				int newgreen = gm.elementAt(x, y);
//				int newblue = bm.elementAt(x, y);
				int rgb = ImageUtil.setRgb(red, green, blue);
				image.setRGB(x, y, rgb);
			}
		}
		return image;
	}

	public IntMatrix getMatrix(int channel) {
		if (channel >= 0 && channel < ImageUtil.RGB.length) {
			return mrgb[channel];
		} 
		return null;
	}

	public void invertRgb(int nchannel) {
		IntMatrix channel = mrgb[nchannel];
		for (int x = 0; x < channel.getCols(); x++) {
			for (int y = 0; y < channel.getRows(); y++) {
				// x and y interchanged because image runs differently
				int r = channel.elementAt(y, x);
				channel.setElementAt(y,  x, ImageUtil.invertRgb(r));
			}
		}
	}

	public void invertRgb() {
		for (int channel = 0; channel < ImageUtil.RGB.length; channel++) {
			invertRgb(channel);
		}
	}

	public static void debug(int[][] channel) {
		LOG.debug("width/cols: "+channel.length+", height/rows: "+channel[0].length);
		for (int x = 0; x < channel.length; x++) {
			for (int y = 0; y < channel[0].length; y++) {
				int r = channel[x][y];
				System.err.print(">rgb>"+((r==255) ? "." : "*"));
			}
			System.err.println();
		}
		LOG.debug("width/cols: "+channel.length+", height/rows: "+channel[0].length);
	}
	
	/** apply simple sharpening function to image.
	 * 
	 * PROBABLY WRONG
	 */
	@Deprecated //"probably wrong"
	public RGBImageMatrix applyFilter(IntArray function) {
		RGBImageMatrix newMatrix = this.copy();
		for (int channel = 0; channel < ImageUtil.RGB.length; channel++) {
			IntMatrix matrix = newMatrix.getMatrix(channel);
			// extract row, filter and replace
			for (int iRow = 0; iRow < matrix.getRows(); iRow++) {
				IntArray row = matrix.extractRowData(iRow);
				IntArray row1 = row.applyFilter(function);
//				row1 = row; // only for debug
				// try to get rid of negative values
//				row1 = row1.trim(Trim.BELOW, 0).trim(Trim.ABOVE, 0xffff);
				matrix.replaceRowData(iRow, row1);
			}
			boolean debug = true;
			if (!debug) {
			// extract column, filter and replace
			for (int icol = 0; icol < matrix.getCols(); icol++) {
				IntArray col = matrix.extractColumnData(icol);
				IntArray col1 = col.applyFilter(function);
//				col1 = col; // only for debug
				// try to get rid of negative values
//				col1 = col1.trim(Trim.BELOW, 0).trim(Trim.ABOVE, 0xffff);
				matrix.replaceColumnData(icol, col1);
			}
			}
			newMatrix.mrgb[channel] = matrix;
		}
		return newMatrix;
	}

	/** apply simple sharpening function to image.
	 * 
	 */
	public RGBImageMatrix applyFilterNew(IntArray function) {
		RGBImageMatrix newMatrix = this.copy();
		for (int channel = 0; channel < ImageUtil.RGB.length; channel++) {
			IntMatrix matrix = newMatrix.getMatrix(channel);
			
			// extract row, filter and replace
			for (int iRow = 0; iRow < matrix.getRows(); iRow++) {
				IntArray row = matrix.extractRowData(iRow);
				IntArray row1 = row.applyFilterNew(function);
				row1 = row1.trim(Trim.BELOW, 0).trim(Trim.ABOVE, 255);
				matrix.replaceRowData(iRow, row1);
			}
			// extract column, filter and replace
			for (int icol = 0; icol < matrix.getCols(); icol++) {
				IntArray col = matrix.extractColumnData(icol);
				IntArray col1 = col.applyFilterNew(function);
				// try to get rid of negative values
				col1 = col1.trim(Trim.BELOW, 0).trim(Trim.ABOVE, 255);
				matrix.replaceColumnData(icol, col1);
			}
			newMatrix.mrgb[channel] = matrix;
		}
		return newMatrix;
	}

	public RGBImageMatrix copy() {
		RGBImageMatrix newMatrix = new RGBImageMatrix();
		newMatrix.mrgb = new IntMatrix[3];
		for (int i = 0; i < ImageUtil.RGB.length; i++) {
			newMatrix.mrgb[i] = new IntMatrix(mrgb[i]);
		}
		return newMatrix;
	}

	public RGBImageMatrix applyFilter(RealMatrix filter) {
		RGBImageMatrix newMatrix = this.copy();
		for (int channel = 0; channel < ImageUtil.RGB.length; channel++) {
			IntMatrix matrix = newMatrix.getMatrix(channel);
			RealMatrix realMatrix = new RealMatrix(matrix);
			RealMatrix filteredMatrix = realMatrix.applyFilter(filter);
			IntMatrix filteredIntMatrix = new IntMatrix(filteredMatrix);
//			filteredIntMatrix.trim(0, 255);
			newMatrix.mrgb[channel] = filteredIntMatrix;
		}
		return newMatrix;
	}

}
