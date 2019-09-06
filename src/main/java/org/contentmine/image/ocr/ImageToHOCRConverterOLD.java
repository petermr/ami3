package org.contentmine.image.ocr;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.args.DefaultArgProcessor;
import org.contentmine.cproject.util.CMineUtil;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealRange;

/** moved to Norma?
 * 
 * @author pm286
 *
 */
@Deprecated
public class ImageToHOCRConverterOLD {

	private static final int TESSERACT_TIMEOUT_STEP = 100;

	private static final int NTRIES = 20;

	private final static Logger LOG = Logger.getLogger(ImageToHOCRConverterOLD.class);
	static {LOG.setLevel(Level.DEBUG);}

	private static final String HOCR = "hocr";
	private static final String USR_LOCAL_BIN_TESSERACT = "/usr/local/bin/tesseract";
	private final static String TESS_CONFIG = "phylo";
//	private static final String ENCODING = "-Dfile.encoding=UTF8";
	private static final String ENCODING = "";
	
	private static final String PNG = ".png";
	public static final int DEFAULT_RETRIES_FOR_TESSERACT_EXIT = 60;
	private static final String HOCR_HTML_SUFFIX = ".pbm.png.hocr.html";
	private static final String HOCR_SUFFIX = ".pbm.png.hocr";
	private static final String HOCR_SVG_SUFFIX = ".pbm.png.hocr.svg";
	private static Real2Range DEFAULT_HOCR_WORD_JOINING_BOX = new Real2Range(new RealRange(0.0, 20.0), new RealRange(-5.0, 5.0));

	private int tryCount;
	private File outputHtmlFile;
	
	public ImageToHOCRConverterOLD() {
		setDefaults();
	}
	
    private void setDefaults() {
    	tryCount = NTRIES;
	}

	public int getTryCount() {
		return tryCount;
	}

	public void setTryCount(int tryCount) {
		this.tryCount = tryCount;
	}

	/** converts Image to HOCR.
     * relies on Tesseract.
     * 
     * Note - creates a *.hocr.html file from output root.
     * 
     * @param inputImageFile
     * @return HOCR.HTML file created (null if failed to create)
     * @throws IOException // if Tesseract not present
     * @throws InterruptedException ??
     */
    public File convertImageToHOCR(File inputImageFile, File output) throws IOException {

    	outputHtmlFile = null;
        // tesseract performs the initial Image => HOCR conversion,
    	
        output.getParentFile().mkdirs();
//        ProcessBuilder tesseractBuilder = new ProcessBuilder(
//        		USR_LOCAL_BIN_TESSERACT, inputImageFile.getAbsolutePath(), output.getAbsolutePath(), TESS_CONFIG, HOCR, ENCODING );
//        String tessConfig = TESS_CONFIG;
        String tessConfig = "";
        ProcessBuilder tesseractBuilder = new ProcessBuilder(
        		USR_LOCAL_BIN_TESSERACT, inputImageFile.getAbsolutePath(), output.getAbsolutePath(), tessConfig, HOCR, ENCODING );
//        ProcessBuilder tesseractBuilder = new ProcessBuilder(
//        		"tesseract", inputImageFile.getAbsolutePath(), output.getAbsolutePath(), TESS_CONFIG, HOCR, ENCODING );
        tesseractBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
    	Process tesseractProc = null;
        try {
        	tesseractProc = tesseractBuilder.start();
        } catch (IOException e) {
        	CMineUtil.catchUninstalledProgram(e, USR_LOCAL_BIN_TESSERACT);
        	return null;
        }
        tesseractProc.getOutputStream().close();
        int exitValue = -1;
        int itry = 0;
        for (; itry < tryCount; itry++) {
			try {
				Thread.sleep(TESSERACT_TIMEOUT_STEP);
			} catch (InterruptedException e1) {
				throw new RuntimeException("BUG: ", e1);
			}
		    try {
		    	exitValue = tesseractProc.exitValue();
		    	if (exitValue == 0) {
		    		LOG.trace("tesseract terminated OK");
		    		break;
		    	}
			} catch (IllegalThreadStateException e) {
				LOG.trace("still not terminated after: "+itry+"; keep going");
			}
		}
		LOG.trace("tries: "+itry);

		if (exitValue != 0) {
			tesseractProc.destroy();
			LOG.error("Process failed to terminate after :"+tryCount);
		}
    	outputHtmlFile = createOutputHtmlFileDescriptorForHOCR_HTML(output);
    	LOG.trace("creating output "+outputHtmlFile);
		if (!outputHtmlFile.exists()) {
			File outputHocr = createOutputHtmlFileDescriptorForHOCR_HOCR(output);
			if (!outputHocr.exists()) {	
				DefaultArgProcessor.CM_LOG.debug("failed to create: "+outputHtmlFile+" or "+outputHocr);
				outputHtmlFile = null;
			} else {
				LOG.trace("copying "+outputHocr+" to "+outputHtmlFile);
				FileUtils.copyFile(outputHocr, outputHtmlFile);
			}
		} else {
			LOG.trace("created "+outputHtmlFile.getAbsolutePath()+"; size: "+ FileUtils.sizeOf(outputHtmlFile));
		}
		return outputHtmlFile;

    }

	private File createOutputHtmlFileDescriptorForHOCR_HTML(File output) {
		String filename = output.getAbsolutePath()+".html";
		LOG.trace("creating HTML output: "+filename);
		return new File(filename);
	}

	private File createOutputHtmlFileDescriptorForHOCR_HOCR(File output) {
		String filename = output.getAbsolutePath()+".hocr";
		LOG.trace("creating hocr.hocr name: "+filename);
		return new File(filename);
	}

}
