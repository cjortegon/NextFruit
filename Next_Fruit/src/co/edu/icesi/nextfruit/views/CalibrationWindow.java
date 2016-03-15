package co.edu.icesi.nextfruit.views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.opencv.core.Mat;
import org.opencv.core.Point;

import co.edu.icesi.nextfruit.controller.CalibrationController;
import co.edu.icesi.nextfruit.modules.Model;
import co.edu.icesi.nextfruit.modules.model.PolygonWrapper;
import co.edu.icesi.nextfruit.mvc.interfaces.Attachable;
import co.edu.icesi.nextfruit.mvc.interfaces.Initializable;
import co.edu.icesi.nextfruit.mvc.interfaces.Updateable;
import co.edu.icesi.nextfruit.util.File;
import co.edu.icesi.nextfruit.util.FilesUtility;
import co.edu.icesi.nextfruit.util.ImageUtility;
import visualkey.KFrame;
import visualkey.KPanel;

public class CalibrationWindow extends KFrame implements Initializable, Updateable {

	private static final Dimension CANVAS_SIZE = new Dimension(300, 200);
	private static final String MATRIX_FOLDER = "./Matrices";
	private static final int BOX_SIZE = 3;

	private Model model;
	private JButton loadColorCheckerButton, loadSizeCalibrationButton, processButton, resultsButton;
	private JComboBox<File> matrixComboBox;
	private JTextField sizeCalibrationMeasure;
	private JLabel colorCheckerStatus, sizeCalibrationStatus;
	private ColorCheckerCanvas colorCheckerCanvas;
	private SizeCalibrationCanvas sizeCalibrationCanvas;
	private Mat colorCheckerMat, sizeCalibrationMat;
	private Image colorCheckerImage, sizeCalibrationImage;
	private File matrixFiles[];
	
	private JButton btLoadCalData;

	
	public CalibrationWindow() {
		matrixFiles = FilesUtility.listFiles(MATRIX_FOLDER, "csv");
	}

	@Override
	public void init(Attachable model, Updateable view) {

		// Initializing objects
		loadColorCheckerButton = new JButton("Load color checker image");
		loadSizeCalibrationButton = new JButton("Load grid image");
		processButton = new JButton("Process");
		processButton.setEnabled(false);
		resultsButton = new JButton("View Results");
		resultsButton.setEnabled(false);
		matrixComboBox = new JComboBox<>(matrixFiles);
		sizeCalibrationMeasure = new JTextField();
		colorCheckerStatus = new JLabel("-o-");
		sizeCalibrationStatus = new JLabel("-o-");
		colorCheckerCanvas = new ColorCheckerCanvas(CANVAS_SIZE);
		sizeCalibrationCanvas = new SizeCalibrationCanvas(CANVAS_SIZE);
		
		btLoadCalData = new JButton("Load Calibration Data From an XML File");

		// Adding objects to window
		addComponent(loadColorCheckerButton, 0, 0, 1, 1, false);
		addComponent(loadSizeCalibrationButton, 0, 1, 1, 1, false);
		addComponent(colorCheckerCanvas, 1, 0, 1, 1, false);
		addComponent(sizeCalibrationCanvas, 1, 1, 1, 1, false);
		addComponent(colorCheckerStatus, 2, 0, 1, 1, false);
		addComponent(sizeCalibrationStatus, 2, 1, 1, 1, false);
		addLabel("Select an RGB working space matrix:", 3, 0, 1, 1, false);
		addLabel("width of a square (in cm):", 3, 1, 1, 1, false);
		addComponent(matrixComboBox, 4, 0, 1, 1, false);
		addComponent(sizeCalibrationMeasure, 4, 1, 1, 1, false);
		addComponent(processButton, 5, 0, 1, 1, true);
		addComponent(resultsButton, 5, 1, 1, 1, true);
		addComponent(btLoadCalData, 6, 0, 2, 1, true);


		// Attaching to model
		this.model = (Model) model;
		model.attach(this);

		// Starting controller
		new CalibrationController().init(model, this);

		// Ending initialization
		pack();
		setResizable(false);
	}

	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
		update();
	}

	public class ColorCheckerCanvas extends KPanel {

		public ColorCheckerCanvas(Dimension canvasSize) {
			super(canvasSize);
		}

		public void paintComponent(Graphics g) {
			if(colorCheckerImage != null) {
				try {
					double size[] = ImageUtility.drawImage(colorCheckerImage, CANVAS_SIZE, g);
					ArrayList<PolygonWrapper> boxes = model.getColorChecker().getColorBoxes();
					for (PolygonWrapper box : boxes) {
						g.setColor(Color.black);
						g.drawRect((int)(box.getCenter().x*size[2])-BOX_SIZE, (int)(box.getCenter().y*size[2])-BOX_SIZE, BOX_SIZE*2, BOX_SIZE*2);
						g.setColor(Color.white);
						g.fillRect((int)(box.getCenter().x*size[2])-BOX_SIZE+1, (int)(box.getCenter().y*size[2])-BOX_SIZE+1, BOX_SIZE*2-1, BOX_SIZE*2-1);
						int[] xs = new int[box.getPolygon().length];
						int[] ys = new int[box.getPolygon().length];
						int i = 0;
						for (Point p : box.getPolygon()) {
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
				g.fillRect(2, 2, CANVAS_SIZE.width-2, CANVAS_SIZE.height-2);
			}
		}
	}

	public class SizeCalibrationCanvas extends KPanel {

		public SizeCalibrationCanvas(Dimension canvasSize) {
			super(canvasSize);
		}

		public void paintComponent(Graphics g) {
			if(sizeCalibrationImage != null) {
				try {
					double size[] = ImageUtility.drawImage(sizeCalibrationImage, CANVAS_SIZE, g);
					for (Point p : model.getSizeCalibrator().getPoints()) {
						int x = (int)(p.x*size[2]);
						int y = (int)(p.y*size[2]);
						g.fillRect(x-BOX_SIZE, y-BOX_SIZE, BOX_SIZE*2, BOX_SIZE*2);
					}
				} catch(NullPointerException npe) {}
			} else {
				g.setColor(Color.white);
				g.fillRect(2, 2, CANVAS_SIZE.width-2, CANVAS_SIZE.height-2);
			}
		}
	}

	public JButton getLoadColorCheckerButton() {
		return loadColorCheckerButton;
	}

	public JButton getLoadSizeCalibrationButton() {
		return loadSizeCalibrationButton;
	}

	public JButton getProcessButton() {
		return processButton;
	}

	public JButton getResultsButton() {
		return resultsButton;
	}

	public JTextField getSizeCalibrationMeasure() {
		return sizeCalibrationMeasure;
	}

	public JComboBox getMatrixComboBox() {
		return matrixComboBox;
	}

	@Override
	public void update() {
		if(isVisible()) {

			// Converting mat to images to show in canvas
			try {
				if(colorCheckerMat != model.getColorChecker().getBGR()) {
					colorCheckerMat = model.getColorChecker().getBGR();
					colorCheckerImage = ImageUtility.mat2Image(colorCheckerMat);
				}
			} catch(NullPointerException npe) {}
			try {
				if(sizeCalibrationMat != model.getSizeCalibrator().getImage()) {
					sizeCalibrationMat = model.getSizeCalibrator().getImage();
					sizeCalibrationImage = ImageUtility.mat2Image(sizeCalibrationMat);
				}
			} catch(NullPointerException npe) {}

			// Color checker status message
			if(colorCheckerImage == null) {
				colorCheckerStatus.setText("Color checker image not loaded yet");
			} else if(model.getColorChecker().getColorBoxes() == null){
				colorCheckerStatus.setText("Color checker ready to process");
			} else {
				colorCheckerStatus.setText("Color checker processed");
			}

			// Size calibration status message
			if(sizeCalibrationImage == null) {
				sizeCalibrationStatus.setText("Size calibration image not loaded yet");
			} else if(model.getSizeCalibrator().getPoints() == null){
				sizeCalibrationStatus.setText("Size calibration ready to process");
			} else {
				sizeCalibrationStatus.setText("Size calibration processed");
			}

			// Repainting components
			repaint();
		}
	}

	public JButton getBtLoadCalData() {
		return btLoadCalData;
	}

}
