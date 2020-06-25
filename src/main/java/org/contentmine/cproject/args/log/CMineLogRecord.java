package org.contentmine.cproject.args.log;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class CMineLogRecord {

	private static final Logger LOG = LogManager.getLogger(CMineLogRecord.class);
private List<String> values;
	private List<String> headers;
	
	public CMineLogRecord(final List<String> headers) {
		this.headers = headers;
		values = new ArrayList<String>(headers.size());
		for (int i = 0; i < headers.size(); i++) {
			values.add("");
		}
	}
	
	public void add(String name, String value) {
		int position = headers.indexOf(name);
		if (position < 0) {
			throw new RuntimeException("name: "+name+" is not in headers: "+headers);
		}
		if (position >= 0 && position < headers.size()) {
			values.set(position, value);
		}
	}

	public Iterable<? extends String> getValues() {
		return values;
	}

}
