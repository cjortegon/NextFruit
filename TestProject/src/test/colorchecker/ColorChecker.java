package test.colorchecker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import co.edu.icesi.frutificator.util.Statistics;
import test.silhouettedetector.SilhouetteDetector;

public class ColorChecker {

	private Mat BGR, LAB, BW;
	private ColorBox grid[][];
	private ColorBox boxes[];
	private int numberOfColors;
	private SilhouetteDetector silohuette;

	public ColorChecker(String imagePath, int sensibility, int numberOfColors) {

		// Setting parameters
		this.numberOfColors = numberOfColors;

		// Reading images
		BGR = Imgcodecs.imread(imagePath);
		LAB = new Mat();
		Imgproc.cvtColor(BGR, LAB, Imgproc.COLOR_BGR2Lab);
		silohuette = new SilhouetteDetector(imagePath, sensibility, 255);
		BW = silohuette.getProcessedImage();
		//		BW = Imgcodecs.imread(imagePath, Imgcodecs.IMREAD_GRAYSCALE);

		// Getting colors
		obtainCenters(sensibility, BW.clone());
		obtainColors();
		defineGrid();
	}

	private void obtainCenters(int sensibility, Mat mat) {
		Mat thresh = new Mat();
		Imgproc.threshold(mat, thresh, sensibility, 255, 1);
		List<MatOfPoint> contours = new ArrayList<>();
		Imgproc.findContours(thresh, contours, mat, 1, 2);
		System.out.println("Contours: "+contours.size());

		boxes = new ColorBox[contours.size()];
		int i = 0;
		for (MatOfPoint cnt : contours) {
			Point[] points = cnt.toArray();
			double xSum = 0;
			double ySum = 0;
			for (Point point : points) {
				xSum += point.x;
				ySum += point.y;
			}
			xSum /= points.length;
			ySum /= points.length;
			boxes[i++] = new ColorBox(points);
		}
		Arrays.sort(boxes);
	}
	
	private void defineGrid() {
		Statistics xStat = new Statistics(), yStat = new Statistics();
		for (int i = 0; i < boxes.length; i++) {
			double smallX = boxes[i].getBoxEdgeLength();
			double smallY = boxes[i].getBoxEdgeLength();
			double measure = boxes[i].getBoxEdgeLength()/2;
			for (int j = i + 1; j < boxes.length; j++) {
				double xValue = Math.abs(boxes[i].getCenter().x - boxes[j].getCenter().x);
				double yValue = Math.abs(boxes[i].getCenter().y - boxes[j].getCenter().y);
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
			System.out.print("("+d[2]+","+d[1]+","+d[0]+") // ");
			d = LAB.get((int)box.getCenter().y, (int)box.getCenter().x);
			System.out.println("("+d[2]+","+d[1]+","+d[0]+")");
			colorsTmp.add(d);
		}
	}

	public ColorBox[] getColorBoxes() {
		return boxes;
	}

	public Mat getBGR() {
		return BGR;
	}

	public Mat getBlackAndWhite() {
		return BW;
	}

	public Mat getLabColorSpace() {
		return LAB;
	}

}
