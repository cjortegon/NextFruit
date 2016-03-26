package co.edu.icesi.nextfruit.modules;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import co.edu.icesi.nextfruit.modules.computervision.FeaturesExtract;
import co.edu.icesi.nextfruit.modules.machinelearning.WekaClassifier;
import co.edu.icesi.nextfruit.modules.model.CameraCalibration;
import co.edu.icesi.nextfruit.modules.model.ColorDistribution;
import co.edu.icesi.nextfruit.modules.model.MatchingColor;
import co.edu.icesi.nextfruit.modules.model.PolygonWrapper;
import co.edu.icesi.nextfruit.util.Statistics;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class ModelBuilder {

	private WekaClassifier classifier;
	private Instances instances;
	private String trainingSet;
	private List<MatchingColor> matchingColors;
	private boolean meanActive, standardDeviationActive, skeewnessActive, kurtosisActive, areaActive, perimeterActive;

	/**
	 * This class gets all the features from the images loaded
	 */
	public ModelBuilder() {
		this.classifier = new WekaClassifier();
	}

	public void obtainTrainingSet(File folder, String destinationFile, CameraCalibration cameraCalibration) {
		obtainTrainingSet(folder, "-", destinationFile, cameraCalibration);
	}

	public void obtainTrainingSet(File folder, String classSeparator, String destinationFile, CameraCalibration calibration) {

		// Creating temporal matching colors
		matchingColors = new ArrayList<>();
		matchingColors.add(new MatchingColor(new double[]{0.5, 0.5, 0.75}, 0.05, calibration.getInverseWorkingSpaceMatrix()));

		// Reading files and validating if is in JPEG or PNG format
		File[] files = folder.listFiles();
		LinkedList<File> images = new LinkedList<>();
		for (File file : files) {
			if(file.isFile() && !file.isHidden() &&
					(file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg") || file.getName().endsWith(".png"))) {
				images.add(file);
			}
		}

		// Starting instances container
		int numberOfImages = images.size();
		this.instances = new Instances("strawberry-qualities", classifier.getFeatures(), numberOfImages);

		// Extracting features from images and creating new instances from that
		System.out.println("Extracting features...");
		int count = 0;
		ArrayList<Attribute> list = classifier.getFeatures();
		for (File file : images) {

			// Getting class name
			String fileName = file.getName();
			String className = fileName.substring(0, fileName.indexOf(classSeparator));

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
			double colors[] = new double[2];
			for (ColorDistribution color : matchs)
				colors[index++] = color.getRepeat()/(double)features.getNumberOfPixels();

			// Creating instances
			Instance instance = new DenseInstance(9);
			instance.setValue(list.get(0), luminantStatistics.getMean());
			instance.setValue(list.get(1), luminantStatistics.getStandardDeviation());
			instance.setValue(list.get(2), luminantStatistics.getSkewness());
			instance.setValue(list.get(3), luminantStatistics.getKurtosis());
			instance.setValue(list.get(4), colors[0]);
			instance.setValue(list.get(5), colors[1]);
			instance.setValue(list.get(6), polygon.getArea());
			instance.setValue(list.get(7), className);
			instances.add(instance);

			percent = (int) (((count*3+3)/((double)images.size()*3))*100);
			System.out.println("Extracting features... "+percent+"%");
			count ++;
		}

		this.classifier.saveDataSetIntoFile(instances, trainingSet);
	}

	public void loadTrainingSet(String fileName) {
		this.trainingSet = fileName;
		this.classifier.LoadDataSetFromFile(fileName);
	}

	public void trainClassifier(String classifierDestination) {
		this.classifier.trainClassifier(new NaiveBayes(), trainingSet, classifierDestination);
	}

}
