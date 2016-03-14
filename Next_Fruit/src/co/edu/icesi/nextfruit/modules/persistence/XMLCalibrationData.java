package co.edu.icesi.nextfruit.modules.persistence;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * This class represents an XML element of type Calibration data.
 * @author juadavcu
 *
 */
@XmlRootElement(name = "CALIBRATION_DATA")
@XmlAccessorType(XmlAccessType.FIELD)
public class XMLCalibrationData {
	
	//
	//	Attributes
	//
	
	@XmlElement(name = "PIXELSXCM")
	private double pixelsxCm;
	
	@XmlElement(name = "ILLUMINANT")
	private String illuminant;
	
	@XmlElement(name = "WHITE_X")
	private double whiteX;
	
	@XmlElement(name = "WHITE_Y")
	private double whiteY;
	
	@XmlElement(name = "WHITE_Z")
	private double whiteZ;
	
	@XmlElement(name = "M_MATRIZ_ROW")
	private double[][] workingSpaceMatrix;
	
	@XmlElement(name = "COLOURS_ROW")
	private XMLColour[][] colours;
	
	//
	//	Constructor
	//
	public XMLCalibrationData(){
		this.colours = new XMLColour[4][6];
		this.workingSpaceMatrix = new double[3][3];
	}
	
	//
	//Access methods
	//
	
	/**
	 * Adds a Colour which represents a square of a color checker
	 * @param colour XML_Colour object
	 * @param posHor index i
	 * @param posVer index j
	 */
	public void addColour(XMLColour colour, int posHor, int posVer){
		this.colours[posHor][posVer] = colour;
	}
	
	public double getPixels(){
		return pixelsxCm;
	}
	
	public XMLColour[][] getColours(){
		return colours;
	}

	public void setPixelsxCm(double pixelsxCm) {
		this.pixelsxCm = pixelsxCm;
	}

	public String getIlluminant() {
		return illuminant;
	}

	public double[][] getWorkingSpaceMatrix() {
		return workingSpaceMatrix;
	}

	public void setIlluminant(String illuminant) {
		this.illuminant = illuminant;
	}

	public void setWorkingSpaceMatrix(double[][] workingSpaceMatrix) {
		this.workingSpaceMatrix = workingSpaceMatrix;
	}

	public void setWhiteX(double whiteX) {
		this.whiteX = whiteX;
	}

	public void setWhiteY(double whiteY) {
		this.whiteY = whiteY;
	}

	public void setWhiteZ(double whiteZ) {
		this.whiteZ = whiteZ;
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
