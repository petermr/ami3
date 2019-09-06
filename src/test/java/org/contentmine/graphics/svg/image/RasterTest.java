package org.contentmine.graphics.svg.image;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author John B. Matthews
 */
public class RasterTest extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int WIDTH = 256;
	private static final int HEIGHT = 256;

	@Test
	public void testDummy() {
		
	}
	
	public RasterTest() {
		setPreferredSize(new Dimension(WIDTH * 2, HEIGHT * 2));
	}

	@Override
	public void paintComponent(Graphics g) {
		final BufferedImage image;
		int[] iArray = { 0, 0, 0, 255 };

		image = (BufferedImage) createImage(WIDTH, HEIGHT);
		WritableRaster raster = image.getRaster();
		for (int row = 0; row < HEIGHT; row++) {
			for (int col = 0; col < WIDTH; col++) {
				int v = row * col;
				iArray[0] = col; // red
				iArray[1] = row; // green
				iArray[2] = 0; // blue
				raster.setPixel(col, row, iArray);
			}
		}
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
	}

	public static void mainMethod() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setDefaultLookAndFeelDecorated(true);
				f.setResizable(false);
				RasterTest rt = new RasterTest();
				f.add(rt, BorderLayout.CENTER);
				f.pack();
				f.setVisible(true);
			}
		});
	}
	
	@Test
	@Ignore // requires graphics
	public void testRaster() {
		RasterTest.mainMethod();
	}
}
