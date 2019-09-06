package org.contentmine.cproject.files.schema;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.SimpleContainer;
import org.contentmine.cproject.files.schema.AbstractSchemaElement.IteratorType;
import org.contentmine.eucl.xml.XMLUtil;

import nu.xom.Element;

/** iterates over the schema for filenames.
 * This provides filenames against which the actual directory contents can be assessed.
 * 
 * @author pm286
 *
 */
public class FilenameSets  {
	private static final Logger LOG = Logger.getLogger(FilenameSets.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private IteratorType iteratorType;
	private List<Element> childElements;
	private Map<String, AbstractSchemaElement> schemaElementSetByName;
	private Set<AbstractSchemaElement> regexSchemaElementSet;
	private boolean checkTrees;
	private List<File> files;

	public FilenameSets(AbstractSchemaElement schemaElement, IteratorType iteratorType) {
		this.iteratorType = iteratorType;
		String typeName = this.iteratorType.getTypeName();
		childElements = XMLUtil.getQueryElements(schemaElement, "./*[local-name()='"+typeName+"']");
		regexSchemaElementSet = new HashSet<AbstractSchemaElement>();
		schemaElementSetByName = new HashMap<String, AbstractSchemaElement>();
		for (Element childElement : childElements) {
			AbstractSchemaElement childSchemaElement = (AbstractSchemaElement) childElement;
			if (childSchemaElement.getRegex() != null) {
				regexSchemaElementSet.add(childSchemaElement);
			} else if (childSchemaElement.getName() != null) {
				schemaElementSetByName.put(childSchemaElement.getName(), childSchemaElement);
			} else {
				throw new RuntimeException("Schema must have either regex or name: "+childSchemaElement.toString());
			}
		}
	}
	
	public void checkFiles(List<File> files) {
		checkFiles(files, false);
	}
		
	public void checkFiles(List<File> files, boolean checkTrees) {
		this.checkTrees = checkTrees;
		this.files = files;
		// check exact matches before regexes
		checkNames();
		checkRegex();
		LOG.trace("unmatched "+files);
	}

	private void checkNames() {
		for (String schemaFilename : schemaElementSetByName.keySet()) {
			AbstractSchemaElement schemaElement = schemaElementSetByName.get(schemaFilename);
			List<File> matchedFiles = checkAndRemoveMatchingFiles(schemaElement);
			if (matchedFiles.size() == 0) {
				LOG.trace("no files for: " + schemaElement.getLabel());
			} else {
				LOG.trace("Matched files "+ schemaElement.getLabel() + " ... " + matchedFiles);
			}
		}
	}
	
	private void checkRegex() {
		for (AbstractSchemaElement schemaElement : regexSchemaElementSet) {
			List<File> matchedFiles = checkAndRemoveMatchingFiles(schemaElement);
			if (matchedFiles.size() == 0) {
				LOG.trace("no regex for "+schemaElement.getRegex());
			} else {
				LOG.trace("Matched regexes "+ schemaElement.getLabel() + matchedFiles);
			}
		}
	}

	private List<File> checkAndRemoveMatchingFiles(AbstractSchemaElement schemaElement) {
		/** exactly one of schemaFileame or pattern should not be null
		 * 
		 */
		SchemaFileMatcher fileMatcher = new SchemaFileMatcher(schemaElement);
		List<File> matchedFiles = new ArrayList<File>();
		boolean change = true;
		while (change) {
			change = false;
			for (int i = 0; i < files.size(); i++) {
				File file = files.get(i);
				String filename = file.getName();
				if (fileMatcher.matches(filename)) {
					files.remove(i);
					matchedFiles.add(file);
					String schema = schemaElement.getSchema();
					if (schema != null && checkTrees) {
						LOG.trace("*******following: "+schema);
						ContainerCheck schemaCheck = new ContainerCheck(schema);
						schemaCheck.setCheckTrees(checkTrees);
						schemaCheck.checkProject(new SimpleContainer(file));
//						checkUnmatchedFiles();
						LOG.trace("*******end: "+schema);
					}
					change = true;
					break;
				}
			}
		}
		return matchedFiles;
	}

	private void checkUnmatchedFiles() {
		if (getFiles().size() != 0) {
			LOG.debug("unmatched files "+getFiles());
		}
	}

	public List<File> getFiles() {
		return files;
	}

}
