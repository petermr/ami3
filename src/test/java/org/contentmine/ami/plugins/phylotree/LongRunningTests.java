package org.contentmine.ami.plugins.phylotree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.AMIFixtures;
import org.contentmine.ami.plugins.phylotree.PhyloConstants;
import org.contentmine.ami.plugins.phylotree.PhyloTreeArgProcessor;
import org.contentmine.ami.plugins.phylotree.nexml.NexmlNEXML;
import org.contentmine.ami.plugins.phylotree.nexml.NexmlOtu;
import org.contentmine.ami.plugins.regex.RegexArgProcessor;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.norma.NAConstants;
import org.contentmine.norma.editor.EditList;
import org.contentmine.norma.editor.Extraction;
import org.contentmine.norma.editor.SubstitutionEditor;
import org.contentmine.norma.image.ocr.HOCRReaderOLD;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

@Ignore("remove Ignore for development")
public class LongRunningTests {

	public static final Logger LOG = Logger.getLogger(LongRunningTests.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	//????????
	public final static String PHYLO = "./target/appassembler/bin/ami-phylotree";

	public final static String SUFFIX = ".pbm.png";
	private static String[] BAD_ROOTS = {
		"ijs.0.64938-0-000",
		"ijs.0.64950-0-000",
		"ijs.0.64952-0-000",
		"ijs.0.64969-0-000",
		"ijs.0.64970-0-001",
		"ijs.0.64980-0-001",
		"ijs.0.65003-0-000",
	};
	@Test
	public void testImage2Nexml() throws IOException {
		File phyloDir = new File(NAConstants.TEST_AMI_DIR+"/phylo/50images/");
		Assert.assertTrue("file exists "+phyloDir, phyloDir.exists() );
		Assert.assertTrue("file is dir "+phyloDir, phyloDir.isDirectory());
		List<File> images = new ArrayList<File>(FileUtils.listFiles(phyloDir, new String[]{"png"}, false));
		for (File image : images) {
			String name = image.getName();
			name = name.substring(0,  name.length() - SUFFIX.length());
			runPhyloEditing2Nexml(phyloDir, name);
		}
	}

	@Test
	// TESTED 2016-01-12
	public void testSpanishRegex() {
		String args = "-q "
				+ "     examples/theses/tesis_alexv6.5"
				+ " -i scholarly.html --xpath //* --r.regex regex/spanish.xml";
		RegexArgProcessor argProcessor = new RegexArgProcessor(args);
		argProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(argProcessor, 1, 0, 
				"<results title=\"spanish\">"
				+ "<result pre=\"ultados . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 150 6. Brillo de \" name0=\"cielo\" value0=\"cielo\" post=\"en la Comunidad de Madrid 153 6.1. Introduccio´n . . . . . . . . . . . . . . . . . . . . . . . . . \" xpath=\"/*[local-name()='h");
		
	}


	private void runPhyloEditing2Nexml(File phyloDir, String name) throws IOException,
			FileNotFoundException {
		LOG.debug("============="+name+"=============");
		File normaTemp = new File("target/phylo/"+name+"/");
		FileUtils.copyFile(new File(phyloDir, name+SUFFIX), new File(normaTemp, "image/"+name+SUFFIX));
		String cmd = "--ph.phylo -q target/phylo/"+name+
				" -i image/"+name+".pbm.png"+
				" --log"+
				" --ph.specpattern ijsemSpeciesEditor.xml"+
				" --ph.hocr.html image/"+name+".hocr.html"+
				" --ph.hocr.svg image/"+name+".hocr.svg"+
				" --ph.nexml image/"+name+".nexml.xml"+
				"";
		PhyloTreeArgProcessor phyloTreeArgProcessor = new PhyloTreeArgProcessor(cmd);
		phyloTreeArgProcessor.runAndOutput();

		NexmlNEXML nexml = phyloTreeArgProcessor.getNexml();

		SubstitutionEditor substitutionEditor = new SubstitutionEditor();
		substitutionEditor.addEditor(phyloTreeArgProcessor.getOrCreateSpeciesPatternInputStream());
		List<NexmlOtu> otuList = nexml.getSingleOtusElement().getNexmlOtuList();
		nexml.getSingleOtusElement().addNamespaceDeclaration(PhyloConstants.CM_PHYLO_PREFIX, PhyloConstants.CM_PHYLO_NS);
		for (NexmlOtu otu : otuList) {
			String value = otu.getValue();
			String editedValue = substitutionEditor.createEditedValueAndRecord(value);
			List<Extraction> extractionList = substitutionEditor.getExtractionList();
			otu.annotateOtuWithEditRecord(substitutionEditor.getEditRecord());
			phyloTreeArgProcessor.annotateOtuWithExtractions(otu, extractionList);
			LOG.trace(">otu>"+otu.toXML());
//			if (substitutionEditor.validate(extractionList)) {
			if (substitutionEditor.validate(editedValue)) {
				EditList editRecord = substitutionEditor.getEditRecord();
				otu.setEditRecord(editRecord.toString());
				LOG.debug("validated: "+value+" => "+editedValue+((editRecord == null || editRecord.size() == 0) ? "" :"; "+editRecord));
			} else {
				LOG.debug("failed validate: "+value);
			}
		}
		LOG.trace(nexml.toXML());
		normaTemp.mkdirs();
		XMLUtil.debug(nexml, new FileOutputStream(new File(normaTemp, name+".edited.nexml.xml")), 1);
	}

	@Test
	//	@Ignore("too many")
		public void testConvertLabelsAndTreeAndMerge() throws Exception {
			
			for (String root : MergeTipTest.ROOTS) {
				try {
					LOG.debug(root);
					File infile = new File(AMIFixtures.TEST_PHYLO_DIR, "15goodtree/"+root+MergeTipTest.PBM_PNG);
					org.apache.commons.io.FileUtils.copyFile(infile, new File(MergeTipTest.X15GOODTREE+root+MergeTipTest.PNG));
					PhyloTreeArgProcessor phyloTreeArgProcessor = new PhyloTreeArgProcessor();
					phyloTreeArgProcessor.setSpeciesPattern(MergeTipTest.IJSEM);
					phyloTreeArgProcessor.setOutputRoot(root);
					phyloTreeArgProcessor.setOutputDir(new File("target/phylo/combined/15goodtree/"));
					if (!phyloTreeArgProcessor.mergeOCRAndPixelTree(infile)) continue; // tesseract failure
					NexmlNEXML nexml = phyloTreeArgProcessor.getNexml();
					new File(MergeTipTest.X15GOODTREE).mkdirs();
					XMLUtil.debug(nexml, new FileOutputStream(MergeTipTest.X15GOODTREE+root+".nexml.xml"), 1);
					FileUtils.write(new File(MergeTipTest.X15GOODTREE+root+".nwk"), nexml.createNewick());
					XMLUtil.debug(nexml.createSVG(), new FileOutputStream(MergeTipTest.X15GOODTREE+root+".svg"), 1);
					HOCRReaderOLD hocrReader = phyloTreeArgProcessor.getOrCreateHOCRReader();
					SVGSVG.wrapAndWriteAsSVG(hocrReader.getOrCreateSVG(), new File(MergeTipTest.X15GOODTREE+root+".words.svg"));
				} catch (Exception e) {
					LOG.debug("error in conversion");
					e.printStackTrace();
				}
				
			}
		}

	@Test
	public void testUnrootedEdges() {
		File inputDir = new File(AMIFixtures.TEST_PHYLO_DIR, "problems1");
		for (String root :BAD_ROOTS) {
			try {
				MergeTipTest.testProblem00(inputDir, new File("target/phylo/problems1/"), root);
			} catch (Exception e) {
				LOG.debug("ERROR: "+e);
			}
		}
	}

	private void extractTreeNewickNexml(String baseFile, File pngFile) throws IOException, InterruptedException {
//		long millis = 30000; // 30 secs
		String baseName = FilenameUtils.getBaseName(pngFile.toString());
		String name = FilenameUtils.getName(pngFile.toString());
		File normaTempCTree = new File(baseFile+baseName+"/");
		File normaImageDir = new File(normaTempCTree, "image/");
		if (normaImageDir.exists() && new File(normaTempCTree, "log.xml").exists()) {
			LOG.debug(normaImageDir+" already exists");
			return;
		}
		LOG.debug("processing: "+normaImageDir);
		normaImageDir.mkdirs();
		File normaImage = new File(normaImageDir, name);
		FileUtils.copyFile(pngFile, normaImage);
		
		String cmd = "--ph.phylo -q "+normaTempCTree+
				" -i image/"+name+
				" --log"+
				" --ph.specpattern ijsemSpeciesEditor.xml"+
				" --ph.hocr.html image/"+baseName+".hocr.html"+
				" --ph.hocr.svg image/"+baseName+".hocr.svg"+
				" --ph.svg image/"+baseName+".svg"+
				" --ph.newick image/"+baseName+".nwk"+
				" --ph.nexml image/"+baseName+".nexml.xml"+
				" --ph.summarize"+
				"";
//		DefaultArgProcessor argProcessor = new PhyloTreeArgProcessor(cmd);
//		argProcessor.runAndOutput();
//		String cmd1 = PHYLO;
		String cmd1 = PHYLO+" "+cmd;
		String[] commands = cmd1.split("\\s+");
		runCommand(commands, 300, 100);  
	}

	private void runCommand(String[] commands, int maxTries, int deltaTime) throws IOException,
			InterruptedException {
		ProcessBuilder processBuilder = new ProcessBuilder(commands);
	    Process process = processBuilder.start();
        int exitValue = -1;
	    for (int i = 0; i < maxTries; i++) {
	        // test it every deltaTime milliseconds
	        Thread.sleep(deltaTime);
	        exitValue = -1;
	        try {
	            exitValue = process.exitValue();
	        } catch (IllegalThreadStateException e) {
	            // this is thrown if the process has not yet terminated
//	            System.out.println("step: "+i);
	            continue;
	        }
	        // process has exited
	        if (exitValue == 0) {
//	            System.out.println("final steps: "+i);
	            break;
	        }
	    }
	    if (exitValue != 1) {
	    	LOG.debug("failed to exit after "+(deltaTime+maxTries)+ " millis; terminated: ");
	    }
	    // destroy() process in all cases
	    process.destroy();
	}

	/** 
	 * development of new options in ami-phylo
	 * 
	 * @throws Exception
	 */
	@Test
	// LONG also bridges to norma
//	@Ignore("requires tesseract")
	public void testProcess140PngList() throws Exception {
		File pngDir = new File("../norma/peterijsem/sourceimages");
		Assert.assertTrue(""+pngDir, pngDir.exists());
		List<File> pngList = new ArrayList<File>(FileUtils.listFiles(pngDir, new String[]{"png"}, false));
		for (File pngFile : pngList) {
			try {
				extractTreeNewickNexml("target/phylo", pngFile);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}
	/** 
	 * development of new options in ami-phylo
	 * 
	 * @throws Exception
	 */
	@Test
	public void testProcess500List() throws Exception {
//		runBatch("../ijsem/500A/");
//		runBatch("../ijsem/500B/");
//		runBatch("../ijsem/500C/");
//		runBatch("../ijsem/500D/");
//		runBatch("../ijsem/500E/");
//		runBatch("../ijsem/500F/");
//		runBatch("../ijsem/500G/");
//		runBatch("../ijsem/500H/");
		runBatch("../ijsem/336J/");
	}

	private void runBatch(String dirName) {
		File pngDir = new File(dirName, "pngs/");
		Assert.assertTrue(""+pngDir, pngDir.exists());
		LOG.debug("pngDir "+pngDir.getAbsolutePath());
		List<File> pngList = new ArrayList<File>(FileUtils.listFiles(pngDir, new String[]{"png"}, false));
		for (File pngFile : pngList) {
			try {
				extractTreeNewickNexml(dirName, pngFile);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}

	@Test
	/** 
	 * development of new options in ami-phylo
	 * 
	 * @throws Exception
	 */
	// LONG
//	@Ignore("requires tesseract")
	public void testProcess15PngList() throws Exception {
		List<File> pngList = new ArrayList<File>(FileUtils.listFiles(new File(AMIFixtures.TEST_PHYLO_DIR, "15goodtree"), new String[]{"png"}, false));
		for (File pngFile : pngList) {
			extractTreeNewickNexml("target/phylo", pngFile);
		}
	}

	/** 
	 * development of new options in ami-phylo
	 * 
	 * @throws Exception
	 */
	@Test
	// LONG
//	@Ignore("requires tesseract")
	public void testProcess50PngList() throws Exception {
		File pngDir = new File(AMIFixtures.TEST_PHYLO_DIR, "50images/");
		List<File> pngList = new ArrayList<File>(FileUtils.listFiles(pngDir, new String[]{"png"}, false));
		for (File pngFile : pngList) {
			extractTreeNewickNexml("target/phylo", pngFile);
		}
	}
	
	@Test
	public void testProcess() throws IOException, InterruptedException {
		ProcessBuilder processBuilder = new ProcessBuilder("sleep", "1");
		Process process = processBuilder.start();
		for (int i = 0; i < 3000; i++) {
		    Thread.sleep(100); // 100 sec
			int exitValue = -1;
			try {
				exitValue = process.exitValue();
			} catch (IllegalThreadStateException e) {
				System.out.println("step: "+i);
				continue;
			}
		    if (exitValue == 0) {
		    	System.out.println("final steps: "+i);
		    	break;
		    }
		}
		process.destroy();	
	}
}