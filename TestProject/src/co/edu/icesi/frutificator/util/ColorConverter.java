package co.edu.icesi.frutificator.util;


/**
 * Clase que convierte los valores de un espacio de color a otro
 * @author JuanD
 *
 */
public class ColorConverter {
		
	private static final double EPSILON = 216/24389;
	private static final double KAPPA = 24389/27;
	
	
	/**
	 * Método que recibe un arreglo que representa un color en el espacio RGB, 
	 * y retorna un arreglo de su valor equivalente en el espacio XYZ
	 * @param rgb
	 * @return
	 */
	public static double[] rgb2xyz(double[] rgb) {
		double red = rgb[0];
		double green = rgb[1];
		double blue = rgb[2];
		
		double[][] M = new double[3][3]; 
		
		M[0][0] = 0.5767309;
		M[0][1] = 0.1855540;
		M[0][2] = 0.1881852;
		
		M[1][0] = 0.2973769;
		M[1][1] = 0.6273491;
		M[1][2] = 0.0752741;
		
		M[2][0] = 0.0270343;
		M[2][1] = 0.0706872;
		M[2][2] = 0.9911085;
		
		double[] xyz = new double[3];
		
		xyz[0] = (((M[0][0])*red)+((M[0][1])*green)+(M[0][2]*blue));
		xyz[1] = (((M[1][0])*red)+((M[1][1])*green)+(M[1][2]*blue));
		xyz[2] = (((M[2][0])*red)+((M[2][1])*green)+(M[2][2]*blue));
		
		return xyz;
	}
	
	
	/**
	 * Método que recibe un arreglo que representa un color en el espacio XYZ, 
	 * y retorna un arreglo de su valor equivalente en el espacio Lab
	 * @param xyz
	 * @return
	 */
	public static double[] xyz2lab(double[] xyz) {
		double[] Lab = new double[3];
		
		/*
		 * De momento x, y e z sub r se dejan con un valor de 1.
		 * Averiguar y corregir el valor verdadero cuando sepamos
		 * a que se refieren con:
		 * ¡This conversion requires a reference white Xr, Yr, Zr!
		 */
		double XOfR = 1;
		double YOfR = 1;
		double ZOfR = 1;
		
		double x = xyz[0]/XOfR;
		double y = xyz[1]/YOfR;
		double z = xyz[2]/ZOfR;
		
		//functions
		double fOfX = 0.0;
		double fOfY = 0.0;
		double fOfZ = 0.0;
		
		//Calculate the values of the functions
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
