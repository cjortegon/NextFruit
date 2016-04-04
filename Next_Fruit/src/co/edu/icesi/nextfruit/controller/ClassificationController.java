package co.edu.icesi.nextfruit.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import co.edu.icesi.nextfruit.modules.Model;
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
	private static final String CLASSIFIY_IMAGE = "ClassifyImage";
	private static final String CHOOSE_CLASSIFIER = "ChooseClassifierPath";
	
	private Model model;
	private ClassificationWindow view;
	private File imageToClassify;
	private File classifierToUse;
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		System.out.println("actionPerformed: "+e.getActionCommand());

		File file = null;
		
		switch (e.getActionCommand()) {
		case CHOOSE_IMAGE:
			file = FilesUtility.loadFile(view, "Load Image to Classify");
			
			if(file != null){
				imageToClassify = file;
				view.setLbLoadImageText(file.getAbsolutePath());
			}else{
				view.showMessage("Please select an image.");
			}
			
			if((imageToClassify != null) && (classifierToUse != null)){
				view.getBtClassify().setEnabled(true);
			}
			
			break;
			
		case CHOOSE_CLASSIFIER:
			file = FilesUtility.loadFile(view, "Load Classifier to use");
			
			if(file != null){
				classifierToUse = file;
				view.setLbLoadClassifierText(file.getAbsolutePath());
			}else{
				view.showMessage("Please select a classifier.");
			}
			
			if((imageToClassify != null) && (classifierToUse != null)){
				view.getBtClassify().setEnabled(true);
			}
			
			break;
			
		case CLASSIFIY_IMAGE:
			
			try{
				double[] result = null;
				
				if((imageToClassify != null) && (classifierToUse != null)){
					result =  model.classifyImage(imageToClassify, classifierToUse);
				}
				
				if(result != null){
					String msg = "<html><b>" +
							"5r -> " + result[0] + "<br>" +
							"5v -> " + result[1] + "<br>" +
							"er -> " + result[2] + "<br>" +
							"fea -> " + result[3] + "<br>" +
							"</b></html>";
					view.showMessage(msg);
				}else{
					view.showMessage("The image could not be classified.");
				}
				
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			break;

		}
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
		
		view.getBtClassify().setActionCommand(CLASSIFIY_IMAGE);
		view.getBtClassify().addActionListener(this);
		
		view.getBtLoadClassifier().setActionCommand(CHOOSE_CLASSIFIER);
		view.getBtLoadClassifier().addActionListener(this);
	}
	
}
