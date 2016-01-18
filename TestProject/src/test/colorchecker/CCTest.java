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
	private static final Dimension WINDOW = new Dimension(1100, 600);

	private Image image;
	private ColorChecker colorChecker;

	public static void main(String args[]) {
		new CCTest();
	}

	public CCTest() {
		super(WINDOW);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);

		// Starting OpenCV
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		colorChecker = new ColorChecker(PHOTO_PATH, 30, 24);

		// Draw original image
		image = ImageUtility.mat2Image(colorChecker.getBGR());
		repaint();
	}

	@Override
	protected void paintCanvas(Graphics g) {
		try {
			double size[] = ImageUtility.getResize(image, WINDOW);
			g.drawImage(image, 0, 0, (int)size[0], (int)size[1], null);
			Point centers[] = colorChecker.getCenters();
			for (Point point : centers) {
				g.setColor(Color.black);
				g.drawRect((int)(point.x*size[2])-5, (int)(point.y*size[2])-5, 10, 10);
				g.setColor(Color.white);
				g.fillRect((int)(point.x*size[2])-4, (int)(point.y*size[2])-4, 9, 9);
			}
		} catch(NullPointerException npe) {}
	}
	
}
