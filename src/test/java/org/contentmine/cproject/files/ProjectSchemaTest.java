package org.contentmine.cproject.files;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.CMineFixtures;
import org.contentmine.cproject.args.DefaultArgProcessor;
import org.contentmine.cproject.files.schema.AbstractSchemaElement;
import org.contentmine.cproject.files.schema.CProjectSchema;
import org.contentmine.cproject.files.schema.ContainerCheck;
import org.contentmine.eucl.xml.XMLUtil;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/** tests project schema
 * 
 * @author pm286
 *
 */
public class ProjectSchemaTest {
	private static final Logger LOG = Logger.getLogger(ProjectSchemaTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void dummy() {
		LOG.warn("Tests not working yet");
	}
	
	@Test
//	@Ignore
	public void testProjectSchema() {
		String name = DefaultArgProcessor.SCHEMA_TOP + "/" + AbstractSchemaElement.C_PROJECT_TEMPLATE_XML;
		InputStream is = this.getClass().getResourceAsStream(name);
		Assert.assertNotNull("stream", is);
		AbstractSchemaElement schemaElement = AbstractSchemaElement.create(XMLUtil.parseQuietlyToRootElement(is));
		Assert.assertNotNull(schemaElement);
	}
	
	@Test
//	@Ignore
	public void testProjectSchemaCheck() {
		String name = DefaultArgProcessor.SCHEMA_TOP + "/" + AbstractSchemaElement.C_PROJECT_TEMPLATE_XML;
		InputStream is = this.getClass().getResourceAsStream(name);
		AbstractSchemaElement projectSchema = (AbstractSchemaElement) CProjectSchema.create(XMLUtil.parseQuietlyToRootElement(is));
		File cProjectFile = new File(CMineFixtures.TEST_PROJECTS_DIR, "project2/");
		Assert.assertTrue("file exists", cProjectFile.exists());
		ContainerCheck projectCheck = new ContainerCheck(projectSchema);
		projectCheck.checkProject(new CProject(cProjectFile));
		Assert.assertEquals("unchecked files", 0, projectCheck.getTotalUncheckedFiles().size());
	}
	
	@Test
//	@Ignore
	public void testProjectWithResultsSchemaCheck() {
		File cProjectFile = new File(CMineFixtures.TEST_PROJECTS_DIR, "treesWithResults/");
		Assert.assertTrue("file exists", cProjectFile.exists());
		ContainerCheck projectCheck = new ContainerCheck();
		projectCheck.checkProject(new CProject(cProjectFile));
		Assert.assertEquals("unchecked files", 0, projectCheck.getTotalUncheckedFiles().size());
	}
	
	@Test
	@Ignore
	public void testProjectAndCTrees() {
		File cProjectFile = new File(CMineFixtures.TEST_PROJECTS_DIR, "treesWithResults/");
		Assert.assertTrue("file exists", cProjectFile.exists());
		ContainerCheck projectCheck = new ContainerCheck();
		projectCheck.setCheckTrees(true);
		projectCheck.checkProject(new CProject(cProjectFile));
		Assert.assertEquals("unchecked files", 0, projectCheck.getTotalUncheckedFiles().size());
	}
	
	@Test
	@Ignore
	public void testCooccurrence() {
		File cProjectFile = new File(CMineFixtures.TEST_PROJECTS_DIR, "cooc/");
		Assert.assertTrue("file exists", cProjectFile.exists());
		ContainerCheck projectCheck = new ContainerCheck();
		projectCheck.setCheckTrees(true);
		projectCheck.checkProject(new CProject(cProjectFile));
		List<File> uncheckedFiles = projectCheck.getTotalUncheckedFiles();
		Assert.assertEquals("unchecked files", 1, uncheckedFiles.size());
		Assert.assertEquals("unchecked", "src/test/resources/org/contentmine/cproject/files/projects/cooc/unexpectedfile.txt",
				uncheckedFiles.get(0).toString());
	}
	
	/** simple CTrees with common children
	 * 
	 */
	@Test
	@Ignore
	public void testDoiNames() {
		File cProjectFile = new File(CMineFixtures.TEST_PROJECTS_DIR, "doiNames/");
		Assert.assertTrue("file exists", cProjectFile.exists());
		ContainerCheck projectCheck = new ContainerCheck();
		projectCheck.setCheckTrees(true);
		projectCheck.checkProject(new CProject(cProjectFile));
		List<File> uncheckedFiles = projectCheck.getTotalUncheckedFiles();
		Assert.assertEquals("unchecked files", 0, uncheckedFiles.size());
	}
	
	/** simple CTrees with common children and some CTree siblings
	 * 
	 */
	@Test
	@Ignore
	public void testProject2() {
		File cProjectFile = new File(CMineFixtures.TEST_PROJECTS_DIR, "project2/");
		Assert.assertTrue("file exists", cProjectFile.exists());
		ContainerCheck projectCheck = new ContainerCheck();
		projectCheck.setCheckTrees(true);
		projectCheck.checkProject(new CProject(cProjectFile));
		List<File> uncheckedFiles = projectCheck.getTotalUncheckedFiles();
		Assert.assertEquals("unchecked files", 0, uncheckedFiles.size());
	}
	
	/** CTrees with results added by AMI
	 * 
	 */
	@Test
	@Ignore
	public void testCTreesWithResults() {
		File cProjectFile = new File(CMineFixtures.TEST_PROJECTS_DIR, "treesWithResults/");
		Assert.assertTrue("file exists", cProjectFile.exists());
		ContainerCheck projectCheck = new ContainerCheck();
		projectCheck.setCheckTrees(true);
		projectCheck.checkProject(new CProject(cProjectFile));
		List<File> uncheckedFiles = projectCheck.getTotalUncheckedFiles();
		Assert.assertEquals("unchecked files", 0, uncheckedFiles.size());
	}
	
	/** simple CTrees with common children and some CTree siblings
	 * 
	 */
	@Test
	@Ignore
	public void testCTreesWithSVGImages() {
		File cProjectFile = new File(CMineFixtures.TEST_PROJECTS_DIR, "treesWithSvgImages/");
		Assert.assertTrue("file exists", cProjectFile.exists());
		ContainerCheck projectCheck = new ContainerCheck();
		projectCheck.setCheckTrees(true);
		projectCheck.checkProject(new CProject(cProjectFile));
		List<File> uncheckedFiles = projectCheck.getTotalUncheckedFiles();
//		Assert.assertEquals("unchecked files", 0, uncheckedFiles.size());
	}
}
