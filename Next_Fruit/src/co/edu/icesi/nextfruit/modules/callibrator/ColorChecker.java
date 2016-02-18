package co.edu.icesi.nextfruit.modules.callibrator;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import co.edu.icesi.nextfruit.util.ColorConverter;
import co.edu.icesi.nextfruit.util.Geometry;
import co.edu.icesi.nextfruit.util.Statistics;

public class ColorChecker {

	private Mat BGR, LAB, gray, MBlurred, silhouette;
	private ColorBox[][] grid;
	private double[][][] originalsRGB, originalsLAB, readRGB, readLAB;
	private ArrayList<ColorBox> boxes;
	private double averageBoxArea, averageBoxDistance, distanceDeviation;
	private int sensibility;
	
	//Color spaces coordinates Arrays
	private int[][][] rgbs;
	private int[][][] labs;
	

	public ColorChecker(String imagePath, int sensibility, double[][][] originalsRGB) {
		this.originalsRGB = originalsRGB;
		this.sensibility = sensibility;
		this.BGR = Imgcodecs.imread(imagePath);
	}

	public void process(File conversionMatrix) {

		//Initialize color space coordinates arrays
		rgbs = new int[4][6][3];
		labs = new int [4][6][3];
		
		// Setting parameters
		grid = new ColorBox[originalsRGB.length][originalsRGB[0].length];
		readLAB = new double[grid.length][grid[0].length][3];
		readRGB = new double[grid.length][grid[0].length][3];

		// Converting originals to LAB
		originalsLAB = new double[originalsRGB.length][originalsRGB[0].length][3];
		for (int i = 0; i < originalsLAB.length; i++) {
			for (int j = 0; j < originalsLAB[0].length; j++) {
				
				//Fill RGB values per color box
				int[] rgb = {(int) originalsRGB[i][j][0], (int) originalsRGB[i][j][1], (int) originalsRGB[i][j][2]};
				rgbs[i][j] = rgb;
				
				
				for (int k = 0; k < 3; k++)
					originalsLAB[i][j][k] = originalsRGB[i][j][k]/255;
				originalsLAB[i][j] = ColorConverter.rgb2xyz(originalsLAB[i][j]);
				originalsLAB[i][j] = ColorConverter.xyz2lab(originalsLAB[i][j]);
				
				//Fill Lab values per color box
				int[] lab = {(int) originalsLAB[i][j][0], (int) originalsLAB[i][j][1], (int) originalsLAB[i][j][2]};
				labs[i][j] = lab;
				
				
			}
		}

		// Median blur
		MBlurred = new Mat();
		int ksize = (int) (Math.sqrt(BGR.width()*BGR.height())/60);
		if(ksize %2 == 0)
			ksize ++;
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
		obtainColors();
		defineGrid();

	}

	private void obtainBoxes(int sensibility, Mat mat) {
		Mat thresh = new Mat();
		Imgproc.threshold(mat, thresh, sensibility, 255, 1);
		List<MatOfPoint> contours = new ArrayList<>();
		Imgproc.findContours(thresh, contours, mat, 1, 2);

		boxes = new ArrayList<ColorBox>();
		int i = 0;
		for (MatOfPoint cnt : contours) {
			boxes.add(new ColorBox(cnt.toArray()));
		}
	}

	private void filterBoxes() {

		// Getting statistics
		Statistics area = new Statistics();
		Statistics perimeter = new Statistics();
		for (int i = 0; i < boxes.size(); i++) {
			area.addValue(boxes.get(i).getArea());
			perimeter.addValue(boxes.get(i).getPerimeter());
		}

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
					for (int k = j+1; k < groups[i].size(); k++) {
						Point a = groups[i].get(j).getCenter();
						Point b = groups[i].get(k).getCenter();
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
				
				
				//Setting RGB and Lab color coordinates of the color box
				grid[i][j].setRGBColor(rgbs[i][j]);
				grid[i][j].setLabColor(labs[i][j]);
				
				
				readRGB[i][j] = box.getAverageRGB(BGR);
				double rgb[] = new double[] {readRGB[i][j][0]/255, readRGB[i][j][1]/255, readRGB[i][j][2]/255};
				readLAB[i][j] = ColorConverter.rgb2xyz(rgb);
				readLAB[i][j] = ColorConverter.xyz2lab(readLAB[i][j]);
			}
		}
		Collections.sort(boxes);
	}

	private void obtainColors() {
		ArrayList<double[]> colorsTmp = new ArrayList<>();
		for (ColorBox box : boxes) {
			double d[] = BGR.get((int)box.getCenter().y, (int)box.getCenter().x);
			colorsTmp.add(d);
		}
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
	
	
	public ColorBox[][] getGrid() {
		return grid;
	}

}
