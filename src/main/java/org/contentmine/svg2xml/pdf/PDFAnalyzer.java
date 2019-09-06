package org.contentmine.svg2xml.pdf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.svg2xml.PDF2SVGConverterWrapper;
import org.contentmine.svg2xml.page.PageAnalyzer;
import org.contentmine.svg2xml.util.SVG2XMLConstantsX;

import com.google.common.collect.Multimap;

/** 
 * Process a complete document.
 * <p>
 * May be called standalone or in an iteration from DocumentListAnalyzer.
 * <p>
 * Uses PDFAnalyzerIO as helper to manage the IO variables. Uses PDFAnalyzerOptions as helper to manage the processing options.
 * <p>
 * Creates List&lt;PageAnalyzer&gt; as a result of processing all pages.
 * <p>
 * Intermediate results may be stored in directory created for each document.
 * <p>
 * Collects conversion to HTML as runningTextElement.
 * 
 * @author pm286
 */

public class PDFAnalyzer {

	private final static Logger LOG = Logger.getLogger(PDFAnalyzer.class);
	static {LOG.setLevel(Level.DEBUG);}
	
	final static PrintStream SYSOUT = System.out;
	public static final String Z_CHUNK = "z_";
	
	private PDFAnalyzerIO pdfIo;
	// created by analyzing pages
	private List<PageAnalyzer> pageAnalyzerList;
	private PDFAnalyzerOptions pdfOptions;
	private HtmlElement runningTextElement;

	
	public PDFAnalyzer() {
		pdfIo = new PDFAnalyzerIO(this);
		setPdfOptions(new PDFAnalyzerOptions(this));
	}

	public void setSVGTopDir(File svgDir) {
		pdfIo.setSvgDir(svgDir);
	}
	
	public void setOutputTopDir(File outDir) {
		pdfIo.setOutputDirectory(outDir);
	}
	
	public File getOutputTopDir() {
		return pdfIo.getOutputTopDir();
	}
	
	public void setFileRoot(String fileRoot) {
		pdfIo.setFileRoot(fileRoot);
	}
	
	/** 
	 * A main entry routine
	 * <p>
	 * If name ends with ".pdf" then treat as single file else directory
	 * <p>
	 * If name starts with "http://" treat as URL of single PDF file
	 * <p>
	 * First creates SVGs, then analyzes them
	 * 
	 * @param name file or directory
	 */
	public void analyzePDFs(String name) {
		if (name == null) {
			throw new RuntimeException("File/s must not be null");
		} else if (name.endsWith(SVG2XMLConstantsX.DOT_PDF)) {
			if (name.startsWith(PDFAnalyzerIO.HTTP)) {
				analyzePDFURL(name);
			} else {
				analyzePDFFile(new File(name));
			}
		} else {
			File file = new File(name);
			readFilenamesAndAnalyzePDFs(file);
		}
	}

	/** 
	 * Reads filenames from file
	 * 
	 * @param file
	 */
	private void readFilenamesAndAnalyzePDFs(File file) {
		if (file.exists()) {
			if (!file.isDirectory()) {
				File parentFile = file.getParentFile();
				try {
					BufferedReader br = new BufferedReader(new FileReader(file));
					while (true) {
						String line = br.readLine();
						if (line == null) {
							break;
						}
						if (line.startsWith("#")) {
							// comment
						} else if (line.endsWith(SVG2XMLConstantsX.DOT_PDF)) {
							readAndAnalyzeFile(parentFile, line);
						}
					}
					br.close();
				} catch (Exception e) {
					throw new RuntimeException("Cannot read listing file: "+file, e);
				}
			} else {
				File[] files = file.listFiles(new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return name.endsWith(SVG2XMLConstantsX.DOT_PDF);
					}
				});
				if (files != null && files.length > 0) {
					for (File pdf : files) {
						createAnalyzerAndAnalyzePDF(pdf);
					}
				}
			}
		}
	}

	private void readAndAnalyzeFile(File parentDir, String filename) {
		File inFile = new File(parentDir, filename);
		if (!inFile.exists()) {
			LOG.error("PDF file does not exist: "+inFile);
		} else {
			createAnalyzerAndAnalyzePDF(inFile);
		}
	}

	private void createAnalyzerAndAnalyzePDF(File inFile) {
		try {
			PDFAnalyzer analyzer = new PDFAnalyzer();
			analyzer.analyzePDFFile(inFile);
		} catch (Exception e) {
			LOG.error("Cannot read file: "+inFile+" ("+e+")");
		}
	}

	private void analyzePDFURL(String name) {
		pdfIo.setPDFURL(name);
		analyzePDF();
	}

	public void analyzePDFFile(File inFile) {
		pdfIo.setUpPDF(inFile);
		analyzePDF();
	}

	private void analyzePDF() {
		ensurePDFIndex();
		createSVGFilesfromPDF();
		LOG.debug("*** created SVG");
		if (pdfIo.isSkipOutput()) {
			LOG.trace("Skipped Output: "+pdfIo.outputDocumentDir);
		} else {
			analyzeRawSVGPagesWithPageAnalyzers();
		}
	}

	public void analyzeRawSVGPagesWithPageAnalyzers() {
		// this does not output anything
		pageAnalyzerList = createAndFillPageAnalyzers();
		// this outputs files
		pdfIo.outputFiles(getPdfOptions());
		createIndexesAndRemoveDuplicates();
		try {
			FileUtils.copyDirectory(pdfIo.getRawSVGPageDirectory(), pdfIo.getExistingOutputDocumentDir(), new FileFilter() {
				public boolean accept(File pathname) {
					return ("png".equals(FilenameUtils.getExtension(pathname.getName())));
				}
			});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void createIndexesAndRemoveDuplicates() {
//		ensurePDFIndex();
//		pdfIndex.ensureElementMultimaps();
//		for (PageAnalyzer pageAnalyzer : pageAnalyzerList) {
//			pdfIndex.addToindexes(pageAnalyzer);
//		}
//		pdfIndex.analyzeContainers();
//		pdfIndex.createIndexes();
//		pdfIndex.AnalyzeDuplicates();
//		LOG.trace("IDS: "+pdfIndex.getUsedIdSet());
	}

	private List<PageAnalyzer> createAndFillPageAnalyzers() {
		File rawSVGDirectory = pdfIo.getRawSVGPageDirectory();
		List<File> rawSvgPageFiles = pdfIo.collectRawSVGFiles();
		ensurePageAnalyzerList();
		LOG.trace(rawSVGDirectory+" files: "+rawSvgPageFiles.size());
		for (int pageCounter = 0; pageCounter < rawSvgPageFiles.size(); pageCounter++) {
			SYSOUT.print(pageCounter+"~");
			PageAnalyzer pageAnalyzer = PageAnalyzer.createAndAnalyze(rawSvgPageFiles.get(pageCounter), rawSVGDirectory, pageCounter);
			pageAnalyzerList.add(pageAnalyzer);
		}
		return pageAnalyzerList;
	}
	
	public List<PageAnalyzer> createAndFillPageAnalyzers(List<SVGSVG> svgList) {
		ensurePageAnalyzerList();
		File rawSVGDirectory = this.pdfIo.getRawSVGDirectory();
		LOG.trace("raw svg "+rawSVGDirectory);
		for (int pageCounter = 0; pageCounter < svgList.size(); pageCounter++) {
			SYSOUT.print(pageCounter+"~");
			PageAnalyzer pageAnalyzer = PageAnalyzer.createAndAnalyze(svgList.get(pageCounter), pageCounter, rawSVGDirectory);
			pageAnalyzerList.add(pageAnalyzer);
		}
		return pageAnalyzerList;
	}
	
	public HtmlElement createRunningHtml() {
		runningTextElement = new HtmlDiv();
		for (PageAnalyzer pageAnalyzer : pageAnalyzerList) {
//			PageIO.copyChildElementsFromTo(pageAnalyzer.getRunningHtmlElement(), runningTextElement);
		}
		return runningTextElement;
	}

	public HtmlElement forceCreateRunningHtml() {
		runningTextElement = new HtmlDiv();
		for (PageAnalyzer pageAnalyzer : pageAnalyzerList) {
//			PageIO.copyChildElementsFromTo(pageAnalyzer.createRunningHtml(), runningTextElement);
		}
		return runningTextElement;
	}

	private void ensurePageAnalyzerList() {
		if (pageAnalyzerList == null) {
			pageAnalyzerList = new ArrayList<PageAnalyzer>();
		}
	}

	public void createSVGFilesfromPDF() {
		LOG.trace("createSVG");
		PDF2SVGConverterWrapper converter = new PDF2SVGConverterWrapper();
		File inFile = pdfIo.getInFile();
		String inputName = pdfIo.getInputName();
		if (inFile != null && inFile.exists()) {
			createSVGFilesfromPDF(converter, inFile.toString());
		} else if (inputName != null && inputName.startsWith(PDFAnalyzerIO.HTTP)) {
			pdfIo.createHttpInputName(inputName);
			createSVGFilesfromPDF(converter, inputName);
		} else {
			throw new RuntimeException("no input file: "+inFile);
		}
	}

	public void createSVGFilesfromPDF(PDF2SVGConverterWrapper converter, String inputName) {
		File svgDocumentDir = pdfIo.getRawSVGDirectory();
		File[] files = (svgDocumentDir == null ? null : svgDocumentDir.listFiles());
		if (!svgDocumentDir.exists() || files == null || files.length == 0) {
			svgDocumentDir.mkdirs();
			LOG.trace("running "+inputName+" to "+svgDocumentDir.toString());
			// only sets outdir
			converter.run("-outdir", svgDocumentDir.toString(), inputName );
		} else {
			LOG.trace("Skipping SVG because files in ("+svgDocumentDir+") already exist: "+files.length);
		}
		return;
	}

	private void ensurePDFIndex() {
//		if (pdfIndex == null) {
//			pdfIndex = new PDFIndex(this);
//		}
	}

	public static List<List<String>> findDuplicates(String title, Multimap<? extends Object, String> map) {
		List<List<String>> duplicateList = new ArrayList<List<String>>();
		for (Map.Entry<? extends Object, Collection<String>> mapEntry : map.asMap().entrySet()) {
			Object key = mapEntry.getKey();
			Collection<String> ids = mapEntry.getValue();
			List<String> idList = (Arrays.asList(ids.toArray(new String[0])));
			Collections.sort(idList);
			if (idList.size() > 1) {
				LOG.trace("DUPLICATES: "+title+" >"+key+"< "+idList);
				duplicateList.add(idList);
			}
		}
		return duplicateList;
	}
		
//	public PDFIndex getIndex() {
//		ensurePDFIndex();
//		return pdfIndex;
//	}

	/**
	 * Example usage:
	 * <p>
	 * mvn exec:java -Dexec.mainClass="org.contentmine.svg2xml.analyzer.PDFAnalyzer" 
	 * -Dexec.args="src/test/resources/pdfs/bmc"
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length == 0) {
			SYSOUT.println("PDFAnalyzer <inputFile(s)>");
			SYSOUT.println("mvn exec:java -Dexec.mainClass=\"org.contentmine.svg2xml.pdf.PDFAnalyzer\" " +
					" -Dexec.args=\"src/test/resources/pdfs/bmc/1471-2180-11-174.pdf\"");
			SYSOUT.println("OR java org.contentmine.svg2xml.analyzer.PDFAnalyzer src/test/resources/pdfs/bmc/1471-2180-11-174.pdf");
			SYSOUT.println("");
			SYSOUT.println("input can be:");
			SYSOUT.println("    (a) single PDF file as above (must end with \".pdf\")");
			SYSOUT.println("    (b) directory containing one or more *.pdf");
			SYSOUT.println("    (c) list of *.pdf files (relative to '.' or absolute)");
			SYSOUT.println("    (d) URL (must start with http:// or https://) - NYI");
			System.exit(0);
		} else {
			PDFAnalyzer analyzer = new PDFAnalyzer();
			analyzer.analyzePDFs(args[0]); 
		}
	}

	public int getDecimalPlaces() {
		return /*PageIO.DECIMAL_PLACES*/ 1;
	}
	
	public PDFAnalyzerIO getPDFIO() {
		return pdfIo;
	}

	public void setRawSvgDirectory(File rawSvgDirectory) {
		pdfIo.setRawSvgDirectory(rawSvgDirectory);
	}

	public List<PageAnalyzer> getPageAnalyzerList() {
		return pageAnalyzerList;
	}

	public HtmlElement getRunningTextHtml() {
		return runningTextElement;
	}

	public PDFAnalyzerOptions getPdfOptions() {
		return pdfOptions;
	}

	public void setPdfOptions(PDFAnalyzerOptions pdfOptions) {
		this.pdfOptions = pdfOptions;
	}

	public boolean getOutputHtmlChunks() {
		return pdfOptions.outputHtmlChunks;
	}

	public boolean getOutputFigures() {
		return pdfOptions.outputRawFigureHtml;
	}

	public boolean getOutputFooters() {
		return pdfOptions.outputFooters;
	}

	public boolean getOutputHeaders() {
		return pdfOptions.outputHeaders;
	}

	public boolean getOutputTables() {
		return pdfOptions.outputRawTableHtml;
	}

	public void setSkipOutput(boolean b) {
		pdfOptions.skipOutput = b;
	}

}