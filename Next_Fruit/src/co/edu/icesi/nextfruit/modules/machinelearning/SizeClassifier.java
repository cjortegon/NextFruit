package co.edu.icesi.nextfruit.modules.machinelearning;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import co.edu.icesi.nextfruit.modules.computervision.FeaturesExtract;
import co.edu.icesi.nextfruit.modules.model.CameraCalibration;
import weka.classifiers.Evaluation;
import weka.core.Attribute;

public class SizeClassifier extends WekaClassifierAdapter{

	public SizeClassifier(CameraCalibration calibration) {
		super("strawberry-size", calibration);
	}

	@Override
	public void insertInstanceFromFeatures(FeaturesExtract extracted, String className) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected ArrayList<Attribute> defineFeaturesVector() {

		// Create and Initialize Attributes
		ArrayList<String> qualityClassValues = new ArrayList<String>();
		qualityClassValues.add("a");
		qualityClassValues.add("b");
		qualityClassValues.add("c");
		qualityClassValues.add("d");
		qualityClassValues.add("e");

		// Defined attributes
		Attribute area = new Attribute("area");
		Attribute entropy = new Attribute("entropy");
		Attribute mean = new Attribute("mean");
		Attribute sD = new Attribute("standard-deviation");
		Attribute skewness = new Attribute("skewness");
		Attribute kurtosis = new Attribute("kurtosis");
		Attribute qualityClass = new Attribute("size", qualityClassValues);

		// Declare the feature vector
		ArrayList<Attribute> features = new ArrayList<Attribute>();
		features.add(area);
		features.add(entropy);
		features.add(mean);
		features.add(sD);
		features.add(skewness);
		features.add(kurtosis);

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

					separator + "Size-A" + "   " + "Size-B" + "   " + "Size-C" + "   " + "Size-D" + "   " + "Size-E" + "   -> Classified as" + separator +
					"(" + cmMatrix[0][0] + ")" + " (" + cmMatrix[0][1] + ")" + " (" + cmMatrix[0][2] + ")" + " (" + cmMatrix[0][3] + ")" + " (" + cmMatrix[0][4] + ")" + "   | Size-A (Real Value)" + separator +
					"(" + cmMatrix[1][0] + ")" + " (" + cmMatrix[1][1] + ")" + " (" + cmMatrix[1][2] + ")" + " (" + cmMatrix[1][3] + ")" + " (" + cmMatrix[1][4] + ")" + "   | Size-B (Real Value)" + separator +
					"(" + cmMatrix[2][0] + ")" + " (" + cmMatrix[2][1] + ")" + " (" + cmMatrix[2][2] + ")" + " (" + cmMatrix[2][3] + ")" + " (" + cmMatrix[2][4] + ")" + "   | Size-C (Real Value)" + separator +
					"(" + cmMatrix[3][0] + ")" + " (" + cmMatrix[3][1] + ")" + " (" + cmMatrix[3][2] + ")" + " (" + cmMatrix[3][3] + ")" + " (" + cmMatrix[3][4] + ")" + "   | Size-D (Real Value)" + separator +
					"(" + cmMatrix[4][0] + ")" + " (" + cmMatrix[4][1] + ")" + " (" + cmMatrix[4][2] + ")" + " (" + cmMatrix[4][3] + ")" + " (" + cmMatrix[4][4] + ")" + "   | Size-E (Real Value)");

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
