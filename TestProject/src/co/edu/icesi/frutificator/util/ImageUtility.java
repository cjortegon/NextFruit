package co.edu.icesi.frutificator.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

public class ImageUtility {

	public static BufferedImage mat2Image(Mat mat) {

		// Create a temporary buffer
		MatOfByte buffer = new MatOfByte();

		// Encode the frame in the buffer, according to the PNG format
		Imgcodecs.imencode(".png", mat, buffer);

		// Build and return an Image created from the image encoded in the buffer
		BufferedImage image = null;
		try {
			image = ImageIO.read(new ByteArrayInputStream(buffer.toArray()));
		} catch (IOException e) {
		}
		return image;
	}

}
