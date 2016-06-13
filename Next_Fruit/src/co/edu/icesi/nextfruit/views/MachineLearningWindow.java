package co.edu.icesi.nextfruit.views;

import java.awt.Dimension;
import java.io.File;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import co.edu.icesi.nextfruit.controller.MachineLearningController;
import co.edu.icesi.nextfruit.modules.Model;
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
	private static final Dimension LOG_SIZE = new Dimension(400, 200);

	private Model model;
	private JTextField tfImagesField;
	private JButton btChooseImagesDir, btGenerateTrainingSet;
	private JLabel lbDirectory, lbNumberOfFiles, lbPercentDone;
	private JTextArea log;
	private DecimalFormat df = new DecimalFormat("0.00");

	@Override
	public void update() {
		if(isVisible()) {
			repaint();
		}
	}

	@Override
	public void init(Attachable model, Updateable view) {
		setTitle("Dataset generation");

		//	Initializing objects
		//		lbDirectory = new JLabel("Load images from directory >> ");
		lbDirectory = new JLabel("(No directory selected)");
		lbNumberOfFiles = new JLabel("Number of files: 0");
		lbPercentDone = new JLabel("Percent done: 0.00%");

		btChooseImagesDir = new JButton("Choose directory");
		btGenerateTrainingSet = new JButton("Generate Training Sets Files");
		log = new JTextArea();
		log.setEditable(false);
		JScrollPane logScroll = new JScrollPane(log);
		logScroll.setPreferredSize(LOG_SIZE);

		addComponent(btChooseImagesDir, 0, 0, 1, 1, false);
		addComponent(btGenerateTrainingSet, 0, 1, 1, 1, false);
		addComponent(lbDirectory, 1, 0, 2, 1, true);
		addComponent(lbNumberOfFiles, 2, 0, 2, 1, true);
		addComponent(lbPercentDone, 3, 0, 2, 1, true);
		addComponent(logScroll, 4, 0, 2, 1, true);

		// Attach to model
		this.model = (Model) model;
		this.model.attach(this);

		//	Starting controller
		new MachineLearningController().init(model, this);

		//	End initialization
		pack();
		setResizable(false);
	}

	public JButton getBtChooseImagesDir() {
		return btChooseImagesDir;
	}

	public JTextField getTfImagesField() {
		return tfImagesField;
	}

	public void setTfImagesFieldText(String tfImagesField) {
		this.tfImagesField.setText(tfImagesField);
	}

	public JLabel getLbDirectory() {
		return lbDirectory;
	}

	public void updateFolderLabels(File directory, int numFiles) {
		String name = directory.getAbsolutePath();
		this.lbDirectory.setText("Selected folder: " + name.substring(name.lastIndexOf("/")));
		this.lbNumberOfFiles.setText("Number of files: "+numFiles);
	}

	public void updatePercentDone(double percent) {
		this.lbPercentDone.setText("Percent done: "+df.format(percent*100)+"%");
	}

	public void clearLog() {
		this.log.setText("");
	}

	public void log(String message) {
		this.log.setText(log.getText()+"\n"+message);
	}

	public JButton getBtGenerateTrainingSet() {
		return btGenerateTrainingSet;
	}

	public void setBtGenerateTrainingSet(JButton btGenerateTrainingSet) {
		this.btGenerateTrainingSet = btGenerateTrainingSet;
	}

}
