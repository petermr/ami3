package org.contentmine.ami.tools.table;

import nu.xom.Element;

public interface HasQuery {

	Element parseQueries();
	Boolean getQueryResult();
}
