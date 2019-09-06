package org.contentmine.ami;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.contentmine.ami.plugins.regex.RegexPlugin;
import org.contentmine.ami.plugins.word.WordPlugin;
import org.contentmine.norma.NAConstants;
import org.contentmine.norma.Norma;

public class Prototypes {

	public static void main(String[] args) throws Exception {
//		runHalThesis1();
//		runHalThesis2();
//		runHalTheses();
//		runSpanishThesis();
//		runSpanishThesis1();
		runItalianThesis();
//		runAstro();
//		createPDFImages("journal.pone.0115884a");
	}

	private static void runHalThesis1() {
//		new Norma().run("-q examples/theses/HalThesis1 -i fulltext.pdf -o fulltext.pdf.txt --transform pdf2txt");
	}

	private static void runAstro() {
		createPDFTXT("examples/misc/", "0004-637X_778_1_1");
		WordPlugin wordPlugin = new WordPlugin("-q "
				+ "     examples/misc/0004-637X_778_1_1"
				+ " -i fulltext.pdf.txt --w.words wordFrequencies "
				+ " --w.stopwords "
				+ "     "+NAConstants.AMI_WORDUTIL+"/stopwords.txt "
				+ "     "+NAConstants.AMI_WORDUTIL+"/stopwords.pub.txt "
//				+ "     "+NAConstants.AMI_WORDUTIL+"/stopwords.fr.txt"
				+ "");
		wordPlugin.runAndOutput();
	}
	
	private static void runHalThesis2() {
		WordPlugin wordPlugin = new WordPlugin("-q examples/theses/HalThesis2 -i fulltext.pdf.txt --w.words wordFrequencies "
				+ "--w.stopwords "
				+ "     "+NAConstants.AMI_WORDUTIL+"/stopwords.txt "
				+ "     "+NAConstants.AMI_WORDUTIL+"/stopwords.pub.txt "
				+ "     "+NAConstants.AMI_WORDUTIL+"/stopwords.fr.txt");
		wordPlugin.runAndOutput();
	}
	
	private static void runHalTheses() {
		
		createPDFTXT("These_Nathalie_Mitton");
		createPDFTXT("Thesis_Calligari");
		createPDFTXT("20130912_Fei_YAO");
		createPDFTXT("HalThesis2");
		createPDFTXT("smigaj");
		createPDFTXT("TH2013PEST1177");

		
		WordPlugin wordPlugin = new WordPlugin("-q "
				+ "     examples/theses/HalThesis2"
				+ "     examples/theses/These_Nathalie_Mitton"
				+ "     examples/theses/20130912_Fei_YAO"
				+ "     examples/theses/smigaj"
				+ "     examples/theses/TH2013PEST1177"
				+ "     examples/theses/Thesis_Calligari"
				+ " -i fulltext.pdf.txt --w.words wordFrequencies "
				+ " --w.stopwords "
				+ "     "+NAConstants.AMI_WORDUTIL+"/stopwords.txt "
				+ "     "+NAConstants.AMI_WORDUTIL+"/stopwords.pub.txt "
				+ "     "+NAConstants.AMI_WORDUTIL+"/stopwords.fr.txt");
		wordPlugin.runAndOutput();
	}

	
	private static void runItalianThesis() {
		String dir = "examples/theses/italian/";
		createPDFTXT(dir,"mbarontini_tesid");
		createPDFTXT(dir,"Erriquez_Daniela_tesi");
		createPDFTXT(dir,"Fiorentina_Elena_tesi");
		createPDFTXT(dir,"Gou_Qian_Tesi");
		createPDFTXT(dir,"terracciano_maria_tesi");
		createPDFTXT(dir,"Trasporti_Europeo");
		
		WordPlugin wordPlugin = new WordPlugin("-q "
				+ "     "+dir+"/mbarontini_tesid"
				+ "     "+dir+"/Erriquez_Daniela_tesi"
				+ "     "+dir+"/Fiorentina_Elena_tesi"
				+ "     "+dir+"/Gou_Qian_Tesi"
				+ "     "+dir+"/terracciano_maria_tesi"
				+ "     "+dir+"/Trasporti_Europeo"
				+ " -i fulltext.pdf.txt --w.words wordFrequencies "
				+ " --w.stopwords "
				+ "     "+NAConstants.AMI_WORDUTIL+"/stopwords.txt "
				+ "     "+NAConstants.AMI_WORDUTIL+"/stopwords.pub.txt "
				+ "    "+NAConstants.AMI_WORDUTIL+"/stop-words_italian_it.txt");
		wordPlugin.runAndOutput();
		
	}
	
	WordPlugin wordPlugin = new WordPlugin("-q examples/theses/HalThesis2 -i fulltext.pdf.txt --w.words wordFrequencies "
			+ "--w.stopwords "
			+ "     "+NAConstants.AMI_WORDUTIL+"/stopwords.txt "
			+ "     "+NAConstants.AMI_WORDUTIL+"/stopwords.pub.txt "
			+ "     "+NAConstants.AMI_WORDUTIL+"/stopwords.fr.txt");
	
	private static void runSpanishThesis() {
		createPDFTXT("tesis_alexv6.5");
		WordPlugin wordPlugin = new WordPlugin("-q "
				+ "     examples/theses/tesis_alexv6.5"
				+ " -i fulltext.pdf.txt --w.words wordFrequencies "
				+ " --w.stopwords "
				+ "     "+NAConstants.AMI_WORDUTIL+"/stopwords.txt "
				+ "     "+NAConstants.AMI_WORDUTIL+"/stopwords.pub.txt "
				+ "     "+NAConstants.AMI_WORDUTIL+"/stop-words_spanish_es.txt");
		wordPlugin.runAndOutput();
		
	}

	private static void runSpanishThesis1() {
		createPDFHTML("tesis_alexv6.5");
		WordPlugin wordPlugin = new WordPlugin("-q "
				+ "     examples/theses/tesis_alexv6.5"
				+ " -i fulltext.pdf.txt --w.words wordFrequencies "
				+ " --w.stopwords "
				+ "     "+NAConstants.AMI_WORDUTIL+"/stopwords.txt "
				+ "     "+NAConstants.AMI_WORDUTIL+"/stopwords.pub.txt "
				+ "     "+NAConstants.AMI_WORDUTIL+"/stop-words_spanish_es.txt");
		wordPlugin.runAndOutput();
		
	}
	
	private static void createPDFTXT(String name) {
		new Norma().run("-i examples/theses/"+name+".pdf -o examples/theses/");
		new Norma().run("-q examples/theses/"+name+" -i fulltext.pdf -o fulltext.pdf.txt --transform pdf2txt");
	}
	private static void createPDFTXT(String dir, String name) {
		String dir1 = dir.endsWith("/") ? dir : dir+"/";
		new Norma().run("-i "+dir1+name+".pdf -o "+dir1);
		new Norma().run("-q "+dir1+name+" -i fulltext.pdf -o fulltext.pdf.txt --transform pdf2txt");
	}

	private static void createPDFHTML(String name) {
		new Norma().run("-i examples/theses/"+name+".pdf -o examples/theses1/");
		new Norma().run("-q examples/theses1/"+name+" -i fulltext.pdf -o fulltext.pdf.html --transform pdf2html");
	}
	

	/** extracts images and writes to (new) images/directory.
	 * 
	 * @param name
	 * @throws Exception
	 */
	private static void createPDFImages(String name) throws Exception {
		String cTreeName = "../norma/src/test/resources/org/contentmine/norma/pubstyle/plosone/"+name+"/";
		String targetName = "../ami-plugin/target/imagetest/";
		FileUtils.copyDirectory(new File(cTreeName), new File(targetName));
		new Norma().run("-q "+targetName+" -i fulltext.pdf -o images/ --transform pdf2images");
	}
	
	
}
