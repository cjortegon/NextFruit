package test.colorchecker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
	//	private SilhouetteDetector silhouette;

	public ColorChecker(String imagePath, int sensibility, int numberOfColors) {

		// Setting parameters
		this.numberOfColors = numberOfColors;

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
		System.out.println("Average distance: "+filterBoxes());
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
		Collections.sort(boxes);
	}

	private double filterBoxes() {

		// Getting statistics
		Statistics area = new Statistics();
		Statistics perimeter = new Statistics();
		for (int i = 0; i < boxes.size(); i++) {
			area.addValue(boxes.get(i).getArea());
			perimeter.addValue(boxes.get(i).getPerimeter());
		}
		System.out.println("Areas "+((int)area.getStandardDeviation()));
		System.out.println("Perimeter "+((int)perimeter.getStandardDeviation()));

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
					double levelDist = Math.sqrt(groups[i].get(j).getArea())*0.25;
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
		double smallest = Double.MAX_VALUE;
		double averageDistance = 0;
		for (int i = 0; i < deviations.length; i++) {
			if(groups[i].size() > 1 && deviations[i][1] < smallest) {
				averageDistance = deviations[i][0];
				smallest = deviations[i][1];
				index = i;
			}
		}
		//		System.out.println("Correlations: "+averageDistance+"//"+smallest);
		boxes.clear();
		boxes.addAll(groups[index]);
		return averageDistance-(smallest/1.75);
	}

	private void defineGrid() {
		Statistics xStat = new Statistics(), yStat = new Statistics();
		for (int i = 0; i < boxes.size(); i++) {
			double smallX = boxes.get(i).getPerimeter();
			double smallY = boxes.get(i).getPerimeter();
			double measure = boxes.get(i).getPerimeter()/2;
			for (int j = i + 1; j < boxes.size(); j++) {
				double xValue = Math.abs(boxes.get(i).getCenter().x - boxes.get(i).getCenter().x);
				double yValue = Math.abs(boxes.get(i).getCenter().y - boxes.get(i).getCenter().y);
				if(xValue > measure && xValue < smallX)
					smallX = xValue;
				if(yValue > measure && yValue < smallY)
					smallY = yValue;
			}
			xStat.addValue(smallX);
			yStat.addValue(smallY);
		}
		System.out.println("X >> Mean: "+xStat.getMean()+" Standard Deviation: "+xStat.getStandardDeviation());
		System.out.println("Y >> Mean: "+yStat.getMean()+" Standard Deviation: "+yStat.getStandardDeviation());
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
