package org.contentmine.norma.biblio.json;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.cproject.args.DefaultArgProcessor;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.metadata.AbstractMetadata;
import org.contentmine.cproject.metadata.MetadataReader;
import org.contentmine.cproject.util.CMineUtil;
import org.contentmine.cproject.util.CellCalculator;
import org.contentmine.cproject.util.CellRenderer;
import org.contentmine.cproject.util.DataTablesTool;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.html.HtmlTd;
import org.contentmine.graphics.html.HtmlTr;
import org.contentmine.norma.NAConstants;
import org.contentmine.norma.biblio.EPMCResultsJsonEntry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;

import net.minidev.json.JSONArray;

public class EPMCConverter implements CellCalculator, MetadataReader {

	private static final Logger LOG = LogManager.getLogger(EPMCConverter.class);
public static final String HTTP_EUROPEPMC_ORG_ARTICLES = "http://europepmc.org/articles/";

	private InputStream jsonInputStream;
	private File cProjectDir;
	private JsonArray entryArray;
	private JsonElement rootJsonElement;
	private String currentId;
	private EPMCResultsJsonEntry currentResultJsonEntry;
	private CTree currentCTree;
	private List<EPMCResultsJsonEntry> jsonEntryList;
	public DataTablesTool dataTablesTool;
	
	public EPMCConverter() {
	}
	
	public EPMCConverter(File cProjectDir) {
		this();
		this.cProjectDir = cProjectDir;
	}

	public void createJsonEntryListAndPossiblyCProject() throws IOException {
		if (cProjectDir != null) {
			cProjectDir.mkdirs();
		}
		rootJsonElement = readJsonElementFromStream();
		if (rootJsonElement instanceof JsonArray) {
		    entryArray = rootJsonElement.getAsJsonArray();
		    getOrCreateJsonEntryList();
			for (int i = 0; i < entryArray.size(); i++) {
				JsonElement entry = entryArray.get(i);
				currentResultJsonEntry = new EPMCResultsJsonEntry(entry);
				currentId = createCurrentId(entry, currentResultJsonEntry);
				if (currentId != null) {
					createCurrentCTree();
					writeCurrentCTree(entry);
					getOrCreateDataTablesTool().getOrCreateRowHeadingList().add(currentId);
				}
				jsonEntryList.add(currentResultJsonEntry);
			}
		} else {
			LOG.trace("rootJsonElement is "+rootJsonElement.getClass());
		}
	}
	
	public List<EPMCResultsJsonEntry> getOrCreateJsonEntryList() {
		if (jsonEntryList == null) {
			jsonEntryList = new ArrayList<EPMCResultsJsonEntry>();
		}
		return jsonEntryList;
	}

	private JsonElement readJsonElementFromStream() throws IOException {
		if (jsonInputStream == null) {
			throw new RuntimeException("No EMPCJson file to convert");
		}
		
		String resultsJsonString = IOUtils.toString(jsonInputStream, CMineUtil.UTF8_CHARSET);
	    JsonParser parser = new JsonParser();
	    JsonElement jsonElement = parser.parse(resultsJsonString);
		return jsonElement;
	}

	public void readAndProcessEntry() throws IOException {
		JsonElement entry = readJsonElementFromStream();
		currentResultJsonEntry = new EPMCResultsJsonEntry(entry);
		currentId = createCurrentId(entry, currentResultJsonEntry);
		if (currentId != null) {
			createCurrentCTree();
			writeCurrentCTree(entry);
		}
	}

	private void writeCurrentCTree(JsonElement entry) {
		if (currentCTree != null && currentCTree.getDirectory() != null) {
			File entryFile = new File(currentCTree.getDirectory(), AbstractMetadata.Type.EPMC.getCProjectMDFilename());
			entry = stripOneElementArrays(entry);
			try {
				DefaultArgProcessor.CM_LOG.debug("wrote: "+entryFile);
				FileUtils.writeStringToFile(entryFile, entry.toString(), CMineUtil.UTF8_CHARSET);
			} catch (IOException e) {
				throw new RuntimeException("Cannot write "+entryFile);
			}
		}
	}

	private CTree createCurrentCTree() {
		if (cProjectDir != null) {
			File cTreeDir = new File(cProjectDir, currentId);
			cTreeDir.mkdirs();
			currentCTree = new CTree(cTreeDir);
		}
		return currentCTree;
	}

	private String createCurrentId(JsonElement entry, EPMCResultsJsonEntry resultJson) {
		String id = null;
		{
			id = resultJson.getPmcidText();
			if (id == null) {
				id = resultJson.getIdText();
			}
			if (id == null) {
				LOG.error("entry without ID: "+entry);
//				return null;
			}
		}
		return id;
	}
	
	private JsonElement stripOneElementArrays(JsonElement entry) {
		LOG.trace("strip one element arrays does not yet work");
		String json = entry.toString();
		String jsonPath = "$..source";//,$..id]";
		ReadContext ctx = JsonPath.parse(json);
		Object result = ctx.read(jsonPath);
		
		JSONArray jsonArray = (JSONArray) result;
		
		for (int i = 0; i < jsonArray.size(); i++) {
			Object object = jsonArray.get(i);
			if (object instanceof JSONArray) {
				JSONArray jsonArray1 = (JSONArray) object;
				if (jsonArray1.size() == 1) {
					Object object1 = jsonArray1.get(0);
					if (object1 instanceof String) {
						String s = object1.toString();
					} else {
						LOG.trace("unexpected object >CL>"+object1.getClass());
					}
				}
			}
		}
		return entry;
	}
	public void setCProjectDir(File cProjectDir) {
		this.cProjectDir = cProjectDir;
	}
	
	public void setJsonFile(File jsonFile) {
		try {
			this.jsonInputStream = new FileInputStream(jsonFile);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("File input stream not found: "+jsonFile);
		}
	}
	
	public void setJsonInputStream(InputStream jsonInputStream) {
		this.jsonInputStream = jsonInputStream;
	}
	
	public JsonArray getOrCreateEntryArray() {
		if (entryArray == null) {
			entryArray = new JsonArray();
		}
		return entryArray;
	}
	
	public JsonElement getJsonElement() {
		return rootJsonElement;
	}

	public CTree getCurrentCTree() {
		return currentCTree;
	}

	public void setDataTablesTool(DataTablesTool dataTablesTool) {
		this.dataTablesTool = dataTablesTool;
	}
	
	public DataTablesTool getOrCreateDataTablesTool() {
		if (dataTablesTool == null) {
			dataTablesTool = DataTablesTool.createBiblioEnabledTable();
		}
		return dataTablesTool;
	}
	
	public HtmlHtml createHtml() {
		getOrCreateDataTablesTool();
		dataTablesTool.setTitle("METADATA");
		dataTablesTool.setCellCalculator(this);
		this.setRemoteLink0("../../"+NAConstants.TEST_AMI_DIR+"/zika/");
		this.setRemoteLink1("/scholarly.html");
		this.setRowHeadingName("EPMCID");
		HtmlHtml html = dataTablesTool.createHtml(this);
		return html;
	}

	public void addCellValues(List<CellRenderer> columnHeadingList, HtmlTr htmlTr, int iRow) {
		EPMCResultsJsonEntry entry = jsonEntryList.get(iRow);
		List<HtmlElement> htmlElements = entry.createHtmlElements(columnHeadingList);
		for (int i = 0; i < htmlElements.size(); i++) {
			HtmlElement td = new HtmlTd();
			htmlTr.appendChild(td);
			HtmlElement s = htmlElements.get(i);
			td.appendChild(s);
		}
		
	}

	public HtmlElement createCellContents(int iRow, int iCol) {
		LOG.debug("createCellContents NYI");
		return null;
	}

	public CellCalculator setRemoteLink0(String link0) {
		getOrCreateDataTablesTool().setRemoteLink0(link0);
		return this;
	}

	public String getRemoteLink0() {
		return getOrCreateDataTablesTool().getRemoteLink0();
	}

	public CellCalculator setRemoteLink1(String link1) {
		getOrCreateDataTablesTool().setRemoteLink1(link1);
		return this;
	}

	public String getRemoteLink1() {
		return getOrCreateDataTablesTool().getRemoteLink1();
	}

	public CellCalculator setLocalLink0(String link0) {
		getOrCreateDataTablesTool().setLocalLink0(link0);
		return this;
	}

	public String getLocalLink0() {
		return getOrCreateDataTablesTool().getLocalLink0();
	}

	public CellCalculator setLocalLink1(String link1) {
		getOrCreateDataTablesTool().setLocalLink1(link1);
		return this;
	}

	public String getLocalLink1() {
		return getOrCreateDataTablesTool().getLocalLink1();
	}

	public CellCalculator setRowHeadingName(String rowHeading) {
		this.getOrCreateDataTablesTool().setRowHeadingName(rowHeading);
		return this;
	}

	public void setColumnHeadingList(List<CellRenderer> columnHeadingList) {
		this.getOrCreateDataTablesTool().setCellRendererList(columnHeadingList);
	}

	@Override
	public AbstractMetadata readEntry(File metadataFile) throws IOException {
		if (metadataFile == null) {
			throw new IllegalArgumentException("null file");
		}
		if (!metadataFile.exists() || metadataFile.isDirectory()) {
			throw new IOException("file must exist and be readable: "+metadataFile);
		}
		InputStream inputStream = new FileInputStream(metadataFile);
		AbstractMetadata entry = readEntry(inputStream);
		return entry;
	}

	@Override
	public AbstractMetadata readEntry(InputStream inputStream) throws IOException {
		this.setJsonInputStream(inputStream);
		rootJsonElement = readJsonElementFromStream();
//		LOG.debug(rootJsonElement);
		EPMCResultsJsonEntry metadataEntry = new EPMCResultsJsonEntry(rootJsonElement);
		metadataEntry.setJsonElement(rootJsonElement);
		return metadataEntry;
	}
	

}
