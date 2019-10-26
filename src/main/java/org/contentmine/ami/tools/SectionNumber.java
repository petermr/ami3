package org.contentmine.ami.tools;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * decimal section number
 * allows sub(sub...)sections
 * @author pm286
 *
 */
public class SectionNumber {
	private static final Logger LOG = Logger.getLogger(SectionNumber.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private int serial;
	private SectionNumber parent;

	public SectionNumber() {
		this(0);
	}
	
	public SectionNumber(int serial) {
		this.serial = serial;
		this.parent = null;
	}
	
	public static SectionNumber createFrom(SectionNumber parent, int serial) {
		SectionNumber sectionNumber = new SectionNumber(serial);
		sectionNumber.parent = parent;
		return sectionNumber;
	}
	
	public void incrementSerial() {
		this.serial++;
	}

	public int getSerial() {
		return serial;
	}

	public void setSerial(int serial) {
		this.serial = serial;
	}

	public SectionNumber getParent() {
		return parent;
	}

	public void setParent(SectionNumber parent) {
		this.parent = parent;
	}

	@Override
	public String toString() {
		return parent != null ? parent.toString() + "." + getSerialString() : getSerialString();
	}

	private String getSerialString() {
		return String.valueOf(serial);
	}
	
	
}
