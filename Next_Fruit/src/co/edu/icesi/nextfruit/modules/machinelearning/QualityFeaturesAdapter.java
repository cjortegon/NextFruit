package co.edu.icesi.nextfruit.modules.machinelearning;

import java.util.ArrayList;
import java.util.Collection;

import co.edu.icesi.nextfruit.modules.computervision.FeaturesExtract;
import co.edu.icesi.nextfruit.modules.model.CameraCalibration;
import co.edu.icesi.nextfruit.modules.model.ColorDistribution;
import co.edu.icesi.nextfruit.modules.model.PolygonWrapper;
import co.edu.icesi.nextfruit.util.Statistics;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;

public class QualityFeaturesAdapter extends WekaClassifierAdapter {

	private final String[] matchingColors = new String[] {
			"0.40;0.47;0.05+0.39;0.41;0.03+0.46;0.42;0.03", // Yellow
			"0.39;0.35;0.03", // Brown
			"0.58;0.34;0.1", // Red
			"0.34;0.43;0.02+0.32;0.40;0.02" // Leaves
	};

	private CameraCalibration calibration;

	public QualityFeaturesAdapter(CameraCalibration calibration, int numberOfInstances) {
		super("strawberry-qualities", numberOfInstances);
		this.calibration = calibration;

		// Loading matching colors
		this.loadMatchingColors(matchingColors, calibration.getInverseWorkingSpaceMatrix());
	}

	@Override
	public void insertInstanceFromFeatures(FeaturesExtract extracted, String className) {

		// Analyzing data
		extracted.analizeData(calibration, getMatchingColors());

		// Getting results
		Collection<ColorDistribution> matchs = extracted.getMatchingColors();
		Statistics luminantStatistics = extracted.getLuminanceStatistics();
		PolygonWrapper polygon = extracted.getPolygon();
		int index = 0;
		double colors[] = new double[3];
		for (ColorDistribution color : matchs)
			colors[index++] = color.getRepeat()/(double)extracted.getNumberOfPixels();

		// Creating instance
		Instance instance = new DenseInstance(9);
		ArrayList<Attribute> features = getFeatures();

		instance.setValue(features.get(0), polygon.getArea());
		instance.setValue(features.get(1), luminantStatistics.getMean());
		instance.setValue(features.get(2), luminantStatistics.getStandardDeviation());
		instance.setValue(features.get(3), luminantStatistics.getSkewness());
		instance.setValue(features.get(4), luminantStatistics.getKurtosis());
		instance.setValue(features.get(5), colors[0]);
		instance.setValue(features.get(6), colors[1]);
		instance.setValue(features.get(7), colors[2]);
		instance.setValue(features.get(8), className);
		this.trainingSet.add(instance);

	}

	/**
	 * Initialize the features vector.
	 */
	@Override
	protected ArrayList<Attribute> defineFeaturesVector() {

		// Create and Initialize Attributes
		ArrayList<String> qualityClassValues = new ArrayList<String>(4);
		qualityClassValues.add("4r");
		qualityClassValues.add("5r");
		qualityClassValues.add("5v");
		qualityClassValues.add("er");
		qualityClassValues.add("fea");

		Attribute area = new Attribute("area");
		Attribute mean = new Attribute("mean");
		Attribute sD = new Attribute("standard-deviation");
		Attribute skewness = new Attribute("skewness");
		Attribute kurtosis = new Attribute("kurtosis");
		Attribute yellow = new Attribute("yellow-percentage");
		Attribute brown = new Attribute("brown-percentage");
		Attribute red = new Attribute("red-percentage");
		Attribute green = new Attribute("green-percentage");
		Attribute qualityClass = new Attribute("quality", qualityClassValues);

		// Declare the feature vector
		ArrayList<Attribute> features = new ArrayList<Attribute>(9);
		features.add(area);
		features.add(mean);
		features.add(sD);
		features.add(skewness);
		features.add(kurtosis);
		features.add(yellow);
		features.add(brown);
		features.add(red);
		features.add(green);
		features.add(qualityClass);

		return features;
	}

}
