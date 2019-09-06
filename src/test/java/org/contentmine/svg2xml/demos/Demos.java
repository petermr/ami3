package org.contentmine.svg2xml.demos;

import org.contentmine.svg2xml.pdf.PDFAnalyzerTest;

public class Demos {

	public static void main(String[] args) {
//			ebola1();
//			astro1();
//			plot1();
			PDFAnalyzerTest.analyzePDF("demos/gandhi/sample.pdf"); 

	}

	private static void ebola1() {
		PDFAnalyzerTest.analyzePDF(("demos/ebola/roadmapsitrep_12Nov2014_eng.pdf")); 
//		PDFAnalyzerTest.analyzePDF(("demos/ebola/roadmapsitrep_14Nov2014_eng.pdf")); 
	}
	
	private static void astro1() {
		PDFAnalyzerTest.analyzePDF(("demos/astro/0004-637X_778_1_1.pdf")); 
	}
	
	private static void plot1() {
		PDFAnalyzerTest.analyzePDF(("demos/plot/22649_Sada_2012-1.pdf")); 
//		analyzePDFFile
	}
	
}
