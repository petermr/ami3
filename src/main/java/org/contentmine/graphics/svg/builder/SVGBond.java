package org.contentmine.graphics.svg.builder;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.linestuff.SVGEdge;
import org.contentmine.graphics.svg.linestuff.SVGNode;

public class SVGBond extends SVGEdge {
	
	private static final Logger LOG = Logger.getLogger(SVGBond.class);
	private List<SVGAtom> atomList;
	private MoleculeBuilder moleculeBuilder;
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public SVGBond(SVGElement line) {
		super(line);
	}
	
	public SVGAtom getAtom(int index) {
		getOrCreateNodeList();
		return index >= 2 ? null :  (SVGAtom) nodeList.get(index);
	}

	public List<SVGAtom> getAtomList() {
		// these are actually atoms
		List<SVGNode> nodes = super.getOrCreateNodeList();
		atomList = new ArrayList<SVGAtom>();
		for (SVGNode node : nodes) {
			atomList.add((SVGAtom)node);
		}
		return atomList;
	}

	public AbstractCMElement getOrCreateSVG() {
		SVGG g = new SVGG();
		SVGLine edgeCopy = (SVGLine) this.copy();
		edgeCopy.setStrokeWidth(this.getWeight());
		g.appendChild(edgeCopy);
		g.appendChild(SVGText.createText(this.getMidPoint(), getId(), "fill:green;font-size:2;"));
		if (getWeight() > 1.5) {
			SVGLine line = new SVGLine(edgeCopy);
			line.setWidth(1.0 / 3.0);
			line.setStroke("white");
			g.appendChild(line);
		}
		return g;
	}

	public String toString() {
		getAtomList();
		StringBuilder sb = new StringBuilder();
		sb.append(this.getId()+": ");
		SVGAtom atom0 = atomList.get(0);
		sb.append(atom0 == null ? "null " : atom0.getId()+"; ");
		SVGAtom atom1 = atomList.get(1);
		sb.append(atom1 == null ? "null ": atom1.getId()+"; ");
		sb.append(this.getXY(0)+" "+this.getXY(1)+"; label: "+label+"; wt: "+getWeight()+" ");
		return sb.toString();
	}

	public void mergeAverageBondWithNearestAtom() {
		List<SVGAtom> moleculeBuilderAtomList = moleculeBuilder.getAtomList();
		for (int i = 0; i < 2; i++) {
			SVGAtom atomInBond = this.getAtom(i);
			Real2 thisXY = atomInBond.getXY();
			// find nearest atom
			SVGAtom nearestAtom = findNearestAtom(moleculeBuilderAtomList, thisXY);
			if (nearestAtom != null) {
				moleculeBuilderAtomList.remove(atomInBond);
//				LOG.info("Removing atom: "+atomInBond);
				this.getOrCreateNodeList().set(i, nearestAtom);
//				LOG.info("replaced with: "+nearestAtom);
			}
		}
	}

	private SVGAtom findNearestAtom(List<SVGAtom> moleculeBuilderAtomList, Real2 thisXY) {
		SVGAtom nearestAtom = null;
		double minDist = Double.MAX_VALUE;
		for (int j = 0; j < moleculeBuilderAtomList.size(); j++) {
			SVGAtom atom = moleculeBuilderAtomList.get(j);
			if (this.indexOf(atom) == -1) {
				double dist = atom.getXY().getDistance(thisXY);
				if (dist < getMoleculeBuilder().getMidPointDelta() && dist < minDist) {
					minDist = dist;
					nearestAtom = atom;
				}
			}
		}
		if (nearestAtom != null) {
//			LOG.debug(nearestAtom+"; "+minDist);
		}
		return nearestAtom;
	}

	public MoleculeBuilder getMoleculeBuilder() {
		return moleculeBuilder;
	}

	public void setMoleculeBuilder(MoleculeBuilder moleculeBuilder) {
		this.moleculeBuilder = moleculeBuilder;
	}


	
}
