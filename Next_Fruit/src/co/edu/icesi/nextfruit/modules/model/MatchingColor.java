package co.edu.icesi.nextfruit.modules.model;

import co.edu.icesi.nextfruit.util.ColorConverter;

/**
 * This class extends from ColorDistribution and is used to initialize one instance of it using a xyY color.
 * It's main used is to deal with color repetitions statistics using the increaseIfClose() method.
 * @author cjortegon
 */
public class MatchingColor extends ColorDistribution {

	/**
	 * The property used to say if a color is close enough to match with this color profile.
	 */
	private double sensibility;

	/**
	 * Constructor of a ColorDistribution using a xyY color.
	 * @param xyY The color to initialize the instance.
	 * @param sensibility The range of colors that will accept the matching to this.
	 * Is in the range of (0 -> 1.42) as it is in ColorDistribution.isCloseToXY() method.
	 * @param inverseMatrixM The matrix used to return to RGB color.
	 */
	public MatchingColor(double[] xyY, double sensibility, double[][] inverseMatrixM) {
		super(ColorConverter.xyY2bgr(xyY, inverseMatrixM));
		setXYY(xyY);
		restartRepeatCount();
		this.sensibility = sensibility;
	}

	/**
	 * Auto-increases the repeat property in increment value if the xyY is close enough to this color.
	 * @param xyY The color to compare.
	 * @param increment The increment value if the color match.
	 */
	public void increaseIfClose(double[] xyY, int increment) {
		if(this.isCloseToXY(xyY, sensibility))
			repeat(increment);
	}

	/**
	 * @return The descriptor of this object is composed of x, y parts of xyY and sensibility.
	 */
	public double[] getDescriptor() {
		return new double[] {getxyY()[0], getxyY()[1], sensibility};
	}

	/**
	 * @return The sensibility to match this color
	 */
	public double getSensibility() {
		return sensibility;
	}

}
