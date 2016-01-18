package test.colorchecker;

import org.opencv.core.Point;

public class ColorBox {

	private Point[] box;
	private Point center;

	public ColorBox(Point[] box) {
		this.box = box;
		double xSum = 0;
		double ySum = 0;
		for (Point point : box) {
			xSum += point.x;
			ySum += point.y;
		}
		xSum /= box.length;
		ySum /= box.length;
		center = new Point(xSum, ySum);
	}

}
