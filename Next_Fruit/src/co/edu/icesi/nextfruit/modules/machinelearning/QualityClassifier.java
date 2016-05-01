package co.edu.icesi.nextfruit.modules.machinelearning;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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

	private final int LUMINANCE_RANGE = 6;

	public QualityClassifier(CameraCalibration calibration) {
		super("strawberry-quality", calibration);
		//this.calibration = calibration;
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
		double colors[] = new double[matchs.size()];
		for (ColorDistribution color : matchs)
			colors[index++] = color.getRepeat()/(double)extracted.getNumberOfPixels();

		// Creating instance
		Instance instance = new DenseInstance(7+LUMINANCE_RANGE+colors.length);
		ArrayList<Attribute> features = getFeatures();

		// Adding defined attributes
		//		System.out.println("Area="+polygon.getArea()
		//		+" Entropy="+extracted.getEntropy()
		//		+" Mean="+luminantStatistics.getMean()
		//		+" SD="+luminantStatistics.getStandardDeviation()
		//		+" Skewness="+luminantStatistics.getSkewness()
		//		+" Kurtosis="+luminantStatistics.getKurtosis());
		instance.setValue(features.get(0), polygon.getArea());
		instance.setValue(features.get(1), extracted.getEntropy());
		instance.setValue(features.get(2), luminantStatistics.getMean());
		instance.setValue(features.get(3), luminantStatistics.getStandardDeviation());
		instance.setValue(features.get(4), luminantStatistics.getSkewness());
		instance.setValue(features.get(5), luminantStatistics.getKurtosis());
		int definedAttributes = 6; // Make sure this value is correct

		// Adding luminance ranges
		double[] ranges = extracted.getHistogram().getRanges();
		for (int i = 0; i < LUMINANCE_RANGE; i++) {
			instance.setValue(features.get(definedAttributes+i), ranges[i]);
		}
		definedAttributes += LUMINANCE_RANGE;

		// Adding colors
		for (int i = 0; i < colors.length; i++)
			instance.setValue(features.get(definedAttributes+i), colors[i]);
		definedAttributes += colors.length;

		// Adding class name
		instance.setValue(features.get(definedAttributes), className);
		this.trainingSet.add(instance);
	}

	/**
	 * Initialize the features vector.
	 */
	@Override
	protected ArrayList<Attribute> defineFeaturesVector() {

		// Loading matching colors
		File file = new File("resources/matching_colors.txt");
		try {
			this.loadMatchingColors(file, calibration.getInverseWorkingSpaceMatrix());
		} catch (IOException e) {
			System.out.println("Could not load: "+file.getAbsolutePath());
			e.printStackTrace();
		}

		// Create and Initialize Attributes
		ArrayList<String> qualityClassValues = new ArrayList<String>();
		qualityClassValues.add("t");
		qualityClassValues.add("f");

		// Defined attributes
		Attribute area = new Attribute("area");
		Attribute entropy = new Attribute("entropy");
		Attribute mean = new Attribute("mean");
		Attribute sD = new Attribute("standard-deviation");
		Attribute skewness = new Attribute("skewness");
		Attribute kurtosis = new Attribute("kurtosis");
		Attribute qualityClass = new Attribute("quality", qualityClassValues);

		// Declare the feature vector
		ArrayList<Attribute> features = new ArrayList<Attribute>();
		features.add(area);
		features.add(entropy);
		features.add(mean);
		features.add(sD);
		features.add(skewness);
		features.add(kurtosis);

		// Luminance ranges
		for (int i = 0; i < LUMINANCE_RANGE; i++) {
			Attribute luminance = new Attribute("luminance"+i);
			features.add(luminance);
		}

		// Colors ranges
		for (int i = 0; i < getMatchingColors().size(); i++) {
			Attribute color = new Attribute("color"+i);
			features.add(color);
		}

		// Class type
		features.add(qualityClass);

		System.out.println("Number of features for "+getClass().getName()+": "+features.size());
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

					separator + "Approved" + "   " + "Rejected"+ "   -> Classified as" + separator +
					"(" + cmMatrix[0][0] + ")" + " (" + cmMatrix[0][1] + ")" + "   | Approved (Real Value)" + separator +
					"(" + cmMatrix[1][0] + ")" + " (" + cmMatrix[1][1] + ")" + "   | Rejected (Real Value)");

			PrintWriter writer = new PrintWriter(file);
			writer.println(s);
			writer.close();
			writeLog("[saveEvaluationData]: Los resultados de la evaluacion se han guardado en " +
					"el archivo " + file.getName() + ".");

		} catch (FileNotFoundException e) {
			writeLog("[saveEvaluationData]: No se pudo guardar el resultado de la evaluacion.");
			e.printStackTrace();
		}		
	}

}
