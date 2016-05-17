package co.edu.icesi.nextfruit.modules.model;

import java.util.ArrayList;

/**
 * This class reads a matching color structure from a text
 * @author cjortegon
 */
public class MatchingColorInterpreter {

	/**
	 * Method to convert from string representation to object representation of one matching color
	 * @param line text that contains the string representation of the matching color
	 * @param inverseMatrixM matrix used to convert from rgb and xyz color spaces
	 * @param defaultY white value used to convert from rgb and xyz color spaces
	 * @return A matching color object witch can be either MatchingColorGroup or MatchingColor
	 */
	public static MatchingColor identifyMatchingColor(String line, double[][] inverseMatrixM, double defaultY) {
		try {
			String parts[] = line.split("\\+");
			if(parts.length == 1) {
				String numbers[] = line.split(";");
				double[] xyY = new double[]{
						Double.valueOf(numbers[0]),
						Double.valueOf(numbers[1]),
						defaultY
				};
				return new MatchingColor(xyY, Double.valueOf(numbers[2]), inverseMatrixM);
			} else {
				ArrayList<MatchingColor> list = new ArrayList<>();
				for (String part : parts) {
					String numbers[] = part.split(";");
					double[] xyY = new double[]{
							Double.valueOf(numbers[0]),
							Double.valueOf(numbers[1]),
							defaultY
					};
					list.add(new MatchingColor(xyY, Double.valueOf(numbers[2]), inverseMatrixM));
				}
				return new MatchingColorGroup(list, inverseMatrixM);
			}
		} catch(NumberFormatException nfe) {
		} catch(ArrayIndexOutOfBoundsException aiobe) {
		}
		return null;
	}

}
