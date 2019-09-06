package org.contentmine.ami.plugins.phylotree;

import java.util.List;

import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.linestuff.ComplexLine;
import org.contentmine.graphics.svg.linestuff.ComplexLine.LineOrientation;
import org.contentmine.graphics.svg.linestuff.ComplexLine.SideOrientation;

/** 
 * Data transfer object for Lines in tree.
 * 
 * @author pm286
 */
public class LineContainer {

	
	private static double eps = PhyloTreeSVGAnalyzer.DEFAULT_PIXEL_EPS;
	
	private List<SVGLine> lines;
	private LineContainer perpendicularContainer;

	private List<ComplexLine> complexLines;
	private List<ComplexLine> emptyEndedLines;
	private List<ComplexLine> doubleEndedLines;
	private List<ComplexLine> minusEndedLines;
	private List<ComplexLine> plusEndedLines;

	public LineContainer(double eps) {
		setPixelEpsilon(eps);
	}

	public void createLines(List<SVGLine> svgLines, LineOrientation orientation) {
		this.lines = ComplexLine.createSubset(svgLines, orientation, eps);
	}

	public void createComplexLines() {
		this.complexLines = ComplexLine.createComplexLines(this.lines, this.perpendicularContainer.getLines(), eps);
	}

	public void createEmptyEndedLines() {
		this.emptyEndedLines = ComplexLine.extractLinesWithBranchAtEnd(this.complexLines, SideOrientation.EMPTYLIST);
	}

	public void createDoubleEndedLines() {
		this.doubleEndedLines = ComplexLine.extractLinesWithBranchAtEnd(this.complexLines, SideOrientation.MINUSPLUSLIST);
	}

	public void createMinusEndedLines() {
		this.minusEndedLines = ComplexLine.extractLinesWithBranchAtEnd(this.complexLines, SideOrientation.MINUSLIST);
	}

	public void createPlusEndedLines() {
		this.plusEndedLines = ComplexLine.extractLinesWithBranchAtEnd(this.complexLines, SideOrientation.PLUSLIST);
	}
	
	// getters and setters
	
	private void setPixelEpsilon(double eps) {
		this.eps = eps;
	}

	public void setPerpendicularLines(LineContainer perpendicularLines) {
		this.perpendicularContainer = perpendicularLines;
	}

	public List<SVGLine> getLines() {
		return lines;
	}

	protected LineContainer getPerpendicularContainer() {
		return perpendicularContainer;
	}

	protected List<ComplexLine> getComplexLines() {
		return complexLines;
	}

	protected List<ComplexLine> getEmptyEndedLines() {
		return emptyEndedLines;
	}

	protected List<ComplexLine> getDoubleEndedLines() {
		return doubleEndedLines;
	}

	protected List<ComplexLine> getMinusEndedLines() {
		return minusEndedLines;
	}

	protected List<ComplexLine> getPlusEndedLines() {
		return plusEndedLines;
	}

	void createAllComplexLines() {
		createComplexLines();
		createEmptyEndedLines();
		createDoubleEndedLines();
		createMinusEndedLines();
		createPlusEndedLines();
	}

}
