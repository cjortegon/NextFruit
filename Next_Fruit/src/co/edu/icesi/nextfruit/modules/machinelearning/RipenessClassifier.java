package co.edu.icesi.nextfruit.modules.machinelearning;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import co.edu.icesi.nextfruit.modules.computervision.FeaturesExtract;
import co.edu.icesi.nextfruit.modules.model.CameraCalibration;
import weka.classifiers.Evaluation;
import weka.core.Attribute;

public class RipenessClassifier extends WekaClassifierAdapter{

	private final int LUMINANCE_RANGE = 6;
	
	
	public RipenessClassifier(CameraCalibration calibration) {
		super("strawberry-ripeness", calibration);
	}

	@Override
	public void insertInstanceFromFeatures(FeaturesExtract extracted, String className) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected ArrayList<Attribute> defineFeaturesVector() {
		
		// Loading matching colors
		File file = new File("resources/matching_colors.txt");
		try {
			this.loadMatchingColors(file, calibration.getInverseWorkingSpaceMatrix());
		} catch (IOException e) {
			System.out.println("Could not load: " + file.getAbsolutePath());
			e.printStackTrace();
		}

		// Create and Initialize Attributes
		ArrayList<String> qualityClassValues = new ArrayList<String>();
		qualityClassValues.add("0");
		qualityClassValues.add("1");
		qualityClassValues.add("2");
		qualityClassValues.add("3");
		qualityClassValues.add("4");
		qualityClassValues.add("5");
		qualityClassValues.add("6");

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
			Attribute luminance = new Attribute("luminance" + i);
			features.add(luminance);
		}

		// Colors ranges
		for (int i = 0; i < getMatchingColors().size(); i++) {
			Attribute color = new Attribute("color" + i);
			features.add(color);
		}

		// Class type
		features.add(qualityClass);

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
