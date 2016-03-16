package co.edu.icesi.nextfruit.modules.model;

import co.edu.icesi.nextfruit.util.ColorConverter;

public class MatchingColor extends ColorDistribution {

	private double sensibility;

	public MatchingColor(double[] xyY, double sensibility) {
		super(ColorConverter.xyY2bgr(xyY));
		this.xyY = xyY;
		this.repeat = 0;
		this.sensibility = sensibility;
	}

	public void increaseIfClose(double[] xyY) {
		if(this.isCloseToXY(xyY, sensibility))
			repeat();
	}
	
	public double[] getDescriptor() {
		return new double[] {xyY[0], xyY[1], sensibility};
	}

}
