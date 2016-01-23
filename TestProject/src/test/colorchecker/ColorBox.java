package test.colorchecker;

import java.util.Arrays;

import org.opencv.core.Point;

import co.edu.icesi.frutificator.util.Geometry;

public class ColorBox implements Comparable<ColorBox> {

	private Point[] box;
	private Point center;
	private double perimeter, area;

	public ColorBox(Point[] box) {
		this.box = box;
		Point last = null;
		double cordX[] = new double[box.length];
		double cordY[] = new double[box.length];
		int i = 0;
		for (Point point : box) {
			cordX[i] = point.x;
			cordY[i] = point.y;
			i ++;
			if(last != null) {
				perimeter += Geometry.distance(last.x, last.y, point.x, point.y);
				area += (last.x*point.y-last.y*point.x);
			}
			last = point;
		}
		area += (last.x*cordY[0]-last.y*cordX[0]);
		area = Math.abs(area/2);
		
		Arrays.sort(cordX);
		Arrays.sort(cordY);
		double centerX = (cordX[0]+cordX[box.length-1])/2;
		double centerY = (cordY[0]+cordY[box.length-1])/2;
		center = new Point(centerX, centerY);
		System.out.println("Area: "+area+" Perimeter: "+perimeter);
	}

	public Point[] getBox() {
		return box;
	}

	public Point getCenter() {
		return center;
	}

	public double getPerimeter() {
		return perimeter;
	}

	public double getArea() {
		return area;
	}

	public double getAverageSideLength() {
		return perimeter / (box.length-1);
	}

	public int getNumberOfSides() {
		return box.length;
	}

	@Override
	public int compareTo(ColorBox o) {
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
