package co.edu.icesi.nextfruit.modules.computervision;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import co.edu.icesi.nextfruit.modules.model.CameraCalibration;
import co.edu.icesi.nextfruit.modules.model.ColorDistribution;
import co.edu.icesi.nextfruit.modules.model.MatchingColor;
import co.edu.icesi.nextfruit.modules.model.PolygonWrapper;
import co.edu.icesi.nextfruit.util.Statistics;

public class FeaturesExtract {

	private Mat mat, CV_8UC1;
	private PolygonWrapper polygon;
	private Histogram histogram;
	private int numberOfPixels;
	private double entropy;
	private Collection<ColorDistribution> colorStatistics;
	private Collection<ColorDistribution> matchingColors;
	private Statistics luminanceStatistics;

	public FeaturesExtract(String imagePath) {
		mat = Imgcodecs.imread(imagePath);
		CV_8UC1 = Imgcodecs.imread(imagePath, CvType.CV_8UC1);
		entropy = -1;
	}

	// ***************** PUBLIC METHODS *****************

	public void extractFeatures(CameraCalibration calibration) {
		//		polygon = getContours(mat.clone());
		polygon = getContours2();
		histogram = new Histogram(mat);
		histogram.applyWhitePatch();
		colorStatistics = histogram.getStatisticalColors(polygon);
		for (ColorDistribution color : colorStatistics)
			numberOfPixels += color.getRepeat();
	}

	public boolean hasExtractedFeatures() {
		return colorStatistics != null;
	}

	public void processLuminanceAnalysis() {
		luminanceStatistics = luminanceAnalysis();
	}

	public void processColorAnalysis(CameraCalibration calibration, List<MatchingColor> colors) {
		matchingColors = colorMatching(colors, calibration);
	}

	public void analizeData(CameraCalibration calibration, List<MatchingColor> colors) {
		if(colors != null) {
			processColorAnalysis(calibration, colors);
		}
		processLuminanceAnalysis();
	}

	// ***************** PUBLIC METHODS *****************

	// **************** PRIVATE METHODS *****************

	private PolygonWrapper getContours(Mat src) {

		// Median blur
		Mat mBlurred = new Mat();
		int ksize = (int) (Math.sqrt(src.width()*src.height())/60);
		if(ksize %2 == 0)
			ksize ++;
		Imgproc.medianBlur(src, mBlurred, ksize);

		// Threshold
		Mat thresh = new Mat();
		Imgproc.threshold(mBlurred, thresh, 100, 255, CvType.CV_8UC1);

		// Gray scale
		Mat src_gray = new Mat();
		Imgproc.cvtColor(thresh, src_gray, Imgproc.COLOR_BGR2GRAY);

		// Cany
		Mat canny = new Mat();
		Imgproc.Canny(src_gray, canny, 0, 15);

		// Kernel
		Mat kernel;

		// Close
		kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3, 3));
		Mat close = new Mat();
		Imgproc.morphologyEx(canny, close, Imgproc.MORPH_CLOSE, kernel);

		// Dilatation
		kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2, 2));
		Mat dilatation = new Mat();
		Imgproc.dilate(close, dilatation, kernel);

		// Open
		kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(2, 2));
		Mat open = new Mat();
		Imgproc.morphologyEx(dilatation, open, Imgproc.MORPH_OPEN, kernel);

		// Close
		kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(100, 100));
		Mat close2 = new Mat();
		Imgproc.morphologyEx(open, close2, Imgproc.MORPH_CLOSE, kernel);

		// Getting contours
		List<MatOfPoint> contours = new ArrayList<>();
		Imgproc.findContours(close2, contours, close2, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

		// Selecting the polygon
		ArrayList<PolygonWrapper> boxes = new ArrayList<>();
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

	private PolygonWrapper getContours2() {

		// Threshold
		Mat thresh = new Mat();
		int block = 11;
		int C = 7;
		Imgproc.adaptiveThreshold(CV_8UC1, thresh, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, block, C);

		// Cany
		Mat canny = new Mat();
		Imgproc.Canny(thresh, canny, 0, 15);

		// Kernel
		Mat kernel;

		// Close
		kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(4, 4));
		Mat close = new Mat();
		Imgproc.morphologyEx(canny, close, Imgproc.MORPH_CLOSE, kernel);

		// Open
		kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5));
		Mat open = new Mat();
		Imgproc.morphologyEx(close, open, Imgproc.MORPH_OPEN, kernel);

		// Dilatation
		kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
		Mat dilatation = new Mat();
		Imgproc.dilate(open, dilatation, kernel);

		// Open
		kernel = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, new Size(8, 8));
		Mat open2 = new Mat();
		Imgproc.morphologyEx(dilatation, open2, Imgproc.MORPH_OPEN, kernel);

		// Black
		kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(30, 30));
		Mat black = new Mat();
		Imgproc.morphologyEx(open2, black, Imgproc.MORPH_BLACKHAT, kernel);

		// Close
		kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(120, 120));
		Mat close3 = new Mat();
		Imgproc.morphologyEx(open2, close3, Imgproc.MORPH_CLOSE, kernel);

		// Getting contours
		List<MatOfPoint> contours = new ArrayList<>();
		Imgproc.findContours(close3, contours, close3, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

		// Selecting the polygon
		ArrayList<PolygonWrapper> boxes = new ArrayList<>();
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
		CV_8UC1 = null;
		return biggest;
	}

	private List<MatOfPoint> findContours(Mat mat) {
		List<MatOfPoint> contours = new ArrayList<>();
		Mat hierarchy = new Mat();
		Imgproc.findContours( mat, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_TC89_L1);
		return contours;
	}

	private List<ColorDistribution> colorMatching(List<MatchingColor> colors, CameraCalibration calibration) {
		ArrayList<ColorDistribution> newColors = new ArrayList<>();
		for (MatchingColor color : colors) {
			color.restartRepeatCount();
			for (ColorDistribution c : colorStatistics) {
				c.transform2xyY(calibration);
				color.increaseIfClose(c.getxyY(), c.getRepeat());
			}
			newColors.add(color);
		}
		return newColors;
	}

	private Statistics luminanceAnalysis() {
		Statistics stat = new Statistics();
		histogram.generateEmptyLuminanceHistogram(256);
		for (ColorDistribution c : colorStatistics) {
			double luminance = Math.max(0, Math.min(c.getxyY()[2], 1));
			histogram.increaseLuminancePosition(luminance, true);
			stat.addValue(c.getxyY()[2]);
		}
		histogram.generateRangesFromStatistics(16);
		return stat;
	}

	private void obtainEntropy() {
		if(entropy < 0) {
			Histogram histogram = new Histogram(mat);
			final double total_size = polygon.getArea();
			histogram.generateGrayscaleHistogram(false, polygon);
			final double log2 = Math.log(2);
			entropy = 0;
			for (int i = 0; i < histogram.getHistogram().length; i++) {
				double p = histogram.getHistogram()[i]/total_size;
				if(p > 0)
					entropy -= (p*(Math.log(p)/log2));
			}
		}
	}

	// **************** PRIVATE METHODS *****************

	// ******************** GETTERS *********************

	public Mat getMat() {
		return mat;
	}

	public PolygonWrapper getPolygon() {
		return polygon;
	}

	public Collection<ColorDistribution> getColorStatistics() {
		return colorStatistics;
	}

	public Collection<ColorDistribution> getMatchingColors() {
		return matchingColors;
	}

	public int getNumberOfPixels() {
		return numberOfPixels;
	}

	public Histogram getHistogram() {
		return histogram;
	}

	public Statistics getLuminanceStatistics() {
		return luminanceStatistics;
	}

	public double getEntropy() {
		obtainEntropy();
		return entropy;
	}

	// ******************** GETTERS *********************

}
