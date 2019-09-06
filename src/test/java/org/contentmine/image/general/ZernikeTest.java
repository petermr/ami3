package org.contentmine.image.general;

import org.apache.log4j.Logger;
import org.contentmine.image.ImageUtil;

public class ZernikeTest {

	private final static Logger LOG = Logger.getLogger(ZernikeTest.class);
	
//	@Test
//	public void testZernikeOrig1() throws Exception {
//		int order = 1;
//		File file = new File(Fixtures.REFFONT_DIR, "65.png");
//		ZernikeMomentsOrig.Complex[] complexArray = calculateComplex(order, file);
//		for (ZernikeMomentsOrig.Complex complex : complexArray) {
//			LOG.trace(complex);
//		}
//		complexArray = calculateComplex(2, file);
//		for (ZernikeMomentsOrig.Complex complex : complexArray) {
//			LOG.trace(complex);
//		}
//		for (int i = 65; i <= 126; i++) {
//			file = new File(Fixtures.REFFONT_DIR, i+".png");
//			debugMoments(file, 2, ""+(char)i);
//		}
//		debugMoments(new File(Fixtures.REFFONT_DIR, "18.png"), 2, "serifB");
//	}
//
//	private void debugMoments(File file, int order, String title) throws IOException {
//		ZernikeMomentsOrig.Complex[] complexArray;
//		if (file.exists()) {
//			complexArray = calculateComplex(order, file);
//			for (ZernikeMomentsOrig.Complex complex : complexArray) {
//				LOG.trace(title+": "+complex);
//			}
////			System.out.println();
//		}
//	}
//	
//	private ZernikeMomentsOrig.Complex[] calculateComplex(int order, File imageFile) throws IOException {
//		BufferedImage image = ImageUtil.readImage(imageFile);
//		PixelProcessor pixelProcessor = new PixelProcessor(image);
//		PixelIslandList pixelIslandList = pixelProcessor.getOrCreatePixelIslandList();
//		PixelList pixelList = pixelIslandList.getPixelList();
//		Real2Array real2Array = Pixel.createReal2Array(pixelList);
//		double[] xarray = real2Array.getXArray().getArray();		
//		double[] yarray = real2Array.getYArray().getArray();
//		ZernikeMomentsOrig.Complex[] complexArray = ZernikeMomentsOrig.zer_mmts(order, xarray, yarray, xarray.length);
//		return complexArray;
//	}
}
