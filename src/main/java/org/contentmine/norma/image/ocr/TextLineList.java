package org.contentmine.norma.image.ocr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.IntRange;

/** manages  a list of TextLines
 * 
 * @author pm286
 *
 */
public class TextLineList implements Iterable<TextLine> {
	private static final Logger LOG = Logger.getLogger(TextLineList.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private List<TextLine> textLineList;
	
	private List<TextLine> getOrCreateTextLineList() {
		if (textLineList == null) {
			textLineList = new ArrayList<>();
		}
		return textLineList;
	}

	public void forEach(Consumer<? super TextLine> action) {
		getOrCreateTextLineList();
		textLineList.forEach(action);
	}

	public int size() {
		getOrCreateTextLineList();
		return textLineList.size();
	}

	public boolean isEmpty() {
		getOrCreateTextLineList();
		return textLineList.isEmpty();
	}

	public boolean contains(Object o) {
		getOrCreateTextLineList();
		return textLineList.contains(o);
	}

	public Iterator<TextLine> iterator() {
		getOrCreateTextLineList();
		return textLineList.iterator();
	}

	public Object[] toArray() {
		getOrCreateTextLineList();
		return textLineList.toArray();
	}

	public <T> T[] toArray(T[] a) {
		getOrCreateTextLineList();
		return textLineList.toArray(a);
	}

	public boolean add(TextLine e) {
		getOrCreateTextLineList();
		return textLineList.add(e);
	}

	public boolean remove(Object o) {
		getOrCreateTextLineList();
		return textLineList.remove(o);
	}

	public boolean containsAll(Collection<?> c) {
		getOrCreateTextLineList();
		return textLineList.containsAll(c);
	}

	public boolean addAll(Collection<? extends TextLine> c) {
		getOrCreateTextLineList();
		return textLineList.addAll(c);
	}

	public boolean addAll(int index, Collection<? extends TextLine> c) {
		getOrCreateTextLineList();
		return textLineList.addAll(index, c);
	}

	public boolean removeAll(Collection<?> c) {
		getOrCreateTextLineList();
		return textLineList.removeAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		getOrCreateTextLineList();
		return textLineList.retainAll(c);
	}

	public void sort(Comparator<? super TextLine> c) {
		getOrCreateTextLineList();
		textLineList.sort(c);
	}

	public void clear() {
		getOrCreateTextLineList();
		textLineList.clear();
	}

	public boolean equals(Object o) {
		getOrCreateTextLineList();
		return textLineList.equals(o);
	}

	public int hashCode() {
		getOrCreateTextLineList();
		return textLineList.hashCode();
	}

	public TextLine get(int index) {
		getOrCreateTextLineList();
		return textLineList.get(index);
	}

	public TextLine set(int index, TextLine element) {
		getOrCreateTextLineList();
		return textLineList.set(index, element);
	}

	public void add(int index, TextLine element) {
		getOrCreateTextLineList();
		textLineList.add(index, element);
	}

	public TextLine remove(int index) {
		getOrCreateTextLineList();
		return textLineList.remove(index);
	}

	public int indexOf(Object o) {
		getOrCreateTextLineList();
		return textLineList.indexOf(o);
	}

	public int lastIndexOf(Object o) {
		getOrCreateTextLineList();
		return textLineList.lastIndexOf(o);
	}

	public ListIterator<TextLine> listIterator() {
		getOrCreateTextLineList();
		return textLineList.listIterator();
	}

	public ListIterator<TextLine> listIterator(int index) {
		getOrCreateTextLineList();
		return textLineList.listIterator(index);
	}

	public List<TextLine> subList(int fromIndex, int toIndex) {
		getOrCreateTextLineList();
		return textLineList.subList(fromIndex, toIndex);
	}

	public void add(int i, IntRange yRange) {
		this.add(i,new TextLine(yRange));
	}

	public void add(IntRange yRange) {
		this.add(new TextLine(yRange));
	}
	
	@Override
	public String toString() {
		return textLineList.toString();
	}


}
