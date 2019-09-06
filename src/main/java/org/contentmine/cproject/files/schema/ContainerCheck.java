package org.contentmine.cproject.files.schema;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.args.DefaultArgProcessor;
import org.contentmine.cproject.files.CContainer;
import org.contentmine.cproject.files.CContainerTraverser;
import org.contentmine.eucl.xml.XMLUtil;

/** checks content of CProject against schema
 * 
 * @author pm286
 *
 */
public class ContainerCheck {
	private static final Logger LOG = Logger.getLogger(ContainerCheck.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private AbstractSchemaElement schema;
	private CContainerTraverser containerTraverser;
	private List<File> sortedDirectories;
	private List<File> sortedFiles;
	private List<File> totalUncheckedFiles;
	private boolean checkTrees;

	public ContainerCheck() {
		AbstractSchemaElement projectSchema = getDefaultProjectSchema();
		setProjectSchema(projectSchema);
		init();
	}

	public ContainerCheck(String schemaString) {
		InputStream is = this.getClass().getResourceAsStream(
				DefaultArgProcessor.SCHEMA_TOP + "/" + schemaString);
		setProjectSchema(schemaString, is);
		init();
	}

	private void init() {
		totalUncheckedFiles = new ArrayList<File>();
	}

	private AbstractSchemaElement getDefaultProjectSchema() {
		String schemaTemplate = DefaultArgProcessor.SCHEMA_TOP + "/" + AbstractSchemaElement.C_PROJECT_TEMPLATE_XML;
		InputStream is = this.getClass().getResourceAsStream(
				schemaTemplate);
		if (is == null) {
			throw new RuntimeException("cannot find/load schema from" + schemaTemplate);
		}
		AbstractSchemaElement projectSchema = 
				(AbstractSchemaElement) CProjectSchema.create(XMLUtil.parseQuietlyToRootElement(is));
		return projectSchema;
	}


	private void setProjectSchema(String schemaString, InputStream is) {
		if (is == null) {
			throw new RuntimeException("cannot load schema: "+schemaString);
		}
		AbstractSchemaElement schema1 = 
				(AbstractSchemaElement) CProjectSchema.create(XMLUtil.parseQuietlyToRootElement(is));
		setProjectSchema(schema1);
	}
	
	public ContainerCheck(AbstractSchemaElement projectSchema) {
		setProjectSchema(projectSchema);
	}
	
	public void setProjectSchema(AbstractSchemaElement projectSchema) {
		this.schema = projectSchema;
	}
	
	public void checkProject(CContainer container) {
		containerTraverser = new CContainerTraverser(container);
		checkDirectoriesAndFilesAgainstSchema();
	}

	private void checkDirectoriesAndFilesAgainstSchema() {
		List<File> uncheckedDirectories = checkDirectories();
		List<File> uncheckedFiles = checkFiles();
		ensureTotalUncheckedFiles();
		totalUncheckedFiles.addAll(uncheckedDirectories);
		totalUncheckedFiles.addAll(uncheckedFiles);
	}

	private void ensureTotalUncheckedFiles() {
		if (totalUncheckedFiles == null) {
			totalUncheckedFiles = new ArrayList<File>();
		}
	}

	private List<File> checkDirectories() {
		sortedDirectories = containerTraverser.getOrCreateSortedDirectoryList();
		FilenameSets dirnameSets = schema.getDirnameSets();
		dirnameSets.checkFiles(sortedDirectories, checkTrees);
		return sortedDirectories;
	}

	private List<File> checkFiles() {
		sortedFiles = containerTraverser.getOrCreateSortedFileList();
		FilenameSets filenameSets = schema.getFilenameSets();
		filenameSets.checkFiles(sortedFiles);
		return sortedFiles;
		
	}

	public List<File> getTotalUncheckedFiles() {
		return totalUncheckedFiles;
	}

	public void setCheckTrees(boolean b) {
		this.checkTrees = b;
	}

}
