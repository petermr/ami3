package org.contentmine.ami;

import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.plugins.CommandProcessor;
import org.junit.Ignore;
import org.junit.Test;

public class TitleTest {

private static final Logger LOG = Logger.getLogger(TitleTest.class);

	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	@Ignore // "NYI"
	public void testAddTitlesToRowHeadings() {
		CommandProcessor commandProcessor = null;
		try {
			commandProcessor = AMIFixtures.createDefaultDirectoriesAndProcessor("title");
		} catch (IOException e) {
			LOG.error("Cannot create dictionaries");
			return;
		}
		commandProcessor.setDefaultCommands("Humgen Spec Genus Primer WordFreq");
	}
}
