package org.contentmine.pdf2svg.demos;

import org.contentmine.svg2xml.PDF2SVGConverter;

public class PDFDemosIT {

	public static void main(String[] args) {
//			ebola1();
//			astro1();
		run("target/plot", "demos/plot/22649_Sada_2012-1.pdf");
//		run("target/gandhi", "demos/gandhi/sample.pdf");

	}

	private static void ebola1() {
		new PDF2SVGConverter().run(
				"-logger", 
				"-infofiles", 
				"-logglyphs", 
				"-outdir", "target/ebola", 
				"demos/ebola/roadmapsitrep_12Nov2014_eng.pdf"
		);
	}
	
	private static void astro1() {
		new PDF2SVGConverter().run(
				"-logger", 
				"-infofiles", 
				"-logglyphs", 
				"-outdir", "target/astro", 
				"demos/astro/0004-637X_778_1_1.pdf"
		);
	}
	
	private static void plot1() {
		new PDF2SVGConverter().run(
				"-logger", 
				"-infofiles", 
				"-logglyphs", 
				"-outdir", "target/plot", 
				"demos/plot/22649_Sada_2012-1.pdf"
		);
	}
	
	private static void run(String outdir, String infile) {
		new PDF2SVGConverter().run(
				"-logger", 
				"-infofiles", 
				"-logglyphs", 
				"-outdir", outdir, 
				infile
		);
	}
	
	
	
}
