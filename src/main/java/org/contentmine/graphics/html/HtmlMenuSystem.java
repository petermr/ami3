/**
 *    Copyright 2011 Peter Murray-Rust et. al.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.contentmine.graphics.html;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.eucl.xml.XMLConstants;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlElement.Target;

import nu.xom.Document;
import nu.xom.Elements;


/** base class for lightweight generic SVG element.
 * no checking - i.e. can take any name or attributes
 * @author pm286
 *
 */
public class HtmlMenuSystem {
	private static Logger LOG = Logger.getLogger(HtmlMenuSystem.class);

	public final static String DEFAULT_HTML_SUFFIX = "html";
	public final static String DEFAULT_MENU_ROOT = "menu";
	public final static int DEFAULT_MENU_WIDTH = 150;
	public final static String DEFAULT_INDEXFRAME_ROOT = "indexFrame";
	public final static String DEFAULT_BOTTOM_ROOT = "bottom";
	public final static String DEFAULT_BOTTOM_WELCOME = "Images will appear here";
	
	private HtmlHtml menu;
	private String menuRootName = DEFAULT_MENU_ROOT;
	private String htmlSuffix = DEFAULT_HTML_SUFFIX;
	private String menuFilename = createMenuFilename();
	private int menuWidth = DEFAULT_MENU_WIDTH;
	
	private HtmlHead head;
	private HtmlBody body;
	private HtmlUl ul;
	
	private HtmlHtml indexFrame;
	private String indexFrameRootName = DEFAULT_INDEXFRAME_ROOT;
	private String indexFrameFilename = createIndexFrameFilename();
	
	private HtmlFrameset menuFrameset;
	private HtmlFrame menuFrame;
	private HtmlFrame bottomFrame;
	
	private HtmlHtml bottom;
	private String bottomRootName = DEFAULT_BOTTOM_ROOT;
	private String bottomFilename = createBottomFilename();
	private String bottomWelcome = DEFAULT_BOTTOM_WELCOME;
	
	@SuppressWarnings("unused")
	private String outdir;
	
	/** constructor.
	 */
	public HtmlMenuSystem() {
		makeBottom();
		makeIndexFrame();
		makeMenu();
	}
	
	public static HtmlMenuSystem readDirectory(File dir) {
		HtmlMenuSystem htmlMenuSystem = null;
		if (dir != null && dir.isDirectory()) {
			htmlMenuSystem = new HtmlMenuSystem();
			htmlMenuSystem.readIndexFrameElement(dir);
			htmlMenuSystem.readMenuElement(dir);
			htmlMenuSystem.readBottomElement(dir);
		}
		return htmlMenuSystem;
	}

	private void readBottomElement(File dir) {
		bottom = readElement(dir, createBottomFilename());
	}

	private void readMenuElement(File dir) {
		menu = readElement(dir, createMenuFilename());
	}

	private void readIndexFrameElement(File dir) {
		indexFrame = readElement(dir, createIndexFrameFilename());
		
	}

	private HtmlHtml readElement(File dir, String filename) {
		File file = new File(dir, filename);
		Document document = XMLUtil.parseQuietlyToDocument(file);
		return (document == null || !(document.getRootElement() instanceof HtmlHtml)) ? null 
				: (HtmlHtml) document.getRootElement();

	}

	private String createBottomFilename() {
		return bottomRootName+XMLConstants.S_PERIOD+htmlSuffix;
	}

	private String createIndexFrameFilename() {
		return indexFrameRootName+XMLConstants.S_PERIOD+htmlSuffix;
	}

	private String createMenuFilename() {
		return menuRootName+XMLConstants.S_PERIOD+htmlSuffix;
	}

	private void makeBottom() {
		bottom = HtmlHtml.createUTF8Html();
		head = bottom.getOrCreateHead();
		body = bottom.getOrCreateBody();
		body.appendChild(bottomWelcome);
	}
	
	private void makeMenu() {
		menu = HtmlHtml.createUTF8Html();
		head = menu.getOrCreateHead();
		body = menu.getOrCreateBody();
		ensureUl();
		body.appendChild(ul);
	}
	
	private void makeIndexFrame() {
		indexFrame = HtmlHtml.createUTF8Html();
		menuFrameset = new HtmlFrameset();
		menuFrameset.setCols(""+menuWidth+", *");
		indexFrame.appendChild(menuFrameset);
		menuFrame = new HtmlFrame(Target.menu, "menu.html");
		menuFrameset.appendChild(menuFrame);
		bottomFrame = new HtmlFrame(Target.bottom, "bottom.html");
		menuFrameset.appendChild(bottomFrame);
	}
	
	public HtmlUl getUl() {
		return ul;
	}
	
	public List<HtmlA> getAList() {
		Elements liElements = ul.getLiElements();
		List<HtmlA> aList = new ArrayList<HtmlA>();
		for (int i = 0; i < liElements.size(); i++) {
			aList.add((HtmlA)liElements.get(i));
		}
		return aList;
	}
	
	public String getFirstTarget() {
		List<HtmlA> aList = getAList();
		return (aList.size() == 0) ? null : aList.get(0).getTarget();
	}

	public HtmlElement addA(String href, Target target, String content) {
		ensureUl();
		HtmlLi li = new HtmlLi();
		ul.appendChild(li);
		HtmlA a = new HtmlA();
		a.setHref(href);
		a.setTarget(target);
		a.appendChild(content);
		li.appendChild(a);
		return a;
	}
	
	private HtmlUl ensureUl() {
		if (ul == null) {
			ul = new HtmlUl();
		}
		return ul;
	}

	public void setOutdir(String outdir) {
		menuFilename = outdir+File.separator + createMenuFilename();		
		indexFrameFilename = outdir+File.separator+ createIndexFrameFilename();		
		bottomFilename = outdir+File.separator+createBottomFilename();		
		this.outdir = outdir;
	}

	public void outputMenuAndBottomAndIndexFrame() throws IOException {
		FileOutputStream fos = new FileOutputStream(menuFilename);
		XMLUtil.debug(menu, fos, 1);
		fos.close();
		fos = new FileOutputStream(indexFrameFilename);
		XMLUtil.debug(indexFrame, fos, 1);
		fos.close();
		fos = new FileOutputStream(bottomFilename);
		XMLUtil.debug(bottom, fos, 1);
		fos.close();
	}

	public void setMenuRootName(String menuRootName) {
		this.menuRootName = menuRootName;
	}

	public void setMenuWidth(int menuWidth) {
		this.menuWidth = menuWidth;
	}

	public void setIndexFrameRootName(String indexFrameRootName) {
		this.indexFrameRootName = indexFrameRootName;
	}

	public void setBottomRootName(String bottomRootName) {
		this.bottomRootName = bottomRootName;
	}

	public void setBottomWelcome(String bottomWelcome) {
		this.bottomWelcome = bottomWelcome;
	}

	public void addHRef(String outputFile) {
		String name = new File(outputFile).getName();
		String content = name+" ";
		this.addA(name, Target.bottom, content);
	}


	public HtmlHtml getMenu() {
		return menu;
	}

	public HtmlHtml getIndexFrame() {
		return indexFrame;
	}
}
