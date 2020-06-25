package org.contentmine.ami.plugins;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.cproject.files.PluginOption;
import org.contentmine.cproject.files.ProjectSnippetsTree;
import org.contentmine.cproject.files.ResultElement;
import org.contentmine.cproject.files.SnippetsTree;
import org.contentmine.cproject.files.XMLSnippets;
import org.contentmine.cproject.util.CMineUtil;
import org.contentmine.cproject.util.CellCalculator;
import org.contentmine.cproject.util.CellRenderer;
import org.contentmine.cproject.util.DataTablesTool;
import org.contentmine.cproject.util.ResultsAnalysis;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlBr;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlSpan;
import org.contentmine.graphics.html.HtmlTable;
import org.contentmine.graphics.html.HtmlTd;
import org.contentmine.graphics.html.HtmlTr;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

import nu.xom.Element;

public class ResultsAnalysisImpl implements CellCalculator, ResultsAnalysis {

	private static final Logger LOG = LogManager.getLogger(ResultsAnalysisImpl.class);

public Map<String, ProjectSnippetsTree> projectSnippetsTreeByPluginOption;
	private Set<String> cTreeNameSet;
	public List<String> cTreeNameList;
	public List<String> pluginOptionNameList;
	private DataTablesTool dataTablesTool;
	private String rowHeadingName;

	@Deprecated
	private List<SummaryType> summaryTypes;
	private SummaryType summaryType;
	private CellRenderer currentCellRenderer;
	
	public ResultsAnalysisImpl() {
	}
		
	public ResultsAnalysisImpl(DataTablesTool dataTablesTool) {
		this.dataTablesTool = dataTablesTool;
		dataTablesTool.setCellCalculator(this);
	}
	
	public void addSnippetsFile(File xmlFile) {
		
		if (xmlFile == null) {
			LOG.error("Null XML file");
			return;
		}
		Element element = null;
		try {
			element = XMLUtil.parseQuietlyToDocument(new FileInputStream(xmlFile)).getRootElement();
		} catch (RuntimeException re) {
			re.printStackTrace();
			LOG.error("bad snippets file:"+xmlFile);
			return;
		} catch (FileNotFoundException e) {
			LOG.error("Non-existent XML file: "+xmlFile);
			return;
		}
		ProjectSnippetsTree projectSnippetsTree = ProjectSnippetsTree.createProjectSnippetsTree(element);
		if (projectSnippetsTree == null) {
			LOG.warn("Cannot create ProjectSnippetsTree: "+xmlFile);
			return;
		}
		ensureProjectSnippetsTreeByPluginOptionName();
		PluginOption pluginOption = projectSnippetsTree.getPluginOption();
		if (pluginOption == null) {
			LOG.warn("Null pluginOption");
			return;
		}
		if (projectSnippetsTreeByPluginOption.containsKey(pluginOption)) {
			LOG.warn("Already has pluginOption: "+pluginOption);
		}
		projectSnippetsTreeByPluginOption.put(pluginOption.toString(), projectSnippetsTree);
	}
	
	private void ensureProjectSnippetsTreeByPluginOptionName() {
		if (projectSnippetsTreeByPluginOption == null) {
			projectSnippetsTreeByPluginOption = new HashMap<String, ProjectSnippetsTree>();
		}
	}

	public Map<String, ProjectSnippetsTree> getProjectSnippetsTreeByPluginOption() {
		return projectSnippetsTreeByPluginOption;
	}

	public Set<String> getCTreeNameSet() {
		if (cTreeNameSet == null) {
			cTreeNameSet = new HashSet<String>();
			ensureProjectSnippetsTreeByPluginOptionName();
			for (Map.Entry<String, ProjectSnippetsTree> entry : projectSnippetsTreeByPluginOption.entrySet()) {
				cTreeNameSet.addAll(entry.getValue().getCTreeNameList());
			}
		}
		return cTreeNameSet;
	}

	public List<String> getSortedCTreeNameList() {
		getCTreeNameSet();
		cTreeNameList = Arrays.asList(cTreeNameSet.toArray(new String[0]));
		Collections.sort(cTreeNameList);
		return cTreeNameList;
	}

	public HtmlElement createSnippetsTreeContents(SnippetsTree snippetsTree) {
		HtmlElement htmlElement = null;
		List<XMLSnippets> list = snippetsTree.getOrCreateSnippetsList();
		List<String> terms = createTerms(list);
		Multiset<String> multiset = createMultisetOmittingNullEntries(terms);
		Iterable<Multiset.Entry<String>> entrys = CMineUtil.getEntriesSortedByCount(multiset);
		Iterator<Entry<String>> iterator = entrys.iterator();
		if (summaryType == null) {
			LOG.warn("no cell content flag");
		} else if (summaryType.equals(SummaryType.FULL)) {
			htmlElement = new HtmlDiv();
			htmlElement.setTitle(String.valueOf(terms.size()));
			int maxCount = 4;
			for (Entry<String> entry : entrys) {
				HtmlElement td = createSpan(entry);
				htmlElement.appendChild(td);
				if (maxCount-- <= 0) {
					break;
				}
				htmlElement.appendChild(new HtmlBr());
			}
		} else if (summaryType.equals(SummaryType.COMMONEST)) {
			if (iterator.hasNext()) {
				Entry<String> entry = iterator.next();
				htmlElement = createSpan(entry);
			}
		} else if (summaryType.equals(SummaryType.COUNT)) {
			htmlElement = createSpan(multiset.size());
		} else if (summaryType.equals(SummaryType.ENTRIES)) {
			htmlElement = createSpan(multiset.entrySet().size());
		} else {
			LOG.warn("Unknown flag: "+summaryType);
		}
		return htmlElement;
	}

	private Multiset<String> createMultisetOmittingNullEntries(List<String> terms) {
		Multiset<String> multiset = HashMultiset.create();
		for (String term : terms) {
			if (term == null) {
				term = "NULL";
			}
			multiset.add(term);
		}
		return multiset;
	}

	private HtmlElement createSpan(int size) {
		HtmlElement htmlElement;
		String ss = String.valueOf(size);
		htmlElement = new HtmlSpan();
		htmlElement.appendChild(ss);
		return htmlElement;
	}

	private HtmlElement createSpan(Entry<String> entry) {
		HtmlElement htmlSpan = new HtmlSpan();
		String value = entry.getElement().toString();
		if (currentCellRenderer == null) {
			htmlSpan.appendChild(value);
		} else {
			currentCellRenderer.setValue(value);
			htmlSpan.appendChild(currentCellRenderer.getHtmlElement());
		}
		int count = entry.getCount();
		if (count > 1) {
			htmlSpan.appendChild(" x "+count);
		}
		return htmlSpan;
	}

	private List<String> createTerms(List<XMLSnippets> list) {
		List<String> terms = new ArrayList<String>();
		for (XMLSnippets snippets : list) {
			for (int i = 0; i < snippets.getChildElements().size(); i++) {
				ResultElement resultElement = ResultElement.createResultElement(snippets.getChildElements().get(i));
				String term = resultElement.getTerm();
				terms.add(term);
			}
		}
		return terms;
	}

	public HtmlTable makeHtmlDataTable() {
		ensureProjectSnippetsTreeByPluginOptionName();
		Set<String> set = this.projectSnippetsTreeByPluginOption.keySet();
		LOG.trace(set);
		pluginOptionNameList = Arrays.asList(set.toArray(new String[0]));
		Collections.sort(this.pluginOptionNameList);
		
		List<CellRenderer> cellRendererList = new ArrayList<CellRenderer>();
		AMIPluginOptionList pluginOptionList = new AMIPluginOptionList();
		for (String pluginOptionName : pluginOptionNameList) {
			AMIPluginOption pluginOption = pluginOptionList.getPluginOption(pluginOptionName);
			if (pluginOption == null) {
				throw new RuntimeException("Cannot find plugin: "+pluginOptionName);
			}
			CellRenderer cellRenderer = pluginOption.getNewCellRenderer();
			cellRenderer.setFlag(pluginOptionName);
			cellRendererList.add(cellRenderer);
		}
		LOG.trace("CELL "+cellRendererList);
		dataTablesTool.setCellRendererList(cellRendererList);
		dataTablesTool.setRowHeadingList(this.getSortedCTreeNameList());
		
		HtmlTable htmlTable = dataTablesTool.createHtmlDataTable();
		htmlTable.setClassAttribute(DataTablesTool.TABLE+" "+DataTablesTool.TABLE_STRIPED+" "+DataTablesTool.TABLE_BORDERED+" "+DataTablesTool.TABLE_HOVER);
		htmlTable.setId(dataTablesTool.getId());

		return htmlTable;
	}

	public CellCalculator setLocalLink0(String link0) {
		dataTablesTool.setLocalLink0(link0);
		return this;
	}

	public CellCalculator setLocalLink1(String link1) {
		dataTablesTool.setLocalLink1(link1);
		return this;
	}

	public CellCalculator setRemoteLink0(String link0) {
		dataTablesTool.setRemoteLink0(link0);
		return this;
	}

	public CellCalculator setRemoteLink1(String link1) {
		dataTablesTool.setRemoteLink1(link1);
		return this;
	}

	public ResultsAnalysisImpl setRowHeadingName(String rowHeadingName) {
		this.rowHeadingName = rowHeadingName;
		return this;
	}

	public String getRowHeadingName() {
		return this.rowHeadingName;
	}

	public DataTablesTool getDataTablesTool() {
		return dataTablesTool;
	}

	public void addCellValues(List<CellRenderer> columnHeadingList, HtmlTr htmlTr, int iRow) {
		for (int iCol = 0; iCol < columnHeadingList.size(); iCol++) {
			this.currentCellRenderer = columnHeadingList.get(iCol);
			HtmlElement htmlElement = createCellContents(iRow, iCol);
			HtmlElement htmlTd = new HtmlTd();
			if (htmlElement != null) {
				htmlTd.appendChild(htmlElement);
			}
			htmlTr.appendChild(htmlTd);
		}
	}

	public List<String> getColumnHeadingList() {
		return pluginOptionNameList;
	}

	public List<String> getRowHeadingList() {
		return cTreeNameList;
	}

	public HtmlElement createCellContents(int iRow, int iCol) {
		String columnHeadingx = getColumnHeadingList().get(iCol);
		ProjectSnippetsTree projectSnippetsTree = this.projectSnippetsTreeByPluginOption.get(columnHeadingx);
		String rowHeading = getRowHeadingList().get(iRow);
		SnippetsTree snippetsTree = projectSnippetsTree.getOrCreateSnippetsTreeByCTreeName().get(rowHeading);
		HtmlElement contents =  (snippetsTree == null) ? null : this.createSnippetsTreeContents(snippetsTree);
		return contents;
	}

	public void setSummaryTypes(List<SummaryType> flags) {
		summaryTypes = flags;
	}

	public void setSummaryType(SummaryType flag) {
		summaryType = flag;
	}

	public void addDefaultSnippets(File projectDir) {
		File[] snippetsFiles = projectDir.listFiles(new FilenameFilter() {
			
			public boolean accept(File dir, String name) {
				return name != null && name.endsWith(SNIPPETS_XML);
			}
		});
		if (snippetsFiles != null) {
			for (File file : snippetsFiles) {
				this.addSnippetsFile(file);
			}
		}
				
	}
	
}
