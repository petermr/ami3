package org.contentmine.ami.plugins;

import java.io.File;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.CTreeList;
import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.Angle.Units;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.eucl.euclid.IntMatrix;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.graphics.svg.GraphicsElement.FontWeight;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.SVGText.RotateText;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset.Entry;

public class CooccurrenceAnalyzer {
	private static final String COOCCUR_CSV = "cooccur.csv";

	private static final Logger LOG = LogManager.getLogger(CooccurrenceAnalyzer.class);
public static final String COOCCURRENCE = CTreeList.FORBIDDEN_PREFIX + "cooccurrence";
	
	private OccurrenceAnalyzer rowAnalyzer;
	private OccurrenceAnalyzer colAnalyzer;
	private IntMatrix cooccurrenceMatrix;
	private EntityAnalyzer entityAnalyzer;

	public CooccurrenceAnalyzer(EntityAnalyzer entityAnalyzer) {
		setEntityAnalyzer(entityAnalyzer);
	}

	public void setEntityAnalyzer(EntityAnalyzer entityAnalyzer) {
		this.entityAnalyzer = entityAnalyzer;
	}
	
	public CooccurrenceAnalyzer setRowAnalyzer(OccurrenceAnalyzer rowAnalyzer) {
		this.rowAnalyzer = rowAnalyzer;
		return this;
	}

	public CooccurrenceAnalyzer setColAnalyzer(OccurrenceAnalyzer colAnalyzer) {
		this.colAnalyzer = colAnalyzer;
		return this;
	}

	public OccurrenceAnalyzer getRowAnalyzer() {
		return rowAnalyzer;
	}

	public OccurrenceAnalyzer getColAnalyzer() {
		return colAnalyzer;
	}

	public IntMatrix analyze() { // new 
		LOG.trace("rowA number of elements "+rowAnalyzer.getOrCreateSerialByTermImportance().size());
		LOG.trace("colA number of elements "+colAnalyzer.getOrCreateSerialByTermImportance().size());
		
		List<File> rowCTreeFiles = rowAnalyzer.getOrCreateCTreeFiles();
		List<File> colCTreeFiles = colAnalyzer.getOrCreateCTreeFiles();
		LOG.trace("rows trees> "+rowCTreeFiles.size()+" / "+rowCTreeFiles);
		LOG.trace("cols trees> "+colCTreeFiles.size()+" / "+colCTreeFiles);
		
		Map<File, List<Entry<String>>> rowEntryListByCTreeFile = rowAnalyzer.getOrCreateEntryListByCTreeFile();
		Map<File, List<Entry<String>>> colEntryListByCTreeFile = colAnalyzer.getOrCreateEntryListByCTreeFile();
		LOG.trace("rows "+rowAnalyzer.getName()+" / "+rowEntryListByCTreeFile);
		LOG.trace("cols "+colAnalyzer.getName()+" / "+colEntryListByCTreeFile);
		
		Map<String, Set<File>> fileSetByRowEntryValue = createFileSetByEntryString(rowEntryListByCTreeFile);
		Map<String, Set<File>> fileSetByColEntryValue = createFileSetByEntryString(colEntryListByCTreeFile);
		LOG.trace("files by rows "+rowAnalyzer.getName()+" / "+fileSetByRowEntryValue);
		LOG.trace("files by cols "+colAnalyzer.getName()+" / "+fileSetByColEntryValue);
		
		int rowCount = Math.min(rowAnalyzer.getMaxCount(), rowAnalyzer.getOrCreateEntriesSortedByImportance().size());
		int colCount = Math.min(colAnalyzer.getMaxCount(), colAnalyzer.getOrCreateEntriesSortedByImportance().size());
		cooccurrenceMatrix = new IntMatrix(
			rowCount,
			colCount
			);
		LOG.trace("COOC: "+cooccurrenceMatrix);

		List<Entry<String>> rowList = rowAnalyzer.getOrCreateEntriesSortedByImportance();
		for (int irow = 0; irow < rowCount; irow++) {
			Entry<String> entry = rowList.get(irow);
			String rowElement = entry.getElement();
			Set<File> rowFileSet = fileSetByRowEntryValue.get(rowElement);
			rowFileSet = rowFileSet == null ? new HashSet<File>() : rowFileSet;
			List<Entry<String>> colList = colAnalyzer.getOrCreateEntriesSortedByImportance();
			for (int jcol = 0; jcol < colCount; jcol++) {
				Entry<String> colEntry = colList.get(jcol);
				String colElement = colEntry.getElement();
				Set<File> colFileSet = fileSetByColEntryValue.get(colElement);
				colFileSet = colFileSet == null ? new HashSet<File>() : new HashSet<File>(colFileSet);
				colFileSet.retainAll(rowFileSet);
				cooccurrenceMatrix.setElementAt(irow, jcol, colFileSet.size());
			}
		}
		/**
		System.out.println(""+
				rowAnalyzer.getOrCreateEntriesSortedByImportance()+"\n"
				+colAnalyzer.getOrCreateEntriesSortedByImportance()+"\n"
				+rowAnalyzer.getName()+"-"+colAnalyzer.getName()+"\n"
				+cooccurrenceMatrix);
				*/
		return cooccurrenceMatrix;
	}

	private Multimap<String, File> createFileListByEntryString(Map<File, List<Entry<String>>> entryListByCTreeFile) {
		Multimap<String, File> fileListByEntryString = ArrayListMultimap.create();
		for (File file : entryListByCTreeFile.keySet()) {
			List<Entry<String>> entryList = entryListByCTreeFile.get(file);
			for (Entry<String> entry : entryList) { // seems cumbersome, am I missing something?
				for (int i = 0; i < entry.getCount(); i++) {
					fileListByEntryString.put(entry.getElement(), file);
				}
			}
		}
		LOG.debug("inverted map "+fileListByEntryString);
		return fileListByEntryString;
	}

	private Map<String, Set<File>> createFileSetByEntryString(Map<File, List<Entry<String>>> entryListByCTreeFile) {
		Map<String, Set<File>> fileSetByEntryString = new HashMap<String, Set<File>>();
		for (File file : entryListByCTreeFile.keySet()) {
			List<Entry<String>> entryList = entryListByCTreeFile.get(file);
			for (Entry<String> entry : entryList) { // seems cumbersome, am I missing something?
				String entryElement = entry.getElement(); // don't weight by count
				Set<File> fileSet = fileSetByEntryString.get(entryElement);
				if (fileSet == null) {
					fileSet = new HashSet<File>();
					fileSetByEntryString.put(entryElement, fileSet);
				}
				fileSet.add(file);
			}
		}
		return fileSetByEntryString;
	}

	public IntMatrix analyzeOld() {
		List<File> colCTreeFiles = colAnalyzer.getOrCreateCTreeFiles();
		List<File> rowCTreeFiles = rowAnalyzer.getOrCreateCTreeFiles();
		Map<File, List<Entry<String>>> rowEntryListByCTreeFile = rowAnalyzer.getOrCreateEntryListByCTreeFile();
		Map<File, List<Entry<String>>> colEntryListByCTreeFile = colAnalyzer.getOrCreateEntryListByCTreeFile();
		LOG.debug("rows "+rowAnalyzer.getName()+" / "+rowEntryListByCTreeFile);
		LOG.debug("cols "+colAnalyzer.getName()+" / "+colEntryListByCTreeFile);
		
		cooccurrenceMatrix = new IntMatrix(
				Math.min(rowAnalyzer.getMaxCount(), rowAnalyzer.getSize()),
				Math.min(colAnalyzer.getMaxCount(), colAnalyzer.getSize())
				);
		for (File rowCTreeFile : rowCTreeFiles) {
			List<Entry<String>> colEntryList = colEntryListByCTreeFile.get(rowCTreeFile);
			if (colEntryList != null) {
				List<Integer> colSerialList = colAnalyzer.getSerialList(colEntryList);
				debugList("col", colSerialList);
				List<Entry<String>> rowEntryList = rowEntryListByCTreeFile.get(rowCTreeFile);
				List<Integer> rowSerialList = rowAnalyzer.getSerialList(rowEntryList);
				debugList("row", rowSerialList);
				for (Integer rowSerial : rowSerialList) {
					if (rowSerial < rowAnalyzer.getMaxCount()) {
						for (Integer colSerial : colSerialList) {
							if (colSerial < colAnalyzer.getMaxCount()) {
								int count = cooccurrenceMatrix.elementAt(rowSerial, colSerial);
								count++;
								cooccurrenceMatrix.setElementAt(rowSerial, colSerial, count);
							}
						}
					}
				}
			}
		}
		return cooccurrenceMatrix;
	}

	private void debugList(String title, List<?> list) {
		String s = list.toString();
		LOG.debug(title+">> "+s.substring(0, Math.min(30,  s.length())));
	}

	public void writeCSV() throws IOException {
		File cooccurrenceFile = createCooccurrenceDir();
		cooccurrenceMatrix.writeCSV(new File(cooccurrenceFile, COOCCUR_CSV));
	}

	private File createCooccurrenceDir() {
		File cooccurrenceFile =  createFileName(COOCCURRENCE);
		cooccurrenceFile.getParentFile().mkdirs();
		return cooccurrenceFile;
	}

	public SVGSVG writeSVG() throws IOException {
		File cooccurrenceFile = new File(createCooccurrenceDir(), "cooccur.svg");
		SVGSVG svg = createSVG();
		if (svg != null) {
			XMLUtil.debug(svg, cooccurrenceFile, 1);
		}
		return svg;
		
	}

	/** really for debugging */
	public SVGSVG createSVG() {
		return createSVG(cooccurrenceMatrix, getOrCreateRowAnalyzer(), getOrCreateColAnalyzer());
	}

	private OccurrenceAnalyzer getOrCreateColAnalyzer() {
		if (colAnalyzer == null) {
			colAnalyzer = new OccurrenceAnalyzer();
		}
		return colAnalyzer;
	}

	private OccurrenceAnalyzer getOrCreateRowAnalyzer() {
		if (rowAnalyzer == null) {
			rowAnalyzer = new OccurrenceAnalyzer();
		}
		return rowAnalyzer;
	}

		/** really for debugging, rewrite with member variables */
	public SVGSVG createSVG(IntMatrix cooccurrenceMatrix, OccurrenceAnalyzer rowAnalyzer, OccurrenceAnalyzer colAnalyzer) {

		double x0 = 10.;
		double dx = 22;
		double xoff = 140;
		double dy = 22;
		double yoff = 140;
		double xoff0 = 0.0;
		double x;
		double y;
//		double fontSizeFactor = 0.85;
		double fontSizeFactor = 0.60;
		double strokeWidth = 0.8;
		int axisFontSize = (int) dy;
		String axisTextStroke = "red";
		double labelOpacity = 1.0;
		
		SVGG grid = new SVGG();
		if (cooccurrenceMatrix.getCols() <= 0 || cooccurrenceMatrix.getRows() <= 0) {
			return null;
		}
		int largestElement = cooccurrenceMatrix.largestElement();
		List<Entry<String>> rowEntries = rowAnalyzer == null ? null : rowAnalyzer.getOrCreateEntriesSortedByImportance();
		List<Entry<String>> colEntries = colAnalyzer == null ? null : colAnalyzer.getOrCreateEntriesSortedByImportance();

		SVGText colSvgText = plotTitle(colAnalyzer.getName(), xoff0, yoff - dy, axisFontSize, axisTextStroke, labelOpacity, grid);
		grid.appendChild(colSvgText);
		SVGText rowSvgText = plotTitle(rowAnalyzer.getName(), xoff0, yoff, axisFontSize, axisTextStroke, labelOpacity, grid);
		grid.appendChild(rowSvgText);

		for (int irow = 0; irow < cooccurrenceMatrix.getRows(); irow++) {
			y = (irow * dy) + yoff;
			if (rowAnalyzer != null) {
				if (irow < rowEntries.size()) {
					String rowTitle = rowEntries.get(irow).getElement();
					SVGText text = createRowText(x0, y + dy, fontSizeFactor * dy, rowTitle);
					grid.appendChild(text);
				}
			}
			for (int jcol = 0; jcol < cooccurrenceMatrix.getCols(); jcol++) {
				x = (jcol * dx) + xoff;
				if (colAnalyzer != null && irow == 0) {
					if (jcol < colEntries.size()) {
						String colTitle = colEntries.get(jcol).getElement();
						SVGText text = createColText(x + dx / 2, yoff - dy / 2, fontSizeFactor * dy, colTitle);
						grid.appendChild(text);
					}
				}
				int count = cooccurrenceMatrix.elementAt(irow, jcol);
				SVGG cell = createCell(dx, dy, x, y, fontSizeFactor * dy, strokeWidth, largestElement, count);
				grid.appendChild(cell);
			}
		}
		SVGSVG svg = SVGSVG.wrapAsSVG(grid);
		return svg;
	}

		private SVGText plotTitle(String rowName, double xoff, double yoff, int axisFontSize,
				String axisTextStroke, double labelOpacity, SVGG grid) {
			Real2 rowXy = new Real2(xoff, yoff);
			SVGText rowSvgText = SVGText.createDefaultText(rowXy, rowName, axisFontSize, axisTextStroke);
			return rowSvgText;
		}

	private static SVGG createCell(double dx, double dy, double x, double y, double fontSize, double strokeWidth,
			int largestElement, int count) {
		SVGG cell = new SVGG();
		SVGRect rect = new SVGRect(x, y, dx, dy);
		rect.setFill(calculateFill(count, largestElement));
		cell.appendChild(rect);
		SVGText cellText = createCellText(x, y + dy, fontSize, strokeWidth, count);
		cell.appendChild(cellText);
		return cell;
	}

	private static SVGText createRowText(double x, double y, double fontSize,
			String title) {
		SVGText text = new SVGText(new Real2(x, y), title.substring(0,  Math.min(15,  title.length())));
		setStyle(fontSize, title, text);
		return text;
	}

	private static SVGText createColText(double x, double y, double fontSize, String title) {
		Real2 xy = new Real2(x, y);
		SVGText text = new SVGText(xy, title.substring(0,  Math.min(15,  title.length())));
		Transform2 t2 = Transform2.getRotationAboutPoint(new Angle(Math.PI / 2.,Units.RADIANS), xy);
		text.applyTransform(t2, RotateText.FALSE);
		setStyle(fontSize, title, text);
		return text;
	}

	private static void setStyle(double fontSize, String title, SVGText text) {
		text.setFontSize(fontSize);
		text.setTitle(title);
		text.setFontWeight(FontWeight.BOLD);
		text.setFill("black");
		text.setFontFamily("Helvetica");
	}

	private static SVGText createCellText(double x, double y, double fontSize,
			double strokeWidth, int count) {
		String fill = "orange";
		String stroke = "orange";
		SVGText cellText = new SVGText(new Real2(x, y), String.valueOf(count));
		cellText.setFontSize(fontSize);
		cellText.setFill(fill);
		cellText.setStroke(stroke);
		cellText.setStrokeWidth(strokeWidth);
		cellText.setFontFamily("Helvetica");
		return cellText;
	}

	private static String calculateFill(int count, int largestElement) {
		double grayfactor = 1;
		int gray = 255 - (int) ((255 * count) / (largestElement * grayfactor));
		String grayS = Integer.toHexString(gray);
		String rgb = "#" + grayS + grayS + grayS;
		return rgb;
	}

	private File createFileName(String suffix) {
		File top = new File(entityAnalyzer.getProjectDir(), suffix);
		String name = rowAnalyzer.getName() + "-" + colAnalyzer.getName() + "/";
		File dir = new File(top, name);
		return dir;
	}

	public IntMatrix getCooccurrenceMatrix() {
		return cooccurrenceMatrix;
	}

	public void setCooccurrenceMatrix(IntMatrix cooccurrenceMatrix) {
		this.cooccurrenceMatrix = cooccurrenceMatrix;
	}

	public EntityAnalyzer getEntityAnalyzer() {
		return entityAnalyzer;
	}

}
