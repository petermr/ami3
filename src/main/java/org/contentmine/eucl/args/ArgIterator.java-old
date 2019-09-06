package org.xmlcml.args;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.euclid.IntArray;
import org.xmlcml.euclid.IntRange;
import org.xmlcml.euclid.RealArray;
import org.xmlcml.euclid.RealRange;

/** wraps ListIterator as this causes reflection problems.
 * 
 * Tokens of form -[digit]... are treated as numbers
 * Tokens of form -[letter]... are treated as flags
 * 
 * @author pm286
 *
 */
@Deprecated
public class ArgIterator {

	
	private static final Logger LOG = Logger.getLogger(ArgIterator.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private final static char MINUS = '-';
	
	private ListIterator<String> listIterator;
	private RealArray doubleArray;
	
	public ArgIterator(ListIterator<String> listIterator) {
		this.listIterator = listIterator;
	}
	
	public ArgIterator(String[] args) {
		listIterator = args == null ? null : Arrays.asList(args).listIterator();
	}

	public boolean hasNext() {
		return listIterator == null ? false : listIterator.hasNext();
	}
	
	public String previous() {
		return listIterator == null ? null : listIterator.previous();
	}

	public String next() {
		return listIterator == null ? null : listIterator.next();
	}

	/** read tokens until next - sign.
	 * 
	 * minus must be folloed by a letter, not a number
	 * leave iterator ready to read next minus
	 * 
	 * @param argIterator
	 * @return
	 */
	public List<String> createTokenListUpToNextNonDigitMinus(ArgumentOption argumentOption) {
		List<String> list = this.createTokenListUpToNextNonDigitMinus();
		checkListSemantics(argumentOption, list);
		return list;
	}

	private void checkListSemantics(ArgumentOption argumentOption, List<String> list) {
		String msg = null;
		msg = argumentOption.checkArgumentCount(list);
		if (msg != null) throw new IllegalArgumentException(argumentOption.getVerbose()+"; "+msg);
		msg = argumentOption.checkArgumentValues(list);
		if (msg != null) throw new IllegalArgumentException(argumentOption.getVerbose()+"; "+msg);
	}


	/** read tokens until next - sign.
	 * 
	 * leave iterator ready to read next minus
	 * 
	 * @param argIterator
	 * @return
	 */
	public List<String> createTokenListUpToNextNonDigitMinus() {
		List<String> list = new ArrayList<String>();
		while (this.hasNext()) {
			String next = this.next();
			Character next1 = next.length() <= 1 ? null : next.charAt(1);
			// flag is -letter... or --
			if (next.charAt(0) == MINUS && (Character.isLetter(next1) || next1 == MINUS)) {
				this.previous();
				break;
			}
			list.add(next);
		}
		return list;
	}

	// STRING
	public List<String> getStrings(ArgumentOption option) {
		return createTokenListUpToNextNonDigitMinus(option);
	}

	public String getString(ArgumentOption option) {
		List<String> tokens = createTokenListUpToNextNonDigitMinus(option);
		if (tokens.size() != 1) {
			throw new RuntimeException("Expected only 1 argument; found: "+tokens.size());
		}
		return tokens.get(0);
	}

	// BOOLEAN
	public Boolean getBoolean(ArgumentOption option) {
		Boolean bool = null;
		String s = getString(option);
		try {
			bool = new Boolean(s);
		} catch (Exception e) {
			throw new RuntimeException("Cannot create a Boolean from: "+s);
		}
		return bool;
	}
	
	// DOUBLE
	public Double getDouble(ArgumentOption option) {
		Double dubble = null;
		String s = getString(option);
		try {
			dubble = new Double(s);
		} catch (Exception e) {
			throw new RuntimeException("Cannot create a Double from: "+s);
		}
		return dubble;
	}

	// REAL RANGE
	
	public RealRange getRealRange(ArgumentOption option) {
		List<String> tokens = this.createTokenListUpToNextNonDigitMinus(option);
		List<RealRange> intRangeList = RealRange.createRealRangeList(tokens);
		if (intRangeList.size() != 1) {
			throw new RuntimeException("requires exactly one RealRange token: "+tokens);
		}
		return intRangeList.get(0);
	}
	
	public List<RealRange> getRealRangeList(ArgumentOption option) {
		List<String> tokens = this.createTokenListUpToNextNonDigitMinus(option);
		return RealRange.createRealRangeList(tokens);
	}

	// REAL ARRAY
	public RealArray getDoubleArray(ArgumentOption option) {
		List<String> tokens = this.createTokenListUpToNextNonDigitMinus(option);
		RealArray realArray = null;
		try {
			realArray = new RealArray(tokens.toArray(new String[0]));
		} catch (Exception e) {
			throw new RuntimeException("bad real array"+tokens, e);
		}
		return realArray;
	}
	
	// INTEGER
	public Integer getInteger(ArgumentOption option) {
		Integer intg = null;
		String s = getString(option);
		try {
			intg = new Integer(s);
		} catch (Exception e) {
			throw new RuntimeException("Cannot create an Integer from: "+s, e);
		}
		return intg;
	}

	// INT RANGE
	public IntRange getIntRange(ArgumentOption option) {
		List<String> tokens = this.createTokenListUpToNextNonDigitMinus(option);
		List<IntRange> intRangeList = IntRange.createIntRangeList(tokens);
		if (intRangeList.size() != 1) {
			throw new RuntimeException("requires exactly one IntRange token: "+tokens);
		}
		return intRangeList.get(0);
	}
	
	public List<IntRange> getIntRangeList(ArgumentOption option) {
		List<String> tokens = this.createTokenListUpToNextNonDigitMinus(option);
		return IntRange.createIntRangeList(tokens);
	}

	// INT ARRAY
	public IntArray getIntArray(ArgumentOption option) {
		List<String> tokens = this.createTokenListUpToNextNonDigitMinus(option);
		IntArray intArray = null;
		try {
			intArray = new IntArray(tokens.toArray(new String[0]));
		} catch (Exception e) {
			throw new RuntimeException("bad integer array"+tokens, e);
		}
		return intArray;
	}
	

}