package test.colorchecker;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import test.silhouettedetector.SilhouetteDetector;

public class ColorChecker {

	private Mat BGR, LAB, BW;
	private Point centers[];
	private ArrayList<double[]> colors;
	private int numberOfColors;
	private SilhouetteDetector silohuette;

	public ColorChecker(String imagePath, int sensibility, int numberOfColors) {

		// Setting parameters
		this.numberOfColors = numberOfColors;

		// Reading images
		BGR = Imgcodecs.imread(imagePath);
		LAB = new Mat();
		Imgproc.cvtColor(BGR, LAB, Imgproc.COLOR_BGR2Lab);
		silohuette = new SilhouetteDetector(imagePath, 60, 255);
		BW = silohuette.getOriginalImage();
		//		BW = Imgcodecs.imread(imagePath, Imgcodecs.IMREAD_GRAYSCALE);

		// Getting colors
		obtainCenters(sensibility);
		obtainColors();

		silohuette = new SilhouetteDetector(imagePath, 60, 255);
	}

	private void obtainCenters(int sensibility) {
		Mat thresh = new Mat();
		Imgproc.threshold(BW, thresh, sensibility, 255, 1);
		List<MatOfPoint> contours = new ArrayList<>();
		Imgproc.findContours(thresh, contours, BW, 1, 2);
		System.out.println("Contours: "+contours.size());

		centers = new Point[contours.size()];
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
			centers[i++] = new Point(xSum, ySum);
		}
	}

	private void obtainColors() {
		colors = new ArrayList<>();
		for (Point point : centers) {
			double d[] = BGR.get((int)point.y, (int)point.x);
			System.out.print("("+d[2]+","+d[1]+","+d[0]+") // ");
			d = LAB.get((int)point.y, (int)point.x);
			System.out.println("("+d[2]+","+d[1]+","+d[0]+")");
			colors.add(d);
		}
	}

	public Point[] getCenters() {
		return centers;
	}

	public Mat getBGR() {
		return BGR;
	}

	public Mat getBlackAndWhite() {
		//		Imgproc.cvtColor(BGR, GRAY, Imgproc.COLOR_BGR2GRAY);
		BW = silohuette.getOriginalImage();
		return BW;
	}

	public Mat getLabColorSpace() {
		return LAB;
	}

}
