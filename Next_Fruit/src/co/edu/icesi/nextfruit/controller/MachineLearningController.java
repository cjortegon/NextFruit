package co.edu.icesi.nextfruit.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import co.edu.icesi.nextfruit.modules.Model;
import co.edu.icesi.nextfruit.modules.ModelBuilder;
import co.edu.icesi.nextfruit.mvc.interfaces.Attachable;
import co.edu.icesi.nextfruit.mvc.interfaces.Initializable;
import co.edu.icesi.nextfruit.mvc.interfaces.Updateable;
import co.edu.icesi.nextfruit.util.FilesUtility;
import co.edu.icesi.nextfruit.views.MachineLearningWindow;


/**
 * @author JuanD
 */
public class MachineLearningController implements Initializable, ActionListener{

	private static final String CHOOSE_IMAGES_DIR = "ChooseImgesDir";
	private static final String GENERATE_TRAINING_SET = "SaveTrainingSet";
	private static final String LOAD_TRAINING_SET = "LoadTrainingSet";
	private static final String TRAIN_CLASSIFIER = "TrainClassifier";

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
				view.setTfImagesFieldText(file.getPath().toString());
				model.loadImagesForTraining(file);
			}
			break;

		case GENERATE_TRAINING_SET:
			file = FilesUtility.chooseFileToSave(view, "Save training set...");
			if(file != null) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						model.processTrainingSet(file);
						view.showMessage("Finish processing your file");
						view.getBtTrainClassifier().setEnabled(true);
					}
				}).start();;
			}
			break;

		case LOAD_TRAINING_SET:
			file = FilesUtility.loadFile(view, "Load trainning set");
			if(file != null) {
				if(model.loadTrainingSet(file))
					view.getBtTrainClassifier().setEnabled(true);
			}
			break;

		case TRAIN_CLASSIFIER:
			file = FilesUtility.chooseFileToSave(view, "Save classifier...");
			if(file != null) {
				view.getBtTrainClassifier().setEnabled(false);
				String classifierType = ModelBuilder.MODEL_TYPES[view.getModelTypeComboBox().getSelectedIndex()];
				new Thread(new Runnable() {
					@Override
					public void run() {
						if(model.trainClassifier(file, classifierType))
							view.showMessage("Model successfully generated");
						else
							view.showMessage("Failed generating model");
						view.getBtTrainClassifier().setEnabled(true);
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

	private void addListeners(){

		view.getBtChooseImagesDir().setActionCommand(CHOOSE_IMAGES_DIR);
		view.getBtChooseImagesDir().addActionListener(this);

		view.getBtGenerateTrainingSet().setActionCommand(GENERATE_TRAINING_SET);
		view.getBtGenerateTrainingSet().addActionListener(this);

		view.getBtLoadTrainingSet().setActionCommand(LOAD_TRAINING_SET);
		view.getBtLoadTrainingSet().addActionListener(this);

		view.getBtTrainClassifier().setActionCommand(TRAIN_CLASSIFIER);
		view.getBtTrainClassifier().addActionListener(this);
		view.getBtTrainClassifier().setEnabled(false);
	}

}