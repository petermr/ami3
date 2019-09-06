package org.contentmine.svg2xml.table;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGTitle;
import org.contentmine.graphics.svg.text.build.Phrase;
import org.contentmine.graphics.svg.text.build.Word;
import org.contentmine.svg2xml.util.GraphPlot;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

public class ColumnManager {
	private static final Logger LOG = Logger.getLogger(ColumnManager.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public final static Comparator<ColumnManager> X_COMPARATOR;
	static {
		X_COMPARATOR = new Comparator<ColumnManager>() {
			public int compare(ColumnManager colManager1, ColumnManager colManager2 ) {
				if (colManager1 == null || colManager2 == null) {
					throw new RuntimeException("Null ColumnManager/s");
				}
				IntRange range1 = colManager1.getEnclosingRange();
				IntRange range2 = colManager2.getEnclosingRange();
				return (range1 == null || range2 == null) ? 0 : range1.getMin() - range2.getMin();
			}
		};
	}
	private IntRange enclosingRange;
	private Multiset<Integer> startXMultiset;
	private Multiset<Integer> endXMultiset;
	private List<Phrase> columnPhrases;
	private int yPointer = -1;
	private double epsilon = 0.3; // compares y values
	private RealArray xMinArray;
	private RealArray xMaxArray;
	private Double xMin;
	private RealArray indentArray;
	private Double xMax;

	public ColumnManager() {
	}

	public void setEnclosingRange(IntRange enclosingRange) {
		if (this.enclosingRange == null) {
			this.enclosingRange = enclosingRange;
		} else {
			if (!this.enclosingRange.equals(enclosingRange)) {
				LOG.trace("new EnclosingRange: "+this.enclosingRange+"=>"+enclosingRange);
//				this.enclosingRange = enclosingRange;
			}
		}
	}

	public void addEnclosingRange(IntRange enclosingRange) {
		if (this.enclosingRange == null) {
			this.enclosingRange = enclosingRange;
		} else {
			this.enclosingRange = this.enclosingRange.plus(enclosingRange);
		}
	}

	public void setStartX(Double startX) {
		if (startX != null) {
			ensureStartXMultiset();
			startXMultiset.add((int)(double)startX);
		}
	}

	private void ensureStartXMultiset() {
		if (startXMultiset == null) {
			startXMultiset = HashMultiset.create();
		}
	}

	public void setEndX(Double endX) {
		if (endX != null) {
			ensureEndXMultiset();
			endXMultiset.add((int)(double)endX);
		}
	}
	
	private void ensureEndXMultiset() {
		if (endXMultiset == null) {
			endXMultiset = HashMultiset.create();
		}
	}

	public void debug() {
		ensureStartXMultiset();
		ensureEndXMultiset();
	}

	public void addPhrase(Phrase phrase) {
		if (phrase != null && phrase.getStringValue().trim().length() != 0) {
			getOrCreateColumnPhrases();
			columnPhrases.add(phrase);
			addRange(phrase.getIntRange());
		} else {
			LOG.trace("adding Null/empty phrase; ignored");
		}
	}

	private void addRange(IntRange intRange) {
		if (enclosingRange == null) {
			enclosingRange = intRange;
		} else {
			enclosingRange = enclosingRange.plus(intRange);
		}
	}

	List<Phrase> getOrCreateColumnPhrases() {
		if (columnPhrases == null) {
			columnPhrases = new ArrayList<Phrase>();
		}
		return columnPhrases;
	}

	public Phrase getPhrase(int iRow) {
		getOrCreateColumnPhrases();
		return (iRow < 0 || iRow >= columnPhrases.size()) ? null : columnPhrases.get(iRow);
	}

	public IntRange getEnclosingRange() {
		return enclosingRange;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (columnPhrases != null) sb.append(String.valueOf(columnPhrases)+"\n");
		if (enclosingRange != null) sb.append(String.valueOf(enclosingRange)+"\n");
		if (startXMultiset != null) sb.append(String.valueOf(startXMultiset)+"\n");
		if (endXMultiset != null) sb.append(String.valueOf(endXMultiset)+"\n");
		return sb.toString();
	}

	public String getStringValue() {
		StringBuilder sb = new StringBuilder();
		for (Phrase phrase : columnPhrases) {
			sb.append(phrase.getStringValue()+" // ");
		}
		return sb.toString();
	}

	public void resetYPointer() {
		yPointer = 0;
	}

	/** 
	 * 
	 * @return if no phrase, return -1
	 */
	public double getPointerYCoord() {
		double y = -1;
		if (yPointer < columnPhrases.size()) {
			Phrase phrase = columnPhrases.get(yPointer);
			if (phrase != null) {
				Double yy = phrase.getY();
				if (yy != null) {
					y = yy;
				}
			}
		}
		return y;
	}

	public int getYPointer() {
		return yPointer;
	}

	private void addEmptyCell(Real2Range bbox, double fontSize) {
		Real2 xy = bbox.getLLURCorners()[0];
		Word emptyWord = Word.createEmptyWord(xy, fontSize);
		Phrase emptyPhrase = new Phrase(emptyWord);
		SVGElement plotBox = GraphPlot.createBoxWithFillOpacity(bbox, "green", 0.1);
		emptyPhrase.appendChild(plotBox);
		columnPhrases.add(yPointer, emptyPhrase);
	}

	/** 
	 * 
	 * @param y
	 */
	public void addCell(Real2Range bbox, double fontSize) {
		double y = bbox.getYMax();
		double x = enclosingRange.getMin();
		if (yPointer >= columnPhrases.size()) {
			addEmptyCell(bbox, fontSize);
		} else {
			Phrase phrase = columnPhrases.get(yPointer);
			Double ycell = phrase.getY();
			if (ycell == null) {
				LOG.trace("Null cell: "+phrase.getStringValue()+phrase.toXML());
			}
			if (ycell - y > epsilon) {
				addEmptyCell(bbox, fontSize);
			} else {
				// cell is already there
			}
		}
		yPointer++;
	}

	public SVGG createCellBoxes(int colno, String[] colors, double[] opacity) {
		SVGG g = new SVGG();
		g.setSVGClassName("col"+"."+colno);
		for (int iPhrase = 0; iPhrase < columnPhrases.size(); iPhrase++) {
			Real2Range contentBox = columnPhrases.get(iPhrase).getBoundingBox();
			if (contentBox.getYMin() < 0) {
				LOG.error("FIXME box: "+contentBox);
			}
			String iPhraseS = columnPhrases.get(iPhrase).getStringValue();
			if (iPhraseS == null || iPhraseS.trim().length() == 0) {
				LOG.trace("empty phrase; possible problem");
			} else {
				String title = colno+"."+iPhrase+"/"+columnPhrases.get(iPhrase).getStringValue();
				SVGTitle svgTitle = new SVGTitle(title);
				SVGRect plotBox = GraphPlot.createBoxWithFillOpacity(contentBox, colors[1], opacity[1]);
				plotBox.setSVGClassName("cell"+"."+colno+"."+iPhrase);
				plotBox.appendChild(svgTitle);
				g.appendChild(plotBox);
			}
		}
		return g;
	}
	
	public RealArray getOrCreateIndentArray() {
		getOrCreateXMinXMaxArray();
		indentArray = new RealArray(xMinArray);
		indentArray = indentArray.plus(-xMin);
		indentArray.format(2);
		return indentArray;
	}

	private RealArray getOrCreateXMinXMaxArray() {
		if (xMinArray == null || xMaxArray == null) {
			xMinArray = new RealArray();
			xMaxArray = new RealArray();
			for (int i = 0; i < columnPhrases.size(); i++) {
				Phrase phrase = columnPhrases.get(i);
				double x = phrase.getX();
				xMinArray.addElement(x);
				double xMax = phrase.getEndX();
				xMaxArray.addElement(x);
			}
			xMin = xMinArray.smallestElement();
			xMax = xMinArray.largestElement();
			// correct for missing values
			correctForMissingValues();
			xMin = xMinArray.smallestElement();
			xMax = xMinArray.largestElement();
		}
		return xMinArray;
	}

	private void correctForMissingValues() {
		for (int i = 0; i < columnPhrases.size(); i++) {
			Phrase phrase = columnPhrases.get(i);
			String value = phrase.getStringValue();
			if (value == null || value.trim().length() == 0) {
				xMinArray.setElementAt(i, xMax);
			}
		}
	}

	public double getMaxIndent() {
		return xMax;
	}

	public double getMinIndent() {
		return xMin;
	}

}
