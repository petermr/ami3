package org.contentmine.cproject.metadata;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.util.RectangularTable;

public class MetadataManager  {

	private static final Logger LOG = Logger.getLogger(MetadataManager.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public static final String DOI = "DOI";
	public static final String CROSSREF = "CR";
	public static final String QS = "QS";
	public static final String SHUFFLED_URLS_TXT = "shuffledUrls.txt";
	public static final String QUICKSCRAPE_DIR = "quickscrape";
	
	private Map<String, RectangularTable> metadataTableByKey;
	
	public MetadataManager() {
		super();
	}

	/** finds last cell value in reference table also in download.
	 * 
	 * @param ref
	 * @param download
	 * @param key
	 * @return
	 */
	public int findLastCorrespondingRow(String ref, String download, String key) {
		int lastDoiIndex = -1;
		ensureMetadataTableByType();
		RectangularTable refTable = metadataTableByKey.get(ref);
		RectangularTable downloadTable = metadataTableByKey.get(download);
		if (refTable != null && downloadTable != null) {
			List<String> refDoiList = refTable.getColumn(DOI);
			List<String> downloadDoiList = downloadTable.getColumn(DOI);
			for (int i = 0; i < refDoiList.size(); i++) {
				String refDoi = refDoiList.get(i);
				int downloadDoiIndex = downloadDoiList.indexOf(refDoi);
				if (downloadDoiIndex != -1) {
					lastDoiIndex = downloadDoiIndex;
				}
			}
		}
		return lastDoiIndex;
	}

	public RectangularTable readMetadataTable(File file, String key) throws IOException {
		RectangularTable metadataTable = RectangularTable.readCSVTable(file, true);
		ensureMetadataTableByType();
		metadataTableByKey.put(key, metadataTable);
		return metadataTable;
	}

	/** get values yet to be downloaded.
	 * 
	 * @param key
	 * @param startRow
	 * @param colHead
	 * @return
	 */
	public List<String> findFollowingValues(String key, int startRow, String colHead) {
		RectangularTable table = ensureMetadataTableByType().get(key);
		List<String> values = null;
		if (table != null) {
			List<String> allValues = table.getColumn(colHead);
			values = (allValues == null) ? null : allValues.subList(startRow, allValues.size());
		}
		return values;
	}
	
	// ====
	
	private Map<String, RectangularTable> ensureMetadataTableByType() {
		if (metadataTableByKey == null) {
			metadataTableByKey = new HashMap<String, RectangularTable>();
		}
		return metadataTableByKey;
	}


	

}
