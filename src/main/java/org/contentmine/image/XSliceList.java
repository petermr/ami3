package org.contentmine.image;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.contentmine.image.slice.XSlice;

public class XSliceList implements Iterable<XSlice> {

	private List<XSlice> xSliceList;
	
	public XSliceList() {
		this.xSliceList = new ArrayList<XSlice>();
	}
	
	public Iterator<XSlice> iterator() {
		return xSliceList.iterator();
	}

	public int size() {
		return xSliceList.size();
	}

	public XSlice get(int i) {
		return xSliceList.get(i);
	}

	public void add(XSlice xSlice) {
		xSliceList.add(xSlice);
		// TODO Auto-generated method stub
		
	}

	
	
}
