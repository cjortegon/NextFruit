package test.contourmanipulation;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import org.opencv.core.Core;
import org.opencv.core.Mat;

import co.edu.icesi.frutificator.util.ImageUtility;
import visualkey.KCanvas;

public class ContourManipulatorTest extends KCanvas {

	private static final String PHOTO_PATH = "resources/p3.jpg";
	private static final Dimension WINDOW = new Dimension(1100, 600);

	private Image image;
	private ContourManipulator contourManipulator;

	public static void main(String args[]) {
		new ContourManipulatorTest();
	}

	public ContourManipulatorTest() {
		super(WINDOW);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);

		// Starting OpenCV
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		contourManipulator = new ContourManipulator(PHOTO_PATH);
		contourManipulator.centerImage();
		Mat imagen = contourManipulator.getImage();
		
		// Draw original image
		image = ImageUtility.mat2Image(imagen);
		this.setSize(imagen.width()+100, imagen.height()+100);
		this.setLocationRelativeTo(null);
		repaint();
	}

	@Override
	protected void paintCanvas(Graphics g) {
		try {
			double size[] = ImageUtility.drawImage(image, WINDOW, g);
		} catch(NullPointerException npe) {}
	}
	
}
