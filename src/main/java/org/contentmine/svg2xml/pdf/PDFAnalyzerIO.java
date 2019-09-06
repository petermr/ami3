package org.contentmine.svg2xml.pdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.html.HtmlMenuSystem;
import org.contentmine.svg2xml.util.NameComparator;
import org.contentmine.svg2xml.util.SVG2XMLConstantsX;

/** 
 * Class to deal with IO from PDFAnalyzer
 * 
 * @author pm286
 */
public class PDFAnalyzerIO {
	
	private static final Logger LOG = Logger.getLogger(PDFAnalyzerIO.class);
	static {LOG.setLevel(Level.DEBUG);}

	public static final File TARGET_DIR = new File("target");
	public static final File TARGET_OUTPUT_DIR = new File(TARGET_DIR, "output");
	public static final File TARGET_SVG_DIR = new File(TARGET_DIR, "svg");
	public static final String HTTP = "http";
	public static final String DOT_PDF = ".pdf";
	final static PrintStream SYSOUT = System.out;

	private File inFile;
	private String inputName;
	String fileRoot;
	private File svgDir = TARGET_SVG_DIR;
	private File rawSvgDirectory;
	private File outputDirectory = TARGET_OUTPUT_DIR;
	File outputDocumentDir;
	private File htmlDir;
	private File imageDirectory;
	private PDFAnalyzer pdfAnalyzer;
	private boolean skipOutput;

	public PDFAnalyzerIO(PDFAnalyzer pdfAnalyzer) {
		this.pdfAnalyzer = pdfAnalyzer;
	}
	
	public void setSvgDir(File svgDir) {
		this.svgDir = svgDir;
	}
	
	public void setOutputDirectory(File outDir) {
		this.outputDirectory = outDir;
	}
	
	public File getOutputTopDir() {
		return outputDirectory;
	}
	
	public void setFileRoot(String fileRoot) {
		this.fileRoot = fileRoot;
	}
	
	void setInputName(String name) {
		this.inputName = name;
	}
	
	public File getInFile() {
		return inFile;
	}
	
	public File getRawSVGDirectory() {
		return rawSvgDirectory;
	}
	
	public void setRawSVGDirectory(File svgDir) {
		rawSvgDirectory = svgDir;
	}
	
//	public void setFinalSVGDirectory(File svgDir) {
//		finalSvgDirectory = svgDir;
//	}
//	
//	public File getFinalSVGDirectory() {
//		return finalSvgDirectory;
//	}
	
	public String getInputName() {
		return inputName;
	}

	public void setPDFURL(String name) {
		setInputName(name);
		setFileRoot(name.substring(0, name.length() - SVG2XMLConstantsX.DOT_PDF.length()));
		if (fileRoot.startsWith(HTTP)) {
			fileRoot = fileRoot.substring(fileRoot.indexOf("//")+2);
			fileRoot = fileRoot.substring(fileRoot.indexOf("/")+1);
		}
		rawSvgDirectory = svgDir;
		outputDocumentDir = new File(outputDirectory, fileRoot);
		outputDocumentDir.mkdirs();
		fileRoot = "";
	}
	
	void setUpPDF(File inFile) {
		this.inFile = inFile;
		inputName = inFile.getName();
		fileRoot = inputName.substring(0, inputName.length() - SVG2XMLConstantsX.DOT_PDF.length());
		rawSvgDirectory = svgDir;
		outputDocumentDir = new File(outputDirectory, fileRoot);
//		imageDirectory = new File(outputDirectory, "images");
	}

	public String createHttpInputName(String inputName) {
		String inputName1 = inputName.substring(inputName.lastIndexOf("/") + 1);
		if (inputName1.toLowerCase().endsWith(DOT_PDF)) {
			inputName = inputName1.substring(0, inputName1.length()-DOT_PDF.length());
		}
		return inputName;
	}
	
	void outputFiles() {
		File htmlDir = (new File(outputDirectory, fileRoot));
		copyOriginalPDF(inFile, htmlDir);
		createHtmlMenuSystem(htmlDir);
	}
	

	void copyOriginalPDF(File inFile, File htmlDir) {
		try {
			htmlDir.mkdirs();
			IOUtils.copy(new FileInputStream(inFile), new FileOutputStream(new File(htmlDir, "00_"+inputName)));
		} catch (Exception e1) {
			throw new RuntimeException(e1);
		}
	}

	 void createHtmlMenuSystem(File dir) {
		HtmlMenuSystem menuSystem = new HtmlMenuSystem();
		menuSystem.setOutdir(dir.toString());
		File[] filesh = dir.listFiles();
		Arrays.sort(filesh, new NameComparator());
		for (File filex : filesh) {
			menuSystem.addHRef(filex.toString());
		}
		try {
			menuSystem.outputMenuAndBottomAndIndexFrame();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	 
	public void createHTMLDir() {
		 htmlDir = new File(outputDirectory, fileRoot);
	}
	
	public File getExistingOutputDocumentDir() {
		outputDocumentDir.mkdirs();
		return outputDocumentDir;
	}

	List<File> collectRawSVGFiles() {
		File[] rawSvgPageFiles = rawSvgDirectory.listFiles();
		List<File> files = new ArrayList<File>();
		LOG.trace("analyzing Files in: "+rawSvgDirectory);
		if (rawSvgPageFiles == null) {
			throw new RuntimeException("No files in "+rawSvgDirectory);
		} else {
			// sort by integer page number "page12.svg"
			for (int i = 0; i < rawSvgPageFiles.length; i++) {
				for (int j = 0; j < rawSvgPageFiles.length; j++) {
					File filej = rawSvgPageFiles[j];
					if (filej.getName().contains("page"+(i + 1)+".svg")) {
						files.add(filej);
					}
				}
			}
		}
		return files;
	}

	public File getRawSVGPageDirectory() {
		return rawSvgDirectory;
	}

	public void setRawSvgDirectory(File rawSvgDirectory) {
		this.rawSvgDirectory = rawSvgDirectory;
	}

	public void outputFiles(PDFAnalyzerOptions options) {
	}


	/** currently the options is not used.
	 * 
	 * @param options null means skip, else skip only if outputDocumentDir exists
	 * @return
	 */
	public boolean isSkipOutput() {
		return skipOutput;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("fileRoot: "+fileRoot+"\n");
		sb.append("inFile: "+inFile+"\n");
		sb.append("inputName: "+inputName+"\n");
		sb.append("outputDocumentDir: "+outputDocumentDir+"\n");
		sb.append("outputTopDir: "+outputDirectory+"\n");
		sb.append("rawSvgDirectory: "+rawSvgDirectory+"\n");
		sb.append("svgTopDir: "+svgDir+"\n");
		sb.append("htmlDir: "+htmlDir+"\n");
		sb.append("imageDirectory: "+imageDirectory+"\n");
		return sb.toString();
	}

	public void setSkipOutput(boolean b) {
		skipOutput = b;
	}

	public void setImageDirectory(File imagesDir) {
		this.imageDirectory = imagesDir;
		
	}
}
