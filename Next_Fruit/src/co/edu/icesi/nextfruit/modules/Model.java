package co.edu.icesi.nextfruit.modules;

import java.util.LinkedList;

import org.opencv.core.Core;

import co.edu.icesi.nextfruit.modules.callibrator.ColorChecker;
import co.edu.icesi.nextfruit.modules.callibrator.SizeCalibrator;
import co.edu.icesi.nextfruit.mvc.interfaces.Attachable;
import co.edu.icesi.nextfruit.mvc.interfaces.Updateable;

public class Model implements Attachable {

	// Private attributes
	private LinkedList<Updateable> updateables;
	private ColorChecker colorChecker;
	private SizeCalibrator sizeCalibrator;

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

	public void calibrate(double gridSize) {
		if(colorChecker != null) {
			colorChecker.process();
		}
		if(sizeCalibrator != null) {
			sizeCalibrator.process(gridSize);
		}
		updateAll();
	}

	public ColorChecker getColorChecker() {
		return colorChecker;
	}

	public SizeCalibrator getSizeCalibrator() {
		return sizeCalibrator;
	}

	// ********************** CALIBRATION MODULE **********************

}
