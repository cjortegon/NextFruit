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
import co.edu.icesi.nextfruit.modules.model.CameraCalibration;
import co.edu.icesi.nextfruit.modules.model.CameraSettings;
import co.edu.icesi.nextfruit.modules.model.MatchingColor;
import co.edu.icesi.nextfruit.modules.persistence.CalibrationDataHandler;
import co.edu.icesi.nextfruit.mvc.interfaces.Attachable;
import co.edu.icesi.nextfruit.mvc.interfaces.Updateable;
import co.edu.icesi.nextfruit.util.MatrixReader;

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
			String numbers[] = line.split(";");
			try {
				double[] xyY = new double[]{
						Double.valueOf(numbers[0]),
						Double.valueOf(numbers[1]),
						0.75
				};
				matchingColors.add(new MatchingColor(xyY, Double.valueOf(numbers[2]), inverseMatrixM));
			} catch(NumberFormatException nfe) {
			} catch(ArrayIndexOutOfBoundsException aiobe) {
			}
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
	 * Loads a training set previously saved.
	 * @param file The location of the file with the training set.
	 */
	public boolean loadTrainingSet(File file) {
		this.modelBuilder = new ModelBuilder();
		return this.modelBuilder.loadTrainingSet(file);
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
	public boolean trainClassifier(File destinationFile, String type) {
		if(modelBuilder != null) {
			this.modelBuilder.trainClassifier(destinationFile, type);
			return true;
		}
		return false;
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
