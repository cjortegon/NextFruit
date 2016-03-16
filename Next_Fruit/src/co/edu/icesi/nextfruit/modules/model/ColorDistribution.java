package co.edu.icesi.nextfruit.modules.model;

import java.awt.Color;

import co.edu.icesi.nextfruit.util.ColorConverter;
import co.edu.icesi.nextfruit.util.Geometry;

public class ColorDistribution extends Color implements Comparable<ColorDistribution> {

	protected int repeat;
	protected double[] xyY;

	public ColorDistribution(int color) {
		super(color);
		this.repeat = 1;
	}

	public ColorDistribution(double[] bgr) {
		super(ColorConverter.bgr2rgb(bgr));
		this.repeat = 1;
	}

	public void repeat() {
		repeat ++;
	}

	public void repeat(int times) {
		repeat += times;
	}

	public void transform2xyY(CameraCalibration calibration) {
		xyY = ColorConverter.rgb2xyY(getRGB(),
				calibration.getWorkingSpaceMatrix(),
				calibration.getWhiteX());
	}

	public double[] getxyY() {
		return xyY;
	}

	public boolean isCloseToXY(double[] xyY, double sensibility) {
		if(this.xyY == null)
			return false;
		else {
			return Geometry.distance(this.xyY[0], this.xyY[1], xyY[0], xyY[1]) < sensibility;
		}
	}

	@Override
	public int compareTo(ColorDistribution o) {
		return repeat - o.repeat;
	}
}
