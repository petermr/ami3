package org.contentmine.graphics.svg.normalize;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.svg.SVGText;

/** superclass for SVG Decorators
 *
 * uses local names for attributes
 * @author pm286
 *
 */
public abstract class AbstractDecorator {
	static final Logger LOG = LogManager.getLogger(AbstractDecorator.class);
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
