package org.contentmine.ami;

import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.plugins.CommandProcessor;
import org.junit.Ignore;
import org.junit.Test;

public class TitleTest {

private static final Logger LOG = LogManager.getLogger(TitleTest.class);

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
