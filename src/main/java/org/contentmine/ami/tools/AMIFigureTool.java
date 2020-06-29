package org.contentmine.ami.tools;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.util.CMineGlobber;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlImg;
import org.contentmine.graphics.html.HtmlLi;
import org.contentmine.graphics.html.HtmlOl;
import org.contentmine.graphics.html.HtmlTable;
import org.contentmine.graphics.html.HtmlTd;
import org.contentmine.graphics.html.HtmlTr;
import org.contentmine.graphics.html.util.HtmlUtil;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
/**
 * 
 * @author pm286
 *
 */

@Command(
name = "figure",
description = "creates Figures from primitives (e.g. adds XML captions to figures)."
		+ "experimental."
)
public class AMIFigureTool extends AbstractAMITool {

	private static final Logger LOG = LogManager.getLogger(AMIFigureTool.class);
	
    @Option(names = {"--create"},
        description = "combines captions and images")
    private boolean create = true;

    /** used by some non-picocli calls
     * obsolete it
     * @param cProject
     */
	public AMIFigureTool(CProject cProject) {
		this.cProject = cProject;
	}
	
	public AMIFigureTool() {
	}
	
    @Override
	protected boolean parseGenerics() {
		super.parseGenerics();
		return true;
	}

    @Override
	protected void parseSpecifics() {
		super.parseSpecifics();
	}
    
    @Override
    protected void runSpecifics() {
    	if (processTrees()) { 
    	} else {
			LOG.error("must give cProject or cTree");
	    }
   }

	protected boolean processTree() {
		processedTree = true;
		LOG.warn("cTree>> "+cTree.getName());
		if (create) {
			createFigures();
		}
		return processedTree;
	}

	private void createFigures() {
		// figure captions
		Map<Integer, File> captionByInt = createMap(Pattern.compile(".*figure_(\\d+)\\.html"), "**/sections/figures/figure*.html");
		//figures by page.serial
		List<File> imgFiles = CMineGlobber.listGlobbedFilesQuietly(cTree.getDirectory(),  "**/pdfimages/image*/raw.png");
		Collections.sort(imgFiles, new ImageFileComparator(".*/pdfimages/image\\.(\\d+)\\.(\\d+)\\..*"));
		// OCR by page.serial
		List<File> ocrFiles = CMineGlobber.listGlobbedFilesQuietly(cTree.getDirectory(), "**/pdfimages/image*/image*/image*/hocr/hocr.svg");
		Collections.sort(ocrFiles, new ImageFileComparator(".*/pdfimages/image\\.(\\d+)\\.(\\d+)\\..*"));
		
		if (captionByInt.size() != imgFiles.size() || captionByInt.size() != ocrFiles.size()) {
			LOG.info(" bad sizes: captions: "+captionByInt.size()+"; img: " + imgFiles.size()+"; ocrs: " + ocrFiles.size());
			for (File ocr : ocrFiles) {
				LOG.info(ocr.getParentFile().getParentFile().getParentFile().getParentFile());
			}
			return;
		}
		
		HtmlOl ol=  new HtmlOl();
		for (int i = 1; i <= captionByInt.size(); i++ ) {
			HtmlLi li = combineFigureInfo(captionByInt.get(i), imgFiles.get(i - 1), ocrFiles.get(i - 1).toString());
			ol.addFluent(li);
		}
		File listFile = new File(imgFiles.get(0).getParentFile().getParentFile(), "images.html");
		XMLUtil.writeQuietly(ol, listFile, 1);
	}

	private HtmlLi combineFigureInfo(File captionFile, File imgFile, String ocrFilename) {
		HtmlTable table = new HtmlTable();
		HtmlTr panelTr = new HtmlTr();
		table.getOrCreateTbody().addRow(panelTr);
		
		HtmlTd td = new HtmlTd();
		panelTr.appendChild(td);
		HtmlImg img = new HtmlImg();
		img.setSrc(imgFile.toString().split(".*pdfimages/")[1]);
		img.setHeight(300);
		td.appendChild(img);
		
		td = new HtmlTd();
		panelTr.appendChild(td);
		img = new HtmlImg();
		img.setSrc(ocrFilename.split(".*pdfimages/")[1]);
		img.setHeight(300);
		td.appendChild(img);
		
		HtmlElement caption = (HtmlElement) HtmlUtil.parseCleanlyToXHTML(captionFile);
		HtmlTr row = new HtmlTr();
		table.addRow(row);
		row.appendChild(caption);
		
		HtmlLi li = new HtmlLi();
		li.addFluent(table);
		li.setStyle("border : 2px black solid;");
		return li;
	}

	private Map<Integer, File> createMap(Pattern sectionPattern, String glob) {
		List<File> files = CMineGlobber.listGlobbedFilesQuietly(cTree.getDirectory(),
				glob);
		return createMap(sectionPattern, files);
	}

	private Map<Integer, File> createMap(Pattern sectionPattern, List<File> files) {
		Map<Integer, File> figureByInt = new HashMap<>();
		for (File file : files) {
			String filename = file.toString();
//			LOG.info(filename);
			Matcher matcher = sectionPattern.matcher(filename);
			if (matcher.matches()) {
				Integer idx = new Integer(matcher.group(1));
				figureByInt.put(idx, file);
			}
		}
		return figureByInt;
	}


}
/** compare entries by their lower-case terms
 * 
 */
class ImageFileComparator implements Comparator<File> {
	
	private Pattern pattern;
	
	public ImageFileComparator(String regex) {
		pattern = Pattern.compile(regex);
	}
	
	// sort on page.serial of image file
	@Override
	public int compare(File o1, File o2) {
		if (o1 == null || o2 == null) {
			return 0;
		}
		String name1 = o1.toString();
		Matcher matcher1 = pattern.matcher(name1);
		if(!matcher1.matches()) return -1;
		String name2 = o2.toString();
		Matcher matcher2 = pattern.matcher(name2);
		if(!matcher2.matches()) return -1;
		if (Integer.parseInt(matcher1.group(1)) == Integer.parseInt(matcher2.group(1))) {
			return Integer.parseInt(matcher1.group(2)) - Integer.parseInt(matcher2.group(2));
		} else {
			return Integer.parseInt(matcher1.group(1)) - Integer.parseInt(matcher2.group(1));
		}
	}
	
}
