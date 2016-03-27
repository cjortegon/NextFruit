package co.edu.icesi.nextfruit.modules.model;

import java.awt.Color;

import co.edu.icesi.nextfruit.util.ColorConverter;
import co.edu.icesi.nextfruit.util.Geometry;

/**
 * This class is used to wrap in xyY (double[]) color into a java.awt.Color.
 * It can be used to count the number of similar colors (repeat property) based on the xyY color space.
 * It implements comparable witch sorts the classes according to the number of repetitions (repeat property).
 * @author cjortegon
 */
public class ColorDistribution extends Color implements Comparable<ColorDistribution> {

	private int repeat;
	private double[] xyY;

	public ColorDistribution(int color) {
		super(color);
		this.repeat = 1;
	}

	/**
	 * The constructor uses a bgr format (blue, green, red) where the 3 values are in the range (0..255)
	 * @param bgr double vector, size 3. [0] Blue, [1] Green, [2] Red.
	 */
	public ColorDistribution(double[] bgr) {
		super(ColorConverter.bgr2rgb(bgr));
		this.repeat = 1;
	}

	/**
	 * Used only by children of this class.
	 * @param xyY calculated xyY
	 */
	protected void setXYY(double[] xyY) {
		this.xyY = xyY;
	}

	/**
	 * Will set the repeat property to zero.
	 */
	public void restartRepeatCount() {
		repeat = 0;
	}

	/**
	 * Will increase repeat property in 1
	 */
	public void repeat() {
		repeat ++;
	}

	/**
	 * Will increase repeat property in the given number (may be negative) 
	 * @param times The number to increase.
	 */
	public void repeat(int times) {
		repeat += times;
	}

	/**
	 * @return The repeat property.
	 */
	public int getRepeat() {
		return repeat;
	}

	/**
	 * This method initializes the xyY property to compare the color with others in the xyY color space.
	 * @param calibration The calibration object from the camera used to read the BGR colors.
	 */
	public void transform2xyY(CameraCalibration calibration) {
		xyY = ColorConverter.rgb2xyY(getRGB(),
				calibration.getWorkingSpaceMatrix(),
				calibration.getWhiteX());
	}

	/**
	 * @return The values in xyY color space. Null if transform2xyY() hasn't been called.
	 */
	public double[] getxyY() {
		return xyY;
	}

	/**
	 * Tells you if the given xyY color is closed to this color.
	 * <pre> transform2xyY() must be called first.
	 * @param xyY The color to compare.
	 * @param sensibility The distance between colors. May be in the range of (0 -> 1.42).
	 * Zero means an exact coincidence and max value (1.42) means that all colors could match to it.
	 * @return false if transform2xyY() hasn't been called or if the color is not close enough according to sensibility. True otherwise.
	 */
	public boolean isCloseToXY(double[] xyY, double sensibility) {
		if(this.xyY == null)
			return false;
		else
			return Geometry.distance(this.xyY[0], this.xyY[1], xyY[0], xyY[1]) < sensibility;
	}

	@Override
	public int compareTo(ColorDistribution o) {
		return repeat - o.repeat;
	}
}
