package co.edu.icesi.nextfruit.views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;

import org.opencv.core.Mat;
import org.opencv.core.Point;

import co.edu.icesi.nextfruit.controller.CalibrationResultsController;
import co.edu.icesi.nextfruit.controller.ComputerVisionController;
import co.edu.icesi.nextfruit.modules.Model;
import co.edu.icesi.nextfruit.modules.model.PolygonWrapper;
import co.edu.icesi.nextfruit.mvc.interfaces.Attachable;
import co.edu.icesi.nextfruit.mvc.interfaces.Initializable;
import co.edu.icesi.nextfruit.mvc.interfaces.Updateable;
import co.edu.icesi.nextfruit.util.ColorConverter;
import co.edu.icesi.nextfruit.util.ImageUtility;
import visualkey.KFrame;
import visualkey.KPanel;

public class ComputerVisionWindow extends KFrame implements Initializable, Updateable {

	private static final Dimension CANVAS_SIZE = new Dimension(300, 200);

	private Model model;
	private Mat mat;
	private Image loadedImage;
	private ImageCanvas canvas;
	private ColorDistribution colors;
	private JButton loadButton, loadSettingsFileButton, processButton;

	@Override
	public void init(Attachable model, Updateable view) {

		// Initializing objects
		canvas = new ImageCanvas(CANVAS_SIZE);
		colors = new ColorDistribution(CANVAS_SIZE);
		loadButton = new JButton("Load image");
		loadSettingsFileButton = new JButton("Load Calibration Data From an XML File");
		processButton = new JButton("Process image");

		// Attaching to model
		this.model = (Model) model;
		model.attach(this);

		//Adding objects to window
		addComponent(loadButton, 0, 0, 1, 1, false);
		addComponent(loadSettingsFileButton, 0, 1, 1, 1, false);
		addComponent(canvas, 1, 0, 1, 1, false);
		addComponent(colors, 1, 1, 1, 1, false);
		addComponent(processButton, 2, 0, 2, 1, true);

		// Starting controller
		new ComputerVisionController().init(model, this);

		// Ending initialization
		pack();
		setResizable(false);
	}

	@Override
	public void update() {
		if(isVisible()) {
			// Repainting components
			try {
				//				if(model.getFeaturesExtract() != null && mat != model.getFeaturesExtract().getMat()) {
				if(model.getFeaturesExtract() != null) {
					mat = model.getFeaturesExtract().getMat();
					loadedImage = ImageUtility.mat2Image(mat);
				}
			} catch(NullPointerException npe){}
			repaint();
		}
	}

	// ***************** SUB-VIEWS *****************

	public class ImageCanvas extends KPanel {

		public ImageCanvas(Dimension canvasSize) {
			super(canvasSize);
		}

		public void paintComponent(Graphics g) {
			if(loadedImage != null) {
				try {
					double size[] = ImageUtility.drawImage(loadedImage, CANVAS_SIZE, g);
					PolygonWrapper border = model.getFeaturesExtract().getPolygon();
					if(border != null) {

						// Fill with real colors
						//						g.setColor(Color.white);
						//						g.fillRect(2, 2, CANVAS_SIZE.width-2, CANVAS_SIZE.height-2);
						//						Iterator<Point> iterator = border.getIterator();
						//						while(iterator.hasNext()) {
						//							Point p = iterator.next();
						//							g.setColor(new Color(ImageUtility.bgr2Rgb(mat.get((int)p.y, (int)p.x))));
						//							g.drawRect((int)(p.x*size[2]), (int)(p.y*size[2]), 1, 1);
						//						}

						int[] xs = new int[border.getPolygon().length];
						int[] ys = new int[border.getPolygon().length];
						int i = 0;
						for (Point p : border.getPolygon()) {
							xs[i] = (int)(p.x*size[2]);
							ys[i] = (int)(p.y*size[2]);
							i ++;
						}
						g.setColor(Color.green);
						g.drawPolygon(xs, ys, xs.length);

						// Fill
						//					Iterator<Point> iterator = border.getIterator();
						//					while(iterator.hasNext()) {
						//						Point p = iterator.next();
						//						g.drawRect((int)(p.x*size[2]), (int)(p.y*size[2]), 1, 1);
						//					}
					}

				} catch(NullPointerException npe) {}
			} else {
				g.setColor(Color.white);
				g.fillRect(2, 2, CANVAS_SIZE.width-2, CANVAS_SIZE.height-2);
			}
		}
	}

	public class ColorDistribution extends KPanel {

		public ColorDistribution(Dimension canvasSize) {
			super(canvasSize);
		}

		public void paintComponent(Graphics g) {

			// White background
			g.setColor(Color.white);
			g.fillRect(2, 2, CANVAS_SIZE.width-2, CANVAS_SIZE.height-2);

			// Grid
			double verticalSpace = CANVAS_SIZE.getHeight()/10;
			for (int i = 0; i < 10; i ++) {
				int y = (int)(i*verticalSpace);
				g.setColor(Color.LIGHT_GRAY);
				g.drawLine(0, y, (int) CANVAS_SIZE.getWidth(), y);
				g.setColor(Color.BLACK);
				g.drawString("0."+(10-i), 5, y);
			}
			double horizontalSpace = CANVAS_SIZE.getWidth()/10;
			for (int j = 0; j < 9; j++) {
				int x = (int)((j+1)*horizontalSpace);
				g.setColor(Color.LIGHT_GRAY);
				g.drawLine(x, 0, x, (int) CANVAS_SIZE.getWidth());
				g.setColor(Color.BLACK);
				g.drawString("0."+(j+1), x, (int)CANVAS_SIZE.getHeight()-5);
			}
			g.setColor(Color.LIGHT_GRAY);
			g.drawRect(0, 0, (int)CANVAS_SIZE.getWidth()-1, (int)CANVAS_SIZE.getHeight()-1);

			// Color distribution
			if(loadedImage != null) {
				try {
					Set<Integer> colors = model.getFeaturesExtract().getColorStatistics().keySet();
					for (Integer color : colors) {
						double[] xyY = ColorConverter.XYZ2xyY(ColorConverter.rgb2xyz(ColorConverter.reverseColor(ColorConverter.rgb2bgr(color))), model.getCameraCalibration().getWhiteX());
						g.setColor(new Color(color));
						g.drawRect((int)(xyY[0]*CANVAS_SIZE.getWidth()), (int)((1-xyY[1])*CANVAS_SIZE.getHeight()), 1, 1);
					}
				} catch(NullPointerException npe) {}
			}
		}
	}

	// ***************** SUB-VIEWS *****************

	// ****************** GETTERS ******************

	public JButton getLoadButton() {
		return loadButton;
	}

	public JButton getLoadSettingsFileButton() {
		return loadSettingsFileButton;
	}

	public JButton getProcessButton() {
		return processButton;
	}

	// ****************** GETTERS ******************

}
