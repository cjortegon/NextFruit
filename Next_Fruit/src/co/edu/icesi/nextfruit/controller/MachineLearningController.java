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
 * -----El panel de evaluacion esta incompleto-----
 */
public class MachineLearningController implements Initializable, ActionListener{

	private static final String CHOOSE_IMAGES_DIR = "ChooseImgesDir";
	private static final String GENERATE_TRAINING_SET = "SaveTrainingSet";
	
	private static final String LOAD_QUALITY_TRAINING_SET = "LoadQualityTrainingSet";
	private static final String LOAD_SIZE_TRAINING_SET = "LoadSizeTrainingSet";
	private static final String LOAD_CLASS_TRAINING_SET = "LoadClassTrainingSet";
	private static final String LOAD_RIPENESS_TRAINING_SET = "LoadRipenessTrainingSet";
	
	private static final String TRAIN_QUALITY_CLASSIFIER = "TrainQualityClassifier";
	private static final String TRAIN_SIZE_CLASSIFIER = "TrainSizeClassifier";
	private static final String TRAIN_CLASS_CLASSIFIER = "TrainClassClassifier";
	private static final String TRAIN_RIPENESS_CLASSIFIER = "TrainRipenessClassifier";
	private static final String TRAIN_ALL_CLASSIFIERS = "TrainAllClassifiers";
	
	private static final String CHOOSE_QUALITY_CLASSIFIER_FILE = "ChooseQualityClassifierFile";
	private static final String CHOOSE_SIZE_CLASSIFIER_FILE = "ChooseSizeClassifierFile";
	private static final String CHOOSE_CLASS_CLASSIFIER_FILE = "ChooseClassClassifierFile";
	private static final String CHOOSE_RIPENESS_CLASSIFIER_FILE = "ChooseRipenessClassifierFile";

	private static final String TEST_LOADED_CLASSIFIER = "TestLoadedClassifier";

	private Model model;
	private MachineLearningWindow view;
	
	private File savedClassifier;
	private File savedTrainingSet;
	private String modelType;

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("actionPerformed: "+e.getActionCommand());
		File file;
		
		switch (e.getActionCommand()) {
		
		case CHOOSE_IMAGES_DIR:
			file = FilesUtility.loadDirectory(view, "Choose Images Directory");
			if(file != null){
				view.setLbDirectoryText(file.getPath().toString());
				model.loadImagesForTraining(file);
				view.getBtGenerateTrainingSet().setEnabled(true);
			}
			break;

		case GENERATE_TRAINING_SET:
			file = FilesUtility.chooseFileToSave(view, "Save training sets...");
			if(file != null) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						model.processTrainingSet(file);
						view.showMessage("Finished processing your file!");
						view.getBtGenerateTrainingSet().setEnabled(false);
						view.getBtTrainAllClassifiers().setEnabled(true);
					}
				}).start();
			}
			break;
			
		case LOAD_QUALITY_TRAINING_SET:
			file = FilesUtility.loadFile(view, "Load quality trainning set");
			if(file != null){
				savedTrainingSet = file;
				view.setLbTrainingSetDirText(file.getAbsolutePath());
				deactivateBtns();
				view.getBtTrainQualityClassifier().setEnabled(true);
			}
			break;
			
		case LOAD_SIZE_TRAINING_SET:
			file = FilesUtility.loadFile(view, "Load size trainning set");
			if(file != null){
				savedTrainingSet = file;
				view.setLbTrainingSetDirText(file.getAbsolutePath());
				deactivateBtns();
				view.getBtTrainSizeClassifier().setEnabled(true);
			}
			break;
			
		case LOAD_CLASS_TRAINING_SET:
			file = FilesUtility.loadFile(view, "Load class trainning set");
			if(file != null){
				savedTrainingSet = file;
				view.setLbTrainingSetDirText(file.getAbsolutePath());
				deactivateBtns();
				view.getBtTrainClassClassifier().setEnabled(true);
			}
			break;
			
		case LOAD_RIPENESS_TRAINING_SET:
			file = FilesUtility.loadFile(view, "Load ripeness trainning set");
			if(file != null){
				savedTrainingSet = file;
				view.setLbTrainingSetDirText(file.getAbsolutePath());
				deactivateBtns();
				view.getBtTrainRipenessClassifier().setEnabled(true);
			}
			break;

		case TRAIN_QUALITY_CLASSIFIER:
			file = FilesUtility.chooseFileToSave(view, "Save quality classifier as...");
			if(file != null) {
				view.getBtTrainQualityClassifier().setEnabled(false);
				String classifierType = ModelBuilder.MODEL_TYPES[view.getModelTypeComboBox().getSelectedIndex()];
				new Thread(new Runnable() {
					@Override
					public void run() {
						if(model.trainClassifier(file, classifierType, ModelBuilder.QUALITY_CLASSIFIER)){
							view.showMessage("Model successfully generated");
						}else{
							view.showMessage("Failed generating model");
						}
						view.getBtTrainQualityClassifier().setEnabled(true);
					}
				}).start();
			}
			break;
			
		case TRAIN_SIZE_CLASSIFIER:
			file = FilesUtility.chooseFileToSave(view, "Save size classifier as...");
			if(file != null) {
				view.getBtTrainSizeClassifier().setEnabled(false);
				String classifierType = ModelBuilder.MODEL_TYPES[view.getModelTypeComboBox().getSelectedIndex()];
				new Thread(new Runnable() {
					@Override
					public void run() {
						if(model.trainClassifier(file, classifierType, ModelBuilder.SIZE_CLASSIFIER)){
							view.showMessage("Model successfully generated");
						}else{
							view.showMessage("Failed generating model");
						}
						view.getBtTrainSizeClassifier().setEnabled(true);
					}
				}).start();
			}
			break;
			
		case TRAIN_CLASS_CLASSIFIER:
			file = FilesUtility.chooseFileToSave(view, "Save class classifier as...");
			if(file != null) {
				view.getBtTrainClassClassifier().setEnabled(false);
				String classifierType = ModelBuilder.MODEL_TYPES[view.getModelTypeComboBox().getSelectedIndex()];
				new Thread(new Runnable() {
					@Override
					public void run() {
						if(model.trainClassifier(file, classifierType, ModelBuilder.CLASS_CLASSIFIER)){
							view.showMessage("Model successfully generated");
						}else{
							view.showMessage("Failed generating model");
						}
						view.getBtTrainClassClassifier().setEnabled(true);
					}
				}).start();
			}
			break;
			
		case TRAIN_RIPENESS_CLASSIFIER:
			file = FilesUtility.chooseFileToSave(view, "Save size classifier as...");
			if(file != null) {
				view.getBtTrainRipenessClassifier().setEnabled(false);
				String classifierType = ModelBuilder.MODEL_TYPES[view.getModelTypeComboBox().getSelectedIndex()];
				new Thread(new Runnable() {
					@Override
					public void run() {
						if(model.trainClassifier(file, classifierType, ModelBuilder.RIPENESS_CLASSIFIER)){
							view.showMessage("Model successfully generated");
						}else{
							view.showMessage("Failed generating model");
						}
						view.getBtTrainRipenessClassifier().setEnabled(true);
					}
				}).start();
			}
			break;
			
		case CHOOSE_QUALITY_CLASSIFIER_FILE:
			file = FilesUtility.loadFile(view, "Load a quality Classifier");
			if(file != null){
				modelType = ModelBuilder.QUALITY_CLASSIFIER;
				savedClassifier = file;
				view.setLbClassifierDirText(file.getAbsolutePath().toString());
				view.getBtTestClassifier().setEnabled(true);
			}
			break;
			
		case CHOOSE_SIZE_CLASSIFIER_FILE:
			file = FilesUtility.loadFile(view, "Load a size Classifier");
			if(file != null){
				modelType = ModelBuilder.SIZE_CLASSIFIER;
				savedClassifier = file;
				view.setLbClassifierDirText(file.getAbsolutePath().toString());
				view.getBtTestClassifier().setEnabled(true);
			}
			break;
		case CHOOSE_CLASS_CLASSIFIER_FILE:
			file = FilesUtility.loadFile(view, "Load a class Classifier");
			if(file != null){
				modelType = ModelBuilder.CLASS_CLASSIFIER;
				savedClassifier = file;
				view.setLbClassifierDirText(file.getAbsolutePath().toString());
				view.getBtTestClassifier().setEnabled(true);
			}
			break;
		case CHOOSE_RIPENESS_CLASSIFIER_FILE:
			file = FilesUtility.loadFile(view, "Load a ripeness Classifier");
			if(file != null){
				modelType = ModelBuilder.RIPENESS_CLASSIFIER;
				savedClassifier = file;
				view.setLbClassifierDirText(file.getAbsolutePath().toString());
				view.getBtTestClassifier().setEnabled(true);
			}
			break;
			
		case TEST_LOADED_CLASSIFIER:
			if(savedClassifier != null && modelType != null){
				
				//
				//	LLAMAR AL METODO EVALUATE AQUÍ
				// -- PENDIENTE DE TERMINAR --
				//
				model.testClassifier("");
				//
				//	LLAMAR AL METODO EVALUATE AQUÍ
				// -- PENDIENTE DE TERMINAR --
				//
				view.showMessage("<html>Evaluation executed successfully!<br>"
						+ "Check the Log for more information.</html>");
				view.getBtTestClassifier().setEnabled(false);
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
		view.getBtGenerateTrainingSet().setEnabled(false);		
		
		
		view.getBtLoadQualityTrainingSet().setActionCommand(LOAD_QUALITY_TRAINING_SET);
		view.getBtLoadQualityTrainingSet().addActionListener(this);
		
		view.getBtLoadSizeTrainingSet().setActionCommand(LOAD_SIZE_TRAINING_SET);
		view.getBtLoadSizeTrainingSet().addActionListener(this);
		
		view.getBtLoadClassTrainingSet().setActionCommand(LOAD_CLASS_TRAINING_SET);
		view.getBtLoadClassTrainingSet().addActionListener(this);
		
		view.getBtLoadRipenessTrainingSet().setActionCommand(LOAD_RIPENESS_TRAINING_SET);
		view.getBtLoadRipenessTrainingSet().addActionListener(this);

		
		view.getBtTrainQualityClassifier().setActionCommand(TRAIN_QUALITY_CLASSIFIER);
		view.getBtTrainQualityClassifier().addActionListener(this);
		view.getBtTrainQualityClassifier().setEnabled(false);
		
		view.getBtTrainSizeClassifier().setActionCommand(TRAIN_SIZE_CLASSIFIER);
		view.getBtTrainSizeClassifier().addActionListener(this);
		view.getBtTrainSizeClassifier().setEnabled(false);
		
		view.getBtTrainClassClassifier().setActionCommand(TRAIN_CLASS_CLASSIFIER);
		view.getBtTrainClassClassifier().addActionListener(this);
		view.getBtTrainClassClassifier().setEnabled(false);
		
		view.getBtTrainRipenessClassifier().setActionCommand(TRAIN_RIPENESS_CLASSIFIER);
		view.getBtTrainRipenessClassifier().addActionListener(this);
		view.getBtTrainRipenessClassifier().setEnabled(false);
		
		view.getBtTrainAllClassifiers().setActionCommand(TRAIN_ALL_CLASSIFIERS);
		view.getBtTrainAllClassifiers().addActionListener(this);
		view.getBtTrainAllClassifiers().setEnabled(false);
		
		
		view.getBtLoadQualityClassifier().setActionCommand(CHOOSE_QUALITY_CLASSIFIER_FILE);
		view.getBtLoadQualityClassifier().addActionListener(this);
		view.getBtLoadQualityClassifier().setEnabled(false);
		
		view.getBtLoadSizeClassifier().setActionCommand(CHOOSE_SIZE_CLASSIFIER_FILE);
		view.getBtLoadSizeClassifier().addActionListener(this);
		view.getBtLoadSizeClassifier().setEnabled(false);
		
		view.getBtLoadClassClassifier().setActionCommand(CHOOSE_CLASS_CLASSIFIER_FILE);
		view.getBtLoadClassClassifier().addActionListener(this);
		view.getBtLoadClassClassifier().setEnabled(false);
		
		view.getBtLoadRipenessClassifier().setActionCommand(CHOOSE_RIPENESS_CLASSIFIER_FILE);
		view.getBtLoadRipenessClassifier().addActionListener(this);
		view.getBtLoadRipenessClassifier().setEnabled(false);
		
		
		view.getBtTestClassifier().setActionCommand(TEST_LOADED_CLASSIFIER);
		view.getBtTestClassifier().addActionListener(this);
		view.getBtTestClassifier().setEnabled(false);
	}
	
	
	private void deactivateBtns(){
		view.getBtTrainQualityClassifier().setEnabled(false);
		view.getBtTrainSizeClassifier().setEnabled(false);
		view.getBtTrainClassClassifier().setEnabled(false);
		view.getBtTrainRipenessClassifier().setEnabled(false);
	}

}
