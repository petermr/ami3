package org.contentmine.ami.tools.table;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import nu.xom.Element;

public interface HasQuery {

	TQueryTool getOrCreateQueryTool();
	

}
