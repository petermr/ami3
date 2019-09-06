package org.contentmine.image.pixel;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/** a collection of PixelIslands and/or PixelArchipelagos.
 * 
 * 
 * @author pm286
 *
 */
public class PixelArchipelago {

	private final static Logger LOG = Logger.getLogger(PixelArchipelago.class);
	
	private PixelIslandList islandList;
	private List<PixelArchipelago> archipelagoList;
	
	public PixelArchipelago() {
		ensureIslandList();
		ensureArchipelagoList();
	}
	
	/** not yet finished */
	public static PixelArchipelago createArchipelago(PixelIslandList islandList) {
		PixelArchipelago archipelago = null;
		if (islandList != null) {
			archipelago = new PixelArchipelago();
			archipelago.islandList = islandList;
		}
		throw new RuntimeException("NYI");

//		return archipelago;
	}

	private void ensureArchipelagoList() {
		if (archipelagoList == null) {
			archipelagoList = new ArrayList<PixelArchipelago>();
		}
	}

	private void ensureIslandList() {
		if (islandList == null) {
			islandList = new PixelIslandList();
		}
	}
	
	public void addIsland(PixelIsland island) {
		ensureIslandList();
		islandList.add(island);
	}
	
	public void addArchipelago(PixelArchipelago archipelago) {
		ensureArchipelagoList();
		archipelagoList.add(archipelago);
	}
	
	/** clusters islands into archipelagos and archipelagos into larger ones.
	 * 
	 * NYI
	 * @param extension distance to next possible neighbour of same class.
	 * @return
	 */
	public static PixelArchipelago createArchipelago(PixelIslandList islandList, Double extension) {
		PixelArchipelago archipelago = new PixelArchipelago();
		throw new RuntimeException("NYI");
//		return archipelago;
	}
}
