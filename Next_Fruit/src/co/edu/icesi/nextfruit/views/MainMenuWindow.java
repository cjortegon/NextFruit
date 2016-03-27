package co.edu.icesi.nextfruit.views;

import javax.swing.JButton;
import javax.swing.JLabel;

import co.edu.icesi.nextfruit.controller.MainMenuController;
import co.edu.icesi.nextfruit.mvc.interfaces.Attachable;
import co.edu.icesi.nextfruit.mvc.interfaces.Initializable;
import co.edu.icesi.nextfruit.mvc.interfaces.Updateable;
import visualkey.KFrame;

public class MainMenuWindow extends KFrame implements Initializable, Updateable {

	private KFrame calibrator, caracterizator, clasification;
<<<<<<< HEAD
	private JButton calibrationButton, characterizationButton, clasificationButton;
	private JButton loadSettingsFileButton;
	private JLabel calibrationFile;
=======
	private JButton calibrationButton, characterizationButton, clasificationButton, trainingButton;
>>>>>>> e8ea9e4583193c17b7ce5d297546c2165944a6e9
	private Attachable model;

	public MainMenuWindow() {
	}

	@Override
	public void init(Attachable model, Updateable view) {

		// Initializing objects
<<<<<<< HEAD
		calibrationButton = new JButton("Calibrar");
		characterizationButton = new JButton("Caracterizar");
		clasificationButton = new JButton("Clasificar");
		loadSettingsFileButton = new JButton("Load Calibration Data From an XML File");
		calibrationFile = new JLabel("(No calibration file loaded)");

		// Adding objects to window
		addComponent(calibrationButton, 0, 0, 1, 1, false);
		addComponent(calibrationFile, 1, 0, 1, 1, true);
		addComponent(loadSettingsFileButton, 2, 0, 1, 1, false);
		addComponent(characterizationButton, 3, 0, 1, 1, false);
		addComponent(clasificationButton, 4, 0, 1, 1, false);
=======
		calibrationButton = new JButton("Camera Calibration");
		characterizationButton = new JButton("Fruit Characterisation");
		clasificationButton = new JButton("Fruit Classification");
		trainingButton = new JButton("Training And Configuration");
		

		// Adding objects to window
		addComponent(calibrationButton, 0, 0, 1, 1, false);
		addComponent(characterizationButton, 1, 0, 1, 1, false);
		addComponent(clasificationButton, 2, 0, 1, 1, false);
		addComponent(trainingButton, 3, 0, 1, 1, false);
>>>>>>> e8ea9e4583193c17b7ce5d297546c2165944a6e9

		// Starting controller
		new MainMenuController().init(model, this);

		pack();
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}

	public JButton getCalibrationButton() {
		return calibrationButton;
	}

	public void setCalibrationButton(JButton calibrationButton) {
		this.calibrationButton = calibrationButton;
	}

	public JButton getCharacterizationButton() {
		return characterizationButton;
	}

	public void setCharacterizationButton(JButton characterizationButton) {
		this.characterizationButton = characterizationButton;
	}

	public JButton getClasificationButton() {
		return clasificationButton;
	}

	public void setClasificationButton(JButton clasificationButton) {
		this.clasificationButton = clasificationButton;
	}

	public JButton getTrainingButton() {
		return trainingButton;
	}

	public void setTrainingButton(JButton trainingButton) {
		this.trainingButton = trainingButton;
	}

	@Override
	public void update() {
	}

}
