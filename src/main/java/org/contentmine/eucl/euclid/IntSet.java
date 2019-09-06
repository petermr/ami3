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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;



/**
 * public class IntSet
 * 
 * Stores a unique set of ints (that is cannot contain duplicate ints. The
 * limits can be set with setMin and setMax. There are operations for combining
 * sets (for example NOT, OR) and sets can be built up incrementally.
 * <P>
 * Inverse mapping. IntSets can be used to map one set of indexed data to
 * another, for example
 * <P>
 * <TT> RealArray x = someFunction();<BR>
 * InstSet idx = x.indexSortAscending();<BR>
 * for (int i = 0; i < x.size(); i++) {<BR>
 * y[i] = x[idx[i]];<BR> }<BR>
 * </TT> To map the other way, <TT>x[i] = y[inv[i]];</TT> the inverse IntSet
 * can be used
 * 
 * @author (C) P. Murray-Rust, 1996
 */
public class IntSet implements EuclidConstants {

    final static Logger LOG = Logger.getLogger(IntSet.class);
    private int array[];
    private int nelem = 0;
    private int min = Integer.MIN_VALUE;
    private int max = Integer.MAX_VALUE;

    Set<Integer> set;

	/** gets all permutations of integers from 0 to n-1.
	 * crude
	 * @param number 
	 * @return list of all permutations (no special order)
	 */
	public static Map<Integer, List<List<Integer>>> integerMap = new HashMap<Integer, List<List<Integer>>>();

    /**
     * constructor.
     */
    public IntSet() {
        initialise(0, 0, true);
    }

    /**
     * creates an IntSet with the integers 0...nelem-1
     * 
     * @param nelem
     */
    public IntSet(int nelem) {
        nelem = (nelem < 0) ? 0 : nelem;
        this.nelem = nelem;
        initialise(0, nelem, true);
    }

    /**
     * creates an IntSet with the integers start...end (if start <= end)
     * 
     * @param start
     * @param end
     * 
     */
    public IntSet(int start, int end) {
        nelem = end - start + 1;
        if (nelem <= 0) {
            nelem = 0;
        }
        initialise(start, nelem, true);
    }

    /**
     * copy constructor
     * 
     * @param is
     */
    public IntSet(IntSet is) {
        if (is != null) {
            array = is.array;
            System.arraycopy(is.array, 0, array, 0, nelem);
            nelem = is.nelem;
            min = is.min;
            max = is.max;
            this.set = new HashSet<Integer>();
            for (Integer ii : is.set) {
                this.set.add(ii);
            }
        }
    }

    /**
     * make from an int[] - all values must be distinct;
     * 
     * @param is
     * @exception EuclidRuntimeException
     *                values were not distinct
     */
    public IntSet(int[] is) throws EuclidRuntimeException {
        nelem = is.length;
        initialise(0, nelem, false);
        int i = 0;
        for (int ii : is) {
            if (this.contains(ii)) {
                throw new EuclidRuntimeException("Duplicate value: " + i);
            }
            array[i++] = ii;
            set.add(new Integer(ii));
        }
    }

    /**
     * use another IntSet to subscript this one that is I(new) = I(this)
     * subscripted by I(sub); Result has dimension of I(sub). If any of I(sub)
     * lies outside 0...this.size()-1, throw an error
     * 
     * @param sub
     * @return the matrix
     * 
     * @throws EuclidRuntimeException
     */
    public IntSet getSubscriptedIntSet(IntSet sub) throws EuclidRuntimeException {
        IntSet is = new IntSet(sub.size());
        for (int i = 0; i < sub.size(); i++) {
            int j = sub.elementAt(i);
            if (j < 0 || j >= this.nelem) {
                throw new EuclidRuntimeException("sub index (" + j
                        + ") too large for " + this.toString());
            }
            is.setElementAt(i, this.array[j]);
        }
        return is;
    }

    private void initialise(int start, int nelem, boolean addSet) {
        array = new int[nelem];
        set = new HashSet<Integer>();
        int nToAdd = nelem /* - start */;
        for (int i = 0; i < nToAdd; i++) {
            array[i] = start + i;
            if (addSet) {
                set.add(new Integer(array[i]));
            }
        }
    }

    /**
     * element-by-element comparison of sets
     * 
     * @param is
     * @return equal
     */
    public boolean isEqualTo(IntSet is) {
        for (int i = 0; i < nelem; i++) {
            if (array[i] != is.array[i])
                return false;
        }
        return true;
    }

    /**
     * get elements.
     * 
     * @return elements as array
     */
    public int[] getElements() {
        // since the array may have spare space, contract
        if (nelem != array.length) {
            int[] temp = new int[nelem];
            System.arraycopy(array, 0, temp, 0, nelem);
            array = temp;
        }
        return array;
    }

    /**
     * set maximum allowed value. if current set has elements greater than max
     * throws exception.
     * 
     * @param max
     * @exception EuclidRuntimeException
     */
    public void setMax(int max) throws EuclidRuntimeException {
        for (int i = 0; i < nelem; i++) {
            if (array[i] > max) {
                throw new EuclidRuntimeException("element in set (" + array[i]
                        + ") greater than new max (" + max + S_RBRAK);
            }
        }
        this.max = max;
    }

    /**
     * set minimum allowed value. if current set has elements less than min
     * throws exception.
     * 
     * @param min
     * @exception EuclidRuntimeException
     */
    public void setMin(int min) {
        for (int i = 0; i < nelem; i++) {
            if (array[i] < min) {
                throw new EuclidRuntimeException("element in set (" + array[i]
                        + ") less than new max (" + max + S_RBRAK);
            }
        }
        this.min = min;
    }

    /**
     * size of array.
     * 
     * @return size
     */
    public int size() {
        return nelem;
    }

    /**
     * add integer Fails if it is outside limits or already exists in set
     * 
     * @param value
     * @return if successful
     * 
     * @throws EuclidRuntimeException
     */
    public boolean addElement(int value) throws EuclidRuntimeException {
        if (value < min || value > max) {
            throw new EuclidRuntimeException("value (" + value + ")outside range ("
                    + min + "..." + max + S_RBRAK);
        }
        if (set.contains(value)) {
            throw new EuclidRuntimeException("value already in set: " + value);
        }
        if (nelem >= array.length) {
            int nbuff = (array.length == 0) ? 1 : array.length;
            while (nelem >= nbuff) {
                nbuff *= 2;
            }
            int temp[] = new int[nbuff];
            for (int i = 0; i < nelem; i++) {
                temp[i] = array[i];
            }
            array = temp;
        }
        array[nelem++] = value;
        set.add(new Integer(value));
        return true;
    }

    /**
     * does set contain value.
     * 
     * @param value
     * @return tur if contains
     */
    public boolean contains(int value) {
        return set.contains(new Integer(value));
    }

    /**
     * get element.
     * 
     * @param i
     * @return element
     * @throws ArrayIndexOutOfBoundsException
     */
    public int elementAt(int i) throws ArrayIndexOutOfBoundsException {
        if (i < 0 || i >= nelem) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return array[i];
    }

    /**
     * gets the ints as an IntArray.
     * 
     * @see #getElements()
     * @return the array
     */
    public IntArray getIntArray() {
        int[] temp = new int[nelem];
        System.arraycopy(array, 0, temp, 0, nelem);
        return new IntArray(temp);
    }

    /**
     * sort the IntSet; MODIFIES 'this'
     */
    public void sortAscending() {
        IntArray temp = this.getIntArray();
        temp.sortAscending();
        for (int i = 0; i < nelem; i++) {
            array[i] = temp.array[i];
        }
    }

    /**
     * concatenate sets.
     * 
     * @param is
     *            set to append
     * @throws EuclidRuntimeException
     *             if there are elements in common or exceed max/min
     */
    public void addSet(IntSet is) throws EuclidRuntimeException {
        for (int i = 0; i < is.nelem; i++) {
            int ii = is.elementAt(i);
            if (this.contains(ii)) {
                throw new EuclidRuntimeException("duplicate element " + ii);
            }
            this.addElement(ii);
        }
    }

    /**
     * intersect two sets (that is elements common to both)
     * 
     * @param is
     * @return set
     * 
     */
    public IntSet intersectionWith(IntSet is) {
        IntSet ix = new IntSet();
        for (int i = 0; i < is.nelem; i++) {
            int ii = is.elementAt(i);
            if (this.contains(ii)) {
                ix.addElement(ii);
            }
        }
        return ix;
    }

    /**
     * elements only in first set
     * 
     * @param is
     * @return set
     */
    public IntSet notIn(IntSet is) {
        IntSet ix = new IntSet();
        for (int i = 0; i < this.nelem; i++) {
            int ii = this.elementAt(i);
            if (!is.contains(ii)) {
                ix.addElement(ii);
            }
        }
        return ix;
    }

    /**
     * add all values from an IntRange if range is 2,5 adds 2,3,4,5
     * 
     * @param ir
     */
    public void addRange(IntRange ir) {
        if (ir == null)
            return;
        for (int i = ir.getMin(); i <= ir.getMax(); i++) {
            this.addElement(i);
        }
    }

    /**
     * Inverse mapping - see introduction if <TT>y[i] = x[this.elementAt(i)];</TT>
     * then the result supports </TT>x[i] = y[inv.elementAt(i)];</TT>
     * 
     * @exception ArrayIndexOutOfBoundsException
     *                the set must contain the integers 0...nelem-1
     * @return inverse map
     */
    public IntSet inverseMap() throws ArrayIndexOutOfBoundsException {
        IntSet temp = new IntSet(this.size());
        for (int i = 0; i < size(); i++) {
            temp.setElementAt(this.elementAt(i), i);
        }
        return temp;
    }

    /**
     * private routine to set elements; check is made on the index, but not on
     * duplicate values
     */
    void setElementAt(int i, int value) throws ArrayIndexOutOfBoundsException {
        if (i >= size()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        array[i] = value;
    }

    /**
     * debug.
     */
    public void debug() {
        for (int i = 0; i < nelem; i++) {
            Util.print(S_SPACE + array[i]);
        }
        Util.println();
    }

    /**
     * to string.
     * 
     * @return string
     */
    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append(S_LBRAK);
        for (int i = 0; i < nelem; i++) {
            if (i > 0) {
                s.append(S_COMMA);
            }
            s.append(array[i]);
        }
        s.append(S_RBRAK);
        return s.toString();
    }

	private static List<Integer> copy(List<Integer> list) {
		List<Integer> newList = new ArrayList<Integer>();
		for (Integer ii : list) {
			newList.add(ii);
		}
		return newList;
	}

	/** create all permutations of an integer.
	 * runs from 0,1,... number-1
	 * @param number
	 * @return
	 */
	public static List<int[]> getPermutations(Integer number) {
		List<int[]> intArrayList = new ArrayList<int[]>();
		List<List<Integer>> intListList = IntSet.getPermutations0(number);
		for (List<Integer> intList : intListList) {
			int size = intList.size();
			int[] ints = new int[size];
			for (int i = 0; i < size; i++) {
				ints[i] = intList.get(i).intValue();
			}
			intArrayList.add(ints);
		}
		return intArrayList;
	}

	/** create all permutations of an integer.
	 * runs from 0,1,... number-1
	 * @param number
	 * @return
	 */
	private static List<List<Integer>> getPermutations0(Integer number) {
		List<List<Integer>> listList = IntSet.integerMap.get(number);
		if (listList == null) {
			listList = new ArrayList<List<Integer>>();
			if (number.equals(0)) {
	    		listList.add(new ArrayList<Integer>());
			} else {
		    	List<List<Integer>> listListMinus = IntSet.getPermutations0(new Integer(number - 1));
		    	for (List<Integer> listMinus : listListMinus) {
	    			for (Integer ii = 0; ii < number; ii++) {
	    				List<Integer> copyList = IntSet.copy(listMinus);
	    				copyList.add(ii.intValue(), number);
	    				listList.add(copyList);
	    			}
		    	}
			}
	    	IntSet.integerMap.put(number, listList);
		}
		return listList;
	}

	/** is the IntSet 0,1,2,3...
	 * 
	 * @return true if zero size or ordered
	 */
	public boolean isCountingFromZero() {
		for (int i = 0; i < nelem; i++) {
			if (i != array[i]) return false;
		}
		return true;
	}

	/** gets previous entry.
	 * if no previous entry returns null
	 * 
	 * @return
	 */
	public Integer getLast() {
		return (nelem == 0) ? null : array[nelem - 1];
	}

}
