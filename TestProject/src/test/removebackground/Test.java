package test.removebackground;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.opencv.core.Core;

import visualkey.KCanvas;

public class Test extends KCanvas {
	
	private Image image;
	
	public static void main(String args[]) {
		new Test();
	}

	public Test() {
		super(new Dimension(500, 500));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		try {
			image = ImageIO.read(new File("resources/example.jpg"));
			RemoveBackground rb = new RemoveBackground(image);
			image = rb.getImage();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		repaint();
	}
	
	@Override
	protected void paintCanvas(Graphics g) {
		g.drawImage(image, 0, 0, null);
	}
	
}
