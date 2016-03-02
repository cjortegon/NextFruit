package co.edu.icesi.nextfruit.modules;

import java.io.File;
import java.util.LinkedList;

import javax.xml.bind.JAXBException;

import org.opencv.core.Core;

import co.edu.icesi.nextfruit.modules.callibrator.Calibration_Data_Handler;
import co.edu.icesi.nextfruit.modules.callibrator.ColorChecker;
import co.edu.icesi.nextfruit.modules.callibrator.SizeCalibrator;
import co.edu.icesi.nextfruit.mvc.interfaces.Attachable;
import co.edu.icesi.nextfruit.mvc.interfaces.Updateable;

public class Model implements Attachable {

	// Private attributes
	private LinkedList<Updateable> updateables;
	private ColorChecker colorChecker;
	private SizeCalibrator sizeCalibrator;
	private Calibration_Data_Handler calibrationDataHandler;
	

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
		calibrationDataHandler = new Calibration_Data_Handler();
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
	 * 
	 * @param file
	 * @param rgbs
	 * @return
	 */
	public boolean saveCalibrationData(File file, int[][][] rgbs, double pixelsxCm){
		
		try {
			
			System.out.println(file + ", " + rgbs[0][0][0] + ", " + pixelsxCm);
			
			calibrationDataHandler.saveCalibrationData(file, rgbs, pixelsxCm);
			return true;
			
		} catch (JAXBException e) {
			
			e.printStackTrace();
			return false;
		}
	}
	
	
	/**
	 * 
	 * @param file
	 * @return
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
