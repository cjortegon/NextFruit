package co.edu.icesi.nextfruit.views;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JLabel;

import co.edu.icesi.nextfruit.controller.ClassificationController;
import co.edu.icesi.nextfruit.modules.Model;
import co.edu.icesi.nextfruit.mvc.interfaces.Attachable;
import co.edu.icesi.nextfruit.mvc.interfaces.Initializable;
import co.edu.icesi.nextfruit.mvc.interfaces.Updateable;
import visualkey.KFrame;

/**
 * 
 * @author JuanD
 *
 */
public class ClassificationWindow extends KFrame implements Initializable, Updateable {

	private Model model;
	
	private JLabel lbLoadImage, lbLoadClassifier;
	private JButton btLoadImage, btClassify, btLoadClassifier; 
	
	
	@Override
	public void update() {
		
		if(isVisible()){
			repaint();
		}
	}

	@Override
	public void init(Attachable model, Updateable view) {
		
		//	Initialize objects
		lbLoadImage = new JLabel("Image: ");
		lbLoadClassifier = new JLabel("Classifier: ");
		btLoadImage = new JButton("Load Image");
		btLoadClassifier = new JButton("Load Classifier");
		btClassify = new JButton("Classify Image");
		btClassify.setEnabled(false);
	

		//	Attach to model
		this.model = (Model) model;
		model.attach(this);
		
		addComponent(lbLoadClassifier, 0, 0, 1, 1, false);
		addComponent(btLoadClassifier, 1, 0, 1, 1, false);
		addComponent(lbLoadImage, 2, 0, 1, 1, false);
		addComponent(btLoadImage, 3, 0, 1, 1, false);
		addComponent(btClassify, 4, 0, 1, 1, true);
		

		//	Start controller
		new ClassificationController().init(model, this);

		
		//	End initialization
		this.setPreferredSize(new Dimension(600, 200));
		pack();
		setResizable(false);
		
	}

	public JButton getBtLoadClassifier() {
		return btLoadClassifier;
	}

	public void setLbLoadClassifierText(String lbLoadClassifier) {
		this.lbLoadClassifier.setText("Classifier: " + lbLoadClassifier);
	}
	
	public JButton getBtLoadImage() {
		return btLoadImage;
	}

	public JButton getBtClassify() {
		return btClassify;
	}

	public void setLbLoadImageText(String lbLoadImage) {
		this.lbLoadImage.setText("Image: " + lbLoadImage);
	}

}
