package co.edu.icesi.nextfruit.modules.model;

import java.util.List;

import co.edu.icesi.nextfruit.util.ColorConverter;

public class MatchingColorGroup extends MatchingColor {

	/**
	 * The property used to say if a color is close enough to match with this color profile.
	 */
	private double sensibility;

	/**
	 * The list of colors used to compare.
	 */
	private List<MatchingColor> list;

	/**
	 * 
	 * @param list
	 * @param sensibility
	 * @param inverseMatrixM
	 */
	protected MatchingColorGroup(List<MatchingColor> list, double[][] inverseMatrixM) {
		super(ColorConverter.xyY2bgr(xyYAverage(list), inverseMatrixM), 0, inverseMatrixM);
		restartRepeatCount();
		this.list = list;
	}

	/**
	 * For construction purposes only.
	 * @param list
	 * @return
	 */
	private static double[] xyYAverage(List<MatchingColor> list) {
		double x = 0, y = 0, Y = 0;
		for (MatchingColor matchingColor : list) {
			x += matchingColor.getxyY()[0];
			y += matchingColor.getxyY()[1];
			Y += matchingColor.getxyY()[2];
		}
		return new double[]{x/list.size(), y/list.size(), Y/list.size()};
	}

	/**
	 * Auto-increases the repeat property in increment value if the xyY is close enough to the container list of colors.
	 * @param xyY The color to compare.
	 * @param increment The increment value if the color match.
	 */
	public void increaseIfClose(double[] xyY, int increment) {
		for (MatchingColor color : list) {
			if(color.isCloseToXY(xyY, color.getSensibility())) {
				repeat(increment);
				return;
			}
		}
	}

	/**
	 * @return The descriptor of this object is composed of x, y parts of xyY and sensibility.
	 */
	public double[] getDescriptor() {
		double[] descriptor = new double[list.size()*3];
		for (int i = 0; i < list.size(); i++) {
			double[] desc = list.get(i).getDescriptor();
			descriptor[i*3] = desc[0];
			descriptor[i*3+1] = desc[1];
			descriptor[i*3+2] = desc[2];
		}
		return descriptor;
	}

	/**
	 * @return The text representation of this MatchingColor
	 */
	public String getTextDescriptor() {
		String descriptor = "";
		for (int i = 0; i < list.size(); i++)
			descriptor += list.get(i).getDescriptor() + (i < list.size()-1 ? "+" : "");
		return descriptor;
	}

}
