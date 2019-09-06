package org.contentmine.image.geom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Array;

/**
 * Reduces the number of points in a shape using the Douglas-Peucker algorithm. <br>
 * From:
 * http://www.phpriot.com/articles/reducing-map-path-douglas-peucker-algorithm/4<br>
 * Ported from PHP to Java. "marked" array added to optimize.
 * 
 * @author M. Kergall
 */
public class DouglasPeucker {
	
	private static class Corner {
		int index;
		double deviation;

		public Corner(int index, double deviation) {
			this.index = index;
			this.deviation = deviation;
		}
	}

	private double tolerance;
	int cornerFindingWindow;
	double relativeCornernessThresholdForCornerAggregation;
	double allowedDifferenceCornerMaximumDeviating;
	int maxNumberCornersToSearch;
	private boolean[] marked;
	private List<Real2> shape;
	private List<Real2> newShape;
	private double maxDeviation;
	private int indexOfMaxDeviation;
	double[] cornernesses;
	int[] positionsCorners;
	private double secondGreatestCornerness;
	private int positionSecondGreatestCornerness;
	private double greatestCornerness;
	private int positionGreatestCornerness;

	public DouglasPeucker(double tolerance) {
		this.tolerance = tolerance;
	}
	
	public DouglasPeucker(double tolerance, int cornerFindingWindow, double relativeCornernessThresholdForCornerAggregation, double allowedDifferenceCornerMaximumDeviating, int maxNumberCornersToSearch) {
		this.tolerance = tolerance;
		this.cornerFindingWindow = cornerFindingWindow;
		this.relativeCornernessThresholdForCornerAggregation = relativeCornernessThresholdForCornerAggregation;
		this.allowedDifferenceCornerMaximumDeviating = allowedDifferenceCornerMaximumDeviating;
		this.maxNumberCornersToSearch = maxNumberCornersToSearch;
	}
	
	/**
	 * Reduce the number of points in a shape using the Douglas-Peucker
	 * algorithm
	 * 
	 * @param shape The shape to reduce
	 * @return the reduced shape
	 */
	public List<Real2> reduce(List<Real2> shape) {
		this.shape = shape;
		int n = shape.size();
		if (n < 3) {
			return shape;
		}

		marked = new boolean[n];
											// marked as "true"
		for (int i = 1; i < n - 1; i++) {
			marked[i] = false;
		}
		
		calculateCornernesses(shape, n);
		
		if (shape.get(0).isEqualTo(shape.get(shape.size() - 1), 1.5)) {
			Collections.rotate(shape, -positionGreatestCornerness);
			// first and last points
			marked[0] = true;
			marked[n - 1] = true;
			int split = (positionSecondGreatestCornerness - positionGreatestCornerness >= 0 ? positionSecondGreatestCornerness - positionGreatestCornerness : positionSecondGreatestCornerness - positionGreatestCornerness + shape.size());
			marked[split] = true;

			calculateCornernesses(shape, n);
			douglasPeuckerReduction(0, split);
			douglasPeuckerReduction(split, n - 1);
		} else {
			// first and last points
			marked[0] = true;
			marked[n - 1] = true;
	
			douglasPeuckerReduction(0, n - 1);
		}
		
		newShape = createNewShapeFromMarked();
		return newShape;
	}

	private void calculateCornernesses(List<Real2> shape, int n) {
		cornernesses = new double[shape.size()];
		positionsCorners = new int[shape.size()];
		greatestCornerness = 0;
		secondGreatestCornerness = 0;
		for (int idx = 1; idx < n - 1; idx++) {
			Real2 point = shape.get(idx);
			List<Real2> localPoints = new ArrayList<Real2>();
			try {
				for (int idx2 = idx - cornerFindingWindow; idx2 <= idx + cornerFindingWindow; idx2++) {
					localPoints.add(shape.get(idx2));
				}
				cornernesses[idx] = Real2.getCentroid(localPoints).getDistance(point);
			} catch (IndexOutOfBoundsException e) {
				
			}
			if (idx > cornerFindingWindow * 2) {
				List<Real2> corners = new ArrayList<Real2>();
				try {
					if (cornernesses[idx - cornerFindingWindow * 2] >= cornernesses[idx - cornerFindingWindow * 2 + 1]) {
						corners.add(new Real2(idx - cornerFindingWindow * 2, cornernesses[idx - cornerFindingWindow * 2]));
					}
				} catch (IndexOutOfBoundsException e) {
					
				}
				try {
					if (cornernesses[idx] >= cornernesses[idx - 1]) {
						corners.add(new Real2(idx, cornernesses[idx]));
					}
				} catch (IndexOutOfBoundsException e) {
					
				}
				for (int idx2 = idx - cornerFindingWindow * 2 + 1; idx2 < idx; idx2++) {
					double thisCornerness = 0;
					double leftCornerness = -1;
					double rightCornerness = -1;
					try {
						thisCornerness = cornernesses[idx2];
					} catch (IndexOutOfBoundsException e) {
						continue;
					}
					try {
						leftCornerness = cornernesses[idx2 - 1];
					} catch (IndexOutOfBoundsException e) {
						
					}
					rightCornerness = cornernesses[idx2 + 1];
					if (thisCornerness >= leftCornerness && thisCornerness >= rightCornerness) {
						corners.add(new Real2(idx2, thisCornerness));
					}
				}
				
				Collections.sort(corners, new Comparator<Real2>(){
					public int compare(Real2 o1, Real2 o2) {
						return Double.compare(o2.getY(), o1.getY());
					}
				});
				positionsCorners[idx - cornerFindingWindow] = (int) ((corners.size() == 1 || corners.get(1).getY() < relativeCornernessThresholdForCornerAggregation * corners.get(0).getY()) ? corners.get(0).getX() : corners.get(0).getMidPoint(corners.get(1)).getX());
				if (cornernesses[idx - cornerFindingWindow] > greatestCornerness) {
					secondGreatestCornerness = greatestCornerness;
					positionSecondGreatestCornerness = positionGreatestCornerness;
					greatestCornerness = cornernesses[idx - cornerFindingWindow];
					positionGreatestCornerness = positionsCorners[idx - cornerFindingWindow];
				} else if (cornernesses[idx - cornerFindingWindow] > secondGreatestCornerness) {
					secondGreatestCornerness = cornernesses[idx - cornerFindingWindow];
					positionSecondGreatestCornerness = positionsCorners[idx - cornerFindingWindow];
				}
			}
		}
	}
	
	private List<Real2> createNewShapeFromMarked() {
		newShape = new ArrayList<Real2>(); 
		for (int i = 0; i < shape.size(); i++) {
			if (marked[i]) {
				newShape.add(shape.get(i));
			}
		}
		return newShape;
	}

	/**
	 * Reduce the points in shape between the specified first and last index.
	 * Mark the points to keep in marked[]
	 * 
//	 * @param shape
//	 *            The original shape
//	 * @param marked
//	 *            The points to keep (marked as true)
//	 * @param tolerance
//	 *            The tolerance to determine if a point is kept
	 * @param firstIdx
	 *            The index in original shape's point of the starting point for
	 *            this line segment
	 * @param lastIdx
	 *            The index in original shape's point of the ending point for
	 *            this line segment
	 */
	private void douglasPeuckerReduction(int firstIdx, int lastIdx) {
		// overlapping indexes
		if (lastIdx <= firstIdx + 1) {
			return;
		}

		int idxMax = findMaximallyDeviatingPoint(shape, firstIdx, lastIdx);

		if (maxDeviation > tolerance) {
			marked[indexOfMaxDeviation] = true;
			douglasPeuckerReduction(firstIdx, idxMax);
			douglasPeuckerReduction(idxMax, lastIdx);
		}
	}
	
	private void douglasPeuckerReductionOld(int firstIdx, int lastIdx) {
		// overlapping indexes
		if (lastIdx <= firstIdx + 1) {
			return;
		}

		findMaximallyDeviatingPoint(shape, firstIdx, lastIdx);

		if (maxDeviation > tolerance) {
			marked[indexOfMaxDeviation] = true;
			douglasPeuckerReduction(firstIdx, indexOfMaxDeviation);
			douglasPeuckerReduction(indexOfMaxDeviation, lastIdx);
		}
	}
	
	private int findMaximallyDeviatingPoint(List<Real2> shape, int firstIdx, int lastIdx) {
		maxDeviation = 0.0;
		indexOfMaxDeviation = 0;
		//int indexOfMaxCornerness = 0;
		//double deviationOfMaxCornerness = 0.0;
		PriorityQueue<Corner> queue = new PriorityQueue<Corner>(shape.size(), new Comparator<Corner>() {

			public int compare(Corner o1, Corner o2) {
				return Double.compare(cornernesses[o2.index], cornernesses[o1.index]);
			}
			
		});

		Real2 firstPoint = shape.get(firstIdx);
		Real2 lastPoint = shape.get(lastIdx);

		for (int idx = firstIdx + 1; idx < lastIdx; idx++) {
			Real2 point = shape.get(idx);
			
			double distance = orthogonalDistance(point, firstPoint, lastPoint);
			// the point with the greatest distance
			if (distance > maxDeviation) {
				maxDeviation = distance;
				indexOfMaxDeviation = idx;
			}
			
			//cornernesses[idx] > cornernesses[indexOfMaxCornerness] && 
			if (idx > firstIdx + cornerFindingWindow && idx < lastIdx - cornerFindingWindow) {
				queue.add(new Corner(positionsCorners[idx], distance));
				//indexOfMaxCornerness = positionsCorners[idx];
				//deviationOfMaxCornerness = distance;
			}
		}
		
		if (queue.size() > 0) {
			int i = 0;
			int previousIndex;
			for (Corner c = queue.poll(); c != null && i < maxNumberCornersToSearch; previousIndex = c.index, c = queue.poll(), i += (c != null && c.index != previousIndex ? 1 : 0)) {
				if (maxDeviation - c.deviation < allowedDifferenceCornerMaximumDeviating && c.deviation > tolerance) {
					indexOfMaxDeviation = c.index;
				}
			}
		}
		/*if (indexOfMaxCornerness != 0 && maxDeviation - deviationOfMaxCornerness < allowedDifferenceCornerMaximumDeviating && deviationOfMaxCornerness > tolerance) {
			indexOfMaxDeviation = indexOfMaxCornerness;
		}*/
		return indexOfMaxDeviation;
	}

	/**
	 * Calculate the orthogonal distance from the line joining the lineStart and
	 * lineEnd points to point
	 * 
	 * @param point
	 * @param lineStart
	 * @param lineEnd
	 * @return distance
	 */
	private double orthogonalDistance(Real2 point, Real2 lineStart,
			Real2 lineEnd) {
		double area = Math.abs(
				(lineStart.getY() * lineEnd.getX() 
				+ lineEnd.getY() * point.getX() 
				+ point.getY() * lineStart.getX() 
				- lineEnd.getY() * lineStart.getX()
				- point.getY() * lineEnd.getX() 
				- lineStart.getY() * point.getX()
				) / 2.0);

		double bottom = Math.hypot(
				lineStart.getY() - lineEnd.getY(),
				lineStart.getX() - lineEnd.getX());

		return (area / bottom * 2.0);
	}
	public Real2Array reduceToArray(Real2Array real2Array) {
		 List<Real2> real2List = reduce(real2Array.getList());
		return new Real2Array(real2List);
	}
}
