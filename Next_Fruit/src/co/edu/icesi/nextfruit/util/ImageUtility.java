package co.edu.icesi.nextfruit.util;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

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

	public static double[] drawImage(Image image, Dimension windowSize, Graphics graphics) {
		double size[] = getResize(image, windowSize);
		graphics.drawImage(image, 0, 0, (int)size[0], (int)size[1], null);
		return size;
	}

	public static int bgr2rgb(double[] bgr) {
		int r = ((int)bgr[2]);
		int g = ((int)bgr[1]);
		int b = ((int)bgr[0]);
		return (r << 16) | (g << 8) | b;
	}
	
	public static double[] rgb2bgr(int rgb) {
		int b = rgb & 255;
		int g = (rgb & (255 << 8)) >> 8;
		int r = (rgb & (255 << 16)) >> 16;
		return new double[]{b, g, r};
	}
}
