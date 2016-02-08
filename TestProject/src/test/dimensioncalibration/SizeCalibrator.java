package test.dimensioncalibration;

import java.util.List;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class SizeCalibrator {

	private Mat mat;
	private List<Point> points;
	private int numCornersHor;
	private int numCornersVer;

	public SizeCalibrator(String path, int rows, int columns) {
		mat = Imgcodecs.imread(path);
		numCornersVer = columns;
		numCornersHor = rows;
		findAndDrawPoints();
	}

	private void findAndDrawPoints() {
		Mat grayImage = new Mat();
		Imgproc.cvtColor(mat, grayImage, Imgproc.COLOR_BGR2GRAY);
		Size boardSize = new Size(this.numCornersHor, this.numCornersVer);
		MatOfPoint2f imageCorners = new MatOfPoint2f();
		boolean found = Calib3d.findChessboardCorners(grayImage, boardSize, imageCorners,
				Calib3d.CALIB_CB_ADAPTIVE_THRESH + Calib3d.CALIB_CB_NORMALIZE_IMAGE + Calib3d.CALIB_CB_FAST_CHECK);
		if (found) {
			System.out.println("Se encontro un patron");
			TermCriteria term = new TermCriteria(TermCriteria.EPS | TermCriteria.MAX_ITER, 30, 0.1);
			Imgproc.cornerSubPix(grayImage, imageCorners, new Size(11, 11), new Size(-1, -1), term);
			Calib3d.drawChessboardCorners(mat, boardSize, imageCorners, found);
		} else {
			System.out.println("No se encontro un patron");
		}
		
		points = imageCorners.toList();
//		for (Point point : points) {
//			System.out.println(point.x+","+point.y);
//		}
	}

	public Mat getImage() {
		return mat;
	}
	
	public List<Point> getPoints() {
		return points;
	}

}
