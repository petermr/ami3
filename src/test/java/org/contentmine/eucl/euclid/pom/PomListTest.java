package org.contentmine.eucl.euclid.pom;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.EuclidTestFixtures;
import org.contentmine.eucl.pom.Dependency;
import org.contentmine.eucl.pom.Pom;
import org.contentmine.eucl.pom.PomList;
import org.junit.Assert;
import org.junit.Test;

public class PomListTest {
	public static final Logger LOG = Logger.getLogger(PomListTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public final static List<String> PROJECTS = Arrays.asList(new String[] {
			"cm-pom", "euclid", "svg", "html", "imageanalysis", "diagramanalyzer",
			"pdf2svg", "svg2xml", "cproject", "norma", "ami"});

	@Test
	public void testAllPomList() {
		PomList pomList = new PomList(EuclidTestFixtures.POM_DIR, PROJECTS);
		List<Pom> poms = pomList.getOrCreatePoms();
		Assert.assertEquals(11,  poms.size());
	
	}

	@Test
	public void testGetDependencies() {
		PomList pomList = new PomList(EuclidTestFixtures.POM_DIR, PROJECTS);
		List<Dependency> dependencyList = pomList.findDependencies();
		Assert.assertEquals(12,  dependencyList.size());
	
	}
	
	@Test
	public void testDotty() throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("digraph "+EuclidTestFixtures.POM_DIR+" {\n");
		PomList pomList = new PomList(EuclidTestFixtures.POM_DIR, PROJECTS);
		List<String> stringList = pomList.getDotty();
		for (String dot : stringList) {
			sb.append(dot+"\n");
		}
		sb.append("}\n");
		FileUtils.write(new File("target/dotty/poms.dot"), sb.toString());
	}
	
	@Test
	public void testDotty1() throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("digraph "+EuclidTestFixtures.POM_DIR+" {\n");
		PomList pomList = new PomList(EuclidTestFixtures.POM_DIR, PROJECTS);
		String dotty = pomList.createDottyString("cmpom");
		FileUtils.write(new File("target/dotty/poms.dot"), dotty);
	}
	
	
	

}
