package co.edu.icesi.nextfruit.views.subviews;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.List;

import co.edu.icesi.nextfruit.modules.Model;
import co.edu.icesi.nextfruit.modules.model.ColorDistribution;
import co.edu.icesi.nextfruit.modules.model.MatchingColor;
import co.edu.icesi.nextfruit.util.ColorConverter;
import visualkey.KPanel;

public class ColorsPanel extends KPanel {

	private Model model;
	private Collection<ColorDistribution> colors;
	private boolean displayColorSpace, changes;
	private double luminantValue;
	private Image loadedImage;
	private BufferedImage canvas;
	private Graphics graphics;
	private Point point;

	public ColorsPanel(Model model, Dimension canvasSize, double luminantValue) {
		super(canvasSize);
		this.model = model;
		this.changes = true;
		this.luminantValue = luminantValue;
	}

	public double[] getPercentOnColorsCanvas(int x, int y) {
		return new double[] {x/getPreferredSize().getWidth(), 1-(y/getPreferredSize().getHeight())};
	}

	public Point xy2inCanvas(double x, double y) {
		return new Point((int)(x*getPreferredSize().getWidth()), (int)((1-y)*getPreferredSize().getHeight()));
	}

	public void paintCanvas() {

		// Checking if image has update
		try {
			Collection<ColorDistribution> colors = model.getFeaturesExtract().getColorStatistics();
			if(this.colors != colors) {
				this.colors = colors;
				this.changes = true;
			}
		} catch(NullPointerException npe){
		}

		if(changes) {
			canvas = new BufferedImage(getPreferredSize().width, getPreferredSize().height, BufferedImage.TYPE_INT_RGB);
			graphics = canvas.getGraphics();

			// White background
			graphics.setColor(Color.white);
			graphics.fillRect(2, 2, getPreferredSize().width-4, getPreferredSize().height-4);

			// Color distribution
			if(displayColorSpace) {
				int startX = getPreferredSize().width/10;
				int startY = getPreferredSize().height/10;
				for(int x = startX; x < getPreferredSize().width-startX; x ++) {
					for (int y = startY; y < getPreferredSize().height-startY; y++) {
						double[] xyY = getPercentOnColorsCanvas(x, y);
						double[][] inverseMatrixM = model.getCameraCalibration().getInverseWorkingSpaceMatrix();
						double[] bgr = ColorConverter.xyY2bgr(new double[]{xyY[0], xyY[1], luminantValue}, inverseMatrixM);
						//						System.out.println("BGR: "+bgr[0]+","+bgr[1]+","+bgr[2]);
						graphics.setColor(new Color(ColorConverter.bgr2rgb(bgr)));
						graphics.drawRect(x, y, 1, 1);
					}
				}
			}

			// Grid
			double verticalSpace = getPreferredSize().getHeight()/10;
			for (int i = 0; i < 10; i ++) {
				int y = (int)(i*verticalSpace);
				graphics.setColor(Color.LIGHT_GRAY);
				graphics.drawLine(0, y, (int) getPreferredSize().getWidth(), y);
				graphics.setColor(Color.BLACK);
				graphics.drawString("0."+(10-i), 5, y);
			}
			double horizontalSpace = getPreferredSize().getWidth()/10;
			for (int j = 0; j < 9; j++) {
				int x = (int)((j+1)*horizontalSpace);
				graphics.setColor(Color.LIGHT_GRAY);
				graphics.drawLine(x, 0, x, (int) getPreferredSize().getWidth());
				graphics.setColor(Color.BLACK);
				graphics.drawString("0."+(j+1), x, (int)getPreferredSize().getHeight()-5);
			}
			graphics.setColor(Color.LIGHT_GRAY);
			graphics.drawRect(0, 0, (int)getPreferredSize().getWidth()-1, (int)getPreferredSize().getHeight()-1);

			// Image distribution
			if(!displayColorSpace) {
				if(loadedImage != null) {
					if(colors != null) {
						for (ColorDistribution color : colors) {
							double[] xyY = ColorConverter.rgb2xyY(color.getRGB(),
									model.getCameraCalibration().getWorkingSpaceMatrix(),
									model.getCameraCalibration().getWhiteX());
							graphics.setColor(color);
							graphics.drawRect((int)(xyY[0]*getPreferredSize().getWidth()),
									(int)((1-xyY[1])*getPreferredSize().getHeight()), 1, 1);
						}
					}
				}
			}

			// Ruler
			graphics.setColor(Color.BLACK);
			for (double i = 1; i < 20; i++)
				graphics.drawLine((int)((i/20.0)*getPreferredSize().getWidth()), (int)(getPreferredSize().getHeight()/2)-5,
						(int)((i/20.0)*getPreferredSize().getWidth()), (int)(getPreferredSize().getHeight()/2)+5);
			for (double i = 1; i < 20; i++)
				graphics.drawLine((int)(getPreferredSize().getWidth()/2)-5, (int)((i/20.0)*getPreferredSize().getHeight()),
						(int)(getPreferredSize().getWidth()/2)+5, (int)((i/20.0)*getPreferredSize().getHeight()));
			changes = false;
		}
	}

	public void paintComponent(Graphics g) {
		paintCanvas();

		// Painting pre-drawed image
		g.drawImage(canvas, 0, 0, null);

		// Matching colors
		List<MatchingColor> matching = model.getMatchingColors();
		if(matching != null) {
			g.setColor(Color.BLACK);
			for (MatchingColor matchingColor : matching) {
				double[] descriptor = matchingColor.getDescriptor();
				double w = descriptor[2]*getPreferredSize().getWidth();
				double h = descriptor[2]*getPreferredSize().getHeight();
				g.drawOval((int)(descriptor[0]*getPreferredSize().getWidth() - w),
						(int)((1-descriptor[1])*getPreferredSize().getHeight() - h),
						(int)(w*2), (int)(h*2));
			}
		}

		// Color scroller
		if(point != null) {
			g.setColor(Color.white);
			g.fillOval(point.x - 4, point.y - 4, 8, 8);
			g.setColor(Color.black);
			g.fillOval(point.x - 2, point.y - 2, 4, 4);
		}
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

	public void setPoint(double[] xyY) {
		if(xyY != null) {
			point = xy2inCanvas(xyY[0], xyY[1]);
		} else
			point = null;
		repaint();
	}
}
