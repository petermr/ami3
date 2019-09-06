package org.contentmine.pdf2svg.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.contentmine.eucl.xml.XMLUtil;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Nodes;

public class LogAggregator {

	private Element pdfLogRoot;

	public LogAggregator() {
		
	}

	/**
	<pdfLog glyphs="true">
    <fontList>
        <font name="DENAEF+Frutiger-LightCn"
        family="Frutiger-LightCn" type="PDType1Font"
        encoding="DictionaryEncoding" fontencoding="DictionaryEncoding"
        basefont="DENAEF+Frutiger-LightCn"
        bold="false" italic="false" symbol="false"/>
    </fontList>
    */
	public void aggregateAllLogs(String topDir) {
		File topFile = new File(topDir);
		System.err.println("Top "+topFile.getAbsolutePath());
		createDummyLog();
		aggregateLogs(topFile);
		removeCharactersWithEmptyPaths();
		removeFontAttributes();
		removeDuplicateCharacters();
		removeNullCharacters();
		removeEmptyPages();
		removeEmptyFiles();
	}

	private void removeNullCharacters() {
		Nodes nullCharacters = pdfLogRoot.query(".//character[@name='null']");
		int removed = 0;
		for (int j = nullCharacters.size()-1; j >= 0; j--) {
			Element nullCharacter = (Element) nullCharacters.get(j);
			Integer code = new Integer(nullCharacter.getAttributeValue("code"));
			if (code < 32) {
				nullCharacters.get(j).detach();
				removed++;
			}
		}
		System.err.println("removed "+removed+" null characters");
	}

	private void removeDuplicateCharacters() {
		Set<String> characterXMLSet = new HashSet<String>();
		Nodes characters = pdfLogRoot.query(".//character");
		for (int j = characters.size()-1; j >= 0; j--) {
			Element character = (Element) characters.get(j);
			String characterXML = character.toXML();
			if (characterXMLSet.contains(characterXML)) {
				character.detach();
			} else {
				characterXMLSet.add(characterXML);
			}
		}
	}

	private void removeFontAttributes() {
		Nodes fonts = pdfLogRoot.query(".//@font");
		for (int j = fonts.size()-1; j >= 0; j--) {
			fonts.get(j).detach();
		}
	}

	private void removeEmptyPages() {
		Nodes emptyPages = pdfLogRoot.query(".//page[count(*)=0]");
		for (int j = emptyPages.size()-1; j >= 0; j--) {
			emptyPages.get(j).detach();
		}
		System.err.println("removed "+emptyPages.size()+" empty pages");
	}

	private void removeEmptyFiles() {
		Nodes emptyFiles = pdfLogRoot.query(".//pdf[count(*)=0]");
		for (int j = emptyFiles.size()-1; j >= 0; j--) {
			emptyFiles.get(j).detach();
		}
		System.err.println("removed "+emptyFiles.size()+" empty pages");
	}

	/**
      <character font="LinLibertine" family="LinLibertine" name="null" code="3">
        <path stroke="black" fill="none" d="" stroke-width="0.0050" xmlns="http://www.w3.org/2000/svg"/>
      </character>
	 */
	private void removeCharactersWithEmptyPaths() {
		Nodes emptyPathNodes = pdfLogRoot.query(".//character[*[local-name()='path' and @d='']]");
		for (int j = emptyPathNodes.size()-1; j >= 0; j--) {
			emptyPathNodes.get(j).detach();
		}
		System.err.println("removed "+emptyPathNodes.size()+" empty paths");
	}

	private void createDummyLog() {
		pdfLogRoot = new Element("pdfLog");
		pdfLogRoot.addAttribute(new Attribute("glyphs", "true"));
		Element fontList = new Element("fontList");
		pdfLogRoot.appendChild(fontList);
		Element dummyFont = new Element("font");
		dummyFont.addAttribute(new Attribute("name", "dummy"));
		fontList.appendChild(dummyFont);
	}

	private void aggregateLogs(File topFile) {
		File[] files = topFile.listFiles();
		if (files != null) {
			for (File file : files) {
				if ("pdfLog.xml".equals(file.getName())) {
					addLogFile(file);
				} else if (file.isDirectory()) {
					aggregateLogs(file);
				}
			}
		}
	}

	/**
    <pdf filename="C:\Users\pm286\workspace\pdf2svg\..\pdfs\pdfsByJournal\ZoologicaScripta\Liu,
    Yang - 2006.pdf" pageCount="18">
        <page num="1"/>
        <page num="2"/>
        <page num="3"/>
        <page num="4"/>
        <page num="5"/>
        <page num="6"/>
        <page num="7">
            <character font="GNBAPP+Universal-NewswithCommPi"
            family="Universal-NewswithCommPi"
            name="H20040" code="1">
                <path stroke="black" fill="none"
	 * @param file
	 */
	private void addLogFile(File file) {
		System.err.println(file.getAbsolutePath());
		Element logElement = XMLUtil.parseQuietlyToDocument(file).getRootElement();
		// add children with characters
		Nodes nodes = logElement.query(".//pdf[page[count(*)>0]]");
		for (int i = 0; i < nodes.size(); i++) {
			Element pdfElement = (Element) nodes.get(i);
			pdfElement.detach();
			pdfLogRoot.appendChild(pdfElement);
		}
	}
	
	private void writeLog(File file) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		XMLUtil.debug(pdfLogRoot, fos, 2);
	}
	
	public static void main(String[] args) throws IOException {
		LogAggregator aggregator = new LogAggregator();
//		aggregator.aggregateAllLogs("target/pdfsByJournal");
//		aggregator.aggregateAllLogs("target/minorJournals");
		aggregator.aggregateAllLogs("target/livingReviews");
		aggregator.writeLog(new File("target/livingReviews-pdfLog.xml"));
	}

}
