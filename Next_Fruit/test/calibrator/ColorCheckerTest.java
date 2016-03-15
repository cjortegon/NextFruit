package calibrator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.Point;

import co.edu.icesi.nextfruit.modules.Constants;
import co.edu.icesi.nextfruit.modules.callibrator.ColorChecker;
import co.edu.icesi.nextfruit.modules.model.PolygonWrapper;
import co.edu.icesi.nextfruit.util.ImageUtility;
import visualkey.KCanvas;

public class ColorCheckerTest extends KCanvas {

	private static final String PHOTO_PATH = "resources/color_checker_1.jpg";
	private static final Dimension WINDOW = new Dimension(1100, 600);

	private Image image;
	private ColorChecker colorChecker;

	public static void main(String args[]) {
		new ColorCheckerTest();
	}

	public ColorCheckerTest() {
		super(WINDOW);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);

		// Starting OpenCV
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		colorChecker = new ColorChecker(PHOTO_PATH, 150, Constants.ORIGINALS);
		colorChecker.process(null);

		// Draw original image
		image = ImageUtility.mat2Image(colorChecker.getBGR());
		repaint();
	}

	@Override
	protected void paintCanvas(Graphics g) {
		try {
			double size[] = ImageUtility.drawImage(image, WINDOW, g);
			ArrayList<PolygonWrapper> boxes = colorChecker.getColorBoxes();
			for (PolygonWrapper box : boxes) {
				g.setColor(Color.black);
				g.drawRect((int)(box.getCenter().x*size[2])-5, (int)(box.getCenter().y*size[2])-5, 10, 10);
				g.setColor(Color.white);
				g.fillRect((int)(box.getCenter().x*size[2])-4, (int)(box.getCenter().y*size[2])-4, 9, 9);
				int[] xs = new int[box.getPolygon().length];
				int[] ys = new int[box.getPolygon().length];
				int i = 0;
				for (Point p : box.getPolygon()) {
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
