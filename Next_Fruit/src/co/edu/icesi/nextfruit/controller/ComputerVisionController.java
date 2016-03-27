package co.edu.icesi.nextfruit.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

import javax.swing.JFileChooser;

import co.edu.icesi.nextfruit.modules.Model;
import co.edu.icesi.nextfruit.modules.model.MatchingColor;
import co.edu.icesi.nextfruit.mvc.interfaces.Attachable;
import co.edu.icesi.nextfruit.mvc.interfaces.Initializable;
import co.edu.icesi.nextfruit.mvc.interfaces.Updateable;
import co.edu.icesi.nextfruit.util.FilesUtility;
import co.edu.icesi.nextfruit.views.ComputerVisionWindow;

public class ComputerVisionController implements Initializable, ActionListener, MouseListener, MouseMotionListener {

	private static final String LOAD_IMAGE = "LoadImage";
//	private static final String LOAD_SETTINGS = "LoadSettings";
	private static final String PROCESS = "Process";
	private static final String DISPLAY_IMAGE = "DispImage";
	private static final String DISPLAY_XYY = "DisplayXYY";
	private static final String INCREASE_Y = "IncreaseY";
	private static final String DECREASE_Y = "DecreaseY";
	private static final String MATCHING_COLORS = "Matching";
	private static final String ANALIZE = "Analize";

	private Model model;
	private ComputerVisionWindow view;
	private boolean colorScroller;

	@Override
	public void init(Attachable model, Updateable view) {
		this.model = (Model) model;
		this.view = (ComputerVisionWindow) view;
		this.colorScroller = true;
		addListeners();
	}

	private void addListeners() {
		view.getLoadButton().setActionCommand(LOAD_IMAGE);
		view.getLoadButton().addActionListener(this);
//		view.getLoadSettingsFileButton().setActionCommand(LOAD_SETTINGS);
//		view.getLoadSettingsFileButton().addActionListener(this);
		view.getProcessButton().setActionCommand(PROCESS);
		view.getProcessButton().addActionListener(this);
		view.getUpdateMatchingColorsButton().setActionCommand(MATCHING_COLORS);
		view.getDisplayImageButton().setActionCommand(DISPLAY_IMAGE);
		view.getDisplayImageButton().addActionListener(this);
		view.getDisplayXYYButton().setActionCommand(DISPLAY_XYY);
		view.getDisplayXYYButton().addActionListener(this);
		view.getIncreaseLuminance().setActionCommand(INCREASE_Y);
		view.getIncreaseLuminance().addActionListener(this);
		view.getDecreaseLuminance().setActionCommand(DECREASE_Y);
		view.getDecreaseLuminance().addActionListener(this);
		view.getUpdateMatchingColorsButton().addActionListener(this);
		view.getAnalizeDataButton().setActionCommand(ANALIZE);
		view.getAnalizeDataButton().addActionListener(this);
		view.getColorsCanvas().addMouseListener(this);
		view.getImageCanvas().addMouseListener(this);
		view.getImageCanvas().addMouseMotionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {

		case LOAD_IMAGE:
			File file = FilesUtility.loadFile("Load your fruit image");
			if(file != null)
				model.startFeaturesExtract(file.getAbsolutePath());
			break;

//		case LOAD_SETTINGS:
//			File settingsFile = FilesUtility.loadFile("Load camera calibration file");
//			if(settingsFile != null) {
//				boolean result = this.model.loadCalibrationData(settingsFile);
//				if(!result) {
//					view.showMessage("Couldn't load the chosen file! Make sure it's a valid file.");
//					view.getCalibrationFile().setText("(No calibration file loaded)");
//				} else {
//					view.showMessage("Settings loaded successfully!");
//					view.getCalibrationFile().setText("Calibration file: "+settingsFile.getName());
//				}
//			}
//			break;

		case PROCESS:
			if(!model.extractFeatures())
				view.showMessage("Load image and camera calibration before processing image.");
			break;

		case MATCHING_COLORS:
			model.identifyMatchingColors(view.getMatchingColors().getText());
			break;

		case DISPLAY_IMAGE:
			view.displayImageDistribution();
			view.update();
			break;

		case DISPLAY_XYY:
			view.displayColorSpace();
			view.update();
			break;

		case INCREASE_Y:
			double incValue = Double.valueOf(view.getLuminanceField().getText())+0.05;
			DecimalFormat numberFormat1 = new DecimalFormat("0.00");
			view.getLuminanceField().setText(""+numberFormat1.format(incValue));
			view.update();
			break;

		case DECREASE_Y:
			double decValue = Double.valueOf(view.getLuminanceField().getText())-0.05;
			if(decValue < 0)
				decValue = 0;
			DecimalFormat numberFormat2 = new DecimalFormat("0.00");
			view.getLuminanceField().setText(""+numberFormat2.format(decValue));
			view.update();
			break;

		case ANALIZE:
			if(!model.analizeImage())
				view.showMessage("Process image first.");
			break;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getSource() == view.getColorsCanvas()) {
			double[] point = view.getPercentOnColorsCanvas(e.getX(), e.getY());
			DecimalFormat numberFormat = new DecimalFormat("0.00");
			String newPoint = numberFormat.format(point[0])+";"+numberFormat.format(point[1])+";0.05";
			newPoint = newPoint.replace(",", ".");
			view.getMatchingColors().setText(view.getMatchingColors().getText()+"\n"+newPoint);
			model.identifyMatchingColors(view.getMatchingColors().getText());
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getSource() == view.getImageCanvas()) {
			this.colorScroller = !colorScroller;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if(e.getSource() == view.getImageCanvas()) {
			this.colorScroller = true;
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if(e.getSource() == view.getImageCanvas() && colorScroller)
			view.realTimeColorSlider(0, 0, false);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if(colorScroller)
			view.realTimeColorSlider(e.getX(), e.getY(), true);
	}
}
