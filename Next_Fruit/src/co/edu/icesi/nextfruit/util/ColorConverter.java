package co.edu.icesi.nextfruit.util;

/**
 * This static class converts color values from one color space to another.
 * @author JuanD
 *
 */
public class ColorConverter {

	private static final double EPSILON = 216/24389;
	private static final double KAPPA = 24389/27;

	/**
	 * Converts from RGB color space to equivalent XYZ color space.
	 * Color must be arranged in [R,G,B] order.
	 * @param rgb, array representing a color.
	 * @param matrixM, the M matrix to use in the conversion.
	 * @return xyz, the xyz equivalent value of the rgb received as a parameter.
	 */
	public static double[] rgb2xyz(double[] rgb, double[][] matrixM) {
		double red = rgb[0];
		double green = rgb[1];
		double blue = rgb[2];

		if(red > 0.04045){
			red = Math.pow(((red + 0.055)/1.055), 2.4);
		}else{
			red = red/12.92;
		}
		if(green > 0.04045){
			green = Math.pow((green + 0.055)/1.055, 2.4);
		}else{
			green = green/12.92;
		}
		if(blue > 0.04045){
			blue = Math.pow((blue + 0.055)/1.055, 2.4);
		}else{
			blue = blue/12.92;
		}

		double[] xyz = new double[3];

		xyz[0] = (((matrixM[0][0])*red)+((matrixM[0][1])*green)+(matrixM[0][2]*blue));
		xyz[1] = (((matrixM[1][0])*red)+((matrixM[1][1])*green)+(matrixM[1][2]*blue));
		xyz[2] = (((matrixM[2][0])*red)+((matrixM[2][1])*green)+(matrixM[2][2]*blue));

		return xyz;
	}

	/**
	 * Converts from RGB color space to equivalent XYZ color space.
	 * Use a sRGB M matrix by default.
	 * Color must be arranged in [R,G,B] order.
	 * @param rgb, array representing a color.
	 * @return xyz, Array with the xyz equivalent value of the rgb received as a parameter.
	 */
	public static double[] rgb2xyz(double[] rgb) {
		double red = rgb[0];
		double green = rgb[1];
		double blue = rgb[2];

		if(red > 0.04045){
			red = Math.pow(((red + 0.055)/1.055), 2.4);
		}else{
			red = red/12.92;
		}
		if(green > 0.04045){
			green = Math.pow((green + 0.055)/1.055, 2.4);
		}else{
			green = green/12.92;
		}
		if(blue > 0.04045){
			blue = Math.pow((blue + 0.055)/1.055, 2.4);
		}else{
			blue = blue/12.92;
		}

		double[][] matrixM = new double[][] {
			{0.4124564, 0.3575761, 0.1804375},
			{0.2126729, 0.7151522, 0.0721750},
			{0.0193339, 0.1191920, 0.9503041}
		};

		double[] xyz = new double[3];

		xyz[0] = (((matrixM[0][0])*red)+((matrixM[0][1])*green)+(matrixM[0][2]*blue));
		xyz[1] = (((matrixM[1][0])*red)+((matrixM[1][1])*green)+(matrixM[1][2]*blue));
		xyz[2] = (((matrixM[2][0])*red)+((matrixM[2][1])*green)+(matrixM[2][2]*blue));

		return xyz;
	}

	/**
	 * Converts from XYZ color space to equivalent Lab color space.
	 * Color must be arranged in [X,Y,Z] order.
	 * @param xyz, array representing a color.
	 * @param illuminant, string
	 * @return lab, Array with the Lab equivalent value of the xyz received as a parameter.
	 */
	public static double[] xyz2lab(double[] xyz, String illuminant) {
		double[] Lab = new double[3];

		//White Reference by defalut
		double XOfR = 1.0;
		double YOfR = 1.0;
		double ZOfR = 1.0;

		switch (illuminant) {

		case "A":
			XOfR = 1.09850;
			ZOfR = 0.35585;
			break;

		case "B":
			XOfR = 0.99072;
			ZOfR = 0.85223;
			break;

		case "C":
			XOfR = 0.98074;
			ZOfR = 1.18232;
			break;

		case "D50":
			XOfR = 0.96422;
			ZOfR = 0.82521;
			break;

		case "D55":
			XOfR = 0.95682;
			ZOfR = 0.92149;
			break;

		case "D65":
			XOfR = 0.95047;
			ZOfR = 1.08883;
			break;

		case "D75":
			XOfR = 0.94972;
			ZOfR = 1.22638;
			break;

		case "E":
			XOfR = 1.00000;
			ZOfR = 1.00000;
			break;

		case "F2":
			XOfR = 0.99186;
			ZOfR = 0.67393;
			break;

		case "F7":
			XOfR = 0.95041;
			ZOfR = 1.08747;
			break;

		case "F11":
			XOfR = 1.00962;
			ZOfR = 0.64350;
			break;

		default:
			XOfR = 0.95047;
			ZOfR = 1.08883;
			break;

		}

		double x = xyz[0]/XOfR;
		double y = xyz[1]/YOfR;
		double z = xyz[2]/ZOfR;

		// Functions
		double fOfX = 0.0;
		double fOfY = 0.0;
		double fOfZ = 0.0;

		// Calculate the values of the functions
		if(x>EPSILON){
			fOfX = Math.cbrt(x);
		}else{
			fOfX = (((KAPPA*x)+16)/116);
		}

		if(y>EPSILON){
			fOfY = Math.cbrt(y);
		}else{
			fOfY = (((KAPPA*y)+16)/116);
		}

		if(z>EPSILON){
			fOfZ = Math.cbrt(z);
		}else{
			fOfZ = (((KAPPA*z)+16)/116);
		}		

		double L = (116*fOfY)-16;
		double a = 500*(fOfX - fOfY);
		double b = 200*(fOfY - fOfZ);

		Lab[0] = L;
		Lab[1] = a;
		Lab[2] = b;

		return Lab;
	}

	/**
	 * Converts from XYZ color space to equivalent xyY color space.
	 * @param XYZ, array representing a color.
	 * @return xyY, Array with the Lab equivalent value of the xyY received as a parameter.
	 */
	public static double[] XYZ2xyY(double[] XYZ){
		double denominator = XYZ[0] + XYZ[1] + XYZ[2];

		double x;
		double y;

		if(denominator != 0){
			x = XYZ[0] / denominator;
			y = XYZ [1] / denominator;
		}else{
			x = 1.0;
			y = 1.0;
		}

		double[] xyY = new double[3];

		xyY[0] = x;
		xyY[1] = y;
		xyY[2] = XYZ[1];

		return xyY;
	}

}
