package org.contentmine.graphics.svg.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Array;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGCircle;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.linestuff.SVGEdge;
import org.contentmine.graphics.svg.linestuff.SVGNode;

public class SVGAtom extends SVGNode {
	private static final Logger LOG = Logger.getLogger(SVGAtom.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static Map<String, String> atomColorMap = new HashMap<String, String>();
	static {
		atomColorMap.put("C", "black");
		atomColorMap.put("N", "blue");
		atomColorMap.put("O", "red");
		atomColorMap.put("OH", "red");
		atomColorMap.put("S", "orange");
	};
	private double radius = 2.0;
	private MoleculeBuilder moleculeBuilder;
	private List<SVGAtom> neighbourList;
	private SVGText labelText;
	private double maxDistanceForStubNeighbour;
	private Real2 centreOfStubIntersectionXY;
	// empirical proportion of length to be added onto stub bond
	private double stubExtensionFactor = 0.4;
	
	public SVGAtom(MoleculeBuilder moleculeBuilder, SVGText text) {
		super(text);
		this.setMoleculeBuilder(moleculeBuilder);
	}

	private void setMoleculeBuilder(MoleculeBuilder moleculeBuilder) {
		this.moleculeBuilder = moleculeBuilder;
	}

	@Override
	public AbstractCMElement getOrCreateSVG() {
		SVGG g = new SVGG();
		if (label != null && !"C".equals(label)) {
			SVGCircle circle = new SVGCircle(this.getMidXY(), radius);
			circle.setFill("white");
			g.appendChild(circle);
			String color = atomColorMap.get(label);
			labelText = SVGText.createText(getXY(), label, "fill:"+color+";");
			labelText.setFontSize(5.0);
			g.appendChild(labelText);
		} else {
//			g.appendChild(new SVGCircle(this.getMidXY(), 0.5));
		}
//		drawBBox(g); // doesn't seem right yet
		if (centreOfStubIntersectionXY != null) {
			SVGCircle circle = new SVGCircle(centreOfStubIntersectionXY, 2.0);
			circle.setFill("purple");
			circle.setOpacity(0.3);
			g.appendChild(circle);
		}
		g.appendChild(SVGText.createText(this.getMidXY(), this.getId(), "fill:cyan;font-size:3;"));
		drawNeighbours(g);
		return g;
	}

	private void drawBBox(SVGG g) {
		if (labelText != null) {
			Real2Range bbox = labelText.getBoundingBoxForCenterOrigin();
			SVGRect rect = SVGRect.createFromReal2Range(bbox);
			g.appendChild(rect);
		}
	}

	private void drawNeighbours(SVGG g) {
		for (SVGAtom neighbour : this.getOrCreateNeighbourList()) {
			SVGLine line = new SVGLine(this.getMidXY(), neighbour.getXY());
			line.setStrokeWidth(0.5);
			line.setStroke("brown");
			line.setOpacity(0.3);
			g.appendChild(line);
		}
	}

	private Real2 getMidXY() {
		Real2Range bbox = this.getBoundingBox();
		Real2[] corners = bbox.getLLURCorners();
		Real2 xyMid = corners[0].getMidPoint(corners[1]);
		return xyMid;
	}

	public List<SVGAtom> createDisconnectedNeighbourList() {
		for (SVGAtom atom : moleculeBuilder.getAtomList()) {
			if (!this.equals(atom) && "C".equals(atom.getLabel())) {
				double dist = atom.getXY().getDistance(this.getMidXY());
				maxDistanceForStubNeighbour = 5;
				if (dist < maxDistanceForStubNeighbour) {
					this.addNeighbour(atom);
				}
			}
		}
		return neighbourList;
	}

	private void addNeighbour(SVGAtom atom) {
		getOrCreateNeighbourList();
		neighbourList.add(atom);
	}

	private List<SVGAtom> getOrCreateNeighbourList() {
		if (neighbourList == null) {
			neighbourList = new ArrayList<SVGAtom>();
		}
		return neighbourList;
	}

	public Real2 getIntersection(List<SVGAtom> neighbours) {
		Real2 meanXY = null;
		
		List<SVGBond> intersectingBondList = new ArrayList<SVGBond>();
		if (neighbours != null) {
			for (SVGAtom neighbour : neighbours) {
				List<SVGBond> neighbourBonds = neighbour.getBondList();
				SVGBond neighbourBond = neighbourBonds.get(0);
				Angle angle = this.getAngle(neighbourBond, neighbour);
				if (angle != null) {
					double radians = Math.abs(angle.getRadian());
					if (radians < Math.PI / 2.0) {
						LOG.error("angle too acute: "+radians);
						continue;
					}
					intersectingBondList.add(neighbourBond);
				}
			}
			double deltaAngMin = 0.1;
			if (intersectingBondList.size() == 1) {
				meanXY = extendStubBond(neighbours, intersectingBondList.get(0));
			} else {
				meanXY = getMeanIntersection(intersectingBondList, deltaAngMin);
			}
		}
		return meanXY;
	}

	private Real2 extendStubBond(List<SVGAtom> neighbours, SVGBond bond) {
		Real2 meanXY;
		SVGAtom stubAtom = neighbours.get(0);
		int stubAtomIndex = bond.indexOf(stubAtom);
		Real2 bondVec = bond.getXY(stubAtomIndex).subtract(bond.getXY(1 - stubAtomIndex));
		Real2 projection = bondVec.multiplyBy(stubExtensionFactor );
		meanXY = bond.getXY(stubAtomIndex).plus(projection);
		return meanXY;
	}

	private Real2 getMeanIntersection(List<? extends SVGLine> intersectingLineList, double deltaAngMin) {
		Real2 meanXY = null;
		Real2Array intersectionsXY = new Real2Array();
		if (intersectingLineList.size() == 0) {
			throw new RuntimeException("no intersecting bonds "+intersectingLineList);
		} else if (intersectingLineList.size() == 1) {
			throw new RuntimeException("one intersecting bonds "+intersectingLineList);
		} else {
			for (int i = 0; i < intersectingLineList.size() - 1; i++) {
				SVGLine line0 = intersectingLineList.get(i);
				for (int j = i + 1; j < intersectingLineList.size(); j++) {
					SVGLine line1 = intersectingLineList.get(j);
					Angle angle = line1.getEuclidLine().getAngleMadeWith(line0.getEuclidLine());
					double radians = Math.abs(angle.getRadian());
					if (Math.PI - radians > deltaAngMin) {
						Real2 intersectXY = line0.getIntersection(line1);
						intersectionsXY.addElement(intersectXY);
					} else {
						LOG.warn("angle too small for intersection: "+radians);
					}
				}
			}
			if (intersectionsXY == null || intersectionsXY.size() == 0) {
				LOG.warn("no intersections from "+intersectingLineList);
			} else {
				meanXY = intersectionsXY.getMean();
			}
		}
		return meanXY;
	}

	private Angle getAngle(SVGBond bond, SVGAtom atom) {
		Angle angle = null;
		int index = bond.indexOf(atom);
		if (index == -1) {
			LOG.error("*** cannot find atom "+atom+" in "+bond.hashCode()+"; "+bond);
			LOG.debug("MOL "+moleculeBuilder.toString());
		} else {
			SVGNode n1 = bond.getNode(index);
			SVGNode n2 = bond.getNode(1 - index);
			Real2 xy0 = this.getXY();
			Real2 xy1 = n1.getXY();
			Real2 xy2 = n2.getXY();
			angle = Real2.getAngle(xy0, xy1, xy2);
		}
		return angle;
	}

	private List<SVGBond> getBondList() {
		List<SVGBond> bondList = new ArrayList<SVGBond>();
		List<SVGEdge> edgeList = this.getOrCreateEdges();
		for (SVGEdge edge : edgeList) {
			bondList.add((SVGBond)edge);
		}
		return bondList;
	}

	/** finds point of intersection of bonds pointing at atom
	 * for hetero atoms in 2D  the bonds are usually partially obscured/clipped for clarity
	 * the centre of this atom should be at the point of intersection if well drawn
	 * 
	 * @return average crossing point
	 */
	public Real2 getCenterOfIntersectionOfNeighbouringStubBonds() {
		List<SVGAtom> neighbours = createDisconnectedNeighbourList();
		centreOfStubIntersectionXY = getIntersection(neighbours);
		// debug
		if (neighbours != null) {
			for (SVGAtom neighbour : neighbours) {
				SVGBond neighbond = neighbour.getBondList().get(0);
				Angle angle = Real2.getAngle(neighbond.getXY(1), neighbond.getXY(0), centreOfStubIntersectionXY);
			}
		}
		return centreOfStubIntersectionXY;
	}

	/** extend stub bonds pointing at atom, remoce old atoms and anneal onto this.
	 * 
	 */
	public void joinNeighbouringStubBonds() {
		for (SVGAtom stubNeighbour : neighbourList) {
			SVGBond stubNeighbourBond = stubNeighbour.getBondList().get(0); // there is only one bond
			int idx = stubNeighbourBond.indexOf(stubNeighbour);
			if (idx == -1) {
				LOG.error("could not find atom in neighbour bond "+stubNeighbour+"; "+stubNeighbourBond);
				continue;
			}
			stubNeighbourBond.setXY(centreOfStubIntersectionXY, idx);
			// replace stub atom by this
			List<SVGNode> subNeighbourBondedAtoms = stubNeighbourBond.getOrCreateNodeList();
			subNeighbourBondedAtoms.set(idx, this);
			// remove stub neighbour
			moleculeBuilder.getAtomList().remove(stubNeighbour);
		}
	}

	
}
