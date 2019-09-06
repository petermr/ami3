package org.contentmine.eucl.pom;

public class Dependency {

	private Pom pom;
	private MvnProject upstream;

	public Dependency(Pom pom, MvnProject upstream) {
		this.pom = pom;
		this.upstream = upstream;
	}
	
	public String toString() {
		return pom+"==>"+upstream;
	}

	public String getDot() {
		String s = "\""+pom+"\""+" -> "+"\""+upstream+"\"";
		return s;
	}

}
