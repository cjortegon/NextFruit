package co.edu.icesi.nextfruit.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import co.edu.icesi.nextfruit.modules.Model;
import co.edu.icesi.nextfruit.mvc.interfaces.Attachable;
import co.edu.icesi.nextfruit.mvc.interfaces.Initializable;
import co.edu.icesi.nextfruit.mvc.interfaces.Updateable;
import co.edu.icesi.nextfruit.util.FilesUtility;
import co.edu.icesi.nextfruit.views.CalibrationWindow;
import co.edu.icesi.nextfruit.views.ClassificationWindow;
import co.edu.icesi.nextfruit.views.ComputerVisionWindow;
import co.edu.icesi.nextfruit.views.MachineLearningWindow;
import co.edu.icesi.nextfruit.views.MainMenuWindow;

public class MainMenuController implements Initializable, ActionListener {

	private static final String CALIBRATION = "Calibration";
	private static final String LOAD_SETTINGS = "LoadSettings";
	private static final String CHARACTERIZATION = "Charaterization";
	private static final String CLASIFICATION = "Clasification";
	private static final String TRAINING = "Training";

	private CalibrationWindow calibration;
	private ComputerVisionWindow charaterization;
	private MachineLearningWindow training;
	private ClassificationWindow classification;

	private Model model;
	private MainMenuWindow view;

	@Override
	public void init(Attachable model, Updateable view) {
		this.model = (Model) model;
		this.view = (MainMenuWindow) view;
		addListeners();
	}

	private void addListeners() {
		view.getCalibrationButton().setActionCommand(CALIBRATION);
		view.getCalibrationButton().addActionListener(this);
		view.getLoadSettingsFileButton().setActionCommand(LOAD_SETTINGS);
		view.getLoadSettingsFileButton().addActionListener(this);
		view.getCharacterizationButton().setActionCommand(CHARACTERIZATION);
		view.getCharacterizationButton().addActionListener(this);
		view.getCharacterizationButton().setEnabled(false);
		view.getClasificationButton().setActionCommand(CLASIFICATION);
		view.getClasificationButton().addActionListener(this);
		view.getClasificationButton().setEnabled(false);
		view.getTrainingButton().setActionCommand(TRAINING);
		view.getTrainingButton().addActionListener(this);
		view.getTrainingButton().setEnabled(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		switch (e.getActionCommand()) {

		case CALIBRATION:
			if(calibration == null) {
				calibration = new CalibrationWindow();
				calibration.init(model, null);
			}
			calibration.setVisible(true);
			break;

		case LOAD_SETTINGS:
			File settingsFile = FilesUtility.loadFile(view, "Load camera calibration file");
			if(settingsFile != null) {
				boolean result = this.model.loadCalibrationData(settingsFile);
				if(!result) {
					view.showMessage("Couldn't load the chosen file! Make sure it's a valid file.");
				} else {
					view.showMessage("Settings loaded successfully!");
					view.getCalibrationFileLabel().setText("Calibration file: "+settingsFile.getName());
					view.getCharacterizationButton().setEnabled(true);
					view.getClasificationButton().setEnabled(true);
					view.getTrainingButton().setEnabled(true);
				}
			}
			break;

		case CHARACTERIZATION:
			if(charaterization == null) {
				charaterization = new ComputerVisionWindow();
				charaterization.init(model, null);
			}
			charaterization.setVisible(true);
			break;

		case CLASIFICATION:
			if(classification == null){
				classification = new ClassificationWindow();
				classification.init(model, null);
			}
			
			classification.setVisible(true);
			break;
			
		case TRAINING:
			if(training == null){
				training = new MachineLearningWindow();
				training.init(model, null);
			}
			training.setVisible(true);
			break;
		}
	}

}
