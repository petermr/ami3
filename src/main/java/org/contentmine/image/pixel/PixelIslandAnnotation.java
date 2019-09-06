package org.contentmine.image.pixel;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/** holds annotation data and methods for PixelIslands.
 * 
 * @author pm286
 *
 */
public class PixelIslandAnnotation {
	private static final Logger LOG = Logger.getLogger(PixelIslandAnnotation.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private String internalRingName;
	private boolean plotInternalRings;
	private String islandName;
	private boolean plotIsland;
	private String outlineName;
	private boolean plotOutline;
	private String ridgeName;
	private boolean plotRidge;
	private String thinnedName;
	private boolean plotThinned;
	private String plotColor;
	
	public PixelIslandAnnotation() {
		setDefaults();
	}
	
	private void setDefaults() {
		this.plotColor = null;
	}
	public boolean isPlotRidge() {
		return plotRidge;
	}
	public void setPlotRidge(boolean plotRidge) {
		this.plotRidge = plotRidge;
	}
	public boolean isPlotThinned() {
		return plotThinned;
	}
	public void setPlotThinned(boolean plotThinned) {
		this.plotThinned = plotThinned;
	}
	public String getInternalRingName() {
		return internalRingName;
	}
	public void setInternalRingName(String internalRingName) {
		this.internalRingName = internalRingName;
	}
	public boolean isPlotInternalRings() {
		return plotInternalRings;
	}
	public void setPlotInternalRings(boolean plotInternalRings) {
		this.plotInternalRings = plotInternalRings;
	}
	public String getIslandName() {
		return islandName;
	}
	public void setIslandName(String islandName) {
		this.islandName = islandName;
	}
	public boolean isPlotIsland() {
		return plotIsland;
	}
	public void setPlotIsland(boolean plotIsland) {
		this.plotIsland = plotIsland;
	}
	public String getOutlineName() {
		return outlineName;
	}
	public void setOutlineName(String outlineName) {
		this.outlineName = outlineName;
	}
	public boolean isPlotOutline() {
		return plotOutline;
	}
	public void setPlotOutline(boolean plotOutline) {
		this.plotOutline = plotOutline;
	}
	public String getRidgeName() {
		return ridgeName;
	}
	public void setRidgeName(String ridgeName) {
		this.ridgeName = ridgeName;
	}
	public String getThinnedName() {
		return thinnedName;
	}
	public void setThinnedName(String thinnedName) {
		this.thinnedName = thinnedName;
	}
	public void setPlotColor(String color) {
		this.plotColor = color;
	}

}
