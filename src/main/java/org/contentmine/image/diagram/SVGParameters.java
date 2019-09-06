package org.contentmine.image.diagram;

import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.EuclidConstants;

public class SVGParameters {

	private final static Logger LOG = Logger.getLogger(SVGParameters.class);
	
	private static final String EDGES = "edges";
	private static final String PIXELS = "pixels";
	private static final String NODES = "nodes";

	private static final String FILE = "file";

	/** CSS-like parameters */
	private static final String FILL = "fill";
	private static final String RADIUS = "radius";
	private static final String STROKE = "stroke";
	private static final String WIDTH = "width";
	
	private String filename;

	private Double edgeWidth;
	private String edgeStroke;
	
	private Double nodeRadius;
	private String nodeFill;
	
	private String pixelFill = "cyan";
	
	private boolean drawEdges;
	private boolean drawPixels = true;
	private boolean drawNodes;

	private void setFilename(String value) {
		this.filename = value;
	}

	private void setEdgeStroke(String value) {
		this.edgeStroke = value;
	}

	private void setEdgeWidth(String value) {
		edgeWidth = getDouble(value);
	}

	private void setNodeRadius(String value) {
		this.nodeRadius = getDouble(value);
	}

	private void setNodeFill(String value) {
		this.nodeFill = value;
	}

	private void setPixelFill(String value) {
		this.pixelFill = value;
	}

	
	public Double getEdgeWidth() {
		return edgeWidth;
	}

	public void setEdgeWidth(Double edgeWidth) {
		this.edgeWidth = edgeWidth;
	}

	public Double getNodeRadius() {
		return nodeRadius;
	}

	public void setNodeRadius(Double nodeRadius) {
		this.nodeRadius = nodeRadius;
	}

	public boolean isDrawEdges() {
		return drawEdges;
	}

	public void setDrawEdges(boolean drawEdges) {
		this.drawEdges = drawEdges;
	}

	public boolean isDrawPixels() {
		return drawPixels;
	}

	public void setDrawPixels(boolean drawPixels) {
		this.drawPixels = drawPixels;
	}

	public boolean isDrawNodes() {
		return drawNodes;
	}

	public void setDrawNodes(boolean drawNodes) {
		this.drawNodes = drawNodes;
	}

	public String getFilename() {
		return filename;
	}

	public String getEdgeStroke() {
		return edgeStroke;
	}

	public String getNodeFill() {
		return nodeFill;
	}

	public String getPixelFill() {
		return pixelFill;
	}

	private Double getDouble(String value) {
		try {
			Double d = new Double(value);
			return d;
		} catch (NumberFormatException e) {
			throw new RuntimeException(e);
		}
	}

	public void parseArgs(String arg, List<String> values) {
		if (!arg.startsWith(DiagramAnalyzer.SVG1)) {
			throw new RuntimeException("SVG args must start with: "+DiagramAnalyzer.SVG1);
		}
		String arg0 = arg.substring(DiagramAnalyzer.SVG1.length());
		if (arg0.equals(FILE)) {
			parseFilename(values);
		} else if (arg0.equals(EDGES)) {
			parseEdges(values);
		} else if (arg0.equals(PIXELS)) {
			parsePixels(values);
		} else if (arg0.equals(NODES)) {
			parseNodes(values);
		} else {
			throw new RuntimeException("unknown SVG arg: "+arg);
		}
	}

	private void parseEdges(List<String> values) {
		for (String value : values) {
			String[] nv = getNameValue(value, EDGES);
			if (nv[0].equals(STROKE)) {
				setEdgeStroke(nv[1]);
			} else if (nv[0].equals(WIDTH)) {
				setEdgeWidth(nv[1]);
			} else {
				throw new RuntimeException("Cannot parse EDGE value: "+value);
			}
		}
		drawEdges = true;
	}

	private void parsePixels(List<String> values) {
		for (String value : values) {
			String[] nv = getNameValue(value, PIXELS);
			if (nv[0].equals(FILL)) {
				setPixelFill(nv[1]);
			} else {
				throw new RuntimeException("Cannot parse PIXEL value: "+value);
			}
		}
		drawPixels = true;
	}
	
	private void parseNodes(List<String> values) {
		for (String value : values) {
			String[] nv = getNameValue(value, NODES);
			if (nv[0].equals(FILL)) {
				setNodeFill(nv[1]);
			} else if (nv[0].equals(RADIUS)) {
				setNodeRadius(nv[1]);
			} else {
				throw new RuntimeException("Cannot parse NODE value: "+value);
			}
		}
		drawNodes = true;
	}

	private void parseFilename(List<String> values) {
		if (values.size() != 1) {
			throw new RuntimeException("svg file needs 1 argument");
		}
		this.setFilename(values.get(0));
	}


	/** spits colon-separated string into exactly 2 strings 
	 * 
	 * @param value
	 * @param svgArg type of argument (for error message)
	 * @return
	 */
	private String[] getNameValue(String value, String svgArg) {
		String[] nameValue = value.split(EuclidConstants.S_COLON);
		if (nameValue.length != 2) {
			throw new RuntimeException("cannot parse as "+svgArg+" arg: "+value);
		}
		return nameValue;
	}

}
