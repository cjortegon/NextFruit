package co.edu.icesi.nextfruit.modules.model;

import co.edu.icesi.nextfruit.modules.persistence.XMLColour;
import org.jblas.DoubleMatrix;


/**
 * This class contains the calibration information associated with a camera, and is loaded
 * from an XML file containing said information.
 * @author JuanD
 *
 */
public class CameraCalibration {

	/**
	 * Matrix that represents the RGB values of every ColorBox in a color checker.
	 */
	private XMLColour[][] RGBValues;
	/**
	 * Value that represents the relation between graphical pixels and physical centimeters.
	 */
	private double pixelsXCm;
	/**
	 * Value that represents the illuminant used (e.g. D65, D55, etc).
	 */
	private String illuminant;
	/**
	 * Matrix that represents the working space matrix M used.
	 */
	private double[][] workingSpaceMatrix;
	/**
	 * Matrix that represents the working space inverse matrix M' used.
	 */
	private double[][] inverseWorkingSpaceMatrix;
	/**
	 * White point reference values
	 */
	private double whiteX;
	private double whiteY;
	private double whiteZ;

	public CameraCalibration(XMLColour[][] rGBValues, double pixelsXCm, double[][] workingSpaceMatrix, double whiteX,
			double whiteY, double whiteZ, String illuminant) {
		this.RGBValues = rGBValues;
		this.pixelsXCm = pixelsXCm;
		this.workingSpaceMatrix = workingSpaceMatrix;
		this.whiteX = whiteX;
		this.whiteY = whiteY;
		this.whiteZ = whiteZ;
		this.illuminant = illuminant;
		
		initializeInverseWorkingSpaceMatrix();
	}

	public void imprimirPrueba(){

		System.out.println("pixelsxcm -> " + pixelsXCm);
		System.out.println("illuminant -> " + illuminant);
		System.out.println("whiteX -> " + whiteX);
		System.out.println("whiteY -> " + whiteY);
		System.out.println("whiteZ -> " + whiteZ);

		System.out.println("Matriz M: ");
		for (double[] row : workingSpaceMatrix) {
			for (double colour : row) {
				System.out.print("(" + colour + ") ");
			}
			System.out.println("");
		}

		System.out.println("Matriz ColorChecker RGB: ");
		for (XMLColour[] row : RGBValues) {
			for (XMLColour colour : row) {
				System.out.print("(r:" + colour.getRed() + ", g:" + colour.getGreen() + ", b:" + colour.getBlue() + ")  ");
			}
			System.out.println("");
		}
	}

	public XMLColour[][] getRGBValues() {
		return RGBValues;
	}

	public double getPixelsXCm() {
		return pixelsXCm;
	}

	public double[][] getWorkingSpaceMatrix() {
		return workingSpaceMatrix;
	}

	public double getWhiteX() {
		return whiteX;
	}

	public double getWhiteY() {
		return whiteY;
	}

	public double getWhiteZ() {
		return whiteZ;
	}	

	public double[][] getInverseWorkingSpaceMatrix() {
		return inverseWorkingSpaceMatrix;
	}

	
	/**
	 * Initialize the parameter inverseWorkingSpaceMatrix, by calculating the
	 * inverse of the matrix workingSpaceMatrix.
	 */
	private void initializeInverseWorkingSpaceMatrix(){
		if(workingSpaceMatrix != null){
			DoubleMatrix originalMatrix = new DoubleMatrix(workingSpaceMatrix);
			DoubleMatrix inverseMatrix = org.jblas.Solve.pinv(originalMatrix);			

			double[][] temp = {
		 			{inverseMatrix.get(0), inverseMatrix.get(3), inverseMatrix.get(6)},
		 			{inverseMatrix.get(1), inverseMatrix.get(4), inverseMatrix.get(7)},
		 			{inverseMatrix.get(2), inverseMatrix.get(5), inverseMatrix.get(8)}};
			
			inverseWorkingSpaceMatrix = temp;
			
		}else{
			inverseWorkingSpaceMatrix = null;
		}
	}
}
