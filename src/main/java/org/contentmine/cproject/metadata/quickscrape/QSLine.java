package org.contentmine.cproject.metadata.quickscrape;

public class QSLine {

	private String level;
	private String module;
	private String msg;

	public QSLine(String level, String module, String msg) {
		this.level = level;
		this.module = module;
		this.msg = msg;
	}
	
	public String toString() {
		return "["+level+"] ["+module+"] "+msg;
	}
}
