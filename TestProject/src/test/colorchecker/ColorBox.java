package test.colorchecker;

import org.opencv.core.Point;

import co.edu.icesi.frutificator.util.Geometry;

public class ColorBox implements Comparable<ColorBox> {

	private Point[] box;
	private Point center;
	private double boxEdgeLength;

	public ColorBox(Point[] box) {
		this.box = box;
		double xSum = 0;
		double ySum = 0;
		Point last = null;
		for (Point point : box) {
			xSum += point.x;
			ySum += point.y;
			if(last != null)
				boxEdgeLength += Geometry.distance(last.x, last.y, point.x, point.y);
			last = point;
		}
		xSum /= box.length;
		ySum /= box.length;
		boxEdgeLength /= (box.length-1);
		center = new Point(xSum, ySum);
	}

	public Point[] getBox() {
		return box;
	}

	public Point getCenter() {
		return center;
	}

	public double getBoxEdgeLength() {
		return boxEdgeLength;
	}

	@Override
	public int compareTo(ColorBox o) {
		Point oc = o.getCenter();
		double w = center.x - oc.x;
		double h = center.y - oc.y;
		double measure = boxEdgeLength/2;
		if(h > measure) {
			return 1;
		} else if(h < measure) {
			return -1;
		} else {
			return (int) w;
		}
	}

}
