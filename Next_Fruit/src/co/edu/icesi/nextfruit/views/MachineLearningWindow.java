package co.edu.icesi.nextfruit.views;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import co.edu.icesi.nextfruit.controller.MachineLearningController;
import co.edu.icesi.nextfruit.modules.Model;
import co.edu.icesi.nextfruit.modules.ModelBuilder;
import co.edu.icesi.nextfruit.mvc.interfaces.Attachable;
import co.edu.icesi.nextfruit.mvc.interfaces.Initializable;
import co.edu.icesi.nextfruit.mvc.interfaces.Updateable;
import visualkey.KFrame;

/**
 * @author JuanD
 */
public class MachineLearningWindow extends KFrame implements Initializable, Updateable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Model model;
	private JTextField tfImagesField;
	private JButton btGenerateTrainingSet, btLoadQualityTrainingSet, btLoadSizeTrainingSet,
	btLoadClassTrainingSet, btLoadRipenessTrainingSet, btTrainQualityClassifier, 
	btTrainSizeClassifier, btTrainClassClassifier, btTrainRipenessClassifier,
	btChooseImagesDir, btTestClassifier, btLoadQualityClassifier, btLoadSizeClassifier,
	btLoadClassClassifier, btLoadRipenessClassifier, btTrainAllClassifiers,
	btLoadQualityTestSet, btLoadSizeTestSet, btLoadClassTestSet, btLoadRipenessTestSet;
	private JComboBox<String> modelTypeComboBox;
	private JLabel lbDirectory, lbPanelTrainingSetConfig, lbPanelClassifierConfig, lbClassifierDir,
	lbPanelClassifierEval, lbTrainingSetDir, lbTestSet, lbTrainingSetDir2, lbDataSetLoad;

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
		lbDirectory = new JLabel("Load images from directory >> ");
		lbClassifierDir = new JLabel("Classifier file >> ");
		lbTrainingSetDir = new JLabel("Training Set File >> ");
		lbTrainingSetDir2 = new JLabel("Training Set File >> ");
		lbTestSet = new JLabel("Test Set File >> ");
		
		lbPanelTrainingSetConfig = new JLabel("DATA SET GENERATION", SwingConstants.CENTER);
		lbPanelTrainingSetConfig.setPreferredSize(new Dimension(600, 100));
		lbPanelTrainingSetConfig.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		
		lbDataSetLoad = new JLabel("LOAD DATA SETS", SwingConstants.CENTER);
		lbDataSetLoad.setPreferredSize(new Dimension(600, 100));
		lbDataSetLoad.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		
		lbPanelClassifierConfig = new JLabel("CLASSIFIER CONFIGURATION", SwingConstants.CENTER);
		lbPanelClassifierConfig.setPreferredSize(new Dimension(600, 100));
		lbPanelClassifierConfig.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		
		lbPanelClassifierEval = new JLabel("CLASSIFIER EVALUATION", SwingConstants.CENTER);
		lbPanelClassifierEval.setPreferredSize(new Dimension(600, 100));
		lbPanelClassifierEval.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		
		JLabel a = new JLabel("-- Classifier Type --", SwingConstants.CENTER);
		a.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		
		btChooseImagesDir = new JButton("Choose directory");
		
		btGenerateTrainingSet = new JButton("Generate Training Sets Files");
		
		btLoadQualityTrainingSet = new JButton("Load Quality TrainingSet File");
		btLoadSizeTrainingSet = new JButton("Load Size TrainingSet File");
		btLoadClassTrainingSet = new JButton("Load Class TrainingSet File");
		btLoadRipenessTrainingSet = new JButton("Load Ripeness TrainingSet File");
		
		btLoadQualityTestSet = new JButton("Load Quality TestSet File");
		btLoadSizeTestSet = new JButton("Load Size TestSet File");
		btLoadClassTestSet = new JButton("Load Class TestSet File");
		btLoadRipenessTestSet = new JButton("Load Ripeness TestSet File");
		
		btTrainQualityClassifier = new JButton("Train Quality Classifier");
		btTrainSizeClassifier = new JButton("Train Size Classifier");
		btTrainClassClassifier = new JButton("Train Class Classifier");
		btTrainRipenessClassifier = new JButton("Train Ripeness Classifier");
		btTrainAllClassifiers = new JButton("Train All Classifiers");
		
		btLoadQualityClassifier = new JButton("Load Quality Classifier");
		btLoadSizeClassifier = new JButton("Load Size Classifier");
		btLoadClassClassifier = new JButton("Load Class Classifier");
		btLoadRipenessClassifier = new JButton("Load Ripeness Classifier");
		btTestClassifier = new JButton("Test Classifier");
		
		modelTypeComboBox = new JComboBox<String>(ModelBuilder.MODEL_TYPES);
		

		//	Attach to model
		this.model = (Model) model;
		this.model.attach(this);

		addComponent(lbPanelTrainingSetConfig, 0, 0, 2, 1, false);
		
		addComponent(lbDirectory, 1, 0, 4, 1, false);
		addComponent(btChooseImagesDir, 2, 0, 1, 1, false);
		addComponent(btGenerateTrainingSet, 2, 1, 1, 1, false);
		
		
		addComponent(lbDataSetLoad, 3, 0, 2, 1, false);
		
		addComponent(btLoadQualityTrainingSet, 4, 0, 1, 1, false);
		addComponent(btLoadSizeTrainingSet, 5, 0, 1, 1, false);
		addComponent(btLoadClassTrainingSet, 6, 0, 1, 1, false);
		addComponent(btLoadRipenessTrainingSet, 7, 0, 1, 1, false);

		addComponent(btLoadQualityTestSet, 4, 1, 1, 1, false);
		addComponent(btLoadSizeTestSet, 5, 1, 1, 1, false);
		addComponent(btLoadClassTestSet, 6, 1, 1, 1, false);
		addComponent(btLoadRipenessTestSet, 7, 1, 1, 1, false);
		
		
		addComponent(lbPanelClassifierConfig, 8, 0, 2, 1, false);
		
		addComponent(lbTrainingSetDir, 9, 0, 4, 1, false);
		addComponent(a, 10, 0, 1, 1, false);
		addComponent(modelTypeComboBox, 10, 1, 1, 1, false);
		
		addComponent(btTrainQualityClassifier, 11, 0, 2, 1, false);
		addComponent(btTrainSizeClassifier, 12, 0, 2, 1, false);
		addComponent(btTrainClassClassifier, 13, 0, 2, 1, false);
		addComponent(btTrainRipenessClassifier, 14, 0, 2, 1, false);
		addComponent(btTrainAllClassifiers, 15, 0, 2, 1, false);
		
		
		addComponent(lbPanelClassifierEval, 16, 0, 2, 1, false);
		
		addComponent(lbTrainingSetDir2, 17, 0, 4, 1, false);
		addComponent(lbClassifierDir, 18, 0, 4, 1, false);
		addComponent(lbTestSet, 19, 0, 4, 1, false);		
		
		addComponent(btLoadQualityClassifier, 20, 0, 1, 1, false);
		addComponent(btLoadSizeClassifier, 20, 1, 1, 1, false);
		addComponent(btLoadClassClassifier, 21, 0, 1, 1, false);
		addComponent(btLoadRipenessClassifier, 21, 1, 1, 1, false);
		
		addComponent(btTestClassifier, 22, 0, 2, 1, false);

		//	Starting controller
		new MachineLearningController().init(this.model, this);

		//	End initialization
		this.setPreferredSize(new Dimension(650, 550));
		pack();
		setResizable(false);
	}


	public JButton getBtLoadTrainingSet() {
		return btLoadQualityTrainingSet;
	}

	public JButton getBtTrainClassifier() {
		return btTrainQualityClassifier;
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

	public JLabel getLbDirectory() {
		return lbDirectory;
	}

	public void setLbDirectoryText(String lbDirectory) {
		this.lbDirectory.setText("Load images from directory >> " + lbDirectory);
	}

	public JLabel getLbClassifierDir() {
		return lbClassifierDir;
	}

	public void setLbClassifierDirText(String lbClassifierDir) {
		this.lbClassifierDir.setText("Classifier file >> " + lbClassifierDir);
	}

	public JButton getBtTestClassifier() {
		return btTestClassifier;
	}

	public void setBtTestClassifier(JButton btTestClassifier) {
		this.btTestClassifier = btTestClassifier;
	}

	public JButton getBtLoadClassifier() {
		return btLoadQualityClassifier;
	}

	public void setBtLoadClassifier(JButton btLoadClassifier) {
		this.btLoadQualityClassifier = btLoadClassifier;
	}

	public JButton getBtGenerateTrainingSet() {
		return btGenerateTrainingSet;
	}

	public void setBtGenerateTrainingSet(JButton btGenerateTrainingSet) {
		this.btGenerateTrainingSet = btGenerateTrainingSet;
	}

	public JButton getBtTrainQualityClassifier() {
		return btTrainQualityClassifier;
	}

	public void setBtTrainQualityClassifier(JButton btTrainQualityClassifier) {
		this.btTrainQualityClassifier = btTrainQualityClassifier;
	}

	public JButton getBtTrainSizeClassifier() {
		return btTrainSizeClassifier;
	}

	public void setBtTrainSizeClassifier(JButton btTrainSizeClassifier) {
		this.btTrainSizeClassifier = btTrainSizeClassifier;
	}

	public JButton getBtTrainClassClassifier() {
		return btTrainClassClassifier;
	}

	public void setBtTrainClassClassifier(JButton btTrainClassClassifier) {
		this.btTrainClassClassifier = btTrainClassClassifier;
	}

	public JButton getBtTrainRipenessClassifier() {
		return btTrainRipenessClassifier;
	}

	public void setBtTrainRipenessClassifier(JButton btTrainRipenessClassifier) {
		this.btTrainRipenessClassifier = btTrainRipenessClassifier;
	}

	public JButton getBtTrainAllClassifiers() {
		return btTrainAllClassifiers;
	}

	public void setBtTrainAllClassifiers(JButton btTrainAllClassifiers) {
		this.btTrainAllClassifiers = btTrainAllClassifiers;
	}

	public JButton getBtLoadQualityTrainingSet() {
		return btLoadQualityTrainingSet;
	}

	public void setBtLoadQualityTrainingSet(JButton btLoadQualityTrainingSet) {
		this.btLoadQualityTrainingSet = btLoadQualityTrainingSet;
	}

	public JButton getBtLoadSizeTrainingSet() {
		return btLoadSizeTrainingSet;
	}

	public void setBtLoadSizeTrainingSet(JButton btLoadSizeTrainingSet) {
		this.btLoadSizeTrainingSet = btLoadSizeTrainingSet;
	}

	public JButton getBtLoadClassTrainingSet() {
		return btLoadClassTrainingSet;
	}

	public void setBtLoadClassTrainingSet(JButton btLoadClassTrainingSet) {
		this.btLoadClassTrainingSet = btLoadClassTrainingSet;
	}

	public JButton getBtLoadRipenessTrainingSet() {
		return btLoadRipenessTrainingSet;
	}

	public void setBtLoadRipenessTrainingSet(JButton btLoadRipenessTrainingSet) {
		this.btLoadRipenessTrainingSet = btLoadRipenessTrainingSet;
	}

	public JButton getBtLoadQualityTestSet() {
		return btLoadQualityTestSet;
	}

	public void setBtLoadQualityTestSet(JButton btLoadQualityTestSet) {
		this.btLoadQualityTestSet = btLoadQualityTestSet;
	}

	public JButton getBtLoadSizeTestSet() {
		return btLoadSizeTestSet;
	}

	public void setBtLoadSizeTestSet(JButton btLoadSizeTestSet) {
		this.btLoadSizeTestSet = btLoadSizeTestSet;
	}

	public JButton getBtLoadClassTestSet() {
		return btLoadClassTestSet;
	}

	public void setBtLoadClassTestSet(JButton btLoadClassTestSet) {
		this.btLoadClassTestSet = btLoadClassTestSet;
	}

	public JButton getBtLoadRipenessTestSet() {
		return btLoadRipenessTestSet;
	}

	public void setBtLoadRipenessTestSet(JButton btLoadRipenessTestSet) {
		this.btLoadRipenessTestSet = btLoadRipenessTestSet;
	}

	public JButton getBtLoadQualityClassifier() {
		return btLoadQualityClassifier;
	}

	public void setBtLoadQualityClassifier(JButton btLoadQualityClassifier) {
		this.btLoadQualityClassifier = btLoadQualityClassifier;
	}

	public JButton getBtLoadSizeClassifier() {
		return btLoadSizeClassifier;
	}

	public void setBtLoadSizeClassifier(JButton btLoadSizeClassifier) {
		this.btLoadSizeClassifier = btLoadSizeClassifier;
	}

	public JButton getBtLoadClassClassifier() {
		return btLoadClassClassifier;
	}

	public void setBtLoadClassClassifier(JButton btLoadClassClassifier) {
		this.btLoadClassClassifier = btLoadClassClassifier;
	}

	public JButton getBtLoadRipenessClassifier() {
		return btLoadRipenessClassifier;
	}

	public void setBtLoadRipenessClassifier(JButton btLoadRipenessClassifier) {
		this.btLoadRipenessClassifier = btLoadRipenessClassifier;
	}

	public JLabel getLbTrainingSetDir() {
		return lbTrainingSetDir;
	}

	public void setLbTrainingSetDirText(String lbTrainingSetDir) {
		this.lbTrainingSetDir.setText("Training Set File >> " + lbTrainingSetDir);
		this.lbTrainingSetDir2.setText("Training Set File >> " + lbTrainingSetDir);
	}

	public void setLbTestSetText(String lbTestSet) {
		this.lbTestSet.setText("Test Set File >> " + lbTestSet);
	}

}
