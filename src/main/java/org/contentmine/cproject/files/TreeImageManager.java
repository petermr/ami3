package org.contentmine.cproject.files;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.util.CMineGlobber;

/** manages the processing of images in a Ctree.
 * required when there are multiple versions of the same image
 * @author pm286
 *
 */
public class TreeImageManager {
	private static final String PIXEL = "pixel";
	private static final String IMAGEDOT = "image.";
	private static final String PAGEDOT = "page.";
	private static final Logger LOG = Logger.getLogger(TreeImageManager.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	/** this is likely to evolve as more types have to be used
	 * 
	 * @author pm286
	 *
	 */
	public enum TreeImageType {
		RAW("raw"),
		SHARPEN("sharpen"),
		THRESHOLD("threshold"),
		;
		private String basename;

		private TreeImageType(String basename) {
			this.basename = basename;
		}
		public static TreeImageType getTypeByBasename(String basename) {
			for (TreeImageType type : values()) {
				if (type.basename.equals(basename)) {
					return type;
				}
			}
			return null;
		}
	}

	private CTree cTree;
	// current file/image stuff. Will change when new file ingested or created
	@Deprecated
	private String oldBasename;
	@Deprecated
	private File oldStyleFile;
	
	private String currentBasename;
	private TreeImageType currentType;  // raw/sharpen4, etc.
	private File currentTypeSubdirectory;
	private File currentPixelDirectory;
	private File currentImageDirectory;
	private File imageTopDirectory;

	public TreeImageManager() {
	}

	/** creates new TreeImageManager with a top directory
	 * 
	 * @param cTree
	 * @param imageTopDirectory
	 * @return null if eitehr arguments null
	 */
	public static TreeImageManager createTreeImageManager(CTree cTree, File imageTopDirectory) {
		TreeImageManager treeImageManager = null;
		if (cTree != null && imageTopDirectory != null) {
			treeImageManager = new TreeImageManager();
			treeImageManager.cTree = cTree;
			treeImageManager.imageTopDirectory = imageTopDirectory;
		}
		return treeImageManager;
	}

	@Deprecated
	/** read old-style filename and convert into new format.
	 * 
	 * @param pngFilename
	 * @return
	 */
	public File getImageFileDerived(String oldPngFilename) {
		oldBasename = FilenameUtils.getBaseName(oldPngFilename);
		getPDFImagesDir();
		oldStyleFile = new File(getPDFImagesDir(), oldPngFilename);
		createCurrentBasenameFromOld();
		return oldStyleFile;
	}

	private void createCurrentBasenameFromOld() {
		if (oldBasename.startsWith(PAGEDOT)) {
			currentBasename = IMAGEDOT + oldBasename.substring(PAGEDOT.length());
		}
	}

	private File getPDFImagesDir() {
		return new File(cTree.directory, CTree.PDF_IMAGES_DIR);
	}

	/** gets current Type subdirectory, and creates it if non-existent.
	 * 
	 * @return
	 */
	public File getMakeTypeSubdirectory() {
		currentTypeSubdirectory = new File(getMakeImageDirectory(),currentType.basename + "/" );
		if (!currentTypeSubdirectory.exists()) {
			currentTypeSubdirectory.mkdirs();
		}
		return currentTypeSubdirectory;
	}

	public File getMakeImageDirectory() {
		File imageTopDirectory = getPDFImagesDir();
		currentImageDirectory = new File(imageTopDirectory, currentBasename + "/");
		if (!currentImageDirectory.exists()) {
			currentImageDirectory.mkdirs();
		}
		return currentImageDirectory;
	}
	
//	public List<File> getImageFiles(TreeImageType type) {
//		
//	}

	/** gets current Pixel subdirectory, and creates it if non-existent.
	 * 
	 * @return
	 */
	public File getMakePixelDir() {
		if (currentPixelDirectory == null) {
			currentPixelDirectory = new File(getMakeTypeSubdirectory(), PIXEL + "/");
			currentPixelDirectory.mkdirs();
		}
		return currentPixelDirectory;
	}

	public TreeImageType getImageType() {
		return currentType;
	}

	public TreeImageManager setImageType(TreeImageType currentType) {
		this.currentType = currentType;
		return this;
	}

	public String getBasename() {
		return currentBasename;
	}

	public TreeImageManager setBasename(String currentBasename) {
		this.currentBasename = currentBasename;
		return this;
	}

	/** lists any *.png, etc. in top directory.
	 * Not recommended for precise work. (Use ImageType and basename structure)
	 * @return
	 */
	public List<File> getRawImageFiles(String suffix) {
		List<File> imageFiles = new ArrayList<File>();
		if (imageTopDirectory != null) {
			imageFiles = CMineGlobber.listSortedChildFiles(imageTopDirectory, suffix);
		}
		return imageFiles;
	}

	/** makes a new directory as child of top directory.
	 * crude probably only for testing.
	 * Not recommended for precise work. (Use ImageType and basename structure)
	 * 
	 * @param outputBasename
	 * @return
	 */
	public File getMakeOutputDirectory(String outputBasename) {
		File outputDirectory = null;
		if (imageTopDirectory != null) {
			outputDirectory = new File(imageTopDirectory, outputBasename + "/");
			outputDirectory.mkdirs();
		}
		return outputDirectory;
	}
	
}
