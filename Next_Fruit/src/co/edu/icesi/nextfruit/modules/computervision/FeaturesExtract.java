package co.edu.icesi.nextfruit.modules.computervision;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import co.edu.icesi.nextfruit.modules.model.CameraCalibration;
import co.edu.icesi.nextfruit.modules.model.ColorDistribution;
import co.edu.icesi.nextfruit.modules.model.MatchingColor;
import co.edu.icesi.nextfruit.modules.model.PolygonWrapper;

public class FeaturesExtract {

	private Mat mat;
	private PolygonWrapper polygon;
	private Histogram histogram;
	private int numberOfPixels;
	private Collection<ColorDistribution> colorStatistics;
	private Collection<ColorDistribution> matchingColors;

	public FeaturesExtract(String imagePath) {
		mat = Imgcodecs.imread(imagePath);
	}

	// ***************** PUBLIC METHODS *****************

	public void extractFeatures(CameraCalibration calibration) {
		mat = bilateralFilter(mat);
		polygon = getContours(mat.clone(), 150);
		histogram = new Histogram(mat);
		histogram.applyWhitePatch();
		colorStatistics = histogram.getStatisticalColors(polygon);
		for (ColorDistribution color : colorStatistics)
			numberOfPixels += color.getRepeat();
		System.out.println(colorStatistics.size()+" colors in this fruit for "+numberOfPixels+" pixels.");
	}

	public boolean hasExtractedFeatures() {
		return colorStatistics != null;
	}

	public void analizeData(CameraCalibration calibration, List<MatchingColor> colors) {
		matchingColors = colorMatching(colors, calibration);
	}

	// ***************** PUBLIC METHODS *****************

	// ******************** FILTERS *********************

	private Mat bilateralFilter(Mat src) {
		Mat dst = Mat.zeros(src.width(), src.height(), CvType.CV_32F);
		double sigmaColor = Math.sqrt(src.width()*src.height())/100;
		Imgproc.bilateralFilter(src, dst, -1, sigmaColor, 3);
		return dst;
	}

	private Mat medianBlur(Mat mat) {
		Mat MBlurred = new Mat();
		int ksize = (int) (Math.sqrt(mat.width()*mat.height())/60);
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

	// ******************** FILTERS *********************

	// **************** PRIVATE METHODS *****************

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

	// ******************** GETTERS *********************

}
