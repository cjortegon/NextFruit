package co.edu.icesi.nextfruit.modules.computervision;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import co.edu.icesi.nextfruit.modules.model.CameraCalibration;
import co.edu.icesi.nextfruit.modules.model.PolygonWrapper;

public class FeaturesExtract {

	//	private double[][] threshold;

	//	public FeaturesExtract(String backgroundPath) {
	//		Histogram bgHistogram = new Histogram(backgroundPath);
	//		bgHistogram.convertToCromaticScale();
	//		bgHistogram.applyWhitePatch();
	//		threshold = bgHistogram.obtainThreshold();
	//		System.out.println("Background: ("+threshold[0][0]+","+threshold[0][1]+","+threshold[0][2]+")");
	//		System.out.println("Range: ("+threshold[1][0]+","+threshold[1][1]+","+threshold[1][2]+")");
	//	}
	//	
	//	public Mat removeBackground(Mat mat) {
	//		
	//		Histogram histogram = new Histogram(mat);
	//		histogram.convertToCromaticScale();
	//		histogram.applyWhitePatch();
	//		histogram.filterFigureByColorProfile(threshold[0], threshold[1], new double[]{255, 255, 255}, null);
	//		
	//		return histogram.getImage();
	//	}

	private Mat mat;
	private PolygonWrapper polygon;
	private Histogram histogram;
	private Map<Integer, Integer> colorStatistics;

	public FeaturesExtract(String imagePath) {
		mat = Imgcodecs.imread(imagePath);
	}

	public void extractFeatures(CameraCalibration calibration) {
		mat = bilateralFilter(mat);
		polygon = getContours(mat.clone(), 150);
		histogram = new Histogram(mat);
		//		long time = System.currentTimeMillis();
		//		System.out.println("Start getStatisticalColors()");
		colorStatistics = histogram.getStatisticalColors(polygon);
		//		System.out.println("Number of colors: "+map.keySet().size());
		//		System.out.println("Time to get all colors: "+(System.currentTimeMillis() - time));

		// Printing all colors
		//		for (Integer color : colorStatistics.keySet()) {
		//			int b = color & (255);
		//			int g = (color & (255 << 8)) >> 8;
		//			int r = (color & (255 << 16)) >> 16;
		//			System.out.println(color+" -> ("+r+","+g+","+b+")");
		//		}
		System.out.println(colorStatistics.keySet().size()+" colors in this fruit!");
	}

	// ******************** FILTERS *********************

	private Mat bilateralFilter(Mat src) {
		Mat dst = Mat.zeros(src.width(), src.height(), CvType.CV_32F);
		double sigmaColor = Math.sqrt(src.width()*src.height())/100;
		//		System.out.println("Using sigmaColor = "+sigmaColor);
		//		long time = System.currentTimeMillis();
		Imgproc.bilateralFilter(src, dst, -1, sigmaColor, 3);
		//		System.out.println("Processing time: "+(System.currentTimeMillis() - time));
		return dst;
	}

	private Mat medianBlur(Mat mat) {
		Mat MBlurred = new Mat();
		int ksize = (int) (Math.sqrt(mat.width()*mat.height())/60);
		//		System.out.println("ksize = "+ksize);
		if(ksize %2 == 0)
			ksize ++;
		Imgproc.medianBlur(mat, MBlurred, ksize);
		return MBlurred;
	}

	private Mat sobelFilter(Mat src) {
		Mat dst = Mat.zeros(src.width(), src.height(), CvType.CV_32F);
		int ddepth = -1;
		double delta = 0;
		int ksize = 5;
		int scale = 2;
		Imgproc.Sobel(src, dst, ddepth, 1, 1, ksize, scale, delta);
		return dst;
	}

	private PolygonWrapper getContours(Mat mat, int sensibility) {

		Mat gray = mat.clone();
		Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY, 1);

		Mat thresh = new Mat();
		Imgproc.threshold(gray, thresh, sensibility, 255, 1);
		List<MatOfPoint> contours = new ArrayList<>();
		Imgproc.findContours(thresh, contours, gray, 1, 2);

		ArrayList<PolygonWrapper> boxes = new ArrayList<PolygonWrapper>();
		PolygonWrapper biggest = null;
		double biggestArea = 0;
		for (MatOfPoint cnt : contours) {
			PolygonWrapper polygon = new PolygonWrapper(cnt.toArray(), false);
			boxes.add(polygon);
			if(polygon.getArea() > biggestArea) {
				biggest = polygon;
				biggestArea = polygon.getArea();
			}
		}

		return biggest;
	}

	// ******************** FILTERS *********************

	// ***************** ACCESS METHODS *****************

	public Mat getMat() {
		return mat;
	}

	public PolygonWrapper getPolygon() {
		return polygon;
	}

	public Histogram getHistogram() {
		return histogram;
	}

	public Map<Integer, Integer> getColorStatistics() {
		return colorStatistics;
	}

	// ***************** ACCESS METHODS *****************

}
