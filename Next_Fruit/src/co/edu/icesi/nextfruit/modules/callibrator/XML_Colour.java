package co.edu.icesi.nextfruit.modules.callibrator;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * This class represents an XML element of type Colour.
 * @author juadavcu
 *
 */
@XmlRootElement(name = "COLOUR")
@XmlAccessorType(XmlAccessType.FIELD)
public class XML_Colour {

	
	//
	//	Attributes
	//
	
	@XmlElement(name = "POSITION")
	private String position;
	
	@XmlElement(name = "RED")
	private int red;
	
	@XmlElement(name = "GREEN")
	private int green;
	
	@XmlElement(name = "BLUE")
	private int blue;
	
	
	
	//
	//	Constructors
	//
	
	public XML_Colour() {
		this.position = "Error";
		this.red = 0;
		this.green = 0;
		this.blue = 0;
	}
	
	public XML_Colour(String position, int red, int green, int blue) {
		this.position = position;
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	
	
	//
	//Access methods
	//
	
	public String getPosition() {
		return position;
	}

	public int getRed() {
		return red;
	}

	public int getGreen() {
		return green;
	}

	public int getBlue() {
		return blue;
	}
	
	
}
