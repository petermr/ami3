package org.contentmine.graphics.svg.normalize;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGText;

/** superclass for SVG Decorators
 *
 * uses local names for attributes
 * @author pm286
 *
 */
public abstract class AbstractDecorator {
	static final Logger LOG = Logger.getLogger(AbstractDecorator.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	protected AttributeComparer attributeComparer;
	
	protected AbstractDecorator() {
		this.attributeComparer = new AttributeComparer();
	}

	public void setElement0(SVGText text0) {
		attributeComparer.setElement0(text0);
	}

	public void setElement1(SVGText text1) {
		attributeComparer.setElement0(text1);
	}

}
