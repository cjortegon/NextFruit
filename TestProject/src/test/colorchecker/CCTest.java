package test.colorchecker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.Point;

import co.edu.icesi.frutificator.util.ImageUtility;
import test.Constants;
import visualkey.KCanvas;

public class CCTest extends KCanvas {

	private static final String PHOTO_PATH = "resources/color_checker_1.jpg";
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
		colorChecker = new ColorChecker(PHOTO_PATH, 150, Constants.ORIGINALS);

		// Draw original image
		image = ImageUtility.mat2Image(colorChecker.getBGR());
		repaint();
	}

	@Override
	protected void paintCanvas(Graphics g) {
		try {
			double size[] = ImageUtility.drawImage(image, WINDOW, g);
			ArrayList<ColorBox> boxes = colorChecker.getColorBoxes();
			for (ColorBox box : boxes) {
				g.setColor(Color.black);
				g.drawRect((int)(box.getCenter().x*size[2])-5, (int)(box.getCenter().y*size[2])-5, 10, 10);
				g.setColor(Color.white);
				g.fillRect((int)(box.getCenter().x*size[2])-4, (int)(box.getCenter().y*size[2])-4, 9, 9);
				int[] xs = new int[box.getBox().length];
				int[] ys = new int[box.getBox().length];
				int i = 0;
				for (Point p : box.getBox()) {
					xs[i] = (int)(p.x*size[2]);
					ys[i] = (int)(p.y*size[2]);
					i ++;
				}
				g.setColor(Color.green);
				g.drawPolygon(xs, ys, xs.length);
			}
		} catch(NullPointerException npe) {}
	}
	
}
