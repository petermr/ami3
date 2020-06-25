package org.contentmine.ami.diagramAnalyzer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.image.ArgIterator;
import org.contentmine.image.ImageProcessor;
import org.contentmine.image.diagram.DiagramAnalyzer;
import org.contentmine.image.pixel.PixelComparator.ComparatorType;
import org.contentmine.image.pixel.PixelGraph;
import org.contentmine.image.pixel.PixelIsland;
import org.contentmine.image.pixel.PixelIslandList;
import org.contentmine.image.pixel.PixelNode;

/**
 * phylogenetic tree from edges and nodes.
 * 
 * @author pm286
 * 
 */
public class PhyloTreePixelAnalyzer extends DiagramAnalyzer {

	private final static Logger LOG = LogManager.getLogger(PhyloTreePixelAnalyzer.class);

	public final static String LENGTH1 = "--lengths";
	public final static String NEWICK1 = "--newick";
	public final static String ROOT1 = "--root";

	private File newickFile;
	private Set<PixelNode> usedPixelNodes;
	private String newickFileString;
	private String newickString;
	public ComparatorType rootPositionComparatorType;
	private boolean computeLengths;
	private PixelGraph selectedGraph;
	private int selectedIslandIndex;
	private DiagramTree diagramTree;

	public PhyloTreePixelAnalyzer() {
		setDefaults();
		clearVariables();
	}

	@Override
	public void setDefaults() {
		super.setDefaults();
		newickFileString = null;
		rootPositionComparatorType = ComparatorType.LEFT;
		computeLengths = false;
	}

	@Override
	public void clearVariables() {
		super.clearVariables();

		newickFile = null;
		usedPixelNodes = null;
		newickString = null;
		pixelGraphList = null;

	}

	public void writeNewickQuietly() {
		if (newickString == null) {
			LOG.debug("Null Newick string");
		} else if (newickFile == null) {
			LOG.debug(newickString);
		} else {
			try {
				FileUtils.writeStringToFile(newickFile, newickString);
				LOG.trace("wrote Newick String to "
						+ newickFile.getAbsolutePath());
			} catch (Exception e) {
				throw new RuntimeException(
						"Cannot write Newick: " + newickFile, e);
			}
		}
	}

	protected void usage() {
		System.err.println("Usage: phylotree [args]");
		super.usage();
		System.err.println("       " + /* ROOT + " " + */ROOT1
				+ "       root position (left right top bottom)");
		System.err.println("       " + /* NEWICK + " " + */NEWICK1
				+ "       newickfile (outputs newick format; optional)");
	}

	public static void main(String[] args) /*throws Exception*/ {
		LOG.trace("RUNNING MAIN PHYLOTREE");
		DiagramAnalyzer phyloTree = new PhyloTreePixelAnalyzer();
		phyloTree.parseArgsAndRun(args);
	}

	protected boolean parseArgAndAdvance(ArgIterator argIterator) {
		boolean found = true;
		ensureImageProcessor();
		String arg = argIterator.getCurrent();
		if (debug) {
			LOG.debug(arg);
		}
		if (arg.equals(ImageProcessor.DEBUG)
				|| arg.equals(ImageProcessor.DEBUG1)) {
			debug = true;
			argIterator.setDebug(true);
			argIterator.next();
		} else if (arg.equals(PhyloTreePixelAnalyzer.NEWICK1)) {
			String value = argIterator.getSingleValue();
			if (value != null) {
				setNewickFileString(value);
			}
		} else if (arg.equals(PhyloTreePixelAnalyzer.LENGTH1)) {
			argIterator.getValues();
			setComputeLengths(true);
		} else if (arg.equals(PhyloTreePixelAnalyzer.ROOT1)) {
			String value = argIterator.getSingleValue();
			if (value != null) {
				setRoot(value.toUpperCase());
			}
		} else {
			found = imageProcessor.parseArgAndAdvance(argIterator);
			if (!found) {
				LOG.debug("skipped unknown token: " + argIterator.getCurrent());
				argIterator.next();
			}
		}
		return found;
	}

	public void setComputeLengths(boolean b) {
		this.computeLengths = b;
	}

	private void setRoot(String root) {
		ComparatorType[] types = { ComparatorType.BOTTOM, ComparatorType.LEFT,
				ComparatorType.TOP, ComparatorType.RIGHT, };
		this.rootPositionComparatorType = null;
		for (ComparatorType type : types) {
			if (type.toString().equalsIgnoreCase(root)) {
				this.rootPositionComparatorType = type;
				break;
			}
		}
		if (this.rootPositionComparatorType == null) {
			throw new RuntimeException("Unknown root orientation: " + root);
		}
	}

	public void debug() {
		if (newickFile != null) {
			LOG.debug("newick output: " + newickFile);
		}
	}

	protected boolean runCommands() {
		LOG.trace("RUNNING COMMANDS");
		processImageIntoGraphsAndTree();
		return true;
	}

	public DiagramTree processImageIntoGraphsAndTree() {
		diagramTree = null;
		LOG.trace("start processImage");
		super.processImageFile();
		LOG.trace("end processImage");

		// FIXME shouldn't have to process all islands to graphs
		this.processGraphList();
		LOG.trace("end processGraph");
		int islandIndex = ensurePixelProcessor().getSelectedIslandIndex();
		islandIndex = (islandIndex == -1) ? this.selectedIslandIndex
				: islandIndex;
		if (islandIndex >= 0) {
			LOG.trace("Making NEWICK");
			selectedGraph = pixelGraphList.get(islandIndex);
//			selectedGraph.debug();
			diagramTree = createFromGraph(selectedGraph);
			if (diagramTree != null) {
				LOG.trace("Tree: "+diagramTree);
				if (computeLengths) {
					diagramTree.setOutputDistances(rootPositionComparatorType);
				}
			}
		}
		return diagramTree;
	}
	
	private void processGraphList() {
		throw new RuntimeException("BUG use DiagramAnalyzerOLD");
	}

	public DiagramTree getDiagramTree() {
		return diagramTree;
	}

	public void reportFailure(RuntimeException e) {
		LOG.error("FAIL " + e);
		if (e.toString().contains("Cannot find extreme edge for")) {
			LOG.error("Exception: " + e.toString());
//			if (diagramLog != null) {
//				diagramLog.appendChild(new LogMessage(e, fileroot));
//			}
		} else if (e.toString().contains("Should have exactly 2 neighbours")) {
			LOG.error("Exception: " + e.toString());
//			if (diagramLog != null) {
//				diagramLog.appendChild(new LogMessage(e, fileroot));
//			}
		} else {
			throw e;
			// LOG.debug("Exception "+e);
		}
	}

	public void setNewickFileString(String fileString) {
		this.newickFileString = fileString;
	}

	public String getNewickFileString() {
		return newickFileString;
	}

	public void setNewickFile(File file) {
		if (file == null) {
			throw new RuntimeException("newick file is null");
		}
		this.newickFile = file;
		this.newickFile.getParentFile().mkdirs();
	}
	
	public String getNewickString() {
		return newickString;
	}
	public PixelGraph getOrCreateSelectedGraph() {
		return selectedGraph;
	}

	public DiagramTree createTreeFromImageFile(File file) {
		// tidy this
		BufferedImage image = imageProcessor.processImageFile(file);
		imageProcessor.processImage(image);
		PixelIslandList pixelIslandList = imageProcessor
				.getOrCreatePixelIslandList();
		PixelIsland treeIsland = pixelIslandList.get(0);
		diagramTree = createFromIsland(treeIsland);
		return diagramTree;
	}

	private DiagramTree createFromIsland(PixelIsland treeIsland) {
		PixelGraph graph = treeIsland.getOrCreateGraph();
		return createFromGraph(graph);
	}
	
	public DiagramTree createFromGraph(PixelGraph graph) {
		return createFromGraph(graph, null);
	}

	public DiagramTree createFromGraph(PixelGraph graph, PixelNode rootPixelNode) {
		graph.addCoordsToNodes();
		TreeBuilder treeBuilder = new TreeBuilder(graph, rootPositionComparatorType);
		treeBuilder.setRootPixelNode(rootPixelNode);
		DiagramTree tree = null;
		try {
			tree = treeBuilder.createFromGraph();
		} catch (RuntimeException e) {
			LOG.debug("FATAL ERROR: "+e);
			return null;
		}
		LOG.trace("root "+tree.getRootXMLNode()+" edge "+graph.getOrCreateEdgeList().size()+" node "+graph.getOrCreateNodeList().size());
		return tree;
	}

	public void setSelectedIslandIndex(int islandIndex) {
		this.selectedIslandIndex = islandIndex;
	}

	public DiagramTree makeDiagramTreeFromImage(BufferedImage image) {
		//SVGXTree tree = new SVGXTree((SVGElement)null);
		PhyloTreePixelAnalyzer treeAnalyzer = new PhyloTreePixelAnalyzer();
		treeAnalyzer.setSelectedIslandIndex(0);
		treeAnalyzer.setComputeLengths(true);
		//treeAnalyzer.setNewickFile(outputFile);
		treeAnalyzer.setImage(image);
		diagramTree = treeAnalyzer.processImageIntoGraphsAndTree();
		return diagramTree;
	}


}
