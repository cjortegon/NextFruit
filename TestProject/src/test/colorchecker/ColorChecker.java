package test.colorchecker;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ColorChecker {

	private Mat BGR, lab, gray;
	private Point centers[];
	private ArrayList<double[]> colors;

	public ColorChecker(String imagePath) {
		
		// Reading images
		BGR = Imgcodecs.imread(imagePath);
		lab = new Mat();
		Imgproc.cvtColor(BGR, lab, Imgproc.COLOR_BGR2Lab);
		gray = Imgcodecs.imread(imagePath, Imgcodecs.IMREAD_GRAYSCALE);

		// Getting colors
		obtainCenters();
		obtainColors();
	}
	
	private void obtainCenters() {
		Mat thresh = new Mat();
		Imgproc.threshold(gray, thresh, 10, 255, 1);
		List<MatOfPoint> contours = new ArrayList<>();
		Imgproc.findContours(thresh, contours, gray, 1, 2);
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
			System.out.println("("+d[2]+","+d[1]+","+d[0]+")");
			colors.add(d);
		}
	}
	
	public Point[] getCenters() {
		return centers;
	}
	
	public Mat getOriginalImage() {
		return BGR;
	}

}
