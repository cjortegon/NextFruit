package calibrator;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import org.opencv.core.Core;
import org.opencv.core.Point;

import co.edu.icesi.nextfruit.modules.callibrator.SizeCalibrator;
import co.edu.icesi.nextfruit.util.ImageUtility;
import visualkey.KCanvas;

public class SizeCalibrationTest extends KCanvas {

	private static final String PHOTO_PATH = "resources/foto_cuadricula.jpg";
	private static final Dimension WINDOW = new Dimension(1100, 600);

	private Image image;
	private SizeCalibrator sizeCalibrator;

	public static void main(String args[]) {
		new SizeCalibrationTest();
	}

	public SizeCalibrationTest() {
		super(WINDOW);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);

		// Starting OpenCV
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		// Process imag
		sizeCalibrator = new SizeCalibrator(PHOTO_PATH, 6, 9, 2.3);
		System.out.println("Pixels for each 2.3cm: "+sizeCalibrator.getPixelsBetweenIntersections());
		System.out.println("Pixels for cm: "+sizeCalibrator.getPixelsForCentimeter());

		// Draw original image
		image = ImageUtility.mat2Image(sizeCalibrator.getImage());
		repaint();
	}

	@Override
	protected void paintCanvas(Graphics g) {
		if(image != null) {
			double size[] = ImageUtility.drawImage(image, WINDOW, g);
			for (Point p : sizeCalibrator.getPoints()) {
				int x = (int)(p.x*size[2]);
				int y = (int)(p.y*size[2]);
				g.fillRect(x-5, y-5, 10, 10);
			}
		}
	}

}
