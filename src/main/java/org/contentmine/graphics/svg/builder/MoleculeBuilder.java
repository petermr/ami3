package org.contentmine.graphics.svg.builder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.Angle.Units;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGCircle;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGLineList;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.cache.ComponentCache;
import org.contentmine.graphics.svg.plot.XPlotBox;

import nu.xom.Attribute;
import nu.xom.Element;

/** a simple class to make molecules from SVGs.
 * 
 * @author pm286
 *
 */
public class MoleculeBuilder {
	private static final Logger LOG = Logger.getLogger(MoleculeBuilder.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	private static final String BOND_ID = "e";
	private static final String NODE_ID = "n";

	private static final String C = "C";
	private static final String H = "H";
	
	private static final String CML_NS = "http://www.xml-cml.org/schema"; // check this
	private static final String CML = "cml";
	private static final String ATOM = "atom";
	private static final String ATOM_ARRAY = "atomArray";
	private static final String ATOM_REFS2 = "atomRefs2";
	private static final String BOND = "bond";
	private static final String BOND_ARRAY = "bondArray";
	private static final String ELEMENT_TYPE = "elementType";
	private static final String ID = "id";
	
	private static final String MOLECULE = "molecule";
	private static final String ORDER = "order";
	private static final String X2 = "x2";
	private static final String Y2 = "y2";

	private double endDelta = 0.3;
	private double midPointDelta = 2.0;
	private Angle parallelEps = new Angle(0.05, Units.RADIANS);
	private List<SVGAtom> atomList;
	private List<SVGBond> bondList;
	private Map<String, SVGBond> bondById;
	private Set<SVGAtom> nonCarbonAtoms;
	private String title;
	private String program;
	private File outputDir;
	private File inputDir;
	private File inputFile;

	public void createWeightedLabelledGraph(AbstractCMElement svgElement) {
		XPlotBox xPlotBox = new XPlotBox();
		ComponentCache componentCache = new ComponentCache(xPlotBox); 
		componentCache.readGraphicsComponentsAndMakeCaches(svgElement);
		SVGLineList lineList = componentCache.getOrCreateLineCache().getOrCreateLineList();
		bondList = createBondList(lineList);
		List<SVGText> textList = componentCache.getOrCreateTextCache().getOrCreateOriginalTextList();
		// make nodes from all texts
		addNonCarbonAtoms(textList);
		addLinesAndJoin();
		getOrCreateBondMap();
		gatherMultipleBonds();
//		LOG.warn("must no skipp joinDisconnectedAtoms");
		joinDisconnectedAtoms();
	}

    private Map<String, SVGBond> getOrCreateBondMap() {
    	if (bondById == null) {
    		bondById = new HashMap<String, SVGBond>();
    	}
    	return bondById;
	}

	private void joinDisconnectedAtoms() {
    	for (SVGAtom atom : nonCarbonAtoms) {
    		Real2 xy = atom.getCenterOfIntersectionOfNeighbouringStubBonds();
    		if (xy != null) {
    			atom.setXY(xy);
    			atom.joinNeighbouringStubBonds();
    		}
    	}
	}

	/** inefficient quadratic
 * only does double bonds ATM
 */
	private void gatherMultipleBonds() {
		SVGG g = new SVGG();
		for (int i = 0; i < getBondList().size() - 1; i++) {
			SVGBond bondi = getBondList().get(i);
			for (int j = i + 1; j < getBondList().size(); j++) {
				SVGBond bondj = getBondList().get(j);
				if (bondi.isAntiParallelTo(bondj, parallelEps) || bondi.isParallelTo(bondj, parallelEps)) {
					double delta = bondi.getMidPoint().getDistance(bondj.getMidPoint());
					if (delta < getMidPointDelta()) {
						SVGBond primaryBond = getPrimaryBond(bondi, bondj);
						if (primaryBond == null) {
							primaryBond = createAverageBond(bondi, bondj);
							removeAtoms(bondj.getAtomList());
							removeBond(bondj);
//							LOG.warn("equal double bonds; need to relocate average atom");
							primaryBond.mergeAverageBondWithNearestAtom();
						} else {
							SVGBond minorBond = primaryBond == bondi ? bondj : bondi;
							removeAtoms(minorBond.getAtomList());
							removeBond(minorBond);
							
						}
						primaryBond.setWeight(2);
					}
				}
			}
		}
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/debug/double.svg"));
	}

	private void removeAtoms(List<SVGAtom> atomAtomList) {
		for (SVGAtom atomAtom : atomAtomList) {
			this.atomList.remove(atomAtom);
		}
	}

	/** find average bond
	 * 
	 * @param bondi // takes this as the result; modified
	 * @param bondj
	 * @return
	 */
	private SVGBond createAverageBond(SVGBond bondi, SVGBond bondj) {
		int index0 = 0;
		int index1 = 1;
		if (bondi.isAntiParallelTo(bondj, parallelEps)) {
			index0 = 1;
			index1 = 0;
		}
		Real2 xy0i = bondi.getXY(0);
		Real2 xy0j = bondj.getXY(index0);
		Real2 xymean = xy0i.getMidPoint(xy0j);
		bondi.setXY(xymean, 0);
		Real2 xy1i = bondi.getXY(1);
		Real2 xy1j = bondj.getXY(index1);
		bondi.setXY(xy1i.getMidPoint(xy1j), 1);
		return bondi;
	}

	private void removeBond(SVGBond bond) {
		if (bond != null) {
			// don't remove atoms!
			getBondList().remove(bond);
		}
	}

	/** finds most prominent edge.
	 * for finading major partner in double bond
	 * if edge has no branches choose other edge
	 * if neither has branches (ethene) return null
	 * if both have branches return null
	 * 
	 * @param bondi
	 * @param bondj
	 * @return null if equal
	 */
	private SVGBond getPrimaryBond(SVGBond bondi, SVGBond bondj) {
		int branchEdgeCounti = bondi.getBranchEdgeCount();
		int branchEdgeCountj = bondj.getBranchEdgeCount();
		if (branchEdgeCounti == 0) {
			return branchEdgeCountj == 0 ? null : bondj;
		} else if (branchEdgeCountj == 0) {
			return bondi;
		}
		// separated double bond, each with branch/es
		return null;
	}

	private void debugGraph() {
		SVGG g = new SVGG();
		g.appendChild(debugAtoms());
		g.appendChild(debugBonds());
		SVGSVG.wrapAndWriteAsSVG(g, new File(new File("target/debug"), "graph.svg"));
	}

	private SVGG debugBonds() {
		SVGG g = new SVGG();
		for (int j = 0; j < getBondList().size(); j++) {
			SVGElement line = getBondList().get(j);
			g.appendChild(line.copy());
		}
		return g;
	}


	private SVGG debugAtoms() {
		SVGG g = new SVGG();
		for (int i = 0; i < getAtomList().size(); i++) {
			SVGAtom atom = getAtomList().get(i);
			Real2 xy = atom.getXY();
			SVGCircle circle = new SVGCircle(xy, 2.0);
			circle.setCSSStyle("fill:none;stroke:red;stroke-width:0.3;");
			SVGText t = new SVGText(xy, ""+i);
			t.setCSSStyle("fill:blue;font-size:2;");
			g.appendChild(t);
			g.appendChild(circle);
		}
		return g;
	}

	private List<SVGBond> createBondList(SVGLineList lineList) {
		bondList = new ArrayList<SVGBond>();
		for (int i = 0; i < lineList.size(); i++) {
			SVGElement line = lineList.get(i);
			SVGBond bond = this.createBond(line);
			addBondAndCreateId(bond);
		}
		return bondList;
	}

	private SVGBond createBond(SVGElement line) {
		SVGBond bond = new SVGBond(line);
		bond.setMoleculeBuilder(this);
		return bond;
	}

	private void addBondAndCreateId(SVGBond bond) {
		bond.setId(BOND_ID + bondList.size());
		bondList.add(bond);
		getOrCreateBondMap().put(bond.getId(), bond);
	}

	void addNodeAndCreateId(SVGAtom atom) {
		getOrCreateAtomList();
		atom.setId(NODE_ID + getAtomList().size());
		getAtomList().add(atom);
	}

	public List<SVGAtom> getOrCreateAtomList() {
		if (atomList == null) {
			atomList = new ArrayList<SVGAtom>();
		}
		return atomList;
	}

	public List<SVGBond> getOrCreateBondList() {
		if (bondList == null) {
			bondList = new ArrayList<SVGBond>();
		}
		return bondList;
	}

	private void addNonCarbonAtoms(List<SVGText> textList) {
		getOrCreateAtomList();
		nonCarbonAtoms = new HashSet<SVGAtom>();
		for (int idx = 0; idx < textList.size(); idx++) {
			SVGAtom atom = this.createAtom(textList.get(idx));
			atom.setId(NODE_ID+idx);
			addNodeAndCreateId(atom);
			nonCarbonAtoms.add(atom);
		}
	}

	private SVGAtom createAtom(SVGText svgText) {
		SVGAtom atom = new SVGAtom(this, svgText);
		return atom;
	}

	private void addLinesAndJoin() {
		for (int idx = 0; idx < getBondList().size(); idx++) {
			SVGBond bond = getBondList().get(idx);
			joinOrCreateAtomsAtEnd(bond, 0);
			joinOrCreateAtomsAtEnd(bond, 1);
		}
	}
	
	private void joinOrCreateAtomsAtEnd(SVGBond bond, int iend) {
		Real2 xyEnd = bond.getXY(iend);
		SVGAtom foundAtom = joinToExistingAtom(xyEnd);
		if (foundAtom == null) {
			// make new carbon
			SVGText text = SVGText.createDefaultText(xyEnd, "C");
			foundAtom = this.createAtom(text);
			getAtomList().add(foundAtom);
			foundAtom.setId("n"+getAtomList().size());
		}
		bond.addNode(foundAtom, iend);
	}

	private SVGAtom joinToExistingAtom(Real2 xyEnd) {
		SVGAtom foundAtom = null;
		for (int i = 0; i < getAtomList().size(); i++) {
			SVGAtom atom = getAtomList().get(i);
			if (atom.getXY().getDistance(xyEnd) < endDelta) {
				foundAtom = atom;
				break;
			}
		}
		return foundAtom;
	}

	public SVGElement getOrCreateSVG() {
		SVGG g = new SVGG();
		for (SVGAtom atom : getAtomList()) {
			g.appendChild(atom.getOrCreateSVG());
		}
		for (SVGBond bond : getBondList()) {
			g.appendChild(bond.getOrCreateSVG());
		}
		return g;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("================>>>\n");
		getOrCreateAtomList();
		for (SVGAtom atom : getAtomList()) {
			sb.append(atom.toString());
			sb.append("\n");
		}
		getOrCreateBondList();
		for (SVGBond bond : getBondList()) {
			sb.append(bond.toString());
			sb.append("\n");
		}
		sb.append("<<<================\n");
		return sb.toString();
	}
	
	public List<SVGAtom> getAtomList() {
		return atomList;
	}

	public List<SVGBond> getBondList() {
		return bondList;
	}

	public Element createCML() {
		Element cmlCml = new Element(CML, CML_NS);
		Element cmlMolecule = new Element(MOLECULE, CML_NS);
		cmlCml.appendChild(cmlMolecule);
		Element cmlAtomArray = new Element(ATOM_ARRAY, CML_NS);
		cmlMolecule.appendChild(cmlAtomArray);
		for (SVGAtom atom : atomList) {
			Element cmlAtom = new Element(ATOM, CML_NS);
			cmlAtom.addAttribute(new Attribute(ID, atom.getId()));
			String atomType = atom.getLabel();
			cmlAtom.addAttribute(new Attribute(ELEMENT_TYPE, atomType));
			Real2 xy = atom.getXY().format(3);
			cmlAtom.addAttribute(new Attribute(X2, ""+xy.getX()));
			cmlAtom.addAttribute(new Attribute(Y2, ""+xy.getY()));
			cmlAtomArray.appendChild(cmlAtom);
		}
		Element cmlBondArray = new Element(BOND_ARRAY, CML_NS);
		cmlMolecule.appendChild(cmlBondArray);
		for (SVGBond bond : bondList) {
			Element cmlBond = new Element(BOND, CML_NS);
			cmlBond.addAttribute(new Attribute(ID, bond.getId()));
			String atomRefs2 = bond.getAtomList().get(0).getId()+" "+bond.getAtomList().get(1).getId();
			cmlBond.addAttribute(new Attribute(ATOM_REFS2, atomRefs2));
			int order = (int)bond.getWeight();
			String orderS = order == 1 ? "S" : "D";
			cmlBond.addAttribute(new Attribute(ORDER, orderS));
			cmlBondArray.appendChild(cmlBond);
		}
		return cmlCml;
	}
	
	/**
 benzene
 ACD/Labs0812062058
 
  6  6  0  0  0  0  0  0  0  0  1 V2000
    1.9050   -0.7932    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0
    1.9050   -2.1232    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0
    0.7531   -0.1282    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0
    0.7531   -2.7882    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0
   -0.3987   -0.7932    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0
   -0.3987   -2.1232    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0
  2  1  1  0  0  0  0
  3  1  2  0  0  0  0
  4  2  2  0  0  0  0
  5  3  1  0  0  0  0
  6  4  1  0  0  0  0
  6  5  2  0  0  0  0
 M  END
 $$$$

Lines 	Section 	Description
1-3 	Header 	
1 		Molecule name ("benzene")
2 		User/Program/Date/etc information
3 		Comment (blank)
4-17 	Connection table (Ctab) 	
4 		Counts line: 6 atoms, 6 bonds, ..., V2000 standard
5-10 		Atom block (1 line for each atom): x, y, z (in angstroms), element, etc.
11-16 		Bond block (1 line for each bond): 1st atom, 2nd atom, type, etc.
17 		Properties block (empty)
18 	$$$$
	 */
	public String createMolFileContent() {
		StringBuilder sb = new StringBuilder();
		title = "title";
		program = "AMI";
		String comment = "";
		sb.append(title+"\n");
		sb.append(program+"\n");
		sb.append(comment+"\n");
		String ctHeader = format("   "+atomList.size(), 3) + format("   "+bondList.size(), 3);
		sb.append(ctHeader+"\n");
		String atomTrail =   "  0  0  0  0  0  0  0  0  0  0  0  0";
		String bondTrail =   "  0  0  0  0";
		Map<String, Integer> serialByIdMap = new HashMap<String, Integer>();
		Integer serial = 1;
		for (SVGAtom atom : atomList) {
			Real2 xy = atom.getXY().format(3);
			String s = format(""+xy.getX(), 10) + format(""+xy.getY(), 10)+"   0.00000";
			sb.append(s);
			String label = atom.getLabel();
			sb.append(" "+label+(label.length() == 1 ? " " : ""));
			sb.append(atomTrail);
			sb.append("\n");
			serialByIdMap.put(atom.getId(), serial);
			serial++;
		}
		for (SVGBond bond : bondList) {
			Integer a0 = serialByIdMap.get(bond.getAtomList().get(0).getId());
			sb.append(format(""+a0, 3));
			Integer a1 = serialByIdMap.get(bond.getAtomList().get(1).getId());
			sb.append(format(""+a1, 3));
			int order = (int)bond.getWeight();
			sb.append(format(""+order, 3));
			sb.append(bondTrail);
			sb.append("\n");
		}
		sb.append(" M  END"+"\n");
		
		return sb.toString();
	}

	private String format(String string, int ndec) {
		string = "              "+string;
		int l = string.length();
		return string.substring(l-ndec);
	}

	/** convenience method for testing.
	 * 
	 * @param inputDirRoot
	 * @param fileroot
	 * @param dirRoot
	 * @throws IOException
	 */
	public void createTestMoleculeAndDefaultOutput(File inputDirRoot, String fileroot, String dirRoot) throws IOException {
		setOutputDir(new File("target/", dirRoot));
		setInputDir(new File(inputDirRoot, dirRoot));
		setInputFile(new File(inputDir, fileroot + ".svg"));
		AbstractCMElement svgElement = SVGElement.readAndCreateSVG(inputFile);
		createWeightedLabelledGraph(svgElement);
		outputFiles(fileroot);
	}

	/** convenience method for testing.
	 * 
	 * @param inputSvgElement
	 * @param fileroot
	 * @param dirRoot
	 * @throws IOException
	 */
	public void createTestMoleculeAndDefaultOutput(AbstractCMElement inputSvgElement, String fileroot, String dirRoot) throws IOException {
		setOutputDir(new File("target/", dirRoot));
		createWeightedLabelledGraph(inputSvgElement);
		outputFiles(fileroot);
	}

	void outputFiles(String fileroot) throws IOException {
		if (fileroot != null) {
			SVGElement svgx = getOrCreateSVG();
			SVGSVG.wrapAndWriteAsSVG(svgx, new File(outputDir, fileroot+".svg"));
			Element cmlElement = createCML();
			File cmlFile = new File(outputDir, fileroot+".cml");
			XMLUtil.debug(cmlElement, cmlFile, 1);
			String mol = createMolFileContent();
			FileUtils.write(new File(outputDir, fileroot+".mol"), mol, "UTF-8");
		}
	}

	public void setInputFile(File inputFile) {
		this.inputFile = inputFile;
	}

	public void setInputDir(File inputDir) {
		this.inputDir = inputDir;
	}

	public void setOutputDir(File outputDir) {
		this.outputDir = outputDir;
	}

	public double getMidPointDelta() {
		return midPointDelta;
	}

	public void setMidPointDelta(double midPointDelta) {
		this.midPointDelta = midPointDelta;
	}

	public double getEndDelta() {
		return endDelta;
	}

	public void setEndDelta(double endDelta) {
		this.endDelta = endDelta;
	}

	public Angle getParallelEps() {
		return parallelEps;
	}

	public void setParallelEps(Angle parallelEps) {
		this.parallelEps = parallelEps;
	}


}
