package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.norma.sections.JATSBuilder.BuilderType;

/** creates JATS builxders for diffwerent inputs (e.g. HTML, JATS, etc.)
 * 
 * @author pm286
 *
 */
public class JATSBuilderFactory {
	private static final Logger LOG = LogManager.getLogger(JATSBuilderFactory.class);
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
