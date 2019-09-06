package org.contentmine.svg2xml.cmucl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.html.HtmlImg;
import org.contentmine.graphics.html.HtmlTd;
import org.contentmine.graphics.html.HtmlTr;
import org.contentmine.graphics.svg.SVGDefs;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGPath;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGShape;
import org.contentmine.graphics.svg.SVGUtil;
import org.contentmine.graphics.svg.linestuff.Path2ShapeConverter;
import org.contentmine.svg2xml.SVG2XMLFixtures;
import org.contentmine.svg2xml.table.TableContentCreator;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import nu.xom.Attribute;

//@Ignore // LONG TEST
public class CMUCLTest {

	public static final Logger LOG = Logger.getLogger(CMUCLTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	final static File CMUCL0 = new File(SVG2XMLFixtures.TABLE_DIR, "cmucl0/");
	private static final File CMUCL_OUT_DIR = new File("target/table/cmucl0");
	private static final String BORDER = "border";
	public final static String TABLE = "table";
	private static final String VERTICAL_ALIGN_TOP = "vertical-align:top";

	@Test
	public void testBMC() {
		Assert.assertTrue("tables should exist", SVG2XMLFixtures.TABLE_DIR.exists());
		Assert.assertTrue("CMUCL0 should exist", CMUCL0.exists());
		String root = "BMC_Medicine";
		extractTables(root);
	}
	
	@Test
	@Ignore // rather long
	public void testNature() {
		String root = "Nature_EurJClinNutrit";
		markupAndOutputTables(root);
	}
	
	@Test
	@Ignore // LONG 
	public void testAllMarkup() {
		File[] dirs = CMUCL0.listFiles();
		for (File dir : dirs) {
			markupAndOutputTables(dir.getName());
		}
	}

	// ======================
	
	public static void createHTML(String root) {
		File inDir = new File(CMUCL0, root+"/");
		LOG.debug("in dir: "+inDir);
		File outDir = new File(CMUCL_OUT_DIR, root+"/");
		List<File> tableFiles = new ArrayList<File>
			(FileUtils.listFiles(inDir, new WildcardFileFilter("table*.svg") , TrueFileFilter.INSTANCE));
		for (File tableFile : tableFiles) {
			TableContentCreator tableContentCreator = new TableContentCreator();
			HtmlHtml html = tableContentCreator.createHTMLFromSVG(tableFile);
			File outfile = new File(outDir, tableFile.getName()+".html");
			LOG.trace("writing: "+outfile);
			try {
				XMLUtil.debug(html, outfile, 1);
			} catch (IOException e) {
				LOG.error("Cannot write file: "+outfile+" ("+e+")");
			}
		}
	}

	static void markupAndOutputTables(String root/*, String filename, int nHeaderCols, int nBodyCols*/) {
		File inDir = new File(CMUCL0, root+"/");
		File outDir = new File(CMUCL_OUT_DIR, root+"/");
		List<File> inputFiles = new ArrayList<File>
		(FileUtils.listFiles(inDir, new WildcardFileFilter("table*.svg") , TrueFileFilter.INSTANCE));
		for (File inputFile : inputFiles) {
			TableContentCreator tableContentCreator = new TableContentCreator(); 
			tableContentCreator.markupAndOutputTable(inputFile, outDir);
		}
	}

	static void extractTables(String root) {
		File outDir = CMUCL_OUT_DIR;
		outDir.mkdirs();
		File bmcDir = new File(CMUCL0, root);
		List<File> tableFiles = new ArrayList<File>
			(FileUtils.listFiles(bmcDir, new WildcardFileFilter("table*.svg") , TrueFileFilter.INSTANCE));
		for (File file : tableFiles) {
			String fileroot = file.getName().replace(".svg", "");
			SVGSVG svg = null;
			try {
				svg = (SVGSVG) SVGUtil.parseToSVGElement(new FileInputStream(file));
			} catch (Exception e) {
				LOG.error("Cannot find/read: "+file);
				continue;
			}
			SVGDefs.removeDefs(svg);
			List<SVGPath> paths = SVGPath.extractSelfAndDescendantPaths(svg);
			List<SVGShape> shapes = new Path2ShapeConverter().convertPathsToShapes(paths);
			SVGG g = new SVGG();
			Multiset<String> classSet = HashMultiset.create();
			for (SVGElement shape : shapes) {
				g.appendChild(shape);
				classSet.add(shape.getClass().getSimpleName());
			}
			SVGSVG.wrapAndWriteAsSVG(g, new File(outDir, root+"/"+fileroot+".shapes.svg"));
		}
	}

	private void addImageToRow(double TD_WIDTH, File png, int imgWidth, int imgHeight, HtmlTr row) {
		HtmlTd imageCell = new HtmlTd();
		row.appendChild(imageCell);
		imageCell.setStyle(VERTICAL_ALIGN_TOP);
		HtmlImg img = new HtmlImg();
		imageCell.appendChild(img);
		img.setSrc("../image/"+png.getName());
		img.setWidth(TD_WIDTH);
		img.setHeight(TD_WIDTH * (double) imgHeight / (double) imgWidth);
		img.setStyle(VERTICAL_ALIGN_TOP);
	}

	// ===================================
	
	private void addSvgToRow(double TD_WIDTH, File svgAnnotFile1, HtmlTr row) {
		HtmlTd svgCell = new HtmlTd();
		row.appendChild(svgCell);
		
		svgCell.setWidth(TD_WIDTH);
		HtmlDiv div = new HtmlDiv();
		svgCell.appendChild(div);
		svgCell.setStyle(VERTICAL_ALIGN_TOP);
		SVGElement svg = null;
		try {
			svg = SVGUtil.parseToSVGElement(new FileInputStream(svgAnnotFile1));
		} catch (FileNotFoundException fnfe) {
			LOG.error("no SVG file:"+svgAnnotFile1);
		}
		if (svg != null) {
			SVGG g = new SVGG();
			XMLUtil.transferChildren(svg, g);
			svg.appendChild(g);
			Real2Range bbox = svg.getBoundingBox();
			double scale = TD_WIDTH / bbox.getXRange().getRange();
			Real2 corner = bbox.getLLURCorners()[0];
			String transform = "scale("+scale+") ";
			transform += " translate("+(-1.0*corner.getX())+","+(-1.0*corner.getY())+")";
			g.addAttribute(new Attribute("transform", transform));
			SVGElement bboxRect = SVGRect.createFromReal2Range(bbox);
			bboxRect.setStroke("black");
			bboxRect.setStrokeWidth(2.);
			bboxRect.setFill("none");
			g.appendChild(bboxRect);
			div.appendChild(svg);
		}
	}

}

class CopyFileVisitor extends SimpleFileVisitor<Path> {
	private static final Logger LOG = Logger.getLogger(CopyFileVisitor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

    private final Path targetPath;
    private Path sourcePath = null;
	private String srcRoot;
	private StringBuilder csvBuilder;
    
    public CopyFileVisitor(StringBuilder csvBuilder, String srcRoot, Path targetPath) {
        this.targetPath = targetPath;
        this.srcRoot = srcRoot;
        this.csvBuilder = csvBuilder;
    }

    @Override
    public FileVisitResult preVisitDirectory(final Path dir,
    		final BasicFileAttributes attrs) throws IOException {
    	File dirFile = dir.toFile();
    	String name = dirFile.getName();
        if (sourcePath == null) {
            sourcePath = dir;
        } else if (name.equals("png") || name.equals("svg")) {
        	LOG.trace("skipped dir "+dir);
            return FileVisitResult.SKIP_SUBTREE;
        } else if (name.equals("image") || name.equals("pdftable")) {
        	Files.createDirectories(targetPath.resolve(sourcePath.relativize(dir)));
        } else {
        	String filename = dir.toString().substring(srcRoot.length());
        	csvBuilder.append(String.valueOf(filename)+","+
        			","+"\n");
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(final Path file,
	    final BasicFileAttributes attrs) throws IOException {
    	File fileFile = file.toFile();
		Path targetFile = targetPath.resolve(sourcePath.relativize(file));
		boolean copy = true;
		if (fileFile.getName().endsWith(TableContentCreator.DOT_ANNOT_SVG)) {
			// skip SVG
			copy = false;
			LOG.trace("skipped "+file);
		} else if (!targetFile.toFile().exists()) {
			Files.copy(file, targetFile);
		}
		if (copy) {
        	if (!file.toFile().getName().endsWith(".png")) {
        		csvBuilder.append(String.valueOf(
        			file.toFile().getParentFile()).substring(srcRoot.length())+","+
        			file.toFile().getName()+","+"\n");
        	}
		}
	    return FileVisitResult.CONTINUE;
    }
}
