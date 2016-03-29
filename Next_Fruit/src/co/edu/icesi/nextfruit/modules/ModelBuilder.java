package co.edu.icesi.nextfruit.modules;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import co.edu.icesi.nextfruit.modules.computervision.FeaturesExtract;
import co.edu.icesi.nextfruit.modules.machinelearning.WekaClassifier;
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

	private WekaClassifier classifier;
	private Instances instances;
	private ArrayList<File> images;
	private List<MatchingColor> matchingColors;
	private boolean meanActive, standardDeviationActive, skeewnessActive, kurtosisActive, areaActive, perimeterActive;
	private boolean hasLoadedImages, hasLoadedTrainingSet;
	private double[][] definedColors = new double[][]{
		{0.30,0.49,0.11},
		{0.62,0.31,0.15},
		{0.37,0.35,0.03}
	};

	/**
	 * Starts a new classifier class
	 */
	public ModelBuilder() {
		this.classifier = new WekaClassifier();
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
	 * This class uses the process the loaded images to create a training set placed in the WekaClassifier (classifier).
	 * @param classSeparator The special character or expression that separates the tag from the fruit class and the name of the specific instance.
	 * @param destinationFile The destination file where the training set will be saved.
	 * @param calibration The calibration settings.
	 */
	public void processTrainingSet(String classSeparator, File destinationFile, CameraCalibration calibration) {

		// Creating temporal matching colors
		matchingColors = new ArrayList<>();
		for (double[] color : definedColors)
			matchingColors.add(new MatchingColor(new double[]{color[0], color[1], 0.75}, color[2], calibration.getInverseWorkingSpaceMatrix()));

		// Starting instances container
		int numberOfImages = images.size();
		this.instances = new Instances("strawberry-qualities", classifier.getFeatures(), numberOfImages);

		// Extracting features from images and creating new instances from that
		int count = 0;
		ArrayList<Attribute> list = classifier.getFeatures();
		for (File file : images) {

			// Getting class name
			String fileName = file.getName();
			String className = fileName.substring(0, fileName.indexOf(classSeparator));
			System.out.println("Extracting features... ("+className+")");

			// Processing features
			FeaturesExtract features = new FeaturesExtract(file.getAbsolutePath());
			features.extractFeatures(calibration);
			int percent = (int) (((count*3+1)/((double)images.size()*3))*100);
			System.out.println("Extracting features... "+percent+"%");
			features.analizeData(calibration, matchingColors);
			percent = (int) (((count*3+2)/((double)images.size()*3))*100);
			System.out.println("Extracting features... "+percent+"%");

			// Getting results
			Collection<ColorDistribution> matchs = features.getMatchingColors();
			Statistics luminantStatistics = features.getLuminanceStatistics();
			PolygonWrapper polygon = features.getPolygon();
			int index = 0;
			double colors[] = new double[3];
			for (ColorDistribution color : matchs)
				colors[index++] = color.getRepeat()/(double)features.getNumberOfPixels();

			// Creating instances
			Instance instance = new DenseInstance(9);

			instance.setValue(list.get(0), polygon.getArea());
			instance.setValue(list.get(1), luminantStatistics.getMean());
			instance.setValue(list.get(2), luminantStatistics.getStandardDeviation());
			instance.setValue(list.get(3), luminantStatistics.getSkewness());
			instance.setValue(list.get(4), luminantStatistics.getKurtosis());
			instance.setValue(list.get(5), colors[0]);
			instance.setValue(list.get(6), colors[1]);
			instance.setValue(list.get(7), colors[2]);
			instance.setValue(list.get(8), className);
			instances.add(instance);

			percent = (int) (((count*3+3)/((double)images.size()*3))*100);
			System.out.println("Extracting features... "+percent+"%");
			count ++;
		}

		this.classifier.saveDataSetIntoFile(instances, destinationFile.getAbsolutePath());
		this.hasLoadedTrainingSet = true;
	}

	/**
	 * Loads a training set from the given file.
	 * @param fileName Location of the file that contains the training set.
	 */
	public void loadTrainingSet(File fileName) {
		try {
			this.classifier.loadDataSetFromFile(fileName.getAbsolutePath());
			this.hasLoadedTrainingSet = true;
		} catch (IOException e) {
			this.hasLoadedTrainingSet = false;
		}
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
			case "":
				classifierType = new NaiveBayes();
				break;
			}
			if(classifierType != null) {
				this.classifier.trainClassifier(classifierType, null, classifierDestination.getAbsolutePath());
				return true;
			}
		}
		return false;
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
