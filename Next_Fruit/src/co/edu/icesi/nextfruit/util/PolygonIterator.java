package co.edu.icesi.nextfruit.util;

import java.util.Iterator;

import org.opencv.core.Point;

import co.edu.icesi.nextfruit.modules.model.PolygonWrapper;

public class PolygonIterator implements Iterator<Point> {

	private PolygonWrapper polygon;
	private Point next;
	private int top, bottom, left, right;
	private int x, y;

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
