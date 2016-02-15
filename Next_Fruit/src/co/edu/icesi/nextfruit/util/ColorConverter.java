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
			
		double[][] matrixM = new double[][]{{0.4124564, 0.3575761, 0.1804375},
			{0.2126729, 0.7151522, 0.0721750},{0.0193339, 0.1191920, 0.9503041}};
		
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
	 * @return lab, Array with the Lab equivalent value of the xyz received as a parameter.
	 */
	public static double[] xyz2lab(double[] xyz) {
		double[] Lab = new double[3];

		//White Reference
		double XOfR = 0.95047;
		double YOfR = 1.00000;
		double ZOfR = 1.08883;

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

}
