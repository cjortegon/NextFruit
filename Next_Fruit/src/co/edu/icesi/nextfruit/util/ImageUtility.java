package co.edu.icesi.nextfruit.util;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

import org.opencv.core.Mat;

import co.edu.icesi.nextfruit.modules.model.PolygonWrapper;

/**
 * This class converts between the different object format of images in java used in this application. And also helps to draw the image in a given canvas.
 * @author cjortegon
 */
public class ImageUtility {

	public static BufferedImage mat2Image(Mat mat) {
		int type = 0;
		if (mat.channels() == 1) {
			type = BufferedImage.TYPE_BYTE_GRAY;
		} else if (mat.channels() == 3) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		BufferedImage image = new BufferedImage(mat.width(), mat.height(), type);
		WritableRaster raster = image.getRaster();
		DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
		byte[] data = dataBuffer.getData();
		mat.get(0, 0, data);
		return image;
	}

	/**
	 * @param image the source image
	 * @param windowSize maximum space to fit the image
	 * @return {width, height, used scale}
	 */
	public static double[] getResize(Image image, Dimension windowSize) {
		double w = image.getWidth(null);
		double h = image.getHeight(null);
		double f = 1;
		double fw = w/windowSize.getWidth();
		double fh = h/windowSize.getHeight();
		if(fw > 1 || fh > 1) {
			if(fh > fw) {
				w /= fh;
				h = windowSize.getHeight();
				f = fh;
			} else {
				w = windowSize.getWidth();
				h /= fw;
				f = fw;
			}
		}
		return new double[]{w, h, 1/f};
	}

	/**
	 * Draws image in the desired window size
	 * @param image to be drawn
	 * @param windowSize size of the canvas
	 * @param graphics graphic object to draw
	 * @return the image size that was drawn
	 */
	public static double[] drawImage(Image image, Dimension windowSize, Graphics graphics) {
		double size[] = getResize(image, windowSize);
		graphics.drawImage(image, 0, 0, (int)size[0], (int)size[1], null);
		return size;
	}

	/**
	 * Draws an image centered to a specific region
	 * @param image to be drawn
	 * @param windowSize size of the canvas
	 * @param graphics graphic object to draw
	 * @param polygon region of the image to focus
	 * @return the start (x,y) point [0] and [1] where the drawing started in the source image and the factor [2] used to achieve the scale the drawing
	 */
	public static double[] drawCenteredImage(Image image, Dimension windowSize, Graphics graphics, PolygonWrapper polygon) {
		double xStart = polygon.getLeft();
		double yStart = polygon.getTop();
		double xEnd = polygon.getRight();
		double yEnd = polygon.getBottom();
		double w = xEnd - xStart;
		double h = yEnd - yStart;
		double factor = Double.min(windowSize.getWidth()/w, windowSize.getHeight()/h);
		double calculatedW = windowSize.getWidth()/factor;
		double calculatedH = windowSize.getHeight()/factor;
		if(w < calculatedW) {
			double dif = (calculatedW - w)/2;
			xStart -= dif;
			xEnd += dif;
		}
		if(h < calculatedH) {
			double dif = (calculatedH - h)/2;
			yStart -= dif;
			yEnd += dif;
		}
		graphics.drawImage(image, 0, 0, windowSize.width, windowSize.height, (int)xStart, (int)yStart, (int)xEnd, (int)yEnd, null);
		return new double[]{xStart, yStart, factor};
	}

}
