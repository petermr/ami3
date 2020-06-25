package org.contentmine.ami.plugins;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.CHESConstants;
import org.contentmine.ami.plugins.CommandProcessor;
import org.contentmine.cproject.testutil.DataTablesToolAnalyzer;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.norma.NAConstants;
import org.junit.Test;

public class CommandProcessorTest {

	private static final Logger LOG = LogManager.getLogger(CommandProcessorTest.class);
private final static String DICTIONARY_RESOURCE = CHESConstants.ORG_CM+"/"+NAConstants.AMI+"/plugins/dictionary";
	
	
	@Test
	public void testCommandLineSyntax() throws IOException {
		String args = "fooDir bar(plugh)";
		CommandProcessor.main(args.split("\\s+"));
	}
	
	
}
