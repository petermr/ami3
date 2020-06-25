package org.contentmine.norma.txt;

import java.util.Iterator;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public abstract class AbstractSection implements Iterable<AnnotatedLine> {

	private static final Logger LOG = LogManager.getLogger(AbstractSection.class);
public AnnotatedLineContainer localLineContainer;
	protected AnnotatedLineContainer parentLineContainer;

	public Iterator<AnnotatedLine> iterator() {
		return localLineContainer.iterator();
	}

	public void add(AnnotatedLine section) {
		ensureAnnotatedLineContainer();
		localLineContainer.add(section);
	}

	private void ensureAnnotatedLineContainer() {
		if (localLineContainer == null) {
			localLineContainer = new AnnotatedLineContainer();
		}
	}

	public void addLines(int start, int end) {
		for (int i = start; i < end; i++) {
			this.add(parentLineContainer.get(i));
		}
	}

	public int size() {
		ensureAnnotatedLineContainer();
		return localLineContainer.size(); 
	}
	
	public void makeSection(AbstractSection section, int startNumber, int endNumber) {
		for (int i = startNumber; i < endNumber; i++) {
			section.add(parentLineContainer.get(i));
		}
	}
}
