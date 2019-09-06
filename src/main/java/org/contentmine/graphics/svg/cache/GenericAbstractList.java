package org.contentmine.graphics.svg.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
/** a generic abstract list for subclassing to create FooList objects.
 * delegates required methods to list.methods
 * 
 * @author pm286
 *
 * @param <T>
 */
public class GenericAbstractList <T extends Object> implements List<T> , Iterable<T> {

	protected List<T> genericList;

	protected GenericAbstractList() {
	}
	
	protected void ensureGenericList() {
		if (genericList == null) {
			genericList = new ArrayList<T>();
		}
	}
	
	@Override
	public int size() {
		ensureGenericList();
		return genericList.size();
	}

	@Override
	public boolean isEmpty() {
		ensureGenericList();
		return genericList.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		ensureGenericList();
		return genericList.contains(o);
	}

	@Override
	public Iterator<T> iterator() {
		ensureGenericList();
		return genericList.iterator();
	}

	@Override
	public Object[] toArray() {
		ensureGenericList();
		return genericList.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		ensureGenericList();
		return genericList.toArray(a);
	}

	@Override
	public boolean add(T e) {
		ensureGenericList();
		return genericList.add(e);
	}

	@Override
	public boolean remove(Object o) {
		ensureGenericList();
		return genericList.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		ensureGenericList();
		return genericList.contains(c);
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		ensureGenericList();
		return genericList.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		ensureGenericList();
		return genericList.addAll(index, c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		ensureGenericList();
		return genericList.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		ensureGenericList();
		return genericList.retainAll(c);
	}

	@Override
	public void clear() {
		ensureGenericList();
		genericList.clear();
	}

	@Override
	public T get(int index) {
		ensureGenericList();
		return genericList.get(index);
	}

	@Override
	public T set(int index, T element) {
		ensureGenericList();
		// fill top of list with nulls
		for (int i = genericList.size(); i <= index; i++) {
			genericList.add(null);
		}
		return genericList.set(index, element);
	}

	@Override
	public void add(int index, T element) {
		ensureGenericList();
		genericList.add(index, element);
	}

	@Override
	public T remove(int index) {
		ensureGenericList();
		return genericList.remove(index);
	}

	@Override
	public int indexOf(Object o) {
		ensureGenericList();
		return genericList.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		ensureGenericList();
		return genericList.lastIndexOf(o);
	}

	@Override
	public ListIterator<T> listIterator() {
		ensureGenericList();
		return genericList.listIterator();
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		ensureGenericList();
		return genericList.listIterator(index);
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		ensureGenericList();
		return genericList.subList(fromIndex, toIndex);
	}

	@Override
	public String toString() {
		ensureGenericList();
		return genericList.toString();
	}
}
