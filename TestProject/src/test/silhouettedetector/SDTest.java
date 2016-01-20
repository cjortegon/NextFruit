package test.silhouettedetector;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import org.opencv.core.Core;

import co.edu.icesi.frutificator.util.ImageUtility;
import visualkey.KCanvas;

public class SDTest extends KCanvas {

	private static final String PHOTO_PATH = "resources/fresas.jpg";

	private Image image;
	private SilhouetteDetector colorChecker;

	public static void main(String args[]) {
		new SDTest();
	}

	public SDTest() {
		super(new Dimension(1440, 900));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);

		// Starting OpenCV
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		colorChecker = new SilhouetteDetector(PHOTO_PATH, 130, 255);

		// Draw original image
		image = ImageUtility.mat2Image(colorChecker.getProcessedImage());
		repaint();
	}

	@Override
	protected void paintCanvas(Graphics g) {
		g.drawImage(image, 0, 0, null);
	}

}
