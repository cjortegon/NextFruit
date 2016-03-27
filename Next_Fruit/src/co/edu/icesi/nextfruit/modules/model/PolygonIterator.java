package co.edu.icesi.nextfruit.modules.model;

import java.util.Iterator;

import org.opencv.core.Point;

/**
 * This object is used to iterate over all the containing points inside a polygon. Points are defined as absolute values.
 * @author cjortegon
 */
public class PolygonIterator implements Iterator<Point> {

	private PolygonWrapper polygon;
	private Point next;
	private int top, bottom, left, right;
	private int x, y;

	/**
	 * This constructor is used by PolygonWrapper in getIterator method.
	 * @param polygon
	 */
	public PolygonIterator(PolygonWrapper polygon) {
		this.polygon = polygon;
		this.top = (int) Math.ceil(polygon.getTop());
		this.bottom = (int) Math.floor(polygon.getBottom()) - 1;
		this.left = (int) Math.ceil(polygon.getLeft());
		this.right = (int) Math.floor(polygon.getRight()) - 1;
		this.x = left;
		this.y = top;
	}

	private void getNext() {
		if(next == null) {
			Point p = new Point(x, y);
			boolean contains = false;
			do {
				contains = polygon.contains(p);
				if(iterate())
					p = new Point(x, y);
				else
					break;
			} while(!contains);
			if(contains)
				next = p;
		}
	}

	private boolean iterate() {
		x ++;
		x %= right;
		if(x == 0) {
			y ++;
			x = left;
		}
		return y <= bottom;
	}

	@Override
	public boolean hasNext() {
		getNext();
		return next != null;
	}

	@Override
	public Point next() {
		getNext();
		Point nextPoint = next;
		next = null;
		return nextPoint;
	}

}
