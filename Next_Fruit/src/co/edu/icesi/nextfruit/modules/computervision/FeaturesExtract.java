package co.edu.icesi.nextfruit.modules.computervision;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import co.edu.icesi.nextfruit.modules.model.CameraCalibration;

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
	private Histogram histogram;
	
	public FeaturesExtract(String imagePath) {
		mat = Imgcodecs.imread(imagePath);
	}
	
	public void extractFeatures(CameraCalibration calibration) {
		
	}
	
	// ***************** ACCESS METHODS *****************

	public Mat getMat() {
		return mat;
	}

	public Histogram getHistogram() {
		return histogram;
	}
	
	// ***************** ACCESS METHODS *****************
	
}
