package co.edu.icesi.nextfruit.views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.text.DecimalFormat;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.opencv.core.Mat;

import co.edu.icesi.nextfruit.controller.ComputerVisionController;
import co.edu.icesi.nextfruit.modules.Model;
import co.edu.icesi.nextfruit.modules.computervision.Histogram;
import co.edu.icesi.nextfruit.modules.model.ColorDistribution;
import co.edu.icesi.nextfruit.mvc.interfaces.Attachable;
import co.edu.icesi.nextfruit.mvc.interfaces.Initializable;
import co.edu.icesi.nextfruit.mvc.interfaces.Updateable;
import co.edu.icesi.nextfruit.util.ColorConverter;
import co.edu.icesi.nextfruit.util.ImageUtility;
import co.edu.icesi.nextfruit.util.Statistics;
import co.edu.icesi.nextfruit.views.subviews.ColorsPanel;
import co.edu.icesi.nextfruit.views.subviews.ImageCanvas;
import visualkey.KFrame;
import visualkey.KPanel;

public class ComputerVisionWindow extends KFrame implements Initializable, Updateable {

	private static final Dimension CANVAS_SIZE_VERTICAL = new Dimension(300, 300);
	private static final Dimension CANVAS_SIZE_SMALL = new Dimension(300, 250);
	private static final Dimension CANVAS_SIZE_HORIZONTAL_LARGE = new Dimension(650, 250);
	private static final Dimension CANVAS_SIZE_HORIZONTAL_SHORT = new Dimension(400, 125);
	private static final Dimension CANVAS_SIZE_MINI = new Dimension(125, 125);
	private static final Dimension CANVAS_SIZE_BIG = new Dimension(350, 350);
	private static final double INITIAL_LUMINANT = 0.55;

	private Model model;
	private Mat mat;

	private ImageCanvas imageCanvas;
	private ColorsPanel colorsPanel;
	private BarDiagramCanvas barsCanvas;
	private HistogramCanvas histogramCanvas;
	private JButton loadButton, processButton, updateMatchingColorsButton, analizeDataButton, displayImageButton, displayXYYButton, increaseLuminance, decreaseLuminance;
	private JTextArea matchingColors, luminanceStatistics, luminanceRanges;
	private JLabel luminanceField, xyYscroller;

	@Override
	public void init(Attachable model, Updateable view) {

		// Attaching to model
		this.model = (Model) model;
		model.attach(this);

		// Initializing objects
		imageCanvas = new ImageCanvas(CANVAS_SIZE_VERTICAL, this.model);
		xyYscroller = new JLabel("(x,y,Y)");
		colorsPanel = new ColorsPanel((Model) model, CANVAS_SIZE_BIG, INITIAL_LUMINANT);
		barsCanvas = new BarDiagramCanvas(CANVAS_SIZE_HORIZONTAL_LARGE);
		histogramCanvas = new HistogramCanvas(CANVAS_SIZE_HORIZONTAL_SHORT);

		matchingColors = new JTextArea();
		//		matchingColors
		JScrollPane matchingColorsScroll = new JScrollPane(matchingColors);
		matchingColorsScroll.setPreferredSize(CANVAS_SIZE_SMALL);
		luminanceStatistics = new JTextArea("Luminance statistics:");
		luminanceStatistics.setPreferredSize(CANVAS_SIZE_MINI);
		luminanceStatistics.setEditable(false);
		luminanceRanges = new JTextArea("");
		luminanceRanges.setPreferredSize(CANVAS_SIZE_MINI);
		luminanceRanges.setEditable(false);
		Font font = new Font("Arial", Font.BOLD, 8);
		luminanceRanges.setFont(font);

		loadButton = new JButton("Load image");
		updateMatchingColorsButton = new JButton("Update matching colors");
		processButton = new JButton("Process image");
		displayImageButton = new JButton("Image distribution");
		displayXYYButton = new JButton("xyY distribution");
		analizeDataButton = new JButton("Analize data");
		luminanceField = new JLabel(""+INITIAL_LUMINANT);
		increaseLuminance = new JButton(">");
		decreaseLuminance = new JButton("<");

		// Adding components

		addComponent(loadButton, 0, 0, 2, 1, false);
		addComponent(imageCanvas, 1, 0, 2, 3, false);
		addComponent(xyYscroller, 4, 0, 1, 1, true);
		addComponent(processButton, 4, 1, 1, 1, false);

		addComponent(colorsPanel, 0, 2, 1, 5, false);

		addComponent(displayImageButton, 0, 3, 1, 1, false);
		addComponent(displayXYYButton, 0, 4, 3, 1, false);
		addComponent(updateMatchingColorsButton, 1, 3, 1, 1, false);
		addComponent(decreaseLuminance, 1, 4, 1, 1, false);
		addComponent(luminanceField, 1, 5, 1, 1, false);
		addComponent(increaseLuminance, 1, 6, 1, 1, false);
		addLabel("List of matching colors", 2, 3, 4, 1, true);
		addComponent(matchingColorsScroll, 3, 3, 4, 1, false);
		addComponent(analizeDataButton, 4, 3, 4, 1, false);

		addComponent(barsCanvas, 5, 0, 3, 2, false);

		addComponent(luminanceStatistics, 5, 3, 2, 1, false);
		addComponent(luminanceRanges, 5, 5, 2, 1, false);
		addComponent(histogramCanvas, 6, 3, 4, 1, false);

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
		double[] drawingConstrains = imageCanvas.getDrawingConstrains();
		if(paint && drawingConstrains != null) {
			int realX = (int) ((screenX/drawingConstrains[2])+drawingConstrains[0]);
			int realY = (int) ((screenY/drawingConstrains[2])+drawingConstrains[1]);
			double[] bgr = mat.get(realY, realX);
			double[] xyY = ColorConverter.rgb2xyY(ColorConverter.bgr2rgb(bgr),
					model.getCameraCalibration().getWorkingSpaceMatrix(),
					model.getCameraCalibration().getWhiteX());
			colorsPanel.setPoint(xyY);
			DecimalFormat numberFormat = new DecimalFormat("0.00");
			xyYscroller.setText("("+numberFormat.format(xyY[0])+","+numberFormat.format(xyY[1])+","+numberFormat.format(xyY[2])+")");
		} else {
			colorsPanel.setPoint(null);
			xyYscroller.setText("(x,y,Y)");
		}
	}

	@Override
	public void update() {
		if(isVisible()) {
			// Repainting components
			try {
				if(model.getFeaturesExtract() != null) {
					if(mat != model.getFeaturesExtract().getMat()) {
						mat = model.getFeaturesExtract().getMat();
						Image image = ImageUtility.mat2Image(mat);
						imageCanvas.setLoadedImage(image);
						colorsPanel.setLoadedImage(image);
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
					} else {
						luminanceStatistics.setText("Luminance statistics:");
					}
				}
			} catch(NullPointerException npe){}
			repaint();
		}
	}

	@Override
	public void repaint() {
		colorsPanel.setLuminantValue(getSelectedLuminance());
		super.repaint();
	}

	// ***************** SUB-VIEWS *****************

	public class BarDiagramCanvas extends KPanel {

		private Dimension size;

		public BarDiagramCanvas(Dimension canvasSize) {
			super(canvasSize);
			this.size = canvasSize;
		}

		public void paintComponent(Graphics g) {
			g.setColor(Color.white);
			g.fillRect(2, 2, size.width-4, size.height-4);
			g.setColor(Color.BLACK);
			g.drawString("Color distribution:", 5, 15);
			try {
				Collection<ColorDistribution> matchingColors = model.getFeaturesExtract().getMatchingColors();
				int width = size.width/(matchingColors.size()*2 + 1);
				double biggest = 0;
				for (ColorDistribution color : matchingColors) {
					double percent = color.getRepeat()/(double)model.getFeaturesExtract().getNumberOfPixels();
					if(percent > biggest)
						biggest = percent;
				}
				int topMargin = (int)((biggest - 0.8)*150 + 10);
				int height = size.height - topMargin;
				int x = width;
				for (ColorDistribution color : matchingColors) {
					g.setColor(color.getColorWithOtherLuminant(getSelectedLuminance(), model.getCameraCalibration()));
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

	private double getSelectedLuminance() {
		return Double.valueOf(luminanceField.getText().replace(",", "."));
	}

	public class HistogramCanvas extends KPanel {

		private Dimension size;

		public HistogramCanvas(Dimension canvasSize) {
			super(canvasSize);
			this.size = canvasSize;
		}

		public void paintComponent(Graphics g) {
			g.setColor(Color.white);
			g.fillRect(2, 2, size.width-4, size.height-4);
			g.setColor(Color.BLACK);
			g.drawString("Luminance histogram:", 5, 15);
			try {
				Histogram histogram = model.getFeaturesExtract().getHistogram();
				int[] values = histogram.getHistogram();
				double max = histogram.getMaxHeight()*1.125;
				g.setColor(Color.CYAN);
				for (int i = 0; i < values.length; i++) {
					double percent = values[i]/max;
					g.drawLine(i+25, (int)(size.height*(1-percent)), i+25, size.height);
				}

				// Painting ranges
				double[] ranges = histogram.getRanges();
				String rangesTxt = "";
				for (int i = 0; i < ranges.length; i++) {
					int bars = (int) (ranges[i]*80);
					for (int j = 0; j < bars; j++)
						rangesTxt += "|";
					rangesTxt += "\n";
				}
				luminanceRanges.setText(rangesTxt);

			} catch(NullPointerException npe){}
		}
	}

	// ***************** SUB-VIEWS *****************

	// ****************** GETTERS ******************

	public JButton getLoadButton() {
		return loadButton;
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
