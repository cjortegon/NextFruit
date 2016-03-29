package co.edu.icesi.nextfruit.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;

import co.edu.icesi.nextfruit.modules.Model;
import co.edu.icesi.nextfruit.mvc.interfaces.Attachable;
import co.edu.icesi.nextfruit.mvc.interfaces.Initializable;
import co.edu.icesi.nextfruit.mvc.interfaces.Updateable;
import co.edu.icesi.nextfruit.util.FilesUtility;
import co.edu.icesi.nextfruit.views.MachineLearningWindow;


/**
 * @author JuanD
 */
public class MachineLearningController implements Initializable, ActionListener{

	private static final String CHOOSE_IMAGES_DIR = "ChImgsDir";
	private static final String CHOOSE_TRAINING_SET_DIR = "chTrnSetDir";
	private static final String CHOOSE_CLASSIFIER_DIR = "chClssfrDir";

	private static final String GENERATE_TRAINING_SET = "SaveTrainingSet";
	private static final String LOAD_TRAINING_SET = "ColorChecker";
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

			//		case CHOOSE_TRAINING_SET_DIR:
			//			file = FilesUtility.loadFile(view, "Load Training Set");
			//			if(file != null){
			//				//				model.loadTrainingSet(file);
			//				view.setTfDataFieldText(file.getName().toString());
			//			}
			//			break;
			//
			//		case CHOOSE_CLASSIFIER_DIR:
			//			file = FilesUtility.loadFile(view, "Load Classifier");
			//			if(file != null) {
			//				view.setTfClassifierFieldText(file.getPath().toString());
			//			}
			//			break;

		case GENERATE_TRAINING_SET:
			file = FilesUtility.chooseFileToSave(view, "Save training set to...");
			if(file != null) {
				view.getBtGenerateTrainingSet().setEnabled(false);
				new Thread(new Runnable() {
					@Override
					public void run() {
						model.processTrainingSet(file);
						view.showMessage("Finish processing your file");
					}
				}).start();;
			}
			break;

		case LOAD_TRAINING_SET:
			break;

		case TRAIN_CLASSIFIER:
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

		view.getBtChooseDataDir().setActionCommand(CHOOSE_TRAINING_SET_DIR);
		view.getBtChooseDataDir().addActionListener(this);

		view.getBtChooseClassifierDir().setActionCommand(CHOOSE_CLASSIFIER_DIR);
		view.getBtChooseClassifierDir().addActionListener(this);

		view.getBtGenerateTrainingSet().setActionCommand(GENERATE_TRAINING_SET);
		view.getBtGenerateTrainingSet().addActionListener(this);

		view.getBtLoadTrainingSet().setActionCommand(LOAD_TRAINING_SET);
		view.getBtLoadTrainingSet().addActionListener(this);

		view.getBtTrainClassifier().setActionCommand(TRAIN_CLASSIFIER);
		view.getBtTrainClassifier().addActionListener(this);
	}

}
