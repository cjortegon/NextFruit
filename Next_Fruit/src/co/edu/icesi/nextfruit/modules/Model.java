package co.edu.icesi.nextfruit.modules;

import java.io.File;
import java.util.LinkedList;

import javax.xml.bind.JAXBException;

import org.opencv.core.Core;

import co.edu.icesi.nextfruit.modules.callibrator.ColorChecker;
import co.edu.icesi.nextfruit.modules.callibrator.SizeCalibrator;
import co.edu.icesi.nextfruit.modules.persistence.CalibrationDataHandler;
import co.edu.icesi.nextfruit.mvc.interfaces.Attachable;
import co.edu.icesi.nextfruit.mvc.interfaces.Updateable;

public class Model implements Attachable {

	// Private attributes
	private LinkedList<Updateable> updateables;
	private ColorChecker colorChecker;
	private SizeCalibrator sizeCalibrator;
	private CalibrationDataHandler calibrationDataHandler;
	

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
	
	public void calibrate(File conversionMatrix, double gridSize) {
		if(colorChecker != null) {
			colorChecker.process(conversionMatrix);
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
		
		try {
						
			calibrationDataHandler.saveCalibrationData(file, rgbs, pixelsxCm);
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
	
	
	
	//
	//	Data Access Methods
	//
	
	public ColorChecker getColorChecker() {
		return colorChecker;
	}

	public SizeCalibrator getSizeCalibrator() {
		return sizeCalibrator;
	}

	// ********************** CALIBRATION MODULE **********************

}
