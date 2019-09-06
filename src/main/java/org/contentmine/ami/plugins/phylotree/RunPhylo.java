package org.contentmine.ami.plugins.phylotree;

import java.io.File;

public class RunPhylo {

	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			System.err.println(""
					+ "RunPhylo infile outdir\n"
					+ "   if infile is png convert it into outdir\n"
					+ "      e.g foo/bar.png target/test"
					+ "   if infile is directory, convert all files into subdirs of outdir\n"
					+ "      e.g. NAConstants.TEST_AMI_DIR+\"/phylo/15goodtree target/phylo/test1");
			
		} else {
			// args0 = input file
			// args1 = output dir
			PhyloTreeArgProcessor.convertPngToHTML_SVG_NEXML_NWK(new File(args[0]), new File(args[1]));
		}
	}
}
