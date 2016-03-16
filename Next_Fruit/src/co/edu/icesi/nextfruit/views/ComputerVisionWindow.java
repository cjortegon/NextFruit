package co.edu.icesi.nextfruit.views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.util.Collection;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JTextArea;

import org.opencv.core.Mat;
import org.opencv.core.Point;

import co.edu.icesi.nextfruit.controller.ComputerVisionController;
import co.edu.icesi.nextfruit.modules.Model;
import co.edu.icesi.nextfruit.modules.model.ColorDistribution;
import co.edu.icesi.nextfruit.modules.model.MatchingColor;
import co.edu.icesi.nextfruit.modules.model.PolygonWrapper;
import co.edu.icesi.nextfruit.mvc.interfaces.Attachable;
import co.edu.icesi.nextfruit.mvc.interfaces.Initializable;
import co.edu.icesi.nextfruit.mvc.interfaces.Updateable;
import co.edu.icesi.nextfruit.util.ColorConverter;
import co.edu.icesi.nextfruit.util.ImageUtility;
import visualkey.KFrame;
import visualkey.KPanel;

public class ComputerVisionWindow extends KFrame implements Initializable, Updateable {

	private static final Dimension CANVAS_SIZE_SMALL = new Dimension(300, 200);
	private static final Dimension CANVAS_SIZE_BIG = new Dimension(450, 300);

	private Model model;
	private Mat mat;
	private Image loadedImage;
	private ImageCanvas imageCanvas;
	private ColorsPanel colorsCanvas;
	private BarDiagramCanvas barsCanvas;
	private HistogramCanvas histogramCanvas;
	private JButton loadButton, loadSettingsFileButton, processButton, updateMatchingColorsButton, analizeDataButton;
	private JTextArea matchingColors, luminantStatistics;

	@Override
	public void init(Attachable model, Updateable view) {

		// Initializing objects
		imageCanvas = new ImageCanvas(CANVAS_SIZE_SMALL);
		colorsCanvas = new ColorsPanel(CANVAS_SIZE_BIG);
		barsCanvas = new BarDiagramCanvas(CANVAS_SIZE_SMALL);
		histogramCanvas = new HistogramCanvas(new Dimension(CANVAS_SIZE_BIG.width, CANVAS_SIZE_SMALL.height));
		matchingColors = new JTextArea();
		Dimension taDimension = new Dimension(CANVAS_SIZE_SMALL.width/2, CANVAS_SIZE_SMALL.height);
		matchingColors.setPreferredSize(taDimension);
		luminantStatistics = new JTextArea("Luminant statistics:");
		luminantStatistics.setPreferredSize(taDimension);
		luminantStatistics.setEditable(false);
		loadButton = new JButton("Load image");
		loadSettingsFileButton = new JButton("Load Calibration Data From an XML File");
		updateMatchingColorsButton = new JButton("Update matching colors");
		processButton = new JButton("Process image");
		analizeDataButton = new JButton("Analize data");

		// Attaching to model
		this.model = (Model) model;
		model.attach(this);

		//Adding objects to window
		addComponent(loadButton, 0, 0, 1, 1, false);
		addComponent(loadSettingsFileButton, 1, 0, 1, 1, false);
		addComponent(imageCanvas, 2, 0, 1, 1, false);
		addComponent(processButton, 3, 0, 1, 1, false);
		addComponent(colorsCanvas, 0, 1, 1, 4, false);
		addComponent(updateMatchingColorsButton, 0, 2, 1, 1, false);
		addComponent(matchingColors, 1, 2, 1, 2, false);
		addComponent(analizeDataButton, 3, 2, 1, 1, false);
		addComponent(barsCanvas, 4, 0, 1, 1, false);
		addComponent(histogramCanvas, 4, 1, 1, 1, false);
		addComponent(luminantStatistics, 4, 2, 1, 1, false);

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
				if(model.getFeaturesExtract() != null && mat != model.getFeaturesExtract().getMat()) {
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
					double size[] = ImageUtility.drawImage(loadedImage, CANVAS_SIZE_SMALL, g);
					PolygonWrapper border = model.getFeaturesExtract().getPolygon();
					if(border != null) {
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
					}

				} catch(NullPointerException npe) {}
			} else {
				g.setColor(Color.white);
				g.fillRect(2, 2, CANVAS_SIZE_SMALL.width-4, CANVAS_SIZE_SMALL.height-4);
				g.setColor(Color.LIGHT_GRAY);
				g.drawRect(2, CANVAS_SIZE_SMALL.height-2, CANVAS_SIZE_SMALL.width/2 - 10, CANVAS_SIZE_SMALL.height/2);
				g.drawRect(CANVAS_SIZE_SMALL.width/2 - 10, CANVAS_SIZE_SMALL.height/2, CANVAS_SIZE_SMALL.width-12, CANVAS_SIZE_SMALL.height-2);
			}
		}
	}

	public class ColorsPanel extends KPanel {

		public ColorsPanel(Dimension canvasSize) {
			super(canvasSize);
		}

		public void paintComponent(Graphics g) {

			// White background
			g.setColor(Color.white);
			g.fillRect(2, 2, CANVAS_SIZE_BIG.width-4, CANVAS_SIZE_BIG.height-4);

			// Grid
			double verticalSpace = CANVAS_SIZE_BIG.getHeight()/10;
			for (int i = 0; i < 10; i ++) {
				int y = (int)(i*verticalSpace);
				g.setColor(Color.LIGHT_GRAY);
				g.drawLine(0, y, (int) CANVAS_SIZE_BIG.getWidth(), y);
				g.setColor(Color.BLACK);
				g.drawString("0."+(10-i), 5, y);
			}
			double horizontalSpace = CANVAS_SIZE_BIG.getWidth()/10;
			for (int j = 0; j < 9; j++) {
				int x = (int)((j+1)*horizontalSpace);
				g.setColor(Color.LIGHT_GRAY);
				g.drawLine(x, 0, x, (int) CANVAS_SIZE_BIG.getWidth());
				g.setColor(Color.BLACK);
				g.drawString("0."+(j+1), x, (int)CANVAS_SIZE_BIG.getHeight()-5);
			}
			g.setColor(Color.LIGHT_GRAY);
			g.drawRect(0, 0, (int)CANVAS_SIZE_BIG.getWidth()-1, (int)CANVAS_SIZE_BIG.getHeight()-1);

			// Color distribution
			if(loadedImage != null) {
				try {
					Collection<ColorDistribution> colors = model.getFeaturesExtract().getColorStatistics();
					for (ColorDistribution color : colors) {
						double[] xyY = ColorConverter.rgb2xyY(color.getRGB(),
								model.getCameraCalibration().getWorkingSpaceMatrix(),
								model.getCameraCalibration().getWhiteX());
						g.setColor(color);
						g.drawRect((int)(xyY[0]*CANVAS_SIZE_BIG.getWidth()), (int)((1-xyY[1])*CANVAS_SIZE_BIG.getHeight()), 1, 1);
					}
				} catch(NullPointerException npe) {}
			}
			
			// Matching colors
			List<MatchingColor> matching = model.getMatchingColors();
			if(matching != null) {
				g.setColor(Color.BLACK);
				for (MatchingColor matchingColor : matching) {
					double[] descriptor = matchingColor.getDescriptor();
					g.drawOval((int)(descriptor[0]*CANVAS_SIZE_BIG.getWidth()),
							(int)((1-descriptor[1])*CANVAS_SIZE_BIG.getHeight()),
							(int)(descriptor[2]*2*CANVAS_SIZE_BIG.getWidth()),
							(int)(descriptor[2]*2*CANVAS_SIZE_BIG.getHeight()));
				}
			}
		}
	}

	public class BarDiagramCanvas extends KPanel {

		public BarDiagramCanvas(Dimension canvasSize) {
			super(canvasSize);
		}

		public void paintComponent(Graphics g) {
			g.setColor(Color.white);
			g.fillRect(2, 2, CANVAS_SIZE_SMALL.width-4, CANVAS_SIZE_SMALL.height-4);
		}
	}

	public class HistogramCanvas extends KPanel {

		public HistogramCanvas(Dimension canvasSize) {
			super(canvasSize);
		}

		public void paintComponent(Graphics g) {
			g.setColor(Color.white);
			g.fillRect(2, 2, CANVAS_SIZE_BIG.width-4, CANVAS_SIZE_SMALL.height-4);
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

	public JButton getUpdateMatchingColorsButton() {
		return updateMatchingColorsButton;
	}

	public JButton getAnalizeDataButton() {
		return analizeDataButton;
	}

	public JTextArea getMatchingColors() {
		return matchingColors;
	}

	// ****************** GETTERS ******************

}
