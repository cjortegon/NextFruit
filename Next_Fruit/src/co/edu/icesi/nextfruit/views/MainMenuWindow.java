package co.edu.icesi.nextfruit.views;

import javax.swing.JButton;
import javax.swing.JLabel;

import co.edu.icesi.nextfruit.controller.MainMenuController;
import co.edu.icesi.nextfruit.mvc.interfaces.Attachable;
import co.edu.icesi.nextfruit.mvc.interfaces.Initializable;
import co.edu.icesi.nextfruit.mvc.interfaces.Updateable;
import visualkey.KFrame;

public class MainMenuWindow extends KFrame implements Initializable, Updateable {

	private JLabel calibrationFile;
	private JButton calibrationButton, characterizationButton, clasificationButton, trainingButton, loadSettingsFileButton;
	private Attachable model;

	public MainMenuWindow() {
	}

	@Override
	public void init(Attachable model, Updateable view) {

		// Initializing objects
		calibrationButton = new JButton("Camera Calibration");
		characterizationButton = new JButton("Fruit Characterisation");
		clasificationButton = new JButton("Fruit Classification");
		trainingButton = new JButton("Training And Configuration");
		loadSettingsFileButton = new JButton("Load Calibration Data From an XML File");
		calibrationFile = new JLabel("(No calibration file loaded)");

		// Adding objects to window
		addComponent(calibrationButton, 0, 0, 1, 1, false);
		addComponent(calibrationFile, 1, 0, 1, 1, true);
		addComponent(loadSettingsFileButton, 2, 0, 1, 1, false);
		addComponent(characterizationButton, 3, 0, 1, 1, false);
		addComponent(clasificationButton, 4, 0, 1, 1, false);
		addComponent(trainingButton, 5, 0, 1, 1, false);

		// Starting controller
		new MainMenuController().init(model, this);

		pack();
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}

	// ************************ ACCESS METHODS ************************

	public JButton getCalibrationButton() {
		return calibrationButton;
	}

	public JButton getCharacterizationButton() {
		return characterizationButton;
	}

	public JButton getClasificationButton() {
		return clasificationButton;
	}

	public JButton getTrainingButton() {
		return trainingButton;
	}

	public JButton getLoadSettingsFileButton() {
		return loadSettingsFileButton;
	}

	public JLabel getCalibrationFileLabel() {
		return calibrationFile;
	}

	// ************************ ACCESS METHODS ************************

	@Override
	public void update() {
	}

}
