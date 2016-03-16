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

	// Private attributes
	private LinkedList<Updateable> updateables;
	private ColorChecker colorChecker;
	private SizeCalibrator sizeCalibrator;
	private CalibrationDataHandler calibrationDataHandler;
	private FeaturesExtract featuresExtract;
	private List<MatchingColor> matchingColors;

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

	private void clearMemory() {
		colorChecker = null;
		sizeCalibrator = null;
		calibrationDataHandler = null;
		featuresExtract = null;
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

	public void startCalDataHandler(){
		calibrationDataHandler = new CalibrationDataHandler();
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
	 * @param rgbs array containing the information about the rgb colors of each box in a colorchecker.
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

			calibrationDataHandler.loadCalibrationData(file);
			return true;

		} catch (JAXBException e) {

			e.printStackTrace();
			return false;
		}
	}

	// ********************** CALIBRATION MODULE **********************

	// ******************* CHARACTERIZATION MODULE ********************

	public void startFeaturesExtract(String path) {
		clearMemory();
		featuresExtract = new FeaturesExtract(path);
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
		for (String line : lines) {
			String numbers[] = line.split(",");
			try {
				double[] xyY = new double[]{
						Double.valueOf(numbers[0]),
						Double.valueOf(numbers[1]),
						0.75
				};
				matchingColors.add(new MatchingColor(xyY, Double.valueOf(numbers[2])));
			} catch(NumberFormatException nfe) {
			} catch(ArrayIndexOutOfBoundsException aiobe) {
			}
		}
		updateAll();
	}

	// ******************* CHARACTERIZATION MODULE ********************

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
