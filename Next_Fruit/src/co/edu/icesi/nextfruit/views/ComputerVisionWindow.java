package co.edu.icesi.nextfruit.views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;

import javax.swing.JButton;

import org.opencv.core.Point;

import co.edu.icesi.nextfruit.controller.CalibrationResultsController;
import co.edu.icesi.nextfruit.controller.ComputerVisionController;
import co.edu.icesi.nextfruit.modules.Model;
import co.edu.icesi.nextfruit.modules.model.PolygonWrapper;
import co.edu.icesi.nextfruit.mvc.interfaces.Attachable;
import co.edu.icesi.nextfruit.mvc.interfaces.Initializable;
import co.edu.icesi.nextfruit.mvc.interfaces.Updateable;
import co.edu.icesi.nextfruit.util.ImageUtility;
import co.edu.icesi.nextfruit.views.CalibrationWindow.ColorCheckerCanvas;
import visualkey.KFrame;
import visualkey.KPanel;

public class ComputerVisionWindow extends KFrame implements Initializable, Updateable {

	private static final Dimension CANVAS_SIZE = new Dimension(300, 200);

	private Model model;
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
		addComponent(processButton, 2, 0, 2, 1, false);

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
					PolygonWrapper border = null;
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
			if(loadedImage != null) {

			} else {
				g.setColor(Color.white);
				g.fillRect(2, 2, CANVAS_SIZE.width-2, CANVAS_SIZE.height-2);
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
