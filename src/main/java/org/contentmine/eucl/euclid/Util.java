/**
 *    Copyright 2011 Peter Murray-Rust
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

package org.contentmine.eucl.euclid;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;


/**
 * A number of miscellaneous tools. Originally devised for jumbo.sgml, now
 * rewritten for jumbo.xml. Use these at your peril - some will be phased out
 * 
 * @author (C) P. Murray-Rust, 1998
 * @author 20 August 2003
 */
public class Util implements EuclidConstants {
	private static final PrintStream SYSOUT = System.out;

	final static Logger LOG = Logger.getLogger(Util.class);

	/** messages */
	public enum Message {
		/** not yet implemented */
		NYI("not yet implemented"), ;
		/** value */
		public String value;

		private Message(String v) {
			value = v;
		}
	}

	private static final String PM286 = "pm286";

	public final static String[] LOWER_ROMAN_NUMERAL = { "i", "ii", "iii",
			"iv", "v", "vi", "vii", "viii", "ix", "x", "xi", "xii", "xiii",
			"xiv", "xv", "xvi", "xvii", "xviii", "xix", "xx", "xxi", "xxii",
			"xxiii", "xxiv", "xxv", "xxvi", "xxvii", "xxviii", "xxix", "xxx",
			"xxxi", "xxxii", "xxxiii", "xxxiv", "xxxv", "xxxvi", "xxxvii",
			"xxxviii", "xxxix", "xl", "xli", "xlii", "xliii", "xliv", "xlv",
			"xlvi", "xlvii", "xlviii", "xlix", "l", };

	/**
	 * regex matching roman numbers up to m ("[ivxlcdm]+").
	 */
	public final static String LOWER_ROMAN_REGEX = "[ivxlcdm]+";
	/**
	 * regex matching roman numbers up to M ("[IVXLCDM]+").
	 */
	public final static String UPPER_ROMAN_REGEX = "[IVXLCDM]+";

	private final static File TEMP_DIRECTORY = new File("target"
			+ File.separator + "test-outputs");

	/**
	 * get temporary directory - mainly for testing methods with outputs. calls
	 * mkdirs() if does not exist
	 * 
	 * @return temporary directory.
	 */
	public static File getTEMP_DIRECTORY() {
		if (!TEMP_DIRECTORY.exists()) {
			boolean ok = TEMP_DIRECTORY.mkdirs();
			if (!ok) {
				throw new RuntimeException(
						"Cannot create temporary directory : "
								+ TEMP_DIRECTORY.getAbsolutePath());
			}
		}
		return TEMP_DIRECTORY;
	}

	/**
	 * get class-specific temporary directory - mainly for testing methods with
	 * ouputs. calls mkdirs() if does not exist
	 * 
	 * @param classx
	 * @return temporary directory.
	 */
	public static File getTestOutputDirectory(Class<?> classx) {
		File tempDir = getTEMP_DIRECTORY();
		String dirs = classx.getName().replace(S_PERIOD, File.separator);
		File testDir = new File(tempDir, dirs);
		if (!testDir.exists()) {
			boolean ok = testDir.mkdirs();
			if (!ok) {
				throw new RuntimeException(
						"Cannot create temporary class directory : "
								+ testDir.getAbsolutePath());
			}
		}
		return testDir;

	}

	/**
	 * convenience method to extend array of Strings.
	 * 
	 * @param array
	 *            to extend
	 * @param s
	 *            element to add
	 * @return extended array
	 */
	public final static String[] addElementToStringArray(String[] array,
			String s) {
		int l = array.length;
		String[] array1 = new String[l + 1];
		for (int i = 0; i < l; i++) {
			array1[i] = array[i];
		}
		array1[l] = s;
		return array1;
	}

	/**
	 * convenience method to remove element from array of Strings.
	 * 
	 * Removes ALL occurrences of string
	 * 
	 * @param array
	 *            to edit
	 * @param s
	 *            element to remove
	 * @return depleted array
	 */
	public final static String[] removeElementFromStringArray(String[] array,
			String s) {
		List<String> sList = new ArrayList<String>();
		for (int i = 0; i < array.length; i++) {
			if (!array[i].equals(s)) {
				sList.add(array[i]);
			}
		}
		return (String[]) sList.toArray(new String[0]);
	}

	/**
	 * traps a bug. use for programming errors where this could can "never be
	 * reached" concatenates msg with "BUG" and throws {@link RuntimeException}
	 * 
	 * @param msg
	 * @param e
	 */
	public static void BUG(String msg, Exception e) {
		msg = (msg == null || msg.trim().length() == 0) ? EC.S_EMPTY : EC.S_LBRAK
				+ msg + EC.S_RBRAK;
		throw new RuntimeException("BUG: " + msg + "should never throw: " + e,
				e);
	}

	/**
	 * traps a bug. empty message.
	 * 
	 * @see #BUG(String, Throwable)
	 * @param e
	 */
	public static void BUG(Exception e) {
		BUG(S_EMPTY, e);
	}

	/**
	 * convenience method for "not yet implemented". deliberately deprecated so
	 * that it requires deprecated on all modules containing NYI
	 * 
	 * @deprecated
	 * @throws RuntimeException
	 */
	public static void throwNYI() {
		throw new RuntimeException(Message.NYI.value);
	}

	/**
	 * traps a bug.
	 * 
	 * @see #BUG(String, Throwable)
	 * @param msg
	 */
	public static void BUG(String msg) {
		BUG(msg, new RuntimeException());
	}

	/**
	 * convenience method to get input stream from resource. the resource is
	 * packaged with the classes for distribution. typical filename is
	 * org/contentmine/molutil/elementdata.xml for file elementdata.xml in class
	 * hierarchy org.contentmine.molutil
	 * 
	 * @param filename
	 *            relative to current class hierarchy.
	 * @return input stream
	 * @throws IOException
	 */
	public static InputStream getInputStreamFromResource(String filename)
			throws IOException {
		return getResource(filename).openStream();
	}

	/**
	 * creates directories and files if they don't exist. creates dir/filename
	 * 
	 * @param dir
	 * @param filename
	 * @throws IOException
	 */
	public static void createFile(File dir, String filename) throws IOException {
		File file = new File(dir + File.separator + filename);
		if (!dir.exists()) {
			boolean ok = dir.mkdirs();
			if (!ok) {
				throw new IOException("cannot make dictories: " + dir + EC.S_SPACE
						+ filename);
			}
		}
		if (!file.exists()) {
			file.createNewFile();
		}
	}

	/**
	 * creates resource from filename. uses ClassLoader.getResource()
	 * 
	 * @param filename
	 *            name relative to classroot
	 * @return url or null
	 */
	public static URL getResource(String filename) {
		URL url = null;
		if (filename != null) {
			ClassLoader l = Util.class.getClassLoader();
			url = l.getResource(filename);
			if (url == null) {
				throw new RuntimeException("No resource with name " + filename);
			}
		}
		return url;
	}

	public static InputStream getResourceUsingContextClassLoader(String name, Class<?> clazz) throws FileNotFoundException {

		 ClassLoader cl = Thread.currentThread().getContextClassLoader();
		 if (cl == null) {
			 cl = clazz.getClassLoader();
		 }
		 InputStream is = cl.getResourceAsStream(name);
		 if (is == null) {
		   throw new FileNotFoundException("Resource not found: "+name);
		 }
		 return is;
	}
	
	/**
	 * gets file from build path components. pm286 is not quite sure how it does
	 * this...
	 * 
	 * @author ojd20@cam.ac.uk
	 * @param path
	 * @return file or null
	 * @throws URISyntaxException
	 */
	public static File getResourceFile(String... path)
			throws URISyntaxException {
		File f = new File(Util.class.getClassLoader().getResource(
				buildPath(path)).toURI());
		return f;
	}

	/**
	 * gets build path from its components.
	 * 
	 * @param parts
	 * @return build path concatenated with File.separatorChar
	 */
	public static String buildPath(String... parts) {
		StringBuilder sb = new StringBuilder(parts.length * 20);
		for (String part : parts) {
			sb.append(part).append(File.separatorChar);
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	/**
	 * delete a file If directory==true then file will be recursively deleted
	 * 
	 * @param file
	 *            Description of the Parameter
	 * @param deleteDirectory
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 */
	public static boolean deleteFile(File file, boolean deleteDirectory) {
		if (file.exists()) {
			if (file.isDirectory() && deleteDirectory) {
				String[] filenames = file.list();
				for (int i = 0; i < filenames.length; i++) {
					File childFile = new File(file.toString() + File.separator
							+ filenames[i]);
					deleteFile(childFile, deleteDirectory);
				}
			}
			return file.delete();
		} else {
			return false;
		}
	}

	/**
	 * copy one file to another (I suspect there is a better way
	 * 
	 * @param inFile
	 *            Description of the Parameter
	 * @param outFile
	 *            Description of the Parameter
	 * @exception FileNotFoundException
	 *                Description of the Exception
	 * @exception IOException
	 *                Description of the Exception
	 */
	public static void copyFile(File inFile, File outFile)
			throws FileNotFoundException, IOException {
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
				inFile));
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(outFile));
		byte[] buffer = new byte[10000];
		while (true) {
			int b = bis.read(buffer);
			if (b == -1) {
				break;
			}
			bos.write(buffer, 0, b);
		}
		bis.close();
		bos.close();
	}

	/**
	 * reads a stream from url and outputs it as integer values of the
	 * characters and as strings. Emulates UNIX od().
	 * 
	 * @param url
	 *            Description of the Parameter
	 * @return String tabular version of input (in 10-column chunks)
	 * @exception Exception
	 *                Description of the Exception
	 */
	public static String dump(URL url) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(url
				.openStream()));
		int count = 0;
		StringBuffer sb = new StringBuffer();
		String s0 = "\n";
		String s1 = EC.S_EMPTY;
		while (true) {
			int i = br.read();
			if (i == -1) {
				break;
			}
			String s = "   " + i;
			while (s.length() > 4) {
				s = s.substring(1);
			}
			s0 += s;
			if (i >= 32 && i < 128) {
				s1 += (char) i;
			} else {
				s1 += EC.S_SPACE;
			}
			if (++count % 10 == 0) {
				sb.append(s0 + "   " + s1);
				s1 = EC.S_EMPTY;
				s0 = "\n";
			}
		}
		if (count != 0) {
			sb.append(s0 + "   " + s1);
		}
		return sb.toString();
	}

	/**
	 * make a String of a given number of spaces
	 * 
	 * @param nspace
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 */
	public static String spaces(int nspace) {
		if (nspace <= 0) {
			return EC.S_EMPTY;
		} else {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < nspace; i++) {
				sb.append(S_SPACE);
			}
			return sb.toString();
		}
	}

	/**
	 * 
	 * gets suffix from filename
	 * 
	 * 
	 * 
	 * @param filename
	 *            Description of the Parameter
	 * 
	 * @return The suffix value
	 * 
	 */
	public static String getSuffix(String filename) {
		int idx = filename.lastIndexOf(Util.S_PERIOD);
		if (idx == -1) {
			return null;
		}
		return filename.substring(idx + 1, filename.length());
	}

	/**
	 * return the first n characters of a string and add ellipses if truncated
	 * 
	 * @param s
	 * @param maxlength
	 * @return String the (possibly) truncated string
	 */
	public static String truncateAndAddEllipsis(String s, int maxlength) {
		if (s != null) {
			int l = s.length();
			s = (l <= maxlength) ? s : s.substring(0, maxlength) + " ... ";
		}
		return s;
	}

	/**
	 * return the first n characters of a string and add ellipses if truncated
	 * 
	 * @param s
	 * @param maxlength
	 * @return String the (possibly) truncated string
	 */
	public static String truncateAndAddNewlinesAndEllipsis(String s,
			int maxlength) {
		return (s == null) ? null : truncateAndAddEllipsis(s.replace(S_NEWLINE,
				"\\n"), maxlength);
	}

	/**
	 * remove balanced quotes from ends of (trimmed) string, else no action
	 * 
	 * @param s
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 */
	public static String deQuote(String s) {
		if (s == null) {
			return null;
		}
		String ss = s.trim();
		if (ss.equals(S_EMPTY)) {
			return ss;
		}
		char c = ss.charAt(0);
		if (c == '"' || c == '\'') {
			int l = ss.length();
			if (ss.charAt(l - 1) == c) {
				return ss.substring(1, l - 1);
			}
		}
		return s;
	}

	/**
	 * remove trailing blanks
	 * 
	 * @param s
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 */
	public static String rightTrim(String s) {
		if (s == null) {
			return null;
		}
		if (s.trim().equals(S_EMPTY)) {
			return EC.S_EMPTY;
		}
		int l = s.length();
		while (l >= 0) {
			if (!Character.isWhitespace(s.charAt(--l))) {
				l++;
				break;
			}
		}
		return s.substring(0, l);
	}

	/**
	 * remove leading blanks
	 * 
	 * @param s
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 */
	public static String leftTrim(String s) {
		if (s == null) {
			return null;
		}
		if (s.trim().equals(S_EMPTY)) {
			return EC.S_EMPTY;
		}
		int l = s.length();
		for (int i = 0; i < l; i++) {
			if (s.charAt(i) != ' ') {
				return s.substring(i);
			}
		}
		return s;
	}

/**
	 * return index of balanced bracket. String MUST start with a generic LH
	 * bracket (e.g. '{', '<', '(' '[')
	 * 
	 * @param lbrack
	 *            starting character
	 * @param s
	 *            string to search
	 * @return index of bracket or -1 if none
	 */
	public static int indexOfBalancedBracket(char lbrack, String s) {
		if (s == null) {
			return -1;
		}
		if (s.charAt(0) != lbrack) {
			return -1;
		}
		char rbrack = ' ';
		if (lbrack == '(') {
			rbrack = ')';
		} else if (lbrack == '<') {
			rbrack = '>';
		} else if (lbrack == '[') {
			rbrack = ']';
		} else if (lbrack == '{') {
			rbrack = '}';
		}
		int l = s.length();
		int i = 0;
		int level = 0;
		while (i < l) {
			if (s.charAt(i) == lbrack) {
				level++;
			} else if (s.charAt(i) == rbrack) {
				level--;
				if (level == 0) {
					return i;
				}
			}
			i++;
		}
		return -1;
	}

	/** finds first different character in strings.
	 * null strings are treated as zero length.
	 * end of string is treated as pseudo character
	 * 
	 * s1="abc" s2="abde" => 2
	 * s1="abc" s2 = "abc" => 2
	 * s1=null s2="abc" => 0
	 * s1="abc" s2="abcd" = 3
	 * 
	 * @param s1
	 * @param s2
	 * @return 
	 */
	public final static int indexOfFirstDiferentChar(String s1, String s2) {
		int index = 0;
		if (s1 == null || s2 == null) {
			return 0;
		}
		int l1 = s1.length();
		int l2 = s2.length();
		int min = Math.min(l1,l2);
		for (;index < min; index++) {
			if (s1.charAt(index) != s2.charAt(index)) {
				return index;
			}
		}
		return index;
		
	}
	/**
	 * parse comma-separated Strings Note fields can be EC.S_EMPTY (as in ,,,) and
	 * fields can be quoted "...". If so, embedded quotes are represented as
	 * EC.S_EMPTY, for example A," this is a EC.S_EMPTYBS_EMPTY character",C. An
	 * unbalanced quote returns a mess
	 * 
	 * @param s
	 *            Description of the Parameter
	 * @return List the vector of Strings - any error returns null
	 * @exception RuntimeException
	 *                missing quote
	 */
	public static List<String> getCommaSeparatedStrings(String s)
			throws RuntimeException {
		if (s == null) {
			return null;
		}
		String s0 = s;
		s = s.trim();
		List<String> v = new ArrayList<String>();
		while (!s.equals(S_EMPTY)) {
			if (s.startsWith(S_QUOT)) {
				String temp = EC.S_EMPTY;
				s = s.substring(1);
				while (true) {
					int idx = s.indexOf(S_QUOT);
					if (idx == -1) {
						throw new RuntimeException("Missing Quote:" + s0
								+ EC.S_COLON);
					}
					int idx2 = s.indexOf(S_QUOT + EC.S_QUOT);
					// next quote is actually EC.S_EMPTY
					if (idx2 == idx) {
						temp += s.substring(0, idx) + EC.S_QUOT;
						s = s.substring(idx + 2);
						// single quote
					} else {
						temp += s.substring(0, idx);
						s = s.substring(idx + 1);
						break;
					}
				}
				v.add(temp);
				if (s.startsWith(S_COMMA)) {
					s = s.substring(1);
				} else if (s.equals(S_EMPTY)) {
				} else {
					throw new RuntimeException("Unbalanced Quotes:" + s0
							+ EC.S_COLON);
				}
			} else {
				int idx = s.indexOf(S_COMMA);
				// end?
				if (idx == -1) {
					v.add(s);
					break;
				} else {
					// another comma
					String temp = s.substring(0, idx);
					v.add(temp);
					s = s.substring(idx + 1);
					if (s.equals(S_EMPTY)) {
						v.add(s);
						break;
					}
				}
			}
		}
		return v;
	}

	/**
	 * create comma-separated Strings fields include a comma or a " they are
	 * wrapped with quotes ("). Note fields can be EC.S_EMPTY (as in ,,,) and
	 * fields can be quoted "...". If so, embedded quotes are represented as
	 * EC.S_EMPTY, for example A," this is a EC.S_EMPTYBS_EMPTY character",C.
	 * 
	 * @param v
	 *            vector of strings to be concatenated (null returns null)
	 * @return String the concatenated string - any error returns null
	 */
	public static String createCommaSeparatedStrings(List<String> v) {
		if (v == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < v.size(); i++) {
			String s = v.get(i).toString();
			s = Util.substituteStrings(s, new String[] { EC.S_QUOT },
					new String[] { EC.S_QUOT + EC.S_QUOT });
			// wrap in quotes to escape comma or other quotes
			if (s.indexOf(S_COMMA) != -1 || s.indexOf(S_QUOT) != -1) {
				s = EC.S_QUOT + s + EC.S_QUOT;
			}
			if (i > 0) {
				sb.append(S_COMMA);
			}
			sb.append(s);
		}
		return sb.toString();
	}

	/**
	 * concatenate strings into quote-separated string
	 * 
	 * @param s
	 *            Description of the Parameter
	 * @return String concatenated string
	 */
	public static String quoteConcatenate(String[] s) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length; i++) {
			if (i > 0) {
				sb.append(S_SPACE);
			}
			boolean quote = false;
			if (s[i].indexOf(S_SPACE) != -1) {
				sb.append(S_QUOT);
				quote = true;
			}
			sb.append(s[i]);
			if (quote) {
				sb.append(S_QUOT);
			}
		}
		return sb.toString();
	}

	/**
	 * get the index of a String in an array
	 * 
	 * @param string
	 *            Description of the Parameter
	 * @param strings
	 *            Description of the Parameter
	 * @param ignoreCase
	 *            ignore case
	 * @return index of string else -1 if not found
	 */
	public static int indexOf(String string, String[] strings,
			boolean ignoreCase) {
		if (string == null || strings == null) {
			return -1;
		}
		for (int i = 0; i < strings.length; i++) {
			if (ignoreCase) {
				if (string.equalsIgnoreCase(strings[i])) {
					return i;
				}
			} else {
				if (string.equals(strings[i])) {
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * remove balanced (well-formed) markup from a string. Crude (that is not
	 * fully XML-compliant);</BR> Example: "This is &lt;A
	 * HREF="foo"&gt;bar&lt;/A&gt; and &lt;/BR&gt; a break" goes to "This is bar
	 * and a break"
	 * 
	 * @param s
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 */
	public static String removeHTML(String s) {
		StringBuilder sb = new StringBuilder();
		while (true) {
			int idx = s.indexOf("<");
			if (idx == -1) {
				sb.append(s);
				break;
			} else {
				sb.append(s.substring(0, idx));
				s = s.substring(idx);
				idx = s.indexOf('>');
				if (idx == -1) {
					throw new RuntimeException("missing >");
				} else {
					s = s.substring(idx + 1);
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 
	 * Warning message - nothing fancy at present
	 * 
	 * 
	 * 
	 * @param s
	 *            Description of the Parameter
	 * 
	 */
	public static void warning(String s) {
		LOG.info("WARNING: " + s);
	}

	/**
	 * 
	 * message - nothing fancy at present
	 * 
	 * 
	 * 
	 * @param s
	 *            Description of the Parameter
	 * 
	 */
	public static void message(String s) {
		LOG.info(s);
	}

	// static jumbo.xml.gui.XText errorText;
	/**
	 * 
	 * Error message - nothing fancy at present. Display in Text frame
	 * 
	 * 
	 * 
	 * @param s
	 *            Description of the Parameter
	 * 
	 */
	public static void error(String s) {
		// if (errorText == null) {
		// errorText = new jumbo.xml.gui.XText();
		// errorText.displayInFrame();
		// }
		LOG.info("ERROR: " + s);
		// errorText.addText(s);
	}

	/**
	 * 
	 * record that we have hit a program bug!!!
	 * 
	 * 
	 * 
	 * @param s
	 *            Description of the Parameter
	 * 
	 */
	// public static void bug(String s) {
	// bug(new Exception(s));
	// }
	/**
	 * traps a bug. use for programming errors where this could can "never be
	 * reached" concatenates msg with "BUG" and throws {@link RuntimeException}
	 * 
	 * @param msg
	 * @param t
	 */
	public static void BUG(String msg, Throwable t) {
		msg = (msg == null || msg.trim().length() == 0) ? EC.S_EMPTY : EC.S_LBRAK
				+ msg + EC.S_RBRAK;
		throw new RuntimeException("BUG: " + msg + "should never throw", t);
	}

	/**
	 * traps a bug. empty message.
	 * 
	 * @see #BUG(String, Throwable)
	 * @param t
	 */
	public static void BUG(Throwable t) {
		BUG(S_EMPTY, t);
	}

	/** file separator. */
	final static String FS = System.getProperty("file.separator");

	/**
	 * 
	 * create new file, including making directory if required This seems to be
	 * 
	 * a mess - f.createNewFile() doesn't seem to work A directory should have
	 * 
	 * a trailing file.separator
	 * 
	 * 
	 * 
	 * @param fileName
	 *            Description of the Parameter
	 * 
	 * @return Description of the Return Value
	 * 
	 * @exception IOException
	 *                Description of the Exception
	 * 
	 */
	// public static File createNewFile(String fileName) throws IOException {
	// File f = null;
	// String path = null;
	// int idx = fileName.lastIndexOf(FS);
	// if (idx != -1) {
	// path = fileName.substring(0, idx);
	// // fileN = fileName.substring(idx+1);
	// }
	// // try {
	// if (path != null) {
	// f = new File(path);
	// f.mkdirs();
	// }
	// if (!fileName.endsWith(FS)) {
	// f = new File(fileName);
	// }
	// // } catch (IOException e) {
	// // logger.info("Failed to create: "+fileName+S_LBRAK+e+S_RBRAK);
	// // }
	// return f;
	// }
	/**
	 * get current directory
	 * 
	 * @return The pWDName value
	 */
	public static String getPWDName() {
		File f = new File(S_PERIOD);
		return new File(f.getAbsolutePath()).getParent();
	}

	/**
	 * create new file, including making directory if required This seems to be
	 * a mess - f.createNewFile() doesn't seem to work A directory should have a
	 * trailing file.separator
	 * 
	 * @param fileName
	 * @return file
	 * 
	 * @exception IOException
	 */
	public static File createNewFile(String fileName) throws IOException {
		File f = null;
		String path = null;
		int idx = fileName.lastIndexOf(FS);
		if (idx != -1) {
			path = fileName.substring(0, idx);
		}
		if (path != null) {
			f = new File(path);
			f.mkdirs();
		}
		if (!fileName.endsWith(FS)) {
			f = new File(fileName);
		}
		return f;
	}

	/**
	 * 
	 * make substitutions in a string. If oldSubtrings = "A" and newSubstrings =
	 * "aa" then count occurrences of "A" in s are replaced with "aa", etc.
	 * 
	 * "AAA" count=2 would be replaced by "aaaaA"
	 * 
	 * @param s
	 * @param oldSubstring
	 * @param newSubstring
	 * @param count
	 * @return new string
	 * 
	 */
	public static String substituteString(String s, String oldSubstring,
			String newSubstring, int count) {
		if (count <= 0) {
			count = Integer.MAX_VALUE;
		}
		StringBuffer sb = new StringBuffer();
		int lo = oldSubstring.length();
		for (int i = 0; i < count; i++) {
			int idx = s.indexOf(oldSubstring);
			if (idx == -1) {
				break;
			}
			sb.append(s.substring(0, idx));
			sb.append(newSubstring);
			s = s.substring(idx + lo);
		}
		sb.append(s);
		return sb.toString();
	}

	/**
	 * make substitutions in a string. If oldSubtrings = {"A", "BB", "C"} and
	 * newSubstrings = {"aa", "b", "zz"} then every occurrence of "A" in s is
	 * 
	 * replaced with "aa", etc. "BBB" would be replaced by "bB"
	 * 
	 * @param s
	 * @param oldSubstrings
	 * @param newSubstrings
	 * 
	 * @return Description of the Return Value
	 * @throws RuntimeException
	 */
	public static String substituteStrings(String s, String[] oldSubstrings,
			String[] newSubstrings) {
		int ol = oldSubstrings.length;
		int nl = newSubstrings.length;
		if (ol != nl) {
			throw new RuntimeException(
					"Util.substituteStrings  arguments of different lengths: "
							+ ol + EC.S_SLASH + nl);
		}
		for (int i = 0; i < ol; i++) {
			String oldS = oldSubstrings[i];
			String newS = newSubstrings[i];
			int lo = oldS.length();
			if (s.indexOf(oldS) == -1) {
				continue;
			}
			String ss = EC.S_EMPTY;
			while (true) {
				int idx = s.indexOf(oldS);
				if (idx == -1) {
					ss += s;
					break;
				}
				ss += s.substring(0, idx) + newS;
				s = s.substring(idx + lo);
			}
			s = ss;
		}
		return s;
	}

	/**
	 * 
	 * substitute characters with =Hex values. Thus "=2E" is translated to
	 * 
	 * char(46); A trailing EQUALS (continuation line is not affected, nor is
	 * 
	 * any non-hex value
	 * 
	 * 
	 * 
	 */
	static String[] dosEquivalents = { EC.S_EMPTY + (char) 12,
	// ??
			S_EMPTY + (char) 127,
			// ??
			S_EMPTY + (char) 128,
			// Ccedil
			S_EMPTY + (char) 129,
			// uuml
			S_EMPTY + (char) 130,
			// eacute
			S_EMPTY + (char) 131,
			// acirc
			S_EMPTY + (char) 132,
			// auml
			S_EMPTY + (char) 133,
			// agrave
			S_EMPTY + (char) 134,
			// aring
			S_EMPTY + (char) 135,
			// ccedil
			S_EMPTY + (char) 136,
			// ecirc
			S_EMPTY + (char) 137,
			// euml
			S_EMPTY + (char) 138,
			// egrave
			S_EMPTY + (char) 139,
			// iuml
			S_EMPTY + (char) 140,
			// icirc
			S_EMPTY + (char) 141,
			// igrave
			S_EMPTY + (char) 142,
			// Auml
			S_EMPTY + (char) 143,
			// Aring
			S_EMPTY + (char) 144,
			// Eacute
			S_EMPTY + (char) 145,
			// aelig
			S_EMPTY + (char) 146,
			// ff?
			S_EMPTY + (char) 147,
			// ocirc
			S_EMPTY + (char) 148,
			// ouml
			S_EMPTY + (char) 149,
			// ograve
			S_EMPTY + (char) 150,
			// ucirc
			S_EMPTY + (char) 151,
			// ugrave
			S_EMPTY + (char) 152,
			// yuml
			S_EMPTY + (char) 153,
			// Ouml
			S_EMPTY + (char) 154,
			// Uuml
			S_EMPTY + (char) 155,
			// ??
			S_EMPTY + (char) 156,
			// ??
			S_EMPTY + (char) 157,
			// ??
			S_EMPTY + (char) 158,
			// ??
			S_EMPTY + (char) 159,
			// ??
			S_EMPTY + (char) 160,
			// aacute
			S_EMPTY + (char) 161,
			// iacute
			S_EMPTY + (char) 162,
			// oacute
			S_EMPTY + (char) 163,
			// uacute
			S_EMPTY + (char) 164,
			// nwave?
			S_EMPTY + (char) 165,
			// Nwave?
			S_EMPTY + (char) 166,
			// ??
			S_EMPTY + (char) 167,
			// ??
			S_EMPTY + (char) 168,
			// ??
			S_EMPTY + (char) 169,
			// ??
			S_EMPTY + (char) 170,
			// 170
			S_EMPTY + (char) 171,
			// ??
			S_EMPTY + (char) 172,
			// ??
			S_EMPTY + (char) 173,
			// ??
			S_EMPTY + (char) 174,
			// ??
			S_EMPTY + (char) 175,
			// ??
			S_EMPTY + (char) 176,
			// ??
			S_EMPTY + (char) 177,
			// ??
			S_EMPTY + (char) 178,
			// ??
			S_EMPTY + (char) 179,
			// ??
			S_EMPTY + (char) 180,
			// 180
			//
			S_EMPTY + (char) 192,
			// eacute
			S_EMPTY + (char) 248,
			// degrees
			S_EMPTY + (char) 352,
			// egrave
			S_EMPTY + (char) 402,
			// acirc
			S_EMPTY + (char) 710,
			// ecirc
			S_EMPTY + (char) 8218,
			// eacute
			S_EMPTY + (char) 8221,
			// ouml
			S_EMPTY + (char) 8222,
			// auml
			S_EMPTY + (char) 8225,
			// ccedil
			S_EMPTY + (char) 8230,
			// agrave
			S_EMPTY + (char) 8240,
			// euml
			S_EMPTY + (char) 65533,
	// uuml
	};

	static String[] asciiEquivalents = { EC.S_EMPTY,
	// ??
			S_EMPTY + (char) 0,
			// ??
			S_EMPTY + (char) 199,
			// Ccedil
			S_EMPTY + (char) 252,
			// uuml
			S_EMPTY + (char) 233,
			// eacute
			S_EMPTY + (char) 226,
			// acirc
			S_EMPTY + (char) 228,
			// auml
			S_EMPTY + (char) 224,
			// agrave
			S_EMPTY + (char) 229,
			// aring
			S_EMPTY + (char) 231,
			// ccedil
			S_EMPTY + (char) 234,
			// ecirc
			S_EMPTY + (char) 235,
			// euml
			S_EMPTY + (char) 232,
			// egrave
			S_EMPTY + (char) 239,
			// iuml
			S_EMPTY + (char) 238,
			// icirc
			S_EMPTY + (char) 236,
			// igrave
			S_EMPTY + (char) 196,
			// Auml
			S_EMPTY + (char) 197,
			// Aring
			S_EMPTY + (char) 201,
			// Eacute
			S_EMPTY + (char) 230,
			// aelig
			S_EMPTY + (char) 0,
			// ff?
			S_EMPTY + (char) 244,
			// ocirc
			S_EMPTY + (char) 246,
			// ouml
			S_EMPTY + (char) 242,
			// ograve
			S_EMPTY + (char) 251,
			// ucirc
			S_EMPTY + (char) 249,
			// ugrave
			S_EMPTY + (char) 255,
			// yuml
			S_EMPTY + (char) 214,
			// Ouml
			S_EMPTY + (char) 220,
			// Uuml
			S_EMPTY + (char) 0,
			// ff?
			S_EMPTY + (char) 0,
			// ff?
			S_EMPTY + (char) 0,
			// ff?
			S_EMPTY + (char) 0,
			// ff?
			S_EMPTY + (char) 0,
			// ff?
			S_EMPTY + (char) 225,
			// aacute
			S_EMPTY + (char) 237,
			// iacute
			S_EMPTY + (char) 243,
			// oacute
			S_EMPTY + (char) 250,
			// uacute
			S_EMPTY + (char) 241,
			// nwave?
			S_EMPTY + (char) 209,
			// Nwave?
			S_EMPTY + (char) 0,
			// ??
			S_EMPTY + (char) 0,
			// ??
			S_EMPTY + (char) 0,
			// ??
			S_EMPTY + (char) 0,
			// ??
			S_EMPTY + (char) 0,
			// 170
			S_EMPTY + (char) 0,
			// ??
			S_EMPTY + (char) 0,
			// ??
			S_EMPTY + (char) 0,
			// ??
			S_EMPTY + (char) 0,
			// ??
			S_EMPTY + (char) 0,
			// ??
			S_EMPTY + (char) 0,
			// ??
			S_EMPTY + (char) 0,
			// ??
			S_EMPTY + (char) 0,
			// ??
			S_EMPTY + (char) 0,
			// ??
			S_EMPTY + (char) 0,
			// 180
			//
			S_EMPTY + (char) 233,
			// eacute
			"[degrees]",
			// degrees
			S_EMPTY + (char) 232,
			// egrave
			S_EMPTY + (char) 226,
			// acirc
			S_EMPTY + (char) 234,
			// ecirc
			S_EMPTY + (char) 233,
			// eacute
			S_EMPTY + (char) 246,
			// ouml
			S_EMPTY + (char) 228,
			// auml
			S_EMPTY + (char) 231,
			// ccedil
			S_EMPTY + (char) 224,
			// agrave
			S_EMPTY + (char) 235,
			// euml
			S_EMPTY + (char) 252,
	// uuml
	};

	/**
	 * isolatin entities
	 */
	public final static String[] ISOLATIN_ENTITIES = new String[256];
	/** */
	public final static int ISOLATIN_LO = 160;
	/** */
	public final static int ISOLATIN_HI = 255;
	static {
		// ones before 160 are null
		ISOLATIN_ENTITIES[160] = "_nbsp_";
		ISOLATIN_ENTITIES[161] = "_iexcl_";
		ISOLATIN_ENTITIES[162] = "_cent_";
		ISOLATIN_ENTITIES[163] = "_pound_";
		ISOLATIN_ENTITIES[164] = "_curren_";
		ISOLATIN_ENTITIES[165] = "_yen_";
		ISOLATIN_ENTITIES[166] = "_brvbar_";
		ISOLATIN_ENTITIES[167] = "_sect_";
		ISOLATIN_ENTITIES[168] = "_uml_";
		ISOLATIN_ENTITIES[169] = "_copy_";
		ISOLATIN_ENTITIES[170] = "_ordf_";
		ISOLATIN_ENTITIES[171] = "_laquo_";
		ISOLATIN_ENTITIES[172] = "_not_";
		ISOLATIN_ENTITIES[173] = "_shy_";
		ISOLATIN_ENTITIES[174] = "_reg_";
		ISOLATIN_ENTITIES[175] = "_macr_";
		ISOLATIN_ENTITIES[176] = "_deg_";
		ISOLATIN_ENTITIES[177] = "_plusmn_";
		ISOLATIN_ENTITIES[178] = "_sup2_";
		ISOLATIN_ENTITIES[179] = "_sup3_";
		ISOLATIN_ENTITIES[180] = "_acute_";
		ISOLATIN_ENTITIES[181] = "_micro_";
		ISOLATIN_ENTITIES[182] = "_para_";
		ISOLATIN_ENTITIES[183] = "_middot_";
		ISOLATIN_ENTITIES[184] = "_cedil_";
		ISOLATIN_ENTITIES[185] = "_sup1_";
		ISOLATIN_ENTITIES[186] = "_ordm_";
		ISOLATIN_ENTITIES[187] = "_raquo_";
		ISOLATIN_ENTITIES[188] = "_frac14_";
		ISOLATIN_ENTITIES[189] = "_frac12_";
		ISOLATIN_ENTITIES[190] = "_frac34_";
		ISOLATIN_ENTITIES[191] = "_iquest_";
		ISOLATIN_ENTITIES[192] = "_Agrave_";
		ISOLATIN_ENTITIES[193] = "_Aacute_";
		ISOLATIN_ENTITIES[194] = "_Acirc_";
		ISOLATIN_ENTITIES[195] = "_Atilde_";
		ISOLATIN_ENTITIES[196] = "_Auml_";
		ISOLATIN_ENTITIES[197] = "_Aring_";
		ISOLATIN_ENTITIES[198] = "_AElig_";
		ISOLATIN_ENTITIES[199] = "_Ccedil_";
		ISOLATIN_ENTITIES[200] = "_Egrave_";
		ISOLATIN_ENTITIES[201] = "_Eacute_";
		ISOLATIN_ENTITIES[202] = "_Ecirc_";
		ISOLATIN_ENTITIES[203] = "_Euml_";
		ISOLATIN_ENTITIES[204] = "_Igrave_";
		ISOLATIN_ENTITIES[205] = "_Iacute_";
		ISOLATIN_ENTITIES[206] = "_Icirc_";
		ISOLATIN_ENTITIES[207] = "_Iuml_";
		ISOLATIN_ENTITIES[208] = "_ETH_";
		ISOLATIN_ENTITIES[209] = "_Ntilde_";
		ISOLATIN_ENTITIES[210] = "_Ograve_";
		ISOLATIN_ENTITIES[211] = "_Oacute_";
		ISOLATIN_ENTITIES[212] = "_Ocirc_";
		ISOLATIN_ENTITIES[213] = "_Otilde_";
		ISOLATIN_ENTITIES[214] = "_Ouml_";
		ISOLATIN_ENTITIES[215] = "_times_";
		ISOLATIN_ENTITIES[216] = "_Oslash_";
		ISOLATIN_ENTITIES[217] = "_Ugrave_";
		ISOLATIN_ENTITIES[218] = "_Uacute_";
		ISOLATIN_ENTITIES[219] = "_Ucirc_";
		ISOLATIN_ENTITIES[220] = "_Uuml_";
		ISOLATIN_ENTITIES[221] = "_Yacute_";
		ISOLATIN_ENTITIES[222] = "_THORN_";
		ISOLATIN_ENTITIES[223] = "_szlig_";
		ISOLATIN_ENTITIES[224] = "_agrave_";
		ISOLATIN_ENTITIES[225] = "_aacute_";
		ISOLATIN_ENTITIES[226] = "_acirc_";
		ISOLATIN_ENTITIES[227] = "_atilde_";
		ISOLATIN_ENTITIES[228] = "_auml_";
		ISOLATIN_ENTITIES[229] = "_aring_";
		ISOLATIN_ENTITIES[230] = "_aelig_";
		ISOLATIN_ENTITIES[231] = "_ccedil_";
		ISOLATIN_ENTITIES[232] = "_egrave_";
		ISOLATIN_ENTITIES[233] = "_eacute_";
		ISOLATIN_ENTITIES[234] = "_ecirc_";
		ISOLATIN_ENTITIES[235] = "_euml_";
		ISOLATIN_ENTITIES[236] = "_igrave_";
		ISOLATIN_ENTITIES[237] = "_iacute_";
		ISOLATIN_ENTITIES[238] = "_icirc_";
		ISOLATIN_ENTITIES[239] = "_iuml_";
		ISOLATIN_ENTITIES[240] = "_eth_";
		ISOLATIN_ENTITIES[241] = "_ntilde_";
		ISOLATIN_ENTITIES[242] = "_ograve_";
		ISOLATIN_ENTITIES[243] = "_oacute_";
		ISOLATIN_ENTITIES[244] = "_ocirc_";
		ISOLATIN_ENTITIES[245] = "_otilde_";
		ISOLATIN_ENTITIES[246] = "_ouml_";
		ISOLATIN_ENTITIES[247] = "_divide_";
		ISOLATIN_ENTITIES[248] = "_oslash_";
		ISOLATIN_ENTITIES[249] = "_ugrave_";
		ISOLATIN_ENTITIES[250] = "_uacute_";
		ISOLATIN_ENTITIES[251] = "_ucirc_";
		ISOLATIN_ENTITIES[252] = "_uuml_";
		ISOLATIN_ENTITIES[253] = "_yacute_";
		ISOLATIN_ENTITIES[254] = "_thorn_";
		ISOLATIN_ENTITIES[255] = "_yuml_";
	}

	/**
	 * replaces entities in string by ISOLatin mnemonics e.g. replaces &#177; by
	 * _plusmn_ sometimes find strings of form &#194;&#177; which actually mean
	 * &#177; the leading string ent should be of form &#194;&#, etc. &# will do
	 * simple entities
	 * 
	 * @param s
	 *            to be edited
	 * @param ent
	 *            leading string before numeric
	 * @param lo
	 *            lowest index allowed
	 * @param hi
	 *            highest index allowed
	 * @param chars
	 *            list of characters
	 * @return edited string
	 * @throws RuntimeException
	 */
	private static String replaceNumericEntityByMnemonic(String s, String ent,
			int lo, int hi, String[] chars) throws RuntimeException {
		if (ent == null || !ent.endsWith(S_AMP + EC.S_HASH)) {
			throw new RuntimeException("bad entity: " + ent);
		}
		int idx = s.indexOf(ent);
		if (idx != -1) {
			// String sub = "";
			while (true) {
				idx = s.indexOf(ent);
				if (idx == -1) {
					break;
				}
				String ss = s.substring(idx + ent.length());
				int ii = ss.indexOf(S_SEMICOLON);
				if (ii == -1) {
					throw new RuntimeException("Bad entity after (" + ent
							+ "): " + s);
				}
				String alpha = "_unk_";
				String sss = ss.substring(0, ii);
				try {
					int ia = Integer.parseInt(sss);
					// ascii
					if (ia >= 32 && ia <= 127) {
						alpha = EC.S_EMPTY + (char) ia;
					} else if (ia < lo || ia > hi) {
						alpha = EC.S_UNDER + "ent" + ia + EC.S_UNDER;
					} else {
						alpha = EC.S_UNDER + chars[ia] + EC.S_UNDER;
					}
				} catch (NumberFormatException e) {
					throw new RuntimeException("Bad numeric entity: " + sss);
				}
				s = s.replace(ent + sss + EC.S_SEMICOLON, alpha);
			}
		}
		return s;
	}

	/**
	 * @param s
	 *            string to be edited
	 * @param ent
	 *            leading entity
	 * @return edited string
	 */
	public static String replaceNumericEntityByISOLatinString(String s,
			String ent) {
		return replaceNumericEntityByMnemonic(s, ent, 160, 255,
				ISOLATIN_ENTITIES);
	}

	/** greek entities */
	public final static String[] GREEK_ENTITIES = new String[200];
	static {
		GREEK_ENTITIES[145] = "Alpha";
		GREEK_ENTITIES[146] = "Beta";
		GREEK_ENTITIES[147] = "Gamma";
		GREEK_ENTITIES[148] = "Delta";
		GREEK_ENTITIES[149] = "Epsilon";
		GREEK_ENTITIES[150] = "Zeta";
		GREEK_ENTITIES[151] = "Eta";
		GREEK_ENTITIES[152] = "Theta";
		GREEK_ENTITIES[153] = "Iota";
		GREEK_ENTITIES[154] = "Kappa";
		GREEK_ENTITIES[155] = "Lambda";
		GREEK_ENTITIES[156] = "Mu";
		GREEK_ENTITIES[157] = "Nu";
		GREEK_ENTITIES[158] = "Omicron";
		GREEK_ENTITIES[159] = "Pi";
		GREEK_ENTITIES[160] = "Rho";
		GREEK_ENTITIES[161] = "Sigma";
		GREEK_ENTITIES[162] = "Tau";
		GREEK_ENTITIES[163] = "Upsilon";
		GREEK_ENTITIES[164] = "Phi";
		GREEK_ENTITIES[165] = "Phi";
		GREEK_ENTITIES[166] = "Psi";
		GREEK_ENTITIES[167] = "Omega";

		GREEK_ENTITIES[177] = "alpha";
		GREEK_ENTITIES[178] = "beta";
		GREEK_ENTITIES[179] = "gamma";
		GREEK_ENTITIES[180] = "delta";
		GREEK_ENTITIES[181] = "epsilon";
		GREEK_ENTITIES[182] = "zeta";
		GREEK_ENTITIES[183] = "eta";
		GREEK_ENTITIES[184] = "theta";
		GREEK_ENTITIES[185] = "iota";
		GREEK_ENTITIES[186] = "kappa";
		GREEK_ENTITIES[187] = "lambda";
		GREEK_ENTITIES[188] = "mu";
		GREEK_ENTITIES[189] = "nu";
		GREEK_ENTITIES[190] = "omicron";
		GREEK_ENTITIES[191] = "pi";
		GREEK_ENTITIES[192] = "rho";
		GREEK_ENTITIES[193] = "sigma";
		GREEK_ENTITIES[194] = "tau";
		GREEK_ENTITIES[195] = "upsilon";
		GREEK_ENTITIES[196] = "phi";
		GREEK_ENTITIES[197] = "chi";
		GREEK_ENTITIES[198] = "psi";
		GREEK_ENTITIES[199] = "omega";
	};

	/** UPPER_GREEK entities */
	public final static String[] UPPER_GREEK_ENTITIES = new String[968];
	public final static Map<String, Character> GREEK2CHARACTER_MAP;
	static {
		UPPER_GREEK_ENTITIES[912] = "Alpha";
		UPPER_GREEK_ENTITIES[914] = "Beta";
		UPPER_GREEK_ENTITIES[915] = "Gamma";
		UPPER_GREEK_ENTITIES[916] = "Delta";
		UPPER_GREEK_ENTITIES[917] = "Epsilon";
		UPPER_GREEK_ENTITIES[918] = "Zeta";
		UPPER_GREEK_ENTITIES[919] = "Eta";
		UPPER_GREEK_ENTITIES[920] = "Theta";
		UPPER_GREEK_ENTITIES[921] = "Iota";
		UPPER_GREEK_ENTITIES[922] = "Kappa";
		UPPER_GREEK_ENTITIES[923] = "Lambda";
		UPPER_GREEK_ENTITIES[924] = "Mu";
		UPPER_GREEK_ENTITIES[925] = "Nu";
		UPPER_GREEK_ENTITIES[926] = "Omicron";
		UPPER_GREEK_ENTITIES[927] = "Pi";
		UPPER_GREEK_ENTITIES[928] = "Rho";
		UPPER_GREEK_ENTITIES[929] = "Sigma";
		UPPER_GREEK_ENTITIES[930] = "Tau";
		UPPER_GREEK_ENTITIES[931] = "Upsilon";
		UPPER_GREEK_ENTITIES[932] = "Phi";
		UPPER_GREEK_ENTITIES[933] = "Phi";
		UPPER_GREEK_ENTITIES[934] = "Psi";
		UPPER_GREEK_ENTITIES[935] = "Omega";

		UPPER_GREEK_ENTITIES[945] = "alpha";
		UPPER_GREEK_ENTITIES[946] = "beta";
		UPPER_GREEK_ENTITIES[947] = "gamma";
		UPPER_GREEK_ENTITIES[948] = "delta";
		UPPER_GREEK_ENTITIES[949] = "epsilon";
		UPPER_GREEK_ENTITIES[950] = "zeta";
		UPPER_GREEK_ENTITIES[951] = "eta";
		UPPER_GREEK_ENTITIES[952] = "theta";
		UPPER_GREEK_ENTITIES[953] = "iota";
		UPPER_GREEK_ENTITIES[954] = "kappa";
		UPPER_GREEK_ENTITIES[955] = "lambda";
		UPPER_GREEK_ENTITIES[956] = "mu";
		UPPER_GREEK_ENTITIES[957] = "nu";
		UPPER_GREEK_ENTITIES[958] = "omicron";
		UPPER_GREEK_ENTITIES[959] = "pi";
		UPPER_GREEK_ENTITIES[960] = "rho";
		UPPER_GREEK_ENTITIES[961] = "sigma";
		UPPER_GREEK_ENTITIES[962] = "tau";
		UPPER_GREEK_ENTITIES[963] = "upsilon";
		UPPER_GREEK_ENTITIES[964] = "phi";
		UPPER_GREEK_ENTITIES[965] = "chi";
		UPPER_GREEK_ENTITIES[966] = "psi";
		UPPER_GREEK_ENTITIES[967] = "omega";
		
		GREEK2CHARACTER_MAP = new HashMap<String, Character>();
		GREEK2CHARACTER_MAP.put("Alpha", (char)912);
		GREEK2CHARACTER_MAP.put("Beta", (char)914);
		GREEK2CHARACTER_MAP.put("Gamma", (char)915);
		GREEK2CHARACTER_MAP.put("Delta", (char)916);
		GREEK2CHARACTER_MAP.put("Epsilon", (char)917);
		GREEK2CHARACTER_MAP.put("Zeta", (char)918);
		GREEK2CHARACTER_MAP.put("Eta", (char)919);
		GREEK2CHARACTER_MAP.put("Theta", (char)920);
		GREEK2CHARACTER_MAP.put("Iota", (char)921);
		GREEK2CHARACTER_MAP.put("Kappa", (char)922);
		GREEK2CHARACTER_MAP.put("Lambda", (char)923);
		GREEK2CHARACTER_MAP.put("Mu", (char)924);
		GREEK2CHARACTER_MAP.put("Nu", (char)925);
		GREEK2CHARACTER_MAP.put("Omicron", (char)926);
		GREEK2CHARACTER_MAP.put("Pi", (char)927);
		GREEK2CHARACTER_MAP.put("Rho", (char)928);
		GREEK2CHARACTER_MAP.put("Sigma", (char)929);
		GREEK2CHARACTER_MAP.put("Tau", (char)930);
		GREEK2CHARACTER_MAP.put("Upsilon", (char)931);
		GREEK2CHARACTER_MAP.put("Phi", (char)932);
		GREEK2CHARACTER_MAP.put("Phi", (char)933);
		GREEK2CHARACTER_MAP.put("Psi", (char)934);
		GREEK2CHARACTER_MAP.put("Omega", (char)935);

		GREEK2CHARACTER_MAP.put("alpha", (char)945);
		GREEK2CHARACTER_MAP.put("beta", (char)946);
		GREEK2CHARACTER_MAP.put("gamma", (char)947);
		GREEK2CHARACTER_MAP.put("delta", (char)948);
		GREEK2CHARACTER_MAP.put("epsilon", (char)949);
		GREEK2CHARACTER_MAP.put("zeta", (char)950);
		GREEK2CHARACTER_MAP.put("eta", (char)951);
		GREEK2CHARACTER_MAP.put("theta", (char)952);
		GREEK2CHARACTER_MAP.put("iota", (char)953);
		GREEK2CHARACTER_MAP.put("kappa", (char)954);
		GREEK2CHARACTER_MAP.put("lambda", (char)955);
		GREEK2CHARACTER_MAP.put("mu", (char)956);
		GREEK2CHARACTER_MAP.put("nu", (char)957);
		GREEK2CHARACTER_MAP.put("omicron", (char)958);
		GREEK2CHARACTER_MAP.put("pi", (char)959);
		GREEK2CHARACTER_MAP.put("rho", (char)960);
		GREEK2CHARACTER_MAP.put("sigma", (char)961);
		GREEK2CHARACTER_MAP.put("tau", (char)962);
		GREEK2CHARACTER_MAP.put("upsilon", (char)963);
		GREEK2CHARACTER_MAP.put("phi", (char)964);
		GREEK2CHARACTER_MAP.put("chi", (char)965);
		GREEK2CHARACTER_MAP.put("psi", (char)966);
		GREEK2CHARACTER_MAP.put("omega", (char)967);

	};

	/**
	 * remove all control (non-printing) characters
	 * 
	 * @param s
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 */
	public static String replaceISOControlsByMnemonic(String s) {
		if (s == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		int l = s.length();
		for (int i = 0; i < l; i++) {
			char ch = s.charAt(i);
			if (Character.isISOControl(ch)) {
				sb.append(translateToMnemonic(ch));
			} else {
				sb.append(ch);
			}
		}
		return sb.toString();
	}

	/**
	 * translate non-printing character to ISO mnemonic. e.g. 1 => SOH
	 * 
	 * @param ch
	 *            [0 - 31] or 127
	 * @return translation or empty string if out of range
	 */
	public static String translateToMnemonic(char ch) {
		switch (ch) {

		case 0:
			return "NUL";
		case 1:
			return "SOH";
		case 2:
			return "STX";
		case 3:
			return "ETX";
		case 4:
			return "EOT";
		case 5:
			return "ENQ";
		case 6:
			return "ACK";
		case 7:
			return "BEL";
		case 8:
			return "BS";
		case 9:
			return "HT";
		case 10:
			return "LF";
		case 11:
			return "VT";
		case 12:
			return "FF";
		case 13:
			return "CR";
		case 14:
			return "SO";
		case 15:
			return "SI";
		case 16:
			return "DLE";
		case 17:
			return "DC1";
		case 18:
			return "DC2";
		case 19:
			return "DC3";
		case 20:
			return "DC4";
		case 21:
			return "NAK";
		case 22:
			return "SYN";
		case 23:
			return "ETB";
		case 24:
			return "CAN";
		case 25:
			return "EM";
		case 26:
			return "SUB";
		case 27:
			return "ESC";
		case 28:
			return "FS";
		case 29:
			return "GS";
		case 30:
			return "RS";
		case 31:
			return "US";
		case 127:
			return "DEL";
		default:
			return "";

		}

	}

	/**
	 * convert 2 UTF8 characters to single IsoLatin1 character. quick and dirty
	 * UTF8 C2 80 => 80 (etc) UTF8 C3 80 => C0 (i.e. add x40) user has
	 * responsibility for selecting characters
	 * 
	 * @param a
	 *            leading character (ignored if not C2 and not C3)
	 * @param b
	 *            range 80-bf
	 * @return Isolatin equiv or 0 if a,b ignored or bad range
	 */
	public static char convertUTF8ToLatin1(char a, char b) {
		char c = 0;
		if (b >= 128 && b < 192) {
			if (a == (char) 194) {
				c = b;
			} else if (a == (char) 195) {
				c = (char) ((int) b + 64);
			}
		}
		return c;
	}

	/**
	 * convert single IsoLatin1 character to 2 UTF8 characters . quick and dirty
	 * user has responsibility for selecting characters a >= x80 && a <= xBF ==>
	 * xC2 a a >= xC0 && a <= xFF ==> xC3 a - x40
	 * 
	 * @param a
	 *            char to be converted (a >= x80 && a <xff)
	 * @return 2 characters or null
	 */
	public static char[] convertLatin1ToUTF8(char a) {
		char[] c = null;
		if (a >= 128 && a < 192) {
			c = new char[2];
			c[0] = (char) 194;
			c[1] = a;
		} else if (a >= 192 && a < 256) {
			c = new char[2];
			c[0] = (char) 195;
			c[1] = (char) (a - 64);
		}
		return c;
	}

	/**
	 * @param s
	 *            string to be edited
	 * @param ent
	 *            leading entity
	 * @return edited string
	 */
	public static String replaceNumericEntityByGreekMnemonics(String s,
			String ent) {
		return replaceNumericEntityByMnemonic(s, ent, 145, 199, GREEK_ENTITIES);
	}

	/**
	 * substitute certain DOS-compatible diacriticals by the Unicode value. Not
	 * guaranteed to be correct. Example 130 is e-acute (==
	 * 
	 * @param s
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 */
	public static String substituteDOSbyAscii(String s) {
		// check for untranslated chars
		for (int i = 0; i < s.length(); i++) {
			int jj = (int) s.charAt(i);
			if (jj > 180) {
				boolean ok = false;
				for (int j = 0; j < dosEquivalents.length; j++) {
					if (dosEquivalents[j].equals(S_EMPTY + s.charAt(i))) {
						ok = true;
						break;
					}
				}
				if (!ok) {
					LOG.error("==Unknown DOS character==" + jj + "//" + s);
				}
			}
		}
		String s1 = substituteStrings(s, dosEquivalents, asciiEquivalents);
		return s1;
	}

	public static String substituteNonASCIIChars(String s, char replacement) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			int c = s.charAt(i);
			c = (c > 256) ? (int) replacement : c;
			sb.append((char)c);
		}
		return sb.toString();
	}
	
	/*
	&#8204;     &zwnj;      Zero Width Non Joiner
	&#8205;     &zwj;       Zero Width Joiner
	&#8206;     &lrm;       Left-Right Mark
	&#8207;     &rlm;       Right-Left Mark
	&#8211;	 	&ndash;	 	en dash
	&#8212;	 	&mdash;		em dash
	&#8216;		&lsquo;	 	left single quotation mark
	&#8217;	 	&rsquo;	 	right single quotation mark
	&#8220;	 	&ldquo;	 	left double quotation mark
	&#8221;	 	&rdquo;	 	right double quotation mark

		 */
	
	public static String substituteSmartCharacters(String s) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			int c = s.charAt(i);
			if (c == 8204 || c == 160) {
				c = ' ';
			} else if (c == 8205) {
				c = -1;
			} else if (c == 8211 || c == 8212) {
				c = '-';
			} else if (c == 8216 || c == 8217) {
				c = '\'';
			} else if (c == 8220 || c == 8221) {
				c = '"';
			} else if (c > 127) {
				c = '?';
			}
			if (c > 0) {
				sb.append((char)c);
			}
		}
		return sb.toString();
		 
	}
	
	/**
	 * replace tabs with spaces while trying to preserve the formatting
	 * @param s
	 * @param width
	 * @return
	 */
	public static String replaceTabs(String s, int width) {
		StringBuilder sb = new StringBuilder();
		int in = 0;
		int out = 0;
		for (; in < s.length(); in++) {
			char c = s.charAt(in);
			if (c == EuclidConstants.C_TAB) {
				int mod = width - (out % width);
				for (int i = 0; i < mod; i++) {
					sb.append(EuclidConstants.C_SPACE);
					out++;
				}
			} else {
				sb.append(c);
				out++;
			}
		}
		return sb.toString();
	}

	/**
	 * substitute hex representation of character, for example =2E by char(46).
	 * If line ends with =, ignore that character.
	 * 
	 * @param s
	 *            Description of the Parameter
	 * @return String result
	 */
	public static String substituteEquals(String s) {
		if (s == null) {
			return null;
		}
		int len = s.length();
		StringBuffer sb = new StringBuffer(S_EMPTY);
		while (true) {
			int idx = s.indexOf(S_EQUALS);
			if (idx == -1) {
				sb.append(s);
				return sb.toString();
			}
			// remove EQUALS
			sb.append(s.substring(0, idx));
			s = s.substring(idx + 1);
			len -= idx + 1;
			// not enough chars
			if (len <= 1) {
				sb.append(S_EQUALS);
				sb.append(s);
				return sb.toString();
			}
			int hex = getIntFromHex(s.substring(0, 2));
			// wasn't a hexchar
			if (hex < 0) {
				sb.append(S_EQUALS);
			} else {
				sb.append((char) hex);
				s = s.substring(2);
				len -= 2;
			}
		}
	}

	/**
	 * 
	 * Translates a Hex number to its int equivalent. Thus "FE" translates to
	 * 
	 * 254. Horrid, but I couldn't find if Java reads hex. All results are >=
	 * 
	 * 0. Errors return -1
	 * 
	 * 
	 * 
	 * @param hex
	 *            Description of the Parameter
	 * 
	 * @return The intFromHex value
	 * 
	 */
	public static int getIntFromHex(String hex) {
		hex = hex.toUpperCase();
		if (hex.startsWith("0X")) {
			hex = hex.substring(2);
		} else if (hex.charAt(0) == 'X') {
			hex = hex.substring(1);
		}
		int result = 0;
		for (int i = 0; i < hex.length(); i++) {
			char c = hex.charAt(i);
			if (Character.isDigit(c)) {
				c -= '0';
			} else if (c < 'A' || c > 'F') {
				return -1;
			} else {
				c -= 'A';
				c += (char) 10;
			}
			result = 16 * result + c;
		}
		return result;
	}

	/**
	 * capitalise a String (whatever the starting case)
	 * 
	 * @param s
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 */
	public static String capitalise(String s) {
		if (s.equals(S_EMPTY)) {
			return EC.S_EMPTY;
		}
		if (s.length() == 1) {
			return s.toUpperCase();
		} else {
			return s.substring(0, 1).toUpperCase()
					+ s.substring(1).toLowerCase();
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param s
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 */
	public static String toCamelCase(String s) {
		StringTokenizer st = new StringTokenizer(s, " \n\r\t");
		String out = EC.S_EMPTY;
		while (st.hasMoreTokens()) {
			s = st.nextToken();
			if (out != EC.S_EMPTY) {
				s = capitalise(s);
			}
			out += s;
		}
		return out;
	}

	/**
	 * reads a byte array from file, *including* line feeds
	 * 
	 * @param filename
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 * @exception FileNotFoundException
	 *                Description of the Exception
	 * @exception IOException
	 *                Description of the Exception
	 */
	public static byte[] readByteArray(String filename)
			throws FileNotFoundException, IOException {
		DataInputStream dis = new DataInputStream(new FileInputStream(filename));
		return Util.readByteArray(dis);
	}

	/**
	 * reads a byte array from DataInputStream, *including* line feeds
	 * 
	 * @param d
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 * @exception IOException
	 *                Description of the Exception
	 */
	public static byte[] readByteArray(DataInputStream d) throws IOException {
		int len = 100;
		int count = 0;
		byte[] src = new byte[len];
		byte b;
		while (true) {
			try {
				b = d.readByte();
			} catch (EOFException e) {
				break;
			}
			src[count] = b;
			if (++count >= len) {
				len *= 2;
				byte[] temp = new byte[len];
				System.arraycopy(src, 0, temp, 0, count);
				src = temp;
			}
		}
		len = count;
		byte[] temp = new byte[len];
		System.arraycopy(src, 0, temp, 0, count);
		return temp;
	}

	/**
	 * remove all control (non-printing) characters
	 * 
	 * @param s
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 */
	public static String stripISOControls(String s) {
		if (s == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		int l = s.length();
		for (int i = 0; i < l; i++) {
			char ch = s.charAt(i);
			if (!Character.isISOControl(ch)) {
				sb.append(ch);
			}
		}
		return sb.toString();
	}

	/**
	 * normalise whitespace in a String (all whitespace is transformed to single
	 * spaces and the string is NOT trimmed
	 * 
	 * @param s
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 */
	public static String normaliseWhitespace(String s) {
		if (s == null || s.equals(S_EMPTY)) {
			return s;
		}
		StringTokenizer st = new StringTokenizer(s, Util.WHITESPACE);
		int l = s.length();
		String ss = EC.S_EMPTY;
		if (Character.isWhitespace(s.charAt(0))) {
			ss = EC.S_SPACE;
		}
		String end = EC.S_EMPTY;
		if (Character.isWhitespace(s.charAt(l - 1))) {
			end = EC.S_SPACE;
		}
		boolean start = true;
		while (st.hasMoreTokens()) {
			if (start) {
				ss += st.nextToken();
				start = false;
			} else {
				ss += EC.S_SPACE + st.nextToken();
			}
		}
		return ss + end;
	}

	/**
	 * strip linefeeds from a byte array
	 * 
	 * @param b
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 */
	public static byte[] stripNewlines(byte[] b) {
		int l = b.length;
		byte[] bb = new byte[l];
		int j = 0;
		for (int i = 0; i < l; i++) {
			if (b[i] != '\n') {
				bb[j++] = b[i];
			}
		}
		byte[] bbb = new byte[j];
		System.arraycopy(bb, 0, bbb, 0, j);
		return bbb;
	}

	/**
	 * get an OutputStream from a file or URL. Required (I think) because
	 * strings of the sort "file:/C:\foo\bat.txt" crash FileOutputStream, so
	 * this strips off the file:/ stuff for Windows-like stuff
	 * 
	 * @param fileName
	 *            Description of the Parameter
	 * @return FileOutputStream a new (opened) FileOutputStream
	 * @exception java.io.FileNotFoundException
	 *                Description of the Exception
	 */
	public static FileOutputStream getFileOutputStream(String fileName)
			throws java.io.FileNotFoundException {
		if (fileName == null) {
			return null;
		}
		// W-like syntax
		if (fileName.startsWith("file:")
				&& fileName.substring(5).indexOf(S_COLON) != -1) {
			fileName = fileName.substring(5);
			if (fileName.startsWith(S_SLASH)
					|| fileName.startsWith(S_BACKSLASH)) {
				fileName = fileName.substring(1);
			}
		}
		return new FileOutputStream(fileName);
	}

	// cache the formats
	static Hashtable<String, DecimalFormat> formTable = new Hashtable<String, DecimalFormat>();

	/**
	 * format for example f8.3 this is a mess; if cannot fit, then either
	 * right-truncates or when that doesn't work, returns ****
	 * 
	 * @param nPlaces
	 *            Description of the Parameter
	 * @param nDec
	 *            Description of the Parameter
	 * @param value
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 * @exception EuclidRuntimeException
	 *                Description of the Exception
	 */
	public static String outputFloat(int nPlaces, int nDec, double value)
			throws EuclidRuntimeException {
		String f = "f" + nPlaces + EC.S_PERIOD + nDec;
		DecimalFormat form = formTable.get(f);
		if (form == null) {
			String pattern = EC.S_EMPTY;
			for (int i = 0; i < nPlaces - nDec - 2; i++) {
				pattern += "#";
			}
			pattern += "0.";
			for (int i = nPlaces - nDec; i < nPlaces; i++) {
				pattern += "0";
			}
			form = (DecimalFormat) NumberFormat.getInstance();
			DecimalFormatSymbols symbols = form.getDecimalFormatSymbols();
			symbols.setDecimalSeparator('.');
			form.setDecimalFormatSymbols(symbols);
			form.setMaximumIntegerDigits(nPlaces - nDec - 1);
			// form.applyLocalizedPattern(pattern);
			form.applyPattern(pattern);
			formTable.put(f, form);
		}
		String result = form.format(value).trim();
		boolean negative = false;
		if (result.charAt(0) == '-') {
			result = result.substring(1);
			negative = true;
		}
		if (negative) {
			result = EC.S_MINUS + result;
		}
		StringBuffer sb = new StringBuffer();
		int l = result.length();
		for (int i = 0; i < nPlaces - l; i++) {
			sb.append(S_SPACE);
		}
		String s = sb.append(result).toString();
		if (l > nPlaces) {
			s = s.substring(0, nPlaces);
			// decimal point got truncated?
			if (s.indexOf(S_PERIOD) == -1) {
				s = EC.S_EMPTY;
				for (int i = 0; i < nPlaces; i++) {
					s += EC.S_STAR;
				}
			}
		}
		return s;
	}

	/**
	 * as above, but trims trailing zeros
	 * 
	 * @param nPlaces
	 *            Description of the Parameter
	 * @param nDec
	 *            Description of the Parameter
	 * @param c
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 */
	public static String outputNumber(int nPlaces, int nDec, double c) {
		String s = Util.outputFloat(nPlaces, nDec, c).trim();
		if (s.indexOf(S_PERIOD) != -1) {
			while (s.endsWith("0")) {
				s = s.substring(0, s.length() - 1);
			}
			if (s.endsWith(S_PERIOD)) {
				s = s.substring(0, s.length() - 1);
			}
		}
		return s;
	}

	/**
	 * invert a Hashtable by interchanging keys and values. This assumes a 1;1
	 * mapping - if not the result is probably garbage.
	 * 
	 * @param table
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 */
	public static Hashtable<Object, Object> invert(
			Hashtable<Object, Object> table) {
		if (table == null) {
			return null;
		}
		Hashtable<Object, Object> newTable = new Hashtable<Object, Object>();
		for (Enumeration<Object> e = table.keys(); e.hasMoreElements();) {
			Object key = e.nextElement();
			Object value = table.get(key);
			newTable.put(value, key);
		}
		return newTable;
	}

	/**
	 * checks array is not null and is of given size.
	 * 
	 * @param array
	 *            to check
	 * @param size
	 *            required size
	 * @throws EuclidRuntimeException
	 *             if null or wrong size
	 */
	public static void check(double[] array, int size)
			throws EuclidRuntimeException {
		if (array == null) {
			throw new EuclidRuntimeException("null array");
		} else if (array.length != size) {
			throw new EuclidRuntimeException("array size required (" + size
					+ ") found " + array.length);
		}
	}

	/**
	 * checks that an in is in the range low to high.
	 * 
	 * @param n
	 *            to check
	 * @param low
	 *            inclusive lower
	 * @param high
	 *            inclusive higher
	 * @throws EuclidRuntimeException
	 *             if out of range
	 */
	public static void check(int n, int low, int high)
			throws EuclidRuntimeException {
		if (n < low || n > high) {
			throw new EuclidRuntimeException("index (" + n + ")out of range: "
					+ low + EC.S_SLASH + high);
		}
	}

	/**
	 * compare two arrays of doubles.
	 * 
	 * @param a
	 *            first array
	 * @param b
	 *            second array
	 * @param eps
	 *            maximum allowed difference
	 * @return true if arrays non-null and if arrays are equal length and
	 *         corresonding elements agree within eps.
	 */
	public static boolean isEqual(double[] a, double[] b, double eps) {
		boolean equal = (a != null && b != null && a.length == b.length);
		if (equal) {
			for (int i = 0; i < a.length; i++) {
				if (Math.abs(a[i] - b[i]) >= eps) {
					equal = false;
					break;
				}
			}
		}
		return equal;
	}

	/**
	 * compare two arrays of ints.
	 * 
	 * @param a
	 *            first array
	 * @param b
	 *            second array
	 * @param eps
	 *            maximum allowed difference
	 * @return true if arrays non-null and if arrays are equal length and
	 *         corresonding elements agree within eps.
	 */
	public static boolean isEqual(int[] a, int[] b, int eps) {
		boolean equal = (a != null && b != null && a.length == b.length);
		if (equal) {
			for (int i = 0; i < a.length; i++) {
				if (Math.abs(a[i] - b[i]) >= eps) {
					equal = false;
					break;
				}
			}
		}
		return equal;
	}

	/**
	 * concatenates array of booleans.
	 * 
	 * @param bb
	 *            the values
	 * @param separator
	 * @return the String
	 */
	public final static String concatenate(boolean[] bb, String separator) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < bb.length; i++) {
			if (i > 0) {
				sb.append(separator);
			}
			sb.append(bb[i]);
		}
		return sb.toString();
	}

	/**
	 * concatenates array of doubles.
	 * 
	 * @param ss
	 *            the values
	 * @param separator
	 * @return the String
	 */
	public final static String concatenate(double[] ss, String separator) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < ss.length; i++) {
			if (i > 0) {
				sb.append(separator);
			}
			if (Double.isInfinite(ss[i])) {
				if (ss[i] < 0) {
					sb.append("-");
				}
				sb.append("INF");
			} else if (Double.isNaN(ss[i])) {
				sb.append("NaN");
			} else {
				sb.append(ss[i]);
			}
		}
		return sb.toString();
	}

	/**
	 * concatenates array of array of doubles.
	 * 
	 * @param ss
	 *            the values
	 * @param separator
	 * @return the String
	 */
	public final static String concatenate(double[][] ss, String separator) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < ss.length; i++) {
			if (i > 0) {
				sb.append(separator);
			}
			sb.append(Util.concatenate(ss[i], separator));
		}
		return sb.toString();
	}

	/**
	 * splits string into ints.
	 * 
	 * @param s
	 *            the string
	 * @param delim
	 * @return array
	 * @throws EuclidRuntimeException
	 *             cannot parse as ints
	 */
	public final static int[] splitToIntArray(String s, String delim)
			throws EuclidRuntimeException {
		String[] ss = s.split(delim);
		int[] ii = new int[ss.length];
		for (int i = 0; i < ss.length; i++) {
			try {
				ii[i] = Integer.parseInt(ss[i]);
			} catch (NumberFormatException nfe) {
				throw new EuclidRuntimeException(S_EMPTY + nfe);
			}
		}
		return ii;
	}

	/**
	 * splits string into doubles. assumes single space delimiters
	 * 
	 * @param s
	 *            the string
	 * @return array
	 * @throws EuclidRuntimeException
	 *             cannot parse as ints
	 */
	public final static double[] splitToDoubleArray(String s) {
		return splitToDoubleArray(s, EC.S_SPACE);
	}

	
	/**
	 * Parses double, taking account of lexical forms of special cases allowed
	 * by the XSD spec: INF, -INF and NaN.
	 * 
	 * @param value
	 * @return
	 * @throws ParseException
	 */
	
	public static double parseFlexibleDouble(String value)
			throws ParseException {
		if (value != null) {
			// 0, -0, INF, -INF and NaN : Special cases from the XSD spec.
			if ("INF".equals(value)) {
				return Double.POSITIVE_INFINITY;
			} else if ("-INF".equals(value)) {
				return Double.NEGATIVE_INFINITY;
			} else if ("NaN".equals(value)) {
				return Double.NaN;
			} else {
				try {
					return Double.valueOf(value);
				} catch (NumberFormatException e) {
					throw new ParseException(e.toString(), 0);
				}
			}
		} else {
			throw new IllegalArgumentException("Null double string not allowed");
		}
	}
	
	/**
	 * splits string into doubles.
	 * 
	 * @param s
	 *            the string
	 * @param delim
	 * @return array
	 * @throws EuclidRuntimeException
	 *             cannot parse as ints
	 */
	public final static double[] splitToDoubleArray(String s, String delim)
			throws EuclidRuntimeException {
		if (s == null) {
			throw new RuntimeException("null argument");
		}
		String[] ss = s.trim().split(delim);
		double[] dd = new double[ss.length];
		for (int i = 0; i < ss.length; i++) {
			try {
				dd[i] = parseFlexibleDouble(ss[i]);
			} catch (NumberFormatException nfe) {
				throw new EuclidRuntimeException(S_EMPTY + nfe.getMessage(),
						nfe);
			} catch (ParseException e) {
				throw new EuclidRuntimeException("Bad double in (" + s + ") : "
						+ ss[i] + "at position " + i, e);
			}
		}
		return dd;
	}

	/**
	 * concatenates array of ints.
	 * 
	 * @param ss
	 *            the values
	 * @param separator
	 * @return the String
	 */
	public final static String concatenate(int[] ss, String separator) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < ss.length; i++) {
			if (i > 0) {
				sb.append(separator);
			}
			sb.append(ss[i]);
		}
		return sb.toString();
	}

	/**
	 * concatenates array of Strings.
	 * 
	 * @param ss
	 *            the values
	 * @param separator
	 * @return the String
	 */
	public final static String concatenate(String[] ss, String separator) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < ss.length; i++) {
			if (i > 0) {
				sb.append(separator);
			}
			sb.append(ss[i]);
		}
		String s = sb.toString();
		if (separator.trim().equals(S_EMPTY)) {
			s = s.trim();
		}
		return s;
	}

	/**
	 * does an array of Strings contain a String.
	 * 
	 * @param strings
	 * @param s
	 *            string to search for
	 * @return true if any ss[i] == s
	 */
	public final static boolean containsString(String[] strings, String s) {
		boolean b = false;
		if (s != null && strings != null) {
			for (int i = 0; i < strings.length; i++) {
				if (s.equals(strings[i])) {
					b = true;
					break;
				}
			}
		}
		return b;
	}

	/**
	 * format trailing decimals
	 * 
	 * @param d
	 *            value to be formatted
	 * @param ndec
	 *            max number of decimals (3 = ****.ddd
	 * @return the formatted number
	 */
	public static double format(double d, int ndec) {
		int pow = 1;
		for (int i = 0; i < ndec; i++) {
			pow *= 10;
		}
		return (double) Math.round(d * (double) pow) / (double) pow;
	}

	/**
	 * trim trailing zeroes and trailing decimal point.
	 * 
	 * @param d
	 * @return trimmed string
	 */
	public static String trim(double d) {
		String s = EC.S_EMPTY + d;
		int idx = s.lastIndexOf(S_PERIOD);
		if (idx > 0) {
			int l = s.length() - 1;
			while (l > idx) {
				if (s.charAt(l) != '0') {
					break;
				}
				l--;
			}
			if (l == idx) {
				l--;
			}
			l++;
			s = s.substring(0, l);
		}
		return s;
	}

	/**
	 * translate array of Strings to a List.
	 * 
	 * @param ss
	 *            strings (can include null)
	 * @return the list
	 */
	public static List<String> createList(String[] ss) {
		List<String> list = new ArrayList<String>();
		for (String s : ss) {
			list.add(s);
		}
		return list;
	}

	private static List<Integer> primeList;

	/**
	 * get i'th prime. calculates it on demand if not already present and caches
	 * result.
	 * 
	 * @param i
	 * @return the primt (starts at 2)
	 */
	public static int getPrime(int i) {
		if (primeList == null) {
			primeList = new ArrayList<Integer>();
			primeList.add(new Integer(2));
			primeList.add(new Integer(3));
			primeList.add(new Integer(5));
			primeList.add(new Integer(7));
			primeList.add(new Integer(11));
		}
		int np = primeList.size();
		int p = primeList.get(np - 1).intValue();
		while (np <= i) {
			p = nextPrime(p);
			primeList.add(new Integer(p));
			np++;
		}
		return primeList.get(i).intValue();
	}

	private static int nextPrime(int pp) {
		int p = pp;
		for (;;) {
			p = p + 2;
			if (isPrime(p)) {
				break;
			}
		}
		return p;
	}

	private static boolean isPrime(int p) {
		boolean prime = true;
		int sp = (int) Math.sqrt(p) + 1;
		for (int i = 1; i < primeList.size(); i++) {
			int pp = primeList.get(i).intValue();
			if (p % pp == 0) {
				prime = false;
				break;
			}
			if (pp > sp) {
				break;
			}
		}
		return prime;
	}

	/**
	 * parse string as integer.
	 * 
	 * @param s
	 * @return true if can be parsed.
	 */
	public static boolean isInt(String s) {
		boolean couldBeInt = true;
		try {
			new Integer(s);
		} catch (NumberFormatException e) {
			couldBeInt = false;
		}
		return couldBeInt;
	}

	/**
	 * parse string as integerArray.
	 * 
	 * @param s
	 * @param delimiterRegex
	 * @return true if can be parsed.
	 */
	public static boolean isIntArray(String s, String delimiterRegex) {
		boolean couldBeIntArray = true;
		String[] ss = s.split(delimiterRegex);
		try {
			new IntArray(ss);
		} catch (NumberFormatException e) {
			couldBeIntArray = false;
		}
		return couldBeIntArray;
	}

	/**
	 * parse string as realArray.
	 * 
	 * @param s
	 * @param delimiterRegex
	 * @return true if can be parsed.
	 */
	public static boolean isFloatArray(String s, String delimiterRegex) {
		boolean couldBeFloatArray = true;
		String[] ss = s.split(delimiterRegex);
		try {
			new RealArray(ss);
		} catch (Exception e) {
			couldBeFloatArray = false;
		}
		return couldBeFloatArray;
	}

	/**
	 * parse string as float.
	 * 
	 * @param s
	 * @return true if can be parsed.
	 */
	public static boolean isFloat(String s) {
		boolean couldBeFloat = true;
		try {
			new Double(s);
		} catch (NumberFormatException e) {
			couldBeFloat = false;
		}
		return couldBeFloat;
	}

	/**
	 * date of form 21-jan-1965.
	 */
	public final static String DATE_REGEX1 = "([0-3][0-9])\\-(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)\\-(\\d\\d\\d\\d)";
	/**
	 * date of form 1965-01-25.
	 */
	public final static String DATE_REGEX2 = "\\d\\d\\d\\d\\-[0-1][0-9]\\-[0-3][0-9]";

	/**
	 * month data .
	 */
	public final static String[] months = { "jan", "feb", "mar", "apr", "may",
			"jun", "jul", "aug", "sep", "oct", "nov", "dec" };

	/**
	 * parse string as date. tries several formats (case insensitive) can be
	 * used to test whether string is parsable as date
	 * 
	 * @param s
	 * @return ISO 8601 format if can be parsed or null
	 */
	public static String getCanonicalDate(String s) {
		String dateS = null;
		Pattern pattern = Pattern
				.compile(DATE_REGEX1, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(s.toLowerCase());
		if (matcher.matches()) {
			int day = Integer.parseInt(matcher.group(1));
			String month = matcher.group(2).toLowerCase();
			boolean ignoreCase = true;
			int imonth = Util.indexOf(month, months, ignoreCase);
			int year = Integer.parseInt(matcher.group(3));
			dateS = "" + year + EC.S_MINUS + imonth + EC.S_MINUS + day;
		} else {
			pattern = Pattern.compile(DATE_REGEX2, Pattern.CASE_INSENSITIVE);
			matcher = pattern.matcher(s.toLowerCase());
			if (matcher.matches()) {
				dateS = s;
			}
		}
		return dateS;
	}

	public static double getDouble(String s) {
		double d = Double.NaN;
		try {
			d = new Double(s).doubleValue();
		} catch (NumberFormatException nfe) {
			throw new RuntimeException("Bad double: " + s);
		}
		return d;
	}

	/**
	 * rounds to decimal place.
	 * 
	 * @param dd
	 *            number to be rounded
	 * @param ndec
	 *            number of places
	 * @return float
	 */
	public static double trimFloat(double dd, int ndec) {
		int trim = 1;
		ndec = Math.min(ndec, 10); // to avoid problems
		for (int i = 0; i < ndec; i++) {
			trim *= 10;
		}
		return ((int) (trim * dd)) / (double) trim;
	}

	/**
	 * sorts a list of string on integers within them. normal lexical sort will
	 * often produce file1, file10, file11, file2, etc. this will order them by
	 * integers int their
	 * 
	 * @param list
	 */
	public static void sortByEmbeddedInteger(List<?> list) {
		StringIntegerComparator fic = new StringIntegerComparator();
		Collections.sort(list, fic);
	}
	
	/** outputs to sysout.
	 * primarily to allow trapping and tracing of sysout calls
	 * which we try to avoid anyway
	 * @param s
	 */
	public static void print(String s) {
		SYSOUT.print(s);
	}
	
	/** outputs to sysout.
	 * primarily to allow trapping and tracing of sysout calls
	 * which we try to avoid anyway
	 * @param s
	 */
	public static void println(String s) {
		SYSOUT.println(s);
	}
	
	/** outputs to sysout.
	 * primarily to allow trapping and tracing of sysout calls
	 * which we try to avoid anyway
	 * @param s
	 */
	public static void println() {
		SYSOUT.println();
	}

	public static List<String> getRESTQueryAsLines(String s, String u,
			String mediaType) throws IOException {
		byte[] content = Util.getRESTQuery(s, u, mediaType);
		BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(content)));
		List<String> lines = new ArrayList<String>();
		String line;
		while ((line = br.readLine()) != null) {
			lines.add(line);
		}
		return lines;
	}

	public static byte[] getRESTQuery(String serviceUrl, String urlString, String mediaType) throws IOException {
		URL url = new URL(urlString+serviceUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.addRequestProperty("accept", mediaType);
		conn.connect();
		InputStream is = conn.getInputStream();
		byte[] bytes = IOUtils.toByteArray(is);
		conn.disconnect();
		return bytes;
	}
	/**
	 * borrowed from somewhere. 
	 * @param s
	 * @return
	 */
	public static String calculateMD5(String s) {
		String md5String = null;
		if (s != null && s.trim().length() > 0) {
			StringBuffer hexString = new StringBuffer();
			try{
				MessageDigest algorithm = MessageDigest.getInstance("MD5");
				algorithm.reset();
				algorithm.update(s.getBytes());
				byte messageDigest[] = algorithm.digest();
				for (int i=0; i < messageDigest.length; i++) {
					hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
				}
			} catch(NoSuchAlgorithmException nsae) {
			            
			}
			md5String = hexString.toString();
		}
		return md5String;
	}
	
	   /**
     * avoids the checked exception
     * @param file
     * @return
     */
	public static String getCanonicalPath(File file) {
		String path = null;
		try {
			File absoluteFile = file.getAbsoluteFile();
			path = absoluteFile.getCanonicalPath();
		} catch (IOException e) {
			throw new RuntimeException("cannot canonicalize "+file+" ... "+e.getMessage(), e);
		}
		return path;
	}

	
	/** path to create file2 from file1
	 *@param file1 
	 *@param file2
	 *@param newSeparator if not null, use as new separator
	 */
	public static String getRelativeFilename(File file1, File file2, String newSeparator) {
		if (newSeparator == null) {
			newSeparator = File.separator;
		}
		String regex = (File.separator.equals("\\")) ? "\\\\" : File.separator;
		String path = null;
		try {
			String path1 = file1.getCanonicalPath();
			String path2 = file2.getCanonicalPath();
			String[] pathComponent1 = path1.split(regex);
			String[] pathComponent2 = path2.split(regex);
			//int minComponents = Math.min(pathComponent1.length, pathComponent2.length);
			int i = 0;
			for (; i < pathComponent1.length; i++) {
				if (!pathComponent2[i].equals(pathComponent1[i])) {
					break;
				}
			}
			path = "";
			for (int j = i; j < pathComponent1.length; j++) {
				path += ".."+newSeparator;
			}
			for (int j = i; j < pathComponent2.length-1; j++) {
				path += pathComponent2[j]+newSeparator;
			}
			path += pathComponent2[pathComponent2.length-1];
			
		} catch (Exception e) {
			throw new RuntimeException("bad names/BUG", e);
			// return null
		}
		return path;
	}

	/**
     * Get the relative path from one file to another, specifying the directory separator. 
     * If one of the provided resources does not exist, it is assumed to be a file unless it ends with '/' or
     * '\'.
     * 
     * from Stackoverflow:
     * http://stackoverflow.com/questions/204784/how-to-construct-a-relative-path-in-java-from-two-absolute-paths-or-urls
     * 
     * @param target targetPath is calculated to this file
     * @param base basePath is calculated from this file
     * @param separator directory separator. The platform default is not assumed so that we can test Unix behaviour when running on Windows (for example)
     * @return
     */
    public static String getRelativePath(String targetPath, String basePath, String pathSeparator) {

        // Normalize the paths
        String normalizedTargetPath = FilenameUtils.normalizeNoEndSeparator(targetPath);
        String normalizedBasePath = FilenameUtils.normalizeNoEndSeparator(basePath);

        // Undo the changes to the separators made by normalization
        if (pathSeparator.equals("/")) {
            normalizedTargetPath = FilenameUtils.separatorsToUnix(normalizedTargetPath);
            normalizedBasePath = FilenameUtils.separatorsToUnix(normalizedBasePath);

        } else if (pathSeparator.equals("\\")) {
            normalizedTargetPath = FilenameUtils.separatorsToWindows(normalizedTargetPath);
            normalizedBasePath = FilenameUtils.separatorsToWindows(normalizedBasePath);

        } else {
            throw new IllegalArgumentException("Unrecognised dir separator '" + pathSeparator + "'");
        }

        String[] base = normalizedBasePath.split(Pattern.quote(pathSeparator));
        String[] target = normalizedTargetPath.split(Pattern.quote(pathSeparator));

        // First get all the common elements. Store them as a string,
        // and also count how many of them there are.
        StringBuffer common = new StringBuffer();

        int commonIndex = 0;
        while (commonIndex < target.length && commonIndex < base.length
                && target[commonIndex].equals(base[commonIndex])) {
            common.append(target[commonIndex] + pathSeparator);
            commonIndex++;
        }

        if (commonIndex == 0) {
            // No single common path element. This most
            // likely indicates differing drive letters, like C: and D:.
            // These paths cannot be relativized.
            throw new RuntimeException("No common path element found for '" + normalizedTargetPath + "' and '" + normalizedBasePath
                    + "'");
        }   

        // The number of directories we have to backtrack depends on whether the base is a file or a dir
        // For example, the relative path from
        //
        // /foo/bar/baz/gg/ff to /foo/bar/baz
        // 
        // ".." if ff is a file
        // "../.." if ff is a directory
        //
        // The following is a heuristic to figure out if the base refers to a file or dir. It's not perfect, because
        // the resource referred to by this path may not actually exist, but it's the best I can do
        boolean baseIsFile = true;

        File baseResource = new File(normalizedBasePath);

        if (baseResource.exists()) {
            baseIsFile = baseResource.isFile();

        } else if (basePath.endsWith(pathSeparator)) {
            baseIsFile = false;
        }

        StringBuffer relative = new StringBuffer();

        if (base.length != commonIndex) {
            int numDirsUp = baseIsFile ? base.length - commonIndex - 1 : base.length - commonIndex;

            for (int i = 0; i < numDirsUp; i++) {
                relative.append(".." + pathSeparator);
            }
        }
        relative.append(normalizedTargetPath.substring(common.length()));
        return relative.toString();
    }

	/** checks that this is local to PMR.
	 * 
	 * Stops certain tests being run outside PMR implementation,
	 * especially ones which use directories outside the maven project
	 * 
	 * uses System.user.name
	 * 
	 * @return
	 */
	public static boolean checkPMR() {
		boolean check = false;
		if (PM286.equals(System.getProperty("user.name"))) {
			check = true;
		} else {
			LOG.info("Skipping PMR-only test");
		}
		return check;
	}


	/** converts character to Unicode representation.
	 * 
	 * @param c
	 * @return string of form "\\udddd"
	 */
	public static String createUnicodeString(char c) {
		String hex = Integer.toHexString((int) c);
		return "\\"+"u"+Util.addLeadingZeros(hex, 4);
	}

	/** pads string with leading zeros.
	 * 
	 * @param hex string to pad
	 * @param length total number of characters in result
	 * @return string left-padded to length
	 */
	public static String addLeadingZeros(String hex, int length) {
		StringBuilder sb = new StringBuilder(hex);
		while (sb.length() < length) {
			sb.insert(0,  '0');
		}
		return sb.toString();
	}

	public static String escapeCSVField(String string) {
		// escape " with ""
		if (string.contains("\"")) {
			string = string.replace("\"", "\"\"");
		}
		if (string.contains("'")) {
			string = "\""+string+"\"";
		}
		return string;
	}

	public static List<String> toStringList(Object[] objects) {
		List<String> list = new ArrayList<String>();
		if (objects != null) {
			for (Object object : objects) {
				list.add(object.toString());
			}
		}
		return list;
	}

	/** pretty print a Json Object
	 * 
	 * @param json as JsonObject
	 * @return object as String
	 */
	public static String prettyPrintJson(JsonObject json) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
	    String prettyJson = gson.toJson(json);
	    return prettyJson;
	}

	public static List<String> createSplitStrings(String characterString, String text) {
		List<String> splitTexts = new ArrayList<>();
		int last = 0;
		for (int j = 0; j < text.length(); j++) {
			String s = text.substring(j,  j+1); // probably inefficent
			if (characterString.contains(s)) {
				if (j > 0) {
//					LOG.debug(last+":"+j);
					String prev = text.substring(last, j);
					splitTexts.add(prev);
				}
				String character = text.substring(j, j+1);
				splitTexts.add(character);
//				LOG.debug(j+":"+character);
				last = j + 1;
			}
		}
		if (last > 0 && last < text.length()) {
			splitTexts.add(text.substring(last));
		}
		return  splitTexts;
	}

	/** creates a String representation  of a list, using only whitespace separators
	 *  not protected if elements contain whitespace.
	 *  e.g. [a, b, c] is returned as "a b c"
	 *  
	 * @param list 
	 * @return "" if null or zero length
	 */
	public static String createWhitespaceSeparatedTokens(List<? extends Object> list) {
		StringBuilder sb = new StringBuilder();
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				String s = list.get(i).toString();
				if (i > 0) sb.append(" ");
				sb.append(s);
			}
		}
		return sb.toString();	
	}


}


class StringIntegerComparator implements Comparator<Object> {
	public int compare(Object o1, Object o2) {
		Integer i1 = strip(o1.toString());
		Integer i2 = strip(o2.toString());
		return i1.compareTo(i2);
	}

	private static Integer strip(String s) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			if (ch >= '0' && ch <= '9') {
				sb.append(ch);
			}
		}
		String ss = sb.toString();
		if (ss.length() == 0) {
			ss = "0";
		}
		return new Integer(ss);
	}

	/** reverses lines in file.
	 * 
	 * line(0) and line(n-1) are swapped, etc.
	 * 
	 * @param inFilename
	 * @param outFilename
	 * @throws Exception
	 */
	public static void reverseLinesInFile(String inFilename, String outFilename) throws Exception {
		List<String> lines = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(inFilename));
		while (true) {
			String line = br.readLine();
			if (line == null) break;
			lines.add(line);
		}
		br.close();
		File file1 = new File(outFilename);
		FileWriter fw = new FileWriter(file1);
		for (int i = lines.size() - 1; i >= 0; i--) {
			fw.write(lines.get(i)+"\n");
		}
		fw.close();
		br.close();
	}
	
	
 }
