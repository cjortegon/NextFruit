package co.edu.icesi.nextfruit.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import co.edu.icesi.nextfruit.modules.Model;
import co.edu.icesi.nextfruit.mvc.interfaces.Attachable;
import co.edu.icesi.nextfruit.mvc.interfaces.Initializable;
import co.edu.icesi.nextfruit.mvc.interfaces.Updateable;
import co.edu.icesi.nextfruit.views.CalibrationWindow;
import co.edu.icesi.nextfruit.views.ComputerVisionWindow;
import co.edu.icesi.nextfruit.views.MachineLearningWindow;
import co.edu.icesi.nextfruit.views.MainMenuWindow;

public class MainMenuController implements Initializable, ActionListener {

	private static final String CALIBRATION = "Calibration";
	private static final String CHARACTERIZATION = "Charaterization";
	private static final String CLASIFICATION = "Clasificator";
	private static final String TRAINING = "Training";
	
	private CalibrationWindow calibration;
	private ComputerVisionWindow charaterization;
	private MachineLearningWindow training;

	private Model model;
	private Updateable view;

	@Override
	public void init(Attachable model, Updateable view) {
		this.model = (Model) model;
		this.view = view;
		addListeners();
	}

	private void addListeners() {
		MainMenuWindow mainMenu = (MainMenuWindow) view;
		mainMenu.getCalibrationButton().setActionCommand(CALIBRATION);
		mainMenu.getCalibrationButton().addActionListener(this);
		mainMenu.getCharacterizationButton().setActionCommand(CHARACTERIZATION);
		mainMenu.getCharacterizationButton().addActionListener(this);
		mainMenu.getClasificationButton().setActionCommand(CLASIFICATION);
		mainMenu.getClasificationButton().addActionListener(this);
		mainMenu.getTrainingButton().setActionCommand(TRAINING);
		mainMenu.getTrainingButton().addActionListener(this);
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

		case CHARACTERIZATION:
			if(charaterization == null) {
				charaterization = new ComputerVisionWindow();
				charaterization.init(model, null);
			}
			charaterization.setVisible(true);
			break;

		case CLASIFICATION:
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
