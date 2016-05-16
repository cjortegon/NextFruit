package co.edu.icesi.nextfruit.modules.machinelearning;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import co.edu.icesi.nextfruit.modules.computervision.FeaturesExtract;
import co.edu.icesi.nextfruit.modules.model.CameraCalibration;
import co.edu.icesi.nextfruit.modules.model.MatchingColor;
import co.edu.icesi.nextfruit.modules.model.MatchingColorInterpreter;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

public abstract class WekaClassifierAdapter extends WekaClassifier {

	private Classifier classifier;
	private List<MatchingColor> matchingColors;

	public WekaClassifierAdapter(String name, CameraCalibration calibration) {
		super(calibration);
		this.trainingSet = new Instances(name, getFeatures(), 0);
	}

	public WekaClassifierAdapter(File classifier, CameraCalibration calibration) throws Exception {
		super(calibration);
		this.classifier = loadClassifierFromFile(classifier);
		this.trainingSet = new Instances("to-classify", getFeatures(), 0);
	}

	// Abstract methods
	public abstract void insertInstanceFromFeatures(FeaturesExtract extracted, String className);
	public abstract Instance getInstanceFromFeatures(FeaturesExtract extracted);

	public double[] classify(Instance instance) throws Exception {
		Instances instances = new Instances("to-classify", getFeatures(), 0);
		instances.add(instance);
		instances.setClassIndex(instance.numAttributes() - 1);
		if(classifier != null) {
			return classifier.distributionForInstance(instances.firstInstance());
		}
		return null;
	}

	public void loadMatchingColors(File file, double[][] inverseMatrixM) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		LinkedList<String> lines = new LinkedList<String>();
		while((line = reader.readLine()) != null) {
			if(line.length() >= 5)
				lines.add(line);
		}
		int i = 0;
		String stringRepresentation[] = new String[lines.size()];
		Iterator<String> it = lines.iterator();
		while(it.hasNext()) {
			stringRepresentation[i] = it.next();
			i ++;
		}
		if(lines.size() > 0)
			loadMatchingColors(stringRepresentation, inverseMatrixM);
	}

	public void loadMatchingColors(String stringRepresentation[], double[][] inverseMatrixM) {
		matchingColors = new ArrayList<>();
		for (String line : stringRepresentation)
			matchingColors.add(MatchingColorInterpreter.identifyMatchingColor(line, inverseMatrixM, 0.5));
	}

	protected List<MatchingColor> getMatchingColors() {
		return matchingColors;
	}

	public Classifier getClassifier() {
		return classifier;
	}

}
