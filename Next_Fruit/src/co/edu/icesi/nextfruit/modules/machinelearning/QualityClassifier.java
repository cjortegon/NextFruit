package co.edu.icesi.nextfruit.modules.machinelearning;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;

import co.edu.icesi.nextfruit.modules.computervision.FeaturesExtract;
import co.edu.icesi.nextfruit.modules.model.CameraCalibration;
import co.edu.icesi.nextfruit.modules.model.ColorDistribution;
import co.edu.icesi.nextfruit.modules.model.PolygonWrapper;
import co.edu.icesi.nextfruit.util.Statistics;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;

public class QualityClassifier extends WekaClassifierAdapter {

	private final String[] matchingColors = new String[] {
			"0.40;0.47;0.05+0.39;0.41;0.03+0.46;0.42;0.03", // Yellow
			"0.39;0.35;0.03", // Brown
			"0.58;0.34;0.1", // Red
			"0.34;0.43;0.02+0.32;0.40;0.02" // Leaves
	};

	private CameraCalibration calibration;

	public QualityClassifier(CameraCalibration calibration, int numberOfInstances) {
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
		qualityClassValues.add("t");
		qualityClassValues.add("f");

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

	@Override
	protected void saveEvaluationData(Evaluation ev, File file) {
		try {
			String separator = System.getProperty("line.separator");
			double[][] cmMatrix = ev.confusionMatrix();

			String s = ("**************" + separator + separator +
					" Test Results" + separator + separator +
					"**************" + separator + separator +
					separator + "-- SUMMARY --" + separator +
					ev.toSummaryString() + separator +
					separator + "-- CONFUSION MATRIX --" + separator + 

					//********************************************************
					//	Modificar la matriz de confusion para que se ajuste a 
					//	nuestro problema.
					//********************************************************

					separator + "Approved" + "   " + "" + "Rejected"+ "   -> Classified as" + separator +
					"(" + cmMatrix[0][0] + ")" + " (" + cmMatrix[0][1] + ")" + "   | Approved (Real Value)" + separator +
					"(" + cmMatrix[1][0] + ")" + " (" + cmMatrix[1][1] + ")" + "   | Rejected (Rela Value)");

			PrintWriter writer = new PrintWriter(file);
			writer.println(s);
			writer.close();
			writeLog("[saveEvaluationData]: Los resultados de la evaluacion se han guardado en " +
					"el archivo " + file.getName() + ".");

		} catch (FileNotFoundException e) {
			writeLog("[saveEvaluationData]: No se pudo guardar el resultado de la evaluacion ");
			e.printStackTrace();
		}		
	}

}
