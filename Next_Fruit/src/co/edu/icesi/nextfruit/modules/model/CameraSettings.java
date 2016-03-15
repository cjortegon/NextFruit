package co.edu.icesi.nextfruit.modules.model;

public class CameraSettings {

	/**
	 * Matrix that represents the working space matrix M used.
	 */
	private double[][] WorkingSpaceMatrix;
	/**
	 * Value that represents the illuminant used (e.g. D65, D55, etc).
	 */
	private String illuminant;
	/**
	 * White point reference values
	 */
	private double whiteX;
	private double whiteY;
	private double whiteZ;


	public CameraSettings(double[][] matrix, String illuminant) {
		this.WorkingSpaceMatrix = matrix;
		this.illuminant = illuminant;
		initializeWhitePoints();
	}

	/**
	 * Initialize the white point reference values x, y and z.
	 */
	private void initializeWhitePoints(){
		this.whiteY = 1.00000;

		switch (illuminant) {

		case "A":
			this.whiteX = 1.09850;
			this.whiteZ = 0.35585;
			break;

		case "B":
			this.whiteX = 0.99072;
			this.whiteZ = 0.85223;
			break;

		case "C":
			this.whiteX = 0.98074;
			this.whiteZ = 1.18232;
			break;

		case "D50":
			this.whiteX = 0.96422;
			this.whiteZ = 0.82521;
			break;

		case "D55":
			this.whiteX = 0.95682;
			this.whiteZ = 0.92149;
			break;

		case "D65":
			this.whiteX = 0.95047;
			this.whiteZ = 1.08883;
			break;

		case "D75":
			this.whiteX = 0.94972;
			this.whiteZ = 1.22638;
			break;

		case "E":
			this.whiteX = 1.00000;
			this.whiteZ = 1.00000;
			break;

		case "F2":
			this.whiteX = 0.99186;
			this.whiteZ = 0.67393;
			break;

		case "F7":
			this.whiteX = 0.95041;
			this.whiteZ = 1.08747;
			break;

		case "F11":
			this.whiteX = 1.00962;
			this.whiteZ = 0.64350;
			break;

		default:
			this.whiteX = 1.00000;
			this.whiteZ = 1.00000;
			break;

		}

	}

	//	****************** Access methods ******************
	
	public double[][] getWorkingSpaceMatrix() {
		return WorkingSpaceMatrix;
	}

	public void setWorkingSpaceMatrix(double[][] workingSpaceMatrix) {
		WorkingSpaceMatrix = workingSpaceMatrix;
	}

	public String getIlluminant() {
		return illuminant;
	}

	public void setIluminant(String iluminant) {
		this.illuminant = iluminant;
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

}
