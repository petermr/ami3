package org.contentmine.eucl.pom;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/** list of Poms.
 * 
 * @author pm286
 *
 */
public class PomList {
	private static final Logger LOG = Logger.getLogger(PomList.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	private final static File NULL = new File("null");


	private List<String> projectNames;
	private File directory;
	private List<Pom> pomList;
	private List<Dependency> dependencyList;
	private List<File> pomFiles;

	public PomList() {
	}
	
	public PomList(List<File> pomFiles) {
		this.pomFiles = pomFiles;
		directory = NULL;
		for (File pomFile : pomFiles) {
			File parentFile = pomFile.getParentFile();
			if (NULL.equals(directory)) {
				directory = parentFile;
			} else if (!parentFile.equals(directory)) {
				directory = null;
			}
		}
	}
	
	public PomList(File directory, List<String> projectNames) {
		setProjectNames(projectNames);
		setDirectory(directory);
		getOrCreatePoms();
	}

	private void setDirectory(File directory) {
		this.directory = directory;
	}

	public void setProjectNames(List<String> projectNames) {
		this.projectNames = projectNames;
	}
	
	public List<Pom> getOrCreatePoms() {
		pomList = new ArrayList<Pom>();
		if (directory != null) {
			extractPomsFromDirectory();
		} else {
			for (File pomFile : pomFiles) {
				Pom pom = new Pom(pomFile);
				pomList.add(pom);
			}
		}
		return pomList;
	}

	private void extractPomsFromDirectory() {
		if (!directory.isDirectory()) {
			LOG.warn("cannot find directory: "+directory.getAbsolutePath());
		} else if (projectNames != null) {
			for (String projectName : projectNames) {
				File directory1 = new File(directory, projectName);
				File pomFile = null;
				try {
					pomFile = new File(directory1, "pom.xml").getCanonicalFile();
				} catch (IOException e) {
					LOG.warn("file does not exist "+pomFile, e);
					break;
				}
				if (!pomFile.exists()) {
					LOG.warn("unknown file: "+pomFile);
				} else if (pomFile.isDirectory()) {
					LOG.warn("file is directory: "+pomFile);
				} else {
					Pom pom = new Pom(pomFile);
					pomList.add(pom);
				}
			}
		}
	}
	
	public Pom getPom(MvnProject project) {
		Pom pom = null;
		if (project != null) {
			for (Pom pom1 : pomList) {
				if (project.equals(pom1.getMvnProject())) {
					pom = pom1;
					break;
				}
			}
		}
		return pom;
	}

	public boolean contains(MvnProject dependency) {
		for (Pom pom : pomList) {
			if (pom.getMvnProject().equals(dependency)) {
				return true;
			}
		}
		return false;
	}

	public List<Dependency> findDependencies() {
		dependencyList = new ArrayList<Dependency>();
		for (Pom pom : pomList) {
			for (MvnProject project : pom.getOrCreateDependencies()) {
				if (contains(project)) {
					Dependency dependency = new Dependency(pom, project);
					LOG.trace(""+dependency);
					dependencyList.add(dependency);
				}
			}
		}
		return dependencyList;
	}

	/**
digraph prenorma {
"pdf" [label="pdf", style="filled", color="yellow"]
"png" [label="png", style="filled", color="yellow"]
"svg" [label="svg", style="filled", color="yellow"]

"pdf.svg" [label="pdf2svg-output"];
"svg2xml.svg" [label="svg2xml SVG"];
"fulltext.html" [label="structured-HTML", style="filled", color="pink"];
"diagrams.svg" [label="structured SVG diagrams", style="filled", color="pink"];

"png.pixels" [label="pixels"];

"pdf" -> "pdf.svg" [label="pdf2svg"];
"pdf.svg" -> "svg2xml.svg" [label="svg2xml"];
"pdf.svg" -> "fulltext.html" [label="svg2xml"];

"png" -> "png.pixels" [label="imageanal"];
"png.pixels" -> "png.pixels.text.svg"  [label="javaocr"]
"png" -> "png.pixels.text.svg"  [label="tesseract"]
"png.pixels.text.svg" -> "diagrams.svg" [label="diagramanal"]
"png.pixels" -> "diagrams.svg"  [label="diagramanal"]
// "png.diagrams.svg" -> "diagrams.svg" [label="svgbuilder"]
"svg2xml.svg" -> "diagrams.svg" [label="svgbuilder"]
"svg" -> "diagrams.svg"  [label="svgbuilder"]
}
	 * 
	 * @return
	 */
	public List<String> getDotty() {
		List<String> dotty = new ArrayList<String>();
		findDependencies();
		for (Dependency dependency : dependencyList) {
			dotty.add(dependency.getDot());
		}
		return dotty;
	}

	public String createDottyString(String title) {
		StringBuilder sb = new StringBuilder();
		sb.append("digraph "+title+" {\n");
		List<String> stringList = getDotty();
		for (String dot : stringList) {
			sb.append(dot+"\n");
		}
		sb.append("}\n");
		return sb.toString();
	}

	public int size() {
		getOrCreatePoms();
		return pomList.size();
	}
	
}
