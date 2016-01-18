package test.colorchecker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import org.opencv.core.Core;
import org.opencv.core.Point;

import co.edu.icesi.frutificator.util.ImageUtility;
import visualkey.KCanvas;

public class CCTest extends KCanvas {

	private static final String PHOTO_PATH = "resources/original.png";

	private Image image;
	private ColorChecker colorChecker;

	public static void main(String args[]) {
		new CCTest();
	}

	public CCTest() {
		super(new Dimension(1440, 900));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);

		// Starting OpenCV
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		colorChecker = new ColorChecker(PHOTO_PATH);

		// Draw original image
		image = ImageUtility.mat2Image(colorChecker.getOriginalImage());
		repaint();
	}

	@Override
	protected void paintCanvas(Graphics g) {
		try {
			g.drawImage(image, 0, 0, null);
			Point centers[] = colorChecker.getCenters();
			for (Point point : centers) {
				g.setColor(Color.black);
				g.drawRect((int)point.x-5, (int)point.y-5, 10, 10);
				g.setColor(Color.white);
				g.fillRect((int)point.x-4, (int)point.y-4, 9, 9);
			}
		} catch(NullPointerException npe) {}
	}

}
