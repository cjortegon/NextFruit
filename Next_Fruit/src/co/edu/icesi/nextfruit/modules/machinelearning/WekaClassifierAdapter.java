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

}
