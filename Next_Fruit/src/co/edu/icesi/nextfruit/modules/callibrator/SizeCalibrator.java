package co.edu.icesi.nextfruit.modules.callibrator;

import java.util.List;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import co.edu.icesi.nextfruit.util.Geometry;

/**
 * This class calibrates the sizes finding a relation between pixels and centimeters using a pre-defined figure (find it in documentation).
 * @author cjortegon
 */
public class SizeCalibrator {

	private Mat mat;
	private List<Point> points;
	private int rows;
	private int columns;
	private double intersectionsCentimeters, pixelsBetweenIntersections;

	public SizeCalibrator(String path, int rows, int columns) {
		mat = Imgcodecs.imread(path);
		this.rows = rows;
		this.columns = columns;
	}

	/**
	 * Finds the relation between pixels and centimeters.
	 * @param intersectionsSize size from each square in the grid of the pre-defined figure (find it in documentation).
	 */
	public void process(double intersectionsSize) {
		this.intersectionsCentimeters = intersectionsSize;
		Mat grayImage = new Mat();
		Imgproc.cvtColor(mat, grayImage, Imgproc.COLOR_BGR2GRAY);
		Size boardSize = new Size(columns, rows);
		MatOfPoint2f imageCorners = new MatOfPoint2f();
		boolean found = Calib3d.findChessboardCorners(grayImage, boardSize, imageCorners,
				Calib3d.CALIB_CB_ADAPTIVE_THRESH + Calib3d.CALIB_CB_NORMALIZE_IMAGE + Calib3d.CALIB_CB_FAST_CHECK);
		if (found) {
			TermCriteria term = new TermCriteria(TermCriteria.EPS | TermCriteria.MAX_ITER, 30, 0.1);
			Imgproc.cornerSubPix(grayImage, imageCorners, new Size(11, 11), new Size(-1, -1), term);
		} else {
		}

		points = imageCorners.toList();
		Point last = null;
		int count = 0;
		for (Point point : points) {
			if(count%columns != 0) {
				pixelsBetweenIntersections += Geometry.distance(last.x, last.y, point.x, point.y);
			}
			last = point;
			count ++;
		}
		pixelsBetweenIntersections /= (count - rows);
	}

	// ******************** GETTERS *********************

	public double getPixelsBetweenIntersections () {
		return pixelsBetweenIntersections;
	}

	public double getPixelsForCentimeter() {
		return pixelsBetweenIntersections / intersectionsCentimeters;
	}

	public Mat getImage() {
		return mat;
	}

	public List<Point> getPoints() {
		return points;
	}

	// ******************** GETTERS *********************

}
