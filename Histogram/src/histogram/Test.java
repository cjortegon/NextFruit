package histogram;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import org.opencv.core.Core;

import co.edu.icesi.frutificator.util.ImageUtility;
import visualkey.KCanvas;

public class Test extends KCanvas {

	private static final String PHOTO_PATH = "resources/imagen3.bmp";
	private static final Dimension WINDOW = new Dimension(768, 500);

	private Histogram histogram;
	private Image image;

	public static void main(String args[]) {
		new Test();
	}

	public Test() {
		super(WINDOW);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("Histogram");
		setVisible(true);

		// Starting OpenCV
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		histogram = new Histogram(PHOTO_PATH);
		histogram.readHistogram(true);
		histogram.statisticalSmothHistogram();
		histogram.smoothFillHistogram();
		
		// Convert color format
		histogram.convertToCromaticScale();
		
		// Removing one part of the image
//		histogram.removeGrayRegion(30, 800, new double[]{255, 255, 255});
		histogram.filterFigureByColorProfile(new double[]{117.5, 66, 38.5}, new double[]{113, 80, 77}, new double[]{255, 255, 255}, null);

		// Draw original image
		image = ImageUtility.mat2Image(histogram.getImage());
		new KCanvas(new Dimension(image.getWidth(null), image.getHeight(null))) {
			@Override
			protected void paintCanvas(Graphics g) {
				g.drawImage(image, 0, 0, null);
			}
		}.setVisible(true);

		// Draw histogram
		repaint();
	}

	@Override
	protected void paintCanvas(Graphics g) {
		if(histogram != null) {
			int hist[] = histogram.getHistogram();
			int red[] = histogram.getRedHistogram();
			int green[] = histogram.getGreenHistogram();
			int blue[] = histogram.getBlueHistogram();
			int max = histogram.getMaxHeight();
			for (int i = 0; i < hist.length; i++) {
				int bar = (int) ((hist[i]/((double)max))*WINDOW.height);
				g.setColor(Color.black);
				g.drawLine(i, 500-bar, i, 500);
				
//				// Red
//				g.setColor(new Color(255, 0, 0, 128));
//				bar = (int) ((red[i/3]/((double)max))*WINDOW.height);
//				g.drawLine(i, 500-bar, i, 500);
//				
//				// Green
//				g.setColor(new Color(0, 255, 0, 128));
//				bar = (int) ((green[i]/((double)max))*WINDOW.height);
//				g.drawLine(i, 500-bar, i, 500);
//				
//				// Blue
//				g.setColor(new Color(0, 0, 255, 128));
//				bar = (int) ((blue[i]/((double)max))*WINDOW.height);
//				g.drawLine(i, 500-bar, i, 500);
			}
		}
	}

}
