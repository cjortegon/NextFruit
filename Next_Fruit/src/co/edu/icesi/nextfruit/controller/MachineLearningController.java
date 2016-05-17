package co.edu.icesi.nextfruit.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import co.edu.icesi.nextfruit.modules.Model;
import co.edu.icesi.nextfruit.mvc.interfaces.Attachable;
import co.edu.icesi.nextfruit.mvc.interfaces.Initializable;
import co.edu.icesi.nextfruit.mvc.interfaces.Updateable;
import co.edu.icesi.nextfruit.util.FilesUtility;
import co.edu.icesi.nextfruit.util.ProgressUpdatable;
import co.edu.icesi.nextfruit.views.MachineLearningWindow;

/**
 * @author JuanD
 */
public class MachineLearningController implements Initializable, ActionListener, ProgressUpdatable {

	private static final String CHOOSE_IMAGES_DIR = "ChooseImgesDir";
	private static final String GENERATE_TRAINING_SET = "SaveTrainingSet";

	private Model model;
	private MachineLearningWindow view;

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("actionPerformed: "+e.getActionCommand());
		File file;

		switch (e.getActionCommand()) {

		case CHOOSE_IMAGES_DIR:
			file = FilesUtility.loadDirectory(view, "Choose Images Directory");
			if(file != null){
				view.updateFolderLabels(file, model.loadImagesForTraining(file));
				view.getBtGenerateTrainingSet().setEnabled(true);
			}
			break;

		case GENERATE_TRAINING_SET:
			view.clearLog();
			file = FilesUtility.chooseFileToSave(view, "Save training sets...");
			if(file != null) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						view.getBtChooseImagesDir().setEnabled(false);
						view.getBtGenerateTrainingSet().setEnabled(false);
						model.processTrainingSet(file, MachineLearningController.this);
						view.showMessage("Finished processing your file!");
						view.getBtGenerateTrainingSet().setEnabled(true);
						view.getBtChooseImagesDir().setEnabled(true);
					}
				}).start();
			}
			break;
		}
	}

	@Override
	public void init(Attachable model, Updateable view) {
		this.model = (Model) model;
		this.view = (MachineLearningWindow) view;
		addListeners();	
	}

	private void addListeners() {

		view.getBtChooseImagesDir().setActionCommand(CHOOSE_IMAGES_DIR);
		view.getBtChooseImagesDir().addActionListener(this);

		view.getBtGenerateTrainingSet().setActionCommand(GENERATE_TRAINING_SET);
		view.getBtGenerateTrainingSet().addActionListener(this);
		view.getBtGenerateTrainingSet().setEnabled(false);		
	}

	@Override
	public void updateProgress(double percent) {
		view.updatePercentDone(percent);
	}

	@Override
	public void updateMessage(String message) {
		view.log(message);
	}

}
