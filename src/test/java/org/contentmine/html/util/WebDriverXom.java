package org.contentmine.html.util;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import nu.xom.Attribute;
import nu.xom.Element;

/** transform WebDriver element to xom Element
 * 
 * @author pm286
 *
 */
public class WebDriverXom {
	private static final Logger LOG = LogManager.getLogger(WebDriverXom.class);
private static String[] ATTNAMES = {
		"alt",
//		"class",
		"content",
		"height",
		"href",
		"id",
		"name",
		"property",
		"rel",
		"src",
		"style",
		"target",
		"type",
		"width",
	};
	private static List<String> EXCLUDE_TAGS = Arrays.asList(new String[]{
		"link",
		"script",
		"style",
	});

	public WebDriverXom() {
		
	}
	public Element createXomElement(WebElement webElement) {
		Element element = null;
		if (webElement != null) {
			String tagName = webElement.getTagName();
			System.out.println(">"+tagName);
			element = new Element(tagName);
			for (String attName : ATTNAMES) {
				addAttribute(webElement, element, attName);
			}
			List<WebElement> childWebElements = webElement.findElements(By.xpath("./*"));
			for (WebElement childWebElement : childWebElements) {
				String childTagName = childWebElement.getTagName();
				if (!EXCLUDE_TAGS.contains(childTagName)) {
					Element xomElement = createXomElement(childWebElement);
					element.appendChild(xomElement);
				} else {
					System.err.println("EXCLUDED "+childTagName);
				}
			}
			String text = webElement.getText();
			if (text != null) {
				if (childWebElements.size() == 0) {
					element.appendChild(text);
				}
			}
		}
		return element;
	}
	
	private Attribute addAttribute(WebElement webElement, Element element, String attName) {
		Attribute attribute = null;
		String attValue = webElement.getAttribute(attName);
		if (attValue != null && attValue.trim().length() > 0) {
			attribute = new Attribute(attName, attValue);
			element.addAttribute(attribute);
		}
		return attribute;
	}
}
