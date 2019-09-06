package org.contentmine.graphics.svg.symbol;

import java.io.File;

import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.eucl.euclid.Vector2;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.symbol.AbstractSymbol.SymbolFill;
import org.junit.Test;

public class SymbolTest {
	
	@Test
	public void testDraw() {
		CircledCross circledCross = new CircledCross();
		circledCross.setTransform(new Transform2(new Vector2(100., 100.)));
		circledCross.setFontSize(20.);
		SVGSVG.wrapAndWriteAsSVG(circledCross, new File("target/circledCross.svg"));
	}

	@Test
	public void testDraw1() {
		SVGG g = new SVGG();
		CircledCross circledCross = new CircledCross();
		circledCross.setTransform(new Transform2(new Vector2(100., 100.)));
		circledCross.setFontSize(20.);
		g.appendChild(circledCross);
		Square square = new Square();
		square.setTransform(new Transform2(new Vector2(120., 120.)));
		square.setFontSize(15.);
		g.appendChild(square);
		square = new Square();
		square.setTransform(new Transform2(new Vector2(150., 150.)));
		square.setStroke("blue");
		square.setFontSize(20.);
		square.setStrokeWidth(2.5); // no effect as stroke is none
		g.appendChild(square);
		square = new Square();
		square.setTransform(new Transform2(new Vector2(250., 250.)));
		square.setStroke("red");
		square.setSymbolFill(SymbolFill.ALL);
		square.setFontSize(30.);
		g.appendChild(square);
		
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/draw1.svg"));
	}

}
