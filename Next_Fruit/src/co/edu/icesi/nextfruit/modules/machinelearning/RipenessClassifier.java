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
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;

public class RipenessClassifier extends WekaClassifierAdapter{

	public RipenessClassifier(CameraCalibration calibration) {
		super("strawberry-ripeness", calibration);
	}

	@Override
	public void insertInstanceFromFeatures(FeaturesExtract extracted, String className) {
		// Analyzing data
		extracted.analizeData(calibration, getMatchingColors());

		// Getting results
		Collection<ColorDistribution> matchs = extracted.getMatchingColors();
		int index = 0;
		double colors[] = new double[matchs.size()];
		for (ColorDistribution color : matchs)
			colors[index++] = color.getRepeat()/(double)extracted.getNumberOfPixels();

		// Creating instance
		Instance instance = new DenseInstance(1+colors.length);
		ArrayList<Attribute> features = getFeatures();
		int definedAttributes = 0; // Make sure this value is correct

		// Adding colors
		for (int i = 0; i < colors.length; i++)
			instance.setValue(features.get(definedAttributes+i), colors[i]);
		definedAttributes += colors.length;

		// Adding class name
		instance.setValue(features.get(definedAttributes), className);
		this.trainingSet.add(instance);
	}

	@Override
	protected ArrayList<Attribute> defineFeaturesVector() {

		// Loading matching colors
		File file = new File("resources/custom_colors.txt");
		try {
			this.loadMatchingColors(file, calibration.getInverseWorkingSpaceMatrix());
		} catch (IOException e) {
			System.out.println("Could not load: " + file.getAbsolutePath());
			e.printStackTrace();
		}

		// Create and Initialize Attributes
		ArrayList<String> ripenessClassValues = new ArrayList<String>();
		ripenessClassValues.add("0");
		ripenessClassValues.add("1");
		ripenessClassValues.add("2");
		ripenessClassValues.add("3");
		ripenessClassValues.add("4");
		ripenessClassValues.add("5");
		ripenessClassValues.add("6");

		// Defined attributes
		Attribute ripenessClass = new Attribute("quality", ripenessClassValues);

		// Declare the feature vector
		ArrayList<Attribute> features = new ArrayList<Attribute>();

		// Colors ranges
		for (int i = 0; i < getMatchingColors().size(); i++) {
			Attribute color = new Attribute("color" + i);
			features.add(color);
		}

		// Class type
		features.add(ripenessClass);

		System.out.println("Number of features for " + getClass().getName() + ": " + features.size());
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

					separator + "R-0" + "   " + "R-1" + "   " + "R-2" + "   " + "R-3" + "   " + "R-4" + "R-5" + "   " + "R-6" + "   -> Classified as" + separator +
					"(" + cmMatrix[0][0] + ")" + " (" + cmMatrix[0][1] + ")" + " (" + cmMatrix[0][2] + ")" + " (" + cmMatrix[0][3] + ")" + " (" + cmMatrix[0][4] + ")" + " (" + cmMatrix[0][5] + ")" + " (" + cmMatrix[0][6] + ")" + "   | R-0 (Real Value)" + separator +
					"(" + cmMatrix[1][0] + ")" + " (" + cmMatrix[1][1] + ")" + " (" + cmMatrix[1][2] + ")" + " (" + cmMatrix[1][3] + ")" + " (" + cmMatrix[1][4] + ")" + " (" + cmMatrix[1][5] + ")" + " (" + cmMatrix[1][6] + ")" + "   | R-1 (Real Value)" + separator +
					"(" + cmMatrix[2][0] + ")" + " (" + cmMatrix[2][1] + ")" + " (" + cmMatrix[2][2] + ")" + " (" + cmMatrix[2][3] + ")" + " (" + cmMatrix[2][4] + ")" + " (" + cmMatrix[2][5] + ")" + " (" + cmMatrix[2][6] + ")" + "   | R-2 (Real Value)" + separator +
					"(" + cmMatrix[3][0] + ")" + " (" + cmMatrix[3][1] + ")" + " (" + cmMatrix[3][2] + ")" + " (" + cmMatrix[3][3] + ")" + " (" + cmMatrix[3][4] + ")" + " (" + cmMatrix[3][5] + ")" + " (" + cmMatrix[3][6] + ")" + "   | R-3 (Real Value)" + separator +
					"(" + cmMatrix[4][0] + ")" + " (" + cmMatrix[4][1] + ")" + " (" + cmMatrix[4][2] + ")" + " (" + cmMatrix[4][3] + ")" + " (" + cmMatrix[4][4] + ")" + " (" + cmMatrix[4][5] + ")" + " (" + cmMatrix[4][6] + ")" + "   | R-4 (Real Value)" + separator +
					"(" + cmMatrix[5][0] + ")" + " (" + cmMatrix[5][1] + ")" + " (" + cmMatrix[5][2] + ")" + " (" + cmMatrix[5][3] + ")" + " (" + cmMatrix[5][4] + ")" + " (" + cmMatrix[5][5] + ")" + " (" + cmMatrix[5][6] + ")" + "   | R-5 (Real Value)" + separator +
					"(" + cmMatrix[6][0] + ")" + " (" + cmMatrix[6][1] + ")" + " (" + cmMatrix[6][2] + ")" + " (" + cmMatrix[6][3] + ")" + " (" + cmMatrix[6][4] + ")" + " (" + cmMatrix[6][5] + ")" + " (" + cmMatrix[6][6] + ")" + "   | R-6 (Real Value)");

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
