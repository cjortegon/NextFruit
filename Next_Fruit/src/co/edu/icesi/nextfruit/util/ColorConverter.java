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
		
		double red = rgb[0]/255.0;
		double green = rgb[1]/255.0;
		double blue = rgb[2]/255.0;

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
	//	public static double[] rgb2xyz(double[] rgb) {
	//		double red = rgb[0];
	//		double green = rgb[1];
	//		double blue = rgb[2];
	//
	//		if(red > 0.04045){
	//			red = Math.pow(((red + 0.055)/1.055), 2.4);
	//		}else{
	//			red = red/12.92;
	//		}
	//		if(green > 0.04045){
	//			green = Math.pow((green + 0.055)/1.055, 2.4);
	//		}else{
	//			green = green/12.92;
	//		}
	//		if(blue > 0.04045){
	//			blue = Math.pow((blue + 0.055)/1.055, 2.4);
	//		}else{
	//			blue = blue/12.92;
	//		}
	//
	//		double[][] matrixM = new double[][] {
	//			{0.4124564, 0.3575761, 0.1804375},
	//			{0.2126729, 0.7151522, 0.0721750},
	//			{0.0193339, 0.1191920, 0.9503041}
	//		};
	//
	//		double[] xyz = new double[3];
	//
	//		xyz[0] = (((matrixM[0][0])*red)+((matrixM[0][1])*green)+(matrixM[0][2]*blue));
	//		xyz[1] = (((matrixM[1][0])*red)+((matrixM[1][1])*green)+(matrixM[1][2]*blue));
	//		xyz[2] = (((matrixM[2][0])*red)+((matrixM[2][1])*green)+(matrixM[2][2]*blue));
	//
	//		return xyz;
	//	}

	/**
	 * Converts from XYZ color space to equivalent Lab color space.
	 * Color must be arranged in [X,Y,Z] order.
	 * @param xyz, array representing a color.
	 * @param illuminant, string
	 * @return lab, Array with the Lab equivalent value of the xyz received as a parameter.
	 */
	public static double[] xyz2lab(double[] xyz, double whiteX, double whiteY, double whiteZ) {
		double[] Lab = new double[3];

		//White Reference by defalut
		double XOfR = whiteX;
		double YOfR = whiteY;
		double ZOfR = whiteZ;

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
	 * @return xyY, Array with the xyY equivalent value of the XYZ received as a parameter.
	 */
	public static double[] XYZ2xyY(double[] XYZ, double whiteX){
		double denominator = XYZ[0] + XYZ[1] + XYZ[2];

		double x;
		double y;

		if(denominator != 0){
			x = XYZ[0] / denominator;
			y = XYZ [1] / denominator;
		}else{
			x = whiteX;
			y = 1.00000;
		}

		double[] xyY = new double[3];

		xyY[0] = x;
		xyY[1] = y;
		xyY[2] = XYZ[1];

		return xyY;
	}

	public static double[] rgb2xyY(int rgb, double[][] matrixM, double whiteX) {
		return XYZ2xyY(rgb2xyz(reverseColor(rgb2bgr(rgb)), matrixM), whiteX);
	}

	/**
	 * Changes the order of RGB to BGR and vice versa according to different implementations
	 * @param color
	 * @return
	 */
	public static double[] reverseColor(double[] color) {
		return new double[]{color[2], color[1], color[0]};
	}

	/**
	 * Groups bgr values into rgb integer
	 * @param bgr: color to be converted
	 * @return rgb as integer value
	 */
	public static int bgr2rgb(double[] bgr) {
		int r = ((int)bgr[2]);
		int g = ((int)bgr[1]);
		int b = ((int)bgr[0]);
		return (r << 16) | (g << 8) | b;
	}

	/**
	 * Splits rgb integer into bgr values
	 * @param rgb: color to be converted
	 * @return bgr values
	 */
	public static double[] rgb2bgr(int rgb) {
		int b = rgb & 255;
		int g = (rgb & (255 << 8)) >> 8;
		int r = (rgb & (255 << 16)) >> 16;
		return new double[]{b, g, r};
	}

	/**
	 * Converts from xyY color space to equivalent RGB color space.
	 * @param xyY, array representing a color.
	 * @param inverseM, the inverse M matrix to use in the conversion.
	 * @return RGB, Array with the RGB equivalent value of the XYZ received as
	 * a parameter. 
	 *  where: 	BGR[0] = blue component.
	 * 			BGR[1] = green component.
	 * 			BGR[2] = red component.
	 */
	public static double[] xyY2bgr(double[] xyY, double[][] inverseM) {
		double[] xyz = xyY2XYZ(xyY);
		double[] bgr = xyz2bgr(xyz, inverseM);
		return driveToComputerScale(bgr);
	}
	
	public static double[] driveToComputerScale(double bgr[]) {
		for (int i = 0; i < bgr.length; i++) {
			bgr[i] = bgr[i] < 0 ? 0 : bgr[i] > 1 ? 255 : bgr[i]*255;
		}
		return bgr;
	}
	
	/**
	 * Converts from xyY color space to equivalent XYZ color space.
	 * @param xyY, array representing a color.
	 * @return XYZ, Array with the XYZ equivalent value of the xyY received as a parameter.
	 */
	public static double[] xyY2XYZ(double[] xyY){
		double X;
		double Z;
		
		double x = xyY[0];
		double y = xyY[1];
		double Y = xyY[2];
		
		X = (x*Y) / y;
		Z = ((1-x-y)*Y) / y;
		
		if(y == 0){
			X = 0;
			Y = 0;
			Z = 0;
		}
		
		double[] returnValue = {X, Y, Z};
		return returnValue;
	}
	
	
	/**
	 * Converts from xyz color space to equivalent RGB color space. 
	 * @param xyz, XYZ, array representing a color.
	 * @param inverseM, the inverse M matrix to use in the conversion.
	 * @return RGB, Array with the RGB equivalent value of the XYZ received as
	 * a parameter. 
	 *  where: 	BGR[0] = blue component.
	 * 			BGR[1] = green component.
	 * 			BGR[2] = red component.
	 */
	public static double[] xyz2bgr(double[] xyz, double[][] inverseM){
		
		double X = xyz[0];
		double Y = xyz[1];
		double Z = xyz[2];
		
		double[] RGB = new double[3];

		RGB[0] = (((inverseM[0][0])*X)+((inverseM[0][1])*Y)+(inverseM[0][2]*Z));
		RGB[1] = (((inverseM[1][0])*X)+((inverseM[1][1])*Y)+(inverseM[1][2]*Z));
		RGB[2] = (((inverseM[2][0])*X)+((inverseM[2][1])*Y)+(inverseM[2][2]*Z));

		//	Red
		if(RGB[0] > 0.0031308){
			RGB[0] = (1.055 * Math.pow(RGB[0], 1 / 2.4)) - 0.055;
		}else{
			RGB[0] = 12.92 * RGB[0];
		}
		
		//	Green
		if(RGB[1] > 0.0031308){
			RGB[1] = (1.055 * Math.pow(RGB[1], 1 / 2.4)) - 0.055;
		}else{
			RGB[1] = 12.92 * RGB[1];
		}
		
		//	Blue
		if(RGB[2] > 0.0031308){
			RGB[2] = (1.055 * Math.pow(RGB[2], 1 / 2.4)) - 0.055;
		}else{
			RGB[2] = 12.92 * RGB[2];
		}
		
		double[] BGR = {RGB[2]*255, RGB[1]*255, RGB[0]*255};
		return BGR;
	}

}
