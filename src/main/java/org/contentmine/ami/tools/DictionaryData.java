package org.contentmine.ami.tools;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.AMIDictionaryTool.DictionaryFileFormat;
import org.contentmine.ami.tools.AMIDictionaryTool.InputFormat;
import org.contentmine.ami.tools.AMIDictionaryTool.Operation;
import org.contentmine.ami.tools.AMIDictionaryTool.WikiLink;

class DictionaryData {
	static final Logger LOG = Logger.getLogger(DictionaryData.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

    String[]                dataCols;
    String[]                dictionary;
    String                  dictionaryTopname;
	String                  href;
    String[]                hrefCols;
    InputFormat             informat;
    String                  input;
	String                  linkCol;
	String[]                log4j;
	String                  nameCol;
    Operation               operation;
    DictionaryFileFormat[]  outformats;
    String                  splitCol=",";
	String                  termCol;
    String[]                terms;
	WikiLink[]              wikiLinks;

}
