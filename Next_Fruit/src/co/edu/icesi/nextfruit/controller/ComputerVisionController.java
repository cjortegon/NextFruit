package co.edu.icesi.nextfruit.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import co.edu.icesi.nextfruit.modules.Model;
import co.edu.icesi.nextfruit.mvc.interfaces.Attachable;
import co.edu.icesi.nextfruit.mvc.interfaces.Initializable;
import co.edu.icesi.nextfruit.mvc.interfaces.Updateable;
import co.edu.icesi.nextfruit.views.CalibrationResultsWindow;
import co.edu.icesi.nextfruit.views.CalibrationWindow;
import co.edu.icesi.nextfruit.views.ComputerVisionWindow;

public class ComputerVisionController implements Initializable, ActionListener {

	private static final String LOAD_IMAGE = "LoadImage";
	private static final String LOAD_SETTINGS = "LoadSettings";
	private static final String PROCESS = "Process";

	private Model model;
	private ComputerVisionWindow view;
	private boolean colorCheckerLoaded, sizeCalibrationLoaded;

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
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("actionPerformed: "+e.getActionCommand());
		switch (e.getActionCommand()) {

		case LOAD_IMAGE:
			File file = loadFile("Load your fruit image");
			if(file != null)
				model.startFeaturesExtract(file.getAbsolutePath());
			break;

		case LOAD_SETTINGS:
			this.model.startCalDataHandler();
			File settingsFile = loadFile("Load camera calibration file");
			if(settingsFile != null) {
				boolean result = this.model.loadCalibrationData(settingsFile);
				if(!result) {
					view.showMessage("Couldn't load the chosen file! Make sure it's a valid file.");
				} else {
					view.showMessage("Settings loaded successfully!");
				}
			}
			break;

		case PROCESS:
			model.extractFeatures();
			break;
		}
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
