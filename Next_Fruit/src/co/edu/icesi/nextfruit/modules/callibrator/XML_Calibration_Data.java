package co.edu.icesi.nextfruit.modules.callibrator;
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
public class XML_Calibration_Data {

	
	//
	//	Attributes
	//
	
	@XmlElement(name = "PIXELSXCM")
	private double pixelsxCm;
	
	@XmlElement(name = "COLOURS_ROW")
	private XML_Colour[][] colours;
	
	
	
	//
	//	Constructor
	//
	
	public XML_Calibration_Data(){
		this.colours = new XML_Colour[4][6];
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
	public void addColour(XML_Colour colour, int posHor, int posVer){
		this.colours[posHor][posVer] = colour;
	}
	
	
	public double getPixels(){
		return pixelsxCm;
	}
	
	public XML_Colour[][] getColours(){
		return colours;
	}

	public void setPixelsxCm(double pixelsxCm) {
		this.pixelsxCm = pixelsxCm;
	}
	
	
}
