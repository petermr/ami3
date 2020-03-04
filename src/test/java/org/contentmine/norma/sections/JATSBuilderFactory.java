package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.norma.sections.JATSBuilder.BuilderType;

/** creates JATS builxders for diffwerent inputs (e.g. HTML, JATS, etc.)
 * 
 * @author pm286
 *
 */
public class JATSBuilderFactory {
	private static final Logger LOG = Logger.getLogger(JATSBuilderFactory.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public static JATSBuilder createJATSBuilder(BuilderType type) {
		JATSBuilder builder = new JATSBuilder();
		if (type == null) {
			throw new RuntimeException("null builder type ");
		} else if (type.equals(JATSBuilder.BuilderType.HTML)) {
			builder = new HtmlMetaJATSBuilder();
		} else {
			throw new RuntimeException("Unsupported builder: "+type);
		}
		return builder;
	}

}
