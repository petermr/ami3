package org.contentmine.eucl.euclid.pom;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.EuclidTestFixtures;
import org.contentmine.eucl.pom.MvnProject;
import org.contentmine.eucl.pom.Pom;
import org.junit.Assert;
import org.junit.Test;


public class PomTest {
	private static final Logger LOG = Logger.getLogger(PomTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testCreatePom() {
		File file = new File(EuclidTestFixtures.POM_DIR, "euclid/pom.xml");
		Pom pom = new Pom(file);
	}

	@Test
	public void testCreateMvnProject() {
		File file = new File(EuclidTestFixtures.POM_DIR, "euclid/pom.xml");
		Pom pom = new Pom(file);
		MvnProject project = pom.getMvnProject();
		Assert.assertEquals("org.contentmine/euclid/2.0.5", project.toString());
		List<MvnProject> dependencies = pom.getOrCreateDependencies();
		Assert.assertNotNull(dependencies);
		Assert.assertEquals("size "+dependencies.size(), 8, dependencies.size());
	}

	@Test
	public void testGetParentPom() {
		File file = new File(EuclidTestFixtures.POM_DIR, "euclid/pom.xml");
		Pom pom = new Pom(file);
		MvnProject parent = pom.getParentPom();
		Assert.assertEquals("org.contentmine/cm-parent/7.0.4", parent.toString());
	}

	@Test
	public void testGetDependency() {
		File file = new File(EuclidTestFixtures.POM_DIR, "svg/pom.xml");
		Pom pom = new Pom(file);
		List<MvnProject> dependencies = pom.getOrCreateDependencies();
		Assert.assertEquals(2,  dependencies.size());
		Assert.assertEquals("0", "org.contentmine/euclid/2.0.5", dependencies.get(0).toString());
		Assert.assertEquals("1", "org.imgscalr/imgscalr-lib/4.2", dependencies.get(1).toString());
		MvnProject dependency = pom.getDependency("org.contentmine", "euclid");
		Assert.assertNotNull("euclid", dependency);
		Assert.assertEquals("euclid version", "2.0.5", dependency.getVersion());
	}
	
	@Test
	public void testGetProperties() {
		File file = new File(EuclidTestFixtures.POM_DIR, "euclid/pom.xml");
		Pom pom = new Pom(file);
		Map<String, String> properties = pom.getOrCreateProperties();
		Assert.assertEquals(2,  properties.size());
	}

	@Test
	public void testGetProperty() {
		File file = new File(EuclidTestFixtures.POM_DIR, "euclid/pom.xml");
		Pom pom = new Pom(file);
		String value = pom.getValue("euclid.version");
		Assert.assertEquals("euclid.version",  "2.0.5", value);
	}
	

}
