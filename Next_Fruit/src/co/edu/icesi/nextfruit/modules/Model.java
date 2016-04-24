package co.edu.icesi.nextfruit.modules;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.opencv.core.Core;

import co.edu.icesi.nextfruit.modules.callibrator.ColorChecker;
import co.edu.icesi.nextfruit.modules.callibrator.SizeCalibrator;
import co.edu.icesi.nextfruit.modules.computervision.FeaturesExtract;
import co.edu.icesi.nextfruit.modules.machinelearning.ClassClassifier;
import co.edu.icesi.nextfruit.modules.machinelearning.QualityClassifier;
import co.edu.icesi.nextfruit.modules.machinelearning.RipenessClassifier;
import co.edu.icesi.nextfruit.modules.machinelearning.SizeClassifier;
import co.edu.icesi.nextfruit.modules.machinelearning.WekaClassifier;
import co.edu.icesi.nextfruit.modules.model.CameraCalibration;
import co.edu.icesi.nextfruit.modules.model.CameraSettings;
import co.edu.icesi.nextfruit.modules.model.MatchingColor;
import co.edu.icesi.nextfruit.modules.model.MatchingColorInterpreter;
import co.edu.icesi.nextfruit.modules.persistence.CalibrationDataHandler;
import co.edu.icesi.nextfruit.mvc.interfaces.Attachable;
import co.edu.icesi.nextfruit.mvc.interfaces.Updateable;
import co.edu.icesi.nextfruit.util.MatrixReader;
import weka.classifiers.Classifier;


public class Model implements Attachable {
	
	private static final int CALIBRATE = 0;
	private static final int EXTRACT = 1;

	// MVC objects
	private LinkedList<Updateable> updateables;

	// Calibration module
	private ColorChecker colorChecker;
	private SizeCalibrator sizeCalibrator;
	private CalibrationDataHandler calibrationDataHandler;

	// Computer vision module
	private FeaturesExtract featuresExtract;
	private List<MatchingColor> matchingColors;

	// Machine learning module
	private ModelBuilder modelBuilder;
	private WekaClassifier weka;

	private double[][] definedColors = new double[][]{
		{0.30,0.49,0.11},
		{0.62,0.31,0.15},
		{0.37,0.35,0.03}
	};


	/**
	 * Creates an empty model ready to interact with all the classes from specific modules.
	 * This starts the OpenCV library
	 */
	public Model() {
		this.updateables = new LinkedList<>();
		// Starting OpenCV
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	// ************************** LIFE CYCLE **************************

	@Override
	public void attach(Updateable updateable) {
		updateables.add(updateable);
	}

	@Override
	public void detach(Updateable updateable) {
		updateables.remove(updateable);
	}

	private void updateAll() {
		for (Updateable updateable : updateables) {
			updateable.update();
		}
	}

	private void clearMemory(int process) {
		switch (process) {
		case CALIBRATE:
			calibrationDataHandler = null;
			featuresExtract = null;
			matchingColors = null;
			break;

		case EXTRACT:
			colorChecker = null;
			sizeCalibrator = null;
			break;
		}
	}

	// ************************** LIFE CYCLE **************************

	// ********************** CALIBRATION MODULE **********************

	public void startColorChecker(String colorCheckerPath) {
		colorChecker = new ColorChecker(colorCheckerPath, 150, Constants.ORIGINALS);
		updateAll();
	}

	public void startSizeCalibrator(String sizeCalibratorPath) {
		sizeCalibrator = new SizeCalibrator(sizeCalibratorPath, 6, 9);
		updateAll();
	}

	public void calibrate(File conversionCsv, double gridSize) throws IOException {
		if(colorChecker != null) {
			colorChecker.process(MatrixReader.readCameraSettings(conversionCsv.getAbsolutePath()));
		}
		if(sizeCalibrator != null) {
			sizeCalibrator.process(gridSize);
		}
		updateAll();
	}

	/**
	 * This method saves the particular calibration data, of a given camera, as an XML file in disk.
	 * @param file File object with the information about the XML file to save.
	 * @param rgbs array containing the information about the RGB colors from each box in the colorchecker.
	 * @return boolean, represents if file was saved correctly or not.
	 */
	public boolean saveCalibrationData(File file, int[][][] rgbs, double pixelsxCm){			
		CameraSettings cS = colorChecker.getCameraSettings();
		try {
			calibrationDataHandler.saveCalibrationData(file, rgbs, pixelsxCm, cS.getIlluminant(),
					cS.getWorkingSpaceMatrix(), cS.getWhiteX(), cS.getWhiteY(), cS.getWhiteZ());
			return true;
		} catch (JAXBException e) {
			e.printStackTrace();
			return false;
		}
	}


	/**
	 * This method loads, to the application, the calibration data of a particular camera,
	 * saved as an XML file in disk.
	 * @param file File object with the information about the XML file to read.
	 * @return boolean, represents if the file was added successfully or not.
	 */
	public boolean loadCalibrationData(File file){
		try {
			calibrationDataHandler = new CalibrationDataHandler();
			calibrationDataHandler.loadCalibrationData(file);
			return true;
		} catch (JAXBException e) {
			calibrationDataHandler = null;
			return false;
		}
	}

	// ********************** CALIBRATION MODULE **********************

	// ******************* CHARACTERIZATION MODULE ********************

	public void startFeaturesExtract(String path) {
		clearMemory(EXTRACT);
		featuresExtract = new FeaturesExtract(path);
		if(matchingColors == null)
			matchingColors = new ArrayList<>();
		updateAll();
	}

	public boolean extractFeatures() {
		if(featuresExtract != null && calibrationDataHandler != null) {
			featuresExtract.extractFeatures(calibrationDataHandler.getCameraCalibration());
			updateAll();
			return true;
		}
		return false;
	}

	public void identifyMatchingColors(String text) {
		matchingColors = new ArrayList<>();
		String lines[] = text.split("\n");
		double[][] inverseMatrixM = getCameraCalibration().getInverseWorkingSpaceMatrix();
		for (String line : lines) {
			MatchingColor mc = MatchingColorInterpreter.identifyMatchingColor(line, inverseMatrixM, 0.75);
			if(mc != null)
				matchingColors.add(mc);
		}
		updateAll();
	}

	public boolean analizeImage() {
		if(featuresExtract != null && featuresExtract.hasExtractedFeatures()) {
			featuresExtract.analizeData(getCameraCalibration(), matchingColors);
			updateAll();
			return true;
		}
		return false;
	}

	// ******************* CHARACTERIZATION MODULE ********************

	// ******************* MACHINE LEARNING MODULE ********************

	/**
	 * Loads a training set previously saved.
	 * @param file The location of the file with the training set.
	 */
	public boolean loadTrainingSet(File file, String classifierType) {
		this.modelBuilder = new ModelBuilder();
		return this.modelBuilder.loadTrainingSet(file, classifierType, getCameraCalibration());
	}

	/**
	 * Loads the image file names from the given folder.
	 * @param folder This is the location where the fruit images are located.
	 */
	public void loadImagesForTraining(File folder) {
		if(folder != null && folder.isDirectory()) {
			this.modelBuilder = new ModelBuilder();
			this.modelBuilder.loadImages(folder);
		}
	}

	/**
	 * This method has to be called after loadImagesForTraining, it extracts the image features and creates a new training set.
	 * @param destinationFile The file where the training set will be saved.
	 */
	public void processTrainingSet(File destinationFile) {
		if(modelBuilder != null && modelBuilder.hasLoadedImages()) {
			this.modelBuilder.processTrainingSet("-", destinationFile, getCameraCalibration());
		}
	}

	/**
	 * This method has to be called if either processTrainingSet or loadTrainingSet have been called.
	 * @param destinationFile The file where the model will be saved.
	 * @param type The type of machine learning to use for building the new model.
	 */
	public boolean trainClassifier(File savedTrainingSet, File destinationFile, String technique, String classifier) {
		if(modelBuilder != null) {
			this.modelBuilder.trainClassifier(savedTrainingSet, destinationFile, technique, classifier);
			return true;
		}
		return false;
	}

	
	/**
	 * Test a classifier.
	 * @param type
	 * @param classificationModel
	 * @param trainingSetFile
	 * @param testSetFile
	 * @param testResults
	 * @throws Exception
	 */
	public void testClassifier(String type, File classificationModel, File trainingSetFile, 
			File testSetFile, File testResults) throws Exception{
		
		switch (type) {
		
		case ModelBuilder.QUALITY_CLASSIFIER:
			weka = new QualityClassifier(getCameraCalibration());
			break;
			
		case ModelBuilder.SIZE_CLASSIFIER:
			weka = new SizeClassifier(getCameraCalibration());
			break;
			
		case ModelBuilder.CLASS_CLASSIFIER:
			weka = new ClassClassifier(getCameraCalibration());
			break;
			
		case ModelBuilder.RIPENESS_CLASSIFIER:
			weka = new RipenessClassifier(getCameraCalibration());
			break;

		}
		
		Classifier param = weka.loadClassifierFromFile(classificationModel);
		weka.testClassifierModel(param, trainingSetFile, 
				testSetFile, testResults);
		
	}

	
	/**
	 * NOT IMPLEMENTED YET
	 * NOT IMPLEMENTED YET
	 * NOT IMPLEMENTED YET
	 * NOT IMPLEMENTED YET
	 * NOT IMPLEMENTED YET
	 * NOT IMPLEMENTED YET
	 * NOT IMPLEMENTED YET
	 * NOT IMPLEMENTED YET
	 * NOT IMPLEMENTED YET
	 * NOT IMPLEMENTED YET
	 * NOT IMPLEMENTED YET
	 * NOT IMPLEMENTED YET
	 * NOT IMPLEMENTED YET
	 * NOT IMPLEMENTED YET
	 * NOT IMPLEMENTED YET
	 * NOT IMPLEMENTED YET
	 * @param image
	 * @param classifier
	 * @return
	 * @throws Exception
	 */
	public double[] classifyImage(File image, File classifier) throws Exception{

//		weka = new QualityFeaturesAdapter(getCameraCalibration(), 1);
//		Classifier model = weka.loadClassifierFromFile(classifier);
//		ArrayList<Attribute> features = weka.getFeatures();
//		Instances dataUnlabeled = new Instances("test-instances", features, 0);
//		CameraCalibration calibration = getCameraCalibration();
//
//		// Creating temporal matching colors
//		matchingColors = new ArrayList<>();
//		for (double[] color : definedColors){
//			matchingColors.add(new MatchingColor(new double[]{color[0], color[1], 0.75}, color[2], calibration.getInverseWorkingSpaceMatrix()));
//		}
//
//		// Getting class name
//		String fileName = image.getName();
//		String className = null;
//		try {
//			className = fileName.substring(0, fileName.indexOf("-"));
//		} catch(Exception e1) {
//			try {
//				className = fileName.substring(0, fileName.indexOf("."));
//			} catch(Exception e2) {
//				className = fileName;
//			}
//		}
//
//		System.out.println("Extracting features... ("+className+")");
//
//		// Processing features
//		FeaturesExtract ft = new FeaturesExtract(image.getAbsolutePath());
//		ft.extractFeatures(calibration);
//		ft.analizeData(calibration, matchingColors);
//
//		// Getting results
//		Collection<ColorDistribution> matchs = ft.getMatchingColors();
//		Statistics luminantStatistics = ft.getLuminanceStatistics();
//		PolygonWrapper polygon = ft.getPolygon();
//		int index = 0;
//		double colors[] = new double[3];
//		for (ColorDistribution color : matchs)
//			colors[index++] = color.getRepeat()/(double)ft.getNumberOfPixels();
//
//		// Creating instances
//		Instance unknown = new DenseInstance(8);
//
//		unknown.setValue(features.get(0), polygon.getArea());
//		unknown.setValue(features.get(1), luminantStatistics.getMean());
//		unknown.setValue(features.get(2), luminantStatistics.getStandardDeviation());
//		unknown.setValue(features.get(3), luminantStatistics.getSkewness());
//		unknown.setValue(features.get(4), luminantStatistics.getKurtosis());
//		unknown.setValue(features.get(5), colors[0]);
//		unknown.setValue(features.get(6), colors[1]);
//		unknown.setValue(features.get(7), colors[2]);
//
//		dataUnlabeled.add(unknown);
//
//		double[] fDistribution = weka.classify(model, dataUnlabeled);
//		return fDistribution;
		
		return null;
	}



	// ******************* MACHINE LEARNING MODULE ********************

	// ************************ ACCESS METHODS ************************

	public ColorChecker getColorChecker() {
		return colorChecker;
	}

	public SizeCalibrator getSizeCalibrator() {
		return sizeCalibrator;
	}

	public CameraCalibration getCameraCalibration() {
		return calibrationDataHandler.getCameraCalibration();
	}

	public FeaturesExtract getFeaturesExtract() {
		return featuresExtract;
	}

	public List<MatchingColor> getMatchingColors() {
		return matchingColors;
	}

	// ************************ ACCESS METHODS ************************

}
