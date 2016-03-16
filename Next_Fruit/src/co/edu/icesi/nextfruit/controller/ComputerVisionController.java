package co.edu.icesi.nextfruit.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.JFileChooser;

import co.edu.icesi.nextfruit.modules.Model;
import co.edu.icesi.nextfruit.modules.model.MatchingColor;
import co.edu.icesi.nextfruit.mvc.interfaces.Attachable;
import co.edu.icesi.nextfruit.mvc.interfaces.Initializable;
import co.edu.icesi.nextfruit.mvc.interfaces.Updateable;
import co.edu.icesi.nextfruit.views.ComputerVisionWindow;

public class ComputerVisionController implements Initializable, ActionListener, MouseListener {

	private static final String LOAD_IMAGE = "LoadImage";
	private static final String LOAD_SETTINGS = "LoadSettings";
	private static final String PROCESS = "Process";
	private static final String DISPLAY_IMAGE = "DispImage";
	private static final String DISPLAY_XYY = "DisplayXYY";
	private static final String MATCHING_COLORS = "Matching";
	private static final String ANALIZE = "Analize";

	private Model model;
	private ComputerVisionWindow view;

	@Override
	public void init(Attachable model, Updateable view) {
		this.model = (Model) model;
		this.view = (ComputerVisionWindow) view;
		addListeners();
	}

	private void addListeners() {
		view.getLoadButton().setActionCommand(LOAD_IMAGE);
		view.getLoadButton().addActionListener(this);
		view.getLoadSettingsFileButton().setActionCommand(LOAD_SETTINGS);
		view.getLoadSettingsFileButton().addActionListener(this);
		view.getProcessButton().setActionCommand(PROCESS);
		view.getProcessButton().addActionListener(this);
		view.getUpdateMatchingColorsButton().setActionCommand(MATCHING_COLORS);
		view.getDisplayImageButton().setActionCommand(DISPLAY_IMAGE);
		view.getDisplayImageButton().addActionListener(this);
		view.getDisplayXYYButton().setActionCommand(DISPLAY_XYY);
		view.getDisplayXYYButton().addActionListener(this);
		view.getUpdateMatchingColorsButton().addActionListener(this);
		view.getAnalizeDataButton().setActionCommand(ANALIZE);
		view.getAnalizeDataButton().addActionListener(this);
		view.getColorsCanvas().addMouseListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {

		case LOAD_IMAGE:
			File file = loadFile("Load your fruit image");
			if(file != null)
				model.startFeaturesExtract(file.getAbsolutePath());
			break;

		case LOAD_SETTINGS:
			File settingsFile = loadFile("Load camera calibration file");
			if(settingsFile != null) {
				boolean result = this.model.loadCalibrationData(settingsFile);
				if(!result) {
					view.showMessage("Couldn't load the chosen file! Make sure it's a valid file.");
					view.getCalibrationFile().setText("(No calibration file loaded)");
				} else {
					view.showMessage("Settings loaded successfully!");
					view.getCalibrationFile().setText("Calibration file: "+settingsFile.getName());
				}
			}
			break;

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

		case ANALIZE:
			if(!model.analizeImage())
				view.showMessage("Process image first.");
			break;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		double[] point = view.getPercentOnColorsCanvas(e.getX(), e.getY());
		DecimalFormat numberFormat = new DecimalFormat("0.00");
		view.getMatchingColors().setText(view.getMatchingColors().getText()+"\n"+numberFormat.format(point[0])+","+numberFormat.format(point[1])+",0.05");
		model.identifyMatchingColors(view.getMatchingColors().getText());
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	private File loadFile(String title) {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new java.io.File("./"));
		chooser.setDialogTitle(title);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setVisible(true);
		if (chooser.showOpenDialog(chooser) == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile();
		}
		return null;
	}

}
