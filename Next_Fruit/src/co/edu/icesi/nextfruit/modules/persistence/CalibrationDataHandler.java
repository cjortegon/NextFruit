package co.edu.icesi.nextfruit.modules.persistence;
import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * This class handles persistence for the camera calibration data.
 * @author juadavcu
 *
 */
public class CalibrationDataHandler {

	
	//
	//	Attributes
	//
	private JAXBContext context;
	private Marshaller marshaller;
	private Unmarshaller unmarshaller;
	private double pixelsxCm;
	private XMLColour[][] colours;
	
	
	
	//
	//	Constructors
	//
	
	public CalibrationDataHandler(){
		
		this.context = null;
		this.marshaller = null;
		this.unmarshaller = null;
		this.pixelsxCm = 0;
		this.colours = null;
		
	}
	
	
	
	//
	//	Methods
	//
	
	/**
	 * This method saves the particular calibration data, of a given camera, as an XML file in disk.
	 * @param file File object with the information about the XML file to save.
	 * @param rgbs array containing the information about the rgb colors of each box in a colorchecker.
	 * @throws JAXBException
	 */
	public void saveCalibrationData(File file, int[][][] rgbs, double pixxCm) throws JAXBException{
	
		context = JAXBContext.newInstance(XMLCalibrationData.class, XMLColour.class);
		marshaller = context.createMarshaller();
		
		XMLCalibrationData calibrationDataXML = new XMLCalibrationData();
		
		calibrationDataXML.setPixelsxCm(pixxCm);
		
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 6; j++){
				
				calibrationDataXML.addColour(new XMLColour(i + "," + j, rgbs[i][j][0], rgbs[i][j][1], rgbs[i][j][2]), 
						i, j);
			
			}
		}
		
		
		marshaller.marshal(calibrationDataXML, file);
		
	}
	
	
	
	/**
	 * This method loads, to the application, the calibration data of a particular camera,
	 * saved as an XML file in disk.
	 * @param file File object with the information about the XML file to read.
	 * @throws JAXBException
	 */
	public void loadCalibrationData(File file) throws JAXBException{
		
		context = JAXBContext.newInstance(XMLCalibrationData.class, XMLColour.class);
		unmarshaller = context.createUnmarshaller();
		
		XMLCalibrationData calibrationDataXML = (XMLCalibrationData) unmarshaller.unmarshal(file);
		
		pixelsxCm = calibrationDataXML.getPixels();
		colours = calibrationDataXML.getColours();
		
		this.showTest();
		
	}


	/**
	 * Print the matrix using the console
	 */
	public void showTest(){
		
		System.out.println(pixelsxCm);
		
		for (XMLColour[] row : colours) {
			
			for (XMLColour colour : row) {
				System.out.print("r:" + colour.getRed() + ", g:" + colour.getGreen() + ", b:" + colour.getBlue() + " -- ");
			}
			
			System.out.println("");
			
		}
	}
	
	
}