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

import co.edu.icesi.frutificator.util.Statistics;
import test.silhouettedetector.SilhouetteDetector;

public class ColorChecker {

	private Mat BGR, LAB, gray, MBlurred, silhouette;
	private ColorBox grid[][];
	private ColorBox boxes[];
	private int numberOfColors;
	//	private SilhouetteDetector silhouette;

	public ColorChecker(String imagePath, int sensibility, int numberOfColors) {

		// Setting parameters
		this.numberOfColors = numberOfColors;

		// Reading image
		BGR = Imgcodecs.imread(imagePath);

		// Median blur
		MBlurred = new Mat();
		int ksize = (int) (Math.sqrt(BGR.width()*BGR.height())/100);
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
		obtainColors();
		defineGrid();
	}

	private void obtainBoxes(int sensibility, Mat mat) {
		Mat thresh = new Mat();
		Imgproc.threshold(mat, thresh, sensibility, 255, 1);
		List<MatOfPoint> contours = new ArrayList<>();
		Imgproc.findContours(thresh, contours, mat, 1, 2);
		System.out.println("Contours: "+contours.size());

		boxes = new ColorBox[contours.size()];
		int i = 0;
		for (MatOfPoint cnt : contours) {
			boxes[i++] = new ColorBox(cnt.toArray());
		}
		Arrays.sort(boxes);
	}

	private void filterBoxes() {

	}

	private void defineGrid() {
		Statistics xStat = new Statistics(), yStat = new Statistics();
		for (int i = 0; i < boxes.length; i++) {
			double smallX = boxes[i].getPerimeter();
			double smallY = boxes[i].getPerimeter();
			double measure = boxes[i].getPerimeter()/2;
			for (int j = i + 1; j < boxes.length; j++) {
				double xValue = Math.abs(boxes[i].getCenter().x - boxes[j].getCenter().x);
				double yValue = Math.abs(boxes[i].getCenter().y - boxes[j].getCenter().y);
				if(xValue > measure && xValue < smallX)
					smallX = xValue;
				if(yValue > measure && yValue < smallY)
					smallY = yValue;
			}
			System.out.println("small ->> ("+smallX+","+smallY+")");
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
	}

	public ColorBox[] getColorBoxes() {
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

}
