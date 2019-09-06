package org.contentmine.svg2xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGSVG;

/** dummy class to enable new PDF2SVG to be slotted in.
 * 
 * wrapper for PDF2SVG
 * 
 * @author pm286
 *
 */
public class PDF2SVGConverterWrapper {
	private static final Logger LOG = Logger.getLogger(PDF2SVGConverterWrapper.class);
	private PDF2SVGConverter converter;
	
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public PDF2SVGConverterWrapper() {
		converter = new PDF2SVGConverter();
	}

	public void run(String ...pdf2svgArgs) {
		converter.run(pdf2svgArgs);
		return;
	}

	public List<SVGSVG> getPageList() {
		return converter.getPageList();
	}

	public void createCompactSVGFromPDF(File pdfOrigDir, File projectDir) {
		projectDir.mkdirs();
		File[] files = pdfOrigDir.listFiles();
		if (files != null) {
			for (File file : files) {
				File projectPdfDir = new File(projectDir, (file.isDirectory() ? file.getName() : FilenameUtils.getBaseName(file.toString())));
				projectPdfDir.mkdirs();
				File svgDir = new File(projectPdfDir, "svg/");
				if (file.isDirectory()) {
					File pdfFile = new File(file, "fulltext.pdf");
					if (!pdfFile.exists()) {
						continue;
					}
					createCompactSVGFromPDF(projectPdfDir, pdfFile, svgDir);
				} else {
					createCompactSVGFromPDF(projectPdfDir, file, svgDir);
				}
			}
		}
	}

	private void createCompactSVGFromPDF(File projectPdfDir, File pdfFile, File svgDir) {
		{
		svgDir.mkdirs();

		// FIXME compact fails
		String[] args0 = {"-logger", "-infofiles", "-logglyphs", "-compact", "-outdir", 
				svgDir.toString(), pdfFile.toString()};
		List<String> argList = new ArrayList<String>(Arrays.asList(args0));
		argList.add("-compact");
		run(argList.toArray(new String[0]));
		
		File pngDir = new File(projectPdfDir, "png/");
		List<File> pngFiles = new ArrayList<File>(FileUtils.listFiles(svgDir, new String[] {"png"}, false));
		for (File pngFile : pngFiles) {
			movePngFileToDirectory(pngDir, pngFile);
		}
		}
	}

	private void movePngFileToDirectory(File pngDir, File pngFile) {
		if (FileUtils.sizeOf(pngFile) == 0) {
			pngFile.delete();
		} else {
			try {
				FileUtils.moveToDirectory(pngFile, pngDir, true);
			} catch (FileExistsException fee) {
				File oldFile = new File(pngDir, pngFile.getName());
				try {
					oldFile.delete();
					FileUtils.moveToDirectory(pngFile, pngDir, true);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (Exception e) {
				LOG.error("cannot move file: "+e);
			}
		}
	}
	
	/** 
	 * Page numbered from ONE
	 * 
	 * @param file
	 * @param page
	 * @return
	 */
	public static SVGSVG getSVGPageFromPDF(File file, int page) {
		PDF2SVGConverterWrapper converter = new PDF2SVGConverterWrapper();
		converter.run("-outdir target "+file);
		LOG.warn("PDF2SVGConverter shorted out");
		SVGSVG svgPage = (page < 1 || page > converter.getPageList().size() ? null : converter.getPageList().get(page - 1));
		return svgPage;
	}
	

}
