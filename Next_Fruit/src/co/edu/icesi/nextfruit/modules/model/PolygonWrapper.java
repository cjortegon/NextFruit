package co.edu.icesi.nextfruit.modules.model;

import java.util.ArrayList;
import java.util.Arrays;

import org.opencv.core.Mat;
import org.opencv.core.Point;

import co.edu.icesi.nextfruit.util.Geometry;

/**
 * This class wraps a list of Point objects that form a polygon.
 * It permits to execute polygon operations over it.
 * PolygonWrapper natural order is calculated using a spatial approach where vertical is evaluated first (top to bottom) and horizontal after (left to right).
 * @author cjortegon
 */
public class PolygonWrapper implements Comparable<PolygonWrapper> {

	/**
	 * The list of points that form the polygon.
	 */
	private Point[] box;

	// Properties
	private Point center;
	private double perimeter, area;
	private double top, bottom, left, right;

	/**
	 * Constructor
	 * @param box The list of points to start the polygon
	 * @param reduceToRectangular True if you want to reduce the polygon into rectangular form.
	 */
	public PolygonWrapper(Point[] box, boolean reduceToRectangular) {
		this.box = box;
		if(reduceToRectangular)
			reduceToRectangular();
		Point last = null;
		double cordX[] = new double[box.length];
		double cordY[] = new double[box.length];
		int i = 0;
		for (Point point : box) {
			cordX[i] = point.x;
			cordY[i] = point.y;
			if(last != null) {
				perimeter += Geometry.distance(last.x, last.y, point.x, point.y);
				area += (last.x*point.y-last.y*point.x);
			}
			i ++;
			last = point;
		}
		area += (last.x*cordY[0] - cordX[0]*last.y);
		area = Math.abs(area/2);

		Arrays.sort(cordX);
		Arrays.sort(cordY);
		double centerX = (cordX[0]+cordX[box.length-1])/2;
		double centerY = (cordY[0]+cordY[box.length-1])/2;
		center = new Point(centerX, centerY);
		getBoundaries();
	}

	/**
	 * Reduces the polygon to a rectangular form.
	 */
	public void reduceToRectangular() {
		double xStart = Double.MAX_VALUE, xEnd = Double.MIN_VALUE, yStart = Double.MAX_VALUE, yEnd = Double.MIN_VALUE;
		for (Point p : box) {
			if(p.x < xStart)
				xStart = p.x;
			if(p.x > xEnd)
				xEnd = p.x;
			if(p.y < yStart)
				yStart = p.y;
			if(p.y > yEnd)
				yEnd = p.y;
		}
		box = new Point[]{new Point(xStart, yStart), new Point(xEnd, yStart),
				new Point(xEnd, yEnd), new Point(xStart, yEnd)};
	}

	/**
	 * Groups closed points to reduce the number of needed points that describe the figure.
	 * This operation is made by angles matching. Removes all the edges with angles smaller than 30 degrees.
	 */
	public void reduceBoxPoints() {
		Point last = null;
		ArrayList<Point> newPoints = new ArrayList<>();
		Double lastAngle = null;
		for (Point point : box) {
			if(last == null) {
				newPoints.add(point);
			} else {
				double angle = Geometry.getAngleOfLineBetweenTwoPoints(point.x, point.y, last.x, last.y);
				if(lastAngle == null) {
					newPoints.add(point);
					lastAngle = angle;
				} else if(Math.abs(lastAngle - angle) > Math.PI/6) {
					newPoints.add(point);
					lastAngle = angle;
				}
			}
			last = point;
		}
		box = new Point[newPoints.size()];
		for (int i = 0; i < box.length; i++) {
			box[i] = newPoints.get(i);
		}
	}

	/**
	 * Returns the average RGB color using a Mat object.
	 * It works correctly only with rectangular figures with edges aligned to X and Y axes.
	 * This method is deprecated. Use the PolygonIterator and calculate the average color by yourself.
	 * @param mat The image to get the colors.
	 * @return The average RGB value in double[] format.
	 */
	@Deprecated
	public double[] getAverageRGB(Mat mat) {
		double xStart = Double.MAX_VALUE, xEnd = Double.MIN_VALUE, yStart = Double.MAX_VALUE, yEnd = Double.MIN_VALUE;
		for (Point p : box) {
			if(p.x < xStart)
				xStart = p.x;
			if(p.x > xEnd)
				xEnd = p.x;
			if(p.y < yStart)
				yStart = p.y;
			if(p.y > yEnd)
				yEnd = p.y;
		}
		double qw = (xEnd - xStart)/4, qh = (yEnd - yStart)/4;
		int startX = (int) (xStart + qw), startY = (int) (yStart + qh), endX = (int) (xStart + qw*3), endY = (int) (yStart + qh*3);
		int pixels = (endX - startX)*(endY - startY);
		double r = 0, g = 0, b = 0;
		for (int x = startX; x < endX; x++) {
			for (int y = startY; y < endY; y++) {
				double d[] = mat.get(y, x);
				r += d[2]; g += d[1]; b += d[0];
			}
		}
		return new double[]{r/pixels, g/pixels, b/pixels};
	}

	/**
	 * @return The list of points that form the polygon.
	 */
	public Point[] getPolygon() {
		return box;
	}

	/**
	 * @return The calculated center of mass from the polygon.
	 */
	public Point getCenter() {
		return center;
	}

	/**
	 * @return The calculated contour from the polygon.
	 */
	public double getPerimeter() {
		return perimeter;
	}

	/**
	 * @return The calculated area from the polygon.
	 */
	public double getArea() {
		return area;
	}

	/**
	 * @return The average size length from each side of the polygon.
	 */
	public double getAverageSideLength() {
		return perimeter / (box.length-1);
	}

	private void getBoundaries() {
		top = Double.MAX_VALUE;
		bottom = Double.MIN_VALUE;
		left = Double.MAX_VALUE;
		right = Double.MIN_VALUE;
		for (int i = 0; i < box.length; i++) {
			if(box[i].y < top)
				top = box[i].y;
			if(box[i].y > bottom)
				bottom = box[i].y;
			if(box[i].x < left)
				left = box[i].x;
			if(box[i].x > right)
				right = box[i].x;
		}
	}

	public PolygonIterator getIterator() {
		return new PolygonIterator(this);
	}

	public boolean contains(Point point) {
		boolean c = false;
		for (int i = 0, j = box.length-1; i < box.length; j = i++) {
			if ( ((box[i].y > point.y) != (box[j].y > point.y)) &&
					(point.x < (box[j].x-box[i].x) * (point.y-box[i].y) / (box[j].y-box[i].y) + box[i].x))
				c = !c;
		}
		return c;
	}

	/**
	 * @return The top margin from the figure.
	 */
	public double getTop() {
		return top;
	}

	/**
	 * @return The bottom margin from the figure.
	 */
	public double getBottom() {
		return bottom;
	}

	/**
	 * @return The left margin from the figure.
	 */
	public double getLeft() {
		return left;
	}

	/**
	 * @return The right margin from the figure.
	 */
	public double getRight() {
		return right;
	}

	@Override
	public int compareTo(PolygonWrapper o) {
		Point oc = o.getCenter();
		double w = center.x - oc.x;
		double h = center.y - oc.y;
		double measure = perimeter/2;
		if(h > measure) {
			return 1;
		} else if(h < measure) {
			return -1;
		} else if(w > 0) {
			return 1;
		} else {
			return -1;
		}
	}

}
