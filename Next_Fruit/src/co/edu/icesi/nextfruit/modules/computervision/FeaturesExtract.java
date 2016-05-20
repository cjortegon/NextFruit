package co.edu.icesi.nextfruit.modules.computervision;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import co.edu.icesi.nextfruit.modules.model.CameraCalibration;
import co.edu.icesi.nextfruit.modules.model.ColorDistribution;
import co.edu.icesi.nextfruit.modules.model.MatchingColor;
import co.edu.icesi.nextfruit.modules.model.PolygonWrapper;
import co.edu.icesi.nextfruit.util.Statistics;

public class FeaturesExtract {

	private Mat mat;
	private Mat CV_8UC1;
	private PolygonWrapper polygon;
	private Histogram histogram;
	private int numberOfPixels;
	private double entropy;
	private Collection<ColorDistribution> colorStatistics;
	private Collection<ColorDistribution> matchingColors;
	private Statistics luminanceStatistics;

	public FeaturesExtract(String imagePath) {
		mat = Imgcodecs.imread(imagePath);
		entropy = -1;
	}

	// ***************** PUBLIC METHODS *****************

	public void extractFeatures(CameraCalibration calibration) {
		//		polygon = getContours1(mat.clone(), calibration);
		//		polygon = getContours2(calibration);
		//		polygon = getContours3(calibration);
		polygon = getContours(calibration);
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

//	/**
//	 * @deprecated
//	 */
//	private PolygonWrapper getContours1(Mat src, CameraCalibration calibration) {
//
//		// Median blur
//		Mat mBlurred = new Mat();
//		int ksize = (int) (Math.sqrt(src.width()*src.height())/60);
//		if(ksize %2 == 0)
//			ksize ++;
//		Imgproc.medianBlur(src, mBlurred, ksize);
//
//		// Threshold
//		Mat thresh = new Mat();
//		Imgproc.threshold(mBlurred, thresh, 100, 255, CvType.CV_8UC1);
//
//		// Gray scale
//		Mat src_gray = new Mat();
//		Imgproc.cvtColor(thresh, src_gray, Imgproc.COLOR_BGR2GRAY);
//
//		// Cany
//		Mat canny = new Mat();
//		Imgproc.Canny(src_gray, canny, 0, 15);
//
//		// Kernel
//		Mat kernel;
//
//		// Close
//		kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3, 3));
//		Mat close = new Mat();
//		Imgproc.morphologyEx(canny, close, Imgproc.MORPH_CLOSE, kernel);
//
//		// Dilatation
//		kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2, 2));
//		Mat dilatation = new Mat();
//		Imgproc.dilate(close, dilatation, kernel);
//
//		// Open
//		kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(2, 2));
//		Mat open = new Mat();
//		Imgproc.morphologyEx(dilatation, open, Imgproc.MORPH_OPEN, kernel);
//
//		// Close
//		kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(100, 100));
//		Mat close2 = new Mat();
//		Imgproc.morphologyEx(open, close2, Imgproc.MORPH_CLOSE, kernel);
//
//		// Getting contours
//		List<MatOfPoint> contours = new ArrayList<>();
//		Imgproc.findContours(close2, contours, close2, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
//
//		// Selecting the polygon
//		ArrayList<PolygonWrapper> boxes = new ArrayList<>();
//		PolygonWrapper biggest = null;
//		double biggestArea = 0;
//		for (MatOfPoint cnt : contours) {
//			PolygonWrapper polygon = new PolygonWrapper(cnt.toArray(), false, calibration);
//			boxes.add(polygon);
//			if(polygon.getArea() > biggestArea) {
//				biggest = polygon;
//				biggestArea = polygon.getArea();
//			}
//		}
//
//		return biggest;
//	}
//
//	/**
//	 * @deprecated
//	 */
//	private PolygonWrapper getContours2(CameraCalibration calibration) {
//
//		// Threshold
//		Mat thresh = new Mat();
//		int block = 11;
//		int C = 7;
//		Imgproc.adaptiveThreshold(CV_8UC1, thresh, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, block, C);
//
//		// Cany
//		Mat canny = new Mat();
//		Imgproc.Canny(thresh, canny, 0, 15);
//
//		// Kernel
//		Mat kernel;
//
//		// Close
//		kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(4, 4));
//		Mat close = new Mat();
//		Imgproc.morphologyEx(canny, close, Imgproc.MORPH_CLOSE, kernel);
//
//		// Open
//		kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5));
//		Mat open = new Mat();
//		Imgproc.morphologyEx(close, open, Imgproc.MORPH_OPEN, kernel);
//
//		// Dilatation
//		kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
//		Mat dilatation = new Mat();
//		Imgproc.dilate(open, dilatation, kernel);
//
//		// Open
//		kernel = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, new Size(8, 8));
//		Mat open2 = new Mat();
//		Imgproc.morphologyEx(dilatation, open2, Imgproc.MORPH_OPEN, kernel);
//
//		// Black
//		kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(30, 30));
//		Mat black = new Mat();
//		Imgproc.morphologyEx(open2, black, Imgproc.MORPH_BLACKHAT, kernel);
//
//		// Close
//		kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(120, 120));
//		Mat close3 = new Mat();
//		Imgproc.morphologyEx(open2, close3, Imgproc.MORPH_CLOSE, kernel);
//
//		// Getting contours
//		List<MatOfPoint> contours = new ArrayList<>();
//		Imgproc.findContours(close3, contours, close3, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
//
//		// Selecting the polygon
//		ArrayList<PolygonWrapper> boxes = new ArrayList<>();
//		PolygonWrapper biggest = null;
//		double biggestArea = 0;
//		for (MatOfPoint cnt : contours) {
//			PolygonWrapper polygon = new PolygonWrapper(cnt.toArray(), false, calibration);
//			boxes.add(polygon);
//			if(polygon.getArea() > biggestArea) {
//				biggest = polygon;
//				biggestArea = polygon.getArea();
//			}
//		}
//		CV_8UC1 = null;
//		return biggest;
//	}
//
//	/**
//	 * @deprecated
//	 */
//	private PolygonWrapper getContours3(CameraCalibration calibration) {
//		int ksize = 0;
//		int BORDER_DEFAULT = 4;
//		Mat kernel;
//
//		ksize = (int) (Math.sqrt(mat.width()*mat.height())/30);
//		if(ksize %2 == 0)
//			ksize ++;
//		Mat blur = new Mat();
//		Imgproc.GaussianBlur(mat, blur, new Size(ksize, ksize), BORDER_DEFAULT);
//
//		// Sobel
//		Mat sobel = Mat.zeros(mat.width(), mat.height(), CvType.CV_32F);
//		int ddepth = -1;
//		double delta = 0;
//		ksize = 7;
//		int scale = 1;
//		Imgproc.Sobel(blur, sobel, ddepth, 1, 1, ksize, scale, delta);
//
//		// Blur a sobel
//		ksize = (int) (Math.sqrt(mat.width()*mat.height())/15);
//		if(ksize %2 == 0)
//			ksize ++;
//		Mat blur2 = new Mat();
//		Imgproc.GaussianBlur(sobel, blur2, new Size(ksize, ksize), BORDER_DEFAULT);
//
//		// Median blur
//		Mat MBlurred = new Mat();
//		ksize = 25;
//		if(ksize %2 == 0)
//			ksize ++;
//		Imgproc.medianBlur(blur2, MBlurred, ksize);
//
//		// Cany
//		Mat canny = new Mat();
//		Mat gray = MBlurred;
//		Imgproc.Canny(gray, canny, 0, 60);
//
//		// Dilatation
//		kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
//		Mat dilatation = new Mat();
//		Imgproc.dilate(canny, dilatation, kernel);
//
//		// Black
//		kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(20, 20));
//		Mat black = new Mat();
//		Imgproc.morphologyEx(dilatation, black, Imgproc.MORPH_BLACKHAT, kernel);
//
//		// Close
//		kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(40, 40));
//		Mat close = new Mat();
//		Imgproc.morphologyEx(black, close, Imgproc.MORPH_CLOSE, kernel);
//
//		// Getting contours
//		List<MatOfPoint> contours = new ArrayList<>();
//		Imgproc.findContours(close, contours, close, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
//		//		System.out.println("Number of contours: "+contours.size());
//
//		// Selecting the polygon
//		ArrayList<PolygonWrapper> boxes = new ArrayList<>();
//		PolygonWrapper biggest = null;
//		double biggestArea = 0;
//		for (MatOfPoint cnt : contours) {
//			PolygonWrapper polygon = new PolygonWrapper(cnt.toArray(), false, null);
//			boxes.add(polygon);
//			if(polygon.getArea() > biggestArea) {
//				biggest = polygon;
//				biggestArea = polygon.getArea();
//			}
//		}
//		return biggest;
//	}

	/**
	 * Method to identity the position of the fruit.
	 * @param calibration CameraCalibration object is used to convert from px to cm.
	 * @return
	 */
	private PolygonWrapper getContours(CameraCalibration calibration) {
		int ksize = 0;
		Mat kernel;

		// Contrast
		double contrast = 0.0625;
		Mat contrasted = Mat.zeros(mat.height(), mat.width(), mat.type());
		for (int i = 0; i < mat.width(); i++) {
			for (int j = 0; j < mat.height(); j++) {
				double[] bgr = mat.get(j, i);
				double R = (int)(((((bgr[0] / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
				if(R < 0) { R = 0; }
				else if(R > 255) { R = 255; }
				double G = (int)(((((bgr[1] / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
				if(G < 0) { G = 0; }
				else if(G > 255) { G = 255; }
				double B = (int)(((((bgr[2] / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
				if(B < 0) { B = 0; }
				else if(B > 255) { B = 255; }
				contrasted.put(j, i, new double[]{B, G, R});
			}
		}

		// Sobel
		Mat sobel = Mat.zeros(mat.width(), mat.height(), CvType.CV_32F);
		int ddepth = -1;
		double delta = 0;
		ksize = 5;
		int scale = 3;
		Imgproc.Sobel(contrasted, sobel, ddepth, 1, 1, ksize, scale, delta);

		Histogram histogram = new Histogram(sobel);
		histogram.filterFigureByGrayProfile(156, 100, null, new double[]{0,0,0});
		sobel = histogram.getImage();

		// Cany
		Mat canny = new Mat();
		Mat gray = sobel;
		Imgproc.Canny(gray, canny, 0, 100);

		// Dilatation
		kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(6, 6));
		Mat dilatation = new Mat();
		Imgproc.dilate(canny, dilatation, kernel);

		// Close
		kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(100, 100));
		Mat close = new Mat();
		Imgproc.morphologyEx(dilatation, close, Imgproc.MORPH_CLOSE, kernel);

		// Fill spaces
		List<MatOfPoint> spaces = new ArrayList<>();
		Mat clone = close.clone();
		Mat fill = close.clone();
		Imgproc.findContours(clone, spaces, clone, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		for (MatOfPoint cnt : spaces) {
			PolygonWrapper polygon = new PolygonWrapper(cnt.toArray(), false, null);
			Iterator<Point> it = polygon.getIterator();
			while(it.hasNext()) {
				Point p = it.next();
				fill.put((int)p.y, (int)p.x, new double[]{255, 255, 255, 255});
			}
		}

		// Open
		kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(30, 30));
		Mat open2 = new Mat();
		Imgproc.morphologyEx(fill, open2, Imgproc.MORPH_OPEN, kernel);

		// Getting contours
		List<MatOfPoint> contours = new ArrayList<>();
		Imgproc.findContours(open2, contours, open2, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

		// Selecting the polygon
		ArrayList<PolygonWrapper> boxes = new ArrayList<>();
		PolygonWrapper biggest = null;
		double biggestArea = 0;
		for (MatOfPoint cnt : contours) {
			PolygonWrapper polygon = new PolygonWrapper(cnt.toArray(), false, calibration);
			boxes.add(polygon);
			if(polygon.getArea() > biggestArea) {
				biggest = polygon;
				biggestArea = polygon.getArea();
			}
		}
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
