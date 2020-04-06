package org.contentmine.norma.pdf;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class GrobidRunnerTest {

	@Test
	public void testExeOption() {
		try {
			GrobidRunner.ExeOption.getOption("xxx");
			fail("Expected exception");
		} catch (IllegalArgumentException ex) {
			assertEquals("Invalid option: 'xxx'. Valid values are: " + Arrays.toString(GrobidRunner.ExeOption.values()), ex.getMessage());
		}
	}

	@Test
	public void testOptionsDefaultValues() {
		GrobidRunner.GrobidOptions options = new GrobidRunner.GrobidOptions();
		assertEquals(System.getProperty("java.home") + "/bin/java", options.getJavaProgram());
		assertEquals(GrobidRunner.GrobidOptions.GROBID_DEFAULT_VERSION, options.getGrobidVersion());
		assertEquals(System.getProperty("user.home") + "/workspace/grobid/grobid-" + options.getGrobidVersion(), options.getGrobidInstallLocation());
		assertEquals(options.getGrobidInstallLocation() + "/grobid-home", options.getGrobidHome());
		assertEquals(String.format("%s/grobid-core/build/libs/grobid-core-%s-onejar.jar",
				options.getGrobidInstallLocation(), options.getGrobidVersion()), options.getGrobidJarPath());
	}

	@Test
	public void testOptionsVersion() {
		GrobidRunner.GrobidOptions options = new GrobidRunner.GrobidOptions();
		options.grobidVersion = "1.2.3";
		assertEquals(System.getProperty("java.home") + "/bin/java", options.getJavaProgram());
		assertEquals("1.2.3", options.getGrobidVersion());
		assertEquals(System.getProperty("user.home") + "/workspace/grobid/grobid-" + options.getGrobidVersion(), options.getGrobidInstallLocation());
		assertEquals(options.getGrobidInstallLocation() + "/grobid-home", options.getGrobidHome());
		assertEquals(String.format("%s/grobid-core/build/libs/grobid-core-%s-onejar.jar",
				options.getGrobidInstallLocation(), options.getGrobidVersion()), options.getGrobidJarPath());
	}

	@Test
	public void testOptionsInstallDir() {
		GrobidRunner.GrobidOptions options = new GrobidRunner.GrobidOptions();
		options.grobidInstallLocation = "/a/b/c";

		assertEquals(System.getProperty("java.home") + "/bin/java", options.getJavaProgram());
		assertEquals("/a/b/c", options.getGrobidInstallLocation());
		assertEquals(options.getGrobidInstallLocation() + "/grobid-home", options.getGrobidHome());
		assertEquals(String.format("%s/grobid-core/build/libs/grobid-core-%s-onejar.jar",
				options.getGrobidInstallLocation(), options.getGrobidVersion()), options.getGrobidJarPath());
	}

	@Test
	public void testOptionsGrobidHome() {
		GrobidRunner.GrobidOptions options = new GrobidRunner.GrobidOptions();
		options.grobidHomeLocation = "/a/b/c";

		assertEquals(System.getProperty("java.home") + "/bin/java", options.getJavaProgram());
		assertEquals(System.getProperty("user.home") + "/workspace/grobid/grobid-" + options.getGrobidVersion(), options.getGrobidInstallLocation());
		assertEquals("/a/b/c", options.getGrobidHome());
		assertEquals(String.format("%s/grobid-core/build/libs/grobid-core-%s-onejar.jar",
				options.getGrobidInstallLocation(), options.getGrobidVersion()), options.getGrobidJarPath());
	}

	@Test
	public void testOptionsGrobidJar() {
		GrobidRunner.GrobidOptions options = new GrobidRunner.GrobidOptions();
		options.grobidJarLocation = "/a/b/c";

		assertEquals(System.getProperty("java.home") + "/bin/java", options.getJavaProgram());
		assertEquals(System.getProperty("user.home") + "/workspace/grobid/grobid-" + options.getGrobidVersion(), options.getGrobidInstallLocation());
		assertEquals(options.getGrobidInstallLocation() + "/grobid-home", options.getGrobidHome());
		assertEquals("/a/b/c", options.getGrobidJarPath());
	}
}