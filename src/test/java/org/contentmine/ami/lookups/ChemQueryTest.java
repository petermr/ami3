package org.contentmine.ami.lookups;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.junit.Ignore;
import org.junit.Test;

@Ignore("online")
public class ChemQueryTest {

	private static final Logger LOG = LogManager.getLogger(ChemQueryTest.class);
@Test
	public void testRSCAPI() throws IOException {
		String s = "http://pubs.rsc.org/en/results/journals?Category=Journal&AllText=rhodium&IncludeReference=true&SelectJournal=false&DateRange=false&SelectDate=false&Type=Months&DateFromMonth=Months&DateToMonth=Months&PriceCode=False&OpenAccess=false";
		URL url= new URL(s);
		InputStream is = url.openStream();
		List<String> lines = IOUtils.readLines(is);
		FileUtils.writeLines(new File("target/chem/rsc.html"), lines);
		LOG.debug(lines);
	}
	
	@Test
	public void testACSAPI() throws IOException {
		String s = "http://pubs.acs.org/action/doSearch?text1=rhodium&field1=AllField";
		URL url= new URL(s);
		InputStream is = url.openStream();
		List<String> lines = IOUtils.readLines(is);
		FileUtils.writeLines(new File("target/chem/acs.html"), lines);
		LOG.debug(lines);
	}
		
	@Test
	public void testWileyAPI() throws IOException {
		String s = "http://onlinelibrary.wiley.com/advanced/search/results/reentry?publicationDoi=10.1002/(ISSN)1521-3773";
		URL url= new URL(s);
		InputStream is = url.openStream();
		List<String> lines = IOUtils.readLines(is);
		FileUtils.writeLines(new File("target/chem/wiley.html"), lines);
		LOG.debug(lines);
	}
				
			
}
