package org.contentmine.norma.output;

import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.cproject.util.CMineUtil;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlScript;

import nu.xom.Element;

/** turns XML into non-XML text.
 * Mainly scripts and styles
 * 
 * @author pm286
 *
 */
public class HtmlTextifier {
	private static final Logger LOG = LogManager.getLogger(HtmlTextifier.class);
public HtmlTextifier() {
		
	}

	public String textify(HtmlElement htmlElement) {
		removeEmptyStyles(htmlElement);
		List<HtmlScript> scripts = HtmlScript.extractSelfAndDescendantScripts(htmlElement);
		// remove empty scripts
		for (int i = scripts.size() - 1; i >= 0; i--) {
			HtmlScript script = scripts.get(i);
			if (script.getSrc() != null) {
			} else if (script.getSrc().trim().length() == 0 || script.getValue().trim().length() == 0) {
				script.detach();
			}
		}
		String content = htmlElement.toXML();
		StringBuilder sb = new StringBuilder();
		int i0 = 0;
		int is = 0;
		while (true) {
			int i1 = content.indexOf("<script", i0);
			if (i1 == -1) break;
			String ss = content.substring(i0, i1);
			sb.append(ss);
			i0 = content.indexOf(">", i1) + 1;
			if (is >= scripts.size()) break;
			HtmlScript script = scripts.get(is++);
			String src = script.getSrc();
			String scriptContent = null;
			try {
				scriptContent = IOUtils.toString(this.getClass().getResourceAsStream(src), CMineUtil.UTF8_CHARSET);
			} catch (IOException e) {
				throw new RuntimeException("bad resource: "+src, e);
			}
			if (scriptContent.trim().length() > 0) {
				sb.append("<script>\n");
				sb.append(scriptContent);
				sb.append("</script>\n");
			}
		}
		sb.append(content.substring(i0));
		return sb.toString();
	}

	private void removeEmptyStyles(HtmlElement htmlElement) {
		List<Element> styles = XMLUtil.getQueryElements(htmlElement, ".//*[local-name()='style' and normalize-space(.)='']");
		for (int i = styles.size() - 1; i >= 0; i--) {
			styles.get(i).detach();
		}
	}
	
}
