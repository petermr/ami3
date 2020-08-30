package org.contentmine.ami.tools.junit5;

public class Person {

	private String first;
	private String last;

	public Person(String first, String last) {
		this.first = first;
		this.last = last;
	}

	public String getFirstName() {
		return first;
	}

	public String getLastName() {
		return last;
	}

}
