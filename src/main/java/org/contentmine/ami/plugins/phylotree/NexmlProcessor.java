package org.contentmine.ami.plugins.phylotree;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.contentmine.ami.diagramAnalyzer.DiagramTree;
import org.contentmine.ami.diagramAnalyzer.PhyloTreePixelAnalyzer;
import org.contentmine.ami.lookups.TaxdumpLookup;
import org.contentmine.ami.plugins.phylotree.PhyloTreeArgProcessor.Message;
import org.contentmine.ami.plugins.phylotree.nexml.NexmlEditor;
import org.contentmine.ami.plugins.phylotree.nexml.NexmlFactory;
import org.contentmine.ami.plugins.phylotree.nexml.NexmlNEXML;
import org.contentmine.ami.plugins.phylotree.nexml.NexmlNode;
import org.contentmine.ami.plugins.phylotree.nexml.NexmlOtu;
import org.contentmine.ami.plugins.phylotree.nexml.NexmlOtus;
import org.contentmine.ami.plugins.phylotree.nexml.NexmlTree;
import org.contentmine.cproject.args.log.AbstractLogElement.LogLevel;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.Real2Range.BoxDirection;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlSpan;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.text.SVGPhrase;
import org.contentmine.image.ImageUtil;
import org.contentmine.image.pixel.PixelGraph;
import org.contentmine.image.pixel.PixelNode;
import org.contentmine.norma.NAConstants;
import org.contentmine.norma.editor.EditList;
import org.contentmine.norma.editor.Extraction;
import org.contentmine.norma.editor.SubstitutionEditor;
import org.contentmine.norma.image.ocr.HOCRReaderOLD;

import nu.xom.Attribute;

public class NexmlProcessor {

	private String newickFilename;
	private NexmlNEXML nexml;
	private String nexmlFilename;
	private NexmlOtus nexmlOtus;
	private DiagramTree diagramTree;
	private PhyloTreeArgProcessor argProcessor;
	private PhyloTreePixelAnalyzer phyloTreePixelAnalyzer;
	public SubstitutionEditor substitutionEditor;
	public TaxdumpLookup taxdumpLookup;
	private boolean pruneBadTips = true;
	private NexmlTree singleTree;
	private List<NexmlNode> tipNodeList;
	private Pattern speciesPattern;
	//	private Element speciesPatternXML;
	private InputStream speciesPatternInputStream;
	private String speciesPatternString;
	private int maxPhraseLength = 4;
	private static final String PHYLOTREE_RESOURCE = NAConstants.PLUGINS_RESOURCE+"/phylotree/";
	
	public NexmlProcessor(PhyloTreeArgProcessor argProcessor) {
		this.argProcessor = argProcessor;
	}
	
	public NexmlNEXML createNexmlAndTreeFromPixels(File inputImageFile) {
		int largestSmallEdgeAllaowed = 5;
		if (inputImageFile != null && inputImageFile.exists()) {
			BufferedImage image = ImageUtil.readImage(inputImageFile);
			phyloTreePixelAnalyzer = argProcessor.getPhyloCore().createAndConfigurePixelAnalyzer(image);
			diagramTree = phyloTreePixelAnalyzer.processImageIntoGraphsAndTree();
			if (diagramTree == null) {
				return null;
			}
			PixelNode rootPixelNode = diagramTree.getRootPixelNode();
			PixelGraph graph = diagramTree.getGraph();
			// use root node later...
			graph.tidyNodesAndEdges(largestSmallEdgeAllaowed);
			diagramTree = new PhyloTreePixelAnalyzer().createFromGraph(graph, rootPixelNode);
			NexmlFactory nexmlFactory = new NexmlFactory(argProcessor);
			nexmlFactory.setRootPixelNode(rootPixelNode);
			nexmlFactory.createAndAddNexmlTree(diagramTree);
			nexml = nexmlFactory.getOrCreateNexmlNEXML();
		}
		return nexml;
	}
	public String getGenus(NexmlOtu otu) {
		return otu.getAttributeValue("genus", PhyloConstants.CM_PHYLO_NS);
	}
	public NexmlNEXML getNexml() {
		return nexml;
	}
	private boolean isBadOtu(NexmlOtu otu) {
		return otu.getGenus() == null;
	}
	void processNexml() throws IOException, FileNotFoundException {
		argProcessor.LOG.trace("processing Nexml");
		if (nexml == null) {
			argProcessor.TREE_LOG().warn("null nexml");
			return;
		}
		NexmlEditor nexmlEditor = new NexmlEditor(nexml);
		argProcessor.LOG.trace("nodesWithChildren: "+nexmlEditor.getNodesWithChildren());
		argProcessor.LOG.trace("nodesWithParents: "+nexmlEditor.getNodesWithParents());
		ensureSubstitutionEditor();
		InputStream speciesPatternInputStream = getOrCreateSpeciesPatternInputStream();
		if (speciesPatternInputStream == null) {
			argProcessor.LOG.warn("cannot create speciesPatternInputStream (?missing file)");
			return;
		}
		substitutionEditor.addEditor(speciesPatternInputStream);
		nexmlOtus = nexml.getSingleOtusElement();
		singleTree = nexml.getTreesElement().get(0);
		tipNodeList = singleTree.getOrCreateTipNodeList();
		List<NexmlOtu> otuList = nexmlOtus.getNexmlOtuList();
		nexml.getSingleOtusElement().addNamespaceDeclaration(PhyloConstants.CM_PHYLO_PREFIX, PhyloConstants.CM_PHYLO_NS);
		for (NexmlOtu otu : otuList) {
			processOtu(otu);
		}
		if (pruneBadTips) {
			pruneBadTips();
		}
		argProcessor.LOG.trace(nexml.toXML());
		String newick = nexml.createNewick();
		argProcessor.LOG.trace("nwk "+newick);
		
		String filename = (argProcessor.getInputList().size() == 0) ? null : argProcessor.getInputList().get(0);
		argProcessor.LOG.trace("dir "+filename);
		if (filename != null) {
			File outputDir = new File("target/phylo", filename+"/");
			outputDir.mkdirs();
			XMLUtil.debug(nexml, new FileOutputStream(new File(outputDir, "edited.nexml.xml")), 1);
			FileUtils.write(new File(outputDir, "edited.nwk"), nexml.createNewick());
		}
	}
	public void processOtu(NexmlOtu nexmlOtu) {
		LogLevel currentLevel = argProcessor.TREE_LOG().getCurrentLevel();
		argProcessor.TREE_LOG().setLevel(LogLevel.INFO);
		ensureTaxdumpLookup();
		ensureSubstitutionEditor();
		String value = nexmlOtu.getValue();
		String editedValue = substitutionEditor.createEditedValueAndRecord(value);
		List<Extraction> extractionList = substitutionEditor.getExtractionList();
		nexmlOtu.annotateOtuWithEditRecord(substitutionEditor.getEditRecord());
		annotateOtuWithExtractions(nexmlOtu, extractionList);
		argProcessor.LOG.trace(">otu>"+nexmlOtu.toXML());
		int maxDelta = 4;
		if (editedValue == null) {
			argProcessor.TREE_LOG().error(""+Message.ERR_BAD_SYNTAX+" ["+value+"]");
		} else {
			boolean validated = false;
			try {
				validated = substitutionEditor.validate(editedValue);
			} catch (Exception e) {
				argProcessor.TREE_LOG().error("failed to validate ["+value+"]");
			}
			if (validated) {
				EditList editRecord = substitutionEditor.getEditRecord();
				nexmlOtu.setEditRecord(editRecord.toString());
				argProcessor.LOG.trace("syntax OK: "+value+" => "+editedValue+((editRecord == null || editRecord.size() == 0) ? "" :"; "+editRecord));
				String genus = getGenus(nexmlOtu);
				String species = getSpecies(nexmlOtu);
				boolean changed = false;
				boolean matched = false;
				if (taxdumpLookup.isValidBinomial(genus, species)) {
					argProcessor.TREE_LOG().debug("Valid organism: "+genus+" "+species);
					matched = true;
				} else if (!taxdumpLookup.isValidGenus(genus)) {
					argProcessor.TREE_LOG().warn("invalid genus, looking for closest match: "+genus);
					List<String> closestGenusList = taxdumpLookup.getClosest(taxdumpLookup.getGenusSet(), genus, maxDelta);
					if (closestGenusList.size() > 0) {
						argProcessor.LOG.trace("Could this be :"+closestGenusList);
						if (closestGenusList.size() == 1) {
							genus = closestGenusList.get(0);
							changed = true;
						}
					}
				}
				if (!matched) {
					// optimize later 
					List<String> speciesList = taxdumpLookup.lookupSpeciesList(genus);
					List<String> bestSpecies = taxdumpLookup.getClosest(speciesList, species, maxDelta);
					if (bestSpecies.size() == 1) {
						species = bestSpecies.get(0);
						changed = true;
					}
				}
				argProcessor.TREE_LOG().debug("genus: "+genus+": "+taxdumpLookup.isValidGenus(genus));
				argProcessor.TREE_LOG().debug("binomial: "+genus+" "+species+": "+taxdumpLookup.isValidBinomial(genus, species));
				if (changed) {
					argProcessor.TREE_LOG().warn("corrected to: "+TaxdumpLookup.getBinomial(genus, species));
				}
			}
		}
		argProcessor.TREE_LOG().setLevel(currentLevel);
	}
	void setNewickFilename(String string) {
		this.newickFilename = string;
	}
	void setNexmlFilename(String f) {
		this.nexmlFilename = f;
	}

	void outputNewick(File phyloTreeDir) {
		File newickFile = new File(phyloTreeDir, getPhyloCore().getImageSerial()+".nwk");
		try {
			FileUtils.write(newickFile, nexml.createNewick());
			argProcessor.TREE_LOG().info("wrote Newick: "+newickFile);
		} catch (IOException e) {
			argProcessor.TREE_LOG().error("Cannot create newickFile: "+newickFile+": "+e);
		}
	}

	// =============================
	
	void outputNexml(File phyloTreeDir) {
		File nexmlFile = new File(phyloTreeDir, getPhyloCore().getImageSerial()+".nexml.xml");
		try {
			XMLUtil.debug(nexml, nexmlFile, 1);
			argProcessor.TREE_LOG().info("wrote NEXML: "+nexmlFile);
		} catch (IOException e) {
			argProcessor.TREE_LOG().error("Cannot create nexmlFile: "+nexmlFile+": "+ e);
		}
	}

	public void ensureSubstitutionEditor() {
		if (substitutionEditor == null) {
			substitutionEditor = new SubstitutionEditor();
		}
	}

	public TaxdumpLookup ensureTaxdumpLookup() {
		if (taxdumpLookup == null) {
			taxdumpLookup = new TaxdumpLookup();
		}
		return taxdumpLookup;
	}

	private List<NexmlNode> getBadNodes() {
		List<NexmlOtu> otuList = nexmlOtus.getNexmlOtuList();
		List<NexmlNode> badNodeList = new ArrayList<NexmlNode>();
		for (NexmlOtu otu : otuList) {
			if (isBadOtu(otu)) {
				argProcessor.LOG.trace("bad otu: "+otu);
				String otuId = otu.getId();
				for (NexmlNode node : tipNodeList) {
					if (node.getOtuRef().equals(otuId)) {
						argProcessor.LOG.trace("will delete: "+otuId);
						badNodeList.add(node);
						break;
					}
				}
			}
		}
		argProcessor.LOG.trace(badNodeList);
		return badNodeList;
	}


	public String getNexmlFilename() {
		return nexmlFilename;
	}

	private void pruneBadTips() {
		List<NexmlNode> badNodes = getBadNodes();
		argProcessor.LOG.trace("bad nodes "+badNodes.size());
		for (NexmlNode badNode : badNodes) {
			argProcessor.LOG.trace("try to delete "+badNode+"; "+badNode.getNexmlChildNodes());
			try {
				nexml.deleteTipAndElideIfParentHasSingletonChild(badNode);
				argProcessor.TREE_LOG().info("deleted node "+badNode);
			} catch (RuntimeException e) {
				argProcessor.TREE_LOG().error("cannot delete tip "+e);
			}
		}
	}

	public void setPruneBadTips(boolean pruneBadTips) {
		this.pruneBadTips = pruneBadTips;
	}

	public boolean isPruneBadTips() {
		return pruneBadTips;
	}

	public String getSpecies(NexmlOtu otu) {
		return otu.getAttributeValue("species", PhyloConstants.CM_PHYLO_NS);
	}

	public String getNewickFilename() {
		return newickFilename;
	}

	InputStream getOrCreateSpeciesPatternInputStream() {
		if (speciesPatternInputStream == null) {
			if (speciesPatternString != null) {
				speciesPatternInputStream = this.getClass().getResourceAsStream(PHYLOTREE_RESOURCE+speciesPatternString);
				if (speciesPatternInputStream == null) {
					argProcessor.TREE_LOG().warn("Cannot read/create speciesPatternInputStream: "+PHYLOTREE_RESOURCE+speciesPatternString);
				}
			} else {
				argProcessor.TREE_LOG().warn("should give speciesPatternString in arguments");
			}
		}
		return speciesPatternInputStream;
	}

	public void matchSpecies(HOCRReaderOLD hocrReader) {
		if (speciesPattern != null) {
			List<HtmlSpan> lines = hocrReader.getNonEmptyLines();
			for (HtmlSpan line : lines) {
				List<String> matchList = HOCRReaderOLD.matchPattern(line, speciesPattern);
				argProcessor.LOG.trace((matchList.size() == 0 ? "?? "+HOCRReaderOLD.getSpacedValue(line).toString() : matchList));
			}
		}
	}

	/** matches nodes against phrases.
		 * 
		 * Brute force algorithm - replace with Hungarian later
		 * 
		 * @param unmatchedNodeList decremented for each match
		 * @param unusedPhraseList decremented for each match
		 */
		void annotateMatchedNodesAndDecrementUnmatchedLists(
				List<NexmlNode> unmatchedNodeList, List<SVGPhrase> unusedPhraseList, Real2Range joiningBox, Double joiningRadius) {
			List<SVGPhrase> matchedPhraseList = new ArrayList<SVGPhrase>();
			List<NexmlNode> matchedNodeList = new ArrayList<NexmlNode>();
			for (NexmlNode unmatchedNode : unmatchedNodeList) {
				Real2 tipXY2 = unmatchedNode.getXY2();
				List<SVGPhrase> phrases = this.annotateNodesWithMatchedPhrases(unusedPhraseList, tipXY2, joiningBox, joiningRadius);
				if (phrases.size() == 1) {
					String label = phrases.get(0).toString();
					if (joiningRadius != null) {
						unmatchedNode.setLabel(label);
					} else {
						unmatchedNode.setOtuValue(label);
					}
					matchedPhraseList.add(phrases.get(0));
				} else if (phrases.size() > 1) {
					argProcessor.TREE_LOG().error("competing words for tip");
				} else if (phrases.size() == 0) {
					argProcessor.TREE_LOG().trace("failed to find phrases to match node:" +unmatchedNode.getLabelString()+"("+unmatchedNode.getXY2()+")");
	//				unmatchedTipList.add(unmatchedNode);
				}
			}
			unusedPhraseList.removeAll(matchedPhraseList);
			unmatchedNodeList.removeAll(matchedNodeList);
			
			if (unusedPhraseList.size() > 0) {
				argProcessor.TREE_LOG().trace("unmatched phrases: \n"+unusedPhraseList);
			}
			if (unmatchedNodeList.size() > 0) {
				argProcessor.LOG.trace("unmatched tips: \n"+unmatchedNodeList);
			}
		}

	/** finds lines within joining box of XY2.
	 * 
	 * Ideally wordLineList should be size==1
	 * 
	 * @param phraseList
	 * @param xy2
	 * @param joiningBox if not-null joins horizontally to node
	 * @param joiningRadius if not null joins radially (for short words)
	 * @return
	 */
	private List<SVGPhrase> annotateNodesWithMatchedPhrases(List<SVGPhrase> phraseList, Real2 xy2, Real2Range joiningBox, Double joiningRadius) {
		getPhyloCore().getOrCreateHOCRReader();
		List<SVGPhrase> matchedPhraseList = new ArrayList<SVGPhrase>();
		if (phraseList != null) {
			for (SVGPhrase phrase : phraseList) {
				Real2Range phraseBox = phrase == null ? null : phrase.getBoundingBox();
				if (phraseBox != null) {
					if (joiningBox != null) {
						Real2 phraseXY2 = phraseBox.getMidPoint(BoxDirection.LEFT);
						Real2 diffXY2 = phraseXY2.subtract(xy2); 
						if (joiningBox.includes(diffXY2)) {
							matchedPhraseList.add(phrase);
						}
					} else if (joiningRadius != null && phrase.toString().length() < maxPhraseLength) {
						double dist = xy2.getDistance(phrase.getBoundingBox().getCentroid());
						if (dist < 50) {
							argProcessor.LOG.trace(dist);
						}
						if (dist < joiningRadius) {
							matchedPhraseList.add(phrase);
						}
					}
				}
			}
		}
		return matchedPhraseList;
	}

	public void annotateOtuWithExtractions(NexmlOtu otu, List<Extraction> extractionList) {
		for (Extraction extraction : extractionList) {
			otu.addAttribute(new Attribute(PhyloConstants.CM_PHYLO_PREFIX+":"+extraction.getName(), PhyloConstants.CM_PHYLO_NS, extraction.getValue()));
		}
	}

	public void setMaxPhraseLength(int maxPhraseLength) {
		this.maxPhraseLength = maxPhraseLength;
	}

	public int getMaxPhraseLength() {
		return maxPhraseLength;
	}

	public void setSpeciesPattern(Pattern speciesPattern) {
		this.speciesPattern = speciesPattern;
	}

	public void setSpeciesPatternInputString(String patternString) {
		this.speciesPatternString = patternString;
	}

	public void setSpeciesPatternString(String string) {
		this.speciesPatternString = string;
		getOrCreateSpeciesPatternInputStream();
	
	}

	public Pattern getSpeciesPattern() {
		return speciesPattern;
	}

	/** does this do anything?
	 * 
	 * @param nexml
	 * @param speciesPattern
	 */
	public void checkOTUsAgainstSpeciesPattern(NexmlNEXML nexml, Pattern speciesPattern) {
		List<NexmlOtu> nexmlOtuList = nexml.getSingleOtusElement().getNexmlOtuList();
		argProcessor.LOG.trace("sp pattern: ["+speciesPattern+"]");
		for (NexmlOtu otu : nexmlOtuList) {
			String tipLabel = otu.getValue();
			Matcher matcher = speciesPattern.matcher(tipLabel);
			if (matcher.matches()) {
				argProcessor.LOG.trace(">"+matcher);
			} else {
				argProcessor.LOG.trace("failed match: "+tipLabel);
			}
		}
	}

	/** can check species from HOCR.
	 * 
	 * this may need to go elsewhere
	 * 
	 * @param svgSvg
	 * @throws Exception
	 */
	private void checkSpecies(SVGSVG svgSvg) throws Exception {
		if (svgSvg != null) {
			matchSpecies(argProcessor.getOrCreateHOCRReader());
			File resultsFile = getPhyloCore().createHocrSVGFileDescriptor();
			XMLUtil.debug(svgSvg, new FileOutputStream(resultsFile), 1);
		}
	}

	private PhyloCore getPhyloCore() {
		return argProcessor.getPhyloCore();
	}

}
