package co.edu.icesi.nextfruit.views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import org.opencv.core.Mat;
import org.opencv.core.Point;

import co.edu.icesi.nextfruit.controller.ComputerVisionController;
import co.edu.icesi.nextfruit.modules.Model;
import co.edu.icesi.nextfruit.modules.computervision.Histogram;
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
	private static final Dimension CANVAS_SIZE_BIG = new Dimension(300, 300);

	private Model model;
	private Mat mat;
	private Image loadedImage;
	private ImageCanvas imageCanvas;
	private ColorsPanel colorsCanvas;
	private BarDiagramCanvas barsCanvas;
	private HistogramCanvas histogramCanvas;
	private JButton loadButton, loadSettingsFileButton, processButton, updateMatchingColorsButton, analizeDataButton, displayImageButton, displayXYYButton;
	private JTextArea matchingColors, luminanceStatistics;
	private JLabel calibrationFile;
	private boolean displayColorSpace;

	@Override
	public void init(Attachable model, Updateable view) {

		// Initializing objects
		imageCanvas = new ImageCanvas(CANVAS_SIZE_SMALL);
		colorsCanvas = new ColorsPanel(CANVAS_SIZE_BIG);
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

		// Attaching to model
		this.model = (Model) model;
		model.attach(this);

		//Adding objects to window
		addComponent(loadButton, 0, 0, 1, 1, false);
		addComponent(loadSettingsFileButton, 1, 0, 1, 1, false);
		addComponent(calibrationFile, 2, 0, 1, 1, true);
		addComponent(imageCanvas, 3, 0, 1, 1, false);
		addComponent(processButton, 4, 0, 1, 1, false);
		addComponent(colorsCanvas, 0, 1, 1, 5, false);
		addComponent(displayImageButton, 0, 2, 1, 1, false);
		addComponent(displayXYYButton, 0, 3, 1, 1, false);
		addComponent(updateMatchingColorsButton, 1, 2, 2, 1, false);
		addLabel("List of matching colors", 2, 2, 2, 1, true);
		addComponent(matchingColors, 3, 2, 2, 1, false);
		addComponent(analizeDataButton, 4, 2, 2, 1, false);
		addComponent(barsCanvas, 5, 0, 1, 1, false);
		addComponent(histogramCanvas, 5, 1, 1, 1, false);
		addComponent(luminanceStatistics, 5, 2, 2, 1, false);

		// Starting controller
		new ComputerVisionController().init(model, this);

		// Ending initialization
		pack();
		setResizable(false);
	}

	public double[] getPercentOnColorsCanvas(int x, int y) {
		return new double[] {x/CANVAS_SIZE_BIG.getWidth(), 1-(y/CANVAS_SIZE_BIG.getHeight())};
	}

	public void displayColorSpace() {
		displayColorSpace = true;
	}

	public void displayImageDistribution() {
		displayColorSpace = false;
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
				g.drawString("No image to display...", 5, 15);
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

			// Color distribution
			if(displayColorSpace) {
				int startX = CANVAS_SIZE_BIG.width/10;
				int startY = CANVAS_SIZE_BIG.height/10;
				for(int x = startX; x < CANVAS_SIZE_BIG.width-startX; x ++) {
					for (int y = startY; y < CANVAS_SIZE_BIG.height-startY; y++) {
						double[] xyY = getPercentOnColorsCanvas(x, y);
						double[][] inverseMatrixM = model.getCameraCalibration().getInverseWorkingSpaceMatrix();
						g.setColor(new Color(ColorConverter.bgr2rgb(ColorConverter.xyY2bgr(new double[]{xyY[0], xyY[1], 0.75}, inverseMatrixM))));
						g.drawRect(x, y, 1, 1);
					}
				}
			}

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

			// Image distribution
			if(!displayColorSpace) {
				if(loadedImage != null) {
					try {
						Collection<ColorDistribution> colors = model.getFeaturesExtract().getColorStatistics();
						for (ColorDistribution color : colors) {
							double[] xyY = ColorConverter.rgb2xyY(color.getRGB(),
									model.getCameraCalibration().getWorkingSpaceMatrix(),
									model.getCameraCalibration().getWhiteX());
//							g.setColor(color);
							xyY[2] = 0.75;
							g.setColor(new Color(ColorConverter.bgr2rgb(ColorConverter.xyY2bgr(xyY, model.getCameraCalibration().getInverseWorkingSpaceMatrix()))));
							g.drawRect((int)(xyY[0]*CANVAS_SIZE_BIG.getWidth()), (int)((1-xyY[1])*CANVAS_SIZE_BIG.getHeight()), 1, 1);
						}
					} catch(NullPointerException npe) {}
				}
			}

			// Ruler
			g.setColor(Color.BLACK);
			for (double i = 1; i < 20; i++) {
				g.drawLine((int)((i/20.0)*CANVAS_SIZE_BIG.getWidth()), (int)(CANVAS_SIZE_BIG.getHeight()/2)-5, (int)((i/20.0)*CANVAS_SIZE_BIG.getWidth()), (int)(CANVAS_SIZE_BIG.getHeight()/2)+5);
			}
			for (double i = 1; i < 20; i++) {
				g.drawLine((int)(CANVAS_SIZE_BIG.getWidth()/2)-5, (int)((i/20.0)*CANVAS_SIZE_BIG.getHeight()), (int)(CANVAS_SIZE_BIG.getWidth()/2)+5, (int)((i/20.0)*CANVAS_SIZE_BIG.getHeight()));
			}

			// Matching colors
			List<MatchingColor> matching = model.getMatchingColors();
			if(matching != null) {
				g.setColor(Color.BLACK);
				for (MatchingColor matchingColor : matching) {
					double[] descriptor = matchingColor.getDescriptor();
					double w = descriptor[2]*CANVAS_SIZE_BIG.getWidth();
					double h = descriptor[2]*CANVAS_SIZE_BIG.getHeight();
					g.drawOval((int)(descriptor[0]*CANVAS_SIZE_BIG.getWidth() - w),
							(int)((1-descriptor[1])*CANVAS_SIZE_BIG.getHeight() - h),
							(int)(w*2), (int)(h*2));
				}
			} else {
				System.out.println("No matching colors");
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
				double max = histogram.getMaxHeight();
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
		return colorsCanvas;
	}

	public JButton getDisplayImageButton() {
		return displayImageButton;
	}

	public JButton getDisplayXYYButton() {
		return displayXYYButton;
	}

	// ****************** GETTERS ******************

}
