package org.contentmine.norma.output;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.RegexPathFilter;
import org.contentmine.cproject.util.Utils;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlImg;
import org.contentmine.graphics.html.HtmlP;
import org.contentmine.graphics.html.HtmlTable;
import org.contentmine.graphics.html.HtmlTd;
import org.contentmine.graphics.html.HtmlTr;
import org.contentmine.graphics.svg.SVGElement;

/** creates HTML output for display
 * 
 * @author pm286
 *
 */
public class HtmlDisplay {
	private static final Logger LOG = LogManager.getLogger(HtmlDisplay.class);

protected String output;
	private List<RegexPathFilter> displayFilters;
	protected CTree cTree;
	protected Pattern fileFilterPattern;
	private File currentDirectory;

	public HtmlDisplay() {
	}

	public HtmlDisplay(List<String> displayFilterStrings) {
		this.createDisplayFilters(displayFilterStrings);
	}

	/** create row, and write to HTML .
	 * 
	 */
	public void display() {
		if (output != null) {
			List<File> files = new RegexPathFilter(fileFilterPattern).listDirectoriesRecursively(cTree.getDirectory());
			LOG.debug("files: "+files.size() + " "+cTree.getDirectory()+"; "+fileFilterPattern);
			for (File file : files) {
				String title = getTitleFromFile(file);
				currentDirectory = file;
				HtmlTable table = new HtmlTable();
				HtmlTr tr = createRowOfAggregatedHtmlFiles();
				table.appendChild(tr);
				table.setTitle(title);
				table.setId(title);
				table.setClassAttribute(HtmlTabbedButton.TABCONTENT);
				try {
					File outputFile = new File(file, this.output);
					LOG.debug("output to "+outputFile.getAbsolutePath());
					XMLUtil.debug(table, outputFile, 1);
				} catch (IOException e) {
					throw new RuntimeException("Cannot write "+output, e);
				}
			}
		}
	}

	private String getTitleFromFile(File file) {
		String title = file.getName();
		LOG.debug("getting title as: "+title);
		return title;
	}

	public HtmlTr createRowOfAggregatedHtmlFiles() {
		if (cTree == null) {
			LOG.error("no CTree");
			return null;
		}
		if (output == null) {
			LOG.error("no output");
			return null;
		}
		HtmlTr tr = createTrWithTdCellsForRegexFilters();
		return tr;
	}

	private HtmlTr createTrWithTdCellsForRegexFilters() {
		HtmlTr tr = new HtmlTr();
		for (RegexPathFilter displayFilter : displayFilters) {
			File file = getSingleFile(displayFilter);
			if (file != null) {
				HtmlTd td = createTd(file);
				tr.appendChild(td);
				File directory = file.getParentFile();
				currentDirectory = directory;
			}
		}
		return tr;
	}

	private File getSingleFile(RegexPathFilter displayFilter) {
		File[] files = Utils.getFilesWithFilter(currentDirectory, displayFilter);
		File file = null;
		if (files.length > 0) {
			file = files[0];
		}
		return file;
	}

	private HtmlTd createTd(File file) {
		LOG.debug("processing TD "+file);
		HtmlTd td = new HtmlTd();
		String filename = file == null ? null : file.getName();
		if (file == null) {
			td.appendChild(new HtmlP("null file"));
		} else if (filename.endsWith(".svg")) {
			AbstractCMElement svgElement = createSVG(file);
			td.appendChild(svgElement);
		} else if (filename.endsWith(".png")) {
			HtmlImg img = createHtmlImg(file.getName());
			td.appendChild(img);
		} else if (filename.endsWith(".html")) {
			HtmlElement htmlElement = createHtml(file);
			if (htmlElement != null) td.appendChild(htmlElement);
		} else {
			td.appendChild(new HtmlP("unknown"));
		}
//		String name = filename == null ? "null" : filename;
		String relativeToHome = file == null ? " null" : Util.getRelativeFilename(new File("."), file, File.separator);
		td.appendChild(new HtmlP(relativeToHome));
		return td;
	}

	private HtmlElement createHtml(File file) {
		HtmlElement htmlElement = HtmlElement.create(XMLUtil.parseQuietlyToDocument(file).getRootElement());
		List<HtmlTable> tables = HtmlTable.extractSelfAndDescendantTables(htmlElement);
		if (tables.size() == 1) {
			htmlElement = tables.get(0);
			htmlElement.detach();
			return htmlElement;
		} else {
			return new HtmlP("no single table");
		}
	}

	private SVGElement createSVG(File file) {
		SVGElement svgElement = SVGElement.readAndCreateSVG(file);
		// remove sodipodi inkscape //		<sodipodi:namedview
		XMLUtil.removeElementsByXPath(svgElement, "//*[local-name()='g' and @class='namedview']");
		XMLUtil.removeElementsByXPath(svgElement, "//*[local-name()='g' and @class='metadata']");
		// remove defs/clipPath
		XMLUtil.removeElementsByXPath(svgElement, "//*[local-name()='defs']/*[local-name()='clipPath']");
		
		return svgElement;
	}

	private HtmlImg createHtmlImg(File file) {
		String relativeFilename = Util.getRelativeFilename(cTree.getDirectory(), file, File.separator);
		return createHtmlImg(relativeFilename);
	}

	/** create img with relative filename.
	 * 
	 * @param filename (caller is responsible for anchoring this)
	 * @return
	 */
	private HtmlImg createHtmlImg(String filename) {
		HtmlImg img = new HtmlImg();
		img.setSrc(filename);
		return img;
	}

	protected void createDisplayFilters(List<String> displayFilterStrings) {
		displayFilters = new ArrayList<RegexPathFilter>();
		for (String displayFilterString : displayFilterStrings) {
			displayFilters.add(new RegexPathFilter(displayFilterString));
		}
		if (displayFilterStrings.size() == 1) {
			fileFilterPattern = Pattern.compile(displayFilterStrings.get(0));
		}
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public void setCTree(CTree currentCTree) {
		this.cTree = currentCTree;
	}

	public void setFileFilterPattern(Pattern fileFilterPattern) {
		this.fileFilterPattern = fileFilterPattern;
	}
	

}
