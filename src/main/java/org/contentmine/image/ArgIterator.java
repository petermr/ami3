package org.contentmine.image;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class ArgIterator {

	private static final Logger LOG = Logger.getLogger(ArgIterator.class);
	
	private List<String> argList;
	private int pointer;
	private boolean debug = true;

	public ArgIterator(String[] args) {
		argList = new ArrayList<String>();
		for (String arg : args) {
			argList.add(arg);
		}
		pointer = 0;
	}

	public int size() {
		return argList.size();
	}

	public boolean hasNext() {
		return pointer < size();
	}

	public String getCurrent() {
		return (pointer < size()) ? argList.get(pointer) : null;
	}

	public String getLast() {
		return (pointer == 0) ? null : argList.get(pointer - 1);
	}

	public void next() {
		pointer++;
	}

	public String getSingleValue() {
		String value = null;
		List<String> values = getValues();
		if (values.size() != 1) {
			LOG.error("expected exactly one arg at "+getCurrent()+" after "+getLast());
		} else {
			value = values.get(0);
		}
		return value;
	}

	public List<String> getValues() {
		pointer++;
		List<String> stringList = new ArrayList<String>();
		while (pointer < size()) {
			String arg = argList.get(pointer);
			if (arg.startsWith("-")) {
				break;
			}
			stringList.add(arg);
			pointer++;
		}
		return stringList;
	}

	public Integer getSingleIntegerValue() {
		Integer value = null;
		String vv = getSingleValue();
		if (vv != null) {
			try {
				value = new Integer(vv);
			} catch (Exception e) {
				LOG.error("Bad integer value: "+vv);
			}
		}
		return value;
	}

	public Double getDoubleValue() {
		Double value = null;
		String vv = getSingleValue();
		if (vv != null) {
			try {
				value = Double.valueOf(vv);
			} catch (Exception e) {
				LOG.error("Bad double value: "+vv);
			}
		}
		return value;
	}

	public void setDebug(boolean b) {
		this.debug = b;
	}
}
