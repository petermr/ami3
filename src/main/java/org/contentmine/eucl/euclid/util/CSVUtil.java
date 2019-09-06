package org.contentmine.eucl.euclid.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class CSVUtil {
	private static final Logger LOG = Logger.getLogger(CSVUtil.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static final String NEW_LINE_SEPARATOR = "\n";

	/**
	 * 
	 * @param fileName
	 * @param csvHeaders list of headers ("row 1")
	 * @param valueListList
	 */
	public static void writeCSV(String fileName, List<String> csvHeaders, List<List<String>> valueListList) {
		if (fileName == null) {
			LOG.error("null filename");
		}
		FileWriter fileWriter = null;
        CSVPrinter csvFilePrinter = null;
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
                 
        try {
        	File file = new File(fileName);
        	file.getParentFile().mkdirs();
            fileWriter = new FileWriter(file);
            csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);
            csvFilePrinter.printRecord(csvHeaders);
             
            for (List<String> record : valueListList) {
                csvFilePrinter.printRecord(record);
            }
        } catch (Exception e) {
            throw new RuntimeException("failed to write CSV", e);
        } finally {
        	// could be null if failed to create file
        	if (fileWriter != null) {
	            try {
	                fileWriter.flush();
	                fileWriter.close();
	                csvFilePrinter.close();
	            } catch (IOException e) {
	                throw new RuntimeException("failed to close/flush CSV", e);
	            }
        	}
        }
	}
	

}
