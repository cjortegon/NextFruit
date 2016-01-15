package test.removebackground;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import org.opencv.core.Core;

import co.edu.icesi.frutificator.util.ImageUtility;
import visualkey.KCanvas;

public class Test extends KCanvas {

	private static final String PHOTO_PATH = "resources/strawberry.jpg";

	private Image image;
	private RemoveBackground remover;

	public static void main(String args[]) {
		new Test();
	}

	public Test() {
		super(new Dimension(500, 500));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);

		// Starting OpenCV
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		remover = new RemoveBackground(PHOTO_PATH);

		// Draw original image
		image = ImageUtility.mat2Image(remover.getOriginalImage());
		repaint();

		// Schedule to draw same image without background in 3 seconds
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
				}
				removeBackground();
			}
		}).start();
	}

	private void removeBackground() {
		remover.setInverse(true);
		image = ImageUtility.mat2Image(remover.doBackgroundRemoval(remover.getOriginalImage()));
		repaint();
	}

	@Override
	protected void paintCanvas(Graphics g) {
		g.drawImage(image, 0, 0, null);
	}

}
