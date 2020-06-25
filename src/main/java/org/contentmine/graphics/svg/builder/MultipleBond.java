package org.contentmine.graphics.svg.builder;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.svg.SVGElement;

/** holds multiple bond partners until we can sort them out later.
 * 
 * @author pm286
 *
 */
public class MultipleBond {
	private static final Logger LOG = LogManager.getLogger(MultipleBond.class);
	
private SVGElement linei;
	private SVGElement linej;

	public MultipleBond(SVGElement linei, SVGElement linej) {
		this.linei = linei;
		this.linej = linej;
	}


}
