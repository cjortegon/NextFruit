package co.edu.icesi.nextfruit.modules.machinelearning;

import java.util.ArrayList;
import java.util.List;

import co.edu.icesi.nextfruit.modules.computervision.FeaturesExtract;
import co.edu.icesi.nextfruit.modules.model.MatchingColor;
import co.edu.icesi.nextfruit.modules.model.MatchingColorInterpreter;
import weka.core.Instances;

public abstract class WekaClassifierAdapter extends WekaClassifier {

	private List<MatchingColor> matchingColors;
	
	public WekaClassifierAdapter(String name) {
		super();
		this.trainingSet = new Instances(name, getFeatures(), 0);
	}

	public abstract void insertInstanceFromFeatures(FeaturesExtract extracted, String className);

	public void loadMatchingColors(String stringRepresentation[], double[][] inverseMatrixM) {
		matchingColors = new ArrayList<>();
		for (String line : stringRepresentation)
			matchingColors.add(MatchingColorInterpreter.identifyMatchingColor(line, inverseMatrixM, 0.5));
	}

	protected List<MatchingColor> getMatchingColors() {
		return matchingColors;
	}

}
