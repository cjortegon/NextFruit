package co.edu.icesi.nextfruit.views.subviews;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import org.opencv.core.Point;

import co.edu.icesi.nextfruit.modules.Model;
import co.edu.icesi.nextfruit.modules.model.PolygonWrapper;
import co.edu.icesi.nextfruit.util.ImageUtility;
import visualkey.KPanel;

public class ImageCanvas extends KPanel {

	private Model model;
	private Image loadedImage;
	private double[] drawingConstrains;

	public ImageCanvas(Dimension canvasSize, Model model) {
		super(canvasSize);
		this.model = model;
	}

	public void paintComponent(Graphics g) {
		if(loadedImage != null) {
			PolygonWrapper border = model.getFeaturesExtract().getPolygon();
			if(border == null) {
				ImageUtility.drawImage(loadedImage, getPreferredSize(), g);
				drawingConstrains = null;
			} else {
				drawingConstrains = ImageUtility.drawCenteredImage(loadedImage, getPreferredSize(), g, border);
				int[] xs = new int[border.getPolygon().length];
				int[] ys = new int[border.getPolygon().length];
				int i = 0;
				for (Point p : border.getPolygon()) {
					xs[i] = (int)((p.x-drawingConstrains[0])*drawingConstrains[2]);
					ys[i] = (int)((p.y-drawingConstrains[1])*drawingConstrains[2]);
					i ++;
				}

				g.setColor(Color.green);
				g.drawPolygon(xs, ys, xs.length);
			}
		} else {
			g.setColor(Color.white);
			g.fillRect(2, 2, getPreferredSize().width-4, getPreferredSize().height-4);
			g.setColor(Color.LIGHT_GRAY);
			g.drawString("No image to display...", 5, 15);
		}
	}

	public void setLoadedImage(Image loadedImage) {
		this.loadedImage = loadedImage;
	}

	public double[] getDrawingConstrains() {
		return drawingConstrains;
	}

}