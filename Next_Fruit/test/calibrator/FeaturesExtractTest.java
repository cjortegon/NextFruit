package calibrator;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import org.opencv.core.Core;
import org.opencv.imgcodecs.Imgcodecs;

import co.edu.icesi.nextfruit.modules.computervision.FeaturesExtract;
import co.edu.icesi.nextfruit.util.ImageUtility;
import visualkey.KCanvas;

public class FeaturesExtractTest extends KCanvas {
	
//	private static final String BACKGROUND_IMAGE = "for_test/background2.jpg";
//	private static final String OBJECT_IMAGE = "for_test/to_remove_back.jpg";
	private static final String BACKGROUND_IMAGE = "for_test/patineta_bg.jpg";
	private static final String OBJECT_IMAGE = "for_test/patineta.jpg";
	private static final Dimension WINDOW = new Dimension(1100, 600);

	private Image image;
	private FeaturesExtract featuresExtract;

	public static void main(String args[]) {
		new FeaturesExtractTest();
	}

	public FeaturesExtractTest() {
		super(WINDOW);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);

		// Starting OpenCV
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		// Load
		featuresExtract = new FeaturesExtract(BACKGROUND_IMAGE);

		// Draw original image
//		image = ImageUtility.mat2Image(featuresExtract.removeBackground(Imgcodecs.imread(OBJECT_IMAGE)));
		repaint();
	}

	@Override
	protected void paintCanvas(Graphics g) {
		try {
			double size[] = ImageUtility.drawImage(image, WINDOW, g);
		} catch(NullPointerException npe) {}
	}

}
