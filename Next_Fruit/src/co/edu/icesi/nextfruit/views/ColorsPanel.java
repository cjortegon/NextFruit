package co.edu.icesi.nextfruit.views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.List;

import co.edu.icesi.nextfruit.modules.Model;
import co.edu.icesi.nextfruit.modules.model.ColorDistribution;
import co.edu.icesi.nextfruit.modules.model.MatchingColor;
import co.edu.icesi.nextfruit.util.ColorConverter;
import visualkey.KPanel;

public class ColorsPanel extends KPanel {

	private Dimension canvasSize;
	private Model model;
	private boolean displayColorSpace, changes;
	private double luminantValue;
	private Image loadedImage;
	private BufferedImage canvas;
	private Graphics graphics;

	public ColorsPanel(Model model, Dimension canvasSize, double luminantValue) {
		super(canvasSize);
		this.model = model;
		this.changes = true;
		this.canvasSize = canvasSize;
		this.luminantValue = luminantValue;
	}

	public double[] getPercentOnColorsCanvas(int x, int y) {
		return new double[] {x/canvasSize.getWidth(), 1-(y/canvasSize.getHeight())};
	}

	public void repaint() {
		if(changes) {
			canvas = new BufferedImage(canvasSize.width, canvasSize.height, BufferedImage.TYPE_INT_RGB);
			graphics = canvas.getGraphics();

			// White background
			graphics.setColor(Color.white);
			graphics.fillRect(2, 2, canvasSize.width-4, canvasSize.height-4);

			// Color distribution
			if(displayColorSpace) {
				int startX = canvasSize.width/10;
				int startY = canvasSize.height/10;
				for(int x = startX; x < canvasSize.width-startX; x ++) {
					for (int y = startY; y < canvasSize.height-startY; y++) {
						double[] xyY = getPercentOnColorsCanvas(x, y);
						double[][] inverseMatrixM = model.getCameraCalibration().getInverseWorkingSpaceMatrix();
						graphics.setColor(new Color(ColorConverter.bgr2rgb(ColorConverter.xyY2bgr(new double[]{xyY[0], xyY[1], luminantValue}, inverseMatrixM))));
						graphics.drawRect(x, y, 1, 1);
					}
				}
			}

			// Grid
			double verticalSpace = canvasSize.getHeight()/10;
			for (int i = 0; i < 10; i ++) {
				int y = (int)(i*verticalSpace);
				graphics.setColor(Color.LIGHT_GRAY);
				graphics.drawLine(0, y, (int) canvasSize.getWidth(), y);
				graphics.setColor(Color.BLACK);
				graphics.drawString("0."+(10-i), 5, y);
			}
			double horizontalSpace = canvasSize.getWidth()/10;
			for (int j = 0; j < 9; j++) {
				int x = (int)((j+1)*horizontalSpace);
				graphics.setColor(Color.LIGHT_GRAY);
				graphics.drawLine(x, 0, x, (int) canvasSize.getWidth());
				graphics.setColor(Color.BLACK);
				graphics.drawString("0."+(j+1), x, (int)canvasSize.getHeight()-5);
			}
			graphics.setColor(Color.LIGHT_GRAY);
			graphics.drawRect(0, 0, (int)canvasSize.getWidth()-1, (int)canvasSize.getHeight()-1);

			// Image distribution
			if(!displayColorSpace) {
				if(loadedImage != null) {
					try {
						Collection<ColorDistribution> colors = model.getFeaturesExtract().getColorStatistics();
						for (ColorDistribution color : colors) {
							double[] xyY = ColorConverter.rgb2xyY(color.getRGB(),
									model.getCameraCalibration().getWorkingSpaceMatrix(),
									model.getCameraCalibration().getWhiteX());
							graphics.setColor(color);
							graphics.drawRect((int)(xyY[0]*canvasSize.getWidth()), (int)((1-xyY[1])*canvasSize.getHeight()), 1, 1);
						}
					} catch(NullPointerException npe) {}
				}
			}

			// Ruler
			graphics.setColor(Color.BLACK);
			for (double i = 1; i < 20; i++) {
				graphics.drawLine((int)((i/20.0)*canvasSize.getWidth()), (int)(canvasSize.getHeight()/2)-5, (int)((i/20.0)*canvasSize.getWidth()), (int)(canvasSize.getHeight()/2)+5);
			}
			for (double i = 1; i < 20; i++) {
				graphics.drawLine((int)(canvasSize.getWidth()/2)-5, (int)((i/20.0)*canvasSize.getHeight()), (int)(canvasSize.getWidth()/2)+5, (int)((i/20.0)*canvasSize.getHeight()));
			}

			// Matching colors
			List<MatchingColor> matching = model.getMatchingColors();
			if(matching != null) {
				graphics.setColor(Color.BLACK);
				for (MatchingColor matchingColor : matching) {
					double[] descriptor = matchingColor.getDescriptor();
					double w = descriptor[2]*canvasSize.getWidth();
					double h = descriptor[2]*canvasSize.getHeight();
					graphics.drawOval((int)(descriptor[0]*canvasSize.getWidth() - w),
							(int)((1-descriptor[1])*canvasSize.getHeight() - h),
							(int)(w*2), (int)(h*2));
				}
			}
			changes = false;
		}
	}

	public void paintComponent(Graphics g) {
		repaint();
		g.drawImage(canvas, 0, 0, null);
	}

	public void displayColorSpace() {
		if(!displayColorSpace)
			changes = true;
		displayColorSpace = true;
	}

	public void displayImageDistribution() {
		if(displayColorSpace)
			changes = true;
		displayColorSpace = false;
	}

	public void setLuminantValue(double luminantValue) {
		if(this.luminantValue != luminantValue)
			changes = true;
		this.luminantValue = luminantValue;
	}

	public void setLoadedImage(Image loadedImage) {
		if(this.loadedImage != loadedImage)
			changes = true;
		this.loadedImage = loadedImage;
	}
}
