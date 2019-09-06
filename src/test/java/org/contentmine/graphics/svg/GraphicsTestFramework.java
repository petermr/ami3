package org.contentmine.graphics.svg;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**main purpose is to provide a simple area for visual inspection of 
 * drawing to Graphics (drawElement())
 * @author pm286
 *
 */
public class GraphicsTestFramework {

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.getContentPane().add(new MyPanel());
		frame.setSize(500, 500);
		frame.show();
	} 
}

class MyPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SVGPath path;
	public MyPanel() {
		path = new SVGPath("M100 200L250,300C100 290 240 110 400 230L110 20 Z M 30 40 L 70 20");
	}
	@Override
	public void paint(Graphics g) {
		g.drawString("foo", 100, 200 );
		path.draw((Graphics2D) g);
	}
}
