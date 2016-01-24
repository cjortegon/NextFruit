package test.colorchecker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import co.edu.icesi.frutificator.util.Geometry;
import co.edu.icesi.frutificator.util.Statistics;
import test.silhouettedetector.SilhouetteDetector;

public class ColorChecker {

	private Mat BGR, LAB, gray, MBlurred, silhouette;
	private ColorBox grid[][];
	private ArrayList<ColorBox> boxes;
	private int numberOfColors;
	private double averageBoxArea, averageBoxDistance, distanceDeviation;

	public ColorChecker(String imagePath, int sensibility, int[] size) {

		// Setting parameters
		this.numberOfColors = size[0]*size[1];
		this.grid = new ColorBox[size[0]][size[1]];

		// Reading image
		BGR = Imgcodecs.imread(imagePath);

		// Median blur
		MBlurred = new Mat();
		int ksize = (int) (Math.sqrt(BGR.width()*BGR.height())/60);
		if(ksize %2 == 0)
			ksize ++;
		System.out.println("ksize for blur: "+ksize);
		Imgproc.medianBlur(BGR, MBlurred, ksize);

		// Silhouette detection
		silhouette = new Mat();
		Imgproc.threshold(MBlurred, silhouette, sensibility, 255, 1);

		// Gray scale
		gray = silhouette.clone();
		Imgproc.cvtColor(silhouette, gray, Imgproc.COLOR_BGR2GRAY, 1);

		// Getting colors
		obtainBoxes(sensibility, gray.clone());
		filterBoxes();
		System.out.println("Average distance between boxes: "+averageBoxDistance);
		System.out.println("Average area of boxes: "+averageBoxArea);
		obtainColors();
		defineGrid();
	}

	private void obtainBoxes(int sensibility, Mat mat) {
		Mat thresh = new Mat();
		Imgproc.threshold(mat, thresh, sensibility, 255, 1);
		List<MatOfPoint> contours = new ArrayList<>();
		Imgproc.findContours(thresh, contours, mat, 1, 2);
		System.out.println("Contours: "+contours.size());

		boxes = new ArrayList<ColorBox>();
		int i = 0;
		for (MatOfPoint cnt : contours) {
			boxes.add(new ColorBox(cnt.toArray()));
		}
		//		Collections.sort(boxes);
	}

	private void filterBoxes() {

		// Getting statistics
		Statistics area = new Statistics();
		Statistics perimeter = new Statistics();
		for (int i = 0; i < boxes.size(); i++) {
			area.addValue(boxes.get(i).getArea());
			perimeter.addValue(boxes.get(i).getPerimeter());
		}
		System.out.println("Deviations -> (Deviation: "+((int)area.getStandardDeviation())
				+")(Perimeter: "+((int)perimeter.getStandardDeviation())+")");

		// Separating groups by area and perimeter differences
		GroupManager groupManager = new GroupManager(new double[]{area.getStandardDeviation(), perimeter.getStandardDeviation()});
		for (int i = 0; i < boxes.size(); i++) {
			groupManager.add(boxes.get(i), new double[]{boxes.get(i).getArea(), boxes.get(i).getPerimeter()});
		}
		groupManager.makeGroups();

		// Filtering by the distance relation between figures
		ArrayList<ColorBox>[] groups = new ArrayList[groupManager.getNumberOfGroups()];
		double[][] deviations = new double[groups.length][2];
		for (int i = 0; i < groups.length; i++) {
			groups[i] = groupManager.getGroups(i+1);
			if(groups[i].size() > 1) {
				Statistics distances = new Statistics();
				for (int j = 0; j < groups[i].size()-1; j++) {
					double smallDist = Double.MAX_VALUE;
					//					double levelDist = Math.sqrt(groups[i].get(j).getArea())*0.25;
					for (int k = j+1; k < groups[i].size(); k++) {
						Point a = groups[i].get(j).getCenter();
						Point b = groups[i].get(k).getCenter();
						//						double xx = Math.abs(a.x - b.x);
						//						double yy = Math.abs(a.y - b.y);
						//						if(xx < levelDist && xx < smallDist)
						//							smallDist = xx;
						//						if(yy < levelDist && yy < smallDist)
						//							smallDist = yy;
						double distTmp = Geometry.distance(a.x, a.y, b.x, b.y);
						if(distTmp < smallDist)
							smallDist = distTmp;
					}
					distances.addValue(smallDist);
				}
				deviations[i][0] = distances.getMean();
				deviations[i][1] = distances.getStandardDeviation();
			}
		}

		// Removing boxes that are not correlated
		int index = 0;
		distanceDeviation = Double.MAX_VALUE;
		for (int i = 0; i < deviations.length; i++) {
			if(groups[i].size() > 1 && deviations[i][1] < distanceDeviation) {
				distanceDeviation = deviations[i][1];
				index = i;
			}
		}
		boxes.clear();
		boxes.addAll(groups[index]);
		averageBoxDistance -= (distanceDeviation/1.75);
	}

	private void defineGrid() {

		// Getting average area
		Statistics boxStat = new Statistics();
		double xStart = Double.MAX_VALUE, xEnd = Double.MIN_VALUE, yStart = Double.MAX_VALUE, yEnd = Double.MIN_VALUE;
		for (ColorBox box : boxes) {
			boxStat.addValue(box.getArea());
			Point center = box.getCenter();
			if(center.x < xStart)
				xStart = center.x;
			if(center.x > xEnd)
				xEnd = center.x;
			if(center.y < yStart)
				yStart = center.y;
			if(center.y > yEnd)
				yEnd = center.y;
		}
		averageBoxDistance = (((xEnd-xStart)/(grid[0].length-1))+((yEnd-yStart)/(grid.length-1)))/2;
		averageBoxArea = boxStat.getMean();
		double diff = Math.sqrt(averageBoxArea)/2;
		Collections.sort(boxes);
		//		for (ColorBox box : boxes) {
		//			System.out.println("("+((int)box.getCenter().x)+","+((int)box.getCenter().y)+")");
		//		}
		boolean[] used = new boolean[boxes.size()];
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[0].length; j++) {
				ColorBox box = null;
				double x = xStart + j*averageBoxDistance;
				double y = yStart + i*averageBoxDistance;
				for (int k = 0; k < used.length; k++) {
					Point kCenter = boxes.get(k).getCenter();
					if(!used[k] && Geometry.distance(x, y, kCenter.x, kCenter.y) < distanceDeviation) {
						box = boxes.get(k);
						break;
					}
				}
				if(box == null) {
					box = new ColorBox(new Point[]{new Point(x-diff, y-diff), new Point(x+diff, y-diff),
							new Point(x+diff, y+diff), new Point(x-diff, y+diff)});
					boxes.add(box);
				}
				grid[i][j] = box;
			}
		}


		//		Statistics xStat = new Statistics(), yStat = new Statistics();
		//		for (int i = 0; i < boxes.size(); i++) {
		//			double smallX = boxes.get(i).getPerimeter();
		//			double smallY = boxes.get(i).getPerimeter();
		//			double measure = boxes.get(i).getPerimeter()/2;
		//			for (int j = i + 1; j < boxes.size(); j++) {
		//				double xValue = Math.abs(boxes.get(i).getCenter().x - boxes.get(i).getCenter().x);
		//				double yValue = Math.abs(boxes.get(i).getCenter().y - boxes.get(i).getCenter().y);
		//				if(xValue > measure && xValue < smallX)
		//					smallX = xValue;
		//				if(yValue > measure && yValue < smallY)
		//					smallY = yValue;
		//			}
		//			xStat.addValue(smallX);
		//			yStat.addValue(smallY);
		//		}
		//		System.out.println("X >> Mean: "+xStat.getMean()+" Standard Deviation: "+xStat.getStandardDeviation());
		//		System.out.println("Y >> Mean: "+yStat.getMean()+" Standard Deviation: "+yStat.getStandardDeviation());
	}

	private void obtainColors() {
		ArrayList<double[]> colorsTmp = new ArrayList<>();
		for (ColorBox box : boxes) {
			double d[] = BGR.get((int)box.getCenter().y, (int)box.getCenter().x);
			System.out.print("("+d[2]+","+d[1]+","+d[0]+")");
			colorsTmp.add(d);
		}
		System.out.println();
	}

	public ArrayList<ColorBox> getColorBoxes() {
		return boxes;
	}

	public Mat getBGR() {
		return BGR;
	}

	public Mat getBlackAndWhite() {
		return gray;
	}

	public Mat getLabColorSpace() {
		return LAB;
	}

	public Mat getMedianBlurred() {
		return MBlurred;
	}

}
