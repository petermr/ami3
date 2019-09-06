package org.contentmine.cproject.files;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.CHESConstants;
import org.contentmine.cproject.CMineFixtures;
import org.junit.Ignore;
import org.junit.Test;

import junit.framework.Assert;

/** filter filepaths by regex
 * 
 * @author pm286
 *
 */
public class RegexPathFilterTest {
	private static final Logger LOG = Logger.getLogger(RegexPathFilterTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testRegexPathFilterCrude() {
		RegexPathFilter regexPathFilter = new RegexPathFilter(".*project.*");
		List<File> files = new ArrayList<File>(FileUtils.listFiles(CMineFixtures.TEST_PROJECTS_DIR, regexPathFilter, TrueFileFilter.TRUE));
		Assert.assertTrue("files "+files.size(),  files.size() > 150);
	}
	
	@Test
	public void testRegexPathFilterCtreeDescendantFiles() {
		RegexPathFilter regexPathFilter = new RegexPathFilter(".*project.*/ctree1/.*");
		List<File> files = new ArrayList<File>(FileUtils.listFiles(CMineFixtures.TEST_PROJECTS_DIR, regexPathFilter, TrueFileFilter.TRUE));
		Collections.sort(files);
		Assert.assertEquals("files",  6, files.size());
		Assert.assertEquals("["
				+ CHESConstants.SRC_TEST_CPROJECT_TOP+"/files/projects/project1/ctree1/fulltext.html,"
				+ " "+CHESConstants.SRC_TEST_CPROJECT_TOP+"/files/projects/project3/ctree1/fulltext.pdf,"
				+ " "+CHESConstants.SRC_TEST_CPROJECT_TOP+"/files/projects/project3/ctree1/fulltext.xml,"
				+ " "+CHESConstants.SRC_TEST_CPROJECT_TOP+"/files/projects/project3/ctree1/results/sequence/dnaprimer/results.xml,"
				+ " "+CHESConstants.SRC_TEST_CPROJECT_TOP+"/files/projects/project3/ctree1/results/word/frequencies/results.html,"
				+ " "+CHESConstants.SRC_TEST_CPROJECT_TOP+"/files/projects/project3/ctree1/results/word/frequencies/results.xml]",
				files.toString());
	}
	
	@Test
	public void testRegexPathFilterDirectoriesPartialPath() {
		RegexPathFilter regexPathFilter = new RegexPathFilter(".*project.*/ctree1.*");
		List<File> files = regexPathFilter.listDirectoriesRecursively(CMineFixtures.TEST_PROJECTS_DIR);
		Collections.sort(files);
		Assert.assertEquals("files",  7, files.size());
		Assert.assertEquals("["
				+ CHESConstants.SRC_TEST_CPROJECT_TOP+"/files/projects/project1/ctree1,"
				+ " "+CHESConstants.SRC_TEST_CPROJECT_TOP+"/files/projects/project3/ctree1,"
				+ " "+CHESConstants.SRC_TEST_CPROJECT_TOP+"/files/projects/project3/ctree1/results,"
				+ " "+CHESConstants.SRC_TEST_CPROJECT_TOP+"/files/projects/project3/ctree1/results/sequence,"
				+ " "+CHESConstants.SRC_TEST_CPROJECT_TOP+"/files/projects/project3/ctree1/results/sequence/dnaprimer,"
				+ " "+CHESConstants.SRC_TEST_CPROJECT_TOP+"/files/projects/project3/ctree1/results/word,"
				+ " "+CHESConstants.SRC_TEST_CPROJECT_TOP+"/files/projects/project3/ctree1/results/word/frequencies"
				+ "]",
				files.toString());
	}
	
	@Test
	public void testRegexPathFilterDirectoriesEndPath() {
		RegexPathFilter regexPathFilter = new RegexPathFilter(".*project.*/ctree1");
		List<File> files = regexPathFilter.listDirectoriesRecursively(CMineFixtures.TEST_PROJECTS_DIR);
		Collections.sort(files);
		Assert.assertEquals("files", 2, files.size());
		Assert.assertEquals("["
				+ CHESConstants.SRC_TEST_CPROJECT_TOP+"/files/projects/project1/ctree1,"
				+ " "+CHESConstants.SRC_TEST_CPROJECT_TOP+"/files/projects/project3/ctree1"
				+ "]",
				files.toString());
	}
	
	@Test
	public void testRegexPathFilterDirectories() {
		RegexPathFilter regexPathFilter = new RegexPathFilter(".*project.*/ctree$");
		List<File> files = new ArrayList<File>(FileUtils.listFilesAndDirs(CMineFixtures.TEST_PROJECTS_DIR, 
				TrueFileFilter.TRUE, 
				TrueFileFilter.TRUE));
		Collections.sort(files);
		Assert.assertTrue("files "+files.size(),  files.size() > 300);
//		LOG.debug(files);
	}
	
	@Test
	@Ignore // files may be machine-dependent
	public void testRegexPathFilter() {
		RegexPathFilter regexPathFilter = new RegexPathFilter(".*project.*/cree1$");
		List<File> files = new ArrayList<File>(FileUtils.listFiles(CMineFixtures.TEST_PROJECTS_DIR, 
				regexPathFilter, 
				TrueFileFilter.TRUE));
		Collections.sort(files);
		Assert.assertEquals("files",  149, files.size());
//		LOG.debug(files);
	}
	
}
