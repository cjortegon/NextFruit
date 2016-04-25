package co.edu.icesi.nextfruit.modules.machinelearning;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import co.edu.icesi.nextfruit.modules.computervision.FeaturesExtract;
import co.edu.icesi.nextfruit.modules.model.CameraCalibration;
import weka.classifiers.Evaluation;
import weka.core.Attribute;

public class ClassClassifier extends WekaClassifierAdapter{

	public ClassClassifier(CameraCalibration calibration) {
		super("strawberry-class", calibration);
	}

	@Override
	public void insertInstanceFromFeatures(FeaturesExtract extracted, String className) {

		//
		//	implementar features para la forma.
		//
		
	}

	@Override
	protected ArrayList<Attribute> defineFeaturesVector() {
		// Create and Initialize Attributes
		ArrayList<String> classClassValues = new ArrayList<String>();
		classClassValues.add("e");
		classClassValues.add("i");
		classClassValues.add("ii");

		// Defined attributes
		Attribute area = new Attribute("area");
		Attribute entropy = new Attribute("entropy");
		Attribute mean = new Attribute("mean");
		Attribute sD = new Attribute("standard-deviation");
		Attribute skewness = new Attribute("skewness");
		Attribute kurtosis = new Attribute("kurtosis");
		Attribute classClass = new Attribute("size", classClassValues);

		// Declare the feature vector
		ArrayList<Attribute> features = new ArrayList<Attribute>();
		features.add(area);
		features.add(entropy);
		features.add(mean);
		features.add(sD);
		features.add(skewness);
		features.add(kurtosis);

		// Class type
		features.add(classClass);

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

					separator + "Class-E" + "   " + "Class-I" + "   " + "Class-II" + "   -> Classified as" + separator +
					"(" + cmMatrix[0][0] + ")" + " (" + cmMatrix[0][1] + ")" + " (" + cmMatrix[0][2] + ")" + "   | Class-E  (Real Value)" + separator +
					"(" + cmMatrix[1][0] + ")" + " (" + cmMatrix[1][1] + ")" + " (" + cmMatrix[1][2] + ")" + "   | Class-I  (Real Value)" + separator +
					"(" + cmMatrix[2][0] + ")" + " (" + cmMatrix[2][1] + ")" + " (" + cmMatrix[2][2] + ")" + "   | Class-II (Real Value)");
			
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
