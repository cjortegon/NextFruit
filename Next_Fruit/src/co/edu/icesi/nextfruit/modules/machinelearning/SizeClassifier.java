package co.edu.icesi.nextfruit.modules.machinelearning;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import co.edu.icesi.nextfruit.modules.computervision.FeaturesExtract;
import co.edu.icesi.nextfruit.modules.model.CameraCalibration;
import co.edu.icesi.nextfruit.modules.model.PolygonWrapper;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class SizeClassifier extends WekaClassifierAdapter{

	public SizeClassifier(CameraCalibration calibration) {
		super("strawberry-size", calibration);
	}

	public SizeClassifier(CameraCalibration calibration, File classifier) throws Exception {
		super(classifier, calibration);
	}

	@Override
	public void insertInstanceFromFeatures(FeaturesExtract extracted, String className) {
		// Analyzing data
		// Not needed, cause this classifier doesn't use colors or luminant values
		//extracted.analizeData(calibration, getMatchingColors());

		// Getting results
		PolygonWrapper polygon = extracted.getPolygon();

		// Creating instance
		int definedAttributes = 2; // Make sure this value is correct
		Instance instance = new DenseInstance(definedAttributes+1);
		ArrayList<Attribute> features = getFeatures();

		// Adding defined attributes
		instance.setValue(features.get(0), polygon.getArea());
		instance.setValue(features.get(1), polygon.getPerimeter());

		// Adding class name
		instance.setValue(features.get(definedAttributes), className);
		this.trainingSet.add(instance);		
	}


	@Override
	protected ArrayList<Attribute> defineFeaturesVector() {

		// Create and Initialize Attributes
		ArrayList<String> sizeClassValues = new ArrayList<String>();
		sizeClassValues.add("big");
		sizeClassValues.add("medium");
		sizeClassValues.add("small");

		// Defined attributes
		Attribute area = new Attribute("area");
		Attribute perimeter = new Attribute("perimeter");
		Attribute sizeClass = new Attribute("size", sizeClassValues);

		// Declare the feature vector
		ArrayList<Attribute> features = new ArrayList<Attribute>();
		features.add(area);
		features.add(perimeter);

		// Class type
		features.add(sizeClass);

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

	public Instance getInstanceFromFeatures(FeaturesExtract extracted) {

		// Getting results
		PolygonWrapper polygon = extracted.getPolygon();

		// Creating instance
		int definedAttributes = 2; // Make sure this value is correct
		Instance instance = new DenseInstance(definedAttributes+1);
		ArrayList<Attribute> features = getFeatures();

		// Adding defined attributes
		instance.setValue(features.get(0), polygon.getArea());
		instance.setValue(features.get(1), polygon.getPerimeter());

		return instance;
	}

}
