package org.contentmine.cproject.util;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.util.ResultsAnalysis.SummaryType;
import org.contentmine.graphics.html.HtmlTable;

public interface ResultsAnalysis {
	public enum SummaryType {
		COMMONEST("commonest"),
		COUNT("count"),
		ENTRIES("entries"),
		FULL("full");
		String type;
		private SummaryType(String type) {
			this.type = type;
		}
		public String toString() {
			return type;
		}
	}
	
	static final String SCHOLARLY_HTML = "/scholarly.html";
	static final String SNIPPETS_XML = "snippets.xml";
	public static List<SummaryType> SUMMARY_TYPES = 
		Arrays.asList(new SummaryType[]{SummaryType.COMMONEST, SummaryType.COUNT, SummaryType.ENTRIES, SummaryType.FULL});

	void addDefaultSnippets(File projectDir);

	void setSummaryType(SummaryType summaryType);

	HtmlTable makeHtmlDataTable();

}
