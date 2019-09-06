package org.contentmine.image.colour;

import java.awt.image.BufferedImage;

/** processes subpixel antialising.
 * 
 * Colours are translated to anti-colours (e.g. 255-col)
 * which gives the amount of blackness
 * @author pm286
 *
 */
public class AntiColour {

	BufferedImage image;
	int x;
	int y;
	int ared;
	int agreen;
	int ablue;
	int black;

	public AntiColour() {
		
	}

	public AntiColour(BufferedImage bImage, int i, int j) {
		this.image = bImage;
		this.x = i;
		this.y = j;
		int rgb= bImage.getRGB(i, j);
		ared = 255- (rgb & (0xFF0000))/(0x010000);
		agreen = 255 - (rgb & (0x00FF00))/(0x000100);
		ablue = 255 - rgb & (0x0000FF);
	}

	public boolean isLeft() {
		return ared <= agreen && agreen <= ablue;
	}
	
	public boolean isRight() {
		return ared >= agreen && agreen >= ablue;
	}
	
	public AntiColour getRight() {
		AntiColour anti = new AntiColour();
		if (ared < agreen && agreen < ablue) {
			int antiblack = ablue - ared;
			anti.ared = antiblack;
			anti.agreen = antiblack;
			anti.ablue = antiblack;
		} else {
			anti.ared = 0;
			anti.agreen = 0;
			anti.ablue = 0;
		}
		return anti;
	}

	public AntiColour getLeft() {
		AntiColour anti = new AntiColour();
		if (ared > agreen && agreen > ablue) {
			int antiblack = ared - ablue;
			anti.ared = antiblack;
			anti.agreen = antiblack;
			anti.ablue = antiblack;
		} else {
			anti.ared = 0;
			anti.agreen = 0;
			anti.ablue = 0;
		}
		return anti;
	}

	public void average() {
		black = (ared + agreen + ablue)/3;
	}

	public int getGray() {
		average();
		int gray0 = 255 - black;
		return (256*256*256) * 255 + (256*256) * gray0 + (256) * gray0 + gray0;
	}
}
