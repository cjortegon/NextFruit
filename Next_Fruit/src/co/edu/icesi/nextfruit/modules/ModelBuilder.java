package co.edu.icesi.nextfruit.modules;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import co.edu.icesi.nextfruit.modules.computervision.FeaturesExtract;
import co.edu.icesi.nextfruit.modules.machinelearning.QualityClassifier;
import co.edu.icesi.nextfruit.modules.machinelearning.WekaClassifier;
import co.edu.icesi.nextfruit.modules.machinelearning.WekaClassifierAdapter;
import co.edu.icesi.nextfruit.modules.model.CameraCalibration;
import co.edu.icesi.nextfruit.modules.model.ColorDistribution;
import co.edu.icesi.nextfruit.modules.model.MatchingColor;
import co.edu.icesi.nextfruit.modules.model.PolygonWrapper;
import co.edu.icesi.nextfruit.util.Statistics;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * This class gets all the features from the images loaded
 */
public class ModelBuilder {

	public static final String NAIVE_BAYES = "Naive Bayes";
	public static final String SECOND_TYPE = "Second type";
	public static final String THIRD_TYPE = "Third type";
	public static final String[] MODEL_TYPES = new String[]{NAIVE_BAYES, SECOND_TYPE, THIRD_TYPE};

	public static final String QUALITY_CLASSIFIER = "QUALITY";
	public static final String SIZE_CLASSIFIER = "SIZE";
	public static final String CLASS_CLASSIFIER = "CLASS";
	public static final String RIPENESS_CLASSIFIER = "RIPENESS";
	
	private ArrayList<File> images;

	private WekaClassifierAdapter classifiers[];
	private boolean hasLoadedImages, hasLoadedTrainingSet;

	/**
	 * Starts a new classifier class
	 */
	public ModelBuilder() {
	}

	/**
	 * This method reads all the files from the given folder and validates if is a JPEG PNG image.
	 * @param folder Directory This is the location where the fruit images are located.
	 */
	public void loadImages(File folder) {
		File[] files = folder.listFiles();
		images = new ArrayList<>();
		for (File file : files) {
			if(file.isFile() && !file.isHidden()) {
				String name = file.getName().toLowerCase();
				if(name.toLowerCase().endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png"))
					images.add(file);
			}
		}
		hasLoadedImages = true;
		System.out.println("Total images to train system: "+images.size());
	}

	/**
	 * This class process the loaded images to create a training set placed in the WekaClassifier (classifier).
	 * @param classSeparator The special character or expression that separates the tag from the fruit class and the name of the specific instance.
	 * @param destinationFile The destination file where the training set will be saved.
	 * @param calibration The calibration settings.
	 */
	public void processTrainingSet(String classSeparator, File destinationFile, CameraCalibration calibration) {

		// Starting classifiers
		this.classifiers = new WekaClassifierAdapter[1];
		this.classifiers[0] = new QualityClassifier(calibration);

		// Extracting features from images and creating new instances from that
		for (File file : images) {

			// Getting class names
			String fileName = file.getName();
			fileName.substring(0, fileName.indexOf("."));
			String classNames[] = fileName.split(classSeparator);
			System.out.println("Processing file: "+file.getName());

			// Processing features
			FeaturesExtract extracted = new FeaturesExtract(file.getAbsolutePath());
			extracted.extractFeatures(calibration);

			// Extracting features for each classifier
			for (int i = 0; i < classifiers.length; i++) {
				System.out.println("Extracting features for instance type: "+classNames[i]);
				classifiers[i].insertInstanceFromFeatures(extracted, classNames[i]);
			}
		}

		// Saving data
		try {
			for (int i = 0; i < classifiers.length; i++) {
				File toSave = new File(destinationFile.getParentFile().getAbsolutePath()+"/ts"+i+"_"+destinationFile.getName());
				classifiers[i].saveTrainningSetIntoFile(toSave);
			}
		} catch (FileNotFoundException e) {
		}
		this.hasLoadedTrainingSet = true;
	}

	/**
	 * Loads a training set from the given file.
	 * @param file Location of the file that contains the training set.
	 */
	public boolean loadTrainingSet(File file) {
		//		try {
		//			this.classifier.loadDataSetFromFile(file);
		//			this.hasLoadedTrainingSet = true;
		//			return true;
		//		} catch (IOException e) {
		//			this.hasLoadedTrainingSet = false;
		return false;
		//		}
	}

	/**
	 * This method can only be used if either processTrainingSet or loadTrainingSet have been called.
	 * @param classifierDestination The destination file where the model will be saved.
	 * @param type The type of machine learning that will be created.
	 * @return If the operation was successfully finished.
	 */
	public boolean trainClassifier(File classifierDestination, String type) {
		if(hasLoadedTrainingSet) {
			Classifier classifierType = null;
			switch (type) {
			case NAIVE_BAYES:
				classifierType = new NaiveBayes();
				break;
			}
			if(classifierType != null) {
				try {
					trainAll(classifierType, classifierDestination);
				} catch (Exception e) {
					return false;
				}
				return true;
			}
		}
		return false;
	}

	private void trainAll(Classifier classifierType, File destinationFile) throws Exception {
		for (int i = 0; i < classifiers.length; i++) {
			File toSave = new File(destinationFile.getParentFile().getAbsolutePath()+"/model"+i+"_"+destinationFile.getName());
			classifiers[i].trainClassifier(classifierType, null, toSave);
		}
	}

	// ************************ ACCESS METHODS ************************

	public boolean hasLoadedImages() {
		return hasLoadedImages;
	}

	public ArrayList<File> getImages() {
		return images;
	}

	// ************************ ACCESS METHODS ************************

}
