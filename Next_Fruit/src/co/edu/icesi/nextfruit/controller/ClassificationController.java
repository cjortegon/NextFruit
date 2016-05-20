package co.edu.icesi.nextfruit.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DecimalFormat;

import co.edu.icesi.nextfruit.modules.Model;
import co.edu.icesi.nextfruit.modules.ModelBuilder;
import co.edu.icesi.nextfruit.mvc.interfaces.Attachable;
import co.edu.icesi.nextfruit.mvc.interfaces.Initializable;
import co.edu.icesi.nextfruit.mvc.interfaces.Updateable;
import co.edu.icesi.nextfruit.util.FilesUtility;
import co.edu.icesi.nextfruit.views.ClassificationWindow;

/**
 * 
 * @author JuanD
 *
 */
public class ClassificationController implements Initializable, ActionListener{

	private static final String CHOOSE_IMAGE = "ChooseImagePath";
	private static final String EXTRACT_FEATURES = "ExtractFeatures";
	private static final String CLASSIFIY_IMAGE = "ClassifyImage";

	private Model model;
	private ClassificationWindow view;
	private DecimalFormat numberFormat;

	public ClassificationController() {
		numberFormat = new DecimalFormat("0.00");
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		System.out.println("actionPerformed: "+e.getActionCommand());

		File file = null;

		switch (e.getActionCommand()) {
		case CHOOSE_IMAGE:
			file = FilesUtility.loadFile(view, "Load your fruit image");
			if(file != null) {
				model.startFeaturesExtract(file.getAbsolutePath());
				view.getBtExtractFeatures().setEnabled(true);
				if(model.canClassify()) {
					view.getBtClassify().setEnabled(true);
				}
			}
			break;

		case ModelBuilder.QUALITY_CLASSIFIER:
		case ModelBuilder.CLASS_CLASSIFIER:
		case ModelBuilder.SIZE_CLASSIFIER:
		case ModelBuilder.RIPENESS_CLASSIFIER:

			file = FilesUtility.loadFile(view, "Load "+e.getActionCommand()+" classifier to use");

			if(file != null) {
				try {
					model.loadClassifier(e.getActionCommand(), file);
					if(model.canClassify()) {
						view.getBtClassify().setEnabled(true);
					}
					view.showMessage(e.getActionCommand()+" classifier loaded successfully!");
				} catch (Exception e1) {
					view.showMessage("Error loading classifier: "+file.getName());
					e1.printStackTrace();
				}
			} else {
				view.showMessage("Please select a classifier.");
			}

			break;

		case EXTRACT_FEATURES:
			if(!model.extractFeatures())
				view.showMessage("Load image before processing image.");
			break;

		case CLASSIFIY_IMAGE:

			try {
				double[][] result = model.classifyImage();
				if(result != null){
					String message = "Quality :="+
							"\n\tT:\t"+percentFormat(result[0][0])+
							"\n\tF:\t"+percentFormat(result[0][1])+
							//							"\n\nClass :=\t(not implemented)"+
							"\n\nSize :="+
							"\n\tBig:\t"+percentFormat(result[2][0])+
							"\n\tMedium:\t"+percentFormat(result[2][1])+
							"\n\tSmall:\t"+percentFormat(result[2][2])+
							"\n\nRipeness :="+
							"\n\t0:\t"+percentFormat(result[3][0])+
							"\n\t1:\t"+percentFormat(result[3][1])+
							"\n\t2:\t"+percentFormat(result[3][2])+
							"\n\t3:\t"+percentFormat(result[3][3])+
							"\n\t4:\t"+percentFormat(result[3][4])+
							"\n\t5:\t"+percentFormat(result[3][5])+
							"\n\t6:\t"+percentFormat(result[3][6]);
					System.out.println(message);
					view.setResults(message);
					view.showMessage("Image has been classified");
				} else {
					view.showMessage("The image could not be classified");
				}
			} catch (Exception e1) {
				view.showMessage("Error classifying the image");
				e1.printStackTrace();
			}

			break;
		}
	}

	public String percentFormat(double number) {
		number *= 100;
		return numberFormat.format(number)+"%";
	}

	@Override
	public void init(Attachable model, Updateable view) {
		this.model = (Model) model;
		this.view = (ClassificationWindow) view;
		AddListeners();
	}

	/**
	 * 
	 */
	private void AddListeners(){

		view.getBtLoadImage().setActionCommand(CHOOSE_IMAGE);
		view.getBtLoadImage().addActionListener(this);

		view.getBtExtractFeatures().setActionCommand(EXTRACT_FEATURES);
		view.getBtExtractFeatures().addActionListener(this);

		view.getBtClassify().setActionCommand(CLASSIFIY_IMAGE);
		view.getBtClassify().addActionListener(this);

		view.getBtQualityClassifier().setActionCommand(ModelBuilder.QUALITY_CLASSIFIER);
		view.getBtClassClassifier().setActionCommand(ModelBuilder.CLASS_CLASSIFIER);
		view.getBtSizeClassifier().setActionCommand(ModelBuilder.SIZE_CLASSIFIER);
		view.getBtRipenessClassifier().setActionCommand(ModelBuilder.RIPENESS_CLASSIFIER);

		view.getBtQualityClassifier().addActionListener(this);
		view.getBtClassClassifier().addActionListener(this);
		view.getBtSizeClassifier().addActionListener(this);
		view.getBtRipenessClassifier().addActionListener(this);
	}

}
