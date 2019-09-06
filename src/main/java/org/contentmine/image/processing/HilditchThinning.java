package org.contentmine.image.processing;

import java.awt.image.BufferedImage;


	//http://nayefreza.wordpress.com/2013/05/11/hilditchs-thinning-algorithm-java-implementation/
/**
 *
 * @author nayef
 */
public class HilditchThinning extends Thinning {
	
    public HilditchThinning(BufferedImage image) {
    	super(image);
    }
	

	public HilditchThinning() {
		// TODO Auto-generated constructor stub
	}

    @Override
    public void doThinning() {
        int a, b;
        boolean hasChange;
        do {
            hasChange = false;
            for (int iy = 1; iy + 1 < binary.length; iy++) {
                for (int jx = 1; jx + 1 < binary[iy].length; jx++) {
                    a = getSumCyclicChanges(iy, jx);
                    b = getNeighbourSum(iy, jx);
                    if (getBinary(iy, jx)==1 && 2 <= b && b <= 6 && a == 1
                        && ((getBinary(iy - 1, jx) * getBinary(iy, jx + 1) * getBinary(iy, jx - 1) == 0) || (getSumCyclicChanges(iy - 1, jx) != 1))
                        && ((getBinary(iy - 1, jx) * getBinary(iy, jx + 1) * getBinary(iy + 1, jx) == 0) || (getSumCyclicChanges(iy, jx + 1) != 1)))
                    {
                        binary[iy][jx] = 0;
                        hasChange = true;
                    }
                }
            }
        } while (hasChange);
    }
 
 
}
