package co.edu.icesi.nextfruit.views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.text.DecimalFormat;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import org.opencv.core.Mat;
import org.opencv.core.Point;

import co.edu.icesi.nextfruit.controller.ComputerVisionController;
import co.edu.icesi.nextfruit.modules.Model;
import co.edu.icesi.nextfruit.modules.computervision.Histogram;
import co.edu.icesi.nextfruit.modules.model.ColorDistribution;
import co.edu.icesi.nextfruit.modules.model.PolygonWrapper;
import co.edu.icesi.nextfruit.mvc.interfaces.Attachable;
import co.edu.icesi.nextfruit.mvc.interfaces.Initializable;
import co.edu.icesi.nextfruit.mvc.interfaces.Updateable;
import co.edu.icesi.nextfruit.util.ColorConverter;
import co.edu.icesi.nextfruit.util.ImageUtility;
import co.edu.icesi.nextfruit.util.Statistics;
import visualkey.KFrame;
import visualkey.KPanel;

public class ComputerVisionWindow extends KFrame implements Initializable, Updateable {

	private static final Dimension CANVAS_SIZE_SMALL = new Dimension(300, 250);
	private static final Dimension CANVAS_SIZE_BIG = new Dimension(350, 350);
	private static final double INITIAL_LUMINANT = 0.75;

	private Model model;
	private Mat mat;

	// Image
	private Image loadedImage;
	private ImageCanvas imageCanvas;
	private double drawingConstrains[];

	private ColorsPanel colorsPanel;
	private BarDiagramCanvas barsCanvas;
	private HistogramCanvas histogramCanvas;
	private JButton loadButton, loadSettingsFileButton, processButton, updateMatchingColorsButton, analizeDataButton, displayImageButton, displayXYYButton, increaseLuminance, decreaseLuminance;
	private JTextArea matchingColors, luminanceStatistics;
	private JLabel calibrationFile, luminanceField;

	@Override
	public void init(Attachable model, Updateable view) {

		// Initializing objects
		imageCanvas = new ImageCanvas(CANVAS_SIZE_SMALL);
		colorsPanel = new ColorsPanel((Model) model, CANVAS_SIZE_BIG, INITIAL_LUMINANT);
		barsCanvas = new BarDiagramCanvas(CANVAS_SIZE_SMALL);
		histogramCanvas = new HistogramCanvas(new Dimension(CANVAS_SIZE_BIG.width, CANVAS_SIZE_SMALL.height));
		matchingColors = new JTextArea();
		matchingColors.setPreferredSize(CANVAS_SIZE_SMALL);
		luminanceStatistics = new JTextArea("Luminance statistics:");
		luminanceStatistics.setPreferredSize(CANVAS_SIZE_SMALL);
		luminanceStatistics.setEditable(false);
		loadButton = new JButton("Load image");
		loadSettingsFileButton = new JButton("Load Calibration Data From an XML File");
		calibrationFile = new JLabel("(No calibration file loaded)");
		updateMatchingColorsButton = new JButton("Update matching colors");
		processButton = new JButton("Process image");
		displayImageButton = new JButton("Image distribution");
		displayXYYButton = new JButton("xyY distribution");
		analizeDataButton = new JButton("Analize data");
		luminanceField = new JLabel(""+INITIAL_LUMINANT);
		increaseLuminance = new JButton(">");
		decreaseLuminance = new JButton("<");

		// Attaching to model
		this.model = (Model) model;
		model.attach(this);
		addComponent(loadButton, 0, 0, 1, 1, false);
		addComponent(loadSettingsFileButton, 1, 0, 1, 1, false);
		addComponent(calibrationFile, 2, 0, 1, 1, true);
		addComponent(imageCanvas, 3, 0, 1, 1, false);
		addComponent(processButton, 4, 0, 1, 1, false);
		addComponent(colorsPanel, 0, 1, 1, 5, false);
		addComponent(displayImageButton, 0, 2, 1, 1, false);
		addComponent(displayXYYButton, 0, 3, 3, 1, false);
		addComponent(updateMatchingColorsButton, 1, 2, 1, 1, false);
		addComponent(decreaseLuminance, 1, 3, 1, 1, false);
		addComponent(luminanceField, 1, 4, 1, 1, false);
		addComponent(increaseLuminance, 1, 5, 1, 1, false);
		addLabel("List of matching colors", 2, 2, 4, 1, true);
		addComponent(matchingColors, 3, 2, 4, 1, false);
		addComponent(analizeDataButton, 4, 2, 4, 1, false);
		addComponent(barsCanvas, 5, 0, 1, 1, false);
		addComponent(histogramCanvas, 5, 1, 1, 1, false);
		addComponent(luminanceStatistics, 5, 2, 4, 1, false);

		// Starting controller
		new ComputerVisionController().init(model, this);

		// Ending initialization
		pack();
		setResizable(false);
	}

	public void displayColorSpace() {
		colorsPanel.displayColorSpace();
	}

	public void displayImageDistribution() {
		colorsPanel.displayImageDistribution();
	}

	public double[] getPercentOnColorsCanvas(int x, int y) {
		return colorsPanel.getPercentOnColorsCanvas(x, y);
	}

	public void realTimeColorSlider(int screenX, int screenY, boolean paint) {
		if(paint && drawingConstrains != null) {
			int realX = (int) ((screenX/drawingConstrains[2])+drawingConstrains[0]);
			int realY = (int) ((screenY/drawingConstrains[2])+drawingConstrains[1]);
			double[] bgr = mat.get(realY, realX);
			double[] xyY = ColorConverter.rgb2xyY(ColorConverter.bgr2rgb(bgr),
					model.getCameraCalibration().getWorkingSpaceMatrix(),
					model.getCameraCalibration().getWhiteX());
			colorsPanel.setPoint(xyY);
		} else
			colorsPanel.setPoint(null);
	}

	@Override
	public void update() {
		if(isVisible()) {
			// Repainting components
			try {
				if(model.getFeaturesExtract() != null) {
					if(mat != model.getFeaturesExtract().getMat()) {
						mat = model.getFeaturesExtract().getMat();
						loadedImage = ImageUtility.mat2Image(mat);
						colorsPanel.setLoadedImage(loadedImage);
					}
					// Updating luminance statistics
					Statistics statistics = model.getFeaturesExtract().getLuminanceStatistics();
					if(statistics != null) {
						DecimalFormat numberFormat = new DecimalFormat("0.000");
						luminanceStatistics.setText("Luminance statistics:\n"
								+ "Mean: "+numberFormat.format(statistics.getMean())+"\n"
								+ "Standar deviation: "+numberFormat.format(statistics.getStandardDeviation())+"\n"
								+ "Skewness: "+numberFormat.format(statistics.getSkewness())+"\n"
								+ "Kurtosis: "+numberFormat.format(statistics.getKurtosis())+"\n");
					}
				}
			} catch(NullPointerException npe){}
			repaint();
		}
	}

	@Override
	public void repaint() {
		colorsPanel.setLuminantValue(Double.valueOf(luminanceField.getText()));
		super.repaint();
	}

	// ***************** SUB-VIEWS *****************

	public class ImageCanvas extends KPanel {

		public ImageCanvas(Dimension canvasSize) {
			super(canvasSize);
		}

		public void paintComponent(Graphics g) {
			if(loadedImage != null) {
				try {
					PolygonWrapper border = model.getFeaturesExtract().getPolygon();
					if(border == null) {
						ImageUtility.drawImage(loadedImage, CANVAS_SIZE_SMALL, g);
						drawingConstrains = null;
					} else {
						drawingConstrains = ImageUtility.drawCenteredImage(loadedImage, CANVAS_SIZE_SMALL, g, border);
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

				} catch(NullPointerException npe) {}
			} else {
				g.setColor(Color.white);
				g.fillRect(2, 2, CANVAS_SIZE_SMALL.width-4, CANVAS_SIZE_SMALL.height-4);
				g.setColor(Color.LIGHT_GRAY);
				g.drawString("No image to display...", 5, 15);
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
			g.setColor(Color.BLACK);
			g.drawString("Color distribution:", 5, 15);
			try {
				Collection<ColorDistribution> matchingColors = model.getFeaturesExtract().getMatchingColors();
				int width = CANVAS_SIZE_SMALL.width/(matchingColors.size()*2 + 1);
				double biggest = 0;
				for (ColorDistribution color : matchingColors) {
					double percent = color.getRepeat()/(double)model.getFeaturesExtract().getNumberOfPixels();
					if(percent > biggest)
						biggest = percent;
				}
				int topMargin = (int)((biggest - 0.8)*150 + 10);
				int height = CANVAS_SIZE_SMALL.height - topMargin;
				int x = width;
				for (ColorDistribution color : matchingColors) {
					g.setColor(color);
					double percent = color.getRepeat()/(double)model.getFeaturesExtract().getNumberOfPixels();
					int y = (int)(height*(1-percent));
					g.fillRect(x, y+topMargin, width, height);
					g.setColor(Color.BLACK);
					g.drawString(((int)(percent*100))+"%", x+2, y-5+topMargin);
					x += width*2;
				}
			} catch(NullPointerException npe) {
			}
		}
	}

	public class HistogramCanvas extends KPanel {

		public HistogramCanvas(Dimension canvasSize) {
			super(canvasSize);
		}

		public void paintComponent(Graphics g) {
			g.setColor(Color.white);
			g.fillRect(2, 2, CANVAS_SIZE_BIG.width-4, CANVAS_SIZE_SMALL.height-4);
			g.setColor(Color.BLACK);
			g.drawString("Luminance histogram:", 5, 15);
			try {
				Histogram histogram = model.getFeaturesExtract().getHistogram();
				int[] values = histogram.getHistogram();
				double max = histogram.getMaxHeight()*1.125;
				g.setColor(Color.CYAN);
				for (int i = 0; i < values.length; i++) {
					double percent = values[i]/max;
					g.drawLine(i+25, (int)(CANVAS_SIZE_SMALL.height*(1-percent)), i+25, CANVAS_SIZE_SMALL.height);
				}
			} catch(NullPointerException npe){}
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

	public JLabel getCalibrationFile() {
		return calibrationFile;
	}

	public ColorsPanel getColorsCanvas() {
		return colorsPanel;
	}

	public JButton getDisplayImageButton() {
		return displayImageButton;
	}

	public JButton getDisplayXYYButton() {
		return displayXYYButton;
	}

	public JButton getIncreaseLuminance() {
		return increaseLuminance;
	}

	public JButton getDecreaseLuminance() {
		return decreaseLuminance;
	}

	public JLabel getLuminanceField() {
		return luminanceField;
	}

	public ImageCanvas getImageCanvas() {
		return imageCanvas;
	}

	// ****************** GETTERS ******************

}
