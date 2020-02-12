package org.contentmine.ami.tools;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.plugins.AbstractSearchArgProcessor;
import org.contentmine.ami.plugins.regex.RegexArgProcessor;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.DebugPrint;
import org.contentmine.eucl.euclid.Axis.Axis2;
import org.contentmine.eucl.euclid.Int2;
import org.contentmine.eucl.euclid.Int2Range;
import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.eucl.euclid.IntRangeComparator;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.euclid.util.MultisetUtil;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGLineList;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.image.ImageUtil;
import org.contentmine.image.diagram.DiagramAnalyzer;
import org.contentmine.image.pixel.AxialPixelFrequencies;
import org.contentmine.image.pixel.IslandRingList;
import org.contentmine.image.pixel.PixelIsland;
import org.contentmine.image.pixel.PixelIslandList;
import org.contentmine.image.pixel.PixelRing;
import org.contentmine.image.pixel.PixelRingList;
import org.contentmine.image.processing.HilditchThinning;
import org.contentmine.image.processing.Thinning;
import org.contentmine.image.processing.ZhangSuenThinning;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

import boofcv.io.image.UtilImageIO;
import nu.xom.Attribute;
import nu.xom.Element;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/** analyses bitmaps
 * 
 * @author pm286
 *
 */
@Command(
name = "ami-regex", 
aliases = "regex",
version = "ami-regex 0.1",
description = "searches with regex, possibly subcommand of search "
)

/**
		// regex
		args="location [location ...]"
		countRange="{1,*}"
		parseMethod="parseRegex"
		runMethod="runRegex"
		outputMethod="outputResultElements"
 * 
 * @author pm286
 *
 */
public class AMIRegexTool extends AbstractAMISearchTool {


	private static final Logger LOG = Logger.getLogger(AMIRegexTool.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	
    @Option(names = {"--dummy"},
    		arity = "1",
            description = "dummy")
	public String dummy = "default";


    /** used by some non-picocli calls
     * obsolete it
     * @param cProject
     */
	public AMIRegexTool(CProject cProject) {
		this.cProject = cProject;
	}
	
	public AMIRegexTool() {
	}
	
    public static void main(String[] args) throws Exception {
    	new AMIRegexTool().runCommands(args);
    }

    @Override
    protected boolean parseGenerics() {
    	LOG.debug("parseGenerics");
    	return super.parseGenerics();
    }
    @Override
	protected void parseSpecifics() {
    	LOG.debug("parseSpecifics");
		System.out.println("dummy             " + dummy);
		System.out.println();
	}

    @Override
    protected void runSpecifics() {
    	if (oldstyle) {
    		LOG.debug("oldstyle regex; no code");
    	} else if (processTrees()) { 
    		
    	} else {
			DebugPrint.debugPrint(Level.ERROR, "must give cProject or cTree ie imageFile");
	    }
    }

	public boolean processTree() {
		processedTree = false;
		LOG.debug("amiRegexTool.processTree NYI");
		return processedTree;
	}

	@Override
	protected void populateArgProcessorFromCLI() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected AbstractSearchArgProcessor getOrCreateSearchProcessor() {
		abstractSearchArgProcessor = new RegexArgProcessor();
		return abstractSearchArgProcessor;
	}

	@Override
	protected void processProject() {
		LOG.debug("regex processProject NYI");
	}

	@Override
	protected String buildCommandFromBuiltinsAndFacets() {
		LOG.debug("regex buildCommandFromBuiltinsAndFacets NYI");
		return null;
	}

	@Override
	protected void runProjectSearch() {
		LOG.debug("regex runProjectSearch NYI");
	}



}
