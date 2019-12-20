package org.contentmine.norma.sections;

import java.util.Arrays;
import java.util.List;

import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlP;

import nu.xom.Element;

public class JATSFnElement extends JATSElement implements IsBlock {

	/**
		<journal-meta>
		...
		</journal-meta>
		<article-meta>
		</article-meta>
		
	 */
	static String TAG = "fn";

	public final static List<String> ALLOWED_CHILD_NAMES = Arrays.asList(new String[] {
			JATSSpanFactory.P,
			JATSSpanFactory.LABEL,
	});
	
	@Override
	protected List<String> getAllowedChildNames() {
		return ALLOWED_CHILD_NAMES;
	}

	public JATSFnElement(Element element) {
		super(element);
	}
	/** HTMLEquivalent
	 */
	@Override
	public HtmlElement createHTML() {
		HtmlP p = new HtmlP();
		p.setClassAttribute(this.TAG);
		p.setCharset("fn");
		return deepCopyAndTransform(p);
	}

	
}
