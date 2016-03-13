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

public class CalibrationController implements Initializable, ActionListener {

	private static final String LOAD_COLOR_CHECKER = "ColorChecker";
	private static final String LOAD_SIZE_CALIBRATION = "SizeCalibrator";
	private static final String PROCESS = "Process";
	private static final String RESULTS = "Results";
	private static final String LOAD_SETTINGS = "LoadSettings"; 

	private Model model;
	private CalibrationWindow view;
	private CalibrationResultsWindow results;
	private boolean colorCheckerLoaded, sizeCalibrationLoaded;

	@Override
	public void init(Attachable model, Updateable view) {
		this.model = (Model) model;
		this.view = (CalibrationWindow) view;
		addListeners();
	}

	private void addListeners() {
		view.getLoadColorCheckerButton().setActionCommand(LOAD_COLOR_CHECKER);
		view.getLoadColorCheckerButton().addActionListener(this);
		view.getLoadSizeCalibrationButton().setActionCommand(LOAD_SIZE_CALIBRATION);
		view.getLoadSizeCalibrationButton().addActionListener(this);
		view.getProcessButton().setActionCommand(PROCESS);
		view.getProcessButton().addActionListener(this);
		view.getResultsButton().setActionCommand(RESULTS);
		view.getResultsButton().addActionListener(this);

		view.getBtLoadCalData().setActionCommand(LOAD_SETTINGS);
		view.getBtLoadCalData().addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("actionPerformed: "+e.getActionCommand());
		switch (e.getActionCommand()) {

		case LOAD_COLOR_CHECKER:
			try {
				File colorCheckerFile = loadFile("Cargar foto del color checker");
				System.out.println("Image selected: "+colorCheckerFile.getName());
				model.startColorChecker(colorCheckerFile.getAbsolutePath());
				colorCheckerLoaded = true;
			} catch (NullPointerException npe) {}
			break;

		case LOAD_SIZE_CALIBRATION:
			try {
				File sizeCalibrationFile = loadFile("Cargar foto de la cuadricula");
				System.out.println("Image selected: "+sizeCalibrationFile.getName());
				model.startSizeCalibrator(sizeCalibrationFile.getAbsolutePath());
				sizeCalibrationLoaded = true;
			} catch (NullPointerException npe) {}
			break;

		case PROCESS:
			try {
				File matrix = (File) view.getMatrixComboBox().getItemAt(view.getMatrixComboBox().getSelectedIndex());
				try {
					model.calibrate(matrix, Double.valueOf(view.getSizeCalibrationMeasure().getText()));
					view.getResultsButton().setEnabled(true);
				} catch (IOException e1) {
					view.showMessage("Matrix file not found");
				}
			} catch (NumberFormatException nfe) {
				view.showMessage("Escriba el tamaño de la cuadricula en cm para calibrar las distancias.");
			}
			break;

		case RESULTS:
			if(results == null) {
				results = new CalibrationResultsWindow();
				results.init(model, null);
			}
			results.setVisible(true);
			break;

		case LOAD_SETTINGS:
			this.model.startCalDataHandler();
			File file = loadFile("");
			if(file != null) {
				boolean result = this.model.loadCalibrationData(file);
				if(!result){
					JOptionPane.showMessageDialog(this.view,
							"Couldn't load the chosen file! Make sure it's a valid file.");
				}
			}

			break;
		}

		// Enabling process button after both images have been loaded
		view.getProcessButton().setEnabled(colorCheckerLoaded && sizeCalibrationLoaded);
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
