package co.edu.icesi.nextfruit.modules.model;

public class CameraSettings {

	private double[][] matrix;
	private String iluminant;

	public CameraSettings(double[][] matrix, String iluminant) {
		this.matrix = matrix;
		this.iluminant = iluminant;
	}

	public double[][] getMatrix() {
		return matrix;
	}

	public void setMatrix(double[][] matrix) {
		this.matrix = matrix;
	}

	public String getIluminant() {
		return iluminant;
	}

	public void setIluminant(String iluminant) {
		this.iluminant = iluminant;
	}

}
