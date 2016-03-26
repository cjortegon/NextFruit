package co.edu.icesi.nextfruit.modules;

import java.util.List;

import co.edu.icesi.nextfruit.modules.machinelearning.WekaClassifier;
import co.edu.icesi.nextfruit.modules.model.MatchingColor;
import weka.classifiers.bayes.NaiveBayes;
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
	}

	public void obtainTrainningSet(String folder, String destinationFile) {
		obtainTrainningSet(folder, "-", destinationFile);
	}

	public void obtainTrainningSet(String folder, String classSeparator, String destinationFile) {

		// Leyendo archivos


		int numberOfImages = 10;
		this.classifier = new WekaClassifier();
		this.instances = new Instances("strawberry-qualities", classifier.getFeatures(), numberOfImages);

		// Leyendo caracteristicas
		//		for (int i = 0; i < array.length; i++) {
		//			this.instances.add(// aqui van las caracteristica)
		//		}

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
