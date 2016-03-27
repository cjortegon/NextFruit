package co.edu.icesi.nextfruit.views;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import co.edu.icesi.nextfruit.controller.MachineLearningController;
import co.edu.icesi.nextfruit.modules.Model;
import co.edu.icesi.nextfruit.mvc.interfaces.Attachable;
import co.edu.icesi.nextfruit.mvc.interfaces.Initializable;
import co.edu.icesi.nextfruit.mvc.interfaces.Updateable;
import visualkey.KFrame;

/**
 * This class represents a window that displays .... ... ...
 * @author JuanD
 *
 */
public class MachineLearningWindow extends KFrame implements Initializable, Updateable{

	private Model model;
	private JLabel lbImagesDirectory, lbDataDirectory, lbClassifierDirectory;
	private JTextField tfImagesField, tfDataField, tfClassifierField;
	private JButton btGenerateTrainingSet, btLoadTrainingSet, btTrainClassifier, 
	btChooseImagesDir, btChooseDataDir, btChooseClassifierDir;
	private JComboBox<String> modelType;
	
	
	@Override
	public void update() {
		if(isVisible()) {
			// Repainting components
			repaint();
		}		
	}

	
	@Override
	public void init(Attachable model, Updateable view) {

		//	Initialize objects
		
		lbImagesDirectory = new JLabel("Load images from directory: ");
		tfImagesField = new JTextField();
		tfImagesField.setPreferredSize(new Dimension(200, 20));
		tfImagesField.setEnabled(false);
		btChooseImagesDir = new JButton("Choose directory");
		
		lbDataDirectory = new JLabel("Save the training set file in directory: ");
		tfDataField = new JTextField();
		tfDataField.setPreferredSize(new Dimension(200, 20));
		tfDataField.setEnabled(false);
		btChooseDataDir = new JButton("Choose directory");
		
		lbClassifierDirectory = new JLabel("Save the classifier file in directory: ");
		tfClassifierField = new JTextField();
		tfClassifierField.setPreferredSize(new Dimension(200, 20));
		tfClassifierField.setEnabled(false);
		btChooseClassifierDir = new JButton("Choose directory");
		
		btGenerateTrainingSet = new JButton("Generate Training Set File");
		btLoadTrainingSet = new JButton("Load Training Set File");
		btTrainClassifier = new JButton("Train Classifier");
		String[] models = {"Naive Bayes", "Model 2", "Model n"};
		modelType = new JComboBox<String>(models);
		
		
		//	Attach to model
		
		this.model = (Model) model;
		model.attach(this);
		
		addComponent(lbImagesDirectory, 0, 0, 1, 1, false);
		addComponent(tfImagesField, 1, 0, 1, 1, false);
		addComponent(btChooseImagesDir, 1, 1, 1, 1, false);
		
		addComponent(lbDataDirectory, 2, 0, 1, 1, false);
		addComponent(tfDataField, 3, 0, 1, 1, false);
		addComponent(btChooseDataDir, 3, 1, 1, 1, false);
		
		addComponent(lbClassifierDirectory, 4, 0, 1, 1, false);
		addComponent(tfClassifierField, 5, 0, 1, 1, false);
		addComponent(btChooseClassifierDir, 5, 1, 1, 1, false);
		
		addComponent(btGenerateTrainingSet, 0, 4, 2, 1, false);
		addComponent(btLoadTrainingSet, 1, 4, 2, 1, false);
		addComponent(modelType, 2, 4, 1, 1, false);
		addComponent(btTrainClassifier, 3, 4, 1, 1, false);
		
		
		//	Start controller
		
		new MachineLearningController().init(model, this);
		
		
		//	End initialization
		
		pack();
		setResizable(false);
		
	}


	public JButton getBtGenerateTrainingSet() {
		return btGenerateTrainingSet;
	}

	public void setBtGenerateTrainingSet(JButton btGenerateTrainingSet) {
		this.btGenerateTrainingSet = btGenerateTrainingSet;
	}

	public JButton getBtLoadTrainingSet() {
		return btLoadTrainingSet;
	}

	public void setBtLoadTrainingSet(JButton btLoadTrainingSet) {
		this.btLoadTrainingSet = btLoadTrainingSet;
	}

	public JButton getBtTrainClassifier() {
		return btTrainClassifier;
	}

	public void setBtTrainClassifier(JButton btTrainClassifier) {
		this.btTrainClassifier = btTrainClassifier;
	}

	public JButton getBtChooseImagesDir() {
		return btChooseImagesDir;
	}

	public void setBtChooseImagesDir(JButton btChooseImagesDir) {
		this.btChooseImagesDir = btChooseImagesDir;
	}

	public JButton getBtChooseDataDir() {
		return btChooseDataDir;
	}

	public void setBtChooseDataDir(JButton btChooseDataDir) {
		this.btChooseDataDir = btChooseDataDir;
	}

	public JButton getBtChooseClassifierDir() {
		return btChooseClassifierDir;
	}
	
	public void setBtChooseClassifierDir(JButton btChooseClassifierDir) {
		this.btChooseClassifierDir = btChooseClassifierDir;
	}

	public JTextField getTfImagesField() {
		return tfImagesField;
	}

	public void setTfImagesFieldText(String tfImagesField) {
		this.tfImagesField.setText(tfImagesField);
	}

	public JTextField getTfDataField() {
		return tfDataField;
	}

	public void setTfDataFieldText(String tfDataField) {
		this.tfDataField.setText(tfDataField);
	}

	public JTextField getTfClassifierField() {
		return tfClassifierField;
	}

	public void setTfClassifierFieldText(String tfClassifierField) {
		this.tfClassifierField.setText(tfClassifierField);
	}

}
