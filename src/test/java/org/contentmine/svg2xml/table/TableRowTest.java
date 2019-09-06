package org.contentmine.svg2xml.table;

import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGUtil;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import nu.xom.Element;

/** 
 * test for RowChunk
 * @author pm286
 *
 */
public class TableRowTest {

	private final static Logger LOG = Logger.getLogger(TableRowTest.class);
	
	@Test
	public void dummy() {
		LOG.trace("TableRowChunkTest NYI");
	}

	@Test
	public void testTHChunkValue() {
		TableChunk cellChunk = new TableChunk();
		Element element = XMLUtil.parseQuietlyToDocument(TableFixtures.HROWFILE).getRootElement();
		AbstractCMElement svgElement = SVGElement.readAndCreateSVG(element);
		List<SVGElement> elementList = SVGUtil.getQuerySVGElements(svgElement, TableFixtures.TEXT_OR_PATH_XPATH);
		Assert.assertEquals("elements", 23, elementList.size());
		cellChunk.setElementList(elementList);
		String value = cellChunk.getValue();
		Assert.assertEquals("value", "aStrainnMLT(min)SD(min)", value);
	}

	@Test
	public void testRowChunk() {
		TableChunk cellChunk = new TableChunk();
		Element element = XMLUtil.parseQuietlyToDocument(TableFixtures.TDBLOCKFILE).getRootElement();
		AbstractCMElement svgElement = SVGElement.readAndCreateSVG(element);
//		svgElement.debug("XXX");
		List<SVGElement> elementList = SVGUtil.getQuerySVGElements(svgElement, TableFixtures.TEXT_OR_PATH_XPATH);
		cellChunk.setElementList(elementList);
	}
	
	
	@Test
	@Ignore
	public void testCreateStructuredRows() {
		TableChunk genericChunk = TableFixtures.createGenericChunkFromElements(TableFixtures.TDBLOCKFILE);
		TableBody tableBody = new TableBody(genericChunk.getElementList());
		List<TableRow> rowList = tableBody.createStructuredRows();
		// may need fixing
		String[] rowHtml = {
//				"<tr xmlns=\"http://www.w3.org/1999/xhtml\"><td><p><span>IN61</span></p></td><td><p><span>274</span></p></td><td><p><span>45.7</span></p></td><td><p><span>2.92</span></p></td></tr>",
				"<tr xmlns=\"http://www.w3.org/1999/xhtml\"><td><div>IN61 </div></td><td><div>274 </div></td><td><div>45.7 </div></td><td><div>2.92 </div></td></tr>",		
				"<tr xmlns=\"http://www.w3.org/1999/xhtml\"><td><p><span>IN56 (WT)</span></p></td><td><p><span>230</span></p></td><td><p><span>65.1</span></p></td><td><p><span>3.24</span></p></td></tr>",
				"<tr xmlns=\"http://www.w3.org/1999/xhtml\"><td><p><span>IN160</span></p></td><td><p><span>47</span></p></td><td><p><span>29.5</span></p></td><td><p><span>3.28</span></p></td></tr>",
				"<tr xmlns=\"http://www.w3.org/1999/xhtml\"><td><p><span>IN62</span></p></td><td><p><span>136</span></p></td><td><p><span>54.3</span></p></td><td><p><span>3.42</span></p></td></tr>",
				"<tr xmlns=\"http://www.w3.org/1999/xhtml\"><td><p><span>IN70</span></p></td><td><p><span>52</span></p></td><td><p><span>54.5</span></p></td><td><p><span>3.86</span></p></td></tr>",
				"<tr xmlns=\"http://www.w3.org/1999/xhtml\"><td><p><span>IN57</span></p></td><td><p><span>53</span></p></td><td><p><span>47.0</span></p></td><td><p><span>4.25</span></p></td></tr>",
				"<tr xmlns=\"http://www.w3.org/1999/xhtml\"><td><p><span>IN69</span></p></td><td><p><span>119</span></p></td><td><p><span>45.0</span></p></td><td><p><span>4.38</span></p></td></tr>",
				"<tr xmlns=\"http://www.w3.org/1999/xhtml\"><td><p><span>IN63</span></p></td><td><p><span>209</span></p></td><td><p><span>41.2</span></p></td><td><p><span>4.55</span></p></td></tr>",
				"<tr xmlns=\"http://www.w3.org/1999/xhtml\"><td><p><span>IN64</span></p></td><td><p><span>63</span></p></td><td><p><span>48.4</span></p></td><td><p><span>4.60</span></p></td></tr>",
				"<tr xmlns=\"http://www.w3.org/1999/xhtml\"><td><p><span>IN68</span></p></td><td><p><span>153</span></p></td><td><p><span>54.1</span></p></td><td><p><span>5.14</span></p></td></tr>",
				"<tr xmlns=\"http://www.w3.org/1999/xhtml\"><td><p><span>IN66</span></p></td><td><p><span>189</span></p></td><td><p><span>82.2</span></p></td><td><p><span>5.87</span></p></td></tr>",
				"<tr xmlns=\"http://www.w3.org/1999/xhtml\"><td><p><span>IN67</span></p></td><td><p><span>212</span></p></td><td><p><span>57.6</span></p></td><td><p><span>6.71</span></p></td></tr>",
				"<tr xmlns=\"http://www.w3.org/1999/xhtml\"><td><p><span>IN65</span></p></td><td><p><span>33</span></p></td><td><p><span>83.8</span></p></td><td><p><span>6.95</span></p></td></tr>",
				"<tr xmlns=\"http://www.w3.org/1999/xhtml\"><td><p><span>IN71</span></p></td><td><p><span>49</span></p></td><td><p><span>68.8</span></p></td><td><p><span>7.67</span></p></td></tr>",
			};
		for (int i = 0; i < rowList.size(); i++) {
			TableRow row = rowList.get(i);
			HtmlElement tr = row.createHtmlElement();
			Assert.assertEquals("row"+i, rowHtml[i], row.createHtmlElement().toXML());
		}
	}
}
