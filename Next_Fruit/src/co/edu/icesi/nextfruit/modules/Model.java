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
import co.edu.icesi.nextfruit.modules.machinelearning.WekaClassifierAdapter;
import co.edu.icesi.nextfruit.modules.model.CameraCalibration;
import co.edu.icesi.nextfruit.modules.model.CameraSettings;
import co.edu.icesi.nextfruit.modules.model.MatchingColor;
import co.edu.icesi.nextfruit.modules.model.MatchingColorInterpreter;
import co.edu.icesi.nextfruit.modules.persistence.CalibrationDataHandler;
import co.edu.icesi.nextfruit.mvc.interfaces.Attachable;
import co.edu.icesi.nextfruit.mvc.interfaces.Updateable;
import co.edu.icesi.nextfruit.util.MatrixReader;
import co.edu.icesi.nextfruit.util.ProgressUpdatable;
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
	private WekaClassifierAdapter classifiers[];

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
	public boolean saveCalibrationData(File file) {
		int[][][] rgbs = double2Int(colorChecker.getReadRGB());
		double pixelsxCm = getSizeCalibrator().getPixelsForCentimeter();
		CameraSettings cS = colorChecker.getCameraSettings();
		try {
			CalibrationDataHandler calibrationDataHandler = new CalibrationDataHandler();
			calibrationDataHandler.saveCalibrationData(file, rgbs, pixelsxCm, cS.getIlluminant(),
					cS.getWorkingSpaceMatrix(), cS.getWhiteX(), cS.getWhiteY(), cS.getWhiteZ());
			return true;
		} catch (JAXBException e) {
			e.printStackTrace();
			return false;
		}
	}

	private int[][][] double2Int(double[][][] values) {
		int[][][] result = new int[values.length][values[0].length][values[0][0].length];
		for (int i = 0; i < result.length; i++) {
			for (int j = 0; j < result[0].length; j++) {
				for (int k = 0; k < result[0][0].length; k++) {
					result[i][j][k] = (int) values[i][j][k];
				}
			}
		}
		return result;
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
			System.out.println("Extracting features...");
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
	public int loadImagesForTraining(File folder) {
		if(folder != null && folder.isDirectory()) {
			this.modelBuilder = new ModelBuilder();
			return this.modelBuilder.loadImages(folder);
		}
		return 0;
	}

	/**
	 * This method has to be called after loadImagesForTraining, it extracts the image features and creates a new training set.
	 * @param destinationFile The file where the training set will be saved.
	 */
	public void processTrainingSet(File destinationFile, ProgressUpdatable progress) {
		if(modelBuilder != null && modelBuilder.hasLoadedImages()) {
			this.modelBuilder.processTrainingSet("-", destinationFile, getCameraCalibration(), progress);
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

	public void loadClassifier(String type, File classifier) throws Exception{

		if(classifiers == null)
			classifiers = new WekaClassifierAdapter[4];

		switch (type) {

		case ModelBuilder.QUALITY_CLASSIFIER:
			classifiers[0] = new QualityClassifier(getCameraCalibration(), classifier);
			break;

		case ModelBuilder.CLASS_CLASSIFIER:
			//classifiers[1] = new ClassClassifier(getCameraCalibration(), classifier);
			break;

		case ModelBuilder.SIZE_CLASSIFIER:
			classifiers[2] = new SizeClassifier(getCameraCalibration(), classifier);
			break;

		case ModelBuilder.RIPENESS_CLASSIFIER:
			classifiers[3] = new RipenessClassifier(getCameraCalibration(), classifier);
			break;
		}
	}

	public boolean canClassify() {
		boolean hasOneClassifier = classifiers != null;
		if(hasOneClassifier) {
			for (WekaClassifierAdapter classifier : classifiers) {
				if(classifier != null) {
					hasOneClassifier = true;
					break;
				}
			}
		}
		return hasOneClassifier && featuresExtract != null;
	}

	public double[][] classifyImage() throws Exception {

		if(classifiers == null || featuresExtract == null)
			return null;

		double[][] fDistributions = new double[classifiers.length][100];

		if(!featuresExtract.hasExtractedFeatures()) {
			extractFeatures();
		}

		for (int i = 0; i < classifiers.length; i++) {
			if(classifiers[i] != null) {
				try {
					fDistributions[i] = classifiers[i].classify(classifiers[i].getInstanceFromFeatures(featuresExtract));
				} catch(Exception e) {
					System.out.println("Error in classifier: "+classifiers[i].getClass().getName());
					e.printStackTrace();
				}
			}
		}

		updateAll();
		return fDistributions;
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
