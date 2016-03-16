package co.edu.icesi.nextfruit.modules.model;

import co.edu.icesi.nextfruit.util.ColorConverter;

public class MatchingColor extends ColorDistribution {

	private double sensibility;

	public MatchingColor(double[] xyY, double sensibility) {
		super(ColorConverter.xyY2bgr(xyY));
		setXYY(xyY);
		restartRepeatCount();
		this.sensibility = sensibility;
	}

	public void increaseIfClose(double[] xyY, int increment) {
		if(this.isCloseToXY(xyY, sensibility))
			repeat(increment);
	}

	public double[] getDescriptor() {
		return new double[] {getxyY()[0], getxyY()[1], sensibility};
	}

}
