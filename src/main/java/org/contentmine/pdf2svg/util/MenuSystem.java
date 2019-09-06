/**
 * Copyright (C) 2012 pm286 <peter.murray.rust@googlemail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.contentmine.pdf2svg.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.eucl.xml.XMLConstants;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlA;
import org.contentmine.graphics.html.HtmlFrame;
import org.contentmine.graphics.html.HtmlFrameset;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.html.HtmlLi;
import org.contentmine.graphics.html.HtmlUl;

import nu.xom.Attribute;


public class MenuSystem {

	private final static Logger LOG = Logger.getLogger(MenuSystem.class);
	
	private static final String INDEX_HTML = "index.html";
	private static final String MENU_HTML = "menu.html";
	private static final String DISPLAY_HTML = "display.html";
	private static final String DISPLAY = "display";
	private static final String MENU = "menu";

	private static final int DEFAULT_ROW_WIDTH = 100;
	private static final String target = PConstants.HTML_TARGET;

	private HtmlUl ul;
	private File outdir;
	private String root;
	private boolean addPdf = true;

	private String label;
	private int rowWidth = DEFAULT_ROW_WIDTH;
	private String display; 
	private String menu; 

	public MenuSystem(File outdir) {
		this.outdir = outdir;
	}
	
	public void writeDisplayFiles(List<File> files, String ext) {
		this.display = DISPLAY+ext;
		this.menu = MENU+ext;
		if (files.size() == 0) {
			System.out.println("No files to display");
		}
		
		createAndWriteIndexFile();
		createAndWriteDisplayFile();
		writeFilenamesToMenu(files);
		
	}

	private void createAndWriteDisplayFile() {
		HtmlHtml html = new HtmlHtml();
		try {
			File displayFile = new File(outdir, DISPLAY_HTML);
			XMLUtil.debug(html, new FileOutputStream(displayFile), 1);
		} catch (IOException e) {
			throw new RuntimeException("Cannot write file: "+DISPLAY_HTML, e);
		}
	}

	private void createAndWriteIndexFile() {
		HtmlHtml html = new HtmlHtml();
		HtmlFrameset frameset = new HtmlFrameset();
		html.appendChild(frameset);
		frameset.setCols(rowWidth+", *");
		HtmlFrame menuFrame = new HtmlFrame();
		menuFrame.setId(menu);
		menuFrame.setSrc(MENU_HTML);
		frameset.appendChild(menuFrame);		
		HtmlFrame displayFrame = new HtmlFrame();
		displayFrame.setId(display);
		displayFrame.setName(display);
		displayFrame.setSrc(DISPLAY_HTML);
		frameset.appendChild(displayFrame);
		try {
			File indexFile = new File(outdir, INDEX_HTML);
			XMLUtil.debug(html, new FileOutputStream(indexFile), 1);
		} catch (IOException e) {
			throw new RuntimeException("Cannot write file: ", e);
		}
	}

	private void writeFilenamesToMenu(List<File> files) {
		File relativeParent = outdir;
		HtmlHtml html1 = new HtmlHtml();
		ul = new HtmlUl();
		html1.appendChild(ul);
		for (File file : files) {
			String path = file.getAbsolutePath();
			if (root != null) {
				path += root;
			}
			addFilename(relativeParent, path);
		}
		if (addPdf) {
			addPDF(relativeParent);
		}
		try {
			File menuFile = new File(outdir, MENU_HTML);
			XMLUtil.debug(html1, new FileOutputStream(menuFile), 1);
		} catch (IOException e) {
			throw new RuntimeException("Cannot write file: ", e);
		}
	}

	private void addPDF(File outDir) {
		File idDir = outDir.getParentFile();
		if (idDir != null) {
			File rootDir = idDir.getParentFile();
			File pdf = new File(rootDir, idDir.getName()+PConstants.PDF);
			addFilename(outDir, pdf.getAbsolutePath());
		}
	}

	private void addFilename(File relativeDir, String filename) {
		HtmlLi li = new HtmlLi();
		ul.appendChild(li);
		HtmlA a = new HtmlA();
		li.appendChild(a);
		File file = new File(filename);
		String relativeName = org.contentmine.eucl.euclid.Util.getRelativeFilename(relativeDir, new File(filename), XMLConstants.S_SLASH);
		a.addAttribute(new Attribute(target, display));
		a.setHref(relativeName);
		String name = file.getName();
		if (label != null) {
			File fileP = new File(file, label);
			try {
				fileP = fileP.getCanonicalFile();
			} catch (IOException e) {
				throw new RuntimeException("Cannot canonicalize: "+fileP);
			}
			name = fileP.getName();
		}
		a.setValue(name);
	}

	public void setRoot(String root) {
		this.root = root;
	}

	public void setAddPdf(boolean b) {
		addPdf = b;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setRowWidth(int w) {
		this.rowWidth = w;
	}


}
