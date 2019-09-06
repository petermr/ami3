package org.contentmine.image.processing;

	 
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import org.contentmine.image.ImageUtil;
 
/**
 *
 * @author nayef
 */
public class ThinningService {
 
    private BufferedImage image;
	private int[][] binaryImage;
	private boolean hasChange;

	public ThinningService(BufferedImage image) {
    	this.image = image;
	    binaryImage = new int[image.getHeight()][image.getWidth()];
    	copyImageToBinary(image, binaryImage);
    }
	

	public ThinningService() {
		// TODO Auto-generated constructor stub
	}

	public int[][] doThinning(/*int[][] binaryImage*/) {
        int a, b;
 
        List<Point> pointsToChange = new LinkedList<Point>();
        do {
 
            hasChange = false;
            for (int y = 1; y + 1 < binaryImage.length; y++) {
                for (int x = 1; x + 1 < binaryImage[y].length; x++) {
                    a = getA(binaryImage, y, x);
                    b = getB(binaryImage, y, x);
                    if ( binaryImage[y][x]==1 && 2 <= b && b <= 6 && a == 1
                            && (binaryImage[y - 1][x] * binaryImage[y][x + 1] * binaryImage[y + 1][x] == 0)
                            && (binaryImage[y][x + 1] * binaryImage[y + 1][x] * binaryImage[y][x - 1] == 0)) {
                        pointsToChange.add(new Point(x, y));
                        //binaryImage[y][x] = 0;
                        hasChange = true;
                    }
                }
            }
 
            for (Point point : pointsToChange) {
                binaryImage[point.getY()][point.getX()] = 0;
            }
 
            pointsToChange.clear();
 
            for (int y = 1; y + 1 < binaryImage.length; y++) {
                for (int x = 1; x + 1 < binaryImage[y].length; x++) {
                    a = getA(binaryImage, y, x);
                    b = getB(binaryImage, y, x);
                    if ( binaryImage[y][x]==1 && 2 <= b && b <= 6 && a == 1
                            && (binaryImage[y - 1][x] * binaryImage[y][x + 1] * binaryImage[y][x - 1] == 0)
                            && (binaryImage[y - 1][x] * binaryImage[y + 1][x] * binaryImage[y][x - 1] == 0)) {
                        pointsToChange.add(new Point(x, y));
 
                        hasChange = true;
                    }
                }
            }
 
            for (Point point : pointsToChange) {
                binaryImage[point.getY()][point.getX()] = 0;
            }
 
            pointsToChange.clear();
 
        } while (hasChange);
 
        return binaryImage;
    }
 
    private class Point {
 
        private int x, y;
 
        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
 
        public int getX() {
            return x;
        }
 
        public void setX(int x) {
            this.x = x;
        }
 
        public int getY() {
            return y;
        }
 
        public void setY(int y) {
            this.y = y;
        }
    };
 
    private int getA(int[][] binaryImage, int y, int x) {
 
        int count = 0;
        //p2 p3
        if (binaryImage[y - 1][x] == 0 && binaryImage[y - 1][x + 1] == 1) {
            count++;
        }
        //p3 p4
        if (binaryImage[y - 1][x + 1] == 0 && binaryImage[y][x + 1] == 1) {
            count++;
        }
        //p4 p5
        if (binaryImage[y][x + 1] == 0 && binaryImage[y + 1][x + 1] == 1) {
            count++;
        }
        //p5 p6
        if (binaryImage[y + 1][x + 1] == 0 && binaryImage[y + 1][x] == 1) {
            count++;
        }
        //p6 p7
        if (binaryImage[y + 1][x] == 0 && binaryImage[y + 1][x - 1] == 1) {
            count++;
        }
        //p7 p8
        if (binaryImage[y + 1][x - 1] == 0 && binaryImage[y][x - 1] == 1) {
            count++;
        }
        //p8 p9
        if (binaryImage[y][x - 1] == 0 && binaryImage[y - 1][x - 1] == 1) {
            count++;
        }
        //p9 p2
        if (binaryImage[y - 1][x - 1] == 0 && binaryImage[y - 1][x] == 1) {
            count++;
        }
 
        return count;
    }
 
    private int getB(int[][] binaryImage, int y, int x) {
 
        return binaryImage[y - 1][x] + binaryImage[y - 1][x + 1] + binaryImage[y][x + 1]
                + binaryImage[y + 1][x + 1] + binaryImage[y + 1][x] + binaryImage[y + 1][x - 1]
                + binaryImage[y][x - 1] + binaryImage[y - 1][x - 1];
    }
    

   /**
    * @param args the command line arguments
    */
   public static void main(String[] args) throws IOException {
    
       BufferedImage image = ImageUtil.readImage(new File("/home/nayef/Desktop/bw.jpg"));

       int[][] imageData = new int[image.getHeight()][image.getWidth()];
       Color c;
       for (int y = 0; y < imageData.length; y++) {
           for (int x = 0; x < imageData[y].length; x++) {

               if (image.getRGB(x, y) == Color.BLACK.getRGB()) {
                   imageData[y][x] = 1;
               } else {
                   imageData[y][x] = 0;

               }
           }
       }

       ThinningService thinningService = new ThinningService();
    
       thinningService.doThinning(/*imageData*/);
        
       for (int y = 0; y < imageData.length; y++) {

           for (int x = 0; x < imageData[y].length; x++) {

               if (imageData[y][x] == 1) {
                   image.setRGB(x, y, Color.BLACK.getRGB());

               } else {
                   image.setRGB(x, y, Color.WHITE.getRGB());
               }


           }
       }

       ImageIO.write(image, "jpg", new File("/home/nayef/Desktop/bwThin.jpg"));

   }
   
	private void copyImageToBinary(BufferedImage image, int[][] imageData) {
		for (int y = 0; y < imageData.length; y++) {
           for (int x = 0; x < imageData[y].length; x++) {

               if (image.getRGB(x, y) == Color.BLACK.getRGB()) {
                   imageData[y][x] = 1;
               } else {
                   imageData[y][x] = 0;

               }
           }
       }
	}
	
	private void copyBinaryToImage(BufferedImage image, int[][] imageData) {
		for (int y = 0; y < imageData.length; y++) {

	           for (int x = 0; x < imageData[y].length; x++) {

	               if (imageData[y][x] == 1) {
	                   image.setRGB(x, y, Color.BLACK.getRGB());

	               } else {
	                   image.setRGB(x, y, Color.WHITE.getRGB());
	               }


	           }
	       }
	}

	public int[][] getBinaryImage() {
		return binaryImage;
	}


	public BufferedImage getThinnedImage() {
	    copyBinaryToImage(image, binaryImage);
		return image;
	}

}
