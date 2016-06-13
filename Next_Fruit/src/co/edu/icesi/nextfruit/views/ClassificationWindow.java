package co.edu.icesi.nextfruit.views;

import java.awt.Dimension;
import java.awt.Image;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.opencv.core.Mat;

import co.edu.icesi.nextfruit.controller.ClassificationController;
import co.edu.icesi.nextfruit.modules.Model;
import co.edu.icesi.nextfruit.mvc.interfaces.Attachable;
import co.edu.icesi.nextfruit.mvc.interfaces.Initializable;
import co.edu.icesi.nextfruit.mvc.interfaces.Updateable;
import co.edu.icesi.nextfruit.util.ImageUtility;
import co.edu.icesi.nextfruit.views.subviews.ImageCanvas;
import visualkey.KFrame;

/**
 * 
 * @author JuanD
 *
 */
public class ClassificationWindow extends KFrame implements Initializable, Updateable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Dimension CANVAS_SIZE_VERTICAL = new Dimension(300, 300);
	private static final Dimension CANVAS_SIZE_VERTICAL_LARGE = new Dimension(300, 350);

	private Model model;
	private Mat mat;


	// Visual elements
	private ImageCanvas imageCanvas;
	private JButton btLoadImage, btExtractFeatures, btClassify;
	private JButton btQualityClassifier, btSizeClassifier, btRipenessClassifier, btClassClassifier;
	private JTextArea results;

	@Override
	public void update() {
		if(isVisible()) {
			// Repainting components
			try {
				if(model.getFeaturesExtract() != null) {
					if(mat != model.getFeaturesExtract().getMat()) {
						mat = model.getFeaturesExtract().getMat();
						Image image = ImageUtility.mat2Image(mat);
						imageCanvas.setLoadedImage(image);
					}
				}
			} catch(NullPointerException npe){}
			repaint();
		}
	}

	@Override
	public void init(Attachable model, Updateable view) {

		// Attach to model
		this.model = (Model) model;
		this.model.attach(this);

		// Initializing objects
		imageCanvas = new ImageCanvas(CANVAS_SIZE_VERTICAL, this.model);
		btLoadImage = new JButton("Load image");
		btExtractFeatures = new JButton("Extract features");
		btExtractFeatures.setEnabled(false);
		btClassify = new JButton("Classify Image");
		btClassify.setEnabled(false);
		btQualityClassifier = new JButton("Quality Classifier");
		btSizeClassifier = new JButton("Size Classifier");
		btClassClassifier = new JButton("Class Classifier");
		btRipenessClassifier = new JButton("Ripeness Classifier");

		results = new JTextArea();
		JScrollPane resultsScroll = new JScrollPane(results);
		resultsScroll.setPreferredSize(CANVAS_SIZE_VERTICAL_LARGE);

		// Adding components

		addComponent(btLoadImage, 0, 0, 2, 1, false);
		addComponent(imageCanvas, 1, 0, 2, 3, false);
		addComponent(btQualityClassifier, 4, 0, 1, 1, false);
		addComponent(btClassClassifier, 4, 1, 1, 1, false);
		addComponent(btSizeClassifier, 5, 0, 1, 1, false);
		addComponent(btRipenessClassifier, 5, 1, 1, 1, false);

		addComponent(btExtractFeatures, 0, 2, 1, 1, false);
		addComponent(btClassify, 0, 3, 1, 1, false);
		addComponent(resultsScroll, 1, 2, 2, 5, false);

		//	Start controller
		new ClassificationController().init(model, this);

		//	End initialization
		pack();
		setResizable(false);
	}

	// ****************** GETTERS ******************

	public JButton getBtLoadImage() {
		return btLoadImage;
	}

	public JButton getBtClassify() {
		return btClassify;
	}

	public JButton getBtQualityClassifier() {
		return btQualityClassifier;
	}

	public JButton getBtSizeClassifier() {
		return btSizeClassifier;
	}

	public JButton getBtRipenessClassifier() {
		return btRipenessClassifier;
	}

	public JButton getBtClassClassifier() {
		return btClassClassifier;
	}

	public void setResults(String results) {
		this.results.setText(results);
	}

	public JButton getBtExtractFeatures() {
		return btExtractFeatures;
	}

	// ****************** GETTERS ******************

}
