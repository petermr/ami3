package org.contentmine.norma.shtml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.contentmine.cproject.util.CMineGlobber;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.norma.Norma;
import org.contentmine.norma.NormaFixtures;
import org.contentmine.norma.pubstyle.PubstyleReader;
import org.contentmine.norma.xsl.TransformerWrapper;
import org.junit.Test;

import junit.framework.Assert;

/** convert TEI output to HTML.
 * 
 * @author pm286
 *
 */
public class TeiTest {

	/** fairly rudimentary Grobid stylesheet.
	 * 
	 * @throws TransformerException
	 */
	@Test
	public void testTEI2JATSAPI() throws TransformerException {
		File sourceDir = NormaFixtures.TEST_GROBID_TEI_DIR;
		File targetDir = new File("target/grobid/tei");
		CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
		File teiFile = new File(targetDir, "0807.3577.tei.xml");
		Source teiSource = new StreamSource(teiFile);
		String teiXslResource = PubstyleReader.TEI_XSL_REOURCE+"/"+"grobid-jats.xsl";
		InputStream xslStream = getClass().getResourceAsStream(teiXslResource);
		Source xslSource = new StreamSource(xslStream);
		Transformer transformer = TransformerFactory.newInstance().newTransformer(xslSource);
		File grobidOutFile = new File(targetDir, "0807.3577.jats.xml");
		Result mzResultSource = new StreamResult(grobidOutFile);
		transformer.transform(teiSource, mzResultSource);
		Assert.assertTrue("output file", grobidOutFile.exists());
	}

	/** fairly rudimentary Grobid stylesheet.
	 * 
	 * @throws TransformerException
	 */
	@Test
	public void testTEI2JATSAPI1() throws TransformerException, IOException {
		File sourceDir = NormaFixtures.TEST_GROBID_TEI_DIR;
		File targetDir = new File("target/grobid/tei");
		targetDir.mkdirs();
		String root = "test_Grobid_1_05452615.tei";
		CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
		File teiFile = new File(targetDir, root+".xml");
		File grobidOutFile = new File(targetDir, root+".html");
		TransformerWrapper transformerWrapper = new TransformerWrapper();
		transformerWrapper.createTransformer(PubstyleReader.TEI_XSL_REOURCE+"/"+"grobid-html.xsl");
		transformerWrapper.transformToXMLFile(teiFile, grobidOutFile);
		Assert.assertTrue("output file", grobidOutFile.exists());
	}

	/** test TEI2HTML stylesheet
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testTEI2HTML() throws IOException {
		Norma norma = new Norma();
		File sourceDir = NormaFixtures.TEST_GROBID_TEI_DIR;
		File targetDir = new File("target/grobid/tei");
		CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
		CMineGlobber globber = new CMineGlobber();
		globber.setRegex(".*.xml");
		globber.setLocation(targetDir.toString());
		List<File> xmlFiles = globber.listFiles();
		Assert.assertEquals(2, xmlFiles.size());
		norma.convertRawTEIXMLToProject(targetDir);
		globber.setRegex(".*/fulltext\\.xml");
		globber.setLocation(targetDir.toString());
		xmlFiles = globber.listFiles();
		Assert.assertEquals(2, xmlFiles.size());
		
		String cmd = "--project "+targetDir + " --input fulltext.xml" + " --output fulltext.html "+" --transform grobid2html ";
		new Norma().run(cmd);
	}

//	0807.3577.tei.xml
//	1001._0908.0054.tei.xml
}
