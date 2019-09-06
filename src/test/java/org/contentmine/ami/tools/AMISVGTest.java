package org.contentmine.ami.tools;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.AMISVGTool;
import org.contentmine.ami.tools.AMISVGTool.TidySVG;
import org.junit.Test;

/** test SVG.
 * 
 * @author pm286
 *
 */
public class AMISVGTest {
	private static final Logger LOG = Logger.getLogger(AMISVGTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	/** 
	 * convert single file
	 */
	public void testSVGTree() throws Exception {
		String args = ""
				+ "-t /Users/pm286/workspace/uclforest/forestplots/bowmann --pages 1 9";
		new AMISVGTool().runCommands(args);
	}
	
	@Test
	/** 
	 * convert single file
	 */
	public void testSVGTreeRegex() throws Exception {
		String args = ""
				+ "-t /Users/pm286/workspace/uclforest/forestplots/bowmann"
				+ ""
				+ " --regex"
				+ " 10 Hedge's\\s+g\\s+and\\s+95%\\s+CI"
				+ " 10 Hedge's(\\s+g)?"
				+ " 8  (control|treatment)\\s+group"
				+ " 8  sample(\\s+|\\-)size"
				+ " 10 statistics\\s+for\\s+each\\s+study"
				+ " 5  st(andar)?d\\s+diff\\s+in\\s+means"
				+ " 4  st(andar)d\\s+error"
				+ " 5  std\\.\\s+mean\\s+difference"
				+ " 8  correlation\\s+and\\s+95%\\s+CI"
				+ " 5  confidence\\sinterval"
				+ " 10 Forest\\s+plots?"
				+ " 10 favou?rs\\s+(control|intervention|experiment(al)?|treatment|A|B)"
				+ " 1  experimental"
				+ " 3  (lower|upper)\\s+limit"
				+ " 4  relative\\s+weight"
				+ " 4  study\\s+(name|size)s?"
				+ " 6  (weighted)?\\s+effect\\s+sizes?"
				+ " 3  (z|p)\\-value"
				+ " 1  control"
				+ " 1  random"
				+ " 1  variance"
				+ " 1  correlations?"
				+ " 1  measure"
				+ " 1  stud(y|ies)"
				+ " 6  LL"
				+ " 6  ES"
				+ " 6  UL"
				+ " 6  CI"
				+ " 1  effects?"
				+ " 1  weights?"
				+ " 1  sizes?"
				+ " 1  subgroups?"
				+ " 1  outcomes?"
				+ " 1  interventions?"
         ;
		new AMISVGTool().runCommands(args);
	}

	@Test
	/** 
	 * convert single file
	 */
	public void testSVGProjectRegex() throws Exception {
		String args = ""
				+ "-p /Users/pm286/workspace/uclforest/forestplots"
				+ ""
//				+ " --regexfile ${CM_AMI}/forest_regex.json"
				+ " --regexfile ${HOME}/ContentMine/ami/forest_regex.json"
         ;
		new AMISVGTool().runCommands(args);
	}
	

	@Test
	/** 
	 * convert s
	 */
	public void testSVGDiagrams() throws Exception {
		String args = ""
				+ "-t /Users/pm286/workspace/uclforest/forestplots/dietrichson"
				+ " --pages 21 22 23 26"
				+ " -vv"
				;
		new AMISVGTool().runCommands(args);
	}

	@Test
	/** 
	 * convert single file
	 */
	public void testSVGProjectRegex1() throws Exception {
		String args = ""
				+ "-p /Users/pm286/workspace/uclforest/dev/"
				+ " --logfile regex.log "
				+ " --regex"
				+ " 10 Hedge's\\s+g\\s+and\\s+95%\\s+CI"
				+ " 10 Hedge's(\\s+g)?"
				+ " 8  (control|treatment)\\s+group"
				+ " 8  sample(\\s+|\\-)size"
				+ " 10 statistics\\s+for\\s+each\\s+study"
				+ " 5  st(andar)?d\\s+diff\\s+in\\s+means"
				+ " 4  st(andar)d\\s+error"
				+ " 5  std\\.\\s+mean\\s+difference"
				+ " 8  correlation\\s+and\\s+95%\\s+CI"
				+ " 5  confidence\\sinterval"
				+ " 10 Forest\\s+plots?"
				+ " 10 favou?rs\\s+(control|intervention|experiment(al)?|treatment|A|B)"
//				+ " 1  experimental"
				+ " 3  (lower|upper)\\s+limit"
				+ " 4  relative\\s+weight"
				+ " 4  study\\s+(name|size)s?"
				+ " 6  (weighted)?\\s+effect\\s+sizes?"
				+ " 3  (z|p)\\-value"
//				+ " 1  control"
//				+ " 1  random"
//				+ " 1  variance"
//				+ " 1  correlations?"
//				+ " 1  measure"
//				+ " 1  stud(y|ies)"
				+ " 6  LL"
				+ " 6  ES"
				+ " 6  UL"
				+ " 6  CI"
//				+ " 1  effects?"
//				+ " 1  weights?"
//				+ " 1  sizes?"
//				+ " 1  subgroups?"
//              + " 1  outcomes?"
//				+ " 1  interventions?"
         ;
		new AMISVGTool().runCommands(args);
	}

	@Test
	/** 
	 * convert single file
	 */
	public void testSVGProjectVectors() throws Exception {
		String args = ""
				+ "-p /Users/pm286/workspace/uclforest/dev/"
				+ " --vectors vectors.log"
				;
		new AMISVGTool().runCommands(args);
	}
	
	@Test
	/** 
	 * convert single file
	 */
	public void testSVGProjectVectorsPages() throws Exception {
		String args = ""
				+ "-p /Users/pm286/workspace/uclforest/dev/"
				+ " --tidysvg nullmove emptypath nomove"
				+ " --vectorlog vectors.log"
				+ " --vectordir vectors/"
				;
		new AMISVGTool().runCommands(args);
	}
	
	@Test
	/** 
	 * make caches
	 */
	public void testCachesTree() throws Exception {
		String args = ""
				+ "-t /Users/pm286/workspace/uclforest/dev/bowmann"
				+ " --caches text line"
				;
		new AMISVGTool().runCommands(args);
	}
	
	@Test
	/** 
	 * make caches
	 */
	public void testCaches() throws Exception {
		String args = ""
				+ "-p /Users/pm286/workspace/uclforest/dev"
				+ " --caches text line rect"
				;
		new AMISVGTool().runCommands(args);
	}
	
}
