package org.contentmine.pdf2svg.xmllog;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.pdfbox.util.ExtensionFileFilter;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.graphics.svg.SVGPath;
import org.contentmine.pdf2svg.xmllog.model.PDFChar;
import org.contentmine.pdf2svg.xmllog.model.PDFCharList;
import org.contentmine.pdf2svg.xmllog.model.PDFCharPath;
import org.contentmine.pdf2svg.xmllog.model.PDFFile;
import org.contentmine.pdf2svg.xmllog.model.PDFFileList;
import org.contentmine.pdf2svg.xmllog.model.PDFFont;
import org.contentmine.pdf2svg.xmllog.model.PDFFontList;
import org.contentmine.pdf2svg.xmllog.model.PDFPage;
import org.contentmine.pdf2svg.xmllog.model.PDFPageList;

/**
 * copied from apach pdfbox PDFReader ...
 * 
 * @author <a href="ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.5 $
 */

public class Viewer extends JFrame implements TreeModel {

	private static final long serialVersionUID = 1L;

	private XMLLog xmlLog;

	private File currentDir = new File(".");
	private String currentFilename = null;
	private String currentPDFFilename = null;
	private int currentPage = 0;
	private int numberOfPages = 0;

	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenuItem openMenuItem;
	private JMenuItem exitMenuItem;
	private JPanel treePanel;
	private JTree tree;
	private JPanel glyphPanel;
	private JLabel glyph;
	private ReaderBottomPanel bottomStatusPanel = new ReaderBottomPanel();

	private TreeModelListener listener;

	/**
	 * Constructor.
	 */
	public Viewer() {
		initComponents();
		xmlLog = new XMLLog();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 */
	private void initComponents() {

		menuBar = new JMenuBar();
		fileMenu = new JMenu();
		openMenuItem = new JMenuItem();
		exitMenuItem = new JMenuItem();
		treePanel = new JPanel();
		tree = new JTree();
		glyphPanel = new JPanel();
		glyph = new JLabel();
		bottomStatusPanel = new ReaderBottomPanel();

		updateTitle();

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				exitApplication();
			}
		});

		tree.setEditable(false);
		tree.setExpandsSelectedPaths(true);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setDragEnabled(false);
		tree.setInvokesStopCellEditing(true);
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent evt) {
				treeValueChanged(evt);
			}
		});

		treePanel.add(tree);

		JScrollPane treeScroller = new JScrollPane();
		treeScroller.setViewportView(treePanel);

		glyphPanel.add(glyph);

		JScrollPane glyphScroller = new JScrollPane();
		glyphScroller.setViewportView(glyphPanel);

		Container contentPane = getContentPane();
		contentPane.add(treeScroller, java.awt.BorderLayout.WEST);
		contentPane.add(glyphScroller, java.awt.BorderLayout.CENTER);
		contentPane.add(bottomStatusPanel, java.awt.BorderLayout.SOUTH);

		fileMenu.setText("File");
		openMenuItem.setText("Open");
		openMenuItem.setToolTipText("Open XML Log file");
		openMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				openMenuItemActionPerformed(evt);
			}
		});

		fileMenu.add(openMenuItem);

		exitMenuItem.setText("Exit");
		exitMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				exitApplication();
			}
		});

		fileMenu.add(exitMenuItem);

		menuBar.add(fileMenu);

		setJMenuBar(menuBar);

		java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit()
				.getScreenSize();
		setBounds((screenSize.width - 800) / 2, (screenSize.height - 600) / 2,
				800, 600);
	}

	private void treeValueChanged(TreeSelectionEvent evt) {
		TreePath treePath  = evt == null ? null : evt.getNewLeadSelectionPath();
		Object selected = treePath == null ? null : treePath.getLastPathComponent();

		if (selected != null && selected instanceof PDFCharPath) {
			PDFCharPath pdfCharPath = (PDFCharPath) selected;

			SVGPath svgPath = new SVGPath(pdfCharPath.getD());

//			int width = glyph.getWidth();
//			if (width < 100)
//				width = 100;
//			int height = glyph.getHeight();
//			if (height < 100)
//				height = 100;
//
//			BufferedImage image = new BufferedImage(width, height,
//					BufferedImage.TYPE_INT_ARGB);
//			Graphics2D g2d = image.createGraphics();
//
//			g2d.setColor(Color.BLACK);
//
//			svgPath.draw(g2d);
//
//			glyph.setIcon(new ImageIcon(image));
//			glyph.validate();
			
			int xoff = 200;
			int yoff = 200;
			int scale = 100;
			int height = scale;
			int width = scale;
			int xoff1 = 0; //width/2;
			int yoff1 = 0; //height/2;
			svgPath = scale(xoff, yoff, scale, svgPath);
			Graphics2D gp = (Graphics2D) glyphPanel.getGraphics();
			gp.setColor(Color.YELLOW);
			gp.fillRect(xoff + xoff1, yoff - height + yoff1, width, height);
			svgPath.setStroke("black");
			gp.setColor(Color.BLACK);
			svgPath.draw(gp);
			gp.drawString("Glyphs", 100, 200);
			
			
		}
	}

	private SVGPath scale(int xoff, int yoff, int scale, SVGPath svgPath) {
//		Real2Range bbox = svgPath.getBoundingBox();
		Transform2 t2 = new Transform2();
		t2.applyScalesToThis(scale, scale);
		t2.setTranslation(new Real2(xoff, yoff));
		svgPath.applyTransform(t2);
		return svgPath;
	}

	private void updateTitle() {
		StringBuilder title = new StringBuilder("PDF2SVG XML Log Display");

		if (currentFilename != null) {
			title.append(" - ");
			title.append(currentFilename);
		}

		if (currentPDFFilename != null) {
			title.append(" - ");
			title.append(currentPDFFilename);
		}

		if (currentPage >= 0 && numberOfPages >= 0) {
			title.append(" (");
			title.append(currentPage + 1);
			title.append("/");
			title.append(numberOfPages);
			title.append(")");
		}

		setTitle(title.toString());
	}

	private void updateStatus(String message) {
		bottomStatusPanel.getStatusLabel().setText(message);
		bottomStatusPanel.validate();
	}

	private void openMenuItemActionPerformed(ActionEvent evt) {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(currentDir);

		ExtensionFileFilter xmllogFilter = new ExtensionFileFilter(
				new String[] { "XML" }, "XML Files");
		chooser.setFileFilter(xmllogFilter);
		int result = chooser.showOpenDialog(Viewer.this);
		if (result == JFileChooser.APPROVE_OPTION) {
			String name = chooser.getSelectedFile().getPath();
			currentDir = new File(name).getParentFile();
			try {
				openXMLLogFile(name);
			} catch (Exception e) {
				e.printStackTrace();
				updateStatus("open XML Log file '" + name + "' failed: " + e);
			}
		}
	}

	private void exitApplication() {
		this.setVisible(false);
		this.dispose();
	}

	public static void main(String[] args) throws Exception {
		Viewer viewer = new Viewer();

		if (args == null || args.length == 0) {
			args = new String[1];
			args[0] = "../pdf2svg/target/";
		}
		String deletePath = ".//page[not(character)] | .//pdf[not(.//character)]";
		viewer.xmlLog.setDeletePath(deletePath);

		if (args.length != 0) {
			if (args.length != 1) {
//				usage();
			}

				 
			try {
				viewer.openXMLLogFile(args[0]);
			} catch (Exception e) {
				System.err.println("open XML Log file '" + args[0]
						+ "' failed: " + e);
				e.printStackTrace(System.err);
			}
		} else {
			// just for test
//			File file = new File("../pdf2svg/target/pdfLog.xml");
//			System.out.println(file.getAbsoluteFile()+" "+file.exists());
//			viewer.openXMLLogFile(file.getAbsolutePath());
		}

		viewer.setVisible(true);
	}

	protected void openXMLLogFile(String filename) throws Exception {

		xmlLog.load(filename);

		currentFilename = filename;
		currentPDFFilename = null;
		currentPage = -1;
		numberOfPages = -1;

		tree.setModel(this);

		int rowCount = tree.getRowCount();
		for (int row = 0; row < rowCount; row++) {
			tree.expandRow(row);
		}

		tree.setRootVisible(true);

		updateTitle();
		updateStatus("XML Log file '" + currentFilename
				+ "' loaded successfully!");

		this.validate();
	}

	/**
	 * Get the bottom status panel.
	 * 
	 * @return The bottom status panel.
	 */
	public ReaderBottomPanel getBottomStatusPanel() {
		return bottomStatusPanel;
	}

	/**
	 * This will print out a message telling how to use this utility.
	 */
	private static void usage() {
		System.err.println("Usage: XMLLogReader [<input-file>]\n"
				+ "    <input-file>   The XML Log file to be loaded\n");
		System.exit(1);
	}

	/**
	 * these methods implement the TreeModel interface ...
	 */

	public Object getRoot() {
		return xmlLog;
	}

	public Object getChild(Object parent, int index) {

		if (parent == null) {
			throw new RuntimeException("parent is NULL!");
		}

		if (xmlLog == null) {
			return null;
		}

		if (parent instanceof XMLLog) {
			switch (index) {
			case 0:
				return xmlLog.getFonts();
			case 1:
				return xmlLog.getPdfs();
			default:
				return null;
			}
		}

		if (parent instanceof PDFFontList) {
			PDFFontList fonts = (PDFFontList) parent;
			return fonts.get(index);
		}

		if (parent instanceof PDFFileList) {
			PDFFileList pdfs = (PDFFileList) parent;
			return pdfs.get(index);
		}

		if (parent instanceof PDFFont) {
			PDFFont pdfFont = (PDFFont) parent;
			switch (index) {
			case 0:
				return "basefont: " + pdfFont.getBasefont();
			case 1:
				return "encoding: " + pdfFont.getEncoding();
			case 2:
				return "family: " + pdfFont.getFamily();
			case 3:
				return "fontencoding: " + pdfFont.getFontencoding();
			case 4:
				return "name: " + pdfFont.getName();
			case 5:
				return "type: " + pdfFont.getType();
			case 6:
				return "bold: " + pdfFont.isBold();
			case 7:
				return "italic: " + pdfFont.isItalic();
			case 8:
				return "symbol: " + pdfFont.isSymbol();
			default:
				return null;
			}
		}

		if (parent instanceof PDFFile) {
			PDFFile pdfFile = (PDFFile) parent;
			switch (index) {
			case 0:
				return "filename: " + pdfFile.getFilename();
			case 1:
				return "pagecount: " + pdfFile.getPagecount();
			case 2:
				return pdfFile.getPagelist();
			default:
				return null;
			}
		}

		if (parent instanceof PDFPageList) {
			PDFPageList pagelist = (PDFPageList) parent;
			return pagelist.get(index);
		}

		if (parent instanceof PDFPage) {
			PDFPage pdfPage = (PDFPage) parent;
			switch (index) {
			case 0:
				return "page: " + pdfPage.getPagenum();
			case 1:
				return pdfPage.getCharlist();
			default:
				return null;
			}
		}

		if (parent instanceof PDFCharList) {
			PDFCharList charlist = (PDFCharList) parent;
			return charlist.get(index);
		}

		if (parent instanceof PDFChar) {
			PDFChar pdfChar = (PDFChar) parent;
			switch (index) {
			case 0:
				return "code: " + pdfChar.getCode();
			case 1:
				return "family: " + pdfChar.getFamily();
			case 2:
				return "font: " + pdfChar.getFont();
			case 3:
				return "name: " + pdfChar.getName();
			case 4:
				if (xmlLog.isLogglyphs())
					return pdfChar.getPath();
			default:
				return null;
			}
		}

		if (parent instanceof PDFCharPath) {
			PDFCharPath pdfCharPath = (PDFCharPath) parent;
			switch (index) {
			case 0:
				return "d: " + pdfCharPath.getD();
			case 1:
				return "fill: " + pdfCharPath.getFill();
			case 2:
				return "stroke: " + pdfCharPath.getStroke();
			case 3:
				return "strokewidth: " + pdfCharPath.getStrokewidth();
			default:
			}
		}

		return null;
	}

	public int getChildCount(Object parent) {

		if (parent == null) {
			throw new RuntimeException("parent is NULL!");
		}

		if (xmlLog == null) {
			return 0;
		}

		if (parent instanceof XMLLog) {
			return 2;
		}

		if (parent instanceof PDFFontList) {
			PDFFontList fonts = (PDFFontList) parent;
			return fonts.size();
		}

		if (parent instanceof PDFFileList) {
			PDFFileList pdfs = (PDFFileList) parent;
			return pdfs.size();
		}

		if (parent instanceof PDFFont) {
			return 9;
		}

		if (parent instanceof PDFFile) {
			return 3;
		}

		if (parent instanceof PDFPageList) {
			PDFPageList pagelist = (PDFPageList) parent;
			return pagelist.size();
		}

		if (parent instanceof PDFPage) {
			return 2;
		}
		
		if (parent instanceof PDFCharList) {
			PDFCharList charlist = (PDFCharList) parent;
			return charlist.size();
		}

		if (parent instanceof PDFChar) {
			if (xmlLog.isLogglyphs()) {
				return 5;
			} else {
				return 4;
			}
		}

		if (parent instanceof PDFCharPath) {
			return 4;
		}

		return 0;
	}

	public boolean isLeaf(Object node) {

		if (node == null) {
			throw new RuntimeException("node is NULL!");
		}

		if (node instanceof XMLLog) {
			return false;
		}

		if (node instanceof PDFFontList) {
			return false;
		}

		if (node instanceof PDFFileList) {
			return false;
		}

		if (node instanceof PDFFont) {
			return false;
		}

		if (node instanceof PDFFile) {
			return false;
		}
		
		if (node instanceof PDFPageList) {
			return false;
		}

		if (node instanceof PDFPage) {
			return false;
		}

		if (node instanceof PDFCharList) {
			return false;
		}

		if (node instanceof PDFChar) {
			return false;
		}

		if (node instanceof PDFCharPath) {
			return false;
		}

		return true;
	}

	public void valueForPathChanged(TreePath path, Object newValue) {
		throw new RuntimeException("tree is read-only!");
	}

	public int getIndexOfChild(Object parent, Object child) {

		if (parent == null) {
			throw new RuntimeException("parent is NULL!");
		}

		if (xmlLog == null) {
			return -1;
		}

		if (parent instanceof XMLLog) {
			if (child.equals(xmlLog.getFonts())) {
				return 0;
			}
			if (child.equals(xmlLog.getPdfs())){
				return 1;
			}
			return -1;
		}

		if (parent instanceof PDFFontList) {
			PDFFontList fonts = (PDFFontList) parent;
			return fonts.indexOf(child);
		}

		if (parent instanceof PDFFileList) {
			PDFFileList pdfs = (PDFFileList) parent;
			return pdfs.indexOf(child);
		}

		if (parent instanceof PDFFont) {
			PDFFont pdfFont = (PDFFont) parent;
			if (child.equals("basefont: " + pdfFont.getBasefont())) {
				return 0;
			}
			if (child.equals("encoding: " + pdfFont.getEncoding())) {
				return 1;
			}
			if (child.equals("family: " + pdfFont.getFamily())) {
				return 2;
			}
			if (child.equals("fontencoding: " + pdfFont.getFontencoding())) {
				return 3;
			}
			if (child.equals("name: " + pdfFont.getName())) {
				return 4;
			}
			if (child.equals("type: " + pdfFont.getType())) {
				return 5;
			}
			if (child.equals("bold: " + pdfFont.isBold())){
				return 6;
			}
			if (child.equals("italic: " + pdfFont.isItalic())) {
				return 7;
			}
			if (child.equals("symbol: " + pdfFont.isSymbol())) {
				return 8;
			}
			return -1;
		}

		if (parent instanceof PDFFile) {
			PDFFile pdfFile = (PDFFile) parent;
			if (child.equals("filename: " + pdfFile.getFilename())) {
				return 0;
			}
			if (child.equals("pagecount: " + pdfFile.getPagecount())) {
				return 1;
			}
			if (child.equals(pdfFile.getPagelist())) {
				return 2;
			}
			return -1;
		}

		if (parent instanceof PDFPageList) {
			PDFPageList pagelist = (PDFPageList) parent;
			return pagelist.indexOf(child);
		}

		if (parent instanceof PDFPage) {
			PDFPage pdfPage = (PDFPage) parent;
			if (child.equals("page: " + pdfPage.getPagenum())) {
				return 0;
			}
			if (child.equals(pdfPage.getCharlist())) {
				return 1;
			}
			return -1;
		}

		if (parent instanceof PDFCharList) {
			PDFCharList charlist = (PDFCharList) parent;
			return charlist.indexOf(child);
		}

		if (parent instanceof PDFChar) {
			PDFChar pdfChar = (PDFChar) parent;
			if (child.equals("code: " + pdfChar.getCode())) {
				return 0;
			}
			if (child.equals("family: " + pdfChar.getFamily())) {
				return 1;
			}
			if (child.equals("font: " + pdfChar.getFont())) {
				return 2;
			}
			if (child.equals("name: " + pdfChar.getName())) {
				return 3;
			}
			if (xmlLog.isLogglyphs() && child.equals(pdfChar.getPath())) {
				return 4;
			}
			return -1;
		}

		if (parent instanceof PDFCharPath) {
			PDFCharPath pdfCharPath = (PDFCharPath) parent;
			if (child.equals("d: " + pdfCharPath.getD())) {
				return 0;
			}
			if (child.equals("fill: " + pdfCharPath.getFill())) {
				return 1;
			}
			if (child.equals("stroke: " + pdfCharPath.getStroke())) {
				return 2;
			}
			if (child.equals("strokewidth: " + pdfCharPath.getStrokewidth())) {
				return 3;
			}
			return -1;
		}

		return -1;
	}

	public void addTreeModelListener(TreeModelListener l) {
		setListener(l);
	}

	public void removeTreeModelListener(TreeModelListener l) {
		setListener(null);
	}

	public TreeModelListener getListener() {
		return listener;
	}

	public void setListener(TreeModelListener listener) {
		this.listener = listener;
	}

}
