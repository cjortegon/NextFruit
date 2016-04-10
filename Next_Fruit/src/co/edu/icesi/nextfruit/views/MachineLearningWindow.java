package co.edu.icesi.nextfruit.views;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import co.edu.icesi.nextfruit.controller.MachineLearningController;
import co.edu.icesi.nextfruit.modules.Model;
import co.edu.icesi.nextfruit.modules.ModelBuilder;
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

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Model model;
	private JTextField tfImagesField;
	private JButton btGenerateTrainingSet, btLoadTrainingSet, btTrainClassifier, btChooseImagesDir;
	private JComboBox<String> modelTypeComboBox;

	@Override
	public void update() {
		if(isVisible()) {
			// Repainting components
			repaint();
		}		
	}

	@Override
	public void init(Attachable model, Updateable view) {

		//	Initializing objects

		tfImagesField = new JTextField();
		tfImagesField.setPreferredSize(new Dimension(200, 20));
		tfImagesField.setEnabled(false);
		btChooseImagesDir = new JButton("Choose directory");

		btGenerateTrainingSet = new JButton("Generate Training Set File");
		btLoadTrainingSet = new JButton("Load Training Set File");
		btTrainClassifier = new JButton("Train Classifier");
		modelTypeComboBox = new JComboBox<String>(ModelBuilder.MODEL_TYPES);

		//	Attach to model
		this.model = (Model) model;
		this.model.attach(this);

		addLabel("Load images from directory:", 0, 0, 1, 1, false);
		addComponent(tfImagesField, 1, 0, 1, 1, false);
		addComponent(btChooseImagesDir, 1, 1, 1, 1, false);

		addComponent(btGenerateTrainingSet, 2, 0, 1, 1, false);
		addComponent(btLoadTrainingSet, 2, 1, 1, 1, false);
		addComponent(modelTypeComboBox, 3, 0, 1, 1, false);
		addComponent(btTrainClassifier, 3, 1, 1, 1, false);

		//	Starting controller
		new MachineLearningController().init(model, this);

		//	End initialization
		pack();
		setResizable(false);
	}

	public JButton getBtGenerateTrainingSet() {
		return btGenerateTrainingSet;
	}

	public JButton getBtLoadTrainingSet() {
		return btLoadTrainingSet;
	}

	public JButton getBtTrainClassifier() {
		return btTrainClassifier;
	}

	public JButton getBtChooseImagesDir() {
		return btChooseImagesDir;
	}

	public JTextField getTfImagesField() {
		return tfImagesField;
	}

	public JComboBox<String> getModelTypeComboBox() {
		return modelTypeComboBox;
	}

	public void setTfImagesFieldText(String tfImagesField) {
		this.tfImagesField.setText(tfImagesField);
	}

}
