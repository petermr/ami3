package org.contentmine.cproject.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.ProjectSnippetsTree;
import org.contentmine.eucl.xml.XMLUtil;

import nu.xom.Element;

public class CMineTestFixtures {

	private static final Logger LOG = Logger.getLogger(CMineTestFixtures.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	/** copies source onto target after cleaning
	 * if copy fails, writes error message and returns false
	 * 
	 * recommended
	 *   if (!CmineTestFixtures.cleanAndCopyDir(s, t) {
	 *       abort / return
	 *   }
	 * 
	 * @param sourceDir
	 * @param targetDir
	 * @return
	 */
	public static boolean cleanAndCopyDir(File sourceDir, File targetDir) {
		try {
			if (!sourceDir.exists() || !sourceDir.isDirectory()) {
				throw new RuntimeException("sourceDir does not exist: "+sourceDir);
			}
			if (sourceDir.equals(targetDir)) {
				LOG.error("Target and source are identical; Cannot delete");
				return false;
			}
			if (targetDir.exists()) FileUtils.forceDelete(targetDir);
			LOG.trace(sourceDir.getAbsolutePath());
			
			FileUtils.copyDirectory(sourceDir, targetDir);
			return true;
		} catch (IOException ioe) {
			LOG.error("failed to clean and copy: "+sourceDir+" @ "+targetDir +": "+ioe, ioe);
			return false;
		}
	}


	public static ProjectSnippetsTree createProjectSnippetsTree(File testZikaFile, String snippetsName) throws IOException {
		File targetDir = createCleanedCopiedDirectory(testZikaFile, new File("target/relevance/zika"));
		Element snippetsTreeXML = XMLUtil.parseQuietlyToDocument(new File(targetDir, snippetsName)).getRootElement();;
		ProjectSnippetsTree projectsSnippetsTree = ProjectSnippetsTree.createProjectSnippetsTree(snippetsTreeXML);
		return projectsSnippetsTree;
	}


	public static File createCleanedCopiedDirectory(File testZikaFile, File targetDir) {
		CMineTestFixtures.cleanAndCopyDir(testZikaFile, targetDir);
		return targetDir;
	}




}
